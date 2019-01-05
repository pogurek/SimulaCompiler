/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.statement;

import java.util.Enumeration;
import java.util.Vector;

import simula.compiler.JavaModule;
import simula.compiler.declaration.ClassDeclaration;
import simula.compiler.declaration.ConnectionBlock;
import simula.compiler.declaration.DeclarationScope;
import simula.compiler.declaration.TypeDeclaration;
import simula.compiler.expression.AssignmentOperation;
import simula.compiler.expression.Expression;
import simula.compiler.expression.Variable;
import simula.compiler.parsing.Parser;
import simula.compiler.utilities.Global;
import simula.compiler.utilities.KeyWord;
import simula.compiler.utilities.Option;
import simula.compiler.utilities.Type;
import simula.compiler.utilities.Util;

/**
 * Connection Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * connection-statement = INSPECT object-expression  connection-part [ OTHERWISE statement ]
 *		connection-part = DO statement | selective-part
 *			selective-part = { WHEN <class identifier> DO <statement> }
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public final class ConnectionStatement extends Statement
{ Expression objectExpression;
  Variable inspectedVariable;
  TypeDeclaration inspectVariableDeclaration;
  Vector<DoPart> connectionPart=new Vector<DoPart>();
  Statement otherwise;
  boolean hasWhenPart;
  private static int SEQU=0;

  public ConnectionStatement()
  { if(Option.TRACE_PARSE) Parser.TRACE("Parse ConnectionStatement");
//  Expression objectExpression=Expression.parseExpression();
    objectExpression=Expression.parseExpression();
    String ident="inspect$"+lineNumber+'$'+(SEQU++);
    inspectedVariable=new Variable(ident);
	inspectVariableDeclaration=new TypeDeclaration(Type.Ref("RTObject"),ident);
	
	//Util.BREAK("NEW ConnectionStatement: new InspectVariableDeclaration="+inspectVariableDeclaration);
	DeclarationScope scope=Global.currentScope;
	while(scope.blockKind==null || scope instanceof ConnectionBlock) scope=scope.declaredIn;
	//Util.BREAK("NEW ConnectionStatement: ADD IT TO "+Global.currentScope);
	scope.declarationList.add(inspectVariableDeclaration);
    
    if(Parser.accept(KeyWord.DO))
    { ConnectionBlock connectionBlock=new ConnectionBlock(inspectedVariable);
      Statement statement=Statement.doParse();
      connectionPart.add(new DoPart(connectionBlock,statement));
      connectionBlock.end();
    }
    else
    { while(Parser.accept(KeyWord.WHEN))
      { String classIdentifier=expectIdentifier();
        Parser.expect(KeyWord.DO);
        ConnectionBlock connectionBlock=new ConnectionBlock(inspectedVariable);
        hasWhenPart=true;
        Statement statement=Statement.doParse();
        connectionPart.add(new WhenPart(classIdentifier,connectionBlock,statement));
        connectionBlock.end();
      }
    	
    }
    otherwise=null;
    if(Parser.accept(KeyWord.OTHERWISE)) otherwise=Statement.doParse();
    if(Option.TRACE_PARSE) Util.TRACE("END NEW ConnectionStatement: "+toString());
  }


  class DoPart
  { String refIdentifier;
    ConnectionBlock connectionBlock;
	public DoPart(ConnectionBlock connectionBlock,Statement statement)
	{ this.connectionBlock=connectionBlock; //this.statement=statement;
	  connectionBlock.setStatement(statement);
	  if(Option.TRACE_PARSE) Util.TRACE("NEW DoPart: " + toString());
	}

	public void doChecking()
	{ Type type=inspectVariableDeclaration.type;
  	  refIdentifier=type.getRefIdent();
      //Util.BREAK("ConnectionStatement.DoPart.doChecking: refIdentifier="+refIdentifier);
	  if(refIdentifier==null) Util.error("The Variable "+inspectedVariable+" is not ref() type");
	  connectionBlock.setClassDeclaration(AssignmentOperation.getQualification(refIdentifier));	
	  connectionBlock.doChecking();
	  SET_SEMANTICS_CHECKED();
	}
	
	public void doCoding(boolean first)
	{ ASSERT_SEMANTICS_CHECKED(this);
	  connectionBlock.doJavaCoding();
	}
	
	public void print(String indent)
	{ System.out.println(indent+"DO ");
	  connectionBlock.print(indent);
	}

	public String toString() { return(connectionBlock.toString()); }
  }

  class WhenPart extends DoPart
  { String classIdentifier;
    ClassDeclaration classDeclaration; // Set during checking.
    boolean impossibleWhenPart; // Set by doChecking
	public WhenPart(String classIdentifier,ConnectionBlock connectionBlock,Statement statement)
	{ super(connectionBlock,statement);
	  this.classIdentifier=classIdentifier;
	  if(Option.TRACE_PARSE) Util.TRACE("NEW DoPart: " + toString());
	}

	public void doChecking()
	{ if(classIdentifier==null) // && objectExpression!=null)
	  { Type type=inspectVariableDeclaration.type;
	    classIdentifier=type.getRefIdent();
	    if(classIdentifier==null) Util.error("The Variable "+inspectedVariable+" is not ref() type");
	  }
      //Util.BREAK("ConnectionStatement.WhenPart.doChecking: ");
	  if(classIdentifier!=null)
	  {	classDeclaration=AssignmentOperation.getQualification(classIdentifier);
		connectionBlock.setClassDeclaration(classDeclaration);
	  }
	  if(!AssignmentOperation.checkCompatability(objectExpression,classIdentifier))
	  { Util.warning("Impossible When Part: "+objectExpression+" is not compatible with "+classIdentifier);
	    impossibleWhenPart=true;
	  }
	  connectionBlock.doChecking();
	}
	
	public void doCoding(boolean first)
	{ //Util.BREAK("ConnectionStatement.WhenPart.doCoding: statement="+statement.getClass().getName());
	  ASSERT_SEMANTICS_CHECKED(this);
	  String prfx=(first)?"":"else ";
	  String cid=classDeclaration.getJavaIdentifier();
	  if(!impossibleWhenPart)
	  { JavaModule.code(prfx+"if("+inspectedVariable.toJavaCode()+" instanceof "+cid+") // WHEN "+cid+" DO ");
	    connectionBlock.doJavaCoding();
	  } else JavaModule.code(prfx+"// WHEN "+cid+" DO -- IMPOSSIBLE REMOVED");
	}
	
	public void print(String indent)
	{ System.out.println(indent+"WHEN "+classIdentifier+" DO ");
	  connectionBlock.print(indent);
	}
	
	public String toString()
	{ return("WHEN "+classIdentifier+" DO ..."); }//+statement);	}
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
    Global.sourceLineNumber=lineNumber;
    if(Option.TRACE_CHECKER) Util.TRACE("BEGIN ConnectionStatement("+toString()+").doChecking - Current Scope Chain: "+Global.currentScope.edScopeChain());
    objectExpression.doChecking();
    Type exprType=objectExpression.type;
    //Util.BREAK("InspectVariable.doChecking("+this+") exprType="+exprType);
    exprType.doChecking(Global.currentScope);
    inspectVariableDeclaration.type=exprType;
    inspectedVariable.type=exprType;
    //Util.BREAK("ConnectionStatement.doChecking(2): inspectVariableDeclaration="+inspectVariableDeclaration);
    inspectedVariable.doChecking();
    //Util.BREAK("ConnectionStatement.doChecking(3): inspectedVariable="+inspectedVariable);
    for(Enumeration<DoPart> e=connectionPart.elements(); e.hasMoreElements();)
    { e.nextElement().doChecking(); }
    if(otherwise!=null) otherwise.doChecking();
	SET_SEMANTICS_CHECKED();
  }
  
  public void doJavaCoding()
  {	Global.sourceLineNumber=lineNumber;
	ASSERT_SEMANTICS_CHECKED(this);
    JavaModule.code("{");
    JavaModule.code("// BEGIN INSPECTION ");
    Expression assignment=new AssignmentOperation(inspectedVariable,KeyWord.ASSIGNREF,objectExpression);
    assignment.doChecking(); JavaModule.code(assignment.toJavaCode()+';');
    if(hasWhenPart) JavaModule.code("//"+"INSPECT "+inspectedVariable);
    else JavaModule.code("if("+inspectedVariable.toJavaCode()+"!=null) //"+"INSPECT "+inspectedVariable);
    boolean first=true;
    for(Enumeration<DoPart> e=connectionPart.elements(); e.hasMoreElements();)
    { e.nextElement().doCoding(first); first=false; }
    if(otherwise!=null)
    { JavaModule.code("else // OTHERWISE ");
      otherwise.doJavaCoding();
      //JavaModule.code("// END OTHERWISE ");
    }
    //JavaModule.code("// END INSPECTION ");
    JavaModule.code("}");
  }
  
  // ***********************************************************************************************
  // *** Printing Utility: print
  // ***********************************************************************************************
  public void print(String indent) { 
//	  if(assignment!=null) assignment.print(indent);
	  System.out.println(indent+"INSPECT "+inspectedVariable);
	  for(DoPart doPart:connectionPart) doPart.print(indent);
	  if(otherwise!=null)
	  { System.out.println(indent+"   OTHERWISE "+otherwise+';'); }
  }

  public String toString()
  { String otherwisePart="";
    if(otherwise!=null) otherwisePart=" OTHERWISE "+otherwise;
	return("INSPECT "+inspectedVariable+" "+connectionPart+otherwisePart);
  }
  
}
