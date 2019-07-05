package com.beastbikes.android.widget.sharepopupwindow;

import android.net.Uri;

import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
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
public class CommonShareLink {

    private BaseFragmentActivity activity;
    private CommonShareLinkDTO commonShareLinkDTO;
    private Logger logger = LoggerFactory.getLogger(CommonShareLink.class);

    public CommonShareLink(BaseFragmentActivity activity, CommonShareLinkDTO commonShareLinkDTO) {
        this.commonShareLinkDTO = commonShareLinkDTO;
        this.activity = activity;
    }

    public void wechatBuildShare() {
        Wechat.ShareParams weChatSp = new Wechat.ShareParams();
        weChatSp.setText(commonShareLinkDTO.getDesc());
        weChatSp.setImageUrl(commonShareLinkDTO.getIconUrl());
        weChatSp.setUrl(commonShareLinkDTO.getTargetUrl());
        weChatSp.setTitle(commonShareLinkDTO.getTitle());
        weChatSp.setComment(commonShareLinkDTO.getDesc());
        weChatSp.setShareType(Platform.SHARE_WEBPAGE);
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);

        weChat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("WeiboBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("WeiboBuildShare onError" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("WeiboBuildShare onCancel");
            }
        });
        weChat.share(weChatSp);
    }

    public void WechatMomentsBuildShare() {
        WechatMoments.ShareParams weChatSp = new WechatMoments.ShareParams();
        weChatSp.setText(commonShareLinkDTO.getDesc());
        weChatSp.setImageUrl(commonShareLinkDTO.getIconUrl());
        weChatSp.setUrl(commonShareLinkDTO.getTargetUrl());
        weChatSp.setTitle(commonShareLinkDTO.getTitle());
        weChatSp.setComment(commonShareLinkDTO.getDesc());
        weChatSp.setShareType(Platform.SHARE_WEBPAGE);
        Platform weChat = ShareSDK.getPlatform(WechatMoments.NAME);

        weChat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("WeiboBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("WeiboBuildShare onError" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("WeiboBuildShare onCancel");
            }
        });
        weChat.share(weChatSp);
    }

    public void QQBuildShare() {

        QQ.ShareParams qqSP = new QQ.ShareParams();
        qqSP.setText(commonShareLinkDTO.getDesc());
        qqSP.setImageUrl(commonShareLinkDTO.getIconUrl());
        qqSP.setUrl(commonShareLinkDTO.getTargetUrl());
        qqSP.setTitle(commonShareLinkDTO.getTitle());
        qqSP.setComment(commonShareLinkDTO.getDesc());
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("WeiboBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("WeiboBuildShare onError" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("WeiboBuildShare onCancel");
            }
        });
        qq.share(qqSP);
    }

    public void WeiboBuildShare() {
        SinaWeibo.ShareParams weiboSP = new SinaWeibo.ShareParams();
        weiboSP.setText(commonShareLinkDTO.getDesc());
        weiboSP.setImageUrl(commonShareLinkDTO.getIconUrl());
        weiboSP.setUrl(commonShareLinkDTO.getTargetUrl());
        weiboSP.setTitle(commonShareLinkDTO.getTitle());
        weiboSP.setComment(commonShareLinkDTO.getDesc());
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                logger.info("WeiboBuildShare onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                logger.error("WeiboBuildShare onError" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                logger.error("WeiboBuildShare onCancel");
            }
        });
        weibo.share(weiboSP);
    }

    public void facebookBuildShare() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setImageUrl(Uri.parse(commonShareLinkDTO.getIconUrl()))
                .setContentTitle(commonShareLinkDTO.getTitle())
                .setContentUrl(Uri.parse(commonShareLinkDTO.getTargetUrl()))
                .build();
        ShareDialog.show(activity, content);
    }

    public void twitterBuildShare() {
        try {
            URL url = new URL(commonShareLinkDTO.getTargetUrl());
            TweetComposer.Builder builder = new TweetComposer.Builder(activity)
                    .text(commonShareLinkDTO.getDesc())
                    .url(url);
            builder.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
