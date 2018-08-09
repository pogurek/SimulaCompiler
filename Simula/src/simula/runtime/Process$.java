/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.runtime;

/**
 * <pre>
 * link class process;
 *      begin ref (EVENT_NOTICE) EVENT;
 *         Boolean TERMINATED;
 *         Boolean procedure idle;              idle := EVENT==none;
 *         Boolean procedure terminated;  terminated := TERMINATED;
 *
 *         long real procedure evtime;
 *            if idle then  error("..." ! No Evtime for idle process)
 *            else evtime := EVENT.EVTIME;
 *
 *         ref (process) procedure nextev;
 *            nextev :- if  idle or else EVENT.suc == none then none
 *                     else EVENT.suc.PROC;
 *
 *         detach;
 *         inner;
 *         TERMINATED:= true;
 *         passivate;
 *         error("..." ! Terminated process;)
 *     end process;
 * </pre>
 * 
 * An object of a class prefixed by "process" is called a process object. A
 * process object has the properties of "link" and, in addition, the capability
 * to be represented in the sequencing set and to be manipulated by certain
 * sequencing statements which may modify its "process state". The possible
 * process states are: active, suspended, passive and terminated.
 * <p>
 * When a process object is generated it immediately becomes detached and its
 * reactivation point positioned in front of the first statement of its user-
 * defined operation rule. The process object remains detached throughout its
 * dynamic scope.
 * <p>
 * The procedure "idle" has the value true if the process object is not
 * currently represented in the sequencing set. It is said to be in the passive
 * or terminated state depending on the value of the procedure "terminated". An
 * idle process object is passive if its reactivation point is at a user-defined
 * prefix level. If and when the PSC passes through the final end of the
 * user-defined part of the body, it proceeds to the final operations at the
 * prefix level of the class "process", and the value of the procedure
 * "terminated" becomes true. (Although the process state "terminated" is not
 * strictly equivalent to the corresponding basic concept defined in chapter 7,
 * an implementation may treat a terminated process object as terminated in the
 * strict sense). A process object currently represented in the sequencing set
 * is said to be "suspended", unless it is represented by the event notice at
 * the lower end of the sequencing set. In the latter case it is active. A
 * suspended process is scheduled to become active at the system time indicated
 * by the attribute EVTIME of its event notice. This time value may be accessed
 * through the procedure "evtime". The procedure "nextev" references the process
 * object, if any, represented by the next event notice in the sequencing set.
 * 
 * @author SIMULA Standards Group
 * @author Øystein Myhre Andersen
 */
public class Process$ extends Link$ {
	public boolean isDetachable() {
		return (true);
	}

	public EVENT_NOTICE$ EVENT = null;
//	public boolean TERMINATED$ = false;
	private static int SEQU = 0; // Used by SIM_TRACE
	private int sequ = 0; // Used by SIM_TRACE

	// Constructor
	public Process$(RTObject$ staticLink) {
		super(staticLink);
		TRACE_BEGIN_DCL$("Process$");
		sequ = SEQU++;
		CODE$ = new ClassBody(CODE$, this,2) {
			public void STM() {
				TRACE_BEGIN_STM$("Process$",inner);
				//System.out.println("Process.STM: Just BEFORE Detach["+Process$.this.edObjectIdent()+']');
				detach();
				//System.out.println("Process.STM: Just AFTER Detach["+Process$.this.edObjectIdent()+']');
				if (inner != null)
					inner.STM();
				TRACE_END_STM$("Process$");
				//TERMINATED$ = true;

//				Process$.this.STATE$=OperationalState.terminated;
//				((Simulation$) SL$).passivate();
//				throw new RuntimeException("INTERNAL error:  Process passes through final end.");
				terminate();
			}
		};
	}
	
	private void terminate()
	{ // Simula Standard ch. 12.1 states:
	  // Although the process state "terminated" is not strictly equivalent to the
	  // corresponding basic concept defined in chapter 7, an implementation may treat
	  // a terminated process object as terminated in the strict sense.

	  //RT.BREAK("Process$.TERMINATE(1): "+this.edObjectAttributes());
	  ((Simulation$) SL$).passivate(true);
	  // Signal special action in RTObject$.EBLK
	  Process$.this.STATE$=OperationalState.terminatingProcess;
	  //RT.BREAK("Process$.TERMINATE(2): "+this.edObjectAttributes());
	}

	public Process$ STM() {
		return ((Process$) CODE$.EXEC$());
	}

	public Process$ START() {
		START(this);
		return (this);
	}

	public boolean idle() {
		return (EVENT == null);
	}

	public boolean terminated() {
//		return (TERMINATED$);
//		return (CUR$.STATE$==OperationalState.terminated);
		return (STATE$==OperationalState.terminated);
	}

	public double evtime() {
		if (idle())
			throw new RuntimeException("Process.Evtime:  The process is idle.");
		return (EVENT.EVTIME);
	}

	public Process$ nextev() {
		if (idle())
			return (null);
		EVENT_NOTICE$ suc = EVENT.suc();
		if (suc == null)
			return (null);
		return (suc.PROC);
	}

	public String toString() {
		return ("Process$"+sequ+"(" + this.edObjectIdent() + ") STATE$="+this.STATE$+", TERMINATED$=" + terminated());
	}

}
