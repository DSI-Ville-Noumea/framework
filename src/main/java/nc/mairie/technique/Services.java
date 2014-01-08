package nc.mairie.technique;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Insérez la description du type ici.
 * Date de création : (21/10/2002 10:36:22)
 * @author: Luc Bourdil
 */
public class Services {
/**
 * Commentaire relatif au constructeur Services.
 */
public Services() {
	super();
}
//*******************************************************************************************
// Retourne la date passée en paramètre (jj/mm/aaaa) + n années, sous la forme jj/mm/aaaa.
//*******************************************************************************************

public static String ajouteAnnee(String uneDate, int nbAnnees) {

	String DATE_FORMAT = "dd/MM/yyyy";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
	java.util.Date aDate;
	try {
		aDate = sdf.parse(uneDate);
	} catch (Exception e)  {
		return null;
	}

	java.util.Calendar aCalendar = java.util.Calendar.getInstance();
	aCalendar.setTime(aDate);

	aCalendar.add(Calendar.YEAR,nbAnnees);
	return sdf.format(aCalendar.getTime());

}
//*******************************************************************************************
// Retourne la date passée en paramètre (jj/mm/aaaa) + n jour, sous la forme jj/mm/aaaa.
//*******************************************************************************************

public static String ajouteJours(String uneDate, int nbJours) {

	String DATE_FORMAT = "dd/MM/yyyy";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
	java.util.Date aDate;
	try {
		aDate = sdf.parse(uneDate);
	} catch (Exception e)  {
		return null;
	}

	java.util.Calendar aCalendar = java.util.Calendar.getInstance();
	aCalendar.setTime(aDate);

	aCalendar.add(Calendar.DATE,nbJours);
	return sdf.format(aCalendar.getTime());

}
//*******************************************************************************************
// Retourne la date passée en paramètre (jj/mm/aaaa) + n mois, sous la forme jj/mm/aaaa.
//*******************************************************************************************

public static String ajouteMois(String uneDate, int nbMois) {

	String DATE_FORMAT = "dd/MM/yyyy";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
	java.util.Date aDate;
	try {
		aDate = sdf.parse(uneDate);
	} catch (Exception e)  {
		return null;
	}

	java.util.Calendar aCalendar = java.util.Calendar.getInstance();
	aCalendar.setTime(aDate);

	aCalendar.add(Calendar.MONTH,nbMois);
	return sdf.format(aCalendar.getTime());

}
//*******************************************************************************************
// Compare une 1ère date avec une autre (les deux sont dans le format jj/mm/aaaa).
// Retourne -1 si la 1ère est < à la seconde, 0 si elles sont égales, 1 si la 1ère est > à
// la seconde. En cas d'erreur quelconque (format des dates non valide), la méthode retourne
// -9999.
//
// Paramètres passés :
//
// uneDate		1ère date sous forme de chaîne de caractères (jj/mm/aaaa)
// autreDate	2ème date sous forme de chaîne de caractères (jj/mm/aaaa)
//*******************************************************************************************

public static int compareDates(String uneDate, String autreDate) {

	uneDate = formateDate(uneDate);
	autreDate=formateDate(autreDate);
	
	String DATE_FORMAT = "dd/MM/yyyy";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
	java.util.Date aDate;
	java.util.Date aAutreDate;
	try {
		aDate = sdf.parse(uneDate);
		aAutreDate = sdf.parse(autreDate);
	} catch (Exception e)  {
		return -9999;
	}

	return	aDate.compareTo(aAutreDate)	;
}
//*******************************************************************************************
// Retourne le nombre de jours compris entre deux dates
// retourne -1 si on a des dates non renseignée
//
// uneDate		1ère date sous forme de chaîne de caractères (jj/mm/aaaa)
// autreDate	2ème date sous forme de chaîne de caractères (jj/mm/aaaa)
//*******************************************************************************************

public static int compteJoursEntreDates(String uneDate, String autreDate) throws Exception{

	if (uneDate == null || uneDate.length() == 0) return 0;
	if (autreDate == null || autreDate.length() == 0) return 0;
	long aTimeUneDate   = java.sql.Date.valueOf(formateDateInternationale(uneDate)).getTime(); 
	long aTimeAutreDate = java.sql.Date.valueOf(formateDateInternationale(autreDate)).getTime();
	return 	Integer.parseInt(String.valueOf((aTimeAutreDate - aTimeUneDate)/86400000)); 
}
/**
 * Commentaire relatif au constructeur USerBroker.
 */
@SuppressWarnings("rawtypes")
public static String contenuMetierToString(Object object) throws Exception {

	if (object instanceof ArrayList) return contenuMetierToString((ArrayList)object);
	
	String classeName = object.getClass().getName();
	int posLastPoint = classeName.lastIndexOf(".");
	String result = "<span><BR> Contenu de l'objet "+classeName.substring(posLastPoint+1)+" :";
	result += "\n<BR>" + Services.rpad(" ",result.length()-4,"-");
	
	//Tableau de fields qui contient la liste de ses champs
	java.lang.reflect.Field [] fieldsObjet = object.getClass().getFields();

	for (int i = 0; i < fieldsObjet.length; i++){
		String valeur = null;
		if (fieldsObjet[i].getType().equals(boolean.class)) {
			boolean val = fieldsObjet[i].getBoolean(object);
			if (val) valeur = "true"; 
				else valeur = "false";
		}
		if (fieldsObjet[i].getType().equals(String.class)) {
			valeur = (String)fieldsObjet[i].get(object);
			if (valeur == null) {
				valeur = "null";
			}
		}
		if (valeur != null) {
			String nomChamp = Services.rpad(fieldsObjet[i].getName(),20," ");
			result = result + "\n<BR>" + nomChamp+ " : " + valeur;
		}
	}
	result += "\n<BR><BR></span>";
	
	return result;
	
}
/**
 * Commentaire relatif au constructeur USerBroker.
 */
@SuppressWarnings("rawtypes")
public static String contenuMetierToString(ArrayList aList) throws Exception {

	String resultat = "";
	for (int i = 0; i < aList.size(); i++){
		resultat+=contenuMetierToString(aList.get(i));
	}

	return resultat;
}
//*******************************************************************************************
// Achève le formattage d'une date sous la forme patern 1 en patern2. Retourne la date formattée
//
// Paramètre passé :
//
// aDate		Une date en String
// patternOrg	Un pattern d'origine
// patternDest	Un pattern d'origine
//
// Exemple convertitDate("17/11/1972", "dd/MM/yyyy", "yyyyMMdd") donne "19721117"
//*******************************************************************************************

public static String convertitDate(String aDate, String patternOrg, String patternDest) throws Exception{

	//cas parciculier du pattern "dd/MM/yyyy" pour prendre en compte sans les slash
	aDate = patternOrg.startsWith("dd/MM/yy") ? formateDate(aDate) : aDate;

	java.text.SimpleDateFormat df1 = new java.text.SimpleDateFormat(patternOrg);
	java.util.Date date1 = df1.parse(aDate);

	df1.applyPattern(patternDest);
	return df1.format(date1);
}
/**
 Methode qui formate une chaine en sortie en forcant la taille de "pChaineSource" à la taille "pTailleSource" en
 en la complétant par la droite et la gauchede caractères "pCaractPading".
  
 Exemple :
 	pChaineSource     : "hello"
 	pTailleSource     : 10
	pCaractPading     : "*"
 	Résultat obtenu   : "***hello**"

 */
public static String cpad(String pChaineSource, int pTailleChamp, String pCaractPading) {
	if (pChaineSource == null)
		pChaineSource = "";
	int nbPading=pTailleChamp-pChaineSource.length();
	String strdeb="";
	String strfin="";
	char c = pCaractPading.charAt(0);
	if (nbPading>0){
		
		int nbPadingLeft = nbPading / 2;
		int nbPadingRight = nbPading - nbPadingLeft;

		StringBuffer sb = new StringBuffer(nbPadingLeft);
		for (int i=0;i<nbPadingLeft;i++)
			sb.append(c);
		strdeb = sb.toString();

		sb = new StringBuffer(nbPadingRight);
		for (int i=0;i<nbPadingRight;i++)
			sb.append(c);
		strfin = sb.toString();
	}

	return strdeb+pChaineSource+strfin;
}
/**
* Cette méthode retourne la date courante sous la forme jj/mm/aaaa
**/

public static String dateDuJour() {

	String DATE_FORMAT = "dd/MM/yyyy";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);

	java.util.Calendar aCalendar = java.util.Calendar.getInstance();
	
	return sdf.format(aCalendar.getTime());
}
//*******************************************************************************************
// Retourne la date passée en paramètre (jj/mm/aaaa) + n années, sous la forme jj/mm/aaaa.
//*******************************************************************************************

public static String enleveAnnee(String uneDate, int nbAnnees) {

	return ajouteAnnee(uneDate, - nbAnnees);

}
//*******************************************************************************************
// Retourne la date passée en paramètre (jj/mm/aaaa) + n jour, sous la forme jj/mm/aaaa.
//*******************************************************************************************

public static String enleveJours(String uneDate, int nbJours) {

	return ajouteJours(uneDate, -nbJours);
}
//*******************************************************************************************
// Retourne la date passée en paramètre (jj/mm/aaaa) + n mois, sous la forme jj/mm/aaaa.
//*******************************************************************************************

public static String enleveMois(String uneDate, int nbMois) {

	return ajouteMois(uneDate, - nbMois);

}
/**
 * Cette méthode contrôle si la chaine de caractères passée en paramètre est alphaBETIQUE.
 * Si alphabétique retourne true
 * Sinon retourne false
 */
public static boolean estAlphabetique(String param) {
	if (param == null || param.length() == 0)
		return false;

	for (int i = 0; i < param.length(); i++){

		char aChar = param.charAt(i);
		//Si le caractère en cours n'est pas une lettre	
		if (!Character.isLetter(aChar))
			return false;
	}
	return true;
}
/**
 * Cette méthode contrôle si la chaine de caractères passée en paramètre est alphaNUMERIQUE.
 * Si alphabétique retourne true
 * Sinon retourne false
 */
public static boolean estAlphaNumerique(String param) {
	if (param == null || param.length() == 0)
		return false;

	for (int i = 0; i < param.length(); i++){

		char aChar = param.charAt(i);
		//Si le caractère en cours n'est pas une lettre	
		if (!Character.isLetter(aChar) && !Character.isDigit(aChar))
			return false;
		
	}
	return true;
}
/**
 * Cette méthode contrôle si la chaine de caractères passée en paramètre est un float.
 * ex : 200.15
 * Si float retourne true
 * Sinon retourne false
 */
public static boolean estFloat(String param) {
	try {
		Float.parseFloat(param.replace(',','.'));
		return true;
	} catch (Exception e) {
		return false;
	}
}
/**
 * Cette méthode contrôle si la chaine de caractères passée en paramètre est numérique.
 * Si numérique retourne true
 * Sinon retourne false
 */
public static boolean estNumerique(String param) {
	try {
		Long.parseLong(param);
		return true;
	} catch (Exception e) {
		return false;
	}
}
//*******************************************************************************************
// Cette méthode retourne true si la chaîne passée en paramètre est une date valide,
// false sinon.
//
// Paramètre passé :
//
// aDate		Une chaîne de caractères pouvant correspondre à une date valide
//*******************************************************************************************

public static boolean estUneDate(String aDate) {
	boolean result = false;

	if (aDate==null)
		return false;
	
	try {
		String aFormattedDate = formateDate(aDate);
		aFormattedDate = formateDateInternationale(aFormattedDate);
		if (java.sql.Date.valueOf(aFormattedDate).toString().equals(aFormattedDate)) {
			result = true;
		}
	} catch (Exception e) {
		result = false;
	}
	return result;
}
//*******************************************************************************************
// Achève le formattage d'une date sous la forme jj/mm/yyyy. Retourne la date formattée
//
// Paramètre passé :
//
// aString		Une date sour forme de chaîne de caractères.
// null         si la date n'est pas au bon format
//*******************************************************************************************

public static String formateDate(String aString) {

	String uneDateString = aString.replace('-','/').trim();

	//Si la date n'a pas de /
	if (uneDateString.indexOf("/") == -1) {
		//si la longeur est de 6 caractères : JJMMAA ou 8 caractères JJMMSSAA on rajoute les /
		if (uneDateString.length() == 6 || uneDateString.length() == 8) {
			uneDateString = uneDateString.substring(0,2) + "/" + uneDateString.substring(2,4) + "/" + uneDateString.substring(4,uneDateString.length());
		}
	}	

	//compte les /
	int cpt = 0, index=-1;
	while ((index = uneDateString.indexOf("/", index+1)) != -1) {
		cpt++;
	}
	if (cpt != 2) return null;

	int deb = 0;
	int fin = uneDateString.indexOf("/",deb);
	//récup du jour
	String day=uneDateString.substring(deb,fin);
	switch (day.length()) {
		case 2 : break;
		case 1 : day = "0"+day; break;
		default: return null;
	}

	deb=fin+1;
	fin = uneDateString.indexOf("/",deb);
	//récup du mois
	String month=uneDateString.substring(deb,fin);
	switch (month.length()) {
		case 2 : break;
		case 1 : month = "0"+month; break;
		default: return null;
	}

	deb=fin+1;
	fin = uneDateString.length();
	//récup de l'année
	String year=uneDateString.substring(deb,fin);
	//Si n'est pas sur 4 caractères, on touche
	if (year.length() < 4) {
		int intValue = Integer.parseInt(year);
	
		if (intValue < 40) {
			intValue = 2000 + intValue;
		} else if (intValue < 100) {
			intValue = 1900 + intValue;
		} else if (intValue < 1000) {
			intValue = 2000 + intValue;
		}
		year = String.valueOf(intValue);
	}

	return day + "/" + month + "/" + year;
}
//*******************************************************************************************
// Transforme une date du format JJ/MM/SSAA en YYYY-MM-JJ
//*******************************************************************************************

public static String formateDateInternationale(String uneDate) throws Exception{

	//Si null, RAF
	if (uneDate == null) return null;
	
	uneDate= uneDate.replace('-','/');
	
	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yy");
	df.setLenient(false); //Pour éviter qu'il transforme 30/02/2003 en 01/03/2003
	java.util.Date d = df.parse(uneDate);

	df.applyPattern("yyyy-MM-dd");
	return df.format(d);
}
/**
 Methode qui formate une chaine en sortie en forcant la taille de "pChaineSource" à la taille "pTailleSource" en
 en la complétant par la gauche de caractères "pCaractPading".
  
 Exemple :
 	pChaineSource     : "hello"
 	pTailleSource     : 10
	pCaractPading     : "*"
 	Résultat obtenu   : "*****hello"

*/

public static String lpad(String pChaineSource, int pTailleChamp, String pCaractPading) {
	if (pChaineSource == null)
		pChaineSource = "";
	int nbPading=pTailleChamp-pChaineSource.length();
	String strOut="";
	char c = pCaractPading.charAt(0);
	if (nbPading>0){
		StringBuffer sb = new StringBuffer(nbPading);
		for (int i=0;i<nbPading;i++)
			sb.append(c);
		strOut = sb.toString();
	}
	strOut=strOut+pChaineSource;
			
	return strOut;
}
/**
 Methode qui formate une chaine en sortie en forcant la taille de "pChaineSource" à la taille "pTailleSource" en
 en la complétant par la droite de caractères "pCaractPading".
  
 Exemple :
 	pChaineSource     : "hello"
 	pTailleSource     : 10
	pCaractPading     : "*"
 	Résultat obtenu   : "hello*****"

 */
public static String rpad(String pChaineSource, int pTailleChamp, String pCaractPading) {
	if (pChaineSource == null)
		pChaineSource = "";
	int nbPading=pTailleChamp-pChaineSource.length();
	String strOut="";
	char c = pCaractPading.charAt(0);
	if (nbPading>0){
		StringBuffer sb = new StringBuffer(nbPading);
		for (int i=0;i<nbPading;i++)
			sb.append(c);
		strOut = sb.toString();
	}

	strOut=pChaineSource+strOut;
			
	return strOut;
}
/**
 * Formate un String en 
 * - remplacant les ' par ''
 * - rajoutant ' au début et à la fin du string
 * @author Luc Bourdil
 */
public static String stringForBase(String aString){
	
	//LB modif 11/10/11
	return "'"+aString.replaceAll("'", "''")+"'";
	
	/*String temp = "";
	//si la chaine contient '
	if (aString.indexOf("'") != -1)
		for (int i = 0; i < aString.length(); i++){
			if (aString.charAt(i) != '\'')
				temp = temp + aString.charAt(i);
			else
				temp = temp + aString.charAt(i)+aString.charAt(i) ;
		}
	else
		temp = aString;
	temp = "'" + temp + "'";	
	return temp;
	*/
}

/**
 *	Formate un String en 
 *		- remplacant les " &quot; pour le HTML
 *  Date de création : (21/10/2002 10:37:13)
 * @return String
 * @param aString String
 */
public static String stringForHTML(String aString) {
	
	//LB modif 11/10/11
	return aString.replaceAll("\"", "&quot;");
	
	/*
	String temp = "";
	//Si la chaine contient "
	if (aString.indexOf("\"") != -1)
		for (int i = 0; i < aString.length(); i++){
			if (aString.charAt(i) != '"')
				temp = temp + aString.charAt(i);
			else
				temp = temp + "&quot;";
		}
	else
		temp = aString;
	return temp;
	*/
}

/**
Cette méthode trie un vecteur d'objets Identiques
 * @param <E>
*/
public static <E> ArrayList<E> trier(ArrayList<E> a, String []nomChamps, boolean []croissants) throws Exception{
	
	Vector<E> v = new Vector<E>(a);
	
	v=trier(v, nomChamps, croissants);
	
	return new ArrayList<E>(v);
}


/**
	Cette méthode trie un vecteur d'objets Identiques
 * @param <E>
*/
@SuppressWarnings("deprecation")
public static <E> Vector<E> trier(Vector<E> v, String []nomChamps, boolean []croissants) throws Exception{

try {
	//Vecteur des champs nulls
	Vector<E> vectorNull = new Vector<E>();

	//Si vide on se casse
	if (v == null || v.size() == 0)
		return v;

	if (nomChamps.length == 0)
		return v;

	if (nomChamps.length!=croissants.length) {
		throw new Exception("Les paramètres champ et croissant doivent avoir la même taille.");
	}

	String nomChamp = nomChamps[0];
	
	//Récupération du nom de champ
	java.lang.reflect.Field field;
	try {
		field = v.elementAt(0).getClass().getField(nomChamp);
	} catch (Exception getField) {
		throw new Exception("Le champ '"+nomChamp+"' est introuvable dans la classe '"+v.elementAt(0).getClass().getName()+"'");
	}

	//Création de la hashTable du champ de recherche code = champ, valeur = vecteur de l'objet
	Hashtable<String, Vector<E>> h = new Hashtable<String, Vector<E>>();
	
	for (int i = 0; i < v.size(); i++){
		E o = v.elementAt(i);
		String champ;
		try {
			champ = (String)field.get(o);
		} catch (Exception get) {
			throw new Exception("Impossible de récupérer la valeur du champ "+nomChamp+" de l'objet ");
		}

		//Si c'est une date, la clé devient un long
		try {
			java.util.Date.parse(champ);
			champ = lpad(String.valueOf(java.sql.Date.valueOf(Services.formateDateInternationale(champ)).getTime()) ,20,"0");
		} catch (Exception dateParse) {
			//Si exception ce n'est pas une date.
			//On ne fait rien au champ
			//Si numérique alors on padde (au hasard 20 caractères)
			if (Services.estNumerique(champ)) {
				champ = lpad(champ,20," "); 
			}
		}
	
		//Si le champ est null alors ajout au vecteur nulls
		if (champ == null) {
			vectorNull.addElement(v.elementAt(i));
		} else {
			//Construction d'un vecteur avec comme clé la valeur du champ
			Vector<E> v2= h.get(champ);
			if (v2 == null)
				v2= new Vector<E>();
			v2.addElement(o);
			h.put(champ,v2);
		} 
	}

	//Tri des clés
	Enumeration<String> enumCles = h.keys();
	Vector<String> cles = new Vector<String>();
	while (enumCles.hasMoreElements()) {
		cles.addElement(enumCles.nextElement());
	}
	
	Collections.sort(cles);
	if (!croissants[0]) {
		Collections.reverse(cles);
	}
	
	//boucle qui échange les données
	Vector<E> result = new Vector<E>();
	for (int k = 0; k < cles.size(); k++){
		Vector<E> vTemp = h.get(cles.elementAt(k));

		//Si le nombre de champs est > 1 alors appel récursif
		if (nomChamps.length > 1 && vTemp.size() > 1) {
			String []autresNomChamps = new String[nomChamps.length-1];
			boolean []autresCroissants = new boolean[croissants.length-1];
			for (int j = 1; j < nomChamps.length; j++){
				autresNomChamps[j-1] = nomChamps[j];
				autresCroissants[j-1] = croissants[j];
			}
			vTemp = trier(vTemp,autresNomChamps,autresCroissants);
		}

		//On cumule tous les enregistrements	
		for (int i = 0; i < vTemp.size(); i++){
			result.addElement(vTemp.elementAt(i));
		}
	}

	//On doit cumuler les enreg Nulls
	if (nomChamps.length > 1 && vectorNull.size() > 1) {
		String []autresNomChamps = new String[nomChamps.length-1];
		boolean []autresCroissants = new boolean[croissants.length-1];
		for (int j = 1; j < nomChamps.length; j++){
			autresNomChamps[j-1] = nomChamps[j];
			autresCroissants[j-1] = croissants[j];
		}
		vectorNull = trier(vectorNull,autresNomChamps,autresCroissants);
	}

	//On les rajoute
	for (int i = 0; i < vectorNull.size(); i++){
		result.addElement(vectorNull.elementAt(i));
	}

	return result;

} catch (Exception quoi) {
	throw quoi;
}
}

}
