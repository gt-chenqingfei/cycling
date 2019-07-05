package com.beastbikes.framework.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.ViewConfiguration;

import com.beastbikes.framework.android.runtime.DefaultUncaughtExceptionHandler;
import com.beastbikes.framework.android.utils.ChannelUtil;
import com.beastbikes.framework.android.utils.PackageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * An implementation of interface {@link ApplicationContext} for Android
 *
 * @author johnson
 */
public abstract class ApplicationContext extends MultiDexApplication implements
        ActivityLifecycleCallbacks {

    static {
        System.loadLibrary("trace");
    }

    private static final Logger logger = LoggerFactory.getLogger("ApplicationContext");

    private static ApplicationContext instance;

    private Object cache;

    public static final ApplicationContext getInstance() {
        return instance;
    }

    public ApplicationContext() {
        ApplicationContext.instance = this;
    }

    /**
     * Returns the value of the specified meta data
     *
     * @param key          The name of meta data
     * @param defaultValue The default value if the specified meta data not found
     * @return The value of the specified meta data, if not found, the
     * {@code defaultValue} is returned.
     */
    public String getMetaData(String key, String defaultValue) {
        final String pkg = getPackageName();
        final PackageManager pm = getPackageManager();

        try {
            final ApplicationInfo ai = pm.getApplicationInfo(pkg,
                    PackageManager.GET_META_DATA);
            final Bundle metaData = ai.metaData;
            if (null == metaData)
                return null;

            return metaData.getString(key, defaultValue);
        } catch (NameNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String getMetaData(Context context, String metaKey) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            String umengAppKey = appInfo.metaData.getString(metaKey);
            return umengAppKey;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            logger.error("PackageManager.NameNotFoundException");
            return "";
        }
    }

    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onCreate() {
        super.onCreate();

        // setup uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(this));

        // register activity life-cycle listener
        this.registerActivityLifecycleCallbacks(this);

        // show action overflow button in force
        final ViewConfiguration vc = ViewConfiguration.get(this);
        try {
            final Class<?> clazz = vc.getClass();
            final Field f = clazz.getDeclaredField("sHasPermanentMenuKey");
            if (f != null) {
                f.setAccessible(true);
                f.setBoolean(vc, false);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        final long blockCount;
        final long blockSize;

        try {
            final File dir = new File(getCacheDir(), "http");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            final StatFs stat = new StatFs(dir.getAbsolutePath());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockCount = stat.getBlockCountLong();
            } else {
                blockCount = stat.getBlockCount();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
            } else {
                blockSize = stat.getBlockSize();
            }

            final Class<?> clazz = Class.forName("android.net.http.HttpResponseCache");
            final Method install = clazz.getMethod("install", File.class, long.class);
            this.cache = install.invoke(clazz, dir, blockCount * blockSize);
        } catch (final Exception e) {
            logger.error("Install http response cache error", e);
        }

        logger.info(getLineSeparator(80, getPackageName()));
        logger.info("> ro.bootloader                : " + Build.BOOTLOADER);
        logger.info("> ro.build.id                  : " + Build.ID);
        logger.info("> ro.build.display.id          : " + Build.DISPLAY);
        logger.info("> ro.build.version.incremental : " + Build.VERSION.INCREMENTAL);
        logger.info("> ro.build.version.release     : " + Build.VERSION.RELEASE);
        logger.info("> ro.build.version.sdk         : " + Build.VERSION.SDK_INT);
        logger.info("> ro.build.version.codename    : " + Build.VERSION.CODENAME);
        logger.info("> ro.build.type                : " + Build.TYPE);
        logger.info("> ro.build.tags                : " + Build.TAGS);
        logger.info("> ro.build.fingerprint         : " + Build.FINGERPRINT);
        logger.info("> ro.build.date.utc            : " + Build.TIME);
        logger.info("> ro.build.user                : " + Build.USER);
        logger.info("> ro.build.host                : " + Build.HOST);
        logger.info("> ro.hardware                  : " + Build.HARDWARE);
        logger.info("> ro.product.board             : " + Build.BOARD);
        logger.info("> ro.product.brand             : " + Build.BRAND);
        logger.info("> ro.product.cpu.abi           : " + Build.CPU_ABI);
        logger.info("> ro.product.cpu.abi2          : " + Build.CPU_ABI2);
        logger.info("> ro.product.device            : " + Build.DEVICE);
        logger.info("> ro.product.manufacturer      : " + Build.MANUFACTURER);
        logger.info("> ro.product.model             : " + Build.MODEL);
        logger.info("> ro.product.name              : " + Build.PRODUCT);
        logger.info("> ro.serialno                  : " + Build.SERIAL);
        logger.info("> ro.versionCode               : " + PackageUtils.getVersionCode(this));
        logger.info("> ro.versionName               : " + PackageUtils.getVersionName(this));
        logger.info("> ro.channel                   : " + ChannelUtil.getChannel(this));
        logger.info("================================================================================");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onTerminate() {
        if (null != this.cache) {
            try {
                final Class<?> clazz = Class.forName("android.net.http.HttpResponseCache");
                final Method close = clazz.getMethod("close");
                close.invoke(this.cache);
            } catch (final Exception e) {
                logger.error("Close http response cache error", e);
            }
        }

        super.onTerminate();
    }

    private static String getLineSeparator(int width, String title) {
        final int w = Math.max(0, width - 2);
        final int l = (w - title.length()) / 2;
        final int r = (w - title.length() - l);
        final StringBuilder seprator = new StringBuilder();

        for (int i = 0; i < l; i++) {
            seprator.append('=');
        }

        seprator.append(' ').append(title).append(' ');

        for (int i = 0; i < r; i++) {
            seprator.append('=');
        }
        return seprator.toString();
    }
}
