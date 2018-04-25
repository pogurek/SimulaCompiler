/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.expression;

import simula.compiler.declaration.Declaration;
import simula.compiler.declaration.Parameter;
import simula.compiler.utilities.Global;
import simula.compiler.utilities.ParameterKind;
import simula.compiler.utilities.Type;
import simula.compiler.utilities.Type.ConversionKind;
import simula.compiler.utilities.Util;

public class TypeConversion extends Expression { // Type type; // Inherited
	Expression expression;

	private TypeConversion(Type type, Expression expression) {
		//Util.BREAK("new TypeConversion("+type+','+expression+") qual="+expression.getClass().getSimpleName());
		this.type=type;
		this.expression = expression; expression.backLink=this;
		if(type!=Type.LabelQuantity)
		    this.doChecking();
	}

	// Test if a TypeConversion is necessary and then do it.
	public static String mayBeConvert(Type fromType,Type toType,String expr) {
		if(fromType==Type.Real || fromType==Type.LongReal)
		{ if(toType==Type.Integer || toType==Type.ShortInteger)
            return("=("+toType.toJavaType()+")("+expr+"+0.5);");
		}
        return("=("+toType.toJavaType()+")("+expr+");");
	}

	// Test if a TypeConversion is necessary and then create it.
	public static Expression testAndCreate(Type toType, Expression expression) {
		if (testCastNeccessary(toType, expression))
			return (new TypeConversion(toType, expression));
		return (expression);
	}

	// Test if a TypeConversion is necessary.
	private static boolean testCastNeccessary(Type toType, Expression expression) {
		//Util.BREAK("TypeConversion.testCastNeccessary("+toType+','+expression+") qual="+expression.getClass().getSimpleName());
		if (toType == null)	return (false);
		Type fromType = expression.type;
		//Util.BREAK("TypeConversion.testCastNeccessary: "+fromType+" ==> "+toType);
		if(fromType==null) Util.error("Expression "+expression+" has no type - can't be converted to "+toType);
		ConversionKind conversionKind = fromType.isConvertableTo(toType);
		switch (conversionKind) {
		case DirectAssignable:	return (false);
		case ConvertValue:
		case ConvertRef:		return (true);
		case Illegal:
			Util.error("TypeConversion: Illegal cast: (" + toType + ") "
					+ expression);
		default: return (false);
		}
	}
	  public Variable getVariable() // Is redefined in (Variable and) BinaryOperation(DOT)
	  {	return(expression.getVariable()); }

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		// Util.BREAK("TypeConversion.doChecking(): "+this);
		type.doChecking(Global.currentScope);
		expression.doChecking();
		Type type = expression.type;
		// Util.BREAK("TypeConversion.doChecking(): Cast="+this.type+", expression.type="+type.toJavaType());
		if (type.isConvertableTo(this.type) == Type.ConversionKind.Illegal)
			Util.error("Illegal Type Conversion " + type + " ==> " + this.type);
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		//Util.BREAK("TypeConversion.toJavaCode: "+expression.type+" ==> "+type);
		//Util.BREAK("TypeConversion.toJavaCode: expression="+expression+", qual="+expression.getClass().getSimpleName());
		
		String evaluated=expression.toJavaCode();
		
		if(type==Type.LabelQuantity)
		{ Util.ASSERT(expression instanceof Variable,"Invariant");
		  Util.ASSERT(expression.type==Type.Label,"Invariant");
		  Variable lab=(Variable)expression;
		  String staticLink=lab.meaning.edStaticLink();
		  return("new $LABQNT("+staticLink+","+evaluated+")");
		}
		
		String cast=type.toJavaType();
		if(expression instanceof Variable)
		{ Variable var=(Variable)expression;
		  Declaration declaredAs=var.meaning.declaredAs;
		  //Util.BREAK("TypeConversion.toJavaCode: declaredAs="+declaredAs+", qual="+declaredAs.getClass().getSimpleName());
		  if(declaredAs instanceof Parameter)
		  { Parameter par=(Parameter)declaredAs;
		    Type type=par.type;
		    ParameterKind kind=par.kind;
			//Util.BREAK("TypeConversion.toJavaCode: type="+type+", kind="+kind);
			if(kind==ParameterKind.Procedure)
			{ evaluated=par.identifier+".get().CPF().$result()";
				
			  if(type.isArithmeticType())
				  return(cast+"Value("+evaluated+")"); // NYLIG RETTA
			}
		  }
		}
		if(type==Type.Integer || type==Type.ShortInteger)
		{ Type fromType=expression.type;
		  if(fromType==Type.Real || fromType==Type.LongReal)
			  evaluated="(("+evaluated+")+0.5)";
		}
		return ("((" + cast + ")(" + evaluated + "))");
	}

	public String toString() {
		return ("((" + type + ")(" + expression + "))");
	}

}