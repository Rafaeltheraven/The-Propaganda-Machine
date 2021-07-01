package net.yura.domination.android;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.yura.domination.R;
import com.google.android.gcm.GCMRegistrar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

public class GCMActivity extends Activity {

    static final Logger logger = Logger.getLogger(GCMActivity.class.getName());

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	try {
	    // TODO we do not support push on BB yet as they have not given us a push token
	    if (!"BlackBerry".equals(Build.BRAND)) {
                setup();
                //unregister();
	    }
	}
	catch (UnsupportedOperationException th) {
	    logger.log(Level.INFO, "gmc fail", th);
	}
	catch (Throwable th) {
	    logger.log(Level.WARNING, "gmc fail", th);
	}
        finish();
    }

    public static void displayMessage(Context context,String text) {
        logger.info(text);
    }

    public static void setup() {
        Context context = net.yura.android.AndroidMeApp.getContext();

        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId.equals("")) {
          GCMRegistrar.register(context, context.getString(R.string.app_id));
        }
        else {
            if (GCMRegistrar.isRegisteredOnServer(context)) {
                displayMessage(context,"Already registered");
            }
            else {
                GCMServerUtilities.register(context, regId);

                // TODO if we FAIL at registering on our server then call
                // GCMRegistrar.unregister(context);
                // currently can not tell
            }
        }
    }

    public static void unregister() {
        Context context = net.yura.android.AndroidMeApp.getContext();
        GCMRegistrar.unregister(context);
    }

}
