
package nc.mairie.technique.reprise;

import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;





/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ObjetPasserelle {
	
	private SuperConnection superConnection;
	private String table;
	private String champs[];
	private Log log;

	/**
	 * 
	 */
	public ObjetPasserelle(Log aLog, SuperConnection aSuperConnection, String aTable, String [] aChamps) {
		super();
		log = aLog;
		superConnection=aSuperConnection;
		table=aTable;
		champs=aChamps;
	}

	public Connection getConnection() throws Exception {
		
		return superConnection.getConnection();
	}
	
	public void deleteTable() throws Exception {
		Connection con = getConnection();
		Statement st = con.createStatement();
		st.executeUpdate("delete from "+table);
		st.close();
	}
	
	public void execute(String requete) throws Exception {
		superConnection.execute(requete);
	}
	
	
	private void copieTableMemeBase(ObjetPasserelle dest, String critere, boolean deleteDest) throws Exception {
		ObjetPasserelle org = this;
				
		Connection conDest = dest.getConnection();
		String insert = "insert into "+dest.table+"(";
		for (int i = 0; i < dest.champs.length; i++) {
			insert+=dest.champs[i];
			if (i+1<dest.champs.length)
				insert+=", ";
		}
		insert+=") select ";
		for (int i = 0; i < org.champs.length; i++) {
			insert+=org.champs[i];
			if (i+1<org.champs.length)
				insert+=", ";
		}
		insert += " from "+org.table;
		
		if (critere !=null) {
			insert +=" "+critere;
		}
		
		//	Si on vide avant de copier
		if (deleteDest) dest.deleteTable();

		int cpt = conDest.createStatement().executeUpdate(insert);
		log.log("Résultat : "+cpt+" lignes copiées");
				
		
	}
	
	//Copie d'une table à l'autre
	public void CopieTable(ObjetPasserelle dest, String critere, boolean deleteDest) throws Exception {
		ObjetPasserelle org = this;
		org.log.log("Debut de copie de "+org.table+" de "+org.superConnection.getDatabaseType()+" vers "+dest.table+" de "+dest.superConnection.getDatabaseType());
/*		if (org.superConnection.getBaseName().equals(dest.superConnection.getBaseName())) {
			org.copieTableMemeBase(dest,critere,deleteDest);
		} else {
*/		if (org.superConnection.getClass().equals(dest.superConnection.getClass())) {
			org.copieTableMemeBase(dest,critere,deleteDest);
		} else {
		
			Connection conOrg = org.getConnection();
			Statement stOrg = conOrg.createStatement();
			
			Connection conDest = dest.getConnection();
			String insert = "insert into "+dest.table+"(";
			for (int i = 0; i < dest.champs.length; i++) {
				insert+=dest.champs[i];
				if (i+1<dest.champs.length)
					insert+=", ";
			}
			insert+=") values (";
			for (int i = 0; i < dest.champs.length; i++) {
				insert+="?";
				if (i+1<dest.champs.length)
					insert+=", ";
			}
			insert+=")";
			
			//	Si on vide avant de copier
			if (deleteDest) dest.deleteTable();
			
			PreparedStatement stDest = conDest.prepareStatement(insert);
			
			stDest.clearBatch();
			
			String select = "select ";
			for (int i = 0; i < org.champs.length; i++) {
				select+=org.champs[i];
				if (i+1<org.champs.length)
					select+=", ";
			}
			select += " from "+org.table;
			
			if (critere !=null) {
				select +=" "+critere;
			}
			
			ResultSet rsOrg = stOrg.executeQuery(select);
//			int cpt = 0;
			while (rsOrg.next()){
				for (int i = 0; i < dest.champs.length; i++) {
					try {
						stDest.setString(i+1,rsOrg.getString(i+1));
					} catch (DataTruncation Exception) {
						//Quedalle !!!
						log.log("La valeur "+rsOrg.getString(org.champs[i])+" pour "+org.champs[i]+" est trop grand : tronqué");
					}
				}
				stDest.addBatch();
		}
			
			try {
				int res[] = stDest.executeBatch();
				if (res == null){ 
					log.log("Résultat : Aucune ligne copiée");
				} else {
					int cpt=0;
					for (int i = 0; i < res.length; i++) {
						cpt+=(res[i]==-2 ? 1 : res[i]);
					}
					log.log("Résultat : "+cpt+" lignes copiées");
				}
				
			} catch (Exception e) {
				throw e;
			}
			
			rsOrg.close();
			stOrg.close();
			stDest.close();
			
		
		}
		

		
	}

}
