package com.beastbikes.android.modules.setting.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beastbikes.android.R;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

/**
 * Created by caoxiao on 16/2/29.
 */

@LayoutResource(R.layout.layout_advertise_weview)
public class KeepAliveHelperActivity extends BaseFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://speedx.com/app/android/keepalivehelper.html");
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }


    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }


}
