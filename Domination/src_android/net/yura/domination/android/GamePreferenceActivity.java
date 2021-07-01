package net.yura.domination.android;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import net.yura.android.AndroidMeActivity;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.ai.AIManager;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.mobile.flashgui.DominationMain;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

public class GamePreferenceActivity extends PreferenceActivity {

    private static ResourceBundle resb = TranslationBundle.getBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            setPreferenceScreen( makePreferenceScreen(getPreferenceManager(),this) );
        }
        else {
            // hack to get rid of strange square on honeycomb
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((View)getListView().getParent()).setBackgroundDrawable(null);
            }
            getFragmentManager().beginTransaction().replace(android.R.id.content, new GamePreferenceFragment()).commit();
        }
    }

    private static PreferenceScreen makePreferenceScreen(PreferenceManager man, Context context) {
        PreferenceScreen root = man.createPreferenceScreen(context);

        PreferenceCategory inlinePrefCat = new PreferenceCategory(context);
        inlinePrefCat.setTitle( resb.getString("swing.menu.options") );
        root.addPreference(inlinePrefCat);

        CheckBoxPreference show_toasts = new CheckBoxPreference(context); // TwoStatePreference = new SwitchPreference(this);
        show_toasts.setTitle( resb.getString("game.menu.showtoasts") );
        show_toasts.setKey("show_toasts");
        inlinePrefCat.addPreference(show_toasts);

        CheckBoxPreference showDice = new CheckBoxPreference(context); // TwoStatePreference = new SwitchPreference(this);
        showDice.setTitle(resb.getString("game.menu.showdice"));
        showDice.setKey(DominationMain.SHOW_DICE_KEY);
        showDice.setDefaultValue(DominationMain.DEFAULT_SHOW_DICE);
        showDice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Risk.setShowDice((Boolean)newValue);
                return true;
            }
        });
        inlinePrefCat.addPreference(showDice);

        CheckBoxPreference color_blind = new CheckBoxPreference(context); // TwoStatePreference = new SwitchPreference(this);
        color_blind.setTitle( resb.getString("game.menu.colorblind") );
        color_blind.setKey("color_blind");
        inlinePrefCat.addPreference(color_blind);

        CheckBoxPreference fullscreen = new CheckBoxPreference(context); // TwoStatePreference = new SwitchPreference(this);
        fullscreen.setTitle( resb.getString("game.menu.fullscreen") );
        fullscreen.setKey("fullscreen");
        fullscreen.setDefaultValue(GameActivity.getDefaultFullScreen(AndroidMeActivity.DEFAULT_ACTIVITY));
        inlinePrefCat.addPreference(fullscreen);
        fullscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean)newValue) {
                    AndroidMeActivity.DEFAULT_ACTIVITY.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                else {
                    AndroidMeActivity.DEFAULT_ACTIVITY.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                return true;
            }
        });

        final ListPreference ai = new IntListPreference(context);
        ai.setTitle( resb.getString("game.menu.aiSpeed") );
        ai.setKey("ai_wait");
        final String[] aiSpeeds = new String[] {
                resb.getString("game.menu.aiSpeed.normal"),
                resb.getString("game.menu.aiSpeed.fast"),
                resb.getString("game.menu.aiSpeed.lightning"),
                resb.getString("game.menu.aiSpeed.instant")};
        final String[] aiSpeedsValues = new String[] {"500","300","100","0"};
        ai.setEntries(aiSpeeds);
        ai.setEntryValues(aiSpeedsValues);
        ai.setDefaultValue(aiSpeedsValues[0]);
        ai.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = String.valueOf(newValue);
                AIManager.setWait(Integer.parseInt(value));
                setSummary(ai,value);
                return true;
            }
        });
        inlinePrefCat.addPreference(ai);
        setSummary(ai,ai.getValue());

        final ListPreference lang = new ListPreference(context);
        lang.setTitle( resb.getString("game.menu.language") );
        lang.setKey("lang");
        Locale[] locales = Locale.getAvailableLocales();
        final String[] languageNames = new String[locales.length];
        final String[] languages = new String[locales.length];
        for (int c=0;c<locales.length;c++) {
            languages[c] = locales[c].toString();
            languageNames[c] = locales[c].getDisplayName();
        }
        lang.setEntries(languageNames);
        lang.setEntryValues(languages);
        lang.setDefaultValue( Locale.getDefault().toString() );
        lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setSummary(lang,String.valueOf(newValue));
                scheduleRestart();
                return true;
            }
        });
        inlinePrefCat.addPreference(lang);
        setSummary(lang, TranslationBundle.getBundle().getLocale().toString());

        return root;
    }

    private static void setSummary(ListPreference prefs,String value) {
	int index = prefs.findIndexOfValue(value);
	if (index >= 0) {
	    prefs.setSummary( prefs.getEntries()[index] );
	}
	else {
	    prefs.setSummary(value);
	    // many wrong values come here (e.g. "_ES" "_EN" "_FR" "_IT" "_PT")
	    System.out.println("value "+value+" not found in "+Arrays.asList(prefs.getEntryValues()));
	}
    }

    private static void scheduleRestart() {

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                Activity activity = AndroidMeActivity.DEFAULT_ACTIVITY;
                Intent i = activity.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( activity.getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                android.os.Process.killProcess(android.os.Process.myPid());

                /*
                // another way of restarting the app, not sure what is better.
                Activity activity = AndroidMeActivity.DEFAULT_ACTIVITY;
                android.app.PendingIntent intent = android.app.PendingIntent.getActivity(activity.getBaseContext(), 0, new Intent(activity.getIntent()), activity.getIntent().getFlags());
                android.app.AlarmManager manager = (android.app.AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                manager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, intent);
                System.exit(2);
                */
            }
        }, 500);

    }
    /*
    // Called only on Honeycomb and later
    @Override
    public void onBuildHeaders(List<Header> target) {
        Header header = new Header();
        header.title = "header title";
        header.fragment = GamePreferenceFragment.class.getName();
        target.add(header);
    }
    */

    public static class GamePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setPreferenceScreen( makePreferenceScreen(getPreferenceManager(),getActivity()) );
        }
    }


    public static class IntListPreference extends ListPreference {

        public IntListPreference(Context context) {
            super(context);
        }
        public IntListPreference(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean persistString(String value) {
            if(value == null) {
                return false;
            } else {
                return persistInt(Integer.valueOf(value));
            }
        }

        @Override
        protected String getPersistedString(String defaultReturnValue) {
            if(getSharedPreferences().contains(getKey())) {
                int intValue = getPersistedInt(0);
                return String.valueOf(intValue);
            } else {
                return defaultReturnValue;
            }
        }
    }

}
