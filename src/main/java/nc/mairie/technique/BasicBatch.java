package nc.mairie.technique;

import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Insérez la description du type ici.
 * Date de création : (06/10/2003 10:59:39)
 * @author: Administrator
 */
public abstract class BasicBatch extends Thread {
	private final static Logger logger = Logger.getLogger(BasicBatch.class.getName());
	private nc.mairie.technique.Transaction transaction;
	private static Hashtable<Class<?>, BasicBatch> hashBatch;
/**
 * Commentaire relatif au constructeur BasicBatch.
 * @param request request
 * @throws Exception exception
 */
public BasicBatch(javax.servlet.http.HttpServletRequest request) throws Exception {
	super();
	UserAppli aUserAppli = (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);

	//Parcours des batch et si présentalors Exception
	if (isBatchRunning(this.getClass())) {
		throw new Exception("Le batch "+this.getClass().getName()+" est déjà en cours d'exécution");
	}
	
	setTransaction(new nc.mairie.technique.Transaction(new nc.mairie.technique.UserAppli(aUserAppli.getUserName(),aUserAppli.getUserPassword(), aUserAppli.getServerName())));
		
	//Ajout du batch dans la liste des batch.
	getHashBatch().put(this.getClass(), this);

}
/**
Destruction
 */
@SuppressWarnings("deprecation")
public void abortTraitement() {
	stop();
	
	//Fermeture de connexion
	fermerConnexion();

	//Nettoyage Liste Batch
	epurerListeBatch();
}
/**
 Retourne vrai si le batch passé en param est en cours d'exécution
 * @param aClass aClass
 * @return boolean
 */
public static boolean arreterBatch(Class<?> aClass) {
	BasicBatch b = (BasicBatch)getHashBatch().get(aClass);
	if (b!=null) {b.abortTraitement();}
	return true;
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/02 08:57:18)
 * @author Luc Bourdil
 * @throws Exception exception
 */
public void commitTransaction() throws Exception{
	if (getTransaction() != null)
		getTransaction().commitTransaction();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (06/10/2003 16:18:41)
 */
public void epurerListeBatch() {
	getTransaction().setConnection(null);
	setTransaction(null);
	getHashBatch().remove(this.getClass());
	return;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (06/10/2003 16:18:41)
 */
public void fermerConnexion() {
	getTransaction().fermerConnexion();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/10/2003 11:04:37)
 * @return Hashtable
 */
public static Hashtable<Class<?>, BasicBatch> getHashBatch() {
	if (hashBatch == null) hashBatch = new Hashtable<Class<?>, BasicBatch>();
	return hashBatch;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (06/10/2003 11:06:15)
 * @return nc.mairie.technique.Transaction
 */
public nc.mairie.technique.Transaction getTransaction() {
	return transaction;
}
/**
 Retourne vrai si le batch passé en param est en cours d'exécution
 * @param aClass aClass
 * @return boolean
 */
public static boolean isBatchRunning(Class<?> aClass) {
	return getHashBatch().containsKey(aClass);
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/02 08:57:18)
 * @throws Exception exception
 */
public void rollbackTransaction() throws Exception{
	if (getTransaction() != null)
		getTransaction().rollbackTransaction();
}
/*
	Le traitement
*/
public void run() {
	//Le traitement
	try {
		traitement();
	} catch (Exception e) {
		logger.info("Exception interceptée dans "+this.getClass());
		e.printStackTrace();
	}

	//Fermeture de connexion
	fermerConnexion();

	//Nettoyage Liste Batch
	epurerListeBatch();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (06/10/2003 11:06:15)
 * @param newTransaction nc.mairie.technique.Transaction
 */
private void setTransaction(nc.mairie.technique.Transaction newTransaction) {
	transaction = newTransaction;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (06/10/2003 16:18:41)
 * @return boolean
 * @throws Exception exception 
 */
public abstract boolean traitement() throws Exception;
}
