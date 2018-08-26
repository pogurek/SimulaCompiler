/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.expression;

import java.util.Iterator;
import java.util.Vector;

import simula.compiler.declaration.ArrayDeclaration;
import simula.compiler.declaration.BlockDeclaration;
import simula.compiler.declaration.ConnectionBlock;
import simula.compiler.declaration.Declaration;
import simula.compiler.declaration.LabelDeclaration;
import simula.compiler.declaration.Parameter;
import simula.compiler.declaration.StandardProcedure;
import simula.compiler.declaration.TypeDeclaration;
import simula.compiler.declaration.Virtual;
import simula.compiler.parsing.Parser;
import simula.compiler.utilities.Global;
import simula.compiler.utilities.KeyWord;
import simula.compiler.utilities.Meaning;
import simula.compiler.utilities.Option;
import simula.compiler.utilities.OverLoad;
import simula.compiler.utilities.Type;
import simula.compiler.utilities.Util;

/**
 * Variable.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * Variable = SimpleObjectExpression . Variable | SimpleVariable | SubscriptedVariable
 * 
 * 	SimpleObjectExpression = NONE | Variable | FunctionDesignator | ObjectGenerator
 *                             | LocalObject | QualifiedObject | ( ObjectExpression)
 *                             
 * 	SimpleVariable = Identifier
 * 
 *	SubscriptedVariable = FunctionDesignator | ArrayElement
 * 
 *  FunctionDesignator = ProcedureIdentifier ( [ ActualParameterPart ] )
 *  
 * 	ActualParameterPart = ActualParameter { , ActualParameter }
 * 		ActualParameter = Expression | ArrayIdentifier1 
 *                      | SwitchIdentifier1 | ProcedureIdentifier1
 * 			Identifier1 = Identifier | RemoteIdentifier
 * 				RemoteIdentifier = SimpleObjectExpression . AttributeIdentifier
 * 								 | TextPrimary . AttributeIdentifier
 *
 *	ArrayElement = ArrayIdentifier [ SubscriptList ]
 *		SubscriptList ::= ArithmeticExpression { , ArithmeticExpression }
 * 
 * </pre>
 * <b>Function designators:</b>
 * <p>
 * A function designator defines a value which results through the application
 * of a given set of rules defined by a procedure declaration (see 5.4) to a
 * fixed set of actual parameters. The rules governing specification of actual
 * parameters are given in 4.6.
 * 
 * Note: Not every procedure declaration defines rules for determining the value
 * of a function designator (cf. 5.4.1).
 * <p>
 * <b>Array elements:</b>
 * <p>
 * Subscripted variables designate values which are components of multi-
 * dimensional arrays. Each arithmetic expression of the subscript list occupies
 * one subscript position of the subscripted variable and is called a subscript.
 * The complete list of subscripts is enclosed by the subscript parentheses ( ).
 * The array component referred to by a subscripted variable is specified by
 * the actual value of its subscripts.
 * <p>
 * Each subscript position acts like a variable of type integer and the
 * evaluation of the subscript is understood to be equivalent to an assignment
 * to this fictitious variable. The value of the subscripted variable is defined
 * only if the actual integer value of each subscript expression is within the
 * associated subscript bounds of the array. A subscript expression value
 * outside its associated bounds causes a run time error.
 * 
 * @author SIMULA Standards Group
 * @author Øystein Myhre Andersen
 */
public class Variable extends Expression {
	public String identifier;
	public Meaning meaning;
	public boolean remotelyAccessed;

	Vector<Expression> params;
	public Vector<Expression> checkedParams; // Set by doChecking

	public enum Kind {
		parameter,attribute,label,virtual,standardAttribute,connectedAttribute
	}
	
	public boolean hasArguments() {
		return(params!=null);
	}
	public String getJavaIdentifier()
	{ return(meaning.declaredAs.getJavaIdentifier());	}
	
	public void setRemotelyAccessed(Meaning meaning) {
		this.meaning = meaning;
		remotelyAccessed=true;
		this.doChecking();	
		SET_SEMANTICS_CHECKED(); // Checked as remote attribute
	}

	public Meaning getMeaning() {
	  //Util.BREAK("BEGIN Variable("+identifier+").getMeaning - meaning="+meaning);
	  if (meaning == null)
		  meaning = Global.currentScope.findMeaning(identifier);
	  Util.ASSERT(meaning.isNO_MEANING() || meaning.declaredIn!=null,"Invariant");
	  return (meaning);
    }

	public Variable(String identifier) {
		this.identifier = identifier;
		if (Option.TRACE_PARSE) Util.TRACE("NEW Variable: " + identifier);
		// Util.BREAK("NEW Variable: "+this);
	}

	public static Variable parse(String ident) {
		if (Option.TRACE_PARSE)
			Util.TRACE("Parse Variable, current=" + Parser.currentToken + ", prev="	+ Parser.prevToken);
		Variable variable = new Variable(ident);
		if (Parser.accept(KeyWord.BEGPAR)) {
			if (!Parser.accept(KeyWord.ENDPAR)) {
				variable.params = new Vector<Expression>();
				do { variable.params.add(parseExpression());
				} while (Parser.accept(KeyWord.COMMA));
				Parser.expect(KeyWord.ENDPAR);
			}
		}
		if (Option.TRACE_PARSE)	Util.TRACE("NEW Variable: " + variable);
		return(variable);
	}

	// Is redefined in Variable, RemoteVariable and TypeConversion
	public Variable getWriteableVariable() { return(this); }

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())	return;
		Global.sourceLineNumber=lineNumber;
		//Util.BREAK("BEGIN Variable("+identifier+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
		Declaration declaredAs=getMeaning().declaredAs;
		if(declaredAs!=null) this.type=declaredAs.type;
		//Util.BREAK("END Variable("+identifier+").doChecking: type="+type+", Declared as: "+meaning);
		if(meaning.declaredAs instanceof StandardProcedure)
		{ //Util.BREAK("Variable("+identifier+").doChecking: type="+type+", Declared as: "+meaning);
		  //Util.BREAK("Variable("+identifier+").doChecking: Declared as'Qual: "+meaning.declaredAs.getClass().getSimpleName());
		  //Util.BREAK("Variable("+identifier+").doChecking: Declared in'Qual: "+meaning.declaredIn.getClass().getSimpleName());
//		  if(meaning.declaredAs instanceof StandardProcedure)
		  if(identifier.equalsIgnoreCase("detach"))
		  { //Util.BREAK("Variable("+identifier+").doChecking: type="+type+", Declared as: "+meaning.declaredAs);
		    //Util.BREAK("Variable("+identifier+").doChecking: type="+type+", Declared in: "+meaning.declaredIn);
			meaning.declaredIn.detachUsed=true;
			//Util.BREAK("Variable("+identifier+").doChecking: (Class "+meaning.declaredIn.identifier+").detachUsed=true");
		  }
		}
		
		if(!this.hasArguments()) {
			SET_SEMANTICS_CHECKED();
			return;
		}
		
		
		checkedParams = new Vector<Expression>();
		Declaration decl=meaning.declaredAs;
		//Util.BREAK("BEGIN SubscriptedVariable("+identifier+").doChecking: meaning="+meaning);
		
		if(decl instanceof ArrayDeclaration) // Declared Array
		{ ArrayDeclaration array=(ArrayDeclaration)decl;
		  this.type=array.type;
		  // Check parameters
		  Iterator<ArrayDeclaration.BoundPair> formalIterator=array.getBoundPairList().iterator();
		  Iterator<Expression> actualIterator=params.iterator();
		  while(actualIterator.hasNext())
		  { if(!formalIterator.hasNext()) Util.error("Wrong number of indices to "+array);
		    ArrayDeclaration.BoundPair formalParameter=formalIterator.next();
//		    Type formalType=Type.Integer;
		    if(Option.TRACE_CHECKER) Util.TRACE("Formal Parameter: "+formalParameter);
		    Expression actualParameter=actualIterator.next();
		    actualParameter.doChecking();
			Expression checkedParameter=TypeConversion.testAndCreate(Type.Integer,actualParameter);
			checkedParameter.backLink=this;
			checkedParams.add(checkedParameter);
		  }
		  if(formalIterator.hasNext()) Util.error("Wrong number of indices to "+array);
		}
		
		else if(decl instanceof BlockDeclaration) // Declared Procedure or Prefix class or Prefixed Block
		{	BlockDeclaration block = (BlockDeclaration) decl;
			//Util.BREAK("SubscriptedVariable("+identifier+") blockHead="+blockHead);
			this.type=block.type;
			Type overloadedType=this.type;
			Iterator<Parameter> formalIterator = block.parameterIterator();
			if(params!=null) {
				// Check parameters
				Iterator<Expression> actualIterator = params.iterator();
				//Util.BREAK("SubscriptedVariable("+identifier+").doChecking: Params="+params);
				LOOP:while (actualIterator.hasNext()) {
					if (!formalIterator.hasNext()) {
						Util.error("Wrong number of parameters to " + block);
						break LOOP;
					}
					Parameter formalParameter = (Parameter)formalIterator.next();
					Type formalType = formalParameter.type;
					//Util.BREAK("Formal Parameter: " + formalParameter + ", Formal Type=" + formalType + ", Formal Kind=" + formalParameter.kind);
					Expression actualParameter = actualIterator.next();
					//Util.BREAK("Actual Parameter: " + actualParameter);
					actualParameter.doChecking();
					//Util.BREAK("Actual Parameter: " + actualParameter.type + " " + actualParameter + ", Actual Type=" + actualParameter.type);
					if(Global.OVERLOADING && formalType instanceof OverLoad)
					{ //Util.BREAK("SubscriptedVariable.doChecking(1): "+this);
						formalType=actualParameter.type; // TODO: AD'HOC for add/subepsilon
						overloadedType=formalType;
					}
					if(formalParameter.kind==Parameter.Kind.Array)
					{ //Util.BREAK("Formal Type=" + formalType + ", Actual Type=" + actualParameter.type);
						if(formalType!=null && formalType!=actualParameter.type && formalType.isArithmeticType())
						Util.error("Parameter Array "+actualParameter+" must be of Type "+formalType);
					}
					Expression checkedParameter=TypeConversion.testAndCreate(formalType,actualParameter);
					checkedParameter.backLink=this;
					checkedParams.add(checkedParameter);
					//Util.BREAK("SubscriptedVariable("+identifier+").doChecking().addCheckedParam: "+checkedParams);
				}
			}
			if (formalIterator.hasNext()) Util.error("Wrong number of parameters to " + block);
			if(Global.OVERLOADING && type instanceof OverLoad)
			{ //Util.BREAK("SubscriptedVariable.doChecking(2): "+this);
			  this.type=overloadedType;
			}
			
    	}
		
		else if (decl instanceof Parameter) // Parameter Simple, Array, Procedure, Label   TODO: , ... ???
		{ Parameter spec=(Parameter) decl;
    	  Parameter.Kind kind=spec.kind;
    	  //Util.BREAK("SubscriptedVariable.doChecking: type="+type);
    	  //Util.BREAK("SubscriptedVariable.doChecking: kind="+kind);
//    	  Util.ASSERT(kind==Parameter.Kind.Array || kind==Parameter.Kind.Procedure || kind==Parameter.Kind.Switch,"Invariant ?");
    	  Util.ASSERT(kind==Parameter.Kind.Array || kind==Parameter.Kind.Procedure,"Invariant ?");
    	  this.type=spec.type;
//    	  Util.warning("SubscriptedVariable("+identifier+") - Parameter Checking is postponed to Runtime");
    	  Iterator<Expression> actualIterator=params.iterator();
    	  while(actualIterator.hasNext())
    	  { Expression actualParameter=actualIterator.next();
    	    actualParameter.doChecking();
    	    if(kind==Parameter.Kind.Array && !actualParameter.type.isArithmeticType()) Util.error("Illegal index-type");
    	      
    		//checkedParams.add(actualParameter);
    	    if(kind==Parameter.Kind.Array)
  			{ Expression checkedParameter=TypeConversion.testAndCreate(Type.Integer,actualParameter);
  			  checkedParameter.backLink=this;
  			  checkedParams.add(checkedParameter);
  			} else checkedParams.add(actualParameter);
    	  }
    	} else if (decl instanceof Virtual) // Parameter Array, Procedure, ... ???
		{ Virtual spec=(Virtual) decl;
      	  { this.type=spec.type;
      	    //Util.warning("Virtual Procedure: "+identifier+" - Parameter Checking is postponed to Runtime");
      	    Iterator<Expression> actualIterator=params.iterator();
      	    while(actualIterator.hasNext())
      	    { Expression actualParameter=actualIterator.next();
      	      actualParameter.doChecking();
      		  checkedParams.add(actualParameter);
      	    }
      	  }
		}
//	    else if(!meaning.isNO_MEANING()) Util.FATAL_ERROR("Umulig å komme hit ??");
		if (Option.TRACE_CHECKER)
			Util.TRACE("END SubscriptedVariable(" + identifier+ ").doChecking: type=" + type);
	    //Util.BREAK("END SubscriptedVariable("+identifier+").doChecking: type=" + type+", checkedParams="+checkedParams);
		
		
		SET_SEMANTICS_CHECKED();
	}

	  // Returns true if this expression may be used as a statement.
	  public boolean maybeStatement()
	  {	ASSERT_SEMANTICS_CHECKED(this);
		Declaration declaredAs=meaning.declaredAs;
		BlockDeclaration.Kind blockKind=declaredAs.blockKind;
		//Util.BREAK("Variable.maybeStatement("+identifier+"): meaning="+meaning);
		//Util.BREAK("Variable.maybeStatement("+identifier+"): declaredAs="+declaredAs+", BlockDeclaration.Kind="+blockKind+", qual="+declaredAs.getClass().getSimpleName());
		if(blockKind==BlockDeclaration.Kind.Procedure) return(true);
		if(blockKind==BlockDeclaration.Kind.Method) return(true);
		if(declaredAs instanceof Parameter)
		{ Parameter par=(Parameter)declaredAs;
		  if(par.kind==Parameter.Kind.Procedure) return(true);
		}
		return(false); // Variable, Parameter, Array, Class, Switch
	  }

	  
	  
	  

	// ******************************************************************
	// *** Coding: toJavaCode
	// ******************************************************************
	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		return (get());
	}
	  
	// ******************************************************************
	// *** Coding: put
	// ******************************************************************
	// Generate code for putting an value(expression) into this Variable
	public String put(String rightPart) {
		//Util.BREAK("Variable("+this+").put("+rightPart+")");
		ASSERT_SEMANTICS_CHECKED(this);
		String lhs = this.editVariable(true); // Is a Destination
		//Util.BREAK("Variable("+result+").put("+rightPart+")");
		if(meaning.declaredAs instanceof Parameter) {
			Parameter par=(Parameter)meaning.declaredAs;
			if(par.kind==Parameter.Kind.Simple && par.mode == Parameter.Mode.name) {
				return(lhs+".put("+rightPart+')');
			}
		}
		return(lhs+'='+rightPart);
	}

	// ******************************************************************
	// *** Coding: get
	// ******************************************************************
	// Generate code for getting the value of this Variable
	public String get() {
		ASSERT_SEMANTICS_CHECKED(this);
		// Util.BREAK("Variable("+identifier+").get() Meaning="+meaning);
		String result = this.editVariable(false); // Not a destination
		return (result);
	}
	
	
	// ******************************************************************
	// *** Coding: editVariable
	// ******************************************************************
	private String editVariable(boolean destination)
	{ Declaration decl=meaning.declaredAs;
	  BlockDeclaration.Kind blockKind=decl.blockKind;
	  //Util.BREAK("Variable.edVariable("+identifier+"): meaning="+meaning+", remotelyAccessed="+remotelyAccessed+", destination="+destination);
	  //Util.BREAK("Variable.edVariable("+identifier+"): decl="+decl+", BlockDeclaration.Kind="+blockKind+", qual="+decl.getClass().getSimpleName());
	  ASSERT_SEMANTICS_CHECKED(this);
	  Expression connectedObject=meaning.getInspectedVariable();
	  
	  if(decl instanceof ArrayDeclaration) { // Declared Array
		  //ArrayDeclaration array=(ArrayDeclaration)decl;
	      //Util.BREAK("SubscriptedVariable("+identifier+").get: ArrayDeclaration="+array);
		  StringBuilder s = new StringBuilder();
	      s.append(edIdentifierAccess(false));
	      if(this.hasArguments()) { // Array Element Access
	    	  int nDim=0;
	    	  s.append(".Elt"); //[x-M.LB[1]]");
	    	  for(Expression ix:checkedParams) {
	    		  String index="["+ix.toJavaCode()+"-"+edIdentifierAccess(false)+".LB["+(nDim++)+"]]";
	    		  s.append(index);
	    	  }
		  }
		  String result=s.toString();
		  return(result);
	  }
	  
	  else if (decl instanceof Virtual) { // Virtual Procedure/Label
		  //s.append(CallProcedure.virtual(this,(Virtual)decl,remotelyAccessed));
		  Virtual virtual=(Virtual)decl;
		  //Util.BREAK("Variable.edVariable("+id+"): virtual="+virtual);
		  //Util.BREAK("Variable.edVariable("+id+"): virtual'kind="+virtual.kind);
		  StringBuilder s = new StringBuilder();
		  if(virtual.kind==Virtual.Kind.Label) s.append(decl.getJavaIdentifier()).append("()");
		  else s.append(CallProcedure.virtual(this,virtual,remotelyAccessed)); 
		  String result=s.toString();
		  return(result);
	  }
	  
	  else if(decl instanceof BlockDeclaration) { // Declared Procedure
		  BlockDeclaration procedure = (BlockDeclaration) decl;
//	      BlockDeclaration.Kind blockKind=decl.blockKind;
	      StringBuilder s = new StringBuilder();
	      //Util.BREAK("SubscriptedVariable3("+identifier+").get: blockKind="+blockKind);
	      if(blockKind==BlockDeclaration.Kind.Method) { // Standard Procedure
			  //if(meaning.declaredAs instanceof StandardProcedure) {
				  if(identifier.equalsIgnoreCase("sourceline"))
			  		  return(""+Global.sourceLineNumber);
			  // }
	          s.append(CallProcedure.asNormalMethod(this));
	      }
	      
//	      else if(procedure.myVirtual!=null)
//	         s.append(CallProcedure.virtual(this,procedure.myVirtual,remotelyAccessed));
	      else if(blockKind==BlockDeclaration.Kind.Procedure) {
	    	  // This Variable is a Procedure-Identifier.
	    	  // When 'destination' it is a variable used to carry the resulting value until the final return.
	    	  // otherwise; it is a ordinary procedure-call.
	    	  if(destination) { // return("$result");
	    		  BlockDeclaration proc=(BlockDeclaration)meaning.declaredAs;
	    		  if(proc.blockLevel==Global.currentScope.blockLevel) return("$result");
	    	      String cast=proc.getJavaIdentifier();
	    	      return("(("+cast+")"+proc.edCTX()+").$result");
	    	  }
		      if(procedure.myVirtual!=null)
			       s.append(CallProcedure.virtual(this,procedure.myVirtual,remotelyAccessed));
		      else s.append(CallProcedure.normal(this));
	      }
	      else if(blockKind==BlockDeclaration.Kind.Class) {
	    	  Util.error("Illegal use of class identifier: "+identifier);
	      }
	      else Util.FATAL_ERROR("Umulig å komme hit ??");
		  String result=s.toString();
		  return(result);	    
	  }
	  
	  else if (decl instanceof Parameter) { // Specified Parameter: Array, Procedure, Label or Simple
		  StringBuilder s = new StringBuilder();
	      Parameter par=(Parameter)decl;
		  switch(par.kind)
		  { case Array: // Parameter Array
			  int nDim=0;
			  String var=edIdentifierAccess(false);
			  //Util.BREAK("Variable.editVariable'Parameter'Array: var="+var);;
			  if(connectedObject!=null) var=connectedObject.toJavaCode()+'.'+var;
			  if(par.mode==Parameter.Mode.name) var=var+".get()";
			  if(this.hasArguments()) {
//				  if(par.mode==Parameter.Mode.name) var=var+".get()";
				  StringBuilder ixs=new StringBuilder();
				  String dimBrackets="";
				  for(Expression ix:checkedParams) {
					  String index="["+ix.toJavaCode()+"-"+var+".LB["+(nDim++)+"]]"; 
					  ixs.append(index);
					  dimBrackets=dimBrackets+"[]";
				  }
				  String eltType=type.toJavaType();
				  String cast="$ARRAY<"+eltType+dimBrackets+">";
				  String castedVar="(("+cast+")"+var+")";
				  s.append(castedVar).append(".Elt").append(ixs);
			  } else s.append(var);
			  break;
		  case Procedure: // Parameter Procedure
			  if(connectedObject!=null) s.append(connectedObject.toJavaCode()).append('.');
			  if(par.mode==Parameter.Mode.value)
				  Util.error("Parameter "+this+" by Value is not allowed - Rewrite Program");
			  else // Procedure By Reference or Name.
				  s.append(CallProcedure.formal(this,par)); 
			  break;
		  case Simple:
		  case Label:
			  s.append(edIdentifierAccess(destination)); // Kind: Simple/Label
			  if(!destination && par.mode == Parameter.Mode.name) s.append(".get()");
		  }
		  
//		  if(!destination && par.mode == Parameter.Mode.name) s.append(".get()");
			  
		  String result=s.toString();
		  return(result);
	  }
	  
	  else if(decl instanceof LabelDeclaration) {
		  //Util.BREAK("LABEL DECLARATION: "+decl);
		  LabelDeclaration label=(LabelDeclaration)decl;
		  StringBuilder s = new StringBuilder();
//		  s.append(decl.getJavaIdentifier());
		  s.append(this.edIdentifierAccess(false));
		  //Util.BREAK("LABEL DECLARATION: myVirtual="+label.myVirtual);
		  if(label.myVirtual!=null) s.append("()"); // Access Virtual Label as Method
		  String result=s.toString();
		  return(result);
	  }
	  
	  else if(decl instanceof TypeDeclaration) {
		  //Util.BREAK("Variable.editVariable: TypeDeclaration: "+decl);
		  return(edIdentifierAccess(destination));
	  }
	  
	  Util.FATAL_ERROR("Umulig å komme hit ??");
	  return(null);
    }

	
	// ***********************************************************************
	// *** Coding: edIdentifierAccess
	// ***********************************************************************
	public String edIdentifierAccess(boolean destination)
	{ Declaration decl=meaning.declaredAs;
	  String id=decl.getJavaIdentifier();
	  if(remotelyAccessed) return(id);
	  
	  if(meaning.variableKind==Variable.Kind.connectedAttribute)
	  { Expression inspectedVariable=((ConnectionBlock)meaning.declaredIn).getInspectedVariable();
	    //Util.BREAK("Variable.toJavaCode: INSPECT - remoteAttribute="+meaning);
	    if(meaning.foundBehindInvisible)
	    { String remoteCast=meaning.foundIn.getJavaIdentifier();
		  id="(("+remoteCast+")("+inspectedVariable.toJavaCode()+"))."+id;
	    } else id=inspectedVariable.toJavaCode()+"."+id;
	  } else if(!(Option.standardClass && meaning.declaredIn.blockKind==BlockDeclaration.Kind.Method)) {
	    String cast=meaning.declaredIn.getJavaIdentifier();
		int n=meaning.declaredIn.blockLevel;
	    if(meaning.foundBehindInvisible) cast=meaning.foundIn.getJavaIdentifier();
	    else if(n==Global.currentScope.blockLevel) return(id);  // currentScope may be a sub-block  TODO: Check Dette !
	    id="(("+cast+")"+meaning.declaredIn.edCTX()+")."+id;
	  }
	  return(id);		  
	}
	
	// ***********************************************************************
	// *** Utility: toString
	// ***********************************************************************
	public String toString() {
		if(params==null) return ("" + identifier);
		else return (("" + identifier + params).replace('[', '(').replace(']', ')'));
	}

}
