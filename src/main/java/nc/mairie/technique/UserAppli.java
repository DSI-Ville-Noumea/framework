package nc.mairie.technique;

/**
 * Insérez la description du type ici.
 * Date de création : (22/10/2002 08:20:16)
 * @author: 
 */
public class UserAppli {
	private java.lang.String userName;
	private java.lang.String userPassword;
	private java.lang.String serverName;
	private java.util.ArrayList listeDroits;
/**
 * Commentaire relatif au constructeur UserAppli.
 */
public UserAppli(String aUserName, String aUserPassword, String aServerName) {
	super();
	userName = aUserName;
	userPassword = aUserPassword;
	serverName = aServerName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (30/12/2002 10:23:42)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getListeDroits() throws Exception {
	if (listeDroits == null) {
		listeDroits = new java.util.ArrayList();
	}
	return listeDroits;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (07/05/2004 10:44:48)
 * @return java.lang.String
 */
public java.lang.String getServerName() {
	return serverName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:00)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:15)
 * @return java.lang.String
 */
public java.lang.String getUserPassword() {
	return userPassword;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (07/05/2004 10:44:48)
 * @param newServerName java.lang.String
 */
public void setServerName(java.lang.String newServerName) {
	serverName = newServerName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:00)
 * @param newUserName java.lang.String
 */
public void setUserName(java.lang.String newUserName) {
	userName = newUserName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:15)
 * @param newUserPassword java.lang.String
 */
public void setUserPassword(java.lang.String newUserPassword) {
	userPassword = newUserPassword;
}

public void setListeDroits(java.util.ArrayList listeDroits) {
	this.listeDroits = listeDroits;
}
}
