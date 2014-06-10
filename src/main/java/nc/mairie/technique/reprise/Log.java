/*
 * Created on 26 janv. 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nc.mairie.technique.reprise;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;



/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Log {
	
	private File logFile;
	private String className = null;
	private String pathName = null; 
	
	public static void main (String [] args) {
		Log l = new Log();
		System.out.println(getRoot(l));
	}
	
	public Log() {
		// TODO Auto-generated constructor stub
		this("Log",".\\");
	}
	
	public Log(String path, String name) {
		super();
		// TODO Auto-generated constructor stub
		className = name;
		pathName = path;
	}
	
	public Log(Object o) {
		this(
				getRoot(o),
				o.getClass().getName().substring(o.getClass().getName().lastIndexOf(".")+1)); 
		
	}
	
	public static String getRoot(Object obj) {
		//recup du root
		Class<? extends Object> cl = obj.getClass();
		String root = cl.getProtectionDomain().getCodeSource().getLocation().getFile();
		if (root.toUpperCase().endsWith("JAR") || root.toUpperCase().endsWith("CLASS") ) {
			root=root.substring(0, root.lastIndexOf('/') +1);
		}
		return root;
	}
	
	public void log(Exception e){
		e.printStackTrace();

		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			log(sw.toString());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	
	public void log(String message){
		System.out.println(new java.util.Date()+" "+message);
		try {
			FileWriter aWriter = new FileWriter(getLogFile().getCanonicalPath(),true);
		    aWriter.write(new java.util.Date()+" "+message + " " + System.getProperty("line.separator"));
			aWriter.flush();
		    aWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public File getLogFile() {
		if (logFile == null) {
			
//			init du logFile
			logFile = new File(pathName+className+".log");
			try{
				if (! logFile.exists()) logFile.createNewFile();
			} catch (Exception e) {
				System.out.println("Impossible d'initialiser le fichier de log : "+e.getMessage());
				System.exit(1);
			}
		}
		return logFile;
	}}
