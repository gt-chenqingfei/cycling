package com.beastbikes.android.modules.ad.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beastbikes.android.R;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.ad.biz.AdManager;
import com.beastbikes.android.modules.ad.dto.AdDto;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by chenqingfei on 16/1/18.
 */
public class AdBannerView extends LinearLayout {

    //    private static final float RESOLUTION = 640f / 340f;
    private static final float RESOLUTION = 275f / 70f;

    public static final String IS_FROM_BANNER = "is_from_banner";
    public static final String IS_FROM_BANNER_OPEN = "1";

    private double windowWidth;
    private double bannerHeight;
    private ImageView discoveryBanner;

    SessionFragmentActivity activity;

    public AdBannerView(Context context, double windowWidth) {
        super(context);
        this.windowWidth = windowWidth;
        init();
    }

    public AdBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (getContext() instanceof BaseFragmentActivity) {
            this.activity = (SessionFragmentActivity) getContext();
        }
        AdBannerView.this.setVisibility(View.GONE);
        getAdInfo();
    }


    private void getAdInfo() {

        activity.getAsyncTaskQueue().add(new AsyncTask<Void, Void, AdDto>() {

            @Override
            protected AdDto doInBackground(Void... voids) {
                try {
                    return new AdManager(activity).adCycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AdDto ad) {
                super.onPostExecute(ad);
                if (ad != null) {
                    displayAdView(ad);
                }
            }
        });
    }

    private void displayAdView(final AdDto dto) {
        discoveryBanner = new ImageView(getContext());

        if (!TextUtils.isEmpty(dto.getImageUrl())) {
            this.setVisibility(View.VISIBLE);
            int padding = DensityUtil.dip2px(getContext(), 20);
            int padding1 = DensityUtil.dip2px(getContext(), 10);

            AdBannerView.this.setPadding(padding1, padding, padding1, padding);

            DisplayMetrics dm = new DisplayMetrics();
            WindowManager vm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            vm.getDefaultDisplay().getMetrics(dm);
            bannerHeight = (int) (windowWidth / RESOLUTION);

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    (int) windowWidth, (int) bannerHeight);

            discoveryBanner.setLayoutParams(lp);
            discoveryBanner.setScaleType(ImageView.ScaleType.FIT_XY);

            AdBannerView.this.addView(discoveryBanner);

            Picasso.with(getContext()).load(dto.getImageUrl()).fit().error(R.drawable.transparent)
                    .placeholder(R.drawable.transparent).into(discoveryBanner);

        } else {
            this.setVisibility(GONE);
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(dto.getLinkTo()) && !TextUtils.isEmpty(dto.getImageUrl())) {
                    SpeedxAnalytics.onEvent(activity, "", "click_more_banner");
                    final Uri uri = Uri.parse(dto.getLinkTo());
                    final Intent intent = new Intent(activity, BrowserActivity.class);
                    intent.setData(uri);
                    intent.setPackage(activity.getPackageName());

                    intent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                            R.anim.activity_in_from_right);
                    intent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                            R.anim.activity_out_to_right);
                    intent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                            R.anim.activity_none);
                    activity.startActivity(intent);
                }
            }
        });


    }


}
