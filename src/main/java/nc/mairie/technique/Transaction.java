package nc.mairie.technique;

/**
 * Ins�rez la description du type � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:46:17)
 * @author : Luc Bourdil
 */
public class Transaction {
	private java.lang.String messageErreur;
	private boolean erreur;
	private java.sql.Connection connection;
	private static java.util.Vector listeConnections;
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
	addTransaction(this);
}
/**
 * Commentaire relatif au constructeur Transaction.
 */
public Transaction(UserAppli aUserAppli) throws Exception{
	this(BasicBroker.getUneConnexion(aUserAppli));
}
/**
 * La m�thode addPropertyChangeListener a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * La m�thode addPropertyChangeListener a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * Commentaire relatif au constructeur Transaction.
 */
private static void addTransaction(Transaction aTransaction) {
	// listeConnections
	java.util.Vector v = new java.util.Vector();
	//On ne garde que les connections ouvertes
	for (int i = 0; i < getListeConnections().size(); i++){
		java.sql.Connection conn = (java.sql.Connection)getListeConnections().elementAt(i);
		try {
		if (!conn.isClosed())
			v.addElement(conn);
		} catch (Exception e) {}
	}
	setListeConnections(v);

	//On rajoute la nouvelle connection
	getListeConnections().addElement(aTransaction.getConnection());
	int i=0;
	i++;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (13/01/2003 11:27:08)
 */
public void ajouteVariable(java.lang.String nomVariable, java.lang.Object valeurVariable) {
	getVariablesActivite().put(nomVariable, valeurVariable);
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/02 08:57:18)
 * @author Luc Bourdil
 */
public void commitTransaction() throws Exception{
	if (isConnexionOuverte()) {
		getConnection().commit();
		setCptCommit(getCptCommit()+1);
	}
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:49:28)
 * @param pMessageErreur java.lang.String
 */
public boolean declarerErreur(String pMessageErreur) {
	if(!isErreur()) { setErreur(true); setMessageErreur(pMessageErreur); return true; }
	return false;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (13/01/2003 11:27:08)
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
 * La m�thode firePropertyChange a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * La m�thode firePropertyChange a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * La m�thode firePropertyChange a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * La m�thode firePropertyChange a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (18/06/01 16:33:18)
 * @return java.sql.Connection
 */
public java.sql.Connection getConnection() {
	return connection;
}
/**
 * Extrait la valeur de la propri�t� cptCommit (long).
 * @return Valeur de la propri�t� cptCommit.
 * @see #setCptCommit
 */
public long getCptCommit() {
	return fieldCptCommit;
}
/**
 * Extrait la valeur de la propri�t� cptRollBack (long).
 * @return Valeur de la propri�t� cptRollBack.
 * @see #setCptRollBack
 */
public long getCptRollBack() {
	return fieldCptRollBack;
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (19/06/01 11:09:58)
 * @return java.util.Vector
 */
public static java.util.Vector getListeConnections() {
	if (listeConnections == null)
		listeConnections = new java.util.Vector();
	return listeConnections;
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:47:01)
 * @return java.lang.String
 */
public java.lang.String getMessageErreur() {
	return messageErreur;
}
/**
 * M�canisme d'acc�s � la zone propertyChange.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (13/01/2003 11:31:16)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getVariablesActivite() {
	if (variablesActivite == null) {
		variablesActivite = new java.util.Hashtable();
	}
	return variablesActivite;
}
/**
 * La m�thode hasListeners a �t� g�n�r�e pour supporter la zone propertyChange.
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
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:47:25)
 * @return boolean
 */
public boolean isErreur() {
	return erreur;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (13/01/2003 11:27:08)
 */
public java.lang.Object recupereVariable(java.lang.String nomVariable) {
	return getVariablesActivite().get(nomVariable);
}
/**
 * La m�thode removePropertyChangeListener a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * La m�thode removePropertyChangeListener a �t� g�n�r�e pour supporter la zone propertyChange.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/02 08:57:18)
 */
public void rollbackTransaction() throws Exception{
	if (isConnexionOuverte()) {
		getConnection().rollback();
		setCptRollBack(getCptRollBack()+1);
	}
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (18/06/01 16:33:18)
 * @param newConnection java.sql.Connection
 */
public void setConnection(java.sql.Connection newConnection) {
	connection = newConnection;
}
/**
 * D�finit la valeur de propri�t� cptCommit (long).
 * @param cptCommit Nouvelle valeur pour la propri�t�.
 * @see #getCptCommit
 */
public void setCptCommit(long cptCommit) {
	long oldValue = fieldCptCommit;
	fieldCptCommit = cptCommit;
	firePropertyChange("cptCommit", new Long(oldValue), new Long(cptCommit));
}
/**
 * D�finit la valeur de propri�t� cptRollBack (long).
 * @param cptRollBack Nouvelle valeur pour la propri�t�.
 * @see #getCptRollBack
 */
public void setCptRollBack(long cptRollBack) {
	long oldValue = fieldCptRollBack;
	fieldCptRollBack = cptRollBack;
	firePropertyChange("cptRollBack", new Long(oldValue), new Long(cptRollBack));
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:47:25)
 * @param newErreur boolean
 */
public void setErreur(boolean newErreur) {
	erreur = newErreur;
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (19/06/01 11:09:58)
 * @param newListeTransactions java.util.Vector
 */
public static void setListeConnections(java.util.Vector newListeConnections) {
	listeConnections = newListeConnections;
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:47:01)
 * @param newMessageErreur java.lang.String
 */
public void setMessageErreur(java.lang.String newMessageErreur) {
	messageErreur = newMessageErreur;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (13/01/2003 11:31:16)
 * @param newVariablesActivite java.util.Hashtable
 */
public void setVariablesActivite(java.util.Hashtable newVariablesActivite) {
	variablesActivite = newVariablesActivite;
}
/**
 * Ins�rez la description de la m�thode � cet endroit.
 *  Date de cr�ation : (22/10/2002 14:54:12)
 * @return java.lang.String
 */
public String traiterErreur() {
	//Si pas d'erreur on ne retourne rien
	if (!isErreur()) return null;

	//R�cup�ration du message d'erreur avant de le renvoyer
	String message =getMessageErreur();
	setErreur(false);
	setMessageErreur(null);
	return message;

}
}
