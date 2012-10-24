package nc.mairie.technique;

import javax.servlet.http.HttpSession;

/**
 * Insérez la description du type à cet endroit.
 *  Date de création : (05/11/99 15:05:53)
 * @author : Administrator
 */
public class EtatSession implements javax.servlet.http.HttpSessionBindingListener, java.io.Serializable {

	private java.util.Date lastAcces;
	final public static String nameClass = "EtatConnecté" ;
	private UserAppli userAppli;

/**
 * Commentaire relatif au constructeur MyClass.
 */
public EtatSession(UserAppli aUserAppli) {
	super();
	setUserAppli(aUserAppli);
	setLastAcces(new java.util.Date());
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (04/03/2002 16:33:48)
 * @return java.util.Date
 */
private java.util.Date getLastAcces() {
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
	setLastAcces(new java.util.Date());
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (04/03/2002 16:33:48)
 * @param newLastAcces java.util.Date
 */
private void setLastAcces(java.util.Date newLastAcces) {
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

	System.out.println("Connexion de '"+getUserAppli().getUserName()+"' à '"+new java.util.Date()+"' avec TimeOut de "+session.getMaxInactiveInterval());
	return;
}
/**
 * Methode qui intercepte la fin de session de l'objet arg1
 */
public void valueUnbound(javax.servlet.http.HttpSessionBindingEvent arg1) {
	HttpSession session = (HttpSession)arg1.getSource();

	System.out.println("Déconnexion de '"+getUserAppli().getUserName()+"' à '"+new java.util.Date()+"'. Dernier accès : "+ new java.util.Date(session.getLastAccessedTime()));

	//On enleve de la session toutes les données.
	String [] names = session.getValueNames();
	for (int i = 0; i < names.length; i++){
		Object o = session.getValue(names[i]);
		if ( ! names[i].equals(EtatSession.nameClass) && o instanceof BasicProcess) {
			BasicProcess process = (BasicProcess)o;
			process.fermerConnexion();
			session.removeValue(names[i]);
		}
	}
}
}
