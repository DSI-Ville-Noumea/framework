
package nc.mairie.technique;

import nc.mairie.servlets.Frontale;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StarjetGenerationVFS {
	
	private String mode = (String)Frontale.getMesParametres().get("STARJET_MODE");
	private String projet = (String)Frontale.getMesParametres().get("STARJET_PROJET");
	private String UNCServeur = (String)Frontale.getMesParametres().get("STARJET_LOCAL_DIR");
	private String starjetServer = (String)Frontale.getMesParametres().get("STARJET_SERVER");
	
	
	private String nomScript;
	private String nomData;
	private String nomPDF;
	private String nomPrint;
	
	private FileObject fileObjectData;
	private String uuidString;
	
	private FileSystemManager fileManager;
	
	
	public String getStarjetServer() {
		return starjetServer;
	}
	
	public String getUuidString() {
		return uuidString;
	}

	public void setUuidString(String uuidString) {
		this.uuidString = uuidString;
	}

	private String getParamPrint() {
		String res = "print=" + nomPrint;
		return res;
	}
	
	public FileObject getFileData() throws Exception {
		if (fileObjectData == null){
			fileObjectData = fileManager.resolveFile(getFileObjectURI("DATA"));
		}
		return fileObjectData;
		
	}

	
	private String getParamPdf() {
		String res = "pdf=" + UNCServeur + "\\" + mode + "\\PDF\\" + projet + "\\" + nomPDF;
		return res;
	}
	
	private String getFileObjectURI(String type){
		String out = null;
		if(type==null){
			return null;
		}
		out = "file:" + starjetServer + "/" + mode + "/" + type + "/" + projet + "/" + nomData;
		return out;
	}
	
	public String getScriptOuverture() throws Exception{
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"javascript\">");
		scriptOuvPDF.append("window.open(\"").append(getStarjetScriptURL()).append("\",'','directories=no,location=no,menubar=no,personalbar=no,status=no,toolbar=no,titlebar=no,dependent=no,resizable=yes');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}
	
	// L'ancienne méthode getStarjetScriptURL
	//
//	private String getStarjetScriptURL() throws Exception{
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append((String)Frontale.getMesParametres().get("STARJET_DISPLAYPDF_URL"));
//		sb.append("?").append(getParamScript());
//		sb.append("&").append(getParamData());
//		if (nomPDF != null) sb.append("&").append(getParamPdf());
//		if (nomPrint != null) sb.append("&").append(getParamPrint());
//		
//		//remplacement des \ par \\
//		for (int i = 0; i < sb.length(); i++) {
//			if (sb.charAt(i) =='\\') {
//				sb.insert(i,'\\');
//				i++;
//			}
//			
//		}
//		System.out.println(sb.toString());
//		return sb.toString();
//	}
	
	
	private String getStarjetScriptURL() throws Exception{
		
		StringBuffer sb = new StringBuffer();
		sb.append((String)Frontale.getMesParametres().get("STARJET_DISPLAYPDF_URL"));
		sb.append("?").append("env=").append(mode);
		sb.append("&").append("proj=").append(projet);
		sb.append("&").append("script=").append(nomScript);
		sb.append("&").append("data=").append(nomData);
		if (nomPDF != null) sb.append("&").append(getParamPdf());
		if (nomPrint != null) sb.append("&").append(getParamPrint());
		
		//remplacement des \ par \\
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) =='\\') {
				sb.insert(i,'\\');
				i++;
			}
			
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param t
	 * @param pScript
	 * @param pData
	 * @param pPDF
	 * @param pPrint
	 * @throws Exception
	 * 
	 * Exemples d'appels
	 * -----------------
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "elections_bureau.sp", "elections");
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "elections_bureau.sp", "elections_bureau", "toto.pdf");
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "elections_bureau.sp", "elections_bureau", "toto.pdf", "\\\\BOY\\COP047"  );

	 */
	
	public StarjetGenerationVFS(	Transaction t, 
							String pScript, 
							String pData, 
							String pPDF, 
							String pPrint) throws Exception{
		
		//Affectation des var
		nomScript = pScript;
		nomData = pData;
		nomPDF = pPDF;
		nomPrint = pPrint;
		setFileManager(VFS.getManager());
		
	}

	public StarjetGenerationVFS(Transaction t, String pScript, String pData, String pPDF) throws Exception {
		this(t, pScript, pData, pPDF, null);
	}
	public StarjetGenerationVFS(Transaction t, String pScript, String pData ) throws Exception {
		this(t, pScript, pData, null);
	}
	
	
	public FileSystemManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileSystemManager fileManager) {
		this.fileManager = fileManager;
	}
	
	
}
