package net.yura.domination.android;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiresApi(api = Build.VERSION_CODES.FROYO)
public class TheBackupAgent extends BackupAgentHelper {

    private static final String LOBBY_UUID_FILE = ".lobby";
    private static final String BACKUP_PROPERTY = "backup";

    @Override
    public void onCreate() {
        super.onCreate();

        FileBackupHelper helper = new FileBackupHelper(this, LOBBY_UUID_FILE);
        addHelper("LOBBY_BACKUP_KEY", helper);
    }

    public static void backup(Context context) {
        File lobbyFile = new File(context.getFilesDir(), LOBBY_UUID_FILE);
        if (lobbyFile.exists()) {
            java.util.Properties prop = new java.util.Properties();
            try {
                prop.load( new FileInputStream(lobbyFile) );
            }
            catch (Exception ex) {
                Logger.getLogger(TheBackupAgent.class.getName()).log(Level.WARNING, "flag load error", ex);
            }
            String backup = prop.getProperty(BACKUP_PROPERTY);
            if (backup == null) {
                prop.setProperty(BACKUP_PROPERTY, "done");
                try {
                    prop.store(new FileOutputStream(lobbyFile), "yura.net Lobby");
                }
                catch (Exception ex) {
                    Logger.getLogger(TheBackupAgent.class.getName()).log(Level.WARNING, "flag save error", ex);
                }

                BackupManager mBackupManager = new BackupManager(context);
                mBackupManager.dataChanged();

                Logger.getLogger(TheBackupAgent.class.getName()).info("lobby uuid backup done");
            }
            else {
                Logger.getLogger(TheBackupAgent.class.getName()).info("lobby uuid backup already " + backup);
            }
        }
        else {
            Logger.getLogger(TheBackupAgent.class.getName()).info("no lobby uuid");
        }
    }
}
