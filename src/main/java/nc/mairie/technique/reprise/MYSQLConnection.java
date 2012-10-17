package nc.mairie.technique.reprise;

import java.util.Properties;

public class MYSQLConnection extends SuperConnection {

	public MYSQLConnection(Log log, String url, String user, String pwd) {
		super(log, url, user, pwd);
		// TODO Raccord de constructeur auto-généré
	}

	public MYSQLConnection(Log log, String baseName, Properties properties) {
		super(log, baseName, properties);
		// TODO Raccord de constructeur auto-généré
	}

	public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

}
