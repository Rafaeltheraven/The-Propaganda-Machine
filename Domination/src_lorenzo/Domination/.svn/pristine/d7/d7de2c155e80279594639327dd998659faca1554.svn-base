package net.yura.domination.mobile.flashgui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.ai.AIManager;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.mapstore.MapChooser;
import net.yura.domination.mapstore.MapUpdateService;
import net.yura.domination.mobile.MiniUtil;
import net.yura.domination.mobile.RiskMiniIO;
import net.yura.grasshopper.ApplicationInfoProvider;
import net.yura.grasshopper.BugSubmitter;
import net.yura.grasshopper.LogList;
import net.yura.grasshopper.SimpleBug;
import net.yura.lobby.mini.MiniLobbyClient;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;
import net.yura.mobile.gui.plaf.nimbus.NimbusLookAndFeel;
import net.yura.swingme.core.CoreUtil;
import net.yura.util.Service;

public class DominationMain extends Midlet {

    public static final boolean DEFAULT_SHOW_DICE = true;
    public static final String SHOW_DICE_KEY = "show_dice";

    public static final String DEFAULT_GAME_TYPE_KEY = "default.gametype";
    public static final String DEFAULT_CARD_TYPE_KEY = "default.cardtype";
    public static final String DEFAULT_AUTO_PLACE_ALL_KEY = "default.autoplaceall";
    public static final String DEFAULT_RECYCLE_CARDS_KEY = "default.recycle";

    public static final String product = "AndroidGUI";
    public static final String version;
    static {
        String versionCode = System.getProperty("versionCode");
        version = versionCode != null ? versionCode : RiskUtil.RISK_VERSION;
    }

    public static Preferences appPreferences;
    public GooglePlayGameServices googlePlayGameServices;

    public Risk risk;
    public MiniFlashRiskAdapter adapter;

    public interface GooglePlayGameServices {
	void beginUserInitiatedSignIn();
	void signOut();
	boolean isSignedIn();

	void showAchievements();
        void unlockAchievement(String id);

	void startGameGooglePlay(net.yura.lobby.model.Game game);
	void setLobbyUsername(String username);
	void gameStarted(int id);
	
	boolean hasPendingOpenLobby();
    }

    public DominationMain() {

        Service.SERVICES_LOCATION = "assets/services/";

        // IO depends on this, so we need to do this first
        RiskUtil.streamOpener = new RiskMiniIO();

        // get version from AndroidManifest.xml
        //String versionName = System.getProperty("versionName");
        //Risk.RISK_VERSION = versionName!=null ? versionName : "?me4se?";

        try {
            SimpleBug.initLogFile(RiskUtil.GAME_NAME + " " + product, version, TranslationBundle.getBundle().getLocale().toString());
            BugSubmitter.setApplicationInfoProvider( new ApplicationInfoProvider() {
                @Override
                public void addInfoForSubmit(Map map) {
                    Risk r = risk;
                    if (r != null) {
                        RiskGame game = r.getGame();
                        if (game != null) {
                            map.put("gameLog", new LogList( game.getCommands() ));
                        }
                    }
                    map.put("lobbyID", MiniLobbyClient.getMyUUID() );
                }
                @Override
                public boolean ignoreError(LogRecord record) {
                    String loggerName = record.getLoggerName();
                    if ("DataScheduler".equals(loggerName)) { // "libcore.io.IoBridge".equals(className) && "isDataSchedulerEnabled".equals(methodName)
                        // isDataSchedulerEnabled(): DataScheduler is disabled, exeption=java.io.FileNotFoundException: /system/etc/datascheduling_policy_conf.xml: open failed: ENOENT (No such file or directory)
                        return true;
                    }

                    String className = record.getSourceClassName();
                    String methodName = record.getSourceMethodName();
                    if ("java.net.InetAddress".equals(className) && "lookupHostByName".equals(methodName)) {
                        return true;
                    }
                    if ("java.net.InetAddress".equals(className) && "getByName".equals(methodName)) {
                        return true;
                    }
                    if ("java.net.AddressCache".equals(className) && "customTtl".equals(methodName)) {
                        return true;
                    }

                    String message = record.getMessage();
                    if ("rto value is too small:0".equals(message) ||
                        "/data/system/carrierinfo.prop: open failed: ENOENT (No such file or directory)".equals(message) ||
                        "isDataSchedulerEnabled():false".equals(message) ||
                        "remove failed: ENOENT (No such file or directory) : /data/data/net.yura.domination/shared_prefs/net.yura.domination_preferences.xml.bak".equals(message) ||
                        "remove failed: ENOENT (No such file or directory) : /data/data/net.yura.domination/shared_prefs/com.google.android.gcm.xml.bak".equals(message) ||
                        "remove failed: ENOENT (No such file or directory) : /data/user/0/net.yura.domination/shared_prefs/net.yura.domination_preferences.xml.bak".equals(message) ||
                        "remove failed: ENOENT (No such file or directory) : /data/user/0/net.yura.domination/shared_prefs/com.google.android.gcm.xml.bak".equals(message) ||
                        "remove failed: ENOENT (No such file or directory) : /data/user/0/net.yura.domination/shared_prefs/com.google.android.gms.signin.xml.bak".equals(message)) {
                        return true;
                    }
                    if (message != null && (
                            message.startsWith("remove failed: ENOENT (No such file or directory) : /data/data/net.yura.domination/files/.java/.userPrefs/net/yura/domination/mobile/flashgui/prefs-") || // then some random GUID
                            message.startsWith("remove failed: ENOENT (No such file or directory) : /data/user/0/net.yura.domination/files/.java/.userPrefs/net/yura/domination/mobile/flashgui/prefs-"))) { // then some random GUID
                        return true;
                    }

                    return false;
                }
            } );

        }
        catch (Throwable th) {
            System.out.println("Grasshopper not loaded");
            th.printStackTrace();
        }

        CoreUtil.setupLogging();

        if ( "true".equals( System.getProperty("debug") ) ) {

            // MWMWMWMWMWMWMWMWMWMWMWM ONLY DEBUG MWMWMMWMWMWMWMWMWMWMWMWMWM

            Logger.getLogger("").addHandler( new Handler() {
                boolean open;
                @Override
                public void publish(LogRecord record) {
                    if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                        if (!open) {
                            open = true;
                            try {
                                OptionPane.showMessageDialog(null, record.getMessage()+" "+record.getThrown(), "WARN", OptionPane.WARNING_MESSAGE);
                            }
                            catch(Exception ex) { ex.printStackTrace(); }
                        }
                    }
                }

                @Override public void flush() { }
                @Override public void close() { }
            } );

            // cant do this on J2SE, swing will print too much junk.
            if (Midlet.getPlatform() != Midlet.PLATFORM_ME4SE) {
                // if we want to see DEBUG, default is INFO
                java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.ALL);
            }

            // so we do not need to wait for AI while testing
            net.yura.domination.engine.ai.AIManager.setWait(5);

            // MWMWMWMWMWMWMWMWMWMWM END ONLY DEBUG MWMWMMWMWMWMWMWMWMWMWMWM
        }

        if (appPreferences!=null) {
            AIManager.setWait( appPreferences.getInt("ai_wait", AIManager.getWait()) );
            String lang = appPreferences.get("lang", null);
            if (lang!=null) {
                TranslationBundle.setLanguage(lang);
            }
            Risk.setShowDice(appPreferences.getBoolean(SHOW_DICE_KEY, DEFAULT_SHOW_DICE));
        }
    }

/*
    @Override
    protected void destroyApp(boolean arg0) throws javax.microedition.midlet.MIDletStateChangeException {

        if (risk.getGame()!=null) {
            risk.parser("savegame auto.save");
        }
        risk.kill();
        try { risk.join(); } catch (InterruptedException e) { } // wait for game thread to die 

        super.destroyApp(arg0);
    }
*/

    @Override
    public void initialize(DesktopPane rootpane) {

        SynthLookAndFeel synth;

        try {
            synth = (SynthLookAndFeel)Class.forName("net.yura.android.plaf.AndroidLookAndFeel").newInstance();

            // small hack to center radiobutton icon
            Style radioButtonStyle = synth.getStyle("RadioButton");
            Icon radioButtonIcon = (Icon)radioButtonStyle.getProperty("icon", Style.ALL);
            if (radioButtonIcon!=null) {
                radioButtonStyle.addProperty( new CentreIcon(radioButtonIcon,radioButtonIcon.getIconWidth(),radioButtonIcon.getIconWidth()), "icon", Style.ALL);
            }

        }
        catch (Exception ex) {
            if (Midlet.getPlatform()==Midlet.PLATFORM_ANDROID) {
                net.yura.mobile.logging.Logger.warn(null, ex);
            }

            synth = new NimbusLookAndFeel();
        }

        try {
            synth.load( Midlet.getResourceAsStream("/dom_synth.xml") );
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        rootpane.setLookAndFeel( synth );

        MapChooser.loadThemeExtension(); // this has theme elements used inside AND outside of the MapChooser



        risk = new Risk();
        adapter = new MiniFlashRiskAdapter(risk);


        // this needs to run in the UI thread, otherwise 1 thread could be trying to read the auto.save file
        // and another trying to delete it, so we do all actions on the file in the same (UI) thread
        DesktopPane.invokeLater(new Runnable() {
            @Override
            public void run() {
                File autoSaveFile = getAutoSaveFile();
                if (autoSaveFile.exists()) {
                    GameActivity.logger.info("[GameActivity] LOADING FROM AUTOSAVE");
                    // rename the file before we load it with the game thread so it does not get deleted by another thread
                    RiskUtil.rename(autoSaveFile, new File(autoSaveFile.getParent(),autoSaveFile.getName()+".load"));
                    risk.parser( "loadgame "+getAutoSaveFile()+".load" );
                }
                else {
                    adapter.openMainMenu();
                    
                    GooglePlayGameServices gpgs = getGooglePlayGameServices();
                    if (gpgs != null && gpgs.hasPendingOpenLobby()) {
                        adapter.openLobby();
                    }
                }
            }
        });


        new Thread() {
            @Override
            public void run() {
                MapUpdateService.getInstance().init( MiniUtil.getFileList("map"), MapChooser.MAP_PAGE );
            }
        }.start();


        //risk.parser("newgame");
        //risk.parser("newplayer ai hard blue bob");
        //risk.parser("newplayer ai hard red fred");
        //risk.parser("newplayer ai hard green greg");
        //risk.parser("startgame domination increasing");


//        try {
//            File saves = new File( net.yura.android.AndroidMeApp.getIntance().getFilesDir() ,"saves");
//            File sdsaves = new File("/sdcard/Domination-saves");
//            copyFolder(saves, sdsaves);
//            copyFolder(sdsaves, saves);
//            System.out.println("files"+ Arrays.asList( saves.list() ) );
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }

    }

    private final static String AUTO_SAVE_FILE_NAME = "auto.save";
    public static File getAutoSaveFile() {
        return new File(MiniUtil.getSaveGameDir(), AUTO_SAVE_FILE_NAME);
    }

    public static class CentreIcon extends Icon {
        Icon wrappedIcon;
        public CentreIcon(Icon icon,int w,int h) {
            wrappedIcon = icon;
            width = w;
            height = h;
        }
        @Override
        public void paintIcon(Component c, Graphics2D g, int x, int y) {
            // paint real icon in the middle of this icon
            wrappedIcon.paintIcon(c, g, x + (getIconWidth()-wrappedIcon.getIconWidth())/2, y + (getIconHeight()-wrappedIcon.getIconHeight())/2);
        }
    }

    public static boolean getBoolean(String key, boolean deflt) {
        if (appPreferences == null) return deflt;
        return appPreferences.getBoolean(key, deflt);
    }

    public static String getString(String key, String defaultValue) {
        if (appPreferences == null) return defaultValue;
        return appPreferences.get(key, defaultValue);
    }

    public static void saveGameSettings(String gameTypeCommand, String cardTypeCommand, boolean autoPlaceAllBoolean, boolean recycleCardsBoolean) {
        if (appPreferences != null) {
            appPreferences.put(DEFAULT_GAME_TYPE_KEY, gameTypeCommand);
            appPreferences.put(DEFAULT_CARD_TYPE_KEY, cardTypeCommand);
            appPreferences.putBoolean(DEFAULT_AUTO_PLACE_ALL_KEY, autoPlaceAllBoolean);
            appPreferences.putBoolean(DEFAULT_RECYCLE_CARDS_KEY, recycleCardsBoolean);
            flushPreferences();
        }
    }

    public static void setAccounts(List<String> accounts) {
        if (!accounts.isEmpty()) {
            if (appPreferences != null) {
                appPreferences.put("accounts", MiniUtil.listToCsv(accounts, ','));
                flushPreferences();
            }
        }
    }

    public static String getAccountsString() {
        String accounts = getString("accounts", null);
        // if we accidentally saved an empty string in an old version, remove it
        if ("".equals(accounts)) {
            appPreferences.remove("accounts");
            flushPreferences();
            return null;
        }
        return accounts;
    }

    private static void flushPreferences() {
        try {
            appPreferences.flush();
        }
        catch(Exception ex) {
            Logger.getLogger(DominationMain.class.getName()).log(Level.WARNING, "can not flush prefs", ex);
        }
    }

    public void setGooglePlayGameServices(GooglePlayGameServices listener) {
	googlePlayGameServices = listener;
    }
    public static GooglePlayGameServices getGooglePlayGameServices() {
	DominationMain main = (DominationMain)Midlet.getMidlet();
        // main is only null if the app is in the process of shutting down.
	return main == null ? null : main.googlePlayGameServices;
    }

    private static Map<Integer,ActivityResultListener> nativeCalls = new HashMap();
    private static int nativeCallsCount = 100000; // auto Ids need to start higher then all hard coded ids.

    public interface ActivityResultListener {
        void onActivityResult(Object data);
        void onCanceled();
    }

    public static void openURL(String url, ActivityResultListener listener) {
        nativeCallsCount++;
        url = url + (url.indexOf('?') >= 0 ? "&" : "?") + "requestCode=" + nativeCallsCount;
        nativeCalls.put(nativeCallsCount,listener);
        Midlet.openURL(url);
    }

    public void onResult(int requestCode, int resultCode, Object obj) {
        ActivityResultListener listener = nativeCalls.remove(requestCode);
        if (listener != null) {
            if (resultCode == -1) { // Activity.RESULT_OK
                listener.onActivityResult(obj);
            }
            else if (resultCode == 0) { // Activity.RESULT_CANCELED
                listener.onCanceled();
            }
            else {
                Logger.getLogger("").warning("unknown resultCode "+resultCode);
            }
        }
    }
}
