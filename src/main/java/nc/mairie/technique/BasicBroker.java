package nc.mairie.technique;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.sql.DataSource;

/**
 * InsÃ©rez la description du type ici. Date de crÃ©ation : (16/10/2002 11:25:10)
 * 
 * @author:
 */
public abstract class BasicBroker implements Cloneable {
	public static final String AS400ServerName = "robin";
	public static final String INITIAL_CONTEXT_FACTORY = "com.ibm.ejs.ns.jndi.CNInitialContextFactory";
	public static final String TOMCAT_JDBC_CONTEXT = "java:comp/env/jdbc/";
	public static final String STANDARD_JDBC_CONTEXT = "jdbc/";

	// POUR WAS 5 public static final String INITIAL_CONTEXT_FACTORY =
	// "com.ibm.websphere.naming.WsnInitialContextFactory";

	protected static javax.naming.Context initialContext = null;
	private static Hashtable<String, DataSource> hashDataSource = null;
	private BasicMetier myBasicMetier;

	private String nomTable;
	private Hashtable<String, BasicRecord> mappageTable;

	private BasicRecord identityBasicRecord;

	public BasicRecord getIdentityBasicRecord() throws Exception {

		if (identityBasicRecord == null) {

			Enumeration<BasicRecord> enume = getMappageTable().elements();

			BasicRecord aBasicRecord;
			while (enume.hasMoreElements()) {
				aBasicRecord = enume.nextElement();

				// Si on trouve un identity
				if ("IDENTITY".equals(aBasicRecord.getTypeAttribut())) {
					identityBasicRecord = aBasicRecord;
					break;
				}
			}

		}
		return identityBasicRecord;
	}

	/**
	 * Commentaire relatif au constructeur BasicBroker.
	 */
	public BasicBroker(BasicMetier aMetier) {
		super();
		setMyBasicMetier(aMetier);
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Methode Ã  implÃ©menter et qui retourne le nom de la table de l'objet
	 * mÃ©tier et sa valeur
	 * 
	 * @author Luc Bourdil
	 */
	private Hashtable<String, String> construitColonneValeur(boolean recupIdentity) throws Exception {
		Hashtable<String, String> result = new Hashtable<String, String>();
		String aColonne = new String();
		String aValeur = new String();
		String aType = new String();
		String aTypeAttribut = new String();
		java.lang.reflect.Field aField = null;

		Enumeration<BasicRecord> enume = getMappageTable().elements();

		while (enume.hasMoreElements()) {
			BasicRecord aBasicRecord = enume.nextElement();
			aColonne = aBasicRecord.getNomChamp();
			aType = aBasicRecord.getTypeChamp();
			aField = aBasicRecord.getAttribut();
			aTypeAttribut = aBasicRecord.getTypeAttribut();

			// Si on ne veut pas identity, et que le type d'attribut est
			// identity, on ne le met pas
			if (aTypeAttribut.equals("IDENTITY") && !recupIdentity)
				continue;

			// Si le type est boolean
			if (aField.getType().equals(boolean.class)) {
				try {
					if (aField.getBoolean(getMyBasicMetier()))
						aValeur = "1";
					else
						aValeur = "0";
				} catch (Exception getBoolean) {
					throw new Exception("Exception dans 'construitColonnesValeur' pendant getBoolean pour " + aColonne + " : " + getBoolean);
				}
				// Dans les autres cas
			} else {
				try {
					aValeur = (String) aField.get(getMyBasicMetier());
				} catch (Exception get) {
					throw new Exception("Exception dans 'construitColonnesValeur' pendant get pour " + aColonne + " : " + get);
				}

			}

			// Si attribut type Date et champ numÃ©rique
			if (aTypeAttribut.equals("DATE") && aType.equals("NUMERIC")) {
				aValeur = (Services.estUneDate(aValeur) ? Services.convertitDate(Services.formateDate(aValeur), "dd/MM/yy", "yyyyMMdd") : "0");
				// Si type Date
			} else if (aType.equals("DATE")) {
				if (aValeur == null || aValeur.length() == 0) {
					aValeur = Services.stringForBase("0001-01-01");
				} else {
					aValeur = Services.formateDateInternationale(Services.formateDate(aValeur));
					aValeur = Services.stringForBase(aValeur);
				}
				// Si la valeur est nulle, on affecte la chaine "null"
			} else if (aValeur == null) {
				aValeur = "null";
				// Si le type dans la base n'est pas un entier
			} else if (!"NUMERIC".equals(aType) && !"DECIMAL".equals(aType) && !"INTEGER".equals(aType)) {
				// On formate pour la base (on crÃ©Ã© '' pour ') et on met 'xxx'
				aValeur = Services.stringForBase(aValeur);
			} else {
				if (aValeur.length() == 0)
					aValeur = "null";
				else
					// Remplacement de la , en .
					aValeur = aValeur.replace(',', '.');
			}

			// On ajoute
			result.put(aColonne, aValeur);
		}
		return result;
	}

	/**
	 * Methode gÃ©nÃ©rique qui enregistre en base de donnÃ©e l'objet mÃ©tier en
	 * cours.
	 * 
	 * @author Luc Bourdil
	 */
	protected boolean creer(Transaction aTransaction) throws Exception {
		java.sql.Connection conn = null;
		try {
			conn = aTransaction.getConnection();

			// On rÃ©cupÃ¨re une HashTable qui contient le nom de colonne et sa
			// valeur
			Hashtable<String, String> colonneValeur = construitColonneValeur(false);

			// Je construit la liste des champs
			String clauseColonnes = "(";
			String clauseValues = "(";
			Enumeration<String> e = colonneValeur.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				clauseColonnes = clauseColonnes + key + ",";
				clauseValues = clauseValues + (String) colonneValeur.get(key) + ",";
			}

			// On enlÃ¨ve la derniÃ¨re virgule
			clauseColonnes = clauseColonnes.substring(0, clauseColonnes.length() - 1) + ")";
			clauseValues = clauseValues.substring(0, clauseValues.length() - 1) + ")";

			// On lance la requete
			java.sql.Statement stmt = conn.createStatement();
			stmt.executeUpdate("insert into " + getTable() + " " + clauseColonnes + " values " + clauseValues, Statement.RETURN_GENERATED_KEYS);

			BasicRecord identityBR = getIdentityBasicRecord();
			// Si un champ identity a Ã©tÃ© dÃ©fini, on va le rÃ©cupÃ©rer
			if (identityBR != null) {

				// Si un champ identity a Ã©tÃ© gÃ©nÃ©rÃ©
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					String generatedKey = rs.getString(1);
					identityBR.getAttribut().set(getMyBasicMetier(), generatedKey);
				}
			}

			stmt.close();

			// Rajout du propertychange
			aTransaction.addPropertyChangeListener(getMyBasicMetier());

			// RAZ du metier base
			getMyBasicMetier().razBasicMetierBase();

		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
				conn.close();
			}
			throw new Exception("Exception dans 'creer' du basicBroker pour la classe " + getClass().getName() + " : " + e);
		}

		return true;

	}

	/**
	 * Methode Ã  dÃ©finir dans chaque objet Broker pour dÃ©clarer la liste des
	 * colonnes et leur type
	 * 
	 * @author Luc Bourdil
	 */
	protected abstract Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException;

	/**
	 * Methode Ã  dÃ©finir dans chaque objet Broker pour pouvoir instancier un
	 * mÃ©tier
	 * 
	 * @author Luc Bourdil
	 * @param <E>
	 */
	protected abstract BasicMetier definirMyMetier();

	/**
	 * Methode Ã  dÃ©finir dans chaque objet Broker pour dÃ©clarer le nom de la
	 * table
	 * 
	 * @author Luc Bourdil
	 */
	protected abstract String definirNomTable();

	/**
	 * Cette mÃ©thode permet de compter un nombre d'enregistrements. Le paramÃ¨tre
	 * 'requeteSQL' est du type "select count(*) from ....." La mÃ©thode retourne
	 * -1 si une erreur a Ã©tÃ© rencontrÃ©e.
	 * 
	 * @author Luc Bourdil
	 * @return rÃ©sultat de la requete
	 */
	protected int executeCompter(Transaction aTransaction, String requeteSQL) throws Exception {

		try {
			String nombre = executeCumuler(aTransaction, requeteSQL);
			int intNombre;
			try {
				intNombre = Integer.parseInt(nombre);
			} catch (Exception pasInteger) {
				intNombre = -1;
			}

			return intNombre;

		} catch (Exception e) {
			throw new Exception("Exception dans 'executeCompter' du basicBroker avec la requete " + requeteSQL + ": " + e);
		}

	}

	/**
	 * Cette mÃ©thode permet de compter un nombre d'enregistrements. Le paramÃ¨tre
	 * 'requeteSQL' est du type "select count(*) from ....." La mÃ©thode retourne
	 * null si une erreur a Ã©tÃ© rencontrÃ©e.
	 * 
	 * @author Luc Bourdil
	 * @return rÃ©sultat de la requete
	 */
	protected String executeCumuler(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermÃ©e ou nulle.");
			}

			// Nombre Ã  retourner
			String nombre = "";

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet result = stmt.executeQuery(requeteSQL);

			// Si pas trouvÃ©
			if (!result.next()) {

				// Fermeture du resultSet
				result.close();
				stmt.close();

				aTransaction.declarerErreur("La requete '" + requeteSQL + "' n'a ramenÃ© aucun Ã©lÃ©ment.");
				return null;
			}

			// S'il y a plus de 1 colonnes alors ce n'est pas une bonne requette
			if (result.getMetaData().getColumnCount() != 1) {
				// Fermeture du resultSet
				result.close();
				stmt.close();

				aTransaction.declarerErreur("La requete '" + requeteSQL + "' ne doit pas ramener plusieurs colonnes.");
				return null;
			}

			// On alimente l'objet Ã  retourner
			nombre = result.getString(1);

			// Fermeture du resultSet
			result.close();
			stmt.close();

			// Retourne l'objet
			return nombre;
		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				aTransaction.rollbackTransaction();
				conn.close();
			}
			throw new Exception("Exception dans 'executeCumuler' du basicBroker avec la requete " + requeteSQL + ": " + e);
		}

	}

	/**
	 * Execute une requÃªte SQL et mappe le rÃ©sultat de la requÃªte dans l'objet.
	 * 
	 * @author Luc Bourdil
	 * @param Transaction
	 * @param String
	 *            requÃªte SQL
	 * @return BasicMetier
	 */
	protected BasicMetier executeSelect(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermÃ©e ou nulle.");
			}

			// Objet Ã  retourner
			BasicMetier aBasicMetier = definirMyMetier();

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet result = stmt.executeQuery(requeteSQL);

			// Si pas trouvÃ©
			if (!result.next()) {

				// Fermeture du resultSet
				result.close();
				stmt.close();

				aTransaction.declarerErreur(MairieMessages.getMessage("ERR998"));
				return aBasicMetier;
			}

			// On alimente l'objet Ã  retourner
			mappeResultToMetier(result, aBasicMetier);

			// Fermeture du resultSet
			result.close();
			stmt.close();

			// MÃ©morise le mÃ©tier base
			aBasicMetier.majBasicMetierBase();

			// Retourne l'objet mÃ©tier
			return aBasicMetier;

		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				aTransaction.rollbackTransaction();
				conn.close();
			}
			throw new Exception("Exception dans 'executeSelect' du basicBroker avec la requete " + requeteSQL + ": " + e);
		}

	}

	/**
	 * Execute une requÃªte SQL et mappe le rÃ©sultat de la requÃªte dans un
	 * tableau de BAsicMetier.
	 * 
	 * @author Luc Bourdil
	 * @param <E>
	 * @param Transaction
	 * @param String
	 *            requÃªte SQL
	 * @return BasicMetier
	 */
	protected <E extends BasicMetier> ArrayList<E> executeSelectListe(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		ArrayList<E> result = new ArrayList<E>();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermÃ©e ou nulle.");
			}

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(requeteSQL);

			// Alimentation des objets
			while (rs.next()) {
				@SuppressWarnings("unchecked")
				E aBasicMetier = (E)definirMyMetier();
				mappeResultToMetier(rs, aBasicMetier);

				// MÃ©morise le mÃ©tier base
				aBasicMetier.majBasicMetierBase();

				// Ajoute l'objet mÃ©tier
				result.add(aBasicMetier);
			}

			// Fermeture du resultSet
			rs.close();
			stmt.close();

			// Retourne la liste des objet ou un vecteur vide
			return result;
		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				aTransaction.rollbackTransaction();
				conn.close();
			}
			throw new Exception("Exception dans 'executeSelectListe' du basicBroker avec la requete " + requeteSQL + " : " + e);
		}

	}

	/**
	 * Met Ã  jour un Insert, update ou delete
	 * 
	 * @author Luc Bourdil
	 * @return boolean
	 */
	protected boolean executeTesteExiste(Transaction aTransaction, String requeteSQL) throws Exception {

		java.sql.Connection conn = aTransaction.getConnection();
		boolean existe = true;
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermÃ©e ou nulle.");
			}

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet result = stmt.executeQuery(requeteSQL);
			existe = result.next();

			// Fermeture du resultSet
			result.close();
			stmt.close();

		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				aTransaction.rollbackTransaction();
				conn.close();
			}
			throw new Exception("Exception dans 'executeTesteExiste' du BasicBroker avec la requete " + requeteSQL + " : " + e);
		}
		return existe;
	}

	/**
	 * Met Ã  jour un Insert, update ou delete
	 * 
	 * @author Luc Bourdil
	 * @return boolean
	 */
	protected boolean executeUpdate(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermÃ©e ou nulle.");
			}

			java.sql.Statement stmt = conn.createStatement();
			stmt.executeUpdate(requeteSQL);
			stmt.close();
			return true;
		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				aTransaction.rollbackTransaction();
				conn.close();
			}
			throw new Exception("Exception dans 'executeUpdate' du BasicBroker avec la requete " + requeteSQL + " : " + e);
		}
	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. Date de crÃ©ation : (17/10/2002
	 * 13:37:12) pour jboss et websphere
	 * 
	 * @author Luc Bourdil
	 * @return javax.sql.DataSource
	 */
	private static javax.sql.DataSource getDataSourceDefault(String serveurName) throws Exception {
		if (getHashDataSource().get(serveurName) == null) {
			try {

				System.out.println("Serveur name : " + STANDARD_JDBC_CONTEXT + serveurName);
				getHashDataSource().put(serveurName, (DataSource) getInitialContext().lookup(STANDARD_JDBC_CONTEXT + serveurName));
				System.out.println("serveur ok (jboss ou websphere) : [" + STANDARD_JDBC_CONTEXT + serveurName + "]");
			} catch (Exception e) {
				throw e;
			}
		}
		return (javax.sql.DataSource) getHashDataSource().get(serveurName);
	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. Date de crÃ©ation : (17/10/2002
	 * 13:37:12) pour jboss et websphere
	 * 
	 * @author Luc Bourdil
	 * @return javax.sql.DataSource
	 */
	private static javax.sql.DataSource getDataSource(String serveurName) throws Exception {
		try {
			return getDataSourceTomcat(serveurName);
		} catch (Exception e) {
			try {
				return getDataSourceDefault(serveurName);
			} catch (Exception ex) {
				System.err.println("Aucun datasource envisagÃ© : " + ex.getMessage());
				throw ex;
			}
		}
	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. pour tomcat 7 Date de crÃ©ation
	 * : (17/10/2002 13:37:12)
	 * 
	 * @author Luc Bourdil
	 * @return javax.sql.DataSource
	 */
	private static javax.sql.DataSource getDataSourceTomcat(String serveurName) throws Exception {
		if (getHashDataSource().get(serveurName) == null) {
			try {
				System.out.println("Serveur name : " + TOMCAT_JDBC_CONTEXT + serveurName);
				/*InitialContext ctx = new InitialContext();
				DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/"+serveurName);
				Connection con = ds.getConnection();
				System.out.println("Connexion : " + con.getMetaData().getDatabaseMajorVersion())*/
				getHashDataSource().put(serveurName, (DataSource) getInitialContext().lookup(TOMCAT_JDBC_CONTEXT + serveurName));
				System.out.println("serveur ok (tomcat) : [" + TOMCAT_JDBC_CONTEXT + serveurName + "]");
			} catch (Exception e) {
				throw e;
			}
		}
		return (javax.sql.DataSource) getHashDataSource().get(serveurName);
	}

	/**
	 * Transforme une date rÃ©cupÃ©rÃ©e de la base au format SSAA/MM/JJ vers le
	 * format JJ/MM/SSAA
	 */
	public static String getFrenchFormattedDate(String pDate) {
		if (pDate.length() > 0 && pDate != null)
			return pDate.substring(8, 10) + "/" + pDate.substring(5, 7) + "/" + pDate.substring(0, 4) + " " + pDate.substring(11, 19);
		else
			return pDate;
	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. Date de crÃ©ation : (07/05/2004
	 * 11:10:05)
	 * 
	 * @return Hashtable
	 */
	private static Hashtable<String, DataSource> getHashDataSource() {
		if (hashDataSource == null) {
			hashDataSource = new Hashtable<String, DataSource>();
		}
		return hashDataSource;
	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. Date de crÃ©ation : (17/10/2002
	 * 13:36:26)
	 * 
	 * @author Luc Bourdil
	 * @return javax.naming.Context
	 */
	private static javax.naming.Context getInitialContext() throws Exception {
		if (initialContext == null) {
			try {
				/*
				 * Hashtable parms = new Hashtable();
				 * parms.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
				 * INITIAL_CONTEXT_FACTORY); initialContext = new
				 * javax.naming.InitialContext(parms);
				 */
				initialContext = new javax.naming.InitialContext();
			} catch (Exception e) {
				System.err.println("Exception dans 'getInitialContext' : " + e);
				throw e;
			}
		}
		return initialContext;
	}

	/**
	 * Retourne les colonnes de la table
	 * 
	 * @author Luc Bourdil Date de crÃ©ation : (04/12/2002 14:19:26)
	 */
	protected Hashtable<String, BasicRecord> getMappageTable() throws NoSuchFieldException {
		if (mappageTable == null) {
			mappageTable = definirMappageTable();
		}
		return mappageTable;
	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. Date de crÃ©ation : (18/11/2002
	 * 09:21:37)
	 * 
	 * @author Luc Bourdil
	 * @return nc.mairie.technique.BasicMetier
	 */
	public BasicMetier getMyBasicMetier() {
		return myBasicMetier;
	}

	/**
	 * Methode Ã  implÃ©menter et qui retourne le nom de la table de l'objet
	 * mÃ©tier
	 * 
	 * @author Luc Bourdil
	 */
	private String getNomTable() {
		if (nomTable == null) {
			nomTable = definirNomTable();
		}
		return nomTable;
	}

	/**
	 * Methode qui retourne le nom de la table de l'objet mÃ©tier
	 * 
	 * @author Luc Bourdil
	 */
	public String getTable() {
		return getNomTable();
	}

	/**
	 * Retourne une connection au SGBD pour un user/pwd donnÃ©
	 * 
	 * @author Luc Bourdil
	 */
	public static Connection getUneConnexion(String nom, String password, String serveurName) throws Exception {
		Connection conn = null;
		try {

			try {
				// jboss ou tomcat
				conn = getDataSource(serveurName).getConnection();
			} catch (Exception e) {
				try {
					// Si param dÃ©bile alors on retourne null
					if (nom == null || password == null)
						return null;
					// websphere
					conn = getDataSource(serveurName).getConnection(nom, password);
				} catch (Exception ex) {
					System.err.println("Exception dans getUneConnexion (nom,password) : " + ex);
					throw ex;
				}
			}
			System.out.println("Connexion : " + conn.getMetaData().getUserName());

			conn.setAutoCommit(false);
			// EnlevÃ© le 05/09/11 par LB car pas en mode transactionnel !!!
			// conn.setTransactionIsolation(
			// java.sql.Connection.TRANSACTION_NONE);
		} catch (Exception e) {
			System.err.println("Exception dans getUneConnexion : " + e);
			throw e;

		}
		return conn;
	}

	/**
	 * Retourne une connection au SGBD pour un userrAppli donnÃ©
	 * 
	 * @author Luc Bourdil
	 */
	public static Connection getUneConnexion(UserAppli aUserAppli) throws Exception {
		// Si param dÃ©bile alors on retourne null
		if (aUserAppli == null)
			return null;

		return getUneConnexion(aUserAppli.getUserName(), aUserAppli.getUserPassword(), aUserAppli.getServerName());
	}

	/**
	 * Commentaire relatif au constructeur BasicBroker.
	 * 
	 * @author Luc Bourdil
	 */
	/*
	 * public static Connection getUneConnexionJDBC(String nom, String password)
	 * { String drv = "com.ibm.as400.access.AS400JDBCDriver"; Connection con =
	 * null; try { Class.forName(drv); con =
	 * DriverManager.getConnection("jdbc:as400://robinnw", nom, password);
	 * con.setAutoCommit(false); } catch (Exception ex) {
	 * System.out.println("erreur driver : " + ex); return null; } return con; }
	 */

	/**
	 * Fait le mappage entre un rÃ©sultset et l'objet mÃ©tier
	 * 
	 * @author Luc Bourdil
	 * @param <E>
	 */
	private <E> void mappeResultToMetier(java.sql.ResultSet result, E object) throws Exception {

		java.sql.ResultSetMetaData metaData = result.getMetaData();
		String aColonne = new String();
		String aTypeAttribut = new String();
		java.lang.reflect.Field aField = null;

		// Parcours des colonnes
		Enumeration<BasicRecord> enume = getMappageTable().elements();
		while (enume.hasMoreElements()) {
			BasicRecord aBasicRecord = enume.nextElement();
			aColonne = aBasicRecord.getNomChamp();
			aField = aBasicRecord.getAttribut();
			aTypeAttribut = aBasicRecord.getTypeAttribut();

			// On recherche l'indice de la colonne correspondant Ã  l'attribut
			int indiceColonneTable = -1;
			try {
				indiceColonneTable = result.findColumn(aColonne);
			} catch (Exception e) {
				indiceColonneTable = -1;
			}

			// Si on l'a trouvÃ© dans la table
			if (indiceColonneTable > 0) {
				// Affectation de la valeur de la table dans l'attribut
				String valeur = result.getString(indiceColonneTable);
				int typeColonne = metaData.getColumnType(indiceColonneTable);

				// Si l'attribut de l'objet est de type boolean
				if (aField.getType().equals(boolean.class))
					// On affecte le champs avec true si la valeur est Ã©gale Ã  1
					try {
						aField.setBoolean(object, valeur.equals("1"));
					} catch (Exception setBoolean) {
						throw new Exception("Exception dans 'mappeResultToMetier' pendant setBoolean de " + aField.getName() + " : " + setBoolean);
					}
				// Si l'attribut de l'objet est de type DATE mais de type
				// Numeric dans la base
				else if (aTypeAttribut.equals("DATE") && typeColonne == java.sql.Types.NUMERIC) {
					try {
						valeur = (valeur.equals("0") ? null : Services.convertitDate(valeur, "yyyyMMdd", "dd/MM/yyyy"));
						aField.set(object, valeur);
					} catch (Exception dateNumeric) {
						throw new Exception("Exception dans 'mappeResultToMetier' pendant dateNumeric " + aField.getName() + " : " + dateNumeric);
					}
				} else
					switch (typeColonne) {
					case java.sql.Types.TIMESTAMP: {
						// On rÃ©cupÃ¨re la date au format franÃ§ais et on enlÃ¨ve
						// le time
						String uneDate = new String();
						if (valeur != null && valeur.length() > 0)
							uneDate = getFrenchFormattedDate(valeur).substring(0, 10);
						try {
							aField.set(object, uneDate);
						} catch (Exception setTimeStamp) {
							throw new Exception("Exception dans 'mappeResultToMetier' pendant setTimeStamp de " + aField.getName() + " : "
									+ setTimeStamp);
						}
						break;
					}
					case java.sql.Types.DATE: {
						// On rÃ©cupÃ¨re la date au format franÃ§ais et on enlÃ¨ve
						// le time
						String uneDate = new String();
						if (valeur != null && valeur.length() > 0)
							uneDate = Services.convertitDate(result.getDate(indiceColonneTable).toString(), "yyyy-MM-dd", "dd/MM/yyyy");
						try {
							aField.set(object, uneDate);
						} catch (Exception setTimeStamp) {
							throw new Exception("Exception dans 'mappeResultToMetier' pendant setTimeStamp de " + aField.getName() + " : "
									+ setTimeStamp);
						}
						break;
					}
					case java.sql.Types.NUMERIC: {
						// On remplace les. par des virgules
						if (valeur != null)
							valeur = valeur.replace('.', ',');
						try {
							aField.set(object, valeur);
						} catch (Exception numeric) {
							throw new Exception("Exception dans 'mappeResultToMetier' pendant numericSet de " + aField.getName() + " : " + numeric);
						}

					}
					// Dans tous les autres cas, on attribut du string
					default: {
						try {
							aField.set(object, valeur);
						} catch (Exception defaultSet) {
							throw new Exception("Exception dans 'mappeResultToMetier' pendant defaultSet de " + aField.getName() + " : " + defaultSet);
						}
					}
					}

			}
		}

	}

	/**
	 * Methode gÃ©nÃ©rique qui modifie en base de donnÃ©e l'objet mÃ©tier en cours.
	 * 
	 * @author Luc Bourdil
	 */
	protected boolean modifier(Transaction aTransaction) throws Exception {

		// ContrÃ´le si l'objet vient de la base
		if (getMyBasicMetier().getBasicMetierBase() == null) {
			aTransaction.declarerErreur("Modification impossible. L'objet n'a pas Ã©tÃ© rÃ©cupÃ©rÃ© du rÃ©fÃ©rentiel.");
			return false;
		}

		java.sql.Connection conn = null;

		try {

			// Recuperation du TimeStamp
			conn = aTransaction.getConnection();

			// On rÃ©cupÃ¨re une HashTable qui contient le nom de colonne et sa
			// valeur
			Hashtable<String, String> colonneValeur = construitColonneValeur(false);
			Hashtable<String, String> colonneValeurOld = getMyBasicMetier().getBasicMetierBase().getMyBasicBroker().construitColonneValeur(true);

			// Je construit la liste des champs
			String clauseUpdate = "";
			String clauseWhere = "";
			Enumeration<String> e = colonneValeur.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String newValeur = (String) colonneValeur.get(key);
				clauseUpdate = clauseUpdate + " " + key + "=" + newValeur + ",";
			}

			e = colonneValeurOld.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				String oldValeur = (String) colonneValeurOld.get(key);
				clauseWhere = clauseWhere + " " + key + (oldValeur.equals("null") ? " is " : "=") + oldValeur + " and ";
			}

			// On enlÃ¨ve la derniÃ¨re virgule
			clauseUpdate = clauseUpdate.substring(0, clauseUpdate.length() - 1);

			// On enlÃ¨ve le dernier and
			clauseWhere = clauseWhere.substring(0, clauseWhere.length() - 5);

			// On lance la requete
			java.sql.Statement stmt = conn.createStatement();
			int nbMaj = stmt.executeUpdate("update " + getTable() + " set " + clauseUpdate + " where " + clauseWhere);
			stmt.close();

			// Rajout du propertychange
			aTransaction.addPropertyChangeListener(getMyBasicMetier());

			// Si aucune mise Ã  jour
			if (nbMaj == 0) {
				aTransaction.declarerErreur("L'objet de la table " + getTable()
						+ " n'a pu Ãªtre mis Ã  jour. Il est introuvable ou a peut-Ãªtre Ã©tÃ© modifiÃ© par quelqu'un d'autre.");
				aTransaction.rollbackTransaction();
				aTransaction.fermerConnexion();
				return false;
			}

		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
				conn.close();
			}
			throw new Exception("Exception dans 'modifier' du basicBroker pour la classe " + getClass().getName() + " : " + e);
		}

		return true;

	}

	/**
	 * InsÃ©rez la description de la mÃ©thode ici. Date de crÃ©ation : (18/11/2002
	 * 09:21:37)
	 * 
	 * @author Luc Bourdil
	 * @param newMyBAsicMetier
	 *            nc.mairie.technique.BasicMetier
	 */
	protected void setMyBasicMetier(BasicMetier newMyBasicMetier) {
		myBasicMetier = newMyBasicMetier;
	}

	/**
	 * Methode gÃ©nÃ©rique qui supprime en base de donnÃ©e l'objet mÃ©tier en cours.
	 * 
	 * @author Luc Bourdil
	 */
	protected boolean supprimer(Transaction aTransaction) throws Exception {

		// ContrÃ´le si l'objet vient de la base
		if (getMyBasicMetier().getBasicMetierBase() == null) {
			aTransaction.declarerErreur("Suppression impossible. L'objet n'a pas Ã©tÃ© rÃ©cupÃ©rÃ© du rÃ©fÃ©rentiel.");
			return false;
		}

		java.sql.Connection conn = null;

		try {

			// Recuperation du TimeStamp
			conn = aTransaction.getConnection();

			// On rÃ©cupÃ¨re une HashTable qui contient le nom de colonne et sa
			// valeur
			Hashtable<String, String> colonneValeurOld = getMyBasicMetier().getBasicMetierBase().getMyBasicBroker().construitColonneValeur(true);

			// Je construit la liste des champs
			String clauseWhere = "";
			Enumeration<String> e = colonneValeurOld.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				String oldValeur = (String) colonneValeurOld.get(key);
				clauseWhere = clauseWhere + " " + key + (oldValeur.equals("null") ? " is " : "=") + oldValeur + " and ";
			}

			// On enlÃ¨ve le dernier and
			clauseWhere = clauseWhere.substring(0, clauseWhere.length() - 5);

			// On lance la requete
			java.sql.Statement stmt = conn.createStatement();
			int nbMaj = stmt.executeUpdate("delete from " + getTable() + " where " + clauseWhere);
			stmt.close();

			// Rajout du propertychange
			aTransaction.addPropertyChangeListener(getMyBasicMetier());

			// Si aucune mise Ã  jour
			if (nbMaj == 0) {
				aTransaction.declarerErreur("L'objet de la table " + getTable()
						+ " n'a pu Ãªtre mis Ã  jour. Il est introuvable ou a peut-Ãªtre Ã©tÃ© modifiÃ© par quelqu'un d'autre.");
				aTransaction.rollbackTransaction();
				aTransaction.fermerConnexion();
				return false;
			}

		} catch (Exception e) {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
				conn.close();
			}
			throw new Exception("Exception dans 'supprimer' du basicBroker pour la classe " + getClass().getName() + " : " + e);
		}

		return true;

	}
}
