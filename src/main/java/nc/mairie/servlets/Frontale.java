package nc.mairie.servlets;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import nc.mairie.technique.*;

import org.apache.commons.codec.binary.Base64;

/**
 * Insérez la description du type ici.
 * Date de création : (17/10/2002 09:22:52)
 * @author: Luc Bourdil
 */
public abstract class Frontale extends javax.servlet.http.HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5225287987424248946L;
	private final static Logger logger = Logger.getLogger(Frontale.class.getName());
	private nc.mairie.robot.Robot robot;
	private static Hashtable<String, String> mesParametres;
	private boolean veutGererActivitite = true;

	private boolean isVeutGererActivitite() {
		return veutGererActivitite;
	}
	protected void setVeutGererActivitite(boolean veutGererActivitite) {
		this.veutGererActivitite = veutGererActivitite;
	}
/**
 * Méthode qui contrôle l'habilitation d'un utilisateur qui se connecte
 * @author Luc Bourdil
 * @param HttpServletRequest
 * @return boolean
 */
public static boolean controlerHabilitation(javax.servlet.http.HttpServletRequest request) {

	//Si un user appli en session alors OK
	if (getUserAppli(request) != null)
		return true;

	//Sinon fenêtre de connexion
	String auth = request.getHeader("Authorization");
	if (auth == null)
		return false;

	String str = null;
	String passwd = null;
	String user = null;

	// Vérification du schéma d'authentification
	String startString = "basic ";
	if (auth.toLowerCase().startsWith(startString)) {
		// Extraction et décodage du user
		String creditB64 = auth.substring(startString.length());
		//sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		
		// Extraction du nom d'utilisateur et du mot de passe
		try {
			//byte[] credit = decoder.decodeBuffer(creditB64);
                        byte[] credit = Base64.decodeBase64(creditB64);
			str = new String(credit);

			//Découpage du nom user:passwd
			int sep = str.indexOf(':');
			user = str.substring(0,sep);
			passwd = str.substring(sep+1);
		} catch (Exception e) {
			return false;
		}
	}

	//Contrôle de l'habilitation LDAP
	if (!MairieLDAP.controlerHabilitation(user,passwd))
		return false;

	//Creation du UserAppli
	UserAppli aUserAppli = new UserAppli(user,passwd, (String)getMesParametres().get("HOST_SGBD"));
		
	//Ajout du user en var globale
	VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_USER_APPLI, aUserAppli);

	//set du timeoutr
//	request.getSession().setMaxInactiveInterval(1);
	
	//Ajout de l'EtatSession
	EtatSession aEtatSession = new EtatSession(aUserAppli);
	VariableGlobale.ajouter(request, EtatSession.nameClass, aEtatSession);
	return true;
}
/**
 * Cette méthode a été importée à partir d'un fichier .class.
 * Aucun code source disponible.
 */
public void destroy() {

	//Nettoyage des batch en cours d'exécution
	Enumeration<BasicBatch> enume = BasicBatch.getHashBatch().elements();
	while (enume.hasMoreElements()) {
		BasicBatch aBatch = enume.nextElement();
		aBatch.abortTraitement();
	}

	//Destroy du super
	super.destroy();
}
/**
 * Process incoming HTTP GET requests 
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}
/**
 * Process incoming HTTP POST requests 
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}
/**
 * Returns the servlet info string.
 * @author Luc Bourdil
 */
public static Hashtable<String, String> getMesParametres() {
	if (mesParametres == null) {
		mesParametres = new Hashtable<String, String>();
	}
	return mesParametres ;
}
/**
 * Retourne le robot de navigation de la servlet
 * @author: Luc Bourdil
 * @return nc.mairie.robot.Robot
 */
private nc.mairie.robot.Robot getRobot() {
	if (robot == null)
		robot = getServletRobot();
	return robot;
}
/**
 * Returns the servlet info string.
 * @author Luc Bourdil
 */
public String getServletInfo() {

	return super.getServletInfo();

}
/**
 * Retourne le robot de navigation de la servlet
 * @author: Luc Bourdil
 */
protected abstract nc.mairie.robot.Robot getServletRobot();
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 11:17:51)
 * @author Luc Bourdil
 * @return nc.mairie.technique.UserAppli
 * @param request javax.servlet.http.HttpServletRequest
 */
protected static UserAppli getUserAppli(javax.servlet.http.HttpServletRequest request) {
	return (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);
}
/**
 * Initializes the servlet.
 * @author Luc Bourdil
 */
public void init() {
	// insert code to initialize the servlet here
	initialiseParametreInitiaux();
	
	initialiseAutresParametres();
}

/**
 * Init dees paramètres du fichier inithab
 */
private void initialiseParametreHab(){
	try {
		Properties prop = null;
/*
		prop = new Properties();
		prop.put("HOST_LDAP_ADMIN", "cn=***REMOVED***,ou=PRESTA,ou=Z-users");
		prop.put("HOST_LDAP_PASSWORD", "***REMOVED***");
		prop.put("HOST_LDAP","ldap://hurle:389/");
		prop.put("BASE_LDAP","dc=site-mairie,dc=noumea,dc=nc");
		prop.put("CRITERE_RECHERCHE_LDAP","samaccountname");
		prop.put("INITCTX_LDAP","com.sun.jndi.ldap.LdapCtxFactory");
		
		FileOutputStream fos = new FileOutputStream("hab.properties");
		GZIPOutputStream gz = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(prop);
		oos.flush();
		oos.close();
		fos.close();
*/	
		
		//deserialize objects sarah and sam
		FileInputStream fis = new FileInputStream("hab.properties");
		GZIPInputStream gs = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gs);
		prop = (Properties) ois.readObject();
		ois.close();
		fis.close();
		
		Enumeration<?> e = prop.keys();
		while (e.hasMoreElements()){
			String cleParametre = (String)e.nextElement();
			String valParametre = prop.getProperty(cleParametre);
			getMesParametres().put(cleParametre,valParametre);
			logger.info("Chargement de la clé : "+cleParametre+" avec "+valParametre);
		}
		
	} catch (Exception e) {
		
	}

}


/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/02/2002 10:51:46)
 * @return fr.averse.servlets.Contexte
 */
private void initialiseParametreInitiaux() {

	boolean doitPrendreInit = getServletContext().getInitParameterNames().hasMoreElements();

	logger.info("Chargement des paramètres initiaux dans la servlet : "+getClass().getName());
	if (getMesParametres().size() == 0) {

		//Initialisation des parametres dans le fichier properties
		initialiseParametreHab();
		
		//chargement des paramêtres du contexte
		Enumeration<?> enumContext = doitPrendreInit ? getServletContext().getInitParameterNames() : getServletContext().getAttributeNames();
		while (enumContext.hasMoreElements()) {
			try {
				String cleParametre = (String)enumContext.nextElement();
				if (cleParametre != null && ! cleParametre.startsWith("com.ibm.websphere") ) {
					String valParametre = doitPrendreInit ? (String)getServletContext().getInitParameter(cleParametre) : (String)getServletContext().getAttribute(cleParametre);
					getMesParametres().put(cleParametre,valParametre);
					logger.info("Chargement de la clé : "+cleParametre+" avec "+valParametre);
				}
			} catch (Exception e) {
				continue;
			}
		}
	
		//chargement des param de la servlet
		Enumeration<?> enumServlet = getInitParameterNames();
		while (enumServlet.hasMoreElements()) {
			String cleParametre = (String)enumServlet.nextElement();
			String valParametre = (String)getInitParameter(cleParametre);
			getMesParametres().put(cleParametre,valParametre);
			logger.info("Chargement de la clé : "+cleParametre+" avec "+valParametre);
		}
	}
	
	logger.info("Fin de chargement des paramètres initiaux dans la servlet : "+getClass().getName());
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/02/2002 10:51:46)
 * @return fr.averse.servlets.Contexte
 */
protected void initialiseAutresParametres() {
	setVeutGererActivitite(true);
}
/**
 * Méthode qui parcour la liste chaînée des process pour nettoyer les process en doublons.
 * @author Luc Bourdil
 */
private void nettoyageProcessAppelant(BasicProcess processCourant) {

	BasicProcess newAppelant = null;
	BasicProcess tmpProcess = processCourant.getProcessAppelant();
	while (tmpProcess != null) {
		if (tmpProcess.getClass() == processCourant.getClass()) {
			newAppelant = tmpProcess.getProcessAppelant();
			processCourant.setProcessAppelant(newAppelant);
			break;
		}
		tmpProcess = tmpProcess.getProcessAppelant();
	}

}
/**
 * Controle l'habilitation
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 */
protected boolean performControleHabilitation(javax.servlet.http.HttpServletRequest request) throws Exception {
	return controlerHabilitation(request);
}
/**
 * Affecte une transaction au process si la connexion est fermée
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 */
protected void performAffecteTransaction(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception {

	//Si transaction fermée on en ouvre une
	if (! processCourant.estConnexionOuverte()) {
		if (processCourant.getTransaction() == null) {
			processCourant.setTransaction(new Transaction(getUserAppli(request)));
		} else {
			processCourant.getTransaction().setConnection(BasicBroker.getUneConnexion(getUserAppli(request)));
		}
	}
	
	return;
}
/**
 * Préparation de l'authentification
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
protected void performAuthentification(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {

	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
	response.setHeader("WWW-Authenticate","BASIC realm=\"Habilitation HTTP pour la Mairie\"");
	javax.servlet.ServletContext sc= getServletContext();
	javax.servlet.RequestDispatcher rd = sc.getRequestDispatcher("/"+"ConnectionInsulte.jsp");
	rd.forward(request,response);

	return;
}
/**
 * Changement d'activité
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 * @param activite
 */
protected BasicProcess performChangeActivite(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant, String activite) throws Exception{

	//Par défaut, rollback de la transaction
	processCourant.rollbackTransaction();

	//Récup de la transaction
	Transaction oldTransaction = processCourant.getTransaction();

	//Effacement d'un message erreur potentiel d l'autre activité
	oldTransaction.traiterErreur();

	//Récup du process
	try {
		processCourant = getRobot().getFirstProcessActivite(activite);
	} catch (Exception e) {
		processCourant = getRobot().getDefaultProcess();
		throw e;
	}
	
	//Si erreur robot
	if (processCourant.getTransaction().isErreur()) {
		//affectation de l'erreur pour affichage
		oldTransaction.declarerErreur(processCourant.getTransaction().traiterErreur());
	}
	processCourant.setTransaction(oldTransaction);

	//Suppression des variables d'activité
	processCourant.getTransaction().setVariablesActivite(null);
	
	//Si transaction fermée on en ouvre une
	performAffecteTransaction(request, response, processCourant);

	VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_PROCESS,processCourant);
	//processCourant.initialiseZones(request);
	processCourant.setActivite(activite);
	
	return processCourant;
}
/**
 * Traitement d'une exception
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 * @param theException
 */
protected void performException(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant, Throwable theException) {

	logger.severe("EXCEPTION dans : "+getClass().getName()+" : "+theException);
	if (processCourant != null) {
		processCourant.fermerConnexion();
	}
	theException.printStackTrace();

	BasicProcess newProcess = new nc.mairie.commun.process.MessageInformation();
	try {
		//On forwarde la JSP Information
		javax.servlet.ServletContext sc= getServletContext();
		javax.servlet.RequestDispatcher rd = null;
		if (processCourant != null) {
			processCourant.setStatut(BasicProcess.STATUT_MESSAGE_INFO,true,theException.toString());
			newProcess.setProcessAppelant(processCourant);
			newProcess.setTransaction(processCourant.getTransaction());
			newProcess.setActivite(processCourant.etatActivite());
		} else {
			newProcess.setStatut(BasicProcess.STATUT_MESSAGE_INFO,true,theException.toString());
		}
		try {newProcess.initialiseZones(request);} catch (Exception hhhh) {}
		//On remet le process dans la session
		VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_PROCESS,newProcess);
		rd = sc.getRequestDispatcher("/"+newProcess.getJSP());
		setNoCache(request, response);
		rd.forward(request, response);
	} catch (Exception eeee) {
		// are occuring to aid in debugging the problem.
		logger.severe("CON D'EXCEPTION : "+theException);
		processCourant.fermerConnexion();
		newProcess.fermerConnexion();
		theException.printStackTrace();
	}

}
/**
 * Traitement d'un fichier à imlprimer
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 */
protected void performFichierImpression(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception {
	//On redirige vers le fichier
	response.sendRedirect(processCourant.getNomFichierImpression());
	processCourant.setNomFichierImpression(null);
	return;
}
/**
 * Traitement de récup du premier process d'une activité.
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param activite
 */
protected BasicProcess performFirstProcess(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, String activite) throws Exception{

	BasicProcess processCourant = null;

	//Récup du process	
	processCourant = activite == null ? getRobot().getDefaultProcess() : getRobot().getFirstProcessActivite(activite);
	processCourant.setTransaction(new Transaction(getUserAppli(request)));

	VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_PROCESS,processCourant);
	//processCourant.initialiseZones(request);
	processCourant.setActivite(activite);

	return processCourant;
}
/**
 * Retourne le process par défaut
 *
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
protected BasicProcess performInfoTimeout(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception{

	BasicProcess processCourant = null;
	
	//Récup du process	
	processCourant = new nc.mairie.commun.process.MessageInformation();
	processCourant.setTransaction(new Transaction(getUserAppli(request)));
	processCourant.getTransaction().declarerErreur("Le temps d'inactivité est dépassé (Time out). Vous avez été déconnecté.");

	VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_PROCESS,processCourant);
	//processCourant.initialiseZones(request);
	processCourant.setActivite(null);

	return processCourant;
}
/**
 * Envoi de la JSP du process
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 */
protected void performJSP(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception {
	//On forwarde la JSP du process en cours
	javax.servlet.ServletContext sc= getServletContext();
	javax.servlet.RequestDispatcher rd = null;
	rd = sc.getRequestDispatcher("/"+processCourant.getJSP());
	setNoCache(request, response);
	rd.forward(request, response);
	return;
}
/**
 * Traitement du process appelant
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 */
protected BasicProcess performProcessAppelant(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception{
	BasicProcess processAppelant = processCourant.getProcessAppelant();

	//Si process appelant existe
	if (processAppelant != null) {
		processCourant=processAppelant;
	} else {
		//Récup de la transaction
		Transaction oldTransaction = processCourant.getTransaction();
		processCourant = getRobot().getDefaultProcess();
		processCourant.setTransaction(oldTransaction);
	}
	
	return processCourant;
}
/**
 * Traitement du process suivant
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 */
protected BasicProcess performProcessSuivant(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception{

	BasicProcess aNewProcess = null;
	String oldActivite = processCourant.etatActivite();
	Transaction oldTransaction = processCourant.getTransaction();
	aNewProcess = getRobot().getNextProcess(processCourant);
	aNewProcess.setActivite(oldActivite);
	aNewProcess.setTransaction(oldTransaction);
	//Si transaction fermée on en ouvre une
	performAffecteTransaction(request, response, processCourant);
	
	//Si on change de process
	if (! processCourant.getClass().getName().equals(aNewProcess.getClass().getName())) {
		//Si le process veut un retour, on le mémorise
		if (processCourant.estVeutRetour()) {
			aNewProcess.setProcessAppelant(processCourant);
		} else {
			aNewProcess.setProcessAppelant(processCourant.getProcessAppelant());
		}
		processCourant = aNewProcess;
		//Nettoyage des process
		nettoyageProcessAppelant(processCourant);
	}
		
	return processCourant;
}
/**
 * Récupération du statut
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 * @param processCourant
 */
protected boolean performRecupererStatut(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception {

	//Réaffectation automatique des zones si désiré
	if (processCourant.estAutoReaffecteZones())
		processCourant.reaffecteZones(request);

	//Affectation du STATUT pour parer l'oubli et Traitement du process
	processCourant.setStatut(BasicProcess.STATUT_MEME_PROCESS);
	boolean traitementOK = processCourant.gererRecuperationStatut(request);

	//Dans tous les cas, rollback (pour parer les oublis des développeurs)
	processCourant.rollbackTransaction();

	return traitementOK;
}
/**
 * Process incoming requests for information
 * 
 * @author Luc Bourdil
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void performTask(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
	//Récupération du process courant et du userAppli
	BasicProcess processCourant = (BasicProcess)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_PROCESS);
	boolean userHabilite = getUserAppli(request) != null;
	String activite = (String)request.getParameter("ACTIVITE");
	boolean doitRecupererStatut = true;

	try {
		//Si pas d'habilitation
		if (!performControleHabilitation(request)) {

			//Il faut s'auThentifier
			performAuthentification(request,response);
			return;
		}
		
		//Si on ne gère pas d'activité
		if (! isVeutGererActivitite()) {
			//Si processCourant null (on vient juste de s'habiliter)
			if (processCourant == null) {
				//Récup du first process de l'activité
				processCourant = performFirstProcess(request,response,activite);
				doitRecupererStatut = false;
			}

		} else 		
		//Si une activité a été choisie
		if (activite != null) {
			
			//Si processCourant null (on vient juste de s'habiliter)
			if (processCourant == null) {
				//Récup du first process de l'activité
				processCourant = performFirstProcess(request,response,activite);
			}

			//Si on change d'activité
			if (!activite.equals(processCourant.etatActivite())) {
				//Changement d'activité
				processCourant = performChangeActivite(request,response,processCourant, activite);
			}

			doitRecupererStatut = false;

		//Si pas d'activité choisie et process courant est null et user appli existe
		} else if (activite == null && processCourant == null && userHabilite) {
			processCourant = performInfoTimeout(request, response);
			doitRecupererStatut = false;
		
		//Si pas d'activité choisie et process courant est null
		} else if (activite == null && processCourant == null) {
			//Récup du process par défaut
			//processCourant = performInfoTimeout(request, response);
			processCourant = getRobot().getDefaultProcess();
			doitRecupererStatut = false;
		}

		//Rollback par défaut du process (pour parer les oublis des développeurs)
		if (processCourant != null) {
			processCourant.rollbackTransaction();
			//Si transaction fermée on en ouvre une
			performAffecteTransaction(request, response, processCourant);
		}

		//Si on doit récupérer le statut
		if (doitRecupererStatut) {
			
			//récup du statut	
			boolean traitementOK = performRecupererStatut(request,response,processCourant);

			//Si traitement KO et pas de message
			if (!traitementOK && ! processCourant.getTransaction().isErreur()) {
				processCourant.getTransaction().declarerErreur("Erreur détectée sans message implémenté");

			//Si traitement OK
			} else {

				//Si demande de process appelant
				if (processCourant.etatStatut() == BasicProcess.STATUT_PROCESS_APPELANT ) {
					processCourant = performProcessAppelant(request,response,processCourant);

				// Sinon, process suivant
				} else {
					processCourant = performProcessSuivant(request, response, processCourant);
				}
			}

		}

		//Si transaction fermée on en ouvre une
		performAffecteTransaction(request, response, processCourant);			

		//On remet le process dans la session
		VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_PROCESS,processCourant);

		//init des zones pour rafraichir
		processCourant.initialiseZones(request);

		// Rollback pour fermer les connexions ouvertes par initialisezone
		processCourant.rollbackTransaction();
		
		//fermeture de la connexion pour la rendre au pool
		processCourant.fermerConnexion();
		
		//Si fichier impression
		if (processCourant.getNomFichierImpression() != null && processCourant.getNomFichierImpression().length() != 0) {
			performFichierImpression(request,response,processCourant);
		//Sinon, le JSP
		} else {
			//On forwarde la JSP du process en cours
			performJSP(request,response,processCourant);
		}			
		
	//Quelle que soit l'exception interceptée
	} catch(Throwable theException)	{
		performException(request,response,processCourant,theException);
	}
}
/**
 * Supprime le cache du browser
 * @author Luc Bourdil
 */
private void setNoCache(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {

	if (request.getProtocol().compareTo("HTTP/1.0") == 0) {
		response.setHeader("Pragma", "no-cache");
	} else if (request.getProtocol().compareTo("HTTP/1.1") == 0) {
		response.setHeader("Cache-Control", "no-cache");
	}
	response.setDateHeader("Expires",0);

}
}
