package net.yura.swingme.core;

import java.util.ResourceBundle;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Properties;

/**
 * @author Yura Mamyrin
 */
public class CoreUtil {

    /**
     * @see net.yura.mobile.gui.layout.XULLoader#getPropertyText(java.lang.String, boolean) 
     */
    public static Properties wrap(final ResourceBundle res) {
        return new Properties() {
            public String getProperty(String key) {
                try {
                    return res.getString(key);
                }
                catch (Exception ex) {
                    // sometimes this method is used by the XULLoader, but sometimes it is used directly
                    // from code, thats why for those cases we should not ever return null, as a sring is expected
                    Logger.warn("String not found " + key, ex);
                    return "???"+key+"???";
                }
            }
        };
    }

    public static void setupLogging() {
        Logger.setLogger(new net.yura.swingme.core.J2SELogger());
    }

}
