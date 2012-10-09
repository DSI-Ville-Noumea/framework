package nc.mairie.technique;

import javax.servlet.http.HttpSession;

/**
 
 *  Date de création : (05/11/99 15:05:53)
 * @author : Administrator
 */
public class EtatSession implements javax.servlet.http.HttpSessionBindingListener, java.io.Serializable {

	private java.util.Date lastAcces;
	final public static String nameClass = "EtatConnect�" ;
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
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (04/03/2002 16:33:48)
 * @return java.util.Date
 */
private java.util.Date getLastAcces() {
	return lastAcces;
}
/**
 * Ins�rez la description de la m�thode ici.
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
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de création : (04/03/2002 16:33:48)
 * @param newLastAcces java.util.Date
 */
private void setLastAcces(java.util.Date newLastAcces) {
	lastAcces = newLastAcces;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de création : (22/10/2002 10:08:07)
 * @param newUserAppli nc.mairie.technique.UserAppli
 */
private void setUserAppli(UserAppli newUserAppli) {
	userAppli = newUserAppli;
}
/**
 * Commentaire relatif � la m�thode valueBound.
 */
public void valueBound(javax.servlet.http.HttpSessionBindingEvent arg1) {
	HttpSession session = (HttpSession)arg1.getSource();

	System.out.println("Connexion de '"+getUserAppli().getUserName()+"' � '"+new java.util.Date()+"' avec TimeOut de "+session.getMaxInactiveInterval());
	return;
}
/**
 * Methode qui intercepte la fin de session de l'objet arg1
 */
public void valueUnbound(javax.servlet.http.HttpSessionBindingEvent arg1) {
	HttpSession session = (HttpSession)arg1.getSource();

	System.out.println("D�connexion de '"+getUserAppli().getUserName()+"' � '"+new java.util.Date()+"'. Dernier acc�s : "+ new java.util.Date(session.getLastAccessedTime()));

	//On enleve de la session toutes les donn�es.
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
