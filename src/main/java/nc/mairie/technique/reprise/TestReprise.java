/*
 * Created on 30 sept. 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nc.mairie.technique.reprise;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import nc.mairie.technique.reprise.Log;
import nc.mairie.technique.reprise.MYSQLConnection;
import nc.mairie.technique.reprise.ObjetPasserelle;
import nc.mairie.technique.reprise.OracleConnection;


/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestReprise {
	
	public Properties properties = null;
	public Log log = null; 
	
	public TestReprise() {
		super();
		log= new Log(this);
	}
	
	public void passerelleEATAL () throws Exception {
		
		MYSQLConnection mySQLATALConnection = new MYSQLConnection(log, "MYSQL_EATAL", properties);
		OracleConnection oracleATALConnection = new OracleConnection(log, "ORACLE_EATAL", properties);
		
		//On se met au bon format
		oracleATALConnection.execute("ALTER SESSION SET NLS_TIMESTAMP_FORMAT='YYYY-MM-DD HH24:MI:SS.FF1'");
		
		//Rapatriement de AWA_REQUESTS
		try {
			String champsFrom [] = 
				{"REQUEST_ID","REQUEST_FROM","REQUEST_OPERATOR","REQUEST_STATE_ID","REQUESTING_DEPARTMENT_ID","CONTACT_ID","REQUEST_DATE"
				,"concat(' ',cast(date(REQUEST_DATE) as char), ' ',cast(REQUEST_TIME as char),'.0')"
				,"REQUEST_DATE_WISHED","REQUEST_DEADLINE","REQUEST_FOLLOWUP","REQUEST_ESTIMATE","REQUEST_LOCATION","REQUEST_DESCRIPTION","BUDGET_ID","BUDGET_REAL_ID","BUDGET_SECTION_ID","ATAL_PROJECT_ID","REQUEST_IP","REQUEST_HOSTNAME","REQUEST_COMMENTS","REQUEST_TO","REQUEST_RECIPIENT_COMMENTS","REQUEST_ESTIMATED_DATE","REQUEST_ATAL_NUMBER","REQUEST_ATTACHED_ATAL_NUMBER","REQUEST_NUMBER","REQUEST_SUBMIT_TIME","REQUEST_OBJECT","EQUIPMENT_ID","EQUIPMENT_TYPE_ID","REQUEST_TYPE_ID","REQUEST_NATURE_ID","PRIORITY_ID","PRIORITY_ATAL_ID","THEMATIC_ID","THEMATIC_ATAL_ID","ORIGIN_ID","DISTRICT_ID","SECTOR_ID","REQUEST_TO_FIRST","NOMENCLATURE_ID","NOMENCLATURE_TYPE_ID","REQUEST_SHIPPING_COSTS","REQUEST_HANDING_OVER","SUPPLIER_ID","CONTRACT_ID","REQUEST_ARTICLE_TYPE_ID","REQUEST_LOAN_TYPE_ID","CONTACT_NOTIFICATION_ID","REQUEST_KEYWORDS","REQUEST_PARENT_ID","REQUEST_PARENT_NUMBER","RESPONSE_PARENT_ID","REQUEST_SUBREQUESTS"
				};
			ObjetPasserelle objetFrom = new ObjetPasserelle(log,mySQLATALConnection, 
					"AWA_REQUESTS", champsFrom);
		
			//String champsDb2 [] = {"EXERCI","IDETBS","ENSCOM","NOENGJ","NLENGJU","CDDEP","MTLENJU"};   
			String champsTo [] = 
				{"REQUEST_ID","REQUEST_FROM","REQUEST_OPERATOR","REQUEST_STATE_ID","REQUESTING_DEPARTMENT_ID","CONTACT_ID","REQUEST_DATE"
				,"REQUEST_TIME"
				,"REQUEST_DATE_WISHED","REQUEST_DEADLINE","REQUEST_FOLLOWUP","REQUEST_ESTIMATE","REQUEST_LOCATION","REQUEST_DESCRIPTION","BUDGET_ID","BUDGET_REAL_ID","BUDGET_SECTION_ID","ATAL_PROJECT_ID","REQUEST_IP","REQUEST_HOSTNAME","REQUEST_COMMENTS","REQUEST_TO","REQUEST_RECIPIENT_COMMENTS","REQUEST_ESTIMATED_DATE","REQUEST_ATAL_NUMBER","REQUEST_ATTACHED_ATAL_NUMBER","REQUEST_NUMBER","REQUEST_SUBMIT_TIME","REQUEST_OBJECT","EQUIPMENT_ID","EQUIPMENT_TYPE_ID","REQUEST_TYPE_ID","REQUEST_NATURE_ID","PRIORITY_ID","PRIORITY_ATAL_ID","THEMATIC_ID","THEMATIC_ATAL_ID","ORIGIN_ID","DISTRICT_ID","SECTOR_ID","REQUEST_TO_FIRST","NOMENCLATURE_ID","NOMENCLATURE_TYPE_ID","REQUEST_SHIPPING_COSTS","REQUEST_HANDING_OVER","SUPPLIER_ID","CONTRACT_ID","REQUEST_ARTICLE_TYPE_ID","REQUEST_LOAN_TYPE_ID","CONTACT_NOTIFICATION_ID","REQUEST_KEYWORDS","REQUEST_PARENT_ID","REQUEST_PARENT_NUMBER","RESPONSE_PARENT_ID","REQUEST_SUBREQUESTS"
				};
			ObjetPasserelle objetTo = new ObjetPasserelle(log,oracleATALConnection, 
					"AWA_REQUESTS", champsTo);
			
			//objetFrom.CopieTable(objetTo,"where com.COD_ORG = 'VDN' and com.cod_bud = '01' and com.SER_DES = 1078", true);
			//On supprime avec truncate (optimisé) et pas DELETE, donc false...
			objetTo.execute("call SP_TRUNCATE_AWA_REQUESTS()");
			objetFrom.CopieTable(objetTo,"", false);

		} catch (Exception e) {
			
			throw e;
		}
		

		//Rapatriement de AWA_RESPONSES
		try {
			String champsFrom [] = 
				{"response_id","response_parent_id","request_id","response_from","response_from_responder"
				//,"response_operator","response_action_id","response_department_id","contact_id","response_date","response_description","response_comments","response_comments_mask","response_to","response_estimated_date","response_estimated_date_end","response_estimated_duration","response_real_duration","response_date_wished","response_deadline","response_number","response_time","response_object","response_type_id","thematic_atal_id","thematic_id","priority_atal_id","priority_id","budget_id","budget_section_id","atal_project_id","response_atal_numbers","response_attached_atal_numbers","equipment_id","equipment_type_id","district_id","sector_id","response_keywords"
				};
			ObjetPasserelle objetFrom = new ObjetPasserelle(log,mySQLATALConnection, 
					"AWA_RESPONSES", champsFrom);
		
			//String champsDb2 [] = {"EXERCI","IDETBS","ENSCOM","NOENGJ","NLENGJU","CDDEP","MTLENJU"};   
			String champsTo [] = 
				{"response_id","response_parent_id","request_id","response_from","response_from_responder",
				//"response_operator","response_action_id","response_department_id","contact_id","response_date","response_description","response_comments","response_comments_mask","response_to","response_estimated_date","response_estimated_date_end","response_estimated_duration","response_real_duration","response_date_wished","response_deadline","response_number","response_time","response_object","response_type_id","thematic_atal_id","thematic_id","priority_atal_id","priority_id","budget_id","budget_section_id","atal_project_id","response_atal_numbers","response_attached_atal_numbers","equipment_id","equipment_type_id","district_id","sector_id","response_keywords"
				};
			ObjetPasserelle objetTo = new ObjetPasserelle(log,oracleATALConnection, 
					"AWA_RESPONSES", champsTo);
			
			//objetFrom.CopieTable(objetTo,"where com.COD_ORG = 'VDN' and com.cod_bud = '01' and com.SER_DES = 1078", true);
			//On supprime avec truncate (optimisé) et pas DELETE, donc false...
			objetTo.execute("call SP_TRUNCATE_AWA_RESPONSES()");
			objetFrom.CopieTable(objetTo,"", false);

		} catch (Exception e) {
			
			throw e;
		}

		//Rapatriement de AWA_USERS
		try {
			String champsFrom [] = 
				{"user_id","user_active","user_name","user_login","user_password","user_lastvisit","user_regdate","role_id","user_style","user_notify","user_rank","user_email","user_from","user_newpasswd","user_telephone","user_telecopie","service_id","user_service_levels","user_role_levels","user_password_change","user_responders_active","user_responders_change","user_access_mode","user_language","user_language_change","user_dn"
				};
			ObjetPasserelle objetFrom = new ObjetPasserelle(log,mySQLATALConnection, 
					"AWA_USERS", champsFrom);
		
			//String champsDb2 [] = {"EXERCI","IDETBS","ENSCOM","NOENGJ","NLENGJU","CDDEP","MTLENJU"};   
			String champsTo [] = 
				{"user_id","user_active","user_name","user_login","user_password","user_lastvisit","user_regdate","role_id","user_style","user_notify","user_rank","user_email","user_from","user_newpasswd","user_telephone","user_telecopie","service_id","user_service_levels","user_role_levels","user_password_change","user_responders_active","user_responders_change","user_access_mode","user_language","user_language_change","user_dn"
				};
			ObjetPasserelle objetTo = new ObjetPasserelle(log,oracleATALConnection, 
					"AWA_USERS", champsTo);
			
			//objetFrom.CopieTable(objetTo,"where com.COD_ORG = 'VDN' and com.cod_bud = '01' and com.SER_DES = 1078", true);
			//On supprime avec truncate (optimisé) et pas DELETE, donc false...
			objetTo.execute("call SP_TRUNCATE_AWA_USERS()");
			objetFrom.CopieTable(objetTo,"", false);

		} catch (Exception e) {
			
			throw e;
		}

		
		//on close tout
		oracleATALConnection.getConnection().commit();
		oracleATALConnection.getConnection().close();
		mySQLATALConnection.getConnection().commit();
		mySQLATALConnection.getConnection().close();
	}

	
	public void run(String args[]) throws Exception {
		//init
		init();
		
		passerelleEATAL();			
		System.exit(1);
	
	}
	
	public static void main (String args[])  {
		TestReprise passerelle = new TestReprise();
		try {

			//log démarrage
			String txt = "Démarrage de la passerelle par "+System.getProperty("user.name");
			passerelle.log("--------------------------------------------------------");
			passerelle.log(txt);
			
			passerelle.run(args);
			passerelle.log("Fin normale");
		} catch (Exception e) {
			passerelle.log(e);
		}
	}
	
	public void log(String message) {
		log.log(message);
	}
	public void log(Exception e) {
		log.log(e);
	}

	public void init() {
	//	lecture des propriétés
		try {
			properties = new Properties();
			
			String root = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
			if (root.toUpperCase().endsWith("JAR") || root.toUpperCase().endsWith("CLASS") ) {
				root=root.substring(0, root.lastIndexOf('/') +1);
			}
			String className = getClass().getName().substring(getClass().getName().lastIndexOf(".")+1);
			
			InputStream is = new FileInputStream(root+className+".properties");
			properties.load(is);
			is.close();
			log("Lecture des propriétés : "+properties);
	      
		} catch (Exception e) {
			log("Impossible de lire le fichier properties : "+e.getMessage());
			System.exit(1);
		}
	}
	
}
