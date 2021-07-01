package net.yura.domination.android;

import com.google.android.gcm.GCMRegistrar;
import net.yura.android.AndroidMeApp;
import net.yura.domination.mobile.flashgui.DominationMain;
import net.yura.domination.mobile.flashgui.MiniFlashRiskAdapter;
import net.yura.lobby.client.AndroidLobbyClient;
import net.yura.lobby.client.Connection;
import net.yura.lobby.mini.MiniLobbyClient;
import android.content.Context;

public class GCMServerUtilities implements AndroidLobbyClient {

    public static void register(Context context, String registrationId) {
        Connection con = getLobbyConnection();
        if (con != null) {
            con.addAndroidEventListener(new GCMServerUtilities(context));
            con.androidRegister(registrationId);
        }
    }

    public static void unregister(Context context, String registrationId) {
        Connection con = getLobbyConnection();
        if (con != null) {
            con.addAndroidEventListener(new GCMServerUtilities(context));
            con.androidUnregister(registrationId);
        }
    }

    static Connection getLobbyConnection() {
        DominationMain main = (DominationMain)AndroidMeApp.getMIDlet();
        if (main != null) {
            MiniFlashRiskAdapter gui = main.adapter;
            if (gui != null) {
                MiniLobbyClient lobby = gui.lobby;
                if (lobby != null) {
                    return lobby.mycom;
                }
            }
        }
        return null;
    }

    private Context context;
    public GCMServerUtilities(Context context) {
        this.context = context;
    }

    @Override
    public void registerDone() {
        GCMRegistrar.setRegisteredOnServer(context, true);
    }

    @Override
    public void unregisterDone() {
        GCMRegistrar.setRegisteredOnServer(context, false);
    }

}
