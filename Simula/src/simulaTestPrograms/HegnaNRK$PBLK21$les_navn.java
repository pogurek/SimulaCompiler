package simulaTestPrograms;
// Simula-Beta-0.3 Compiled at Fri May 03 22:07:42 CEST 2019
import simula.runtime.*;
@SuppressWarnings("unchecked")
public final class HegnaNRK$PBLK21$les_navn extends BASICIO$ {
    public int prefixLevel() { return(0); }
    public TXT$ RESULT$;
    public Object RESULT$() { return(RESULT$); }
    char char$=0;
    TXT$ navn=null;
    public HegnaNRK$PBLK21$les_navn(RTObject$ SL$) {
        super(SL$);
        BBLK();
        STM$();
    }
    public HegnaNRK$PBLK21$les_navn STM$() {
        navn=blanks(((HegnaNRK$PBLK21)(CUR$.SL$)).navne_lengde$1);
        char$=sysin().inchar();
        while((char$==(((char)32)))) {
            char$=sysin().inchar();
        }
        while(((char$!=(((char)32)))&((TXT$.pos(navn)<=(((HegnaNRK$PBLK21)(CUR$.SL$)).navne_lengde$1))))) {
            {
                TXT$.putchar(navn,char$);
                char$=sysin().inchar();
            }
        }
        RESULT$=copy(copy(TXT$.strip(navn)));
        EBLK();
        return(this);
    }
    public static PROGINFO$ INFO$=new PROGINFO$("HegnaNRK.sim","Procedure les_navn",1,341,10,343,12,344,20,345,22,346,24,347,28,348,30,350,33,351,37,353,41,354);
}
