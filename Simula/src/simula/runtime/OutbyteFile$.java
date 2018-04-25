/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.runtime;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * The class "outbytefile".
 * 
 * <pre>
 *  bytefile class outbytefile;
 *     begin
 *           Boolean procedure open;
 *           Boolean procedure close;
 *           procedure outbyte(x); short integer x;
 *           procedure outtext(t); text t;
 *           Boolean procedure checkpoint;
 * 
 *  end outbytefile;
 * </pre>
 * 
 * An object of the class "outbytefile" is used to represent a sequential output
 * file of bytes.
 * 
 * @author SIMULA Standards Group
 * @author �ystein Myhre Andersen
 *
 */
public class OutbyteFile$ extends ByteFile$ {
	OutputStream outputStream;

	public boolean open() {
		if (OPEN$)
			return (false);
		if (FILENAME$.edText().equalsIgnoreCase("sysout"))
			outputStream = System.out;
		else {
			try {
				outputStream = new FileOutputStream(FILENAME$.edText());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return (false);
			}
		}
		OPEN$ = true;
		BYTESIZE$ = ((short) (8));
		return (true);
	}

	public boolean close() {
		if (OPEN$) {
			OPEN$ = false;
			return (true);
		}
		return (false);
	}

	public void outbyte(int b) {
		if (!OPEN$)
			throw new RuntimeException("file closed");
		if (b < 0 || b >= (2 ^ BYTESIZE$))
			throw new RuntimeException("Illegal byte value");
		try {
			outputStream.write(b);
		} catch (IOException e) {
			throw new RuntimeException("Outbyte failed", e);
		}
	}

	public void outtext(TXT$ t) {
		t.setpos(1);
		while (t.more()) {
			outbyte((int) t.getchar());
		}
	}

	public boolean checkpoint() {
		return (false);
	}

	// Constructor
    public OutbyteFile$(RTObject$ staticLink,TXT$ FILENAME) {
      super(staticLink,FILENAME);
      // Create Class Body
      CODE$=new ClassBody(CODE$,this) {
         public void STM() {
            if(inner!=null) inner.STM();
      }};
    }
    // Class Statements
    public OutbyteFile$ STM() { return((OutbyteFile$)CODE$.EXEC$()); }
    public OutbyteFile$ START() { START(this); return(this); }
}