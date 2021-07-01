/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.domination.engine.translation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Observer;
import java.util.ResourceBundle;
import junit.framework.TestCase;
import net.yura.domination.engine.RiskIO;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.RiskGame;

public class TranslationBundleTest extends TestCase {

    public TranslationBundleTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    /**
     * to update the compiled note, run:
     * sed -i 's+48.0/java1.4+49/java1.5+g' src/net/yura/domination/engine/translation/Risk*.properties
     */
    public void testSystemInfoString() {
        System.out.println("testSystemInfoString");

        RiskUtil.streamOpener = new RiskIO() {
            public ResourceBundle getResourceBundle(Class c, String n, Locale l) {
                return ResourceBundle.getBundle(c.getPackage().getName()+"."+n, l );
            }

            public InputStream openStream(String name) throws IOException { return null; }
            public InputStream openMapStream(String name) throws IOException { return null; }
            public void openURL(URL url) throws Exception { }
            public void openDocs(String doc) throws Exception { }
            public void saveGameFile(String name, RiskGame obj) throws Exception { }
            public InputStream loadGameFile(String file) throws Exception { return null; }
            public OutputStream saveMapFile(String fileName) throws Exception { return null; }
            public void renameMapFile(String oldName, String newName) { }
            public void getMap(String filename, Observer observer) { }
            public boolean deleteMapFile(String mapName) { return false; }
        };

        //Locale[] locales = Locale.getAvailableLocales();
        Locale[] locales = getAppLocales();

        if (locales.length < 5) {
            throw new RuntimeException("number too small");
        }

        for (Locale locale : locales) {

            TranslationBundle.setLanguage(locale.toString());
            ResourceBundle resb = TranslationBundle.getBundle();

            String text = resb.getString("about.infopanel");
            String[] split = text.split("\\n");

            if (split.length != 14) {
                throw new RuntimeException("error in " + locale);
            }
            //if (!split[6].equals(" Screen: ")) {
            //    throw new RuntimeException("error in " + locale + " >" + split[6] + "< ");
            //}
            
            String compiled = resb.getString("about.compiledfor");
            if (!compiled.contains("49/java1.5")) {
                throw new RuntimeException("error2 in " + locale);
            }
        }
        System.out.println("testSystemInfoString PASS");
    }

    private Locale[] getAppLocales() {
        File translation = new File("src/net/yura/domination/engine/translation");
        String[] files = translation.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("Risk") && name.endsWith(".properties");
            }
        });
        Locale[] locales = new Locale[files.length];
        int c = 0;
        for (String file : files) {
            locales[c++] = getLocale(file);
        }
        return locales;
    }

    public static Locale getLocale(String fileName) {
        int index = fileName.indexOf('_');
        if (index < 0) {
            return TranslationBundle.getLocale("");
        }
        return TranslationBundle.getLocale(fileName.substring(index + 1, fileName.lastIndexOf('.')));
    }
}
