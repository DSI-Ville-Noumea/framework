
package nc.mairie.technique;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * @deprecated since release 1.9 replaced by {@link StarjetGenerationVFS}
 */
@Deprecated public class StarjetGeneration {
	
	private String schema;
	private String mode;
	private String projet;
	private String UNCServeur;
	private String partage;
	private String ip;
	
	
	private String nomScript;
	private String nomData;
	private String nomPDF;
	private String nomPrint;
	private String nomService;
	
	private File fileData; 
	
	
	private String getParamScript() {
		String res = "script="+UNCServeur+"\\"+mode+"\\scripts\\"+projet+"\\"+nomScript;
		return res;
	}

	private String getParamData() throws Exception{
		String res = "data="+UNCServeur+"\\"+mode+"\\data\\"+projet+"\\"+getFileData().getName();
		return res;
	}
	
	private String getParamPdf() {
		String res = "pdf="+UNCServeur+"\\"+mode+"\\archives\\"+projet+"\\"+nomPDF;
		return res;
	}

	private String getParamPrint() {
		String res = "print="+nomPrint;
		return res;
	}
	
	public File getFileData() throws Exception {
		
		if (fileData == null){
			fileData = new File("\\\\starjet\\"+partage+"\\"+mode+"\\DATA\\"+projet+"\\"+ip+"_"+nomData+".dat");
			//fileData = File.createTempFile(nomData, ".dat", new File("\\\\starjet\\"+partage+"\\"+mode+"\\DATA\\"+projet));
		}
		return fileData;
		
	}
	
	public String getScriptOuverture() throws Exception{
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"javascript\">");
		scriptOuvPDF.append("window.open(\"").append(getStarjetScriptURL()).append("\",'','directories=no,location=no,menubar=no,personalbar=no,status=no,toolbar=no,titlebar=no,dependent=no,resizable=yes');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}
	
	
	private String getStarjetScriptURL() throws Exception{
		
		StringBuffer sb = new StringBuffer();
		sb.append("http://starjet/").append(mode).append("/cgi/displayPDF.asp");
		sb.append("?").append(getParamScript());
		sb.append("&").append(getParamData());
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
	 * @param pSchema
	 * @param pMode
	 * @param pProjet
	 * @param pScript
	 * @param pData
	 * @param pPDF
	 * @param pPrint
	 * @param pService
	 * @throws Exception
	 * 
	 * Exemples d'appels
	 * -----------------
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "MAIRIE", "DVLP", "ELECTIONS", "elections_bureau.sp", "elections");
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "MAIRIE", "DVLP", "ELECTIONS", "elections_bureau.sp", "elections_bureau", "toto.pdf", null );
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "MAIRIE", "DVLP", "ELECTIONS", "elections_bureau.sp", "elections_bureau", "toto.pdf", "\\\\BOY\\COP047"  );
	 * StarjetGeneration g = new StarjetGeneration(getTransaction(), "MAIRIE", "DVLP", "ELECTIONS", "elections_bureau.sp", "elections_bureau", "toto.pdf", null, "LUC"  );

	 */
	
	public StarjetGeneration(	Transaction t, 
							String pSchema, 
							String pMode, 
							String pProjet, 
							String pScript, 
							String pData, 
							String pPDF, 
							String pPrint, 
							String pService) throws Exception{
		
		//Affectation des var
		schema=pSchema;
		mode = pMode;
		projet = pProjet;
		nomScript = pScript;
		nomData = pData;
		nomPDF = pPDF;
		nomPrint = pPrint;
		nomService = pService;
		ip=t.getConnection().getMetaData().getUserName();
		
		//recherche des infos
		StringBuffer query = new StringBuffer("SELECT DISTINCT ");
		query.append(" UNCSERVEUR");
		query.append(",ADRESSE_IP");
		query.append(",PORT_FTP");
		query.append(",MODE");
		query.append(",LIBELLE");
		query.append(",NOMAPPLI");
		query.append(",NOMSERVICE");
		query.append(",LIBSERVICE");
		query.append(",NOMIMPRIMANTE");
		query.append(",PARTAGE");
		query.append(" FROM ").append(schema).append(".v_starjet as vsj ");
		query.append(" WHERE ");
		query.append(" mode='").append(mode).append("'");
		query.append(" AND nomappli='").append(projet).append("'");
		if (nomService != null) query.append(" AND nomservice='").append(nomService).append("'");
	
		
		
		Statement st = t.getConnection().createStatement();
		ResultSet rs = st.executeQuery(query.toString());
				
		//on lit
		if (!rs.next()) {
			rs.close();
			st.close();
			String message = "Erreur : Impossible de trouver les donn�es dans v_starjet avec "+mode+", "+projet+(nomService!= null ? ", "+nomService : "")+".";
			t.declarerErreur(message);
			throw new Exception(message);
			
		}
		
		UNCServeur = rs.getString("UNCSERVEUR");
		partage = rs.getString("PARTAGE");
		if (nomService != null && nomPrint == null) nomPrint = rs.getString("NOMIMPRIMANTE");
		
		rs.close();
		st.close();
	}
	
	public StarjetGeneration(Transaction t, String pSchema, String pMode, String pProjet, String pScript, String pData, String pPDF, String pPrint) throws Exception {
		this(t, pSchema, pMode, pProjet, pScript, pData, pPDF, pPrint, null);
	}
	public StarjetGeneration(Transaction t, String pSchema, String pMode, String pProjet, String pScript, String pData ) throws Exception {
		this(t, pSchema, pMode, pProjet, pScript, pData, null, null);
	}
	
	
}