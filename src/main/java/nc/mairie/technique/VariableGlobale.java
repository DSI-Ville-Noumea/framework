package nc.mairie.technique;

import java.io.Serializable;


/**
 * Ins�rez la description du type ici.
 * Date de création : (08/11/2002 14:27:54)
 * @author: Luc Bourdil
 */
public class VariableGlobale implements nc.mairie.commun.technique.ListeVariableGlobale, Serializable{
/**
 * M�thode qui ajoute dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @ Author : Luc Bourdil
 */
public static void ajouter(javax.servlet.http.HttpServletRequest request, String cle, Object valeur) {
	request.getSession().putValue(cle,valeur);
	//request.getSession().setAttribute(cle,valeur);
}
/**
 * M�thode qui enl�ve de la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @ Author : Luc Bourdil
 */
public static void enlever(javax.servlet.http.HttpServletRequest request, String cle) {
	request.getSession().removeValue(cle);
	//request.getSession().removeAttribute(cle);
}
/**
 * M�thode qui recupere dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @ Author : Luc Bourdil
 */
public static Object recuperer(javax.servlet.http.HttpServletRequest request, String cle) {
	return request.getSession().getValue(cle);
	//return request.getSession().getAttribute(cle);
}
}
