package nc.mairie.technique;

import java.util.Hashtable;;
/**
 * Insérez la description du type ici.
 * Date de création : (27/02/2004 14:45:11)
 * @author: Administrator
 */
public class MairiePDF {
	private String nomMasque;
	@SuppressWarnings("rawtypes")
	private Hashtable hashChamps;
/**
 * Commentaire relatif au constructeur MairiePDF.
 */
public MairiePDF(String nomMasque, @SuppressWarnings("rawtypes") Hashtable hashChamps) {
	super();
	this.nomMasque = nomMasque;
	this.hashChamps = hashChamps;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (27/02/2004 14:48:20)
 * @return Hashtable
 */
@SuppressWarnings("rawtypes")
public Hashtable getHashChamps() {
	if (hashChamps == null) {
		hashChamps = new Hashtable();
	}
	return hashChamps;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (27/02/2004 14:45:56)
 * @return String
 */
public String getNomMasque() {
	return nomMasque;
}
}
