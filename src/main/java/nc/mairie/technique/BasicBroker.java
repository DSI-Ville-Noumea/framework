package nc.mairie.technique;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Insérez la description du type ici. Date de création : (16/10/2002 11:25:10)
 * 
 * @author:
 */
public abstract class BasicBroker implements Cloneable {
	private final static Logger logger = Logger.getLogger(BasicBroker.class.getName());
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
	 * @param aMetier aMetier 
	 */
	public BasicBroker(BasicMetier aMetier) {
		super();
		setMyBasicMetier(aMetier);
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Methode à implémenter et qui retourne le nom de la table de l'objet
	 * métier et sa valeur
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

			// Si attribut type Date et champ numérique
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
				// On formate pour la base (on créé '' pour ') et on met 'xxx'
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
	 * Methode générique qui enregistre en base de donnée l'objet métier en
	 * cours.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction 
	 * @return boolean
	 * @throws Exception exception 
	 */
	protected boolean creer(Transaction aTransaction) throws Exception {
		java.sql.Connection conn = null;
		try {
			conn = aTransaction.getConnection();

			// On récupère une HashTable qui contient le nom de colonne et sa
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

			// On enlève la dernière virgule
			clauseColonnes = clauseColonnes.substring(0, clauseColonnes.length() - 1) + ")";
			clauseValues = clauseValues.substring(0, clauseValues.length() - 1) + ")";

			// On lance la requete
			java.sql.Statement stmt = conn.createStatement();
			stmt.executeUpdate("insert into " + getTable() + " " + clauseColonnes + " values " + clauseValues, Statement.RETURN_GENERATED_KEYS);

			BasicRecord identityBR = getIdentityBasicRecord();
			// Si un champ identity a été défini, on va le récupérer
			if (identityBR != null) {

				// Si un champ identity a été généré
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
	 * Methode à définir dans chaque objet Broker pour déclarer la liste des
	 * colonnes et leur type
	 * 
	 * @author Luc Bourdil
	 * @return Hashtable<String, BasicRecord>
	 * @throws NoSuchFieldException exception
	 */
	protected abstract Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException;

	/**
	 * Methode à définir dans chaque objet Broker pour pouvoir instancier un
	 * métier
	 * 
	 * @author Luc Bourdil
	 * @return BasicMetier
	 */
	protected abstract BasicMetier definirMyMetier();

	/**
	 * Methode à définir dans chaque objet Broker pour déclarer le nom de la
	 * table
	 * 
	 * @author Luc Bourdil
	 * @return String
	 */
	protected abstract String definirNomTable();

	/**
	 * Cette méthode permet de compter un nombre d'enregistrements. Le paramètre
	 * 'requeteSQL' est du type "select count(*) from ....." La méthode retourne
	 * -1 si une erreur a été rencontrée.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction  
	 * @param requeteSQL requeteSQL
	 * @return int
	 * @throws Exception exception
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
	 * Cette méthode permet de cumuler un nombre d'enregistrements. Le paramètre
	 * 'requeteSQL' est du type "select sum(*) from ....." La méthode retourne
	 * null si une erreur a été rencontrée.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction
	 * @param requeteSQL requeteSQL 
	 * @return String
	 * @throws Exception Exception 
	 */
	protected String executeCumuler(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermée ou nulle.");
			}

			// Nombre à retourner
			String nombre = "";

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet result = stmt.executeQuery(requeteSQL);

			// Si pas trouvé
			if (!result.next()) {

				// Fermeture du resultSet
				result.close();
				stmt.close();

				aTransaction.declarerErreur("La requete '" + requeteSQL + "' n'a ramené aucun élément.");
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

			// On alimente l'objet à retourner
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
	 * Execute une requête SQL et mappe le résultat de la requête dans l'objet.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction 
	 * @param requeteSQL requeteSQL 
	 * @param Transaction Transaction
	 * @param String  requête SQL
	 * @return BasicMetier BasicMetier
	 * @throws Exception exception
	 */
	protected BasicMetier executeSelect(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermée ou nulle.");
			}

			// Objet à retourner
			BasicMetier aBasicMetier = definirMyMetier();

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet result = stmt.executeQuery(requeteSQL);

			// Si pas trouvé
			if (!result.next()) {

				// Fermeture du resultSet
				result.close();
				stmt.close();

				aTransaction.declarerErreur(MairieMessages.getMessage("ERR998"));
				return aBasicMetier;
			}

			// On alimente l'objet à retourner
			mappeResultToMetier(result, aBasicMetier);

			// Fermeture du resultSet
			result.close();
			stmt.close();

			// Mémorise le métier base
			aBasicMetier.majBasicMetierBase();

			// Retourne l'objet métier
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
	 * Execute une requête SQL et mappe le résultat de la requête dans un
	 * tableau de BAsicMetier.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction  aTransaction
	 * @param requeteSQL requête SQL
	 * @return BasicMetier basicmetier
	 * @throws Exception exception
	 */
	protected <E extends BasicMetier> ArrayList<E> executeSelectListe(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		ArrayList<E> result = new ArrayList<E>();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermée ou nulle.");
			}

			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(requeteSQL);

			// Alimentation des objets
			while (rs.next()) {
				@SuppressWarnings("unchecked")
				E aBasicMetier = (E)definirMyMetier();
				mappeResultToMetier(rs, aBasicMetier);

				// Mémorise le métier base
				aBasicMetier.majBasicMetierBase();

				// Ajoute l'objet métier
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
	 * Met à jour un Insert, update ou delete
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction 
	 * @param requeteSQL requeteSQL 
	 * @return boolean boolean
	 * @throws Exception Exception 
	 */
	protected boolean executeTesteExiste(Transaction aTransaction, String requeteSQL) throws Exception {

		java.sql.Connection conn = aTransaction.getConnection();
		boolean existe = true;
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermée ou nulle.");
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
	 * Met à jour un Insert, update ou delete
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction 
	 * @param requeteSQL requeteSQL 
	 * @return boolean boolean
	 * @throws Exception Exception 
	 */
	protected boolean executeUpdate(Transaction aTransaction, String requeteSQL) throws Exception {
		java.sql.Connection conn = aTransaction.getConnection();
		try {
			// Controle de la Transaction
			if (aTransaction.getConnection() == null || aTransaction.getConnection().isClosed()) {
				throw new Exception("La connexion de la transaction est fermée ou nulle.");
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
	 * Insérez la description de la méthode ici. Date de création : (17/10/2002
	 * 13:37:12) pour jboss et websphere
	 * 
	 * @author Luc Bourdil
	 * @return javax.sql.DataSource
	 */
	private static javax.sql.DataSource getDataSourceDefault(String serveurName) throws Exception {
		if (getHashDataSource().get(serveurName) == null) {
			try {

				logger.info("Serveur name : " + STANDARD_JDBC_CONTEXT + serveurName);
				getHashDataSource().put(serveurName, (DataSource) getInitialContext().lookup(STANDARD_JDBC_CONTEXT + serveurName));
				logger.info("serveur ok (jboss ou websphere) : [" + STANDARD_JDBC_CONTEXT + serveurName + "]");
			} catch (Exception e) {
				throw e;
			}
		}
		return (javax.sql.DataSource) getHashDataSource().get(serveurName);
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (17/10/2002
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
				logger.severe("Aucun datasource envisagé Tomcat : " + e.getMessage());
				logger.severe("Aucun datasource envisagé JBOSS/WAS : " + ex.getMessage());
				ex.initCause(e);
				throw ex;
			}
		}
	}

	/**
	 * Insérez la description de la méthode ici. pour tomcat 7 Date de création
	 * : (17/10/2002 13:37:12)
	 * 
	 * @author Luc Bourdil
	 * @return javax.sql.DataSource
	 */
	private static javax.sql.DataSource getDataSourceTomcat(String serveurName) throws Exception {
		if (getHashDataSource().get(serveurName) == null) {
			try {
				logger.info("Serveur name : " + TOMCAT_JDBC_CONTEXT + serveurName);
				/*InitialContext ctx = new InitialContext();
				DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/"+serveurName);
				Connection con = ds.getConnection();
				logger.info("Connexion : " + con.getMetaData().getDatabaseMajorVersion())*/
				getHashDataSource().put(serveurName, (DataSource) getInitialContext().lookup(TOMCAT_JDBC_CONTEXT + serveurName));
				logger.info("serveur ok (tomcat) : [" + TOMCAT_JDBC_CONTEXT + serveurName + "]");
			} catch (Exception e) {
				throw e;
			}
		}
		return (javax.sql.DataSource) getHashDataSource().get(serveurName);
	}

	/**
	 * Transforme une date récupérée de la base au format SSAA/MM/JJ vers le
	 * format JJ/MM/SSAA
	 * @param pDate pDate
	 * @return String
	 */
	public static String getFrenchFormattedDate(String pDate) {
		if (pDate.length() > 0 && pDate != null)
			return pDate.substring(8, 10) + "/" + pDate.substring(5, 7) + "/" + pDate.substring(0, 4) + " " + pDate.substring(11, 19);
		else
			return pDate;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (07/05/2004
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
	 * Insérez la description de la méthode ici. Date de création : (17/10/2002
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
				logger.severe("Exception dans 'getInitialContext' : " + e);
				throw e;
			}
		}
		return initialContext;
	}

	/**
	 * Retourne les colonnes de la table
	 * 
	 * @author Luc Bourdil Date de création : (04/12/2002 14:19:26)
	 * @return Hashtable<String, BasicRecord>
	 * @throws NoSuchFieldException  exception
	 */
	protected Hashtable<String, BasicRecord> getMappageTable() throws NoSuchFieldException {
		if (mappageTable == null) {
			mappageTable = definirMappageTable();
		}
		return mappageTable;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (18/11/2002
	 * 09:21:37)
	 * 
	 * @author Luc Bourdil
	 * @return nc.mairie.technique.BasicMetier
	 */
	public BasicMetier getMyBasicMetier() {
		return myBasicMetier;
	}

	/**
	 * Methode à implémenter et qui retourne le nom de la table de l'objet
	 * métier
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
	 * Methode qui retourne le nom de la table de l'objet métier
	 * 
	 * @author Luc Bourdil
	 * @return String
	 */
	public String getTable() {
		return getNomTable();
	}

	/**
	 * Retourne une connection au SGBD pour un user/pwd donné
	 * 
	 * @author Luc Bourdil
	 * @param nom nom 
	 * @param password password 
	 * @param serveurName serveurName 
	 * @return Connection
	 * @throws Exception Exception 
	 */
	public static Connection getUneConnexion(String nom, String password, String serveurName) throws Exception {
		Connection conn = null;
		try {

			try {
				// jboss ou tomcat
				conn = getDataSource(serveurName).getConnection();
			} catch (Exception e) {
				try {
					// Si param débile alors on retourne null
					if (nom == null || password == null)
						throw new Exception("nom et password sont nulls et "+e.getMessage());
					// websphere
					conn = getDataSource(serveurName).getConnection(nom, password);
				} catch (Exception ex) {
					logger.severe("Exception dans getUneConnexion (nom,password) : " + ex);
					throw ex;
				}
			}
			logger.info("Connexion : " + conn.getMetaData().getUserName());

			conn.setAutoCommit(false);
			// Enlevé le 05/09/11 par LB car pas en mode transactionnel !!!
			// conn.setTransactionIsolation(
			// java.sql.Connection.TRANSACTION_NONE);
		} catch (Exception e) {
			logger.severe("Exception dans getUneConnexion : " + e);
			throw e;

		}
		return conn;
	}

	/**
	 * Retourne une connection au SGBD pour un userrAppli donné
	 * 
	 * @author Luc Bourdil
	 * @param aUserAppli aUserAppli 
	 * @return Connection
	 * @throws Exception Exception 
	 */
	public static Connection getUneConnexion(UserAppli aUserAppli) throws Exception {
		// Si param débile alors on retourne null
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
	 * logger.info("erreur driver : " + ex); return null; } return con; }
	 */

	/**
	 * Fait le mappage entre un resultset et l'objet métier
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

			// On recherche l'indice de la colonne correspondant à l'attribut
			int indiceColonneTable = -1;
			try {
				indiceColonneTable = result.findColumn(aColonne);
			} catch (Exception e) {
				indiceColonneTable = -1;
			}

			// Si on l'a trouvé dans la table
			if (indiceColonneTable > 0) {
				// Affectation de la valeur de la table dans l'attribut
				String valeur = result.getString(indiceColonneTable);
				int typeColonne = metaData.getColumnType(indiceColonneTable);

				// Si l'attribut de l'objet est de type boolean
				if (aField.getType().equals(boolean.class))
					// On affecte le champs avec true si la valeur est égale à 1
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
						// On récupère la date au format français et on enlève
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
						// On récupère la date au format français et on enlève
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
	 * Methode générique qui modifie en base de donnée l'objet métier en cours.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction 
	 * @return boolean
	 * @throws Exception exception
	 */
	protected boolean modifier(Transaction aTransaction) throws Exception {

		// Contrôle si l'objet vient de la base
		if (getMyBasicMetier().getBasicMetierBase() == null) {
			aTransaction.declarerErreur("Modification impossible. L'objet n'a pas été récupéré du référentiel.");
			return false;
		}

		java.sql.Connection conn = null;

		try {

			// Recuperation du TimeStamp
			conn = aTransaction.getConnection();

			// On récupère une HashTable qui contient le nom de colonne et sa
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

			// On enlève la dernière virgule
			clauseUpdate = clauseUpdate.substring(0, clauseUpdate.length() - 1);

			// On enlève le dernier and
			clauseWhere = clauseWhere.substring(0, clauseWhere.length() - 5);

			// On lance la requete
			java.sql.Statement stmt = conn.createStatement();
			int nbMaj = stmt.executeUpdate("update " + getTable() + " set " + clauseUpdate + " where " + clauseWhere);
			stmt.close();

			// Rajout du propertychange
			aTransaction.addPropertyChangeListener(getMyBasicMetier());

			// Si aucune mise à jour
			if (nbMaj == 0) {
				aTransaction.declarerErreur("L'objet de la table " + getTable()
						+ " n'a pu être mis à jour. Il est introuvable ou a peut-être été modifié par quelqu'un d'autre.");
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
	 * Insérez la description de la méthode ici. Date de création : (18/11/2002
	 * 09:21:37)
	 * 
	 * @author Luc Bourdil
	 * @param newMyBasicMetier  nc.mairie.technique.BasicMetier
	 */
	protected void setMyBasicMetier(BasicMetier newMyBasicMetier) {
		myBasicMetier = newMyBasicMetier;
	}

	/**
	 * Methode générique qui supprime en base de donnée l'objet métier en cours.
	 * 
	 * @author Luc Bourdil
	 * @param aTransaction aTransaction 
	 * @return boolean
	 * @throws Exception Exception 
	 */
	protected boolean supprimer(Transaction aTransaction) throws Exception {

		// Contrôle si l'objet vient de la base
		if (getMyBasicMetier().getBasicMetierBase() == null) {
			aTransaction.declarerErreur("Suppression impossible. L'objet n'a pas été récupéré du référentiel.");
			return false;
		}

		java.sql.Connection conn = null;

		try {

			// Recuperation du TimeStamp
			conn = aTransaction.getConnection();

			// On récupère une HashTable qui contient le nom de colonne et sa
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

			// On enlève le dernier and
			clauseWhere = clauseWhere.substring(0, clauseWhere.length() - 5);

			// On lance la requete
			java.sql.Statement stmt = conn.createStatement();
			int nbMaj = stmt.executeUpdate("delete from " + getTable() + " where " + clauseWhere);
			stmt.close();

			// Rajout du propertychange
			aTransaction.addPropertyChangeListener(getMyBasicMetier());

			// Si aucune mise à jour
			if (nbMaj == 0) {
				aTransaction.declarerErreur("L'objet de la table " + getTable()
						+ " n'a pu être mis à jour. Il est introuvable ou a peut-être été modifié par quelqu'un d'autre.");
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
