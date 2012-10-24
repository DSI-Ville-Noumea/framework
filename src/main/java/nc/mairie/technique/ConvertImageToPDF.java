package nc.mairie.technique;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import java.io.File;
import com.lowagie.text.Image;
/**
 * Insérez la description du type ici.
 * Date de création : (06/04/2004 13:01:32)
 * @author: Administrator
 */
public class ConvertImageToPDF {
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 */
public static boolean convertir(Image image, File fileOut) throws Exception {

	return convertir(image, new java.io.FileOutputStream(fileOut.getAbsolutePath()));
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 */
public static boolean convertir(Image image, java.io.OutputStream output) throws Exception {

	Document document = new Document();
	PdfWriter writer = PdfWriter.getInstance(document, output);

	document.setMargins(0,0,0,0);
	document.open();

	Rectangle r = document.getPageSize();
	image.scaleToFit(r.getWidth()-1, r.getHeight()-1);

	document.add(image);

	document.close();
	writer.close();

	return true;
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 */
public static boolean convertir(File fileIn, File fileOut) throws Exception {

	Image image = Image.getInstance(fileIn.getAbsolutePath());
	return 	convertir(image, fileOut);
		
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 */
public static boolean convertir(File fileIn, java.io.OutputStream output) throws Exception {

	Image image = Image.getInstance(fileIn.getAbsolutePath());
	return 	convertir(image, output);
		
}
}
