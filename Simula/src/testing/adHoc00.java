package testing;
// Simula Beta(0.1) Compiled at Sun Apr 22 15:06:38 CEST 2018
import simula.runtime.*;
public class adHoc00 extends RTObject$ {
   // BlockKind=SimulaProgram, BlockLevel=1, hasLocalClasses=true, System=true, detachUsed=false
   public boolean isQPSystemBlock() { return(true); }
   // Declare parameters as attributes
   // Declare locals as attributes
   int i=0;
   // Normal Constructor
   public adHoc00(RTObject$ staticLink) {
      super(staticLink);
      // Parameter assignment to locals
      // Declaration Code
   }
   // SimulaProgram Statements
   public RTObject$ STM() {
      BPRG("adHoc00");
      sysout().outtext(new TXT$("Hallo fra meg �ss� !!!"));
      EBLK();
      return(null);
   }

   public static void main(String[] args) {
     new adHoc00(CTX$).STM();
   }
}
