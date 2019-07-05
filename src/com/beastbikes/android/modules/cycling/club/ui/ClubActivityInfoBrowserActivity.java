package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.locale.LocaleManager;

public class ClubActivityInfoBrowserActivity extends BrowserActivity implements Constants {
    static final String CLUB_ACTIVITY_URL = "/club-activity-share-detail.html?activityId=";

    /**
     * 0 添加， 1 分享
     */
    public static final String EXTRA_ACTIVITY_TYPE = "activity_type";
    /**
     * 俱乐部管理权限
     */
    public static final String EXTRA_CLUB_ACTIVITY_LEVEL = "club_level";
    /**
     * 俱乐部活动ID
     */
    public static final String EXTRA_CLUB_ACTIVITY_ID = "activity_id";

//    public static final String CREATE_CLUB_ACTIVITY_URL = "/club/publish.html";

    private int menuType;
    private int level;
    private String activityId;

    public static String getActivityUrl(String activityId, Context context) {
        final StringBuilder sb = new StringBuilder(RestfulAPI.BASE_WEB_URL);
        sb.append(ClubActivityInfoBrowserActivity.CLUB_ACTIVITY_URL);
        sb.append(activityId);
        sb.append("&areaCode=" + LocaleManager.getCountryCode(context));
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        Intent intent = getIntent();
        if (null == intent)
            return;

        this.menuType = intent.getIntExtra(EXTRA_ACTIVITY_TYPE, -1);
        this.level = intent.getIntExtra(EXTRA_CLUB_ACTIVITY_LEVEL, 0);
        this.activityId = intent.getStringExtra(EXTRA_CLUB_ACTIVITY_ID);
        isShowShareMenu = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        switch (menuType) {
            case 0:
                if (level == 128) {
                    getMenuInflater().inflate(R.menu.add_menu, menu);
                } else {
                    return false;
                }
                break;
            case 1:
                if (TextUtils.isEmpty(this.activityId)) {
                    return false;
                }
                isShowShareMenu = true;
                getMenuInflater().inflate(R.menu.share_menu, menu);
                break;
            default:
                return false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                if (TextUtils.isEmpty(this.activityId)) {
                    return false;
                }
                WebView webView = getBrowser();
                if (null != webView) {
                    webView.loadUrl("javascript:closeEnrollForm()");
                }
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc)) {
                    if (webView != null) {
                        webView.loadUrl("javascript:getShareInfo('android')");
                    }
                } else {
                    showShareWindow();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

}
