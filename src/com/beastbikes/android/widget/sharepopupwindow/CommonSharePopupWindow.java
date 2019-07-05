package com.beastbikes.android.widget.sharepopupwindow;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareBaseDTO;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareImageDTO;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

/**
 * Created by caoxiao on 16/5/17.
 */
public class CommonSharePopupWindow extends PopupWindow implements View.OnClickListener {
    private Activity activity;
    private View view;

    private ImageView shareWechatMomentsIV;
    private TextView shareWechatMomentsTV;

    private ImageView shareWechatIV;
    private TextView shareWechatTV;

    private ImageView shareQQIV;
    private TextView shareQQTV;

    private ImageView shareWeiboIV;
    private TextView shareWeiboTV;

    private ImageView shareFacebookIV;
    private TextView shareFacebookTV;

    private ImageView shareTwitterIV;
    private TextView shareTwitterTV;

    private CommonShareBaseDTO commonShareBaseDTO;

    private CommonShareHandle commonShareHandle;

    private boolean saveToSDCard;

    private String analytics;

    private TextView shareTitle;

    public CommonSharePopupWindow(BaseFragmentActivity activity, CommonShareImageDTO commonShareImage, String analytics) {
        super(activity);
        this.activity = activity;
        this.commonShareBaseDTO = commonShareImage;
        this.analytics = analytics;
        commonShareHandle = new CommonShareHandle(activity, commonShareImage);
        saveToSDCard = true;
        initView();
    }

    public CommonSharePopupWindow(BaseFragmentActivity activity, CommonShareLinkDTO commonShareLinkDTO, String analytics) {
        super(activity);
        this.activity = activity;
        this.commonShareBaseDTO = commonShareLinkDTO;
        this.analytics = analytics;
        commonShareHandle = new CommonShareHandle(activity, commonShareLinkDTO);
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        view = layoutInflater.inflate(R.layout.popopwindow_common_share, null);
        shareTitle = (TextView) view.findViewById(R.id.share_menu_title);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.common_share_popupwindow_content);
        ViewGroup shareWechatMoments = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareWechatMoments.setId(R.id.share_item_wechat_moments);
        shareWechatMomentsIV = (ImageView) shareWechatMoments.findViewById(R.id.share_menu_icon);
        shareWechatMomentsTV = (TextView) shareWechatMoments.findViewById(R.id.share_menu_name);

        ViewGroup shareWechat = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareWechat.setId(R.id.share_item_wechat);
        shareWechatIV = (ImageView) shareWechat.findViewById(R.id.share_menu_icon);
        shareWechatTV = (TextView) shareWechat.findViewById(R.id.share_menu_name);

        ViewGroup shareQQ = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareQQ.setId(R.id.share_item_qq);
        shareQQIV = (ImageView) shareQQ.findViewById(R.id.share_menu_icon);
        shareQQTV = (TextView) shareQQ.findViewById(R.id.share_menu_name);

        ViewGroup shareWeibo = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareWeibo.setId(R.id.share_item_weibo);
        shareWeiboIV = (ImageView) shareWeibo.findViewById(R.id.share_menu_icon);
        shareWeiboTV = (TextView) shareWeibo.findViewById(R.id.share_menu_name);

        ViewGroup shareFacebook = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareFacebook.setId(R.id.share_item_facebook);
        shareFacebookIV = (ImageView) shareFacebook.findViewById(R.id.share_menu_icon);
        shareFacebookTV = (TextView) shareFacebook.findViewById(R.id.share_menu_name);

        ViewGroup shareTwitter = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareTwitter.setId(R.id.share_item_twitter);
        shareTwitterIV = (ImageView) shareTwitter.findViewById(R.id.share_menu_icon);
        shareTwitterTV = (TextView) shareTwitter.findViewById(R.id.share_menu_name);

        initShareItem();
        if (LocaleManager.isChineseTimeZone()) {
            linearLayout.addView(shareWechatMoments);
            linearLayout.addView(shareWechat);
            linearLayout.addView(shareQQ);
            linearLayout.addView(shareWeibo);
            linearLayout.addView(shareFacebook);
            linearLayout.addView(shareTwitter);
        } else {
            linearLayout.addView(shareFacebook);
            linearLayout.addView(shareTwitter);
            linearLayout.addView(shareWechatMoments);
            linearLayout.addView(shareWechat);
            linearLayout.addView(shareQQ);
            linearLayout.addView(shareWeibo);
        }
        if (saveToSDCard) {
            ViewGroup shareSDCard = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
            shareSDCard.setId(R.id.share_item_sdcard);
            ImageView shareSDCardIV = (ImageView) shareSDCard.findViewById(R.id.share_menu_icon);
            shareSDCardIV.setImageResource(R.drawable.share_icon_download);
            TextView shareSDCardTV = (TextView) shareSDCard.findViewById(R.id.share_menu_name);
            shareSDCardTV.setText(R.string.activity_finished_menu_save);
            shareSDCard.setOnClickListener(this);
            linearLayout.addView(shareSDCard);
        }

        shareTwitter.setOnClickListener(this);
        shareWechatMoments.setOnClickListener(this);
        shareWechat.setOnClickListener(this);
        shareQQ.setOnClickListener(this);
        shareWeibo.setOnClickListener(this);
        shareFacebook.setOnClickListener(this);
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //设置弹出窗体需要软键盘，
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //再设置模式，和Activity的一样，覆盖，调整大小。
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.share_menu_root)
                        .getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void initShareItem() {
        shareWechatMomentsIV.setImageResource(R.drawable.share_icon_moments);
        shareWechatMomentsTV.setText(R.string.activity_finished_menu_wechat_friend);

        shareWechatIV.setImageResource(R.drawable.share_icon_wechat);
        shareWechatTV.setText(R.string.activity_finished_menu_wechat);

        shareQQIV.setImageResource(R.drawable.share_icon_qq);
        shareQQTV.setText(R.string.activity_finished_menu_qq);

        shareWeiboIV.setImageResource(R.drawable.share_icon_weibo);
        shareWeiboTV.setText(R.string.activity_finished_menu_weibo);

        shareFacebookIV.setImageResource(R.drawable.activity_account_management_facebook_icon_band);
        shareFacebookTV.setText(R.string.activity_account_management_facebook_str);

        shareTwitterIV.setImageResource(R.drawable.activity_account_management_twitter_icon_band);
        shareTwitterTV.setText(R.string.activity_account_management_twitter_str);
    }

    public void setShareTitle(String title) {
        shareTitle.setText(title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_item_facebook:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—分享到Facebook",null);
                }
                commonShareHandle.shareFacebook(commonShareBaseDTO);
                break;
            case R.id.share_item_twitter:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—分享到Twitter",null);
                }
                commonShareHandle.shareTwitter(commonShareBaseDTO);
                break;
            case R.id.share_item_wechat:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—分享到微信朋友",null);
                }
                commonShareHandle.shareWechat(commonShareBaseDTO);
                break;
            case R.id.share_item_wechat_moments:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—分享到微信朋友圈",null);
                }
                commonShareHandle.shareWechatMoments(commonShareBaseDTO);
                break;
            case R.id.share_item_qq:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—分享到QQ好友",null);
                }
                commonShareHandle.shareQQ(commonShareBaseDTO);
                break;
            case R.id.share_item_weibo:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—分享到新浪微博",null);
                }
                commonShareHandle.shareWeibo(commonShareBaseDTO);
                break;
            case R.id.share_item_sdcard:
                if (!TextUtils.isEmpty(analytics)) {
                    SpeedxAnalytics.onEvent(activity, analytics + "—图片保存到本地",null);
                }
                commonShareHandle.saveToSdCard();
                break;
        }
        dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
//        this.setBackgroundAlpha(0.5f);
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        this.setBackgroundAlpha(1f);
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

}
