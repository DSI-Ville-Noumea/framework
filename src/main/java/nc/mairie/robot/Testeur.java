package nc.mairie.robot;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;


import nc.mairie.technique.*;
/**
 * Process ZZZTESTEUR
 * @author : Générateur de process
*/
public abstract class Testeur extends nc.mairie.technique.BasicProcess {
	private java.lang.String[] LB_PROCESS;
	private boolean instancie = false;
/**
 * Constructeur du process ZZZTESTEUR.

 * @author : Générateur de process
 */
public Testeur() {
	super();
}
/**
 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
 * NOM_LB_PROCESS_SELECT

 * @author : Générateur de process
 */
public abstract java.lang.String [] definirListeProcess();
/**
 * Retourne le nom de la JSP du process
 * Zone à utiliser dans un champ cach� dans chaque formulaire de la JSP.

 * @author : Générateur de process
 */
public String getJSP() {
	return "ZZZTESTEUR.jsp";
}
/**
 * Getter de la liste avec un lazy initialize :
 * LB_PROCESS
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
private String [] getLB_PROCESS() {
	if (LB_PROCESS == null)
		LB_PROCESS = initialiseLazyLB();
	return LB_PROCESS;
}
/**
 * Retourne le nom de la zone pour la JSP :
 * NOM_LB_PROCESS
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
public java.lang.String getNOM_LB_PROCESS() {
	return "NOM_LB_PROCESS";
}
/**
 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
 * NOM_LB_PROCESS_SELECT
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
public java.lang.String getNOM_LB_PROCESS_SELECT() {
	return "NOM_LB_PROCESS_SELECT";
}
/**
 * Retourne le nom d'un bouton pour la JSP :
 * PB_OECOMMUNEFSELECTION
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public java.lang.String getNOM_PB_PROCEDER() {
	return "NOM_PB_PROCEDER";
}
/**
 * Retourne le nom d'un bouton pour la JSP :
 * PB_ANNULER
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public java.lang.String getNOM_PB_ANNULER() {
	return "NOM_PB_ANNULER";
}
/**
 * Retourne le nom d'un bouton pour la JSP :
 * PB_INSTANCIE
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public java.lang.String getNOM_PB_INSTANCIE() {
	return "NOM_PB_INSTANCIE";
}
/**
 * Retourne le nom d'un bouton pour la JSP :
 * PB_RAZ_ACTIVITE
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public java.lang.String getNOM_PB_RAZ_ACTIVITE() {
	return "NOM_PB_RAZ_ACTIVITE";
}
/**
 * Retourne pour la JSP le nom de la zone statique :
 * ST_RESULTAT
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public java.lang.String getNOM_ST_RESULTAT() {
	return "NOM_ST_RESULTAT";
}
/**
 * M�thode � personnaliser
 * Retourne la valeur � afficher pour la zone de la JSP :
 * LB_PROCESS
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
public java.lang.String []  getVAL_LB_PROCESS() {
	return getLB_PROCESS();
}
/**
 * M�thode � personnaliser
 * Retourne l'indice � s�lectionner pour la zone de la JSP :
 * LB_PROCESS
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
public java.lang.String getVAL_LB_PROCESS_SELECT() {
	return getZone(getNOM_LB_PROCESS_SELECT());
}
/**
 * Retourne la valeur � afficher par la JSP  pour la zone :
 * ST_RESULTAT
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public java.lang.String getVAL_ST_RESULTAT() {
	return getZone(getNOM_ST_RESULTAT());
}
/**
 * Retourne le nom d'une zone de saisie pour la JSP :
 * EF_CLASSE
 * Date de cr�ation : (17/01/07 14:03:23)
 * @author : Générateur de process
 */
public java.lang.String getNOM_EF_CLASSE() {
	return "NOM_EF_CLASSE";
}
/**
 * Retourne la valeur � afficher par la JSP pour la zone de saisie  :
 * EF_CANDIDAT
 * Date de cr�ation : (17/01/07 14:03:23)
 * @author : Générateur de process
 */
public java.lang.String getVAL_EF_CLASSE() {
	return getZone(getNOM_EF_CLASSE());
}
/**
 * Retourne le nom d'une zone de saisie pour la JSP :
 * EF_ID
 * Date de cr�ation : (17/01/07 14:03:23)
 * @author : Générateur de process
 */
public java.lang.String getNOM_EF_ID() {
	return "NOM_EF_ID";
}
/**
 * Retourne la valeur � afficher par la JSP pour la zone de saisie  :
 * EF_ID
 * Date de cr�ation : (17/01/07 14:03:23)
 * @author : Générateur de process
 */
public java.lang.String getVAL_EF_ID() {
	return getZone(getNOM_EF_ID());
}

/**
 * Retourne le nom d'une zone de saisie pour la JSP :
 * EF_ACTIVITE
 * Date de cr�ation : (17/01/07 14:03:23)
 * @author : Générateur de process
 */
public java.lang.String getNOM_EF_ACTIVITE() {
	return "NOM_EF_ACTIVITE";
}
/**
 * Retourne la valeur � afficher par la JSP pour la zone de saisie  :
 * EF_ACTIVITE
 * Date de cr�ation : (17/01/07 14:03:23)
 * @author : Générateur de process
 */
public java.lang.String getVAL_EF_ACTIVITE() {
	return getZone(getNOM_EF_ACTIVITE());
}

/**
 * Initialisation des zones � afficher dans la JSP
 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
 * Utilisation de la m�thode addZone(getNOMxxx, String);
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public void resultatDefault(HttpServletRequestWrapper request) throws Exception{
	
	String res = getVAL_ST_RESULTAT();
	res+= listerVarActivites();	
	
	addZone(getNOM_ST_RESULTAT(), res );
}
/**
 * Initialisation des zones � afficher dans la JSP
 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
 * Utilisation de la m�thode addZone(getNOMxxx, String);
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public void initialiseZones(HttpServletRequestWrapper request) throws Exception{

	if (!instancie) addZone(getNOM_ST_RESULTAT(),"");
	else instancie = false;
	
	setLB_PROCESS(listeProcess());

	if (etatStatut() != STATUT_MEME_PROCESS && etatStatut()!=STATUT_MESSAGE_INFO) {
		String className = listeProcess()[etatStatut()].substring(listeProcess()[etatStatut()].lastIndexOf(".")+1);
		Class [] classes = {HttpServletRequest.class};
		java.lang.reflect.Method methodeInitialise = null;
		boolean methodeTrouvee = false;
		try {
			methodeInitialise = getClass().getMethod("resultat"+className, classes);
			methodeTrouvee = true;
		} catch (Exception e) {
				methodeTrouvee = false;
		}

		if (methodeTrouvee) {
			Object [] parametre = {request};
			methodeInitialise.invoke(this,parametre);
		} 
	}

	resultatDefault(request);
	
/*	switch (etatStatut()) {
		case 0 : {initialiseTESTAPPEL(request);break;}
		case 1 : {initialiseOECOMMUNEFSELECTION(request);break;}
		case 2 : {initialiseOECOMMUNEESELECTION(request);break;}
		case 3 : {initialiseOeVOIESelection(request);break;}
		case 4 : {initialiseOEAGENTSELECTION(request);break;}
		case 5 : {initialiseOEAGENTCONTACTGESTION(request);break;}
		case 6 : {initialiseOEAGENTCONTACTVISUALISATION(request);break;}
		case 7 : {initialiseOEAGENTINAPTITUDEGESTION(request);break;}
		case 8 : {initialiseOEAGENTINAPTITUDEVISUALISATION(request);break;}
		case 9 : {initialiseOEAGENTVISITESGESTION(request);break;}
		case 10: {initialiseOEAGENTVISITESVISUALISATION(request);break;}
		case 11: {initialiseOECODEPOSTALSELECTION(request);break;}
		case 12: {initialiseOEENFANTGESTION(request);break;}
		case 13: {initialiseOEAGENTCREATIONSUIVI(request);break;}
		case 14: {initialiseOEENFANTRECHERCHE(request);break;}
		case 15: {initialiseOEENFANTVISUALISATION(request);break;}
		case 16: {initialiseOEAGENTMODIFICATION(request);break;}
		case 17: {initialiseOEAGENTVISUALISATION(request);break;}
		case 18: {initialiseOEAGENTCREATION(request);break;}
		case 19: {initialiseOEADMINISTRATIONGESTION(request);break;}
		case 20: {initialiseOEAGENTADMINISTRATIONGESTION(request);break;}
	}
*/
}
/**
 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
 * NOM_LB_PROCESS_SELECT
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
public java.lang.String [] listeProcess() {
	return definirListeProcess();
}
/**
 * 
 *
 */
public boolean performPB_RAZ_ACTIVITE(javax.servlet.http.HttpServletRequest request){
	Hashtable h = getTransaction().getVariablesActivite();
	Enumeration enume = h.keys();
	String key;
	while (enume.hasMoreElements()){
		key = (String)enume.nextElement();
		h.remove(key);
	}
	return true;
}
/**
 * 
 *
 */
public boolean performPB_ANNULER(javax.servlet.http.HttpServletRequest request){
	setStatut(STATUT_PROCESS_APPELANT);
	return true;
}
/**
 * 
 *
 */
public boolean performPB_INSTANCIE(javax.servlet.http.HttpServletRequest request) throws Exception{
	String theClasse = getZone(getNOM_EF_CLASSE());
	String theID = getZone(getNOM_EF_ID());
	String theActivite = getZone(getNOM_EF_ACTIVITE());
	
	boolean isListe = theID == null || theID.length() == 0;
	
	if (theClasse == null || theClasse.length() == 0) {
		getTransaction().declarerErreur("Il faut saisir le nom de la classe");
		return false;
	}
	
	Class c;
	try {
		c=Class.forName(theClasse,false, getClass().getClassLoader());
	} catch (Exception e) {
		getTransaction().declarerErreur("Impossible de trouver la classe "+theClasse);
		return false;
	}
	
	Class [] classLister = { Transaction.class};
	Class [] classChercher = { Transaction.class, String.class};
	
	Class [] classes = isListe ? classLister : classChercher;
	java.lang.reflect.Method methode = null;

	//Recherche de la methode
	String nomMethode = (isListe ? "lister" : "chercher")+c.getName().substring(c.getName().lastIndexOf(".")+1);
	try {
		methode = c.getMethod(nomMethode, classes);
	} catch (Exception e) {
		getTransaction().declarerErreur("Impossible de trouver la methode "+nomMethode+" pouer la classe "+c.getName());
		return false;
	}
	
	//Appel de la m�thode
	Object [] parametreLister = {getTransaction()};
	Object [] parametreChercher = {getTransaction(), getVAL_EF_ID()};
	Object [] parametre = isListe ? parametreLister : parametreChercher;
	Object res = null;
	try {
		res = methode.invoke(c,parametre);
	} catch (Exception e){
		getTransaction().declarerErreur("Exception en invoquant la m�thode "+nomMethode);
		return false;
	}

	addZone(getNOM_ST_RESULTAT(), Services.contenuMetierToString(res));

	//Si Activite renseign�
	if (theActivite != null) {
		VariableActivite.ajouter(this, theActivite, res);
	}
	
	instancie = true;
	
	return true;
}/**
 * M�thode appel�e par la servlet qui aiguille le traitement : 
 * en fonction du bouton de la JSP 
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public boolean recupererStatut(javax.servlet.http.HttpServletRequest request) throws Exception{

	//Si on arrive de la JSP alors on traite le get
	if (request.getParameter("JSP")!=null && request.getParameter("JSP").equals(getJSP())) {

		//Si clic sur le bouton PB_PROCEDER
		if (testerParametre(request, getNOM_PB_PROCEDER())) {
			return performPB_PROCEDER(request);
		//Si clic sur le bouton PB_RAZ_ACTIVITE
		} else if (testerParametre(request, getNOM_PB_RAZ_ACTIVITE())) {
			return performPB_RAZ_ACTIVITE(request);
		//Si clic sur le bouton PB_RAZ_ACTIVITE
		} else if (testerParametre(request, getNOM_PB_INSTANCIE())) {
			return performPB_INSTANCIE(request);
		//Si clic sur le bouton PB_ANNULER
		} else if (testerParametre(request, getNOM_PB_ANNULER())) {
			return performPB_ANNULER(request);
		}

	}
	//Si TAG INPUT non g�r� par le process
	setStatut(STATUT_MEME_PROCESS);
	return true;
}
/**
 * - Traite et affecte les zones saisies dans la JSP.
 * - Impl�mente les r�gles de gestion du process
 * - Positionne un statut en fonction de ces r�gles :
 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
 * Date de cr�ation : (23/01/03 15:13:31)
 * @author : Générateur de process
 */
public boolean performPB_PROCEDER(javax.servlet.http.HttpServletRequest request) throws Exception {

	int indice = (Services.estAlphaNumerique(getZone(getNOM_LB_PROCESS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_PROCESS_SELECT())) : -1);

	if (indice == -1) {
		setStatut(STATUT_MEME_PROCESS,true,"Mais heu !!! Faut s�lectionner un process d'abord !!");
		return false;
	}
	

	setStatut(indice,true);
	
	return true;
}

/**
 * 
 *
 */
public String listerVarActivites() throws Exception{
	
	Hashtable h = getTransaction().getVariablesActivite();
	Enumeration enume = h.keys();
	String key;
	String res="<hr><u>Liste des Var d'activit� : </u>";
	while (enume.hasMoreElements()){
		res+=(String)enume.nextElement()+" ";
	}
	
	res+="<BR>";
	
	enume = h.keys();
	Object o;
	while (enume.hasMoreElements()){
		key = (String)enume.nextElement();
		o = h.get(key); 
		res+= Services.contenuMetierToString(o);
	}

	return res;
}
/**
 * Setter de la liste:
 * LB_PROCESS
 * Date de cr�ation : (27/01/03 15:56:56)
 * @author : Générateur de process
 */
private void setLB_PROCESS(java.lang.String[] newLB_PROCESS) {
	LB_PROCESS = newLB_PROCESS;
}
}
