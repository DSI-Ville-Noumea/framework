package nc.mairie.technique;

/**
 * Ins�rez la description du type ici.
 * Date de cr�ation : (08/11/2002 14:27:54)
 * @author: Luc Bourdil
 */
public class VariableActivite implements nc.mairie.commun.technique.ListeVariableActivite{
/**
 * M�thode qui ajoute dans la session une variable globale
 * Date de cr�ation : (08/11/2002 14:53:28)
 * @ Author : Luc Bourdil
 */
public static void ajouter(BasicProcess aProcess, String nomVariable, Object valeurVariable) {
	aProcess.getTransaction().ajouteVariable(nomVariable,valeurVariable);
}
/**
 * M�thode qui ajoute dans la session une variable globale
 * Date de cr�ation : (08/11/2002 14:53:28)
 * @ Author : Luc Bourdil
 */
public static void enlever(BasicProcess aProcess, String nomVariable) {
	aProcess.getTransaction().enleveVariable(nomVariable);
}
/**
 * M�thode qui ajoute dans la session une variable globale
 * Date de cr�ation : (08/11/2002 14:53:28)
 * @ Author : Luc Bourdil
 */
public static Object recuperer(BasicProcess aProcess, String nomVariable) {
	return aProcess.getTransaction().recupereVariable(nomVariable);
}
}
