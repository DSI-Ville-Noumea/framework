package nc.mairie.technique;

import java.io.Serializable;


/**
 * Insérez la description du type ici.
 * Date de création : (08/11/2002 14:27:54)
 * @author: Luc Bourdil
 */
public class VariableGlobale implements nc.mairie.commun.technique.ListeVariableGlobale, Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -4696898340330155060L;
/**
 * Méthode qui ajoute dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @param request request 
 * @param cle cle 
 * @param valeur valeur 
 * @Author : Luc Bourdil
 */
public static void ajouter(javax.servlet.http.HttpServletRequest request, String cle, Object valeur) {
	//request.getSession().putValue(cle,valeur);
	request.getSession().setAttribute(cle,valeur);
}
/**
 * Méthode qui enlève de la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @param request request 
 * @param cle cle 
 * @Author : Luc Bourdil
 */
public static void enlever(javax.servlet.http.HttpServletRequest request, String cle) {
	//request.getSession().removeValue(cle);
	request.getSession().removeAttribute(cle);
}
/**
 * Méthode qui recupere dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @param request request 
 * @param cle cle 
 * @return object
 * @Author : Luc Bourdil
 */
public static Object recuperer(javax.servlet.http.HttpServletRequest request, String cle) {
	//return request.getSession().getValue(cle);
	return request.getSession().getAttribute(cle);
}
}
