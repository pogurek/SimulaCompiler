// JavaLine 1 ==> SourceLine 10
package simulaTestPrograms;
// Simula-Beta-0.3 Compiled at Wed Mar 13 15:33:31 CET 2019
import simula.runtime.*;
@SuppressWarnings("unchecked")
public class CoroutineSample$A extends CLASS$ {
    // ClassDeclaration: BlockKind=Class, BlockLevel=2, PrefixLevel=0, firstLine=10, lastLine=18, hasLocalClasses=false, System=false, detachUsed=true
    public int prefixLevel() { return(0); }
    public boolean isDetachUsed() { return(true); }
    // Declare parameters as attributes
    // Declare locals as attributes
    // JavaLine 12 ==> SourceLine 12
    public int i=0;
    // Normal Constructor
    public CoroutineSample$A(RTObject$ staticLink) {
        super(staticLink);
        // Parameter assignment to locals
        BBLK(); // Iff no prefix
        // Declaration Code
        TRACE_BEGIN_DCL$("A",12);
        // Create Class Body
        CODE$=new ClassBody(CODE$,this,0) {
            public void STM$() {
                TRACE_BEGIN_STM$("A",12,inner);
                // JavaLine 25 ==> SourceLine 13
                new CoroutineSample$trace(((CoroutineSample)(CUR$.SL$)),new TXT$("A: State 1(Initiating)"));
                // JavaLine 27 ==> SourceLine 14
                detach();
                // JavaLine 29 ==> SourceLine 15
                new CoroutineSample$trace(((CoroutineSample)(CUR$.SL$)),new TXT$("A: State 2"));
                // JavaLine 31 ==> SourceLine 16
                detach();
                // JavaLine 33 ==> SourceLine 17
                new CoroutineSample$trace(((CoroutineSample)(CUR$.SL$)),new TXT$("A: State 3(Terminating)"));
                // JavaLine 35 ==> SourceLine 13
                if(inner!=null) {
                    inner.STM$();
                    TRACE_BEGIN_STM_AFTER_INNER$("A",13);
                }
                TRACE_END_STM$("A",13);
                EBLK(); // Iff no prefix
            }
        };
    } // End of Constructor
    // Class Statements
    public CoroutineSample$A STM$() { return((CoroutineSample$A)CODE$.EXEC$()); }
    public CoroutineSample$A START() { START(this); return(this); }
    public static PROGINFO$ INFO$=new PROGINFO$("CoroutineSample.sim","Class A",1,10,12,12,25,13,27,14,29,15,31,16,33,17,35,13,47,18);
}