/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import simula.compiler.declaration.BlockDeclaration;
import simula.compiler.utilities.Global;
import simula.compiler.utilities.Option;
import simula.compiler.utilities.Util;

/**
 * 
 * @author �ystein Myhre Andersen
 *
 */
public class JavaModule
{	JavaModule enclosingJavaModule;
	public BlockDeclaration blockDeclaration;
	Writer writer;
	Writer prevWriter;
	public String javaOutputFileName;
	
	public JavaModule(BlockDeclaration blockDeclaration)
	{ this.blockDeclaration=blockDeclaration; }

	public String getClassOutputFileName()
	{ return(Global.tempClassFileDir+Global.packetName+'/'+blockDeclaration.getJavaIdentifier()+".class"); }


	public void openJavaOutput() 
	{ enclosingJavaModule=Global.currentJavaModule;
	  Global.currentJavaModule=this;
	  javaOutputFileName = Global.tempJavaFileDir + blockDeclaration.getJavaIdentifier() + ".java";
	  try {
		File outputFile = new File(javaOutputFileName);
		if (Option.verbose) Util.TRACE("Output: " + outputFile.getCanonicalPath());
		writer = new FileWriter(outputFile);
		prevWriter=Util.setWriter(writer);
		Util.code("package "+Global.packetName+";"); 
		Util.code("// "+Global.simulaID+" Compiled at " + new Date());
		
		// Output Dependencies
		Util.code("import simula.runtime.*;");
	  } catch (IOException e) {
		throw new RuntimeException("Writing .java output failed",e);
	  }
	}
		 
	public void closeJavaOutput() 
	{ try {
	  writer.flush();
	  writer.close();
	  Util.setWriter(prevWriter);
	} catch (IOException e) {
	  throw new RuntimeException("Writing .java output failed",e);
	}
	Global.currentJavaModule=enclosingJavaModule;
	enclosingJavaModule=null;
  }

}
