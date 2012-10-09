package nc.mairie.technique.reprise;

import java.util.Properties;

public class DB2Connection extends SuperConnection {

	public DB2Connection(Log log, String url, String user, String pwd) {
		super(log, url, user, pwd);
		
	}

	public DB2Connection(Log log, String baseName, Properties properties) {
		super(log, baseName, properties);
		
	}

    @Override
	public String getDriverClassName() {
		return "com.ibm.as400.access.AS400JDBCDriver";
	}

}
