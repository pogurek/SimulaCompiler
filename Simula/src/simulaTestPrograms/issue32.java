// JavaLine 1 <== SourceLine 3
package simulaTestPrograms;
// Simula-1.0 Compiled at Tue Jun 04 09:37:28 CEST 2019
import simula.runtime.*;
@SuppressWarnings("unchecked")
public final class issue32 extends Simulation$ {
    // PrefixedBlockDeclaration: BlockKind=PrefixedBlock, BlockLevel=1, firstLine=3, lastLine=10, hasLocalClasses=true, System=true, detachUsed=false
    public int prefixLevel() { return(0); }
    public boolean isQPSystemBlock() { return(true); }
    public boolean isDetachUsed() { return(true); }
    // Declare parameters as attributes
    // Declare locals as attributes
    // Normal Constructor
    public issue32(RTObject$ staticLink) {
        super(staticLink);
        // Parameter assignment to locals
        BPRG("issue32");
        // Declaration Code
        TRACE_BEGIN_DCL$("issue32",6);
    }
    // Class Statements
    public issue32 STM$() {
        TRACE_BEGIN_STM$("issue32",6);
        // Class Simset: Code before inner
        // Class Simulation: Code before inner
        // Class issue32: Code before inner
        new issue32$Customer$abra(((issue32$Customer)new issue32$Customer(((issue32)(CUR$))).START$()));
        TRACE_END_STM$("issue32",8);
        EBLK();
        return(this);
    } // End of Class Statements
    
    public static void main(String[] args) {
        //System.setProperty("file.encoding","UTF-8");
        RT.setRuntimeOptions(args);
        new issue32(CTX$).STM$();
    } // End of main
    public static PROGINFO$ INFO$=new PROGINFO$("issue32.sim","PrefixedBlock issue32",1,3,37,10);
} // End of Class
