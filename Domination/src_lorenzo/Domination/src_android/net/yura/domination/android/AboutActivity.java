package net.yura.domination.android;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.yura.domination.R;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.mobile.MiniUtil;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TabHost;
import android.app.Activity;

public class AboutActivity extends Activity implements TabHost.TabContentFactory {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ResourceBundle resb = TranslationBundle.getBundle();
        //setTitle( resb.getString("about.title") );

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabContentFactory factory = this;

        tabHost.addTab( tabHost.newTabSpec("about").setIndicator( resb.getString("about.title") ).setContent(factory) );
        tabHost.addTab( tabHost.newTabSpec("credits").setIndicator( resb.getString("about.tab.credits") ).setContent(factory) );
        tabHost.addTab( tabHost.newTabSpec("license").setIndicator( resb.getString("about.tab.license") ).setContent(factory) );
        tabHost.addTab( tabHost.newTabSpec("changelog").setIndicator( resb.getString("about.tab.changelog") ).setContent(factory) );

        tabHost.setCurrentTab(0);  
    }

    @Override
    public View createTabContent(String tag) {
        WebView webView = new WebView(this);
        final String prefix = "file:///android_asset/";
        if ("about".equals(tag)) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("file://") && !url.startsWith(prefix)) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                        catch (Exception ex) {
                            Logger.getLogger("").log(Level.INFO,"cant open " + url, ex);
                        }
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
            String aboutHtml = MiniUtil.getAboutHtml();
            webView.loadDataWithBaseURL(prefix, aboutHtml, "text/html", "UTF-8", null);

            // OLD way of displaying html, does not support catching file:// links
            //WebSettings settings = webView.getSettings();
            //settings.setDefaultTextEncodingName("utf-8"); // UTF-8 here works on only v2 android
            // hack to fix bug on android
            //aboutHtml = aboutHtml.replace("%", "%25").replace("#", "%23").replace("'", "%27").replace("?", "%3f");
            //webView.loadData(aboutHtml, "text/html; charset=utf-8", null); // UTF-8 here works on only v4 android
        }
        else if ("credits".equals(tag)){
            webView.loadUrl(prefix+"help/game_credits.htm");
        }
        else if ("license".equals(tag)){
            webView.loadUrl(prefix+"gpl.txt");
        }
        else if ("changelog".equals(tag)){
            webView.loadUrl(prefix+"ChangeLog.txt");
        }
        else {
            throw new IllegalArgumentException("strange tag "+tag);
        }
        return webView;
    }

}
