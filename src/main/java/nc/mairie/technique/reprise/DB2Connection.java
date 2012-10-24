package nc.mairie.technique.reprise;

import java.util.Properties;

public class DB2Connection extends SuperConnection {

	public DB2Connection(Log log, String url, String user, String pwd) {
		super(log, url, user, pwd);
		// TODO Raccord de constructeur auto-généré
	}

	public DB2Connection(Log log, String baseName, Properties properties) {
		super(log, baseName, properties);
		// TODO Raccord de constructeur auto-généré
	}

	public String getDriverClassName() {
		return "com.ibm.as400.access.AS400JDBCDriver";
	}

}
