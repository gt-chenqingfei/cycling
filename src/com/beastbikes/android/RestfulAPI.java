package com.beastbikes.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.framework.android.utils.TelephonyUtils;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RestfulAPI {
    public  static final String API_VERSION= "v2.4";
    public static final String BASE_URL = Constants.UrlConfig.DEV_SPEEDX_API+API_VERSION;
    public static final String BASE_WEB_URL = Constants.UrlConfig.DEV_SPEEDX_HOST+"/app/"+API_VERSION;

    public RestfulAPI() {
    }

    public static Map<String, String> getParams(Context context) {
        final Map<String, String> params = new TreeMap<>();
        params.put("User-Agent", buildUserAgent(context));
        params.put("X-Client-Lang", Locale.getDefault().getLanguage());
        if (null != AVUser.getCurrentUser()) {
            params.put("COOKIE", "sessionid=" + AVUser.getCurrentUser().getSessionToken());
        }
        return params;
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

    /**
     * 设置webkit cookie
     */
    public static void cookieSync() {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        if (AVUser.getCurrentUser() != null) {
            StringBuilder cookieBuilder = new StringBuilder("sessionid=");
            cookieBuilder.append(AVUser.getCurrentUser().getSessionToken()).append(";");
            cookieBuilder.append("domain=.speedx.com;");
            cookieManager.setCookie(".speedx.com", cookieBuilder.toString());
        }
    }
}
