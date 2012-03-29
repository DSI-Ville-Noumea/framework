package nc.mairie.technique;

import java.util.Hashtable;

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
//	public static String HOST_LDAP = "ldap://hurle:389/";
//TEST IBM DIRECTORY SERVER	public static String HOST_LDAP = "ldap://was:389/";
//	public static String BASE_LDAP = "dc=ville-noumea,dc=nc";
//TEST IBM DIRECTORY SERVER	public static String BASE_LDAP = "o=ibm,c=us";

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
			System.out.println("MairieLDAP : Impossible de r�cup�rer le contexte admin");
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

	//Contr�les de base
	if (userName == null || userName.length() == 0 || userPassword == null || userPassword.length() == 0)
		return false;
	
	try {

		DirContext contextAdmin = null;
		//Recup du contexte admin
		try {
			 contextAdmin= getAdminContext(parametres);
		} catch (Exception admin) {
			System.out.println("MairieLDAP : Impossible de r�cup�rer le contexte admin");
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
		String hostLDAP = (String)parametres.get("HOST_LDAP");
		newEnv.put(Context.PROVIDER_URL, hostLDAP);

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
 * Methode controlerHabilitation qui retourne
 * true ou false
 */
private static DirContext getAdminContext(java.util.Hashtable parametres)  throws Exception {

		java.util.Hashtable envAdmin = new java.util.Hashtable();

		//initialisation du contexte
		String initctx_ldap = (String)parametres.get("INITCTX_LDAP");
		if (initctx_ldap == null) throw new Exception("Param�tre INITCTX_LDAP non trouv�.");
		envAdmin.put(Context.INITIAL_CONTEXT_FACTORY, initctx_ldap );
		
		String hostLDAP = (String)parametres.get("HOST_LDAP");
		if (hostLDAP == null) throw new Exception("Param�tre HOST_LDAP non trouv�.");
		envAdmin.put(Context.PROVIDER_URL, hostLDAP);

		// initialisation de l'utilisateur
		envAdmin.put(Context.SECURITY_AUTHENTICATION, "simple");
		String baseLDAP = (String)parametres.get("BASE_LDAP");
		if (baseLDAP == null) throw new Exception("Param�tre BASE_LDAP non trouv�.");
		//String critere = (String)parametres.get("CRITERE_RECHERCHE_LDAP");
		String admin = (String)parametres.get("HOST_LDAP_ADMIN");
		if (admin == null) throw new Exception("Param�tre HOST_LDAP_ADMIN non trouv�.");
		admin = mettreGuillemet(admin);
		String pwd = (String)parametres.get("HOST_LDAP_PASSWORD");
		if (pwd == null) throw new Exception("Param�tre HOST_LDAP_PASSWORD non trouv�.");
		envAdmin.put(Context.SECURITY_PRINCIPAL, admin+", " + baseLDAP);
		envAdmin.put(Context.SECURITY_CREDENTIALS,pwd);	

		return new InitialDirContext(envAdmin);
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
