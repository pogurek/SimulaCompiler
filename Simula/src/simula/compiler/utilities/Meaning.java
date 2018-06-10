/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.utilities;

import simula.compiler.declaration.ConnectionBlock;
import simula.compiler.declaration.Declaration;
import simula.compiler.declaration.DeclarationScope;
import simula.compiler.expression.Expression;

/**
 * 
 * @author Øystein Myhre Andersen
 *
 */
public class Meaning {
	public VariableKind variableKind;
	public boolean foundBehindInvisible; // Behind hidden/protected
	public Declaration declaredAs;
	public DeclarationScope declaredIn;  // Search started here
	public DeclarationScope foundIn;     // Search ended here
	
	
	public Meaning(VariableKind variableKind,Declaration declaredAs,DeclarationScope declaredIn)
	{ this.variableKind=variableKind;
	  this.declaredAs=declaredAs;
	  this.declaredIn=declaredIn;
	}
	public Meaning(VariableKind variableKind,Declaration declaredAs,DeclarationScope declaredIn
			      ,DeclarationScope foundIn,boolean foundBehindInvisible)
	{ this(variableKind,declaredAs,declaredIn);
	  this.foundIn=foundIn;
	  this.foundBehindInvisible=foundBehindInvisible;
    }
	public Meaning(VariableKind variableKind) // Used by: ConnectedAttribute
	{ this.variableKind=variableKind;
	}
	
	public Expression getInspectedVariable() {
		if(variableKind!=VariableKind.connectedAttribute) return(null);
		return(((ConnectionBlock)declaredIn).getInspectedVariable());
	}
	  
	// ***************************************************************************************
	// *** CODING: edStaticLink
	// ***************************************************************************************
	public String edStaticLink()
	{ // Edit staticLink reference
	  String staticLink;
	  Expression connectedObject=getInspectedVariable();
      //Util.BREAK("Meaning.edStaticLink: connectedObject="+connectedObject);
      if(connectedObject!=null)
    	   staticLink=connectedObject.toJavaCode();
      else
      {
//        Util.BREAK("Meaning.edStaticLink: Meaning="+this);
//        Util.BREAK("Meaning.edStaticLink: Meaning'declaredIn.blockLevel="+declaredIn.blockLevel);
//        Util.BREAK("Meaning.edStaticLink: Current="+Global.currentScope);
//        Util.BREAK("Meaning.edStaticLink: Current'BlockLevel="+Global.currentScope.blockLevel);
    	staticLink=Global.currentScope.edCTX(declaredIn.blockLevel);
	    String cast=declaredIn.getJavaIdentifier();
	    staticLink="(("+cast+")"+staticLink+')';  // Unnecessary Cast for debugging purposes
      }
      
      //Util.BREAK("Meaning.edStaticLink: staticLink="+staticLink);
      return(staticLink);
	}
	
	public String toString()
	{ return(variableKind.toString()+' '+declaredAs+", foundBehindInvisible="+foundBehindInvisible
			+"  (blockLevel="+declaredIn.blockLevel+",declaredIn="+declaredIn+",foundIn="+foundIn+')');
	}
}
