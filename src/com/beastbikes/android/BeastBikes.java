package com.beastbikes.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

import com.avos.avoscloud.AVAnalytics;
import com.baidu.mapapi.SDKInitializer;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.main.AdvService;
import com.beastbikes.android.modules.cycling.SyncService;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityService;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.persistence.BeastPersistenceManager;
import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.android.utils.SpUtil.DefaultSP;
import com.beastbikes.android.utils.SpUtil.PacketSP;
import com.beastbikes.framework.android.ApplicationContext;
import com.beastbikes.framework.android.runtime.DefaultUncaughtExceptionHandler;
import com.beastbikes.framework.android.utils.ChannelUtil;
import com.beastbikes.framework.android.utils.PackageUtils;
import com.beastbikes.framework.android.utils.ProcessUtils;
import com.beastbikes.framework.business.BusinessContext;
import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Locale;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import io.fabric.sdk.android.Fabric;

public class BeastBikes extends ApplicationContext implements Constants,
        BusinessContext, OnSharedPreferenceChangeListener {

    static {
        System.loadLibrary("beastbikes-jni");
        // config logback
        BasicLogcatConfigurator.configureDefaultContext();
    }

    private static final String TAG = "BeastBikes";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private BeastPersistenceManager persistenceManager;

    public static boolean hasBeenLaunched = false;
    public static boolean isDebug = false;
    /**
     * 设置devMode只有isDebug = true 才会生效
     * isDebug = false 是线上环境
     * tester 是用于测试环境
     * dev 是用于开发环境
     * devMode{tester,dev1,dev2,dev3,dev4,dev5,...}
     */
    public static String devMode = "tester";

    private ActivityManager activityManager;

    public BeastPersistenceManager getPersistenceManager() {
        return this.persistenceManager;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        final Object value = sp.getAll().get(key);
        logger.trace("onSharedPreferenceChanged: " + key + " =" + String.valueOf(value));
    }

    @Override
    public String getErrorMessage(int errorCode) {
        return null;
    }

    @Override
    public String getErrorMessage(Locale locale, int errorCode) {
        return null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

    }

    @Override
    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        AVAnalytics.onPause(activity);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        AVAnalytics.onResume(activity);
        MobclickAgent.onResume(activity);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onCreate() {
        isDebug = isDebug(this);
        devMode = getMetaData(this, "DEV_MODE");
        final String processName = ProcessUtils.getCurrentProcessName(this);
        if (TextUtils.isEmpty(processName) || processName.endsWith(":remote")
                || processName.endsWith(":DownloadingService")
                || processName.endsWith(":ipc")
                || processName.endsWith("io.rong.push")) {
            logger.trace("======================== " + processName + " ========================");
            return;
        }
        super.onCreate();

        if (isDebug) {
            Stetho.initializeWithDefaults(this);
        }

        try {
            ShareSDK.initSDK(this);
        } catch (RuntimeException e) {
            logger.error("ShareSDK Exception e=" + e.getMessage());
        }

        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(this));

        //UMeng配置channel
        String channel = ChannelUtil.getChannel(this);
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,
                getMetaData(this, "UMENG_APPKEY"), channel));
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);//友盟在线参数

        JPushInterface.setDebugMode(isDebug);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        this.native_initialize(isDebug);

        AVAnalytics.setAnalyticsEnabled(!BuildConfig.DEBUG);
        AVUser.initAVCloudUser();

        BeastStore.Caches.initialize(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getTwitterConsumerKey(), getTwitterConsumerSecret());
        Fabric.with(this, new Twitter(authConfig));

        // Initialize BaiduMap SDK
        SDKInitializer.initialize(this);

        FacebookSdk.sdkInitialize(this);

        CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(this);
        userStrategy.setAppChannel(channel);
        userStrategy.setAppVersion(PackageUtils.getVersionName(this));
        userStrategy.setAppReportDelay(5000);
        CrashReport.initCrashReport(this, getBugglyAppId(), isDebug, userStrategy);


        try {
            // Initialize preferences with default values
            PreferenceManager.setDefaultValues(this, R.xml.beastbikes, true);
        } catch (Exception e) {

        }

        this.persistenceManager = new BeastPersistenceManager(this);
        DefaultSP.getInstance().registerListener(this, this);
        PacketSP.getInstance().registerListener(this, this);

        // Start sync service
        if (AVUser.getCurrentUser() != null) {
            logger.info("Start SyncService ");
            try {
                this.startService(new Intent(this, SyncService.class));
                this.startService(new Intent(this, AdvService.class));
            } catch (Exception e) {
                logger.info("OPPO Service SecurityException");
            }
        } else {
            logger.info("Start SyncService fail, because user is null!");
        }

        this.activityManager = new ActivityManager(this);
        String activityId = ActivityManager.getCurrentActivityId(this);
        if (!TextUtils.isEmpty(activityId)) {
            logger.trace("activityId = " + activityId);
            try {
                Intent intent = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
                intent.setPackage(getPackageName());
                startService(intent);
            } catch (SecurityException e) {
                logger.error("Start activity service error, " + e);
            }
        } else {
            logger.info("activityId = null ");
        }

        RongCloudManager.init(this, getRongCloudKey());

        CookieSyncManager.createInstance(this);
        CookieHandler.setDefault(new CookieManager());
    }

    @Override
    public void onTerminate() {
        this.native_finalize();
        DefaultSP.getInstance().unRegisterListener(this, this);
        PacketSP.getInstance().unRegisterListener(this, this);
        super.onTerminate();
    }

    public int getAccuracySetting() {
        return DefaultSP.getInstance().getInt(this, PREF_SETTING_ACCURACY, 0);
    }

    public void setAccuracySetting(int value) {
        DefaultSP.getInstance().put(this, PREF_SETTING_ACCURACY, value).apply();
    }

    public void setVoiceFeedbackEnabled(int flag, boolean b) {
        final int v = DefaultSP.getInstance().getInt(this, PREF_SETTING_VOICE_FEEDBACK, 0);
        final int f = b ? (v | flag) : (v & (~flag));
        DefaultSP.getInstance().put(this, PREF_SETTING_VOICE_FEEDBACK, f).apply();
    }

    public boolean isVoiceFeedbackEnabled(int flag) {
        return 0 != (flag & DefaultSP.getInstance().getInt(this, PREF_SETTING_VOICE_FEEDBACK, 0));
    }

    public void setAutoPauseEnabled(boolean b) {
        DefaultSP.getInstance().put(this, PREF_SETTING_AUTO_PAUSE, b).apply();
    }

    public boolean isAutoPauseEnabled() {
        return DefaultSP.getInstance().getBoolean(this, PREF_SETTING_AUTO_PAUSE, false);
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    /**
     * 骑行状态下应用置于前台
     *
     * @param b
     */
    public void setForeGroundEnabled(boolean b) {
        DefaultSP.getInstance().put(this, PREF_SETTING_FOREGROUND, b).apply();
    }

    public boolean isForeGroundEnabled() {
        return DefaultSP.getInstance().getBoolean(this, PREF_SETTING_FOREGROUND, true);
    }

    /**
     * 地图样式优化
     *
     * @param enable
     */
    public void setMapStyleEnabled(boolean enable) {
        DefaultSP.getInstance().put(this, PREF_SETTING_MAP_STYLE, enable).apply();
    }

    public boolean isMapStyleEnabled() {
        return DefaultSP.getInstance().getBoolean(this, PREF_SETTING_MAP_STYLE, false);
    }

    /**
     * 设置骑行过程是否常亮骑行页
     *
     * @param enable
     */
    public void setCyclingScreenOnEnable(boolean enable) {
        DefaultSP.getInstance().put(this, PREF_SETTING_CYCLING_KEEP_SCREEN_ON, enable).apply();
    }

    public boolean isCyclingScreenOnEnable() {
        return DefaultSP.getInstance().getBoolean(this, PREF_SETTING_CYCLING_KEEP_SCREEN_ON, false);
    }

    private native void native_initialize(boolean isDebug);

    private native void native_finalize();

    public static native String getHost();

    public static native String getHostDomain();

    public static native String getApiUrl();

    public static native String getMapBoxAccessToken();

    public static native String getUserPrivateKey();

    public static native String getRongCloudKey();

    public static native String getTwitterConsumerKey();

    public static native String getTwitterConsumerSecret();

    public static native String getBugglyAppId();
}
