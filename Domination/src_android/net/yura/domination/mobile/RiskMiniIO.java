package net.yura.domination.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.microedition.io.Connector;
import net.yura.domination.engine.RiskIO;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.RiskGame;
import net.yura.mobile.gui.Midlet;

/**
 * @author Yura Mamyrin
 */
public class RiskMiniIO implements RiskIO {

    public InputStream openStream(String name) throws IOException {
        return Connector.openInputStream("file:///android_asset/"+name);
    }

    public InputStream openMapStream(String name) throws IOException {
        return MiniUtil.openMapStream(name);
    }

    public ResourceBundle getResourceBundle(Class c, String n, Locale l) {
        return ResourceBundle.getBundle(c.getPackage().getName()+"."+n, l );
    }

    public void openURL(URL url) throws Exception {
        Midlet.openURL(url.toString());
    }

    public void openDocs(String doc) throws Exception {
        Midlet.openURL("file:///android_asset/" + doc );
    }

    public InputStream loadGameFile(String file) throws Exception {
        return new FileInputStream(file);
    }

    public void saveGameFile(String file, RiskGame obj) throws Exception {
        obj.saveGame(new FileOutputStream(file));
    }

    public OutputStream saveMapFile(String fileName) throws Exception {
        return RiskUtil.getOutputStream( MiniUtil.getSaveMapDir(), fileName);
    }

    public void getMap(String filename, Observer observer) {
        net.yura.domination.mapstore.GetMap.getMap(filename, observer);
    }

    public void renameMapFile(String oldName, String newName) {
        File oldFile = new File(MiniUtil.getSaveMapDir(), oldName);
        File newFile = new File(MiniUtil.getSaveMapDir(), newName);
        RiskUtil.rename(oldFile, newFile);
    }
    
    public boolean deleteMapFile(String mapUID) {
        File mapFile = new File(MiniUtil.getSaveMapDir(), mapUID);
        if (mapFile.exists()) {
            return mapFile.delete();
        }
        return false;
    }

}
