package simulaTestPrograms;
// Simula-Beta-0.3 Compiled at Fri May 03 22:07:42 CEST 2019
import simula.runtime.*;
@SuppressWarnings("unchecked")
public class HegnaNRK$PBLK21$liste_element extends Link$ {
    public int prefixLevel() { return(2); }
    public int p2$nr;
    public HegnaNRK$PBLK21$liste_element(RTObject$ staticLink,int sp2$nr) {
        super(staticLink);
        this.p2$nr = sp2$nr;
        TRACE_BEGIN_DCL$("liste_element",222);
    }
    public HegnaNRK$PBLK21$liste_element STM$() {
        EBLK();
        return(this);
    }
    public static PROGINFO$ INFO$=new PROGINFO$("HegnaNRK.sim","Class liste_element",1,222,15,224,18,224);
}
