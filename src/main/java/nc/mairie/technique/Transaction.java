package nc.mairie.technique;

import java.io.Serializable;

/**
 * Insérez la description du type à cet endroit.
 *  Date de création : (22/10/2002 14:46:17)
 * @author : Luc Bourdil
 */
public class Transaction implements Serializable {
	private java.lang.String messageErreur;
	private boolean erreur;
	private java.sql.Connection connection;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private long fieldCptCommit = 0;
	private long fieldCptRollBack = 0;
	private java.util.Hashtable variablesActivite = new java.util.Hashtable();
/**
 * Commentaire relatif au constructeur Transaction.
 */
public Transaction(java.sql.Connection aConnection) {
	super();
	setErreur(false);
	setConnection(aConnection);
}
/**
 * Commentaire relatif au constructeur Transaction.
 */
public Transaction(UserAppli aUserAppli) throws Exception{
	this(BasicBroker.getUneConnexion(aUserAppli));
}
/**
 * La méthode addPropertyChangeListener a été générée pour supporter la zone propertyChange.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * La méthode addPropertyChangeListener a été générée pour supporter la zone propertyChange.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}

/**
 * Insérez la description de la méthode ici.
 *  Date de création : (13/01/2003 11:27:08)
 */
public void ajouteVariable(java.lang.String nomVariable, java.lang.Object valeurVariable) {
	getVariablesActivite().put(nomVariable, valeurVariable);
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/02 08:57:18)
 * @author Luc Bourdil
 */
public void commitTransaction() throws Exception{
	if (isConnexionOuverte()) {
		getConnection().commit();
		setCptCommit(getCptCommit()+1);
	}
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/2002 14:49:28)
 * @param pMessageErreur java.lang.String
 */
public boolean declarerErreur(String pMessageErreur) {
	if(!isErreur()) { setErreur(true); setMessageErreur(pMessageErreur); return true; }
	return false;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (13/01/2003 11:27:08)
 */
public void enleveVariable(java.lang.String nomVariable) {
	getVariablesActivite().remove(nomVariable);
}
/**
 * Ferme la connexion courante si elle est ouverte
 */
public boolean fermerConnexion() {
	try {
		if (isConnexionOuverte())
			getConnection().close();
		return true;
	} catch (Exception e) {
		return false;
	}
}
/**
 * La méthode firePropertyChange a été générée pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * La méthode firePropertyChange a été générée pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * La méthode firePropertyChange a été générée pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * La méthode firePropertyChange a été générée pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (18/06/01 16:33:18)
 * @return java.sql.Connection
 */
public java.sql.Connection getConnection() {
	return connection;
}
/**
 * Extrait la valeur de la propriété cptCommit (long).
 * @return Valeur de la propriété cptCommit.
 * @see #setCptCommit
 */
public long getCptCommit() {
	return fieldCptCommit;
}
/**
 * Extrait la valeur de la propriété cptRollBack (long).
 * @return Valeur de la propriété cptRollBack.
 * @see #setCptRollBack
 */
public long getCptRollBack() {
	return fieldCptRollBack;
}

/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/2002 14:47:01)
 * @return java.lang.String
 */
public java.lang.String getMessageErreur() {
	return messageErreur;
}
/**
 * Mécanisme d'accès à la zone propertyChange.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (13/01/2003 11:31:16)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getVariablesActivite() {
	if (variablesActivite == null) {
		variablesActivite = new java.util.Hashtable();
	}
	return variablesActivite;
}
/**
 * La méthode hasListeners a été générée pour supporter la zone propertyChange.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Teste si la connexion courante est ouverte.
*/
public boolean isConnexionOuverte(){
	try {
		return getConnection() != null && !getConnection().isClosed();
	} catch (Exception e) {
		return false;
	}
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/2002 14:47:25)
 * @return boolean
 */
public boolean isErreur() {
	return erreur;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (13/01/2003 11:27:08)
 */
public java.lang.Object recupereVariable(java.lang.String nomVariable) {
	return getVariablesActivite().get(nomVariable);
}
/**
 * La méthode removePropertyChangeListener a été générée pour supporter la zone propertyChange.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * La méthode removePropertyChangeListener a été générée pour supporter la zone propertyChange.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/02 08:57:18)
 */
public void rollbackTransaction() throws Exception{
	if (isConnexionOuverte()) {
		getConnection().rollback();
		setCptRollBack(getCptRollBack()+1);
	}
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (18/06/01 16:33:18)
 * @param newConnection java.sql.Connection
 */
public void setConnection(java.sql.Connection newConnection) {
	connection = newConnection;
}
/**
 * Définit la valeur de propriété cptCommit (long).
 * @param cptCommit Nouvelle valeur pour la propriété.
 * @see #getCptCommit
 */
public void setCptCommit(long cptCommit) {
	long oldValue = fieldCptCommit;
	fieldCptCommit = cptCommit;
	firePropertyChange("cptCommit", new Long(oldValue), new Long(cptCommit));
}
/**
 * Définit la valeur de propriété cptRollBack (long).
 * @param cptRollBack Nouvelle valeur pour la propriété.
 * @see #getCptRollBack
 */
public void setCptRollBack(long cptRollBack) {
	long oldValue = fieldCptRollBack;
	fieldCptRollBack = cptRollBack;
	firePropertyChange("cptRollBack", new Long(oldValue), new Long(cptRollBack));
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/2002 14:47:25)
 * @param newErreur boolean
 */
public void setErreur(boolean newErreur) {
	erreur = newErreur;
}

/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/2002 14:47:01)
 * @param newMessageErreur java.lang.String
 */
public void setMessageErreur(java.lang.String newMessageErreur) {
	messageErreur = newMessageErreur;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (13/01/2003 11:31:16)
 * @param newVariablesActivite java.util.Hashtable
 */
public void setVariablesActivite(java.util.Hashtable newVariablesActivite) {
	variablesActivite = newVariablesActivite;
}
/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/10/2002 14:54:12)
 * @return java.lang.String
 */
public String traiterErreur() {
	//Si pas d'erreur on ne retourne rien
	if (!isErreur()) return null;

	//Récupération du message d'erreur avant de le renvoyer
	String message =getMessageErreur();
	setErreur(false);
	setMessageErreur(null);
	return message;

}
}
