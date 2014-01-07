package nc.mairie.technique;

/**
 * Insérez la description du type ici.
 * Date de création : (05/03/2003 08:38:58)
 * @author: Administrator
 */
public class BasicRecord {
	private String nomChamp;
	private String typeChamp;
	private java.lang.reflect.Field attribut;
	private String typeAttribut;
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (05/03/2003 09:30:47)
 */
public BasicRecord(String aNomChamp, String aTypeChamp, java.lang.reflect.Field aAttribut, String aTypeAttribut) {
	nomChamp = aNomChamp;
	typeChamp = aTypeChamp;
	attribut = aAttribut;
	typeAttribut = aTypeAttribut;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (05/03/2003 09:38:33)
 * @return java.lang.reflect.Field
 */
public java.lang.reflect.Field getAttribut() {
	return attribut;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (05/03/2003 09:38:33)
 * @return String
 */
public String getNomChamp() {
	return nomChamp;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (05/03/2003 09:38:33)
 * @return String
 */
public String getTypeAttribut() {
	return typeAttribut;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (05/03/2003 09:38:33)
 * @return String
 */
public String getTypeChamp() {
	return typeChamp;
}
}
