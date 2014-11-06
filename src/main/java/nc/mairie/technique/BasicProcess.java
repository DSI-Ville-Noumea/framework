package nc.mairie.technique;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Insérez la description du type ici.
 * Date de création : (21/10/2002 10:27:37)
 * @author: Luc Bourdil
 */
public abstract class BasicProcess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6176966373224167150L;
	protected static String [] LBVide = {""};
	private int statut;
	private Hashtable<String, String> zonesDeSaisie;
	private Transaction transaction;
	private boolean autoReaffecteZones = true;

	//Les statuts de base
	public static final int STATUT_MEME_PROCESS = 999999 ;
	public static final int STATUT_PROCESS_APPELANT = 888888 ;
	public static final int STATUT_MESSAGE_INFO = 777777 ;		

	private String activite;
	private boolean veutRetour = false;
	private BasicProcess processAppelant = null;
	private String nomFichierImpression;

	public String onglet="ONGLET1";
/**
 * Commentaire relatif au constructeur ProcessGestion.
 */
public BasicProcess() {
	super();
	setTransaction(new Transaction((java.sql.Connection) null));
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (21/10/2002 10:35:38)
 * @param zone String
 * @param valeur String
 */
protected void addZone(String zone, String valeur) {
	if (valeur == null) valeur = new String();
	valeur = Services.stringForHTML(valeur);
	getZonesDeSaisie().put(zone, valeur.trim());
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/02 08:57:18)
 * @author Luc Bourdil
 * @throws Exception Exception 
 */
public void commitTransaction() throws Exception{
	if (getTransaction() != null)
		getTransaction().commitTransaction();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (24/10/2002 08:45:37)
 * @return boolean
 */
public boolean estAutoReaffecteZones() {
	return autoReaffecteZones;
}
/**
 * Teste si la connexion courante est ouverte.
 * @return boolean
*/
public boolean estConnexionOuverte(){
	try {
		return getTransaction() != null && getTransaction().isConnexionOuverte();
	} catch (Exception e) {
		return false;
	}
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (29/10/2002 09:52:21)
 * @return boolean
 */
public boolean estVeutRetour() {
	return veutRetour;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 14:03:48)
 * @return String
 */
public String etatActivite() {
	return getActivite();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 08:47:55)
 * @return int
 */
public int etatStatut() {
	return getStatut();
}
/**
 * Ferme la connexion courante si elle est ouverte
 * @return boolean
 */
public boolean fermerConnexion() {
	try {
		if (getTransaction() != null)
			getTransaction().fermerConnexion();
		return true;
	} catch (Exception e) {
		return false;
	}
}
/**
 * Retourne une checkBoxHTML
 * 
 * @param name
 * @param value
 * @return String
 */

public String forCheckBoxHTML(String name, String value) {

	StringBuffer result = new StringBuffer();

	result.append(name == null ? "" : " name=\""+name+"\"");
	result.append(value == null ? "" : " value=\""+value+"\"" + (value.equals(getCHECKED_ON()) ? " checked" : ""));

	return result.toString();
	
}
/**
 * Retourne une suite de tag HTML OPTION 
 * 
 * @param liste
 * @param select
 * @return String
 */
public String forComboHTML(String [] liste, String select) {
	return forComboHTML(liste, null, select);
}
/**
 *  * Retourne une suite de tag HTML OPTION 
 * 
 * @param liste
 * @param colors
 * @param select
 * @return String
 */
public String forComboHTML(String [] liste, String [] colors, String select) {
	return forComboHTML(liste, colors, null, select);
}

/**
 * Retourne une suite de tag HTML OPTION
 * 
 * @param liste
 * @param colors
 * @param fonds
 * @param select
 * @return String
 */
public String forComboHTML(String [] liste, String [] colors, String [] fonds, String select) {
	return forComboHTML(liste, colors, fonds, null, select);
}

/**
 * Retourne une suite de tag HTML OPTION
 * 
 * @param liste
 * @param colors
 * @param fonds
 * @param titles
 * @param select
 * @return String
 */

public String forComboHTML(String [] liste, String [] colors, String [] fonds, String [] titles, String select) {

	//<OPTION value="2" style="background-color: red; color: green;" title="deux">2</OPTION>
	
	StringBuffer result = new StringBuffer();
	
	if (liste.length == 0) return "";

	String aColor;
	String aFond;
	String aStyle;
	String aTitle;
	
	for (int i = 0; i < liste.length; i++){
		result.append("<OPTION ");
		if (String.valueOf(i).equals(select)) {
			result.append("selected ");
		}
		result.append("value=\"");
		result.append(i);
		result.append("\"");
		
		//Affectation de color
		try {
			aColor = colors[i];
		} catch (Exception exColor) {
			aColor=null;
		}
			
		//Affectation de fond
		try {
			aFond = fonds[i];
		} catch (Exception exFond) {
			aFond=null;
		}
		
		//Affectation du style
		aStyle = (aColor == null ? "" : "color: "+aColor+";")+
				 (aFond == null ? "" : "background-color: "+aFond+";");
		if (aStyle.length() > 0) {
			aStyle = " style=\"" + aStyle+"\"";
		}
		result.append(aStyle);	
		
		//Affectation du title
		try {
			aTitle = titles[i];
		} catch (Exception e) {
			aTitle = null;
		}
		
		if (aTitle != null) {
			result.append(" title=\""+aTitle+"\"");
		}
		
		result.append(">");
		result.append(liste[i]);
		result.append("</OPTION>\n");
		
	}
	
	return result.toString();
	
}

/**
 * Retourne un radio bouton
 * 
 * @param NOM_RG
 * @param NOM_RB
 * @param VAL_RG
 * @return String
 */

public String forRadioHTML(String NOM_RG, String NOM_RB, String VAL_RG) {

	StringBuffer result = new StringBuffer();

	result.append(NOM_RG == null ? "" : " name=\""+NOM_RG+"\"");
	result.append(NOM_RB == null ? "" : " value=\""+NOM_RB+"\"" + (NOM_RB.equals(VAL_RG) ? " checked" : ""));

	return result.toString();
	
}
/**
 * REtourne un radio bouton
 * 
 * @param NOM_RG
 * @param NOM_RB
 * @return String
 */
public String forRadioHTML(String NOM_RG, String NOM_RB) {

	return forRadioHTML(NOM_RG, NOM_RB, getZone(NOM_RG));
	
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @return boolean
 * @throws Exception 
 */
public boolean gererRecuperationStatut(javax.servlet.http.HttpServletRequest request) throws Exception {

	//Précontrole:
	if (!recupererPreControles(request)) return false;
	
	//Si clic sur un onglet : OK
	if (recupererOnglet(request)) return true;
	else getTransaction().traiterErreur();

	//On récupère le statut
	return recupererStatut(request);
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 14:03:48)
 * @return String
 */
private String getActivite() {
	return activite;
}
/**
	Retourne l'état d'un check bouton dans la JSP
 * @return String
 */
public String getCHECKED_OFF() {
	return "CHECKED_OFF";
}
/**
	Retourne l'état d'un check bouton dans la JSP
 * @return String
 */
public String getCHECKED_ON() {
	return "CHECKED_ON";
}
/**
	Retourne le nom de la JSP du process
	Zone à utiliser dans un champ caché dans chaque formulaire de la JSP.
 * @return String
 */
public abstract String getJSP();
/* 	Méthode utilisée dans les JSP qui possèdent une List Box
	et qui est implémentée dynamiquement.
*/
public String getLB_INDICE(int indice){
	return Integer.toString(indice);
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (20/11/2002 12:04:43)
 * @return String
 */
public String getNomFichierImpression() {
	return nomFichierImpression;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (29/10/2002 09:53:28)
 * @return nc.mairie.technique.ProcessGestion
 */
public BasicProcess getProcessAppelant() {
	return processAppelant;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 08:47:55)
 * @return int
 */
private int getStatut() {
	return statut;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:41:57)
 * @return nc.mairie.technique.Transaction
 */
public nc.mairie.technique.Transaction getTransaction() {
	return transaction;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (21/10/2002 10:34:37)
 * @return String
 * @param zone String
 */
protected String getZone(String zone) {
	String s = (String)getZonesDeSaisie().get(zone);
	if (s==null) s = "";
	return s;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (21/10/2002 10:28:10)
 * @return Hashtable
 */
private Hashtable<String, String> getZonesDeSaisie() {
	if (zonesDeSaisie == null)
		zonesDeSaisie = new Hashtable<String, String>();
	return zonesDeSaisie;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (29/01/2004 14:14:37)
 * @return boolean
 */
public String [] initialiseLazyLB() {
	return LBVide;
}
/**
	Initialisation des zones à afficher dans le JSP
 * @param request request
 * @throws Exception exception
 */
public abstract void initialiseZones(javax.servlet.http.HttpServletRequest request) throws Exception;
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/03/2004 09:47:06)
 * @return nc.mairie.technique.MairiePDF
 *
 * Cette méthode doit être redéfinie dans le but de générer un PDF.
 */
public MairiePDF prepareImpressionPDF() {
	return null;
}
/**
	Méthode qui : 
	- Réaffecte les zones de saisie dans la fenêtre
 * @param request request
 */
public void reaffecteZones(javax.servlet.http.HttpServletRequest request) {

		//Je mets par défaut toutes les Check box à false;
		Enumeration<String> enumZonesDeSaisies = getZonesDeSaisie().keys();
		while (enumZonesDeSaisies.hasMoreElements()) {
			String cle = (String) enumZonesDeSaisies.nextElement();
			if (cle.startsWith("NOM_CK")) {
				addZone(cle,"CHECKED_OFF");
			}
		}		

		Enumeration<?> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String cle = (String) e.nextElement();
			//Si commence par nom alors je récupère
			if (cle.startsWith("NOM")) {
				String []valeur = request.getParameterValues(cle);
				if (valeur != null) {
					//Si un seul élément et si <> null
					if (valeur.length == 1) {
						//Si c'est une ckeckBox
						if (cle.startsWith("NOM_CK")) {
							addZone(cle,"CHECKED_ON");
						//Si c'est une liste
						} else 	if (cle.startsWith("NOM_LB")) {
							addZone(cle+"_SELECT",valeur[0]);
						} else {
							addZone(cle,valeur[0]);
						}
					//Plus d'un element
					} else {
						if (cle.startsWith("NOM_LB")) {
							for (int i = 0; i < valeur.length; i++) {
								addZone(cle+"_SELECT"+i,valeur[i]);
							}
						}
					}
				}
			}
		}
		
	
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @return boolean
 * @throws Exception Exception  
 */
public boolean recupererOnglet(javax.servlet.http.HttpServletRequest request) throws Exception{
	String newOnglet = request.getParameter("NOM_PB_ONGLET");
	if (newOnglet != null) {
		onglet=newOnglet;
		return true;
	}
	return false;
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @return boolean
 * @throws Exception Exception 
 */
public boolean recupererPreControles(javax.servlet.http.HttpServletRequest request) throws Exception {
	//Si pas de TAG JSP
	if (request.getParameter("JSP")==null) {
		setStatut(STATUT_MEME_PROCESS,false,"Erreur : TAG JSP non trouvé");
		return false;
	}

	//Si on arrive de cette JSP la JSP alors on traite le get
	if (!request.getParameter("JSP").equals(getJSP())) {
		setStatut(STATUT_MEME_PROCESS,false,"Erreur : Le TAG JSP ne correspond pas au process en cours");
		return false;
	}
	return true;
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @return boolean
 * @throws Exception Exception 
 */
public abstract boolean recupererStatut(javax.servlet.http.HttpServletRequest request) throws Exception;
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/02 08:57:18)
 * @throws Exception Exception
 */
public void rollbackTransaction() throws Exception{
	if (getTransaction() != null)
		getTransaction().rollbackTransaction();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 14:03:48)
 * @param newActivite String
 */
public void setActivite(String newActivite) {
	activite = newActivite;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (24/10/2002 08:45:37)
 * @param newAutoReaffecteZones boolean
 */
public void setAutoReaffecteZones(boolean newAutoReaffecteZones) {
	autoReaffecteZones = newAutoReaffecteZones;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (20/11/2002 12:04:43)
 * @param newNomFichierImpression String
 */
public void setNomFichierImpression(String newNomFichierImpression) {
	nomFichierImpression = newNomFichierImpression;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (29/10/2002 09:53:28)
 * @param newProcessAppelant nc.mairie.technique.ProcessGestion
 */
public void setProcessAppelant(BasicProcess newProcessAppelant) {
	processAppelant = newProcessAppelant;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 08:47:55)
 * @param newStatut int
 */
public void setStatut(int newStatut) {
	setStatut(newStatut,false);
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (31/10/99 11:32:39)
 * @param newStatut int
 * @param veutRetour boolean
 */
public void setStatut(int newStatut, boolean veutRetour) {
	statut = newStatut;
	if (newStatut == STATUT_MEME_PROCESS || newStatut == STATUT_PROCESS_APPELANT)
		this.veutRetour = false;
	else
		this.veutRetour = veutRetour;
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (31/10/99 11:32:39)
 * @param newStatut newStatut 
 * @param newRetour newRetour 
 * @param message message 
 */
public void setStatut(int newStatut, boolean newRetour, String message) {

	//Statut normal
	setStatut(newStatut, newRetour);
	
	//Si message null
	if (message == null) {
		message = "";
	}

	//Alimentation de la zone erreur
	if (getTransaction() == null)
		try {
			setTransaction(new Transaction((UserAppli)null));
		} catch (Exception e) {
			setTransaction(null);
		}
		
	getTransaction().declarerErreur(message);

}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:41:57)
 * @param newTransaction nc.mairie.technique.Transaction
 */
public void setTransaction(nc.mairie.technique.Transaction newTransaction) {
	transaction = newTransaction;
}
/**
	Méthode qui teste si un paramètre se trouve dans le formulaire
 * @param request request 
 * @param param param 
 * @return boolean
*/
public boolean testerParametre(javax.servlet.http.HttpServletRequest request, String param) {
	return (request.getParameter(param) != null || request.getParameter(param + ".x") != null);
}
}
