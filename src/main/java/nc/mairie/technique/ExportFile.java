/*
 * Created on 14 févr. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nc.mairie.technique;

/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportFile {

	private String extension;
	private String contenu="";
	
	/**
	 * 
	 */
	public ExportFile() {
		this(null);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	public ExportFile(String pExtension) {
		extension=pExtension;
	}
	
	public void ajouteLigne(String pLigne) {
		contenu+=pLigne+"\n";
	}
	
	public void ajouteLigne(String [] pLigne) {
		
		boolean excel = extension.toUpperCase().equals("CSV") ||
						extension.toUpperCase().equals("XLS");
		
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < pLigne.length; i++) {
			b.append(pLigne[i]);
			if (excel) b.append(";");
		}
		
		ajouteLigne(b.toString());
	}
	
	public String getScriptOuverture() throws Exception{
		StringBuffer scriptOuv = new StringBuffer("<script language=\"javascript\">");
		scriptOuv.append("window.parent.location=\"Telecharger.jsp\";");
		scriptOuv.append("</script>");
		return scriptOuv.toString();
	}
	
	public String getContenu() {
		return contenu;
	}
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}
	public String getFileName() {
		return "export"+(extension == null ? "" : "."+extension);
	}
	
}
