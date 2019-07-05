package com.beastbikes.android.modules.user.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.activity.ui.record.CyclingCompletedActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.sharepopupwindow.CommonShareHandle;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareImageDTO;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

import java.io.File;

@Alias("水印相机完成页")
@LayoutResource(R.layout.activity_watermark_finished)
public class WatermarkFinishedActivity extends BaseFragmentActivity implements
        OnClickListener {

    public static final String EXTRA_PICTURE_PATH = "path";

    private final DisplayMetrics displayMetrics = new DisplayMetrics();

    @IdResource(R.id.activity_watermark_finished_image)
    private ImageView image;

    @IdResource(R.id.activity_watermark_finished_next)
    private TextView ok;

    @IdResource(R.id.activity_watermark_finished_back)
    private ImageView back;

    private ViewGroup shareWechatMoments;
    private ImageView shareWechatMomentsIV;
    private TextView shareWechatMomentsTV;

    private ViewGroup shareWechat;
    private ImageView shareWechatIV;
    private TextView shareWechatTV;

    private ViewGroup shareQQ;
    private ImageView shareQQIV;
    private TextView shareQQTV;

    private ViewGroup shareWeibo;
    private ImageView shareWeiboIV;
    private TextView shareWeiboTV;

    private ViewGroup shareFacebook;
    private ImageView shareFacebookIV;
    private TextView shareFacebookTV;

    private ViewGroup shareTwitter;
    private ImageView shareTwitterIV;
    private TextView shareTwitterTV;

    private ViewGroup shareSDCard;
    private ImageView shareSDCardIV;
    private TextView shareSDCardTV;

    private LayoutInflater layoutInflater;

    @IdResource(R.id.common_share_layout_content)
    private LinearLayout linearLayout;

    private String sharePath;

    private CommonShareHandle commonShareHandle;

    private CommonShareImageDTO commonShareImageDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        this.initShare();

        final int w = displayMetrics.widthPixels - DimensionUtils.dip2px(this, 26);

        final Intent intent = getIntent();
        if (intent != null) {
            final String path = intent.getStringExtra(EXTRA_PICTURE_PATH);
            if (!TextUtils.isEmpty(path)) {
                image.setImageURI(Uri.fromFile(new File(path)));
                this.sharePath = path;
                this.image.setLayoutParams(new LayoutParams(w, w));
            }
            // image.setImageBitmap((Bitmap)intent.getParcelableExtra(EXTRA_PICTURE_PATH));
        }
        commonShareImageDTO = new CommonShareImageDTO();
        commonShareImageDTO.setImagePath(sharePath);
        commonShareHandle = new CommonShareHandle(this, commonShareImageDTO);
    }

    private void initShare() {
        this.ok.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.image.setOnClickListener(this);
        layoutInflater = LayoutInflater.from(this);
        shareWechatMoments = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareWechatMoments.setId(R.id.share_item_wechat_moments);
        shareWechatMomentsIV = (ImageView) shareWechatMoments.findViewById(R.id.share_menu_icon);
        shareWechatMomentsTV = (TextView) shareWechatMoments.findViewById(R.id.share_menu_name);

        shareWechat = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareWechat.setId(R.id.share_item_wechat);
        shareWechatIV = (ImageView) shareWechat.findViewById(R.id.share_menu_icon);
        shareWechatTV = (TextView) shareWechat.findViewById(R.id.share_menu_name);

        shareQQ = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareQQ.setId(R.id.share_item_qq);
        shareQQIV = (ImageView) shareQQ.findViewById(R.id.share_menu_icon);
        shareQQTV = (TextView) shareQQ.findViewById(R.id.share_menu_name);

        shareWeibo = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareWeibo.setId(R.id.share_item_weibo);
        shareWeiboIV = (ImageView) shareWeibo.findViewById(R.id.share_menu_icon);
        shareWeiboTV = (TextView) shareWeibo.findViewById(R.id.share_menu_name);

        shareFacebook = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareFacebook.setId(R.id.share_item_facebook);
        shareFacebookIV = (ImageView) shareFacebook.findViewById(R.id.share_menu_icon);
        shareFacebookTV = (TextView) shareFacebook.findViewById(R.id.share_menu_name);

        shareTwitter = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareTwitter.setId(R.id.share_item_twitter);
        shareTwitterIV = (ImageView) shareTwitter.findViewById(R.id.share_menu_icon);
        shareTwitterTV = (TextView) shareTwitter.findViewById(R.id.share_menu_name);

        shareSDCard = (ViewGroup) layoutInflater.inflate(R.layout.share_menu_item, null);
        shareSDCard.setId(R.id.share_item_sdcard);
        shareSDCardIV = (ImageView) shareSDCard.findViewById(R.id.share_menu_icon);
        shareSDCardTV = (TextView) shareSDCard.findViewById(R.id.share_menu_name);

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
        linearLayout.addView(shareSDCard);
        initShareItem();
        shareTwitter.setOnClickListener(this);
        shareWechatMoments.setOnClickListener(this);
        shareWechat.setOnClickListener(this);
        shareQQ.setOnClickListener(this);
        shareWeibo.setOnClickListener(this);
        shareFacebook.setOnClickListener(this);
        shareSDCard.setOnClickListener(this);
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

        shareSDCardTV.setText(R.string.activity_finished_menu_save);
        shareSDCardIV.setImageResource(R.drawable.share_icon_download);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_watermark_finished_back:
                this.finish();
                break;
            case R.id.activity_watermark_finished_image:
                break;
            case R.id.activity_watermark_finished_next:
                final Intent intent = getIntent();
                intent.putExtra(CyclingCompletedActivity.EXTRA_FILE_PATH, sharePath);
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
            case R.id.share_item_facebook:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.shareFacebook(commonShareImageDTO);
                break;
            case R.id.share_item_twitter:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.shareTwitter(commonShareImageDTO);
                break;
            case R.id.share_item_wechat:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.shareWechat(commonShareImageDTO);
                break;
            case R.id.share_item_wechat_moments:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.shareWechatMoments(commonShareImageDTO);
                break;
            case R.id.share_item_qq:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.shareQQ(commonShareImageDTO);
                break;
            case R.id.share_item_weibo:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.shareWeibo(commonShareImageDTO);
                break;
            case R.id.share_item_sdcard:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_share");
                commonShareHandle.saveToSdCard();
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }
}
