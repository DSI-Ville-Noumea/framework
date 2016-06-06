package nc.mairie.technique;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;
import com.lowagie.text.Document;

import java.io.File;
import java.util.ArrayList;

import com.lowagie.text.Image;
/**
 * Insérez la description du type ici.
 * Date de création : (06/04/2004 13:01:32)
 * @author: Administrator
 */
public class ConvertImageToPDF {
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 * @param image image 
 * @param fileOut fileOut 
 * @return boolean
 * @throws Exception Exception 
 */
public static boolean convertir(Image image, File fileOut) throws Exception {

	return convertir(image, new java.io.FileOutputStream(fileOut.getAbsolutePath()));
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 * @param images images 
 * @param fileOut fileOut 
 * @return boolean
 * @throws Exception Exception 
 */
public static boolean convertir(ArrayList<Image> images, File fileOut) throws Exception {

	return convertir(images, new java.io.FileOutputStream(fileOut.getAbsolutePath()));
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 * @param image image 
 * @param output output 
 * @return boolean
 * @throws Exception Exception 
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
 * @param images images
 * @param output output 
 * @return boolean
 * @throws Exception Exception 
 */
public static boolean convertir(ArrayList<Image> images, java.io.OutputStream output) throws Exception {

	Document document = new Document();
	PdfWriter writer = PdfWriter.getInstance(document, output);

	document.setMargins(0,0,0,0);
	document.open();

	Rectangle r = document.getPageSize();
	
	for (Image image : images) {

		image.scaleToFit(r.getWidth()-1, r.getHeight()-1);
		document.add(image);
	}
	
	document.close();
	writer.close();

	return true;
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 * @param fileIn fileIn 
 * @param fileOut fileOut 
 * @return boolean
 * @throws Exception Exception 
 */
public static boolean convertir(File fileIn, File fileOut) throws Exception {

	
	RandomAccessFileOrArray ra = new RandomAccessFileOrArray(fileIn.getAbsolutePath());
	
	int pages = 0;
	
	try { 
		pages = TiffImage.getNumberOfPages(ra);
	} catch (Exception e) {
		pages =1;
	}

	//si une seule page
	if (pages == 1) {
		Image image = Image.getInstance(fileIn.getAbsolutePath());
		return 	convertir(image, fileOut);
	}
	
	ArrayList<Image> images = new ArrayList<Image>();
	
	for (int i = 0; i < TiffImage.getNumberOfPages(ra); i++) {
		images.add(TiffImage.getTiffImage(ra, i+1));
	}
	
	ra.close();
	
	return 	convertir(images, fileOut);
}
/**
 * Commentaire relatif au constructeur ConvertImageToPDF.
 * @param fileIn fileIn 
 * @param output output 
 * @return boolean
 * @throws Exception Exception 
 */
public static boolean convertir(File fileIn, java.io.OutputStream output) throws Exception {

	Image image = Image.getInstance(fileIn.getAbsolutePath());
	return 	convertir(image, output);
		
}
}
