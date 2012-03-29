package nc.mairie.technique.reprise;

import java.util.Properties;

public class OracleConnection extends SuperConnection {

	public OracleConnection(Log log, String url, String user, String pwd) {
		super(log, url, user, pwd);
	}

	public OracleConnection(Log log, String baseName, Properties properties) {
		super(log, baseName, properties);
	}


    @Override
       public String getDriverClassName() {
		return "oracle.jdbc.OracleDriver";
	}

}
