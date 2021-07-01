package net.yura.domination.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.mobile.flashgui.DominationMain;
import net.yura.grasshopper.BugUIInfo;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.io.FileUtil;

public class MiniUtil {

    public static void showAbout() {
        Midlet.openURL("nativeNoResult://net.yura.domination.android.AboutActivity");
    }

    private static void showOldAbout() {

        ResourceBundle resb = TranslationBundle.getBundle();

        String text = getAboutHtml();

        Button credits = new Button(resb.getString("about.tab.credits"));
        credits.setActionCommand("credits");
        Button license = new Button(resb.getString("about.tab.license"));
        license.setActionCommand("license");
        Button changelog = new Button(resb.getString("about.tab.changelog"));
        changelog.setActionCommand("changelog");
        Button ok = new Button(resb.getString("about.okbutton"));
        ok.setMnemonic( KeyEvent.KEY_END );

        OptionPane.showOptionDialog(new ActionListener() {
            public void actionPerformed(String actionCommand) {
                try {
                    if ("license".equals(actionCommand)) {
                        RiskUtil.openDocs("gpl.txt");
                    }
                    else if ("changelog".equals(actionCommand)) {
                        RiskUtil.openDocs("ChangeLog.txt");
                    }
                    else if ("credits".equals(actionCommand)) {
                        RiskUtil.openDocs("help/game_credits.htm");
                    }
                }
                catch(Exception e) {
                    OptionPane.showMessageDialog(null,"Unable to open info: "+e.getMessage(),"Error", OptionPane.ERROR_MESSAGE);
                }
            }
        } ,text,resb.getString("about.title"), 0, OptionPane.INFORMATION_MESSAGE,
        null, new Button[] {credits,license,changelog,ok} , ok);

    }

    public static String getAboutHtml() {
        ResourceBundle resb = TranslationBundle.getBundle();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String copyright = resb.getString("about.copyright").replaceAll("\\{0\\}", String.valueOf(year) );
        String author = resb.getString("about.author") + " Yura Mamyrin (yura@yura.net)";
        String c1="#DA4437",c2="#F6971D",c3="#F5EA3B",c4="#65AF45",c5="#4284F3",c6="#7E3793";

        File externalMapDir = getExternalMapDir();

        String version = DominationMain.version;
        String versionName = System.getProperty("versionName");
        if (version != null && !version.equals(versionName)) {
            version = versionName + " (" + version + ")";
        }

        return "<html>" +
                "<div style=\"" +
// start CSS
"background-image: -webkit-gradient(linear, left top, left bottom, " +
    "color-stop(0%,   "+c1+"), color-stop(16.7%, "+c1+"), " +
    "color-stop(16.7%,"+c2+"), color-stop(33.3%, "+c2+"), " +
    "color-stop(33.3%,"+c3+"), color-stop(50%,   "+c3+"), " +
    "color-stop(50%,  "+c4+"), color-stop(66.7%, "+c4+"), " +
    "color-stop(66.7%,"+c5+"), color-stop(83.3%, "+c5+"), " +
    "color-stop(83.3%,"+c6+"), color-stop(100%,  "+c6+")" +
");"+
"height:30px;" +
// end CSS
                "\"></div>"+
                "<h3>yura.net "+RiskUtil.GAME_NAME+"</h3>"+
                "<p>"+DominationMain.product+" "+resb.getString("about.version")+" "+version+("true".equals( System.getProperty("debug") )?" DEBUG":"")+"</p>"+
                "<p>"+"Game Engine: "+" "+RiskUtil.RISK_VERSION +"</p>"+
                "<p>"+author+"</p>"+
                "<p>"+copyright+"</p>"+
               // "<p>"+ resb.getString("about.comments") +"</p>"+
                "<p>DPI: "+System.getProperty("display.dpi")+" Density: "+System.getProperty("display.density")+" Size: "+System.getProperty("display.size")+"</p>"+
                "<p>Locale: "+Locale.getDefault()+" use: "+resb.getLocale()+"</p>"+
                "<p>"+BugUIInfo.getLookAndFeel()+"</p>"+
                "<p>ExternalMapDir="+(externalMapDir == null ? "none" : "<a href=\""+externalMapDir.toURI()+"\">"+externalMapDir+"</a>")+"</p>"+
                // e.g. file:///storage/emulated/0/Domination%20Maps/
                "</html>";
    }

    public static String mapsdir = "file:///android_asset/maps/";

    public static List getFileList(String string) {
        List result = new java.util.Vector();

        File externalMapDir = getExternalMapDir();
        if (externalMapDir != null) {
            String[] list = externalMapDir.list();
            if (list != null) {
                for (int c = 0; c < list.length; c++) {
                    String file = list[c];
                    if (file.endsWith("." + string)) {
                        result.add(file);
                    }
                }
            }
        }

        Enumeration en = FileUtil.getDirectoryFiles(mapsdir);
        while (en.hasMoreElements()) {
            String file = (String)en.nextElement();
            if (file.endsWith("."+string) && !result.contains(file)) {
                result.add( file );
            }
        }


        String[] list = getSaveMapDir().list();
        for (int c=0;c<list.length;c++) {
            String file = list[c];
            if (file.endsWith("."+string) && !result.contains(file)) {
                result.add( file );
            }
        }

        return result;
    }

    public static InputStream openMapStream(String name) throws IOException {
        try {
            File externalMapDir = getExternalMapDir();
            if (externalMapDir != null) {
                File newFile = new File(externalMapDir, name);
                if (newFile.exists()) {
                    return new FileInputStream(newFile);
                }
            }
        }
        catch (Exception ex1) {
            ex1.printStackTrace();
            // ignore if we cant find map in external dir.
        }

        try {
            File userMaps = MiniUtil.getSaveMapDir();
            File newFile = new File(userMaps, name);
            return new FileInputStream(newFile);
        } catch (Exception ex) {
            try {
                return FileUtil.getInputStreamFromFileConnector(mapsdir + name);
            } catch (Exception ex2) {
                IOException exception = new IOException(ex2.toString());
                exception.initCause(ex); // in android 1.6
                throw exception;
            }
        }
    }

    private static File externalMapsDir;
    static File getExternalMapDir() {
        if (externalMapsDir!=null) {
            return externalMapsDir;
        }

        String ExternalStorageDirectory = System.getProperty("ExternalStorageDirectory");
        if (ExternalStorageDirectory != null) {
            File userMaps = new File(new File(ExternalStorageDirectory), RiskUtil.GAME_NAME + " Maps");
            //if (!userMaps.isDirectory() && !userMaps.mkdirs()) { // if it does not exist and i cant make it
            //    return null; // if we can not make it,
            //}

            externalMapsDir = userMaps;
            return userMaps;
        }
        return null;
    }

    private static File mapsDir;
    public static File getSaveMapDir() {

        if (mapsDir!=null) {
            return mapsDir;
        }

        File userHome = new File( System.getProperty("user.home") );
        File userMaps = new File(userHome, "maps");
        if (!userMaps.isDirectory() && !userMaps.mkdirs()) { // if it does not exist and i cant make it
            throw new RuntimeException("can not create dir "+userMaps);
        }

        mapsDir = userMaps;
        return userMaps;
    }

    private static File savesDir;
    public static File getSaveGameDir() {

        if (savesDir!=null) {
            return savesDir;
        }

        File userHome = new File( System.getProperty("user.home") );
        File userMaps = new File(userHome, "saves");
        if (!userMaps.isDirectory() && !userMaps.mkdirs()) { // if it does not exist and i cant make it
            throw new RuntimeException("can not create dir "+userMaps);
        }

        savesDir = userMaps;
        return userMaps;
    }

    public static String getSaveGameName(RiskGame game) {
        String file = game.getMapFile();
        if (file.endsWith(".map")) {
            file = file.substring(0, file.length() - 4);
        }
        return file;
    }

    public static void openHelp() {
        try {
            RiskUtil.openDocs("help/rules.htm");
        }
        catch(Exception e) {
            OptionPane.showMessageDialog(null,"Unable to open manual: "+e.getMessage(),"Error", OptionPane.ERROR_MESSAGE);
        }
    }

    public static String listToCsv(List<String> listOfStrings, char separator) {
        StringBuilder sb = new StringBuilder();

        // all but last
        for(int i = 0; i < listOfStrings.size() - 1 ; i++) {
            sb.append(listOfStrings.get(i));
            sb.append(separator);
        }

        // last string, no separator
        if(listOfStrings.size() > 0){
            sb.append(listOfStrings.get(listOfStrings.size()-1));
        }

        return sb.toString();
    }
}
