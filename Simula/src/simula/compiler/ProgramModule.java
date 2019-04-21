/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler;

import simula.compiler.declaration.BlockKind;
import simula.compiler.declaration.ClassDeclaration;
import simula.compiler.declaration.ConnectionBlock;
import simula.compiler.declaration.Declaration;
import simula.compiler.declaration.ExternalDeclaration;
import simula.compiler.declaration.MaybeBlockDeclaration;
import simula.compiler.declaration.PrefixedBlockDeclaration;
import simula.compiler.declaration.ProcedureDeclaration;
import simula.compiler.declaration.StandardClass;
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
 * ProgramModule = SimulaProgram | ClassDeclaration | ProcedureDeclaration
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
 * @author Øystein Myhre Andersen
 */
public final class ProgramModule extends Statement {
	final Declaration module;
	final Variable sysin;
	final Variable sysout;
  
	public String getRelativeAttributeFileName() {
		if(module.blockKind==BlockKind.Class) return(Global.packetName+"/CLASS.AF");
		if(module.blockKind==BlockKind.Procedure) return(Global.packetName+"/PROCEDURE.AF");
		else return(null);
	  }
	  
	public boolean isExecutable() {
		if(module.blockKind==BlockKind.SimulaProgram) return(true);
		if(module.blockKind==BlockKind.PrefixedBlock) return(true);
		else return(false);
	}

	public ProgramModule() {
		Declaration module=null;
		sysin=new Variable("sysin");
		sysout=new Variable("sysout");
		try	{
			if(Option.TRACE_PARSE) Parser.TRACE("Parse Program");
			Global.currentScope=StandardClass.BASICIO;     // BASICIO Begin
			new ConnectionBlock(sysin,null)                     //    Inspect sysin do
			     .setClassDeclaration(StandardClass.InFile);
			new ConnectionBlock(sysout,null)                    //    Inspect sysout do
			     .setClassDeclaration(StandardClass.PrintFile);
			Global.currentScope.sourceBlockLevel=0;
			while(Parser.accept(KeyWord.EXTERNAL)) {
				ExternalDeclaration.doParse(StandardClass.ENVIRONMENT.declarationList);
				//Util.BREAK("BEGIN PRINT ENVIRONMENT");
				//StandardClass.ENVIRONMENT.print(0);
				//Util.BREAK("END PRINT ENVIRONMENT");
				Parser.expect(KeyWord.SEMICOLON);
			}
			String ident=acceptIdentifier();
			if(ident!=null) {
				if(Parser.accept(KeyWord.CLASS)) module=ClassDeclaration.doParseClassDeclaration(ident);
			    else {
			    	Variable blockPrefix=Variable.parse(ident);	
			  	    Parser.expect(KeyWord.BEGIN);
			  	    module=PrefixedBlockDeclaration.createPrefixedBlock(blockPrefix); 
			    }
			}
			else if(Parser.accept(KeyWord.BEGIN)) module=MaybeBlockDeclaration.createMaybeBlock(); 
			else if(Parser.accept(KeyWord.CLASS)) module=ClassDeclaration.doParseClassDeclaration(null);
			else {
				Type type=acceptType();
			    if(Parser.expect(KeyWord.PROCEDURE)) module=ProcedureDeclaration.doParseProcedureDeclaration(type);
			}
			StandardClass.BASICIO.declarationList.add(module);
		
			if(Option.verbose) Util.TRACE("ProgramModule: END NEW SimulaProgram: "+toString());
		} catch(Throwable e) { Util.INTERNAL_ERROR("Impossible",e); }
		this.module=module;
	}	

	public void doChecking() {
		if(IS_SEMANTICS_CHECKED()) return;
		sysin.doChecking();
		sysout.doChecking();
		module.doChecking();
		SET_SEMANTICS_CHECKED();
	}
  
	public void doJavaCoding() { module.doJavaCoding(); }
	public void print(final int indent) { module.print(0); }
	public String toString() { return((module==null)?"":""+module.externalIdent); }
	public String getIdentifier() { return(module.identifier); }
	
}
