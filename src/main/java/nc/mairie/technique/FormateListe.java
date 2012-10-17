package nc.mairie.technique;

/**
 * Ins�rez la description du type ici.
 * Date de cr�ation : (30/10/2002 14:48:55)
 * @author: 
 */
public class FormateListe {
	//	String vide = "ALT+255"
	final String vide = "����������������������������������������������������������������������������������������������������������";

	private int[] taillesColonnes;
	private String[] padding;
	private java.util.ArrayList resultat = new java.util.ArrayList();
	private static java.util.Hashtable hash;
	private boolean separateur;
/**
 * Commentaire relatif au constructeur Liste.
 */
public FormateListe(int [] taillesColonnes) {
	this(taillesColonnes, null, false);
}
/**
 * Commentaire relatif au constructeur Liste.
 */
public FormateListe(int [] taillesColonnes, String [] padding, boolean separateur) {
	super();
	this.taillesColonnes = taillesColonnes;
	this.padding = padding;
	this.separateur = separateur;
}
/**
 * Commentaire relatif au constructeur Liste.
 */
public FormateListe(int [] taillesColonnes, java.util.ArrayList metiers, String [] nomAttributs) throws Exception {
	this(taillesColonnes, metiers, nomAttributs, false);
}
/**
 * Commentaire relatif au constructeur Liste.
 */
public FormateListe(int [] taillesColonnes, java.util.ArrayList metiers, String [] nomAttributs, String [] padding, boolean separateur) throws Exception {
	this(taillesColonnes, padding, separateur);

	java.util.Iterator it = metiers.iterator();
	while (it.hasNext()) {
		Object o = it.next();
		String [] ligne = new String [nomAttributs.length];
		for (int i = 0; i < nomAttributs.length; i++){
			String nom = nomAttributs[i];
			String val = null;
			try {
				val = (String)o.getClass().getField(nom).get(o);
			} catch (NoSuchFieldException fieldExc) {
				throw new Exception("Exception dans FormateListe : L'attribut "+nom+" est inexistant dans la classe "+o.getClass().getName());
			}
			ligne[i] = val;
		}
		ajouteLigne(ligne);
	}

}
/**
 * Commentaire relatif au constructeur Liste.
 */
public FormateListe(int [] taillesColonnes, java.util.ArrayList metiers, String [] nomAttributs, boolean separateur) throws Exception {
	this(taillesColonnes,metiers, nomAttributs, null, separateur);
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (30/10/2002 14:50:53)
 */
public void ajouteLigne(String [] aLigne) {
	//nombre de zones dans la chaine
	int nbZones = getTaillesColonnes().length;
	String ligne = "";	

	for (int j = 0; j < nbZones; j++){
			
			String champ = aLigne[j];
			if (champ == null) champ = "";
			champ = champ.trim();
			int longueur = getTaillesColonnes()[j];
			if (champ.length() > longueur) {
				champ = champ.substring(0,longueur);
			} else if (champ.length() < longueur) {
				//	Si padding = null alors on colle � gauche sinon Si D ou R � droite sinon le caract�re pass�
				char pos = (padding == null ? 'L' : (padding[j].charAt(0) == 'D' ? 'R' : padding[j].charAt(0)));
				switch (pos) {
					//ALT 255
					case 'R' : {
						champ = Services.lpad(champ, longueur, "�");break;
					}case 'C' : {
						champ = Services.cpad(champ, longueur, "�");break;
					} default : {
						champ = Services.rpad(champ, longueur, "�");break;
					}
				}
				
			}
			if (j<nbZones -1)
				if (separateur)
				   champ = champ + "|";
				else champ = champ + " ";
			ligne = ligne + champ;
	}

	String res = "";
	for (int i = 0; i < ligne.length(); i++){
		String s = String.valueOf(ligne.charAt(i));
		res+=(getHash().containsKey(s) ? (String)getHash().get(s) : s);
	}

	getResultat().add(res);
	
	
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (31/10/2002 07:40:57)
 * @return java.util.Hashtable
 */
private java.util.Hashtable getHash() {
	if (hash == null) {
		hash = new java.util.Hashtable();
		hash.put("\"","&quot;");
//		hash.put(" ","&nbsp;"); //Espace en &nbsp
//		hash.put("�","&nbsp;"); //Alt 255 en &nbsp
		hash.put(" ","�");//espace en ALT 255
	}
	return hash;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (30/10/2002 14:50:53)
 */
public String [] getListeFormatee() {

	//Alimentation r�sultat
	return getListeFormatee(false);
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (30/10/2002 14:50:53)
 */
public String [] getListeFormatee(boolean ligneZeroVide) {

	//Alimentation r�sultat
	Object o [] = getResultat().toArray();
	int decalage = (ligneZeroVide ? 1 : 0);

	int nbElem = getResultat().size()+decalage;
	String [] result = new String[nbElem];
	
	try {
		result [0] = "";
	} catch (Exception e) {
		// Le nb d'�l�ment est vide alors on renvoie null
		return null;
	}

	System.arraycopy(o,0,result,decalage,o.length);
	
	return result;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (30/10/2002 14:54:12)
 * @return java.util.ArrayList[]
 */
private java.util.ArrayList getResultat() {
	return resultat;
}
/**
 * Ins�rez la description de la m�thode ici.
 *  Date de cr�ation : (30/10/2002 14:50:53)
 * @return int[]
 */
private int[] getTaillesColonnes() {
	return taillesColonnes;
}

}
