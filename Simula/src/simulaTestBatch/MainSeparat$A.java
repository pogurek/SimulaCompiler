package simulaTestBatch;
// Simula-Beta-0.3 Compiled at Sun May 12 15:12:53 CEST 2019
import simula.runtime.*;
@SuppressWarnings("unchecked")
public class MainSeparat$A extends CLASS$ {
    public int prefixLevel() { return(0); }
    public ARRAY$<float[]>X=null;
    public int ord=0;
    public MainSeparat$A(RTObject$ staticLink) {
        super(staticLink);
        BBLK(); // Iff no prefix
        TRACE_BEGIN_DCL$("A",60);
        int[] X$LB=new int[1]; int[] X$UB=new int[1];
        X$LB[0]=((int)((int)Math.round(new MainSeparat$P(((MainSeparat)(CUR$.SL$))).RESULT$))); X$UB[0]=1;
        BOUND_CHECK$(X$LB[0],X$UB[0]);
        X=new ARRAY$<float[]>(new float[X$UB[0]-X$LB[0]+1],X$LB,X$UB);
    }
    public MainSeparat$A STM$() {
        ord=((MainSeparat)(CUR$.SL$)).nA=(((MainSeparat)(CUR$.SL$)).nA+(1));
        new MainSeparat$trace(((MainSeparat)(CUR$.SL$)),CONC(new TXT$("Event A-1: "),new MainSeparat$A$idA(((MainSeparat$A)CUR$)).RESULT$));
        EBLK();
        return(this);
    }
    public static PROGINFO$ INFO$=new PROGINFO$("ExternalClass1.sim","Class A",1,57,8,58,10,59,16,58,23,61,25,62,27,61,30,63);
}
