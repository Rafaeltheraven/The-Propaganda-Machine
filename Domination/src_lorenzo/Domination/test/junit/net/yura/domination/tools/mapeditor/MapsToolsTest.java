package net.yura.domination.tools.mapeditor;

import java.io.File;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;

/**
 * @author Yura Mamyrin
 */
public class MapsToolsTest extends TestCase {
    
    public MapsToolsTest(String testName) {
        super(testName);
    }

    public void testIsValidName() {

        assertTrue(MapsTools.isValidName("file"));
        assertTrue(MapsTools.isValidName("file.txt"));
        assertTrue(MapsTools.isValidName(".file"));
        assertTrue(MapsTools.isValidName(".text.file"));
        assertTrue(MapsTools.isValidName("my.file.text"));
        assertTrue(MapsTools.isValidName("my file.text"));
        assertTrue(MapsTools.isValidName(" !#$%'()+,-09;=@AZ[]^_`az{}"));

        // bad windows chars
        assertFalse(MapsTools.isValidName("file\\name"));
        assertFalse(MapsTools.isValidName("file/name"));
        assertFalse(MapsTools.isValidName("file:name.txt"));
        assertFalse(MapsTools.isValidName("file*name.txt"));
        assertFalse(MapsTools.isValidName("file?name"));
        assertFalse(MapsTools.isValidName("file\"name"));
        assertFalse(MapsTools.isValidName("file<name"));
        assertFalse(MapsTools.isValidName("file>name"));
        assertFalse(MapsTools.isValidName("file|name"));

        // other invalid names
        assertFalse(MapsTools.isValidName(""));
        assertFalse(MapsTools.isValidName("."));
        assertFalse(MapsTools.isValidName("file."));
        assertFalse(MapsTools.isValidName("con"));
        assertFalse(MapsTools.isValidName("con.txt"));
        assertFalse(MapsTools.isValidName("file€name.txt"));
        assertFalse(MapsTools.isValidName("file & name.map"));
    }
    
    
    public void testGetSafeMapID() {
        System.out.println("getSafeMapID");
        
        assertEquals("bob", MapsTools.getSafeMapID("bob.map"));
        assertEquals("bobTheBuilder", MapsTools.getSafeMapID("bob the builder.map"));
        assertEquals("bobTheBuilder", MapsTools.getSafeMapID("bobTheBuilder.map"));
        assertEquals("bob", MapsTools.getSafeMapID("bob .map"));
        assertEquals("Bob", MapsTools.getSafeMapID(" bob.map"));
        assertEquals("bob", MapsTools.getSafeMapID("bob"));
        assertEquals("bob", MapsTools.getSafeMapID("bob "));
        assertEquals("bob", MapsTools.getSafeMapID("bob   "));
        assertEquals("bobTheBuilder", MapsTools.getSafeMapID("bob   the   builder"));
        assertEquals("", MapsTools.getSafeMapID("   "));
        assertEquals("", MapsTools.getSafeMapID(""));
    }

    public void testGetExtension() {
        System.out.println("getExtension");
        
        assertEquals("jpeg", MapsTools.getExtension( new File("file.jpeg") ));
        assertEquals("jpg", MapsTools.getExtension( new File("file.something.jpg") ));
        assertEquals("", MapsTools.getExtension( new File(".file") ));
        assertEquals("", MapsTools.getExtension( new File("file") ));
        assertEquals("JPG", MapsTools.getExtension( new File("Something.JPG") ));
        assertEquals("PNG", MapsTools.getExtension( new File(".FILE.2.PNG") ));
    }
}
