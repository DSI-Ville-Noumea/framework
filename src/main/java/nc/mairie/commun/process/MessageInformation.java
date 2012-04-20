package nc.mairie.commun.process;

import nc.mairie.technique.BasicProcess;

/**
 	Process MessageInformation
*/
public class MessageInformation extends BasicProcess {
/**
 * Constructeur du process MessageInformation.
 */
public MessageInformation() {
	super();
}
/**
 * Retourne le nom de la JSP du process
 * Zone � utiliser dans un champ cach� dans chaque formulaire de la JSP.
 * Date de création : (07/11/02 08:59:41)
 * @author : G�n�rateur de process
 */
public String getJSP() {
	return "MessageInformation.jsp";
}
/**
 Retourne le nom d'un bouton pour la JSP :
 PB_OK
 */
public String getNOM_PB_OK() {
	return "NOM_PB_OK";
}
/**
 * Retourne pour la JSP le nom de la zone statique :
 * ST_MESSAGE
 * Date de création : (07/11/02 08:59:41)
 * @author : G�n�rateur de process
 */
public String getNOM_ST_MESSAGE() {
	return "NOM_ST_MESSAGE";
}
/**
 * Retourne la valeur � afficher par la JSP  pour la zone :
 * ST_MESSAGE
 * Date de création : (07/11/02 08:59:41)
 * @author : G�n�rateur de process
 */
public String getVAL_ST_MESSAGE() {
	return getZone(getNOM_ST_MESSAGE());
}
/**
	Initialisation des zones � afficher dans la JSP
	Alimentation des listes, s'il y en a, avec setListeLB_XXX()
	ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
	Utilisation de la m�thode addZone(getNOMxxx, String);
 */
public void initialiseZones(javax.servlet.http.HttpServletRequest request) {
	String message = getZone(getNOM_ST_MESSAGE());
	//Si pas de message
	if (message == null || message.length() == 0) {
		//R�cup du message dans la transaction
		message = getTransaction().traiterErreur();

		//Si message toujours null
		if (message == null || message.length() == 0) {
			String name = (getProcessAppelant() == null ? "null" : getProcessAppelant().getClass().getName());
			addZone(getNOM_ST_MESSAGE(),"Aucun message n'a �t� positionn� par le process "+name);
		} else {
			addZone(getNOM_ST_MESSAGE(),message);
		}
	}
}
/**
	Méthode qui : 
	- Traite et affecte les zones saisies dans la JSP.
	- Impl�mente les r�gles de gestion du process
	- Positionne un statut en fonction de ces r�gles :
	     setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
 */
public boolean recupererStatut(javax.servlet.http.HttpServletRequest request) {

	//Si on arrive de la JSP alors on traite le get
	if (request.getParameter("JSP")!=null && request.getParameter("JSP").equals(getJSP())) {

		//Si clic sur le bouton PB_OK
		if (testerParametre(request, getNOM_PB_OK())) {
			setStatut(STATUT_PROCESS_APPELANT,false);			
		}

	}else {
		setStatut(STATUT_MEME_PROCESS,false);
	}
	return true;
}
}
