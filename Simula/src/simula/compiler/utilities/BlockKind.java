/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler.utilities;

/**
 * Block Kind.
 * <p>
 * 
 * @author �ystein Myhre Andersen
 *
 */
public enum BlockKind {
	StandardClass,
	ConnectionBlock,
	CompoundStatement,
    SubBlock,
    Procedure,
    Method, // Procedure coded as a Java Method. 
    Class,
    PrefixedBlock,
    SimulaProgram,
    Switch
}
