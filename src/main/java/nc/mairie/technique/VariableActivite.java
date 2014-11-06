package nc.mairie.technique;

/**
 * Insérez la description du type ici.
 * Date de création : (08/11/2002 14:27:54)
 * @author: Luc Bourdil
 */
public class VariableActivite implements nc.mairie.commun.technique.ListeVariableActivite{
/**
 * Méthode qui ajoute dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @param aProcess aProcess 
 * @param nomVariable nomVariable 
 * @param valeurVariable valeurVariable 
 * @Author : Luc Bourdil
 */
public static void ajouter(BasicProcess aProcess, String nomVariable, Object valeurVariable) {
	aProcess.getTransaction().ajouteVariable(nomVariable,valeurVariable);
}
/**
 * Méthode qui ajoute dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @param aProcess aProcess 
 * @param nomVariable nomVariable 
 * @Author : Luc Bourdil
 */
public static void enlever(BasicProcess aProcess, String nomVariable) {
	aProcess.getTransaction().enleveVariable(nomVariable);
}
/**
 * Méthode qui ajoute dans la session une variable globale
 * Date de création : (08/11/2002 14:53:28)
 * @param aProcess aProcess 
 * @param nomVariable nomVariable 
 * @return object
 * @Author : Luc Bourdil
 */
public static Object recuperer(BasicProcess aProcess, String nomVariable) {
	return aProcess.getTransaction().recupereVariable(nomVariable);
}
}
