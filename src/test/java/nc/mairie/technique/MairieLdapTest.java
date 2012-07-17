/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.mairie.technique;

import java.util.*;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;




/**
 *
 * @author salad74
 */
public class MairieLdapTest {

    //private MairieLDAP mairieLdap  = null;
    //private static Logger logger = LoggerFactory.getLogger(MairieLdapTest.class);
    private final static Logger logger = Logger.getLogger(MairieLdapTest.class .getName());
    
    private Hashtable<String, String> parameters = null;
    private Configuration configuration = null;

    @Before
    public void setParameters() {
        Exception confException = null;
        String lINITCTX_LDAP = null;
        String lHOST_LDAP = null;
        String lBASE_LDAP = null;
        String lHOST_LDAP_ADMIN = null;
        String lHOST_LDAP_PASSWORD = null;
        String lCRITERE_RECHERCHE_LDAP = null;

        try {
            
            configuration = new PropertiesConfiguration("etc/test.properties");
            
            //logger.info("Test filename : " + System.getProperty("test.properties"));
            
            lINITCTX_LDAP = configuration.getString(MairieLDAP.INITCTX_LDAP);
            lHOST_LDAP = configuration.getString(MairieLDAP.HOST_LDAP);
            lBASE_LDAP = configuration.getString(MairieLDAP.BASE_LDAP);
            lHOST_LDAP_ADMIN = configuration.getString(MairieLDAP.HOST_LDAP_ADMIN);
            lHOST_LDAP_PASSWORD = configuration.getString(MairieLDAP.HOST_LDAP_PASSWORD);
            lCRITERE_RECHERCHE_LDAP = configuration.getString(MairieLDAP.CRITERE_RECHERCHE_LDAP);

            // Test parameters
            Assert.assertNotNull(MairieLDAP.INITCTX_LDAP + " n'est pas correctement renseigné.", lINITCTX_LDAP);
            Assert.assertNotNull(MairieLDAP.HOST_LDAP + " n'est pas correctement renseigné.", lHOST_LDAP);
            Assert.assertNotNull(MairieLDAP.BASE_LDAP + " n'est pas correctement renseigné.", lBASE_LDAP);
            Assert.assertNotNull(MairieLDAP.HOST_LDAP_ADMIN + " n'est pas correctement renseigné.", lHOST_LDAP_ADMIN);
            Assert.assertNotNull(MairieLDAP.HOST_LDAP_PASSWORD + " n'est pas correctement renseigné.", lHOST_LDAP_PASSWORD);
            Assert.assertNotNull(MairieLDAP.CRITERE_RECHERCHE_LDAP + " n'est pas correctement renseigné.", lCRITERE_RECHERCHE_LDAP);


            parameters = new Hashtable<String, String>();
            parameters.put("INITCTX_LDAP", lINITCTX_LDAP);
            parameters.put("HOST_LDAP", lHOST_LDAP);
            parameters.put("BASE_LDAP", lBASE_LDAP);
            logger.info("BASE_LDAP = " + lBASE_LDAP);
            
            parameters.put("HOST_LDAP_ADMIN", lHOST_LDAP_ADMIN);
            parameters.put("HOST_LDAP_PASSWORD", lHOST_LDAP_PASSWORD);
            parameters.put("CRITERE_RECHERCHE_LDAP", lCRITERE_RECHERCHE_LDAP);

        } catch (Exception ex) {
            confException = ex;
            logger.severe("Impossible d'ouvrir le fichier de conf pour effectuer les tests : " + ex.getMessage());
        }
        Assert.assertNull(confException);




    }

    @Test
    public void testChercherUserLDAPAttributs() {
        //String sAmaccountName = "salad74";
        String[] sAmaccountNames = configuration.getStringArray("users");
        
        
        
        Hashtable<String, String> test = null;
        
        Assert.assertTrue("la liste des logins de test n'est pas renseignée (champ [users], il faut renseigner au moins un user dans le fichier de properties", sAmaccountNames != null);
        Assert.assertTrue(sAmaccountNames.length > 0);
        List<String> usersList = Arrays.asList(sAmaccountNames);
        Iterator<String> usersIter = usersList.iterator();
        String sAmaccountName = null;
        while (usersIter.hasNext()) {
            sAmaccountName = usersIter.next();
            logger.info("Test sur [" + sAmaccountName + "] ...");
            test = MairieLDAP.chercherUserLDAPAttributs(parameters, sAmaccountName);
            Assert.assertTrue("Impossible de trouver l'entrée sAmaccountName = [" + sAmaccountName + "] ", test != null);
            if (test != null) {
                Assert.assertTrue(test.size() > 0);
                logger.fine(test + "");
            }
        }

    }
    
    @Test
    public void testcontrolerHabilitation(){
        Assert.assertTrue(true);
        // récupère la liste des users dans [authenticate] et fait des tests d'authentification sur chacun d'entre eux
    }
    
    @Test
    public void testLdapServersRandomList(){
        
        logger.fine("Input ldap servers from conf : [" + parameters.get(MairieLDAP.HOST_LDAP) + "]");
        logger.fine("randomized list : [" + MairieLDAP.randomizeLdapServersList(parameters.get(MairieLDAP.HOST_LDAP)));
        Assert.assertTrue(true);
    }
    
}
