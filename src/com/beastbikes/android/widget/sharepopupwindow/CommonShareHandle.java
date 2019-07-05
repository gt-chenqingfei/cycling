package com.beastbikes.android.widget.sharepopupwindow;

import android.app.Activity;

import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareBaseDTO;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareImageDTO;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

/**
 * Created by caoxiao on 16/5/18.
 */
public class CommonShareHandle {

    private CommonShareImage commonShareImage;
    private CommonShareLink commonShareLink;
    private CommonShareBaseDTO commonShareBaseDTO;

    public CommonShareHandle(BaseFragmentActivity activity, CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage = new CommonShareImage(activity, (CommonShareImageDTO) commonShareBaseDTO);
        } else if (commonShareBaseDTO instanceof CommonShareLinkDTO) {
            commonShareLink = new CommonShareLink(activity, (CommonShareLinkDTO) commonShareBaseDTO);
        }
    }

    public void shareFacebook(CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage.facebookBuildShare();
        } else {
            commonShareLink.facebookBuildShare();
        }
    }

    public void shareTwitter(CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage.twitterBuildShare();
        } else {
            commonShareLink.twitterBuildShare();
        }
    }

    public void shareWechat(CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage.wechatBuildShare();
        } else {
            commonShareLink.wechatBuildShare();
        }
    }

    public void shareWechatMoments(CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage.WechatMomentsBuildShare();
        } else {
            commonShareLink.WechatMomentsBuildShare();
        }
    }

    public void shareQQ(CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage.QQBuildShare();
        } else {
            commonShareLink.QQBuildShare();
        }
    }

    public void shareWeibo(CommonShareBaseDTO commonShareBaseDTO) {
        if (commonShareBaseDTO instanceof CommonShareImageDTO) {
            commonShareImage.WeiboBuildShare();
        } else {
            commonShareLink.WeiboBuildShare();
        }
    }

    public void saveToSdCard() {
        commonShareImage.saveToSdCard();
    }
}
