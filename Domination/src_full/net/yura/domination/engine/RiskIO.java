package net.yura.domination.engine;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Observer;
import java.util.ResourceBundle;
import java.net.URL;
import net.yura.domination.engine.core.RiskGame;

public interface RiskIO {

        /**
         * for access to local files like game.ini or commands.txt, credits.htm
         */
	InputStream openStream(String name) throws IOException;

	InputStream openMapStream(String name) throws IOException;
	ResourceBundle getResourceBundle(Class a,String n,Locale l);
	void openURL(URL url) throws Exception;
	void openDocs(String doc) throws Exception;

        // only here coz of android, as are not used for lobby
        void saveGameFile(String name,RiskGame obj) throws Exception;
        InputStream loadGameFile(String file) throws Exception;
        
        OutputStream saveMapFile(String fileName) throws Exception;
        void renameMapFile(String oldName, String newName);
        
        /**
         * @return did the file get successfully deleted
         */
        boolean deleteMapFile(String mapName);

        /**
         * get callback with either {@link RiskUtil#SUCCESS} or {@link RiskUtil#ERROR}
         */
        void getMap(String filename, Observer observer);
}
