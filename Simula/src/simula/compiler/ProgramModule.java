/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler;

import simula.compiler.declaration.ConnectionBlock;
import simula.compiler.declaration.Declaration;
import simula.compiler.declaration.BlockDeclaration;
import simula.compiler.declaration.StandardClass;
import simula.compiler.expression.SubscriptedVariable;
import simula.compiler.expression.Variable;
import simula.compiler.parsing.Parser;
import simula.compiler.statement.Statement;
import simula.compiler.utilities.Global;
import simula.compiler.utilities.KeyWord;
import simula.compiler.utilities.Option;
import simula.compiler.utilities.Type;
import simula.compiler.utilities.Util;

/**
 * Simula Program.
 * 
 * <pre>
 * 
 * Syntax:
 *
 * ProgramModule = SimulaPrpgram | ClassDeclaration | ProcedureDeclaration
 * 
 * SimulaProgram = [ BlockPrefix ] Block | [ BlockPrefix ] CompoundStatement
 * 
 *	 BlockPrefix = ClassIdentifier [ ( ActualParameterList ) ]
 *
 * ProcedureDeclaration
 *     = [ type ] PROCEDURE ProcedureIdentifier ProcedureHead ProcedureBody
 *
 * </pre>
 * 
 * @author �ystein Myhre Andersen
 */
public class ProgramModule extends Statement
{ Declaration module;
  SubscriptedVariable sysin;
  SubscriptedVariable sysout;

  public ProgramModule()
  { try
	{ if(Option.TRACE_PARSE) Parser.TRACE("Parse Program");
//	  SimpleParser.nextSymb();  // Flytta til SimpleParse.open
	
	  Global.currentScope=StandardClass.BASICIO;     // BASICIO Begin
	  sysin=new SubscriptedVariable("sysin");
	  new ConnectionBlock(sysin)                     //    Inspect sysin do
	     .setClassDeclaration(StandardClass.InFile);
	  sysout=new SubscriptedVariable("sysout");
	  new ConnectionBlock(sysout)                    //    Inspect sysout do
	     .setClassDeclaration(StandardClass.PrintFile);
	  String ident=acceptIdentifier();
	  if(ident!=null)
	  { if(Parser.accept(KeyWord.CLASS)) module=BlockDeclaration.doParseClassDeclaration(ident);
	    else
	    { Variable prefix=Variable.parse(ident);	
	  	  Parser.expect(KeyWord.BEGIN);
	  	  module=BlockDeclaration.createMaybeBlock(prefix,Global.sourceName); 
	    }
	  }
	  else if(Parser.accept(KeyWord.BEGIN)) module=BlockDeclaration.createMaybeBlock(null,Global.sourceName); 
	  else if(Parser.accept(KeyWord.CLASS)) module=BlockDeclaration.doParseClassDeclaration(null);
	  else
	  { Type type=acceptType();
	    if(Parser.expect(KeyWord.PROCEDURE)) module=BlockDeclaration.doParseProcedureDeclaration(type);
	  }
	  if(Option.verbose) Util.TRACE("ProgramModule: END NEW SimulaProgram: "+toString());
    } catch(Throwable e) { e.printStackTrace();  /*System.exit(-1);*/ }
  }	

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
    sysin.doChecking();
    sysout.doChecking();
    module.doChecking();
	SET_SEMANTICS_CHECKED();
  }
  
  public void doJavaCoding(String indent) { module.doJavaCoding(indent); }
  public void doJVMCoding() { module.doJVMCoding(); }
  public void print(String indent,String tail) { module.print("",tail); }
  public String toString() { return(""+module); }
  public String getIdentifier() { return(module.identifier); }
}