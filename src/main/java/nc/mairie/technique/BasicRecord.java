package nc.mairie.technique;

/**
 * Ins�rez la description du type ici.
 * Date de cr�ation : (05/03/2003 08:38:58)
 * @author: Administrator
 */
public class BasicRecord {
	private java.lang.String nomChamp;
	private java.lang.String typeChamp;
	private java.lang.reflect.Field attribut;
	private java.lang.String typeAttribut;
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (05/03/2003 09:30:47)
 */
public BasicRecord(String aNomChamp, String aTypeChamp, java.lang.reflect.Field aAttribut, String aTypeAttribut) {
	nomChamp = aNomChamp;
	typeChamp = aTypeChamp;
	attribut = aAttribut;
	typeAttribut = aTypeAttribut;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (05/03/2003 09:38:33)
 * @return java.lang.reflect.Field
 */
public java.lang.reflect.Field getAttribut() {
	return attribut;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (05/03/2003 09:38:33)
 * @return java.lang.String
 */
public java.lang.String getNomChamp() {
	return nomChamp;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (05/03/2003 09:38:33)
 * @return java.lang.String
 */
public java.lang.String getTypeAttribut() {
	return typeAttribut;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (05/03/2003 09:38:33)
 * @return java.lang.String
 */
public java.lang.String getTypeChamp() {
	return typeChamp;
}
}
