package nc.mairie.technique.reprise;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public abstract class SuperConnection {

	String url = null;
	String user=null;
	String pwd = null;
	Log log = null;
	Connection con;
	
	public abstract String getDriverClassName();
	

	public SuperConnection(Log log, String url, String user, String pwd) {
		super();
		this.log = log;
		this.url = url;
		this.user = user;
		this.pwd = pwd;
		initConnection();
	}

	public SuperConnection(Log log,String baseName, Properties properties) {
		super();
		this.log = log;
		this.url = (String)properties.get(baseName+"_URL");
		this.user = (String)properties.get(baseName+"_USER");
		this.pwd = (String)properties.get(baseName+"_PWD");
		initConnection();
	}
	
	public String getDatabaseType() throws Exception {
		return getConnection().getMetaData().getDatabaseProductName();
	}

	
	private void initConnection()  {
		try {
			Class.forName(getDriverClassName());
			con = DriverManager.getConnection(url,user,pwd);
			con.setAutoCommit(false);
			log.log("Connection avec "+getDriverClassName()+ 
					" URL : "+url+
					" user :"+user+
					" pwd : "+pwd);
		} catch (Exception e) {
			log.log(e);
		}
	}
	
	public Connection getConnection() throws Exception {
		if (con == null || con.isClosed()) {
			initConnection();
		}
		return con;
	}
	
	public void execute(String requete) throws Exception {
		Connection con = getConnection();
		Statement st = con.createStatement();
		st.executeUpdate(requete);
		st.close();
	}
	
}
