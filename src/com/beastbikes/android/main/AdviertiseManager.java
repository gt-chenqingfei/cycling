package com.beastbikes.android.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.utils.BitmapLoadManager;
import com.umeng.onlineconfig.OnlineConfigAgent;

/**
 * Created by zhangyao on 2016/2/26.
 */
public class AdviertiseManager {

    private static final String PREF_AD_ISOPEN_URL_KEY = "open_launch_ad";
    private static final String PREF_AD_ISOPEN_URL_KEY_EN = "open_launch_ad_en";
    private static final String PREF_AD_IMAGE_URL_KEY = "launch_ad_image_url";
    private static final String PREF_AD_TARGET_URL_KEY = "launch_ad_image_target_url";
    private static final String PREF_AD_IMAGE_URL_KEY_EN = "launch_ad_image_url_en";
    private static final String PREF_AD_TARGET_URL_KEY_EN = "launch_ad_image_target_url_en";
    public static final String PREF_AD_URL = "url";
    public static final String PREF_AD_IMGURL = "img_url";
    private static final String PREF_AD_ISOPEN = "isopen";
    public static final String  OPENED = "1";
    private static final String PREF_AD_ADVERTY = "advertise";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean isOpen = false;
    private String url = "";
    private String imgUrl = "";
    private Context context;


    public AdviertiseManager(Context context) {

        this.context = context.getApplicationContext();

        sp = this.context.getSharedPreferences(PREF_AD_ADVERTY, Context.MODE_PRIVATE);
        editor = sp.edit();

        isOpen = sp.getString(PREF_AD_ISOPEN, "0").equals(OPENED);
        imgUrl = sp.getString(PREF_AD_IMGURL, "");
        url = sp.getString(PREF_AD_URL, "");
    }


    /**
     * 预加载下一次广告信息
     */
    public void adviertiseLoadPre() {
        if(context == null)
            return;

        String isopen = "";
        String adImgUrl = "";
        String adUrl = "";

        if (LocaleManager.isChineseTimeZone()) {
            isopen = OnlineConfigAgent.getInstance().getConfigParams(context, PREF_AD_ISOPEN_URL_KEY);
            adImgUrl = OnlineConfigAgent.getInstance().getConfigParams(context, PREF_AD_IMAGE_URL_KEY);
            adUrl = OnlineConfigAgent.getInstance().getConfigParams(context, PREF_AD_TARGET_URL_KEY);
        } else {
            isopen = OnlineConfigAgent.getInstance().getConfigParams(context, PREF_AD_ISOPEN_URL_KEY_EN);
            adImgUrl = OnlineConfigAgent.getInstance().getConfigParams(context, PREF_AD_IMAGE_URL_KEY_EN);
            adUrl = OnlineConfigAgent.getInstance().getConfigParams(context, PREF_AD_TARGET_URL_KEY_EN);
        }

        if(TextUtils.isEmpty(adImgUrl))
            return;

        if (!imgUrl.equals(adImgUrl)) {
            BitmapLoadManager.deleteBitmap(imgUrl, context);
        }

        if (!BitmapLoadManager.isCache(adImgUrl, context)) {
            BitmapLoadManager.fetchImage(adImgUrl, context);
        }

        editor.putString(PREF_AD_IMGURL, adImgUrl);
        editor.putString(PREF_AD_URL, adUrl);
        editor.putString(PREF_AD_ISOPEN, isopen);
        editor.commit();
    }


    public void adviertiseLoad() {
        if(context == null || TextUtils.isEmpty(imgUrl))
            return;

        boolean isCache = BitmapLoadManager.isCache(imgUrl, context);
        if (isCache
                && AVUser.getCurrentUser() != null
                && isOpen) {
            Intent intent = new Intent(context, AdvertiseActivity.class);
            intent.putExtra(AdviertiseManager.PREF_AD_URL, this.url);
            intent.putExtra(AdviertiseManager.PREF_AD_IMGURL, this.imgUrl);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
