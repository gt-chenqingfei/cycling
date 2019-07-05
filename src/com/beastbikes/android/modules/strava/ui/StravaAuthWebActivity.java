package com.beastbikes.android.modules.strava.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.strava.biz.StravaManager;
import com.beastbikes.framework.ui.android.DefaultWebChromeClient;
import com.beastbikes.framework.ui.android.DefaultWebViewClient;
import com.beastbikes.framework.ui.android.WebActivity;

import org.json.JSONObject;

/**
 *
 * Created by chenqingfei on 16/9/27.
 */
public class StravaAuthWebActivity extends WebActivity {
    public static final String EXTRA_TOKEN = "token";
    public static final String EXTRA_ERROR_MSG = "error_msg";
    String tag = "StravaAuthWebActivity";
    private LoadingDialog loadingDialog;

    private static final String DEFAULT_EMPTY_PAGE_URL = "file:///android_asset/"
            + "webkit/empty.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle(R.string.label_service_manager);

        super.setWebViewClient(new DefaultWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                Log.e(tag, "0000002" + url);
                if (uri != null) {
                    String host = uri.getHost();
                    String schema = uri.getScheme();
                    if (schema.equals("speedx") && host.equals("strava_callback_url_for_speedx")) {
                        String code = uri.getQueryParameter("code");

                        tokenExchange(code);
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
                try {
                    view.loadUrl(DEFAULT_EMPTY_PAGE_URL);
                } catch (Exception e) {

                }
            }
        });

        super.setWebChromeClient(new DefaultWebChromeClient(this) {

            @Override
            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
            }
        });

        super.onCreate(savedInstanceState);
    }


    public void tokenExchange(final String code) {
        if (TextUtils.isEmpty(code)) {
            finish();
            return;
        }

        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this, null, true);
        }

        loadingDialog.show();

        getAsyncTaskQueue().add(new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                JSONObject object = new StravaManager(StravaAuthWebActivity.this).
                        tokenExchange(code);
                Log.d(tag, object.toString());
                String token = object.optString("access_token");
                if (TextUtils.isEmpty(token)) {
                    token = object.toString();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                super.onPostExecute(token);
                Intent intent = getIntent();
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (!TextUtils.isEmpty(token)) {
                    intent.putExtra(EXTRA_TOKEN, token);
                    setResult(RESULT_OK, intent);
                } else {
                    intent.putExtra(EXTRA_ERROR_MSG, token);
                    setResult(RESULT_CANCELED, intent);
                }

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
