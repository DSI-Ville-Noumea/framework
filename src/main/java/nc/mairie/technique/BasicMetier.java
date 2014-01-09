package nc.mairie.technique;

import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * InsÃ©rez la description du type ici.
 * Date de crÃ©ation : (18/11/2002 08:52:40)
 * @author: 
 */
public abstract class BasicMetier implements Cloneable, java.beans.PropertyChangeListener{
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
 Methode Ã  dÃ©finir dans chaque objet MÃ©tier pour instancier un Broker
 * @param <E>
 */
protected abstract BasicBroker definirMyBroker();
/**
 * VÃ©rifie si deux objets sont Ã©gaux. Retourne un boolÃ©en qui indique
 *  si cet objet Ã©quivaut Ã  celui indiquÃ©. Cette mÃ©thode
 *  est utilisÃ©e lorsqu'un objet est stockÃ© dans une table de hachage.
 * @author Luc Bourdil
 * @param objl'objet Ã  comparer avec
 * @return true si ces objets sont Ã©gaux ; false dans le cas contraire.
 * @see Hashtable
 */
public boolean equals(BasicMetier obj) throws Exception{

	// Si pas la mÃªme classe alors faux
	if (! obj.getClass().equals(getClass()))
		return false;

	Field [] fieldsThis = getClass().getFields();
	//parcours des attributs
	for (int i = 0; i < fieldsThis.length; i++){
		String name = fieldsThis[i].getName();
		Object attributThis = fieldsThis[i].get(this);
		Object attributObj  = obj.getClass().getField(name).get(obj);

		//Teste si Ã©gaux (null est Ã©gal Ã  null)
		if (attributThis == attributObj)
			continue;
		
		try {
			//Si diffÃ©rents alors faux	
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
 * InsÃ©rez la description de la mÃ©thode ici.
 *  Date de crÃ©ation : (19/11/2002 09:09:16)
 * @author Luc Bourdil
 * @return nc.mairie.technique.BasicMetier
 */
public BasicMetier getBasicMetierBase() {
	return basicMetierBase;
}
/**
 * InsÃ©rez la description de la mÃ©thode ici.
 *  Date de crÃ©ation : (18/11/2002 08:57:28)
 * @return nc.mairie.technique.BasicBroker
 */
protected BasicBroker getMyBasicBroker() {
	if (myBasicBroker == null) {
		myBasicBroker = definirMyBroker();
	}
	return myBasicBroker;
}
/**
 * InsÃ©rez la description de la mÃ©thode ici.
 *  Date de crÃ©ation : (19/11/2002 08:57:28)
 * @author Luc Bourdil
 * @param Transaction
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
 Le but de cette mÃ©thode est de recopier tous les attributs de l'objet origine passÃ© en paramÃ¨tre
 et d'alimenter les attributs de l'objet en cours.
 Seuls les attributs de type String ou boolean sont recopiÃ©s.
 */
public void mappeAttributsFromMetier(BasicMetier metierOrigine) throws Exception  {
try {
	BasicMetier origine = metierOrigine;
	
	Field [] fieldsOrigine = origine.getClass().getFields();
	Field [] fieldsThis = this.getClass().getFields();

	//HashTable qui contient en clÃ© le nom de l'attribut de this et en valeur la position dans le tableau
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
			//Si le field est trouvÃ© dans la destination on l'alimente
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
	System.err.println("Exception dans 'mappeAttributsFromMetier' : " +e );
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
 * InsÃ©rez la description de la mÃ©thode ici.
 *  Date de crÃ©ation : (06/12/2002 10:20:28)
 * @author Luc Bourdil
 * @param Transaction
 */
public void razBasicMetierBase() {
	basicMetierBase = null;
}
/**
 * InsÃ©rez la description de la mÃ©thode ici.
 *  Date de crÃ©ation : (18/11/2002 08:57:28)
 * @param newMyBasicBroker nc.mairie.technique.BasicBroker
 */
protected void setMyBasicBroker(BasicBroker newMyBasicBroker) {
	myBasicBroker = newMyBasicBroker;
}
}
