package nc.mairie.technique;

import java.util.Hashtable;
/**
 * Insérez la description du type ici.
 * Date de création : (27/02/2004 14:45:11)
 * @author: Administrator
 */
public class MairiePDF {
	private java.lang.String nomMasque;
	private java.util.Hashtable hashChamps;
/**
 * Commentaire relatif au constructeur MairiePDF.
 */
public MairiePDF(String nomMasque, Hashtable hashChamps) {
	super();
	this.nomMasque = nomMasque;
	this.hashChamps = hashChamps;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (27/02/2004 14:48:20)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getHashChamps() {
	if (hashChamps == null) {
		hashChamps = new java.util.Hashtable();
	}
	return hashChamps;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (27/02/2004 14:45:56)
 * @return java.lang.String
 */
public java.lang.String getNomMasque() {
	return nomMasque;
}
}
