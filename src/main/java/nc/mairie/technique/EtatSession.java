package nc.mairie.technique;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

/**
 * Insérez la description du type à cet endroit.
 *  Date de création : (05/11/99 15:05:53)
 * @author : Administrator
 */
public class EtatSession implements javax.servlet.http.HttpSessionBindingListener, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5250273558400186181L;
	private final static Logger logger = Logger.getLogger(BasicBatch.class.getName());
	private Date lastAcces;
	final public static String nameClass = "EtatConnecté" ;
	private UserAppli userAppli;

/**
 * Commentaire relatif au constructeur MyClass.
 * @param aUserAppli aUserAppli 
 */
public EtatSession(UserAppli aUserAppli) {
	super();
	setUserAppli(aUserAppli);
	setLastAcces(new Date());
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (04/03/2002 16:33:48)
 * @return Date
 */
@SuppressWarnings("unused")
private Date getLastAcces() {
	return lastAcces;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 10:08:07)
 * @return nc.mairie.technique.UserAppli
 */
private UserAppli getUserAppli() {
	return userAppli;
}
/**
 */
public void modifierDateEtatSession() {
	setLastAcces(new Date());
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (04/03/2002 16:33:48)
 * @param newLastAcces Date
 */
private void setLastAcces(Date newLastAcces) {
	lastAcces = newLastAcces;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 10:08:07)
 * @param newUserAppli nc.mairie.technique.UserAppli
 */
private void setUserAppli(UserAppli newUserAppli) {
	userAppli = newUserAppli;
}
/**
 * Commentaire relatif à la méthode valueBound.
 */
public void valueBound(javax.servlet.http.HttpSessionBindingEvent arg1) {
	HttpSession session = (HttpSession)arg1.getSource();

	logger.info("Connexion de '"+getUserAppli().getUserName()+"' à '"+new Date()+"' avec TimeOut de "+session.getMaxInactiveInterval());
	return;
}
/**
 * Methode qui intercepte la fin de session de l'objet arg1
 */
public void valueUnbound(javax.servlet.http.HttpSessionBindingEvent arg1) {

	//logger.info("Déconnexion de '"+getUserAppli().getUserName()+"' à '"+new Date()+"'. Dernier accès : "+ new Date(session.getLastAccessedTime()));
	logger.info("Déconnexion de '"+getUserAppli().getUserName()+"' à '"+new Date()+"'.");

	//A ce niveau là, la session est invalidée, donc impossible de parcourir les AttributeNames...
	/*Enumeration<?> e = session.getAttributeNames();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      Object value = session.getAttribute(name);
      
      if ( ! name.equals(EtatSession.nameClass) && value instanceof BasicProcess) {
			BasicProcess process = (BasicProcess)value;
			process.fermerConnexion();
			session.removeAttribute(name);
		}
    }*/
	

}
}
