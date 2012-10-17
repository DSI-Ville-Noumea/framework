package nc.mairie.technique;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.directory.*;
import javax.naming.*;
import nc.mairie.servlets.Frontale;

/**
 * Insérez la description du type ici.
 * Date de création : (22/11/2002 10:05:43)
 * @author: 
 */
public class MairieLDAP {
	// Initialisation du contexte
//Pour WTE	public static String INITCTX_LDAP = "com.ibm.jndi.LDAPCtxFactory";
//POUR WAS 5	public static String INITCTX_LDAP = "com.sun.jndi.ldap.LdapCtxFactory";

static String HOST_LDAP = null;

/**
 * Commentaire relatif au constructeur MairieLDAP.
 */
public MairieLDAP() {
	super();
}

/**
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
public static  Hashtable chercherUserLDAPAttributs(String userName)  {
	return chercherUserLDAPAttributs(Frontale.getMesParametres(), userName);
}

/**
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
public static Hashtable chercherUserLDAPAttributs(Hashtable parametres, String userName)  {
	Hashtable res = new Hashtable();
	
	try {

		DirContext contextAdmin = null;
		//Recup du contexte admin
		try {
			 contextAdmin= getAdminContext(parametres);
		} catch (Exception admin) {
			System.out.println("MairieLDAP : Impossible de récupérer le contexte admin");
			throw admin;
		}
	
		
		//RECHERCHE DU USER EN PARAM		
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

		//hurle NamingEnumeration enum = ctx.search(BASE_LDAP, "(cn="+user+")", constraints);
		String critere = (String)parametres.get("CRITERE_RECHERCHE_LDAP");
				
		NamingEnumeration enume = contextAdmin.search((String)parametres.get("BASE_LDAP"), "("+critere+"="+userName+")", constraints);

		if (enume.hasMore()) {
			SearchResult sr = (SearchResult)enume.next();
			Attributes attr = sr.getAttributes();
			NamingEnumeration ne = attr.getIDs();
			while (ne.hasMoreElements()) {
				Object o  = ne.nextElement();
				attr.get(o.toString()).get();
				res.put(o,attr.get(o.toString()).get());
			}
		}
			

	} catch (Exception e) {
		System.out.println(e);
		return null;
	}
	
	res.get("department");
	
	return res;
}

/**
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
public static  boolean controlerHabilitation(String userName, String userPassword)  {
	return controlerHabilitation(Frontale.getMesParametres(), userName, userPassword);
}
/**
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
public static  boolean controlerHabilitation(java.util.Hashtable parametres, String userName, String userPassword)  {

	//Contrôles de base
	if (userName == null || userName.length() == 0 || userPassword == null || userPassword.length() == 0)
		return false;
	
	try {

		DirContext contextAdmin = null;
		//Recup du contexte admin
		try {
			 contextAdmin= getAdminContext(parametres);
		} catch (Exception admin) {
			System.out.println("MairieLDAP : Impossible de récupérer le contexte admin");
			throw admin;
		}
	
		
		//RECHERCHE DU USER EN PARAM		
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

		//hurle NamingEnumeration enum = ctx.search(BASE_LDAP, "(cn="+user+")", constraints);
		String critere = (String)parametres.get("CRITERE_RECHERCHE_LDAP");
				
		NamingEnumeration enume = contextAdmin.search((String)parametres.get("BASE_LDAP"), "("+critere+"="+userName+")", constraints);

		String dn = "";
		
		if (enume.hasMore()) {
			SearchResult sr = (SearchResult)enume.next();
			dn = sr.getName();
		}

		//Comme le dn revient avec des espaces, il faut mettre des guillemets
		dn=mettreGuillemet(dn);

		//PREPARATION CONTROLE PWD
		java.util.Hashtable newEnv = new java.util.Hashtable();

		//initialisation du contexte
		newEnv.put(Context.INITIAL_CONTEXT_FACTORY, (String)parametres.get("INITCTX_LDAP"));
		newEnv.put(Context.PROVIDER_URL, HOST_LDAP);

		// initialisation de l'utilisateur
		newEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		newEnv.put(Context.SECURITY_PRINCIPAL, dn+","+(String)parametres.get("BASE_LDAP"));
		newEnv.put(Context.SECURITY_CREDENTIALS, userPassword);

		try {
			new InitialDirContext(newEnv);
		} catch (AuthenticationException auth) { // Mauvais password
			System.out.println("MairieLDAP : Mauvais mot de passe pour le user " + userName +" --> "+auth);
			return false;
		} catch (NameNotFoundException auth2) { //Nom inexistant
			System.out.println("MairieLDAP : User " + userName +" inexistant dans ldap --> "+auth2);
			return false;
		}

		return true;
	} catch (Exception e) {
		System.out.println(e);
		return false;
	}
}

/**
 * Construit de facon random la liste des HostLDAP disponibles trouvés dans 
 * le fichier hab.Properties
 */
private static ArrayList construitListeHostLDAP(java.util.Hashtable parametres){
	ArrayList res = new ArrayList();
	Enumeration keys = parametres.keys();
	//Ajout de la liste des hosts possibles lus dans le fichier hab.properties 
	while (keys.hasMoreElements()) {
		String key = (String) keys.nextElement();
		if (key.startsWith("HOST_LDAP") && ! key.startsWith("HOST_LDAP_")) {
			res.add((String)parametres.get(key));
		}
	}
	
	//Tri random des hostLDAP
	int lower = 0;
	int higher = 2;
	
	for (int i = 0; i < res.size(); i++) {
	
		int random = (int)(Math.random() * (higher+1-lower)) + lower;
		
		String temp =(String)res.get(i);
		res.remove(i);
		res.add(random,temp);
		
	}
	return res;
}
/**
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
private static DirContext getAdminContext(java.util.Hashtable parametres)  throws Exception {

		java.util.Hashtable envAdmin = new java.util.Hashtable();

		//initialisation du contexte
		String initctx_ldap = (String)parametres.get("INITCTX_LDAP");
		if (initctx_ldap == null) throw new Exception("Paramètre INITCTX_LDAP non trouvé.");
		envAdmin.put(Context.INITIAL_CONTEXT_FACTORY, initctx_ldap );

		// initialisation de l'utilisateur
		envAdmin.put(Context.SECURITY_AUTHENTICATION, "simple");
		String baseLDAP = (String)parametres.get("BASE_LDAP");
		if (baseLDAP == null) throw new Exception("Paramètre BASE_LDAP non trouvé.");
		//String critere = (String)parametres.get("CRITERE_RECHERCHE_LDAP");
		String admin = (String)parametres.get("HOST_LDAP_ADMIN");
		if (admin == null) throw new Exception("Paramètre HOST_LDAP_ADMIN non trouvé.");
		admin = mettreGuillemet(admin);
		String pwd = (String)parametres.get("HOST_LDAP_PASSWORD");
		if (pwd == null) throw new Exception("Paramètre HOST_LDAP_PASSWORD non trouvé.");
		envAdmin.put(Context.SECURITY_PRINCIPAL, admin+", " + baseLDAP);
		envAdmin.put(Context.SECURITY_CREDENTIALS,pwd);	
	
		
		//si déjà trouvé host, on reste sur le même
		if (HOST_LDAP != null) {
			envAdmin.put(Context.PROVIDER_URL, HOST_LDAP);
			return new InitialDirContext(envAdmin);
		}
		
		//si on est là : soit le HOST_LDAP est tombé, soit c'est la première connexion
		DirContext dirContextAdmin = null;
		
		ArrayList listeHostLDap = construitListeHostLDAP(parametres);
		//On teste les host LDAP et on arrête au premier bon host
		for (Iterator iter = listeHostLDap.iterator(); iter.hasNext();) {
			HOST_LDAP = (String) iter.next();
			envAdmin.put(Context.PROVIDER_URL, HOST_LDAP);
				
			try {
				dirContextAdmin = new InitialDirContext(envAdmin);
				break;
			} catch (Exception e) {
				System.out.println("MairieLDAP : Impossible de récupérer le contexte admin avec "+HOST_LDAP);
				HOST_LDAP = null;
			}
			
		}

		if (HOST_LDAP == null ) {
			//Si on est là : aucun HOST_LDAP n'est valide !!!
			throw new Exception("MairieLDAP : Paramètre HOST_LDAP non trouvé ou incorrect.");
		}
		
		return dirContextAdmin;
}
/**
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
private static String mettreGuillemet(String dn)  {

	//Si chaine ok
	if (dn != null && dn.length() > 0) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < dn.length(); i++){
			char c = dn.charAt(i);
			if (c == ',') b.append('"');
			b.append(c);
			if (c == '=') b.append('"');
		}
		b.append('"');
		dn = b.toString();
	}
	return dn;
}
}
