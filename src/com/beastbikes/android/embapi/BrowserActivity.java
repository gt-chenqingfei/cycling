package com.beastbikes.android.embapi;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.modules.pay.PayHelper;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.ui.MedalInfoActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.sharepopupwindow.CommonSharePopupWindow;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.DefaultWebViewClient;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.framework.android.webkit.RequestInterceptor;
import com.beastbikes.framework.ui.android.DefaultWebChromeClient;
import com.beastbikes.framework.ui.android.WebActivity;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@MenuResource(R.menu.task_info_browser_menu)
public class BrowserActivity extends WebActivity implements Constants {
    private static final String BASE_URL = "http://hybrid.beastbikes.com/1.0";
    private static final Logger logger = LoggerFactory.getLogger(BrowserActivity.class);
    private static final Map<String, RequestInterceptor> interceptors = new HashMap<String, RequestInterceptor>();
    public static final String EXTRA_MENU_STATUS = "menu_status";
    public final static int FILE_CHOOSER_RESULT_CODE = 1;
    public final static int FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 2;

    static {
        interceptors.put(BASE_URL + "/hybrid.js", new HybridInterceptor());
        interceptors.put(BASE_URL + "/agent", new AgentInterceptor());
        interceptors.put(BASE_URL + "/device", new DeviceInterceptor());
        interceptors.put(BASE_URL + "/auth", new UserInterceptor());
        interceptors.put(BASE_URL + "/connectivity/network",
                new NetworkInterceptor());
    }

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    protected boolean isShowShareMenu = false;
    private LoadingDialog loadingDialog;
    protected String title;
    protected String desc;
    protected String targetUrl;
    protected String iconUrl;
    protected PayHelper payHelper;
    protected CommonSharePopupWindow window;
    protected CommonShareLinkDTO commonShareLinkDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if (intent != null) {
            isShowShareMenu = intent.getBooleanExtra(EXTRA_MENU_STATUS, false);
            String host = intent.getData().getHost();
            if ("hybrid.speedx.com".equals(host)) {
                ActionBar bar = getSupportActionBar();
                if (bar != null) {
                    bar.hide();
                }
                canGoBack = true;
            }
        }

        loadingDialog = new LoadingDialog(BrowserActivity.this,
                getString(R.string.web_loading_msg), true);

        super.setWebViewClient(new DefaultWebViewClient(this) {

            @Override
            @TargetApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                final String method = request.getMethod();
                if (!"GET".equalsIgnoreCase(method)) {
                    return null;
                }

                final String url = request.getUrl().toString();
                if (!url.startsWith(BASE_URL)) {
                    return super.shouldInterceptRequest(view, request);
                }

                logger.trace("Intercepting1 " + url);

                final RequestInterceptor interceptor = interceptors.get(url);
                if (null != interceptor) {
                    final WebResourceResponse wrr = interceptor.intercept(view, request.getMethod(), url, request.getRequestHeaders());
                    if (wrr != null)
                        return wrr;
                }

                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (!url.startsWith(BASE_URL)) {
                    return super.shouldInterceptRequest(view, url);
                }

                logger.info("trace " + url);

                final RequestInterceptor interceptor = interceptors.get(url);
                if (null != interceptor) {
                    final WebResourceResponse wrr = interceptor.intercept(view, "GET", url, Collections.<String, String>emptyMap());
                    if (wrr != null)
                        return wrr;
                }

                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                Intent intent = SchemaInterceptor.interceptUrlSchema(uri, BrowserActivity.this);
                if (intent != null) {
                    return true;
                }
                final boolean isKitKat = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
                if (isKitKat) {
                    if (url != null && url.startsWith("sinaweibo://")) {
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                try {
                    if (!isFinishing() && null != loadingDialog && !loadingDialog.isShowing()) {
                        loadingDialog.show();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (!isFinishing() && null != loadingDialog) {
                        loadingDialog.dismiss();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                handler.proceed();
            }
        });

        super.setWebChromeClient(new DefaultWebChromeClient(this) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                BrowserActivity.this.title = title;
                setTitle(title);
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }

            //浏览器访问本地的位置信息，客户端必须设置
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

        });
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        } else if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            String result = intent.getExtras().getString("pay_result"); //返回值
            String errorMsg = intent.getExtras().getString("error_msg"); // 错误信息
            String extraMsg = intent.getExtras().getString("extra_msg"); // 错误信息
            doJsMethodCreatePaymentResponse(result, errorMsg, extraMsg);
        }
    }

    @Override
    public void finish() {
        if (null != this.loadingDialog && this.loadingDialog.isShowing()) {
            this.loadingDialog.dismiss();
        }
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return isShowShareMenu ? super.onCreateOptionsMenu(menu) : isShowShareMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.task_info_browser_menu_share:
                SpeedxAnalytics.onEvent(this, "", "click_cycling_event_event_share");
                if (getBrowser() != null) {
                    getBrowser().loadUrl("javascript:getShareInfo('android')");
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5);
    }

    /**
     * 显示分享
     */
    public void showShareWindow() {
        if (null == this.window) {
            commonShareLinkDTO = new CommonShareLinkDTO(iconUrl, title, targetUrl, desc, targetUrl, targetUrl);
            this.window = new CommonSharePopupWindow(this, commonShareLinkDTO, getTitle().toString());
        }
        this.window.showAtLocation(getBrowser(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void receiveAwardSuccess(int status) {
        super.receiveAwardSuccess(status);
        Intent intent = getIntent();
        intent.putExtra(MedalInfoActivity.EXTRA_AWARD_STATUS, status);
        setResult(RESULT_OK, intent);
        //web已经调用finish
    }

    @Override
    public int onJsMethodGetCountryCode(int countryCode[]) {
        countryCode[0] = LocaleManager.getCountryCode(this);
        return countryCode[0];
    }

    @Override
    public void onJsMethodSpeedxBridge(String data) {
        super.onJsMethodSpeedxBridge(data);

        if (data != null) {
            try {
                JSONObject json = new JSONObject(data);
                this.title = json.optString("title");
                this.targetUrl = json.optString("url");
                this.desc = json.optString("desc");
                this.iconUrl = json.optString("shareLogo");
                showShareWindow();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onJsMethodLightMedal(int badgeId) {
        super.onJsMethodLightMedal(badgeId);
        final Intent intent = new Intent(this, MedalInfoActivity.class);
        intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_ID, badgeId);
        startActivity(intent);
        UserManager userManager = new UserManager(this);
        userManager.updateUserMedalNum();
    }

    @Override
    public void onJsMethodFinishClubTransfer() {
        super.onJsMethodFinishClubTransfer();

        final Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            SharedPreferences userSp = getSharedPreferences(user.getObjectId(), 0);
            SharedPreferences.Editor editor = userSp.edit();
            editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_TRANSFER_MASTER, System.currentTimeMillis());
            int totalCount = userSp.getInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0);
            totalCount--;
            editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, totalCount);
            editor.apply();
        }

        this.finish();
    }

    @Override
    public void onJsMethodCreatePayment(String charge) {
        super.onJsMethodCreatePayment(charge);
        if (payHelper == null) {
            payHelper = new PayHelper(this);
        }
        payHelper.doPay(charge);
    }

    /**
     * 支付响应
     *
     * @param result   处理返回值
     *                 {
     *                 "success" - payment succeed,
     *                 "fail"    - payment failed,
     *                 "cancel"  - user canceld,
     *                 "invalid" - payment plugin not installed
     *                 }
     * @param errorMsg
     * @param extraMsg
     */
    public void doJsMethodCreatePaymentResponse(String result, String errorMsg, String extraMsg) {
        if (getBrowser() != null) {
            getBrowser().loadUrl("javascript:createPaymentResponse('" + result + "','" + errorMsg +
                    "','" + extraMsg + "')");
        }
    }

}
