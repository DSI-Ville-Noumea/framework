package nc.mairie.robot;

import java.io.Serializable;
import java.util.Hashtable;

import nc.mairie.technique.BasicProcess;

/**
 * Insérez la description du type à cet endroit.
 *  Date de création : (28/10/02 10:14:36)
 * @author : Luc Bourdil
 */
public abstract class Robot extends Object implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2320964365870414341L;
	private Hashtable<String, String> navigation;
	private Testeur myTesteur;
/**
 * Commentaire relatif au constructeur Robot.
 */
public Robot() {
	super();
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 * @return BasicProcess
 * @throws Exception 
 */
public abstract BasicProcess getDefaultProcess() throws Exception;
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 * @param activite 
 * @return BasicProcess
 * @throws Exception 
 */
public abstract BasicProcess getFirstProcess(String activite) throws Exception ;


/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 * @param activite activite
 * @return BasicProcess
 * @throws Exception 
 */
public BasicProcess getFirstProcessActivite(String activite) throws Exception {
	//Si null alors erreur
	BasicProcess aProcess = getFirstProcess(activite);
	if (aProcess == null){
		throw new Exception("Activite "+activite+" non déclarée dans le robot de navigation");
	}
	return aProcess;
}

/**
 * Insérez la description de la méthode ici.
 *  Date de création : (21/05/2003 15:29:59)
 * @return nc.mairie.robot.Testeur
 */
private Testeur getMyTesteur() {
	if (myTesteur == null) {
		myTesteur = initialiseTesteur();
	}
	return myTesteur;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 11:59:52)
 * @return Hashtable
 */
private Hashtable<String, String> getNavigation() {
	if (navigation == null) {
		navigation = initialiseNavigation();
	}
	return navigation;
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (31/10/99 10:16:34)
 * @param process processCourant
 * @return nextProcess
 * @throws Exception 
 */
public BasicProcess getNextProcess(BasicProcess process) throws Exception {

	if (process.etatStatut() == BasicProcess.STATUT_MESSAGE_INFO) {
		return new nc.mairie.commun.process.MessageInformation();
//	} else if (process.etatStatut() == process.STATUT_MEME_PROCESS || process.etatStatut() == 0) {
	} else if (process.etatStatut() == BasicProcess.STATUT_MEME_PROCESS || (process.etatStatut() == 0 && process.getClass().getName().indexOf("ZZZTESTEUR") == -1)) {		
		return process;
	}

	String nomClasse;
	
	//Si process de test
	if (process.getClass().getName().indexOf("ZZZTESTEUR") != -1) {
		nomClasse = getMyTesteur().listeProcess()[process.etatStatut()];
	} else {
		nomClasse = (String)getNavigation().get(process.getClass().getName()+process.etatStatut());
	}
	if (nomClasse == null)
		throw new Exception("Navigation du robot non déterminée avec le process " + process.getClass() +" et le statut "+process.etatStatut());
	
	Class<?> c;
	try {
		//c = Class.forName(nomClasse);
		c = Class.forName(nomClasse, true, process.getClass().getClassLoader());
	} catch (Exception e) {
		throw new Exception("Erreur dans le robot avec process " + process.getClass() +"\n Exception initiale : "+ e);
	}
	return (BasicProcess)c.newInstance();
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 * @return Hashtable<String, String>
 */
protected abstract Hashtable<String, String> initialiseNavigation() ;
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 * @return Testeur
 */
protected abstract Testeur initialiseTesteur() ;
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (21/05/2003 15:29:59)
 * @param newMyTesteur nc.mairie.robot.Testeur
 */
@SuppressWarnings("unused")
private void setMyTesteur(Testeur newMyTesteur) {
	myTesteur = newMyTesteur;
}
}
