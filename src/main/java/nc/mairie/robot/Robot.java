package nc.mairie.robot;

import java.io.Serializable;

import nc.mairie.technique.BasicProcess;

/**
 
 *  Date de création : (28/10/02 10:14:36)
 * @author : Luc Bourdil
 */
public abstract class Robot extends Object implements Serializable{ 
	private java.util.Hashtable navigation;
	private Testeur myTesteur;
/**
 * Commentaire relatif au constructeur Robot.
 */
public Robot() {
	super();
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 */
public abstract BasicProcess getDefaultProcess() throws Exception;
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 */
public abstract BasicProcess getFirstProcess(String activite) throws Exception ;


/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 */
public BasicProcess getFirstProcessActivite(String activite) throws Exception {
	//Si null alors erreur
	BasicProcess aProcess = getFirstProcess(activite);
	if (aProcess == null){
		throw new Exception("Activite "+activite+" non d�clar�e dans le robot de navigation");
	}
	return aProcess;
}

/**
 * Ins�rez la description de la m�thode ici.
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
 * Ins�rez la description de la m�thode ici.
 *  Date de création : (28/10/2002 11:59:52)
 * @return java.util.Hashtable
 */
private java.util.Hashtable getNavigation() {
	if (navigation == null) {
		navigation = initialiseNavigation();
	}
	return navigation;
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (31/10/99 10:16:34)
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
		throw new Exception("Navigation du robot non d�termin�e avec le process " + process.getClass() +" et le statut "+process.etatStatut());
	
	Class c;
	try {
		//c = Class.forName(nomClasse);
		c = Class.forName(nomClasse, true, process.getClass().getClassLoader());
	} catch (Exception e) {
		throw new Exception("Erreur dans le robot avec process " + process.getClass() +"\n Exception initiale : "+ e);
	}
	return (BasicProcess)c.newInstance();
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 */
protected abstract java.util.Hashtable initialiseNavigation() ;
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (28/10/02 10:16:34)
 */
protected abstract Testeur initialiseTesteur() ;
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de création : (21/05/2003 15:29:59)
 * @param newMyTesteur nc.mairie.robot.Testeur
 */
private void setMyTesteur(Testeur newMyTesteur) {
	myTesteur = newMyTesteur;
}
}
