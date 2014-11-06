package nc.mairie.technique;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Insérez la description du type ici.
 * Date de création : (18/11/2002 08:52:40)
 * @author: 
 */
public abstract class BasicMetier implements Cloneable, java.beans.PropertyChangeListener{
	private final static Logger logger = Logger.getLogger(BasicMetier.class.getName());
	private BasicBroker myBasicBroker;
	private BasicMetier basicMetierBase;
/**
 * Commentaire relatif au constructeur BasicMetier.
 */
public BasicMetier() {
	super();
}
public Object clone() throws CloneNotSupportedException{
	try {
		BasicBroker aCloneBroker = (BasicBroker)getMyBasicBroker().clone();
		BasicMetier aCloneMetier = (BasicMetier)super.clone();
		aCloneMetier.mappeAttributsFromMetier(aCloneMetier);
		aCloneMetier.setMyBasicBroker(aCloneBroker);
		aCloneBroker.setMyBasicMetier(aCloneMetier);
		return aCloneMetier;
	} catch (Exception e) {
		throw new CloneNotSupportedException("Exception dans 'clone' du BasicMetier pour la classe "+getClass().getName()+" : "+e);
	}
}
/**
 Methode à définir dans chaque objet Métier pour instancier un Broker
 * @return BasicBroker
 */
protected abstract BasicBroker definirMyBroker();
/**
 * Vérifie si deux objets sont égaux. Retourne un booléen qui indique
 *  si cet objet équivaut à celui indiqué. Cette méthode
 *  est utilisée lorsqu'un objet est stocké dans une table de hachage.
 * @author Luc Bourdil
 * @param obj l'objet à comparer avec
 * @return true si ces objets sont égaux ; false dans le cas contraire.
 * @throws Exception Exception 
 */
public boolean equals(BasicMetier obj) throws Exception{

	// Si pas la même classe alors faux
	if (! obj.getClass().equals(getClass()))
		return false;

	Field [] fieldsThis = getClass().getFields();
	//parcours des attributs
	for (int i = 0; i < fieldsThis.length; i++){
		String name = fieldsThis[i].getName();
		Object attributThis = fieldsThis[i].get(this);
		Object attributObj  = obj.getClass().getField(name).get(obj);

		//Teste si égaux (null est égal à null)
		if (attributThis == attributObj)
			continue;
		
		try {
			//Si différents alors faux	
			if (! attributThis.equals(attributObj) )
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	//Tout OK alors true
	return true;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (19/11/2002 09:09:16)
 * @author Luc Bourdil
 * @return nc.mairie.technique.BasicMetier
 */
public BasicMetier getBasicMetierBase() {
	return basicMetierBase;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (18/11/2002 08:57:28)
 * @return nc.mairie.technique.BasicBroker
 */
protected BasicBroker getMyBasicBroker() {
	if (myBasicBroker == null) {
		myBasicBroker = definirMyBroker();
	}
	return myBasicBroker;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (19/11/2002 08:57:28)
 * @author Luc Bourdil
 */
public void majBasicMetierBase() {
	try {
		basicMetierBase = (BasicMetier)this.clone();
	} catch (Exception e) {
		e.printStackTrace();
		basicMetierBase = null;
	}
}
/**
 Le but de cette méthode est de recopier tous les attributs de l'objet origine passé en paramètre
 et d'alimenter les attributs de l'objet en cours.
 Seuls les attributs de type String ou boolean sont recopiés.
 * @param metierOrigine metierOrigine 
 * @throws Exception Exception 
 */
public void mappeAttributsFromMetier(BasicMetier metierOrigine) throws Exception  {
try {
	BasicMetier origine = metierOrigine;
	
	Field [] fieldsOrigine = origine.getClass().getFields();
	Field [] fieldsThis = this.getClass().getFields();

	//HashTable qui contient en clé le nom de l'attribut de this et en valeur la position dans le tableau
	Hashtable<String, String> nomFieldsThis = new Hashtable<String, String>();

	//Je construit la Hashtable de This
	for (int i = 0; i < fieldsThis.length; i++){
		nomFieldsThis.put(fieldsThis[i].getName(),Integer.toString(i));
	}

	//On parcours tous les Fields de l'origine
	for (int i = 0; i < fieldsOrigine.length; i++){
		//Si fields boolean ou String on rajoute
		Class<?> classFieldOrigine = fieldsOrigine[i].getType();
		if (classFieldOrigine.equals(boolean.class) || classFieldOrigine.equals(String.class)) {
			String positionFieldThis = (String)nomFieldsThis.get(fieldsOrigine[i].getName());
			//Si le field est trouvé dans la destination on l'alimente
			if (positionFieldThis != null) {
				int pos = Integer.parseInt(positionFieldThis);
				if (classFieldOrigine.equals(String.class)) {
					String temp = fieldsOrigine[i].get(origine) == null ? null : new String((String)fieldsOrigine[i].get(origine));
					fieldsThis[pos].set(this, temp);
				} else {
					fieldsThis[pos].set(this,fieldsOrigine[i].get(origine));
				}
			}
		}
	}
} catch (Exception e) {
	logger.severe("Exception dans 'mappeAttributsFromMetier' : " +e );
	throw e;
}
		
}
/**
 * This method gets called when a bound property is changed.
 * @author Luc Bourdil
 * @param evt A PropertyChangeEvent object describing the event source 
 *   	and the property that has changed.
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	String prop = evt.getPropertyName().toUpperCase();
	//Si c'est un commit
	if (prop.indexOf("COMMIT") != -1) {
		majBasicMetierBase();
		((Transaction)evt.getSource()).removePropertyChangeListener(this);
	//Si c'est un rollback
	} else if (prop.indexOf("ROLLBACK") != -1) {
		((Transaction)evt.getSource()).removePropertyChangeListener(this);
	}
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (06/12/2002 10:20:28)
 * @author Luc Bourdil
 */
public void razBasicMetierBase() {
	basicMetierBase = null;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (18/11/2002 08:57:28)
 * @param newMyBasicBroker nc.mairie.technique.BasicBroker
 */
protected void setMyBasicBroker(BasicBroker newMyBasicBroker) {
	myBasicBroker = newMyBasicBroker;
}
}
