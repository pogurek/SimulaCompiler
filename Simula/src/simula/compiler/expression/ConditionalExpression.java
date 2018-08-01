/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.expression;

import simula.compiler.utilities.Global;
import simula.compiler.utilities.Option;
import simula.compiler.utilities.Type;
import simula.compiler.utilities.Util;

/**
 * Conditional Expression.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   ConditionalExpression
 *       = IF BooleanExpression THEN SimpleExpression ELSE Expression
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ConditionalExpression extends Expression {
	Expression condition;
	Expression thenExpression;
	Expression elseExpression;

	public ConditionalExpression(Type type, Expression condition,
			Expression thenExpression, Expression elseExpression) {
		this.condition = condition;
		this.thenExpression = thenExpression; thenExpression.backLink=this;
		this.elseExpression = elseExpression; elseExpression.backLink=this;
		if (Option.TRACE_PARSE)
			Util.TRACE("NEW ConditionalExpression: " + toString());
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())	return;
		Global.sourceLineNumber=lineNumber;
		//Util.warning("ConditionalExpression in this Context is not Supported - Rewrite program");
		condition.doChecking();
		condition.backLink=this; // To ensure $result from functions
		//Util.BREAK("ConditionalExpression.doChecking(): Condition: "+condition);
		Type cType = condition.type;
		if (cType != Type.Boolean)
			Util.error("ConditionalExpression: Condition is not a boolean (rather "
					+ cType + ")");
		thenExpression.doChecking();
		elseExpression.doChecking();
		Type expectedType=Type.commonTypeConversion(thenExpression.type,elseExpression.type);
		thenExpression = TypeConversion.testAndCreate(expectedType, thenExpression);
		elseExpression = TypeConversion.testAndCreate(expectedType, elseExpression);
		thenExpression.doChecking(); // In case TypeConversion was added
		elseExpression.doChecking(); // In case TypeConversion was added
		this.type=expectedType;
		// Util.BREAK("END ConditionalExpression" + toString() + ".doChecking - Result type=" + this.type);
		SET_SEMANTICS_CHECKED();
	}

	  // Returns true if this expression may be used as a statement.
	  public boolean maybeStatement()
	  {	ASSERT_SEMANTICS_CHECKED(this);
		return(false);  
	  }

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		//Util.BREAK("ConditionalExpression.toJavaCode: thenExpression="+thenExpression+", qual="+thenExpression.getClass().getSimpleName());
		//Util.BREAK("ConditionalExpression.toJavaCode: elseExpression="+elseExpression+", qual="+elseExpression.getClass().getSimpleName());
		return ("((" + condition.get() + ")?("
				+ thenExpression.get() + "):("
				+ elseExpression.get() + "))");
	}

	public String toString() {
		return ("(IF " + condition + " THEN " + thenExpression + " ELSE "
				+ elseExpression + ')');
	}
}
