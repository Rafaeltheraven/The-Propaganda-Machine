package net.yura.domination.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import net.yura.domination.R;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleAccount {

    private static final int RC_SIGN_IN = 9000;

    private Activity activity;
    private final GoogleSignInOptions GOOGLE_SIGN_IN_OPTIONS;

    interface SignInListener {
        void onSignInSucceeded();
        void onSignInFailed();
    }

    public static abstract class SafeOnSuccessListener<TResult> implements OnSuccessListener<TResult> {
        public void onSuccess(TResult r) {
            try {
                onSuccessSafe(r);
            }
            catch (Exception ex) {
                Logger.getLogger(GoogleAccount.class.getName()).log(Level.WARNING, "error handling success", ex);
            }
        }
        public abstract void onSuccessSafe(TResult r);
    }

    private List<SignInListener> listeners = new ArrayList();

    public GoogleAccount(Activity activity) {
        this.activity = activity;

        GOOGLE_SIGN_IN_OPTIONS = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(activity.getString(R.string.server_client_id)).requestEmail().build();
    }

    public void addSignInListener(SignInListener listener) {
        listeners.add(listener);
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(activity) != null;
    }

    public void signInSilently() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(activity, GOOGLE_SIGN_IN_OPTIONS);
        signInClient.silentSignIn().addOnSuccessListener(activity,
                new SafeOnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccessSafe(GoogleSignInAccount signedInAccount) {
                        // The signed in account is stored in the task's result.
                        signInSuccessful(signedInAccount);
                    }
                });
    }

    public void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(activity, GOOGLE_SIGN_IN_OPTIONS);
        Intent intent = signInClient.getSignInIntent();
        activity.startActivityForResult(intent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null && result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                signInSuccessful(signedInAccount);
            }
            else {
                signInFailed();

                // HACK: no idea why sometimes result is null, google doc says it should never be null, bug in GMS?
                // HACK: for some strange reason, user cancelled actually returns status of ERROR
                if (result == null || result.getStatus() != Status.RESULT_CANCELED && result.getStatus().getStatusCode() != CommonStatusCodes.ERROR) {
                    String message = result == null ? null : result.getStatus().getStatusMessage();
                    if (message == null || "".equals(message)) {
                        message = "Failed to sign in";
                    }
                    new AlertDialog.Builder(activity).setMessage(message)
                            .setNeutralButton(android.R.string.ok, null).show();
                }
            }
        }
    }

    public void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(activity,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnSuccessListener(activity,
                new SafeOnSuccessListener<Void>() {
                    @Override
                    public void onSuccessSafe(Void r) {
                        // at this point, the user is signed out.
                    }
                });
    }

    private void signInSuccessful(GoogleSignInAccount signedInAccount) {
        for (SignInListener listener : listeners) {
            listener.onSignInSucceeded();
        }
    }

    private void signInFailed() {
        for (SignInListener listener : listeners) {
            listener.onSignInFailed();
        }
    }
}
