package com.beastbikes.android.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.ui.AuthenticationActivity;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * The launcher of beast
 *
 * @author johnson
 */
@Alias("程序入口(无界面)")
@LayoutResource(R.layout.splash_activity)
public class MainActivity extends BaseActivity implements Constants {

    /**
     * 引导设置
     */
    public static final String PREF_GUIDE_SETTING = "guide_setting";

    private SharedPreferences sp;

    private MHandler mHandler = new MHandler(this);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sp = getSharedPreferences(getPackageName(), 0);

        new AdviertiseManager(MainActivity.this).adviertiseLoad();

        if (BeastBikes.hasBeenLaunched) {
            intentToNext();
        } else {
            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    intentToNext();
                }
            }, 3000L);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler = null;
        }
    }

    /**
     * 跳转到下个页面
     */
    private void intentToNext() {
        BeastBikes.hasBeenLaunched = true;
        if (!this.sp.getBoolean(TutorialActivity.PREF_HAS_SHOWN, false) && LocaleManager.isChineseTimeZone()) {
            this.startActivity(new Intent(this, TutorialActivity.class));
            this.finish();
        } else {
            this.gotoAuthenticationPageIfNeeded();
        }
    }


    private void gotoAuthenticationPageIfNeeded() {
        final AVUser usr = AVUser.getCurrentUser();
        if (null == usr || TextUtils.isEmpty(usr.getSessionToken()) /*|| !usr.isAuthenticated()*/) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            this.finish();
        } else {
            this.goHome();
        }
    }

    protected void goHome() {
        String brand = Build.BRAND;
        boolean isSetting = this.sp.getBoolean(PREF_GUIDE_SETTING, false);
        if (brand.equalsIgnoreCase("Xiaomi") && !isSetting) {
            this.goMiuiSetting();
            return;
        }

        if (brand.equalsIgnoreCase("Meizu") && !isSetting) {
            this.goMeiZuSetting();
            return;
        }

        AVUser user = AVUser.getCurrentUser();
        if (null != user) {

            android.webkit.CookieManager manager = android.webkit.CookieManager.getInstance();
            manager.setCookie(UrlConfig.DEV_SPEEDX_HOST_DOMAIN,
                    "sessionid=" + user.getSessionToken());

        }

        Intent homeIntent = new Intent(this, HomeActivity.class);
        Intent intent = getIntent();
        if (null != intent) {
            String pushData = intent
                    .getStringExtra(Constants.PUSH_START_ACTIVITY_DATA);
            if (!TextUtils.isEmpty(pushData)) {
                homeIntent.putExtra(Constants.PUSH_START_ACTIVITY_DATA,
                        pushData);
            }
            //rongcloud
            String rongPush = intent.getStringExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY);
            if (!TextUtils.isEmpty(rongPush)) {
                Log.e("rongPush", rongPush);
                homeIntent.putExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY,
                        rongPush);
            } else {
                Log.e("rongPush", "null");
            }
            if(null != getIntent()) {
                Uri schemaData = getIntent().getData();
                if (schemaData != null) {
                    homeIntent.setData(schemaData);
                }
            }
        }
        this.startActivity(homeIntent);
        this.finish();
    }

    /**
     * Jump miui6 follow setting
     */
    private void goMiuiSetting() {
        Intent miuiIntent = new Intent(this, MiuiSettingActivity.class);
        startActivity(miuiIntent);
        this.sp.edit().putBoolean(PREF_GUIDE_SETTING, true).apply();
        this.finish();
    }

    /**
     * Jump MEIZU follow setting
     */
    private void goMeiZuSetting() {
        Intent meiZuIntent = new Intent(this, MeiZuSettingActivity.class);
        this.startActivity(meiZuIntent);
        this.sp.edit().putBoolean(PREF_GUIDE_SETTING, true).apply();
        this.finish();
    }

    /**
     * avoid memory leak
     */
    private static class MHandler extends Handler {

        private WeakReference<MainActivity> mMainActivity;

        public MHandler(MainActivity mainActivity) {
            this.mMainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = mMainActivity.get();
            if (null != mainActivity) {
                mainActivity.intentToNext();
            }
        }
    }

}
