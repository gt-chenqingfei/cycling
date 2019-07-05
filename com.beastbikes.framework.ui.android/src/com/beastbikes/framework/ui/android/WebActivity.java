package com.beastbikes.framework.ui.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.beastbikes.framework.android.utils.PackageUtils;
import com.beastbikes.framework.android.utils.TelephonyUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebActivity extends BaseFragmentActivity {

    public static final String EXTRA_TITLE = "title";

    public static final String EXTRA_HTTP_HEADERS = "additional_http_headers";

    public static final String EXTRA_ENTER_ANIMATION = "enter_animation";

    public static final String EXTRA_EXIT_ANIMATION = "exit_animation";

    public static final String EXTRA_NONE_ANIMATION = "none_animation";

    public static final String EXTRA_CAN_GOBACK = "can_goback";

    private WebViewClient defaultWebViewClient;

    private WebChromeClient defaultWebChromeClient;

    private int enterAnim;

    private int exitAnim;

    private int noneAnim;

    private FrameLayout container;

    private WebView browser;

    private String userAgent;

    private String targetUrl;
    protected boolean canGoBack = false;

    public WebActivity() {
        this.defaultWebViewClient = new DefaultWebViewClient(this);
        this.defaultWebChromeClient = new DefaultWebChromeClient(this);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (intent == null) {
            this.finish();
            return;
        }
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        this.canGoBack = intent.getBooleanExtra(EXTRA_CAN_GOBACK, canGoBack);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                WebView.setWebContentsDebuggingEnabled(true);
            } catch (Exception e) {

            }
        }
        this.browser = new WebView(this);
        this.container = new FrameLayout(this);
        this.container.addView(this.browser, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setContentView(this.container, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        this.setupBrowser();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected WebView getBrowser() {
        return this.browser;
    }

    @Override
    public void finish() {
        super.overridePendingTransition(0, this.exitAnim);
        super.finish();
    }

    protected WebViewClient getWebViewClient() {
        return this.defaultWebViewClient;
    }

    protected WebChromeClient getWebChromeClient() {
        return this.defaultWebChromeClient;
    }

    protected void setWebViewClient(WebViewClient webViewClient) {
        this.defaultWebViewClient = webViewClient;
    }

    protected void setWebChromeClient(WebChromeClient webChromeClient) {
        this.defaultWebChromeClient = webChromeClient;
    }

    protected boolean handleURL(String url) {
        return false;
    }


    @SuppressLint("SetJavaScriptEnabled")
    protected void setupBrowser() {
        final Intent intent = getIntent();

        final String ver = PackageUtils.getVersionName(this);
        final String ext = getPackageName() + "/" + ver;
        final WebSettings settings = this.browser.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
        settings.setUserAgentString(buildUserAgent(this));
        settings.setBuiltInZoomControls(false);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(true);
        String dir = getDir("database", Context.MODE_PRIVATE).getPath();//加载地图的数据库路径（必须有）
        settings.setGeolocationEnabled(true);//允许加载地图
        settings.setGeolocationDatabasePath(dir);//设置加载的路径
        settings.setDomStorageEnabled(true);
        browser.addJavascriptInterface(new JavaScriptCallback(this), "speedx");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        this.userAgent = settings.getUserAgentString();
        this.browser.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                final Request request = new Request(Uri.parse(url));

                if (!TextUtils.isEmpty(userAgent)) {
                    request.addRequestHeader("User-Agent", userAgent);
                }

                if (!TextUtils.isEmpty(contentDisposition)) {
                    request.addRequestHeader("Content-Disposition", contentDisposition);
                }

                if (!TextUtils.isEmpty(mimetype)) {
                    request.setMimeType(mimetype);
                }

                request.allowScanningByMediaScanner();
                request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
                dm.enqueue(request);
            }
        });
        this.browser.setWebChromeClient(getWebChromeClient());
        this.browser.setWebViewClient(getWebViewClient());
        this.browser.setBackgroundColor(0xff222222);
        this.enterAnim = intent.getIntExtra(EXTRA_ENTER_ANIMATION, 0);
        this.exitAnim = intent.getIntExtra(EXTRA_EXIT_ANIMATION, 0);
        this.noneAnim = intent.getIntExtra(EXTRA_NONE_ANIMATION, 0);
        super.overridePendingTransition(this.enterAnim, this.noneAnim);

        final String title = intent.getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            this.setTitle(title);
        }

        targetUrl = intent.getDataString();
        if (!TextUtils.isEmpty(targetUrl)) {
            this.browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl(targetUrl, getRequestHeaders());

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (canGoBack) {
                    if (!browser.getUrl().equals(targetUrl)) {
                        browser.goBack();
                        return true;
                    }
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public static String buildUserAgent(final Context context) {
        final String osVersion = "Android/" + Build.VERSION.RELEASE;
        final PackageManager pm = context.getPackageManager();
        final String packageName = context.getPackageName();
        final String device = Build.FINGERPRINT;
        final String channel = getAppMetaData(context, "Channel ID");
        final String deviceId = TelephonyUtils.getDeviceId(context);

        try {
            final String versionName = pm.getPackageInfo(packageName, 0).versionName;
            return osVersion + ";" + device + ";Beast/" + versionName + "_"
                    + channel + ";" + deviceId;
        } catch (final Exception e) {
            return osVersion;
        }
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    private static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(),
                        PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    public Map<String, String> getRequestHeaders() {
        final Map<String, String> headers = new HashMap<String, String>();
        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle bundle = intent.getBundleExtra(EXTRA_HTTP_HEADERS);

            if (null != bundle && bundle.size() > 0) {
                final Set<String> names = bundle.keySet();
                for (final String key : names) {
                    final String value = bundle.getString(key);

                    if (!TextUtils.isEmpty(value)) {
                        headers.put(key, value);
                    }
                }
            }
        }

        return Collections.unmodifiableMap(headers);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (canGoBack) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                if (!browser.getUrl().equals(targetUrl)) {
                    browser.goBack();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    class JavaScriptCallback {
        Activity mActivity;

        public JavaScriptCallback(Activity activity) {
            this.mActivity = activity;
        }

        @JavascriptInterface
        public void speedxBridge(String data) {
            if (!TextUtils.isEmpty(data)) {
                onJsMethodSpeedxBridge(data);
            }
        }

        @JavascriptInterface
        public int getCountryCode() {
            int countryCode[] = {0};
            int code = onJsMethodGetCountryCode(countryCode);
            return countryCode[0];
        }

        @JavascriptInterface
        public void finish() {
            WebActivity.this.finish();
        }

        @JavascriptInterface
        public void lightMedal(int badgeId) {
            onJsMethodLightMedal(badgeId);
        }

        @JavascriptInterface
        public void finishClubTransfer() {
            onJsMethodFinishClubTransfer();
        }

        @JavascriptInterface
        public void createPayment(String charge) {
            onJsMethodCreatePayment(charge);
        }

        /**
         * 勋章前去领奖界面领奖成功回调
         * @param status 1：已经抽奖，没有获奖。    2：已经抽奖，获奖了，但是没有领取。  3 ： 已经领奖（不管抽奖与否)
         */
        @JavascriptInterface
        public void receiveAwardSuccess(int status) {
            WebActivity.this.receiveAwardSuccess(status);
        }
    }

    /**
     * 勋章前去领奖界面领奖成功回调
     * @param status 1：已经抽奖，没有获奖。    2：已经抽奖，获奖了，但是没有领取。  3 ： 已经领奖（不管抽奖与否)
     */
    protected void receiveAwardSuccess(int status) {

    }

    public void onJsMethodSpeedxBridge(String data) {

    }

    public void onJsMethodFinishClubTransfer() {

    }

    public int onJsMethodGetCountryCode(int[] countryCode) {

        return onJsMethodGetCountryCode(countryCode);
    }

    public void onJsMethodLightMedal(int badgeId) {
    }

    public void onJsMethodCreatePayment(String charge) {
    }
}
