// JavaLine 1 ==> SourceLine 38
package simulaTestBatch;
// Simula-Beta-0.3 Compiled at Sun Mar 17 18:20:58 CET 2019
import simula.runtime.*;
@SuppressWarnings("unchecked")
public final class simtst102$PBLK38 extends Simulation$ {
    // PrefixedBlockDeclaration: BlockKind=PrefixedBlock, BlockLevel=3, firstLine=38, lastLine=58, hasLocalClasses=true, System=true, detachUsed=false
    public int prefixLevel() { return(0); }
    public boolean isQPSystemBlock() { return(true); }
    public boolean isDetachUsed() { return(true); }
    // Declare parameters as attributes
    // Declare locals as attributes
    simtst102$p$simtst102$PBLK38$Car car1=null;
    // Normal Constructor
    public simtst102$PBLK38(RTObject$ staticLink) {
        super(staticLink);
        // Parameter assignment to locals
        // Declaration Code
        TRACE_BEGIN_DCL$("simtst102$PBLK38",42);
        // Create Class Body
        CODE$=new ClassBody(CODE$,this,2) {
            public void STM$() {
                TRACE_BEGIN_STM$("simtst102$PBLK38",42,inner);
                // JavaLine 24 ==> SourceLine 52
                new simtst102$trace(((simtst102)(CUR$.SL$.SL$)),52,new TXT$("START SIMULATION"));
                // JavaLine 26 ==> SourceLine 53
                car1=new simtst102$p$simtst102$PBLK38$Car(((simtst102$PBLK38)CUR$),new TXT$("Bil 1")).START();
                // JavaLine 28 ==> SourceLine 54
                new simtst102$trace(((simtst102)(CUR$.SL$.SL$)),54,CONC(car1.p3$pname,new TXT$(" is scheduled for 4.0")));
                // JavaLine 30 ==> SourceLine 55
                ((simtst102$PBLK38)CUR$).ActivateAt(false,car1,4.0f,false);
                // JavaLine 32 ==> SourceLine 56
                hold(((double)(12)));
                TRACE_END_STM$("simtst102$PBLK38",56);
            }
        };
    } // End of Constructor
    public static PROGINFO$ INFO$=new PROGINFO$("simtst102.sim","PrefixedBlock simtst102$PBLK38",1,38,24,52,26,53,28,54,30,55,32,56,37,58);
}