/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.declaration;

import java.util.Iterator;
import java.util.Vector;

import simula.compiler.utilities.Type;

/**
 * Procedure Specification.
 * <pre>
 * Syntax:
 * 
 * ProcedureSpecification
 *     = [ type ] PROCEDURE ProcedureIdentifier ProcedureHead EmptyBody
 *     
 * ProcedureHead
 *     = [ FormalParameterPart ; [ ModePart ]
 *         specification-part  ] ;
 *         
 * ProcedureBody = Statement
 * ProcedureIdentifier = Identifier
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ProcedureSpecification
{ public String identifier;
  public Type type;  
  public Vector<Parameter> parameterList=new Vector<Parameter>();
  
  // ***********************************************************************************************
  // *** CONSTRUCTOR
  // ***********************************************************************************************
  public ProcedureSpecification(String identifier,Type type,Vector<Parameter> parameterList)
  { this.identifier=identifier; this.type=type; this.parameterList=parameterList; } 
  
  // ***********************************************************************************************
  // *** Utility: doChecking 
  // ***********************************************************************************************
  public void doChecking(DeclarationScope scope)
  { //Util.BREAK("ProcedureSpecification.checkCompatible: this Type="+type);
	if(type!=null) type.doChecking(scope);
	// Check parameters
	for(Parameter par:parameterList) par.doChecking();
  }
  
  // ***********************************************************************************************
  // *** Utility: checkCompatible  -- 
  // ***********************************************************************************************
  public boolean checkCompatible(BlockDeclaration proc)
  { //Util.BREAK("ProcedureSpecification.checkCompatible: this Type="+type);
    //Util.BREAK("ProcedureSpecification.checkCompatible: othr Type="+proc.type);
    //Util.BREAK("ProcedureSpecification.checkCompatible: TypeEquals="+type.equals(proc.type));
	if(type!=null && !type.equals(proc.type)) return(false);
	// Check parameters
	Iterator<Parameter> formalIterator = parameterList.iterator();
	Iterator<Parameter> actualIterator = proc.parameterList.iterator();
	//Util.BREAK("SubscriptedVariable("+identifier+").doChecking: Params="+params);
	while (actualIterator.hasNext()) {
		if (!formalIterator.hasNext()) return(false); //Util.error("Wrong number of parameters to " + block);
		Parameter formalParameter = (Parameter)formalIterator.next();
	    //Util.BREAK("ProcedureSpecification.checkCompatible: formalParameter="+formalParameter);
		Parameter actualParameter = actualIterator.next();
	    //Util.BREAK("ProcedureSpecification.checkCompatible: actualParameter="+actualParameter);
	    //Util.BREAK("ProcedureSpecification.checkCompatible: TypeEquals="+formalParameter.equals(actualParameter));
		if(!formalParameter.equals(actualParameter)) return(false);
	}
	if (formalIterator.hasNext()) return(false); //Util.error("Wrong number of parameters to " + block);
    return(true);
  }
  
  // ***********************************************************************************************
  // *** Printing Utility: editParameterList
  // ***********************************************************************************************
  private String editParameterList()
  { StringBuilder s=new StringBuilder(); s.append('(');
    boolean first=true;
    for(Parameter par:parameterList)
    { if(!first) s.append(','); s.append(par); first=false; }
    s.append(')');
    s.append(';');
    return(s.toString());
  }

  public String toString()
  { StringBuilder s=new StringBuilder();
    if(type!=null) s.append(type).append(' ');
    s.append("PROCEDURE ").append(identifier).append(editParameterList());
	return(s.toString());
  }

  
}