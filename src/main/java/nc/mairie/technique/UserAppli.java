package nc.mairie.technique;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Insérez la description du type ici.
 * Date de création : (22/10/2002 08:20:16)
 * @author: 
 */
public class UserAppli implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 133004226206889793L;
	private String userName;
	private String userPassword;
	private String serverName;
	private ArrayList<String> listeDroits;
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
 * @return ArrayList
 */
public ArrayList<String> getListeDroits() throws Exception {
	if (listeDroits == null) {
		listeDroits = new ArrayList<String>();
	}
	return listeDroits;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (07/05/2004 10:44:48)
 * @return String
 */
public String getServerName() {
	return serverName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:00)
 * @return String
 */
public String getUserName() {
	return userName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:15)
 * @return String
 */
public String getUserPassword() {
	return userPassword;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (07/05/2004 10:44:48)
 * @param newServerName String
 */
public void setServerName(String newServerName) {
	serverName = newServerName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:00)
 * @param newUserName String
 */
public void setUserName(String newUserName) {
	userName = newUserName;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (22/10/2002 08:21:15)
 * @param newUserPassword String
 */
public void setUserPassword(String newUserPassword) {
	userPassword = newUserPassword;
}

public void setListeDroits(ArrayList<String> listeDroits) {
	this.listeDroits = listeDroits;
}
}
