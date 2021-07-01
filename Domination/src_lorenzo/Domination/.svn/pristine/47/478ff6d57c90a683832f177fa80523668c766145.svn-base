package net.yura.domination.android;

import static net.yura.domination.android.GCMActivity.displayMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.microedition.midlet.MIDlet;
import net.yura.android.AndroidMeApp;
import net.yura.domination.R;
import net.yura.lobby.client.AndroidLobbyClient;
import net.yura.lobby.mini.MiniLobbyClient;
import android.content.Context;
import android.content.Intent;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * @see com.google.android.gcm.demo.app.GCMIntentService
 */
public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(AndroidMeApp.getContext().getString(R.string.app_id));
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        displayMessage(context,"Device registered: regId = "+registrationId);
        GCMServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        displayMessage(context, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            GCMServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            displayMessage(context, "Ignoring unregister callback");
        }
    }

    /**
     * @see MiniLobbyClient#notify(net.yura.lobby.model.Game, boolean)
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
	String msg = intent.getExtras().getString(AndroidLobbyClient.MESSAGE);
        String gameId = intent.getExtras().getString(AndroidLobbyClient.GAME_ID);
        String options = intent.getExtras().getString(AndroidLobbyClient.OPTIONS);

        String message = msg==null?"Received message":msg;
        displayMessage(context, message);
        // notifies user
        Map<String, Object> extras = new HashMap();
        if (gameId != null) {
            extras.put(MiniLobbyClient.EXTRA_GAME_ID, gameId);
        }
        if (options != null) {
            extras.put(MiniLobbyClient.EXTRA_GAME_OPTIONS, options);
        }
        MIDlet.showNotification(context.getString(R.string.app_name), message, R.drawable.icon, -1, extras);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        String message = "Received deleted messages notification "+total;
        displayMessage(context, message);
        // notifies user
        MIDlet.showNotification(context.getString(R.string.app_name), message, R.drawable.icon, -1, Collections.EMPTY_MAP);
    }

    @Override
    public void onError(Context context, String errorId) {
        displayMessage(context, "Received error: "+errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        displayMessage(context, "Received recoverable error: "+errorId);
        return super.onRecoverableError(context, errorId);
    }

}
