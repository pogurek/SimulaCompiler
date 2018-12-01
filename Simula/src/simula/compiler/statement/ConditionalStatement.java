/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.statement;

import simula.compiler.JavaModule;
import simula.compiler.expression.Expression;
import simula.compiler.parsing.Parser;
import simula.compiler.utilities.Global;
import simula.compiler.utilities.KeyWord;
import simula.compiler.utilities.Type;
import simula.compiler.utilities.Util;

/**
 * Conditional Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   ConditionalStatement = Ifclause { Label : } ForStatement
 *                        | Ifclause { Label : } UnconditionalStatement  [ ELSE Statement ]
 *     Ifclause = IF BooleanExpression THEN
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public final class ConditionalStatement extends Statement {
	Expression condition;
	Statement thenStatement;
	Statement elseStatement = null;

	public ConditionalStatement() {
//		Util.BREAK("BEGIN ConditionalStatement:");
		condition = Expression.parseExpression();
//		Util.BREAK("BEGIN ConditionalStatement: condition="+condition);
		Parser.expect(KeyWord.THEN); thenStatement = Statement.doParse();
//		Util.BREAK("BEGIN ConditionalStatement: thenStatement="+thenStatement);
		if (Parser.accept(KeyWord.ELSE)) {
			elseStatement = Statement.doParse();
//			Util.BREAK("BEGIN ConditionalStatement: elseStatement="+elseStatement);
		}
//		if (Parser.accept(KeyWord.THEN)) Util.error("Missplaced keyword: THEN");
//		if (Parser.accept(KeyWord.STEP)) Util.error("Missplaced keyword: STEP");
		Parser.skipMissplacedSymbol(KeyWord.THEN);
		Parser.skipMissplacedSymbol(KeyWord.STEP);
	}

	public void print(String indent) {
		StringBuilder s = new StringBuilder(indent);
		s.append("IF ").append(condition);
		System.out.println(s.toString());
		System.out.println(indent + "THEN ");
		if (elseStatement != null) {
			thenStatement.print(indent + "    ");
			System.out.println(indent + "ELSE ");
			elseStatement.print(indent + "     ");
		} else
			thenStatement.print(indent + "    ");
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		condition.doChecking();
		condition.backLink=this; // To ensure RESULT$ from functions
		//Util.BREAK("ConditionalStatement.doChecking: condition="+condition);
		//Util.BREAK("ConditionalStatement.doChecking: condition.type="+condition.type);
		if (!condition.type.equals(Type.Boolean))
			Util.error("ConditionalStatement.doChecking: Condition is not of Type Boolean, but: "
					+ condition.type);
		thenStatement.doChecking();
		if (elseStatement != null)
			elseStatement.doChecking();
		SET_SEMANTICS_CHECKED();
	}

	public void doJavaCoding() {
		Global.sourceLineNumber=lineNumber;
		ASSERT_SEMANTICS_CHECKED(this);
		JavaModule.code("if(" + condition.toJavaCode() + ") {");
		thenStatement.doJavaCoding();
		if (elseStatement != null) {
			JavaModule.code("} else");
			elseStatement.doJavaCoding();
		} else
			JavaModule.code("}");
	}

	public String toString() {
		return ("IF " + condition + " THEN " + thenStatement + " ELSE "
				+ elseStatement + ';');
	}
}
