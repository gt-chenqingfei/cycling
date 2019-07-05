package com.beastbikes.android.modules.shop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedImageDetailsActivity;
import com.beastbikes.android.modules.shop.biz.BikeShopManager;
import com.beastbikes.android.modules.shop.dto.BikeShopInfoDTO;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.sharepopupwindow.CommonSharePopupWindow;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by caoxiao on 16/4/11.
 */
@LayoutResource(R.layout.activity_bikeshop_detail)
@MenuResource(R.menu.bike_shop_info_menu)
public class BikeShopDetailActivity extends SessionFragmentActivity implements Constants, View.OnClickListener {

    public final static String INTENT_SHOP_ID = "bike_shop_id";
    public final static String SHOW_ENTER_CLUB = "show_enter_club";
    public final static String EXTRA_TYPE = "type";


    @IdResource(R.id.bike_shop_server_linear)
    private LinearLayout mBikeShopServerLinear;

    @IdResource(R.id.bike_shop_detail_container)
    private View container;

    @IdResource(R.id.activity_bikeshop_detail_logo)
    private CircleImageView detailLogo;

    @IdResource(R.id.activity_bikeshop_detail_name)
    private TextView detailName;

    @IdResource(R.id.activity_bikeshop_detail_distance_value)
    private TextView distanceValue;

    @IdResource(R.id.bike_shop_club_name)
    private TextView detailClubName;

    @IdResource(R.id.bike_shop_club_avatar)
    private CircleImageView clubAvatar;

    @IdResource(R.id.bike_shop_club_lay)
    private LinearLayout clubLay;

    @IdResource(R.id.activity_bikeshop_detail_desc_value)
    private TextView detailDescValueTV;

    @IdResource(R.id.activity_bikeshop_activity_notice_value)
    private TextView activityNotice;

    @IdResource(R.id.bike_shop_shop_hours)
    private TextView shopHours;

    @IdResource(R.id.bike_shop_brands)
    private TextView shopMainProduct;

    @IdResource(R.id.activity_bikeshop_detail_pictures)
    private LinearLayout picturesLay;

    @IdResource(R.id.activity_bikeshop_activity_notice_lay)
    private LinearLayout activityLay;

    @IdResource(R.id.activity_bikeshop_pictures_horizon_lay)
    private View picturesHorizonLay;

    @IdResource(R.id.bike_shop_club_click_lay)
    private View clubClickLay;

    @IdResource(R.id.bike_shop_address)
    private TextView shopAddress;

    @IdResource(R.id.bike_shop_telephone)
    private TextView shopTelephone;

    @IdResource(R.id.bike_shop_tag_service_safeguard)
    View tagServiceSafeguard;

    @IdResource(R.id.bike_shop_tag_after_sell)
    View tagAfterSell;

    @IdResource(R.id.bike_shop_tag_activity)
    private TextView tvTagActivity;

    @IdResource(R.id.bike_shop_tag_care)
    public TextView tvTagCare;

    @IdResource(R.id.bike_shop_tag_fix)
    public TextView tvTagFix;

    @IdResource(R.id.bike_shop_tag_rent)
    public TextView tvTagRent;


    @IdResource(R.id.bike_shop_auth_icon)
    View authIcon;

    private long shopId;

    private BikeShopManager bikeShopManager;

    private BikeShopInfoDTO bikeShopInfoDTO;

    private float lat;
    private float lon;
    private String type = null;
    private CommonSharePopupWindow window;
    private CommonShareLinkDTO commonShareLinkDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        ActionBar bar = getSupportActionBar();
        bar.setTitle(getResources().getString(R.string.shop_detail));
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent == null)
            finish();
        boolean isClubShow = intent.getBooleanExtra(SHOW_ENTER_CLUB, false);
        clubLay.setVisibility(isClubShow ? View.VISIBLE : View.GONE);
        shopId = intent.getLongExtra(INTENT_SHOP_ID, -1);
        type = intent.getStringExtra(EXTRA_TYPE);

        if (shopId == -1)
            finish();

        bikeShopManager = new BikeShopManager(this);
        SharedPreferences sharedPreferences = getSharedPreferences(UtilsLocationManager.getInstance().getClass().getName(), 0);
        lat = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LAT, "0"));
        lon = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LON, "0"));
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit:

                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setMessage(R.string.dialog_sure_bike_shop_edit);
                dialog.setPositiveButton(R.string.club_release_activites_dialog_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST)
                                .append("/app/shop/auth.html?shopId=" + shopId+"#shop");
                        final Uri browserUri = Uri.parse(sb.toString());
                        Intent browserIntent = new Intent(BikeShopDetailActivity.this, BrowserActivity.class);
                        browserIntent.setData(browserUri);
                        browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                        browserIntent.setPackage(getPackageName());
                        browserIntent.putExtra(WebActivity.EXTRA_TITLE,
                                getString(R.string.bike_shop_edit));
                        browserIntent.putExtra(WebActivity.EXTRA_CAN_GOBACK,true);
                        browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                                R.anim.activity_in_from_right);
                        browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                                R.anim.activity_out_to_right);
                        browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                                R.anim.activity_none);
                        startActivity(browserIntent);
                    }
                }).setNegativeButton(R.string.club_release_activites_dialog_cencle, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();

                break;
            case R.id.menu_share:
                showShareWindow();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!"mine".equals(type)) {
            menu.findItem(R.id.menu_edit).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void initView() {
        shopTelephone.setOnClickListener(this);
        clubClickLay.setOnClickListener(this);
        shopAddress.setOnClickListener(this);
        getBikeShopInfo();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View view) {
        if (bikeShopInfoDTO == null) {
            return;
        }
        if (view == clubClickLay) {
            if (!TextUtils.isEmpty(bikeShopInfoDTO.getClubId())) {
                IntentUtils.goClubFeedInfoActivity(this, bikeShopInfoDTO.getClubId());
            }
        } else if (view == shopAddress) {
            try {

                Uri uri = Uri.parse("geo:" + bikeShopInfoDTO.getLatitude() + "," + bikeShopInfoDTO.getLongitude() + "?q=" + bikeShopInfoDTO.getAddress());
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            } catch (Exception e) {
                Toasts.show(this,R.string.bike_shop_nav_no_map_tip);
            }
        } else if (view == shopTelephone) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bikeShopInfoDTO.getTelephone()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void getBikeShopInfo() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, BikeShopInfoDTO>() {

            @Override
            protected BikeShopInfoDTO doInBackground(Void... voids) {
//                if (lat == 0 || lon == 0)
//                    return null;
                try {
                    return bikeShopManager.getBikeShopInfo(shopId, lon, lat);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final BikeShopInfoDTO shopInfoDTO) {
                if (shopInfoDTO == null)
                    return;

                bikeShopInfoDTO = shopInfoDTO;
                bindView();
            }
        });
    }

    /**
     * 显示分享
     */
    public void showShareWindow() {
        if (bikeShopInfoDTO == null)
            return;
        if (null == this.window) {
            commonShareLinkDTO = new CommonShareLinkDTO();
            commonShareLinkDTO.setTitle("【"+bikeShopInfoDTO.getName()+"】"+getString(R.string.shop_detail));
            commonShareLinkDTO.setDesc(getString(R.string.bike_shop_share_content));
            commonShareLinkDTO.setIconUrl("http://global.speedx.com/image/shareLogo.jpg");
            commonShareLinkDTO.setTargetUrl(Constants.UrlConfig.DEV_SPEEDX_HOST +"/app/shop/shareshop.html?shopId=" +bikeShopInfoDTO.getShopId());
            commonShareLinkDTO.setWechatText(commonShareLinkDTO.getDesc());
            commonShareLinkDTO.setWeiboText(commonShareLinkDTO.getDesc());
            this.window = new CommonSharePopupWindow(this, commonShareLinkDTO, "车店");
        }
        this.window.showAtLocation(container, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    private void bindView() {
        if (bikeShopInfoDTO == null)
            return;
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getLogo())) {
            Picasso.with(BikeShopDetailActivity.this).load(bikeShopInfoDTO.getLogo()).fit().placeholder(R.drawable.ic_launcher).
                    error(R.drawable.ic_launcher).centerCrop().into(detailLogo);
        }
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getClubLogo())) {
            Picasso.with(BikeShopDetailActivity.this).load(bikeShopInfoDTO.getClubLogo()).fit().placeholder(R.drawable.ic_launcher).
                    error(R.drawable.ic_launcher).centerCrop().into(clubAvatar);
        }
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getClubName()) && !TextUtils.isEmpty(bikeShopInfoDTO.getClubId())) {
            detailClubName.setText(bikeShopInfoDTO.getClubName());
        } else {
            clubLay.setVisibility(View.GONE);
        }
        detailName.setText(bikeShopInfoDTO.getName());

        if (LocaleManager.isDisplayKM(BikeShopDetailActivity.this)) {
            double distance = bikeShopInfoDTO.getRange() / 1000;
            java.text.DecimalFormat df;
            if (distance < 10) {
                df = new java.text.DecimalFormat("#.#");
            } else {
                df = new java.text.DecimalFormat("#");
            }
            String shopDistanceStr = df.format(distance);
            distanceValue.setText(shopDistanceStr + getResources().getString(R.string.task_info_activity_joined_unit));
        } else {
            double distance = LocaleManager.kilometreToMile(bikeShopInfoDTO.getRange()) / 1000;
            java.text.DecimalFormat df;
            if (distance < 10) {
                df = new java.text.DecimalFormat("#.#");
            } else {
                df = new java.text.DecimalFormat("#");
            }
            String shopDistanceStr = df.format(distance);
            distanceValue.setText(shopDistanceStr + getResources().getString(R.string.mi));
        }

        int openTime = bikeShopInfoDTO.getOpenHour();
        int openHour = openTime / 100;
        int openMinues = openTime % 100;
        String openTimeStr = "";
        String openMinuesStr = "";
        if (openMinues < 10) {
            openMinuesStr = "0" + openMinues;
        } else {
            openMinuesStr = "" + openMinues;
        }

        int closeTime = bikeShopInfoDTO.getCloseHour();
        int closeHour = closeTime / 100;
        int closeMinues = closeTime % 100;
        String closeMinuesStr = "";
        if (closeMinues < 10) {
            closeMinuesStr = "0" + closeMinues;
        } else {
            closeMinuesStr = "" + closeMinues;
        }
        String closeTimeStr = "";
        if (openHour < 10) {
            openTimeStr = String.valueOf("0" + openHour + ":" + openMinuesStr);
        } else {
            openTimeStr = String.valueOf(openHour + ":" + openMinuesStr);
        }
        if (closeHour < 10) {
            closeTimeStr = String.valueOf("0" + closeHour + ":" + closeMinuesStr);
        } else {
            closeTimeStr = String.valueOf(closeHour + ":" + closeMinuesStr);
        }

        if (!TextUtils.isEmpty(bikeShopInfoDTO.getOfficeActivity()) &&
                !"null".equals(bikeShopInfoDTO.getOfficeActivity())) {
            activityNotice.setText(bikeShopInfoDTO.getOfficeActivity());
        } else {
            activityLay.setVisibility(View.GONE);
        }

        if (bikeShopInfoDTO.getLevel() == 1) {
            authIcon.setVisibility(View.VISIBLE);
//            tagAfterSell.setVisibility(View.VISIBLE);
//            tagServiceSafeguard.setVisibility(View.VISIBLE);
        }

        if (bikeShopInfoDTO.getTagInfo() != null) {
            mBikeShopServerLinear.setVisibility(View.VISIBLE);
            tvTagActivity.setVisibility(bikeShopInfoDTO.getTagInfo().isActivity() ? View.VISIBLE : View.GONE);
            tvTagCare.setVisibility(bikeShopInfoDTO.getTagInfo().isCare() ? View.VISIBLE : View.GONE);
            tvTagFix.setVisibility(bikeShopInfoDTO.getTagInfo().isFix() ? View.VISIBLE : View.GONE);
            tvTagRent.setVisibility(bikeShopInfoDTO.getTagInfo().isRent() ? View.VISIBLE : View.GONE);
        } else {
            mBikeShopServerLinear.setVisibility(View.GONE);
        }

        shopHours.setText(getResources().getString(R.string.opening_hours) + " " + openTimeStr + "-" + closeTimeStr);
        String address ="";
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getCitygetProvince()) && !"null".equals(bikeShopInfoDTO.getCitygetProvince())) {
            address = bikeShopInfoDTO.getCitygetProvince();
        }
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getCity()) && !"null".equals(bikeShopInfoDTO.getCity())) {
            address += bikeShopInfoDTO.getCity();
        }
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getDistrict()) && !"null".equals(bikeShopInfoDTO.getDistrict())) {
            address += bikeShopInfoDTO.getDistrict();
        }

        address += bikeShopInfoDTO.getAddress();
        shopAddress.setText(address );


        if (!TextUtils.isEmpty(bikeShopInfoDTO.getTelephone()) && !"null".equals(bikeShopInfoDTO.getTelephone())) {
            shopTelephone.setText(bikeShopInfoDTO.getTelephone());
        }
        if (!TextUtils.isEmpty(bikeShopInfoDTO.getDescription()) && !"null".equals(bikeShopInfoDTO.getDescription())) {
            detailDescValueTV.setText(bikeShopInfoDTO.getDescription());
        }

        if (TextUtils.isEmpty(bikeShopInfoDTO.getClubId())) {
            clubLay.setVisibility(View.GONE);
        }


        if (TextUtils.equals("null", bikeShopInfoDTO.getMainProducts())) {
            bikeShopInfoDTO.setMainProducts("");
        }
        shopMainProduct.setText(bikeShopInfoDTO.getMainProducts());
        final ArrayList<String> resultList = new ArrayList<>();

        if (bikeShopInfoDTO.getPictures() != null && bikeShopInfoDTO.getPictures().size() > 0) {
            for (int i = 0; i < bikeShopInfoDTO.getPictures().size(); i++) {
                final String url = bikeShopInfoDTO.getPictures().get(i);
                if (TextUtils.isEmpty(url))
                    continue;
                resultList.add(url);
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(this, 60), DensityUtil.dip2px(this, 60));
                layoutParams.setMargins(0, 0, DensityUtil.dip2px(this, 10), 0);
                imageView.setLayoutParams(layoutParams);
                imageView.setId(i);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent it = new Intent(BikeShopDetailActivity.this, ClubFeedImageDetailsActivity.class);
                        it.putStringArrayListExtra(ClubFeedImageDetailsActivity.EXTRA_IMAGES, resultList);
                        it.putExtra(ClubFeedImageDetailsActivity.EXTRA_POS, v.getId());
                        it.putExtra(ClubFeedImageDetailsActivity.EXTRA_CANDEL,false);
                        it.putExtra(ClubFeedImageDetailsActivity.EXTRA_COMPRESS ,false);
                        startActivity(it);
                    }
                });
                picturesLay.addView(imageView);
                Picasso.with(BikeShopDetailActivity.this).load(url).into(imageView);
            }
        } else {
            picturesHorizonLay.setVisibility(View.GONE);
        }
    }
}
