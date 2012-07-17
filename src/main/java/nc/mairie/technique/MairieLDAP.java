package nc.mairie.technique;

import java.util.*;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import nc.mairie.servlets.Frontale;
import org.apache.commons.lang.StringUtils;


/**
 * Insérez la description du type ici. Date de création : (22/11/2002 10:05:43)
 *
 * @author:
 */
public class MairieLDAP {

    public final static String INITCTX_LDAP = "INITCTX_LDAP";
    public final static String HOST_LDAP = "HOST_LDAP";
    public final static String BASE_LDAP = "BASE_LDAP";
    public final static String HOST_LDAP_ADMIN = "HOST_LDAP_ADMIN";
    public final static String HOST_LDAP_PASSWORD = "HOST_LDAP_PASSWORD";
    public final static String CRITERE_RECHERCHE_LDAP = "CRITERE_RECHERCHE_LDAP";
    private final static Logger logger = Logger.getLogger(MairieLDAP.class .getName());
//    private static Logger logger = LoggerFactory.getLogger(MairieLDAP.class);

// Initialisation du contexte
//  Pour WTE      public static String INITCTX_LDAP = "com.ibm.jndi.LDAPCtxFactory";
//  POUR WAS 5    public static String INITCTX_LDAP = "com.sun.jndi.ldap.LdapCtxFactory";
//        public static String HOST_LDAP = "ldap://hurle:389/";
//  TEST IBM DIRECTORY SERVER     public static String HOST_LDAP = "ldap://was:389/";
//        public static String BASE_LDAP = "dc=ville-noumea,dc=nc";
//  TEST IBM DIRECTORY SERVER     public static String BASE_LDAP = "o=ibm,c=us";
    /**
     * Commentaire relatif au constructeur MairieLDAP.
     */
    public MairieLDAP() {
        super();
    }

    /** Génère une liste aléatoire des serveurs, cf variable HOST_LDAP.
     * @param serversList, la liste des serveurs ldap, en une seule chaîne,
     * séparée par des espaces simples, tel que défini dans la norme, et renvoie
     * cette même liste, dans un ordre différent
     *
     * @return
     */
    public static String randomizeLdapServersList(String serversList) {
        String out = null;
        String work = StringUtils.trimToNull(serversList);
        if (work == null) {
            return null;
        } else {
            
            String[] ldaps = serversList.split(" ");
            List<String> ldapServers = Arrays.asList(ldaps);
            Collections.shuffle(ldapServers);
            Iterator<String> iter = ldapServers.iterator();
            //
            out = "";
            while (iter.hasNext()) {
                //logger.debug(iter.next());
                out += " " + iter.next();
            }
            out = StringUtils.trim(out);
            return out;

        }

    }

    /**
     * Methode controlerHabilitation qui retourne true ou false
     */
    public static Hashtable chercherUserLDAPAttributs(String userName) {
        return chercherUserLDAPAttributs(Frontale.getMesParametres(), userName);
    }

    /**
     * Methode controlerHabilitation qui retourne true ou false
     */
    public static Hashtable chercherUserLDAPAttributs(Hashtable parametres, String userName) {
        Hashtable res = new Hashtable();

        try {
            DirContext contextAdmin = null;

            // Recup du contexte admin
            try {
                contextAdmin = getAdminContext(parametres);
            } catch (Exception admin) {
                logger.severe("MairieLDAP : Impossible de récupérer le contexte admin");

                throw admin;
            }

            // RECHERCHE DU USER EN PARAM
            SearchControls constraints = new SearchControls();

            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // hurle NamingEnumeration enum = ctx.search(BASE_LDAP, "(cn="+user+")", constraints);
            String critere = (String) parametres.get(MairieLDAP.CRITERE_RECHERCHE_LDAP);
            NamingEnumeration enume = contextAdmin.search((String) parametres.get(MairieLDAP.BASE_LDAP),
                    "(" + critere + "=" + userName + ")", constraints);

            if (enume.hasMore()) {
                SearchResult sr = (SearchResult) enume.next();
                Attributes attr = sr.getAttributes();
                NamingEnumeration ne = attr.getIDs();

                while (ne.hasMoreElements()) {
                    Object o = ne.nextElement();

                    attr.get(o.toString()).get();
                    res.put(o, attr.get(o.toString()).get());
                }
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());

            return null;
        }

        res.get("department");

        return res;
    }

    /**
     * Methode controlerHabilitation qui retourne true ou false
     */
    public static boolean controlerHabilitation(String userName, String userPassword) {
        return controlerHabilitation(Frontale.getMesParametres(), userName, userPassword);
    }

    /**
     * Methode controlerHabilitation qui retourne true ou false
     */
    public static boolean controlerHabilitation(Hashtable parametres, String userName, String userPassword) {

        // Contrôles de base
        if ((userName == null) || (userName.length() == 0) || (userPassword == null) || (userPassword.length() == 0)) {
            return false;
        }

        try {
            DirContext contextAdmin = null;

            // Recup du contexte admin
            try {
                contextAdmin = getAdminContext(parametres);
            } catch (Exception admin) {
                logger.severe("MairieLDAP : Impossible de récupérer le contexte admin");

                throw admin;
            }

            // RECHERCHE DU USER EN PARAM
            SearchControls constraints = new SearchControls();

            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // hurle NamingEnumeration enum = ctx.search(BASE_LDAP, "(cn="+user+")", constraints);
            String critere = (String) parametres.get(MairieLDAP.CRITERE_RECHERCHE_LDAP);
            NamingEnumeration enume = contextAdmin.search((String) parametres.get(MairieLDAP.BASE_LDAP),
                    "(" + critere + "=" + userName + ")", constraints);
            String dn = "";

            if (enume.hasMore()) {
                SearchResult sr = (SearchResult) enume.next();

                dn = sr.getName();
            }

            // Comme le dn revient avec des espaces, il faut mettre des guillemets
            dn = mettreGuillemet(dn);

            // PREPARATION CONTROLE PWD
            java.util.Hashtable newEnv = new java.util.Hashtable();

            // initialisation du contexte
            newEnv.put(Context.INITIAL_CONTEXT_FACTORY, (String) parametres.get(MairieLDAP.INITCTX_LDAP));

            //String hostLDAP = (String)parametres.get(MairieLDAP.HOST_LDAP);
            String hostLDAP = randomizeLdapServersList((String)parametres.get(MairieLDAP.HOST_LDAP));

            newEnv.put(Context.PROVIDER_URL, hostLDAP);

            // initialisation de l'utilisateur
            newEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
            newEnv.put(Context.SECURITY_PRINCIPAL, dn + "," + (String) parametres.get(MairieLDAP.BASE_LDAP));
            newEnv.put(Context.SECURITY_CREDENTIALS, userPassword);

            try {
                new InitialDirContext(newEnv);
            } catch (AuthenticationException auth) {    // Mauvais password
                logger.warning("MairieLDAP : Mauvais mot de passe pour le user " + userName + " --> " + auth);

                return false;
            } catch (NameNotFoundException auth2) {     // Nom inexistant
                logger.warning("MairieLDAP : User " + userName + " inexistant dans ldap --> " + auth2);

                return false;
            }

            return true;
        } catch (Exception e) {
            logger.severe(e.getMessage());

            return false;
        }
    }

    /**
     * Methode controlerHabilitation qui retourne true ou false
     */
    private static DirContext getAdminContext(java.util.Hashtable parametres) throws Exception {
        java.util.Hashtable envAdmin = new java.util.Hashtable();

        // initialisation du contexte
        String initctx_ldap = (String) parametres.get(MairieLDAP.INITCTX_LDAP);

        if (initctx_ldap == null) {
            throw new Exception("Paramètre " + MairieLDAP.INITCTX_LDAP + " non trouvé.");
        }

        envAdmin.put(Context.INITIAL_CONTEXT_FACTORY, initctx_ldap);

        //String hostLDAP = (String) parametres.get(MairieLDAP.HOST_LDAP);
        String hostLDAP = randomizeLdapServersList((String)parametres.get(MairieLDAP.HOST_LDAP));

        if (hostLDAP == null) {
            throw new Exception("Paramètre HOST_LDAP non trouvé.");
        }

        envAdmin.put(Context.PROVIDER_URL, hostLDAP);

        // initialisation de l'utilisateur
        envAdmin.put(Context.SECURITY_AUTHENTICATION, "simple");

        String baseLDAP = (String) parametres.get(MairieLDAP.BASE_LDAP);

        if (baseLDAP == null) {
            throw new Exception("Paramètre " + MairieLDAP.BASE_LDAP + " non trouvé.");
        }

        // String critere = (String)parametres.get("CRITERE_RECHERCHE_LDAP");
        String admin = (String) parametres.get(MairieLDAP.HOST_LDAP_ADMIN);

        if (admin == null) {
            throw new Exception("Paramètre " + MairieLDAP.HOST_LDAP_ADMIN + " non trouvé.");
        }

        admin = mettreGuillemet(admin);

        String pwd = (String) parametres.get(MairieLDAP.HOST_LDAP_PASSWORD);

        if (pwd == null) {
            throw new Exception("Paramètre " + MairieLDAP.HOST_LDAP_PASSWORD + " non trouvé.");
        }

        envAdmin.put(Context.SECURITY_PRINCIPAL, admin + ", " + baseLDAP);
        envAdmin.put(Context.SECURITY_CREDENTIALS, pwd);

        return new InitialDirContext(envAdmin);
    }

    /**
     * Methode controlerHabilitation qui retourne true ou false
     */
    private static String mettreGuillemet(String dn) {

        // Si chaine ok
        if ((dn != null) && (dn.length() > 0)) {
            StringBuffer b = new StringBuffer();

            for (int i = 0; i < dn.length(); i++) {
                char c = dn.charAt(i);

                if (c == ',') {
                    b.append('"');
                }

                b.append(c);

                if (c == '=') {
                    b.append('"');
                }
            }

            b.append('"');
            dn = b.toString();
        }

        return dn;
    }
    /*
     * public static void main(String[] args){ //mairieLdap = new MairieLDAP();
     *
     * Hashtable<String, String> parameters = new Hashtable<String, String>();
     * parameters.put("INITCTX_LDAP", "com.sun.jndi.ldap.LdapCtxFactory");
     * parameters.put("HOST_LDAP", "ldap://hurle:389/");
     * parameters.put("BASE_LDAP", "dc=site-mairie,dc=noumea,dc=nc");
     * parameters.put("HOST_LDAP_ADMIN",
     * "cn=***REMOVED***,ou=WAS,ou=APPLI,ou=Z-users");
     * parameters.put("HOST_LDAP_PASSWORD", "***REMOVED***");
     * parameters.put("CRITERE_RECHERCHE_LDAP", "samaccountname");
     *
     * Hashtable<String,String> test =
     * MairieLDAP.chercherUserLDAPAttributs(parameters, "salad74");
     * logger.info("Nombre d'attributs remontés : " + test.size() + "");
}
     */
}
