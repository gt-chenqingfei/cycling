package com.beastbikes.android.authentication;


import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by caoxiao on 16/1/26.
 */
public class AuthenticationFactory {

    private static MyPlatformActionListener paListener = new MyPlatformActionListener();
    private static ShareSDKUserInfoCallBack mShareSDKUserInfoCallBack;
    private static final Logger logger = LoggerFactory
            .getLogger(AuthenticationFactory.class);
    //    public final static String[] shareSdkArray = new String[]{"WEIBO_TYPE", "WECHAT_TYPE", "EMAIL_TYPE", "MOBILE_TYPE", "QQ_TYPE"};
    private static String type;

    //1 email, 2 mobilephone, 4 weibo, 8 qq, 16 weixin, 32 twitter, 64 facebook, 128 google plus
    public final static int TYPE_EMAIL = 1;
    public final static int TYPE_MOBILE_PHONE = 2;
    public final static int TYPE_WEIBO = 4;
    public final static int TYPE_QQ = 8;
    public final static int TYPE_WEIXIN = 16;
    public final static int TYPE_TWITTER = 32;
    public final static int TYPE_FACEBOOK = 64;
    public final static int TYPE_GOOGLE_PLUS = 128;
    public final static int TYPE_STRAVA = 256;

    public final static String SDK_WEIBO = "SinaWeibo";
    public final static String SDK_QQ = "QQ";
    public final static String SDK_WECHAT = "Wechat";
    public final static String SDK_FACEBOOK = "Facebook";
    public final static String SDK_TWITTER = "Twitter";
    public final static String SDK_GOOGLE_PLUS = "GooglePlus";
    public final static String SDK_STRAVA = "Strava";

    public static void authAndGetUserInfo(Context context, String name, ShareSDKUserInfoCallBack shareSDKUserInfoCallBack) {
        try {
            ShareSDK.initSDK(context);
        } catch (RuntimeException e) {
            logger.error("ShareSDK Exception e=" + e.getMessage());
            return;
        }
        type = name;
        Context mContext = new WeakReference<>(context).get();
        mShareSDKUserInfoCallBack = new WeakReference<>(shareSDKUserInfoCallBack).get();
        Platform platform = ShareSDK.getPlatform(mContext, name);
        if (platform != null) {
            platform.removeAccount(true);
            platform.setPlatformActionListener(paListener);
            platform.showUser(null);
        }
//        if (platform.isValid()) {//已经授权了获取数据库的信息,实际是SP
//            PlatformDb db = platform.getDb();
//            String accessToken = db.getToken(); // 获取授权token
//            String openId = db.getUserId(); // 获取用户在此平台的ID
//            String nickname = db.get("nickname"); // 获取用户昵称
//            logger.info("accessToken:" + accessToken);
//            logger.info("openId:" + openId);
//            logger.info("nickname:" + nickname);
//            if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(openId) || TextUtils.isEmpty(nickname)) {//数据库获取不完整
//                platform.setPlatformActionListener(paListener);
//                platform.showUser(null);//执行登录，登录后在回调里面获取用户资料
//            } else {
//                ShareSDKUserInfoBean shareSDKUserInfoBean = new ShareSDKUserInfoBean(accessToken, openId, nickname, type);
//                mShareSDKUserInfoCallBack.getShareSDKUserInfoCallBack(shareSDKUserInfoBean);
//            }
//        } else {

//            platform.authorize();

//        }
    }

    public static void getUserInfo(Context context, String name, ShareSDKUserInfoCallBack shareSDKUserInfoCallBack) {
        try {
            ShareSDK.initSDK(context);
        } catch (RuntimeException e) {
            logger.error("ShareSDK Exception e=" + e.getMessage());
            return;
        }
        type = name;
        Context mContext = new WeakReference<>(context).get();
        mShareSDKUserInfoCallBack = new WeakReference<>(shareSDKUserInfoCallBack).get();
        Platform platform = ShareSDK.getPlatform(mContext, name);
        platform.setPlatformActionListener(paListener);
        platform.showUser(null);
    }

    public static void removeAccountFromType(Context context, int type) {
        try {
            ShareSDK.initSDK(context);
        } catch (RuntimeException e) {
            logger.error("ShareSDK Exception e=" + e.getMessage());
            return;
        }
        switch (type) {
            case TYPE_WEIBO:
                removeAccount(context, SinaWeibo.NAME);
                break;
            case TYPE_QQ:
                removeAccount(context, QQ.NAME);
                break;
            case TYPE_WEIXIN:
                removeAccount(context, Wechat.NAME);
                break;
            case TYPE_TWITTER:
                removeAccount(context, Twitter.NAME);
                break;
            case TYPE_FACEBOOK:
                removeAccount(context, Facebook.NAME);
                break;
            case TYPE_GOOGLE_PLUS:
                removeAccount(context, GooglePlus.NAME);
                break;
            default:
                break;
        }
    }

    public static void removeAccount(Context context, String name) {
        try {
            ShareSDK.initSDK(context);
        } catch (RuntimeException e) {
            logger.error("ShareSDK Exception e=" + e.getMessage());
            return;
        }
        Platform platform = ShareSDK.getPlatform(context, name);
        if (platform == null)
            return;
        platform.removeAccount();
    }

    private static class MyPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            logger.info("MyPlatformActionListener onComplete");
            if (platform == null || platform.getDb() == null) {
                mShareSDKUserInfoCallBack.getShareSDKUserInfoCallBack(null);
                return;
            }
            PlatformDb db = platform.getDb();
            String accessToken = db.getToken(); // 获取授权token
            String openId = db.getUserId(); // 获取用户在此平台的ID
            String nickname = db.get("nickname"); // 获取用户昵称
            String tokenSecret = db.getTokenSecret();
            logger.info("accessToken:" + accessToken);
            logger.info("openId:" + openId);
            logger.info("nickname:" + nickname);
            logger.info("tokenSecret:" + tokenSecret);
            AuthenticationBean shareSDKUserInfoBean = new AuthenticationBean(accessToken, openId, nickname, tokenSecret, type);
            try {
                if (mShareSDKUserInfoCallBack != null) {
                    mShareSDKUserInfoCallBack.getShareSDKUserInfoCallBack(shareSDKUserInfoBean);
                }
            } catch (Exception e) {
                logger.error("MyPlatformActionListener onComplete catch Exception");
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            logger.error("MyPlatformActionListener onError");
            logger.error("MyPlatformActionListener onError" + throwable.getMessage());
            try {
                if (mShareSDKUserInfoCallBack != null) {
                    mShareSDKUserInfoCallBack.getShareSDKUserInfoCallBack(null);
                }
            } catch (Exception e) {
                logger.error("MyPlatformActionListener onError catch Exception");
            }
        }

        @Override
        public void onCancel(Platform platform, int i) {
            logger.error("MyPlatformActionListener onCancel");
            try {
                mShareSDKUserInfoCallBack.getShareSDKUserInfoCallBack(null);
            } catch (Exception e) {
                logger.error("MyPlatformActionListener onCancel catch Exception");
            }
        }
    }

    public static void removeALLAccount(Context context) {
        try {
            ShareSDK.initSDK(context);
        } catch (RuntimeException e) {
            logger.error("ShareSDK Exception e=" + e.getMessage());
            return;
        }
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (weibo != null) {
            weibo.removeAccount();
            weibo.removeAccount(true);
        }
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (qq != null) {
            qq.removeAccount();
            qq.removeAccount(true);
        }
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        if (wechat != null) {
            wechat.removeAccount();
            wechat.removeAccount(true);
        }
        Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
        if (twitter != null) {
            twitter.removeAccount();
            twitter.removeAccount(true);
        }
        Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
        if (facebook != null) {
            facebook.removeAccount();
            facebook.removeAccount(true);
        }
        Platform googlePlus = ShareSDK.getPlatform(GooglePlus.NAME);
        if (googlePlus != null) {
            googlePlus.removeAccount();
            googlePlus.removeAccount(true);
        }
        ShareSDK.removeCookieOnAuthorize(true);
    }

    public interface ShareSDKUserInfoCallBack {
        void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean);
    }
}