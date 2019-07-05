package com.beastbikes.android.widget.sharepopupwindow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.beastbikes.android.R;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareImageDTO;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by caoxiao on 16/5/18.
 */
public class CommonShareImage {
    private String sharePath;
    private Activity activity;
    private Logger logger = LoggerFactory.getLogger(CommonShareImage.class);

    public CommonShareImage(Activity activity, CommonShareImageDTO commonShareImageDTO) {
        this.sharePath = commonShareImageDTO.getImagePath();
        this.activity = activity;
    }

    public void wechatBuildShare() {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setImagePath(sharePath);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform wechatMoments = ShareSDK.getPlatform(Wechat.NAME);
        wechatMoments.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("wechatBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("wechatBuildShare onError:" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("wechatBuildShare onCancel");
            }
        });
        // 设置分享事件回调
        // 执行图文分享
        wechatMoments.share(sp);
    }

    public void WechatMomentsBuildShare() {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setImagePath(sharePath);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        wechatMoments.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("WechatMomentsBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("WechatMomentsBuildShare onError:" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("WechatMomentsBuildShare onCancel");
            }
        });
        // 设置分享事件回调
        // 执行图文分享
        wechatMoments.share(sp);
    }

    public void QQBuildShare() {
        QQ.ShareParams spQQ = new QQ.ShareParams();
        spQQ.setImagePath(sharePath);
        spQQ.setShareType(Platform.SHARE_IMAGE);
        Platform platformQQ = ShareSDK.getPlatform(QQ.NAME);
        platformQQ.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("QQBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("QQBuildShare onError:" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("QQBuildShare onCancel");
            }
        }); // 设置分享事件回调
        // 执行图文分享
        platformQQ.share(spQQ);
    }

    public void WeiboBuildShare() {
        SinaWeibo.ShareParams spWeiBo = new SinaWeibo.ShareParams();
        spWeiBo.setText(activity.getResources().getString(R.string.weibo_topic) + " " + activity.getResources().getString(R.string.weibo_offical));
        spWeiBo.setImagePath(sharePath);

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("WeiboBuildShare", "onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("WeiboBuildShare", "onError");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.info("WeiboBuildShare", "onCancel");
            }
        }); // 设置分享事件回调
        // 执行图文分享
        weibo.share(spWeiBo);
    }

    public void facebookBuildShare() {
        if (!FacebookUtil.checkFbInstalled(activity)) {

            return;
        }
        try {
            Bitmap imageBitmap = BitmapFactory.decodeFile(sharePath);
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(imageBitmap)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            ShareDialog.show(activity, content);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void twitterBuildShare() {
        File myImageFile = new File(sharePath);
        Uri myImageUri = Uri.fromFile(myImageFile);
        TweetComposer.Builder builder = new TweetComposer.Builder(activity)
                .image(myImageUri);
        builder.show();
    }

    public void saveToSdCard() {
        BitmapUtil.saveToSdCard(sharePath);
    }

}
