package nc.mairie.technique;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class ConvertImageToPDFTest {

	@Test
    public void testconvertir(){
        Assert.assertTrue(true);
        
        URL urlFileIn = getClass().getResource("/test.tif");
        File fileIn = new File(urlFileIn.getFile());
        Assert.assertTrue(fileIn.exists());
        
        File fileOut = new File(fileIn.getAbsolutePath() + ".pdf");
        
        if (fileOut.exists()) {
        	fileOut.delete();
        }
        Assert.assertFalse(fileOut.exists());
        
        try {
			ConvertImageToPDF.convertir(fileIn, fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
        
        Assert.assertTrue(fileOut.exists());
        
    }	
	
}
