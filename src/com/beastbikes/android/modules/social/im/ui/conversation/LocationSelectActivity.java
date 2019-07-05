package com.beastbikes.android.modules.social.im.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnBean;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnCallBack;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.dto.PoiInfoDTO;
import com.beastbikes.android.modules.cycling.route.ui.RouteMapSearchGeoActivity;
import com.beastbikes.android.modules.cycling.route.ui.RoutePlanActivity;
import com.beastbikes.android.modules.map.MapType;
import com.beastbikes.android.modules.map.SpeedxMap;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.Toasts;

@LayoutResource(R.layout.localtion_select_activity)
public class LocationSelectActivity extends SessionFragmentActivity implements
        OnClickListener,
        SpeedxMap.OnMapStatusChangeListener, GoogleMapCnCallBack,
        RequestQueueManager {

    @IdResource(R.id.localtion_selsect_activity_mapview)
    private SpeedxMap speedxMap;

    @IdResource(R.id.localtion_selsect_activity_button_location)
    private ImageView btnLocation;

    @IdResource(R.id.localtion_selsect_activity_button_zoom_out)
    private ImageView btnZoonOut;

    @IdResource(R.id.localtion_selsect_activity_button_zoom_in)
    private ImageView btnZoomIn;

    @IdResource(R.id.route_map_make_select_point_view)
    private RelativeLayout selectPointView;

    @IdResource(R.id.location_route_map_make_back)
    private RelativeLayout route_map_make_back;

    @IdResource(R.id.location_route_map_make_search)
    private LinearLayout routeMapMakeSearchLL;

    @IdResource(R.id.route_map_make_select_start_point)
    private TextView mapSelectPoint;

    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";
    public static final String EXTRA_ADDR = "extra_addr";
    public static final String EXTRA_CITYNAME = "city_name";
    public static final String EXTRA_PROVINCE = "province_name";
    public static final String EXTRA_AREA = "area_name";
    public static final String EXTRA_IMG_SRC = "extra_img_src";

    private GoogleMapCnAPI googleMapCnAPI;
    private boolean first = true;
    private float zoomLevel = 16;
    private boolean mapChanged = true;

    private LatLng currentLl;
    private PoiInfoDTO currentPoiInfo;

    private AlphaAnimation alpAm;
    private TranslateAnimation animation;

    private RequestQueue requestQueue;

    @Override
    public final RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        this.requestQueue = RequestQueueFactory.newRequestQueue(this);

        googleMapCnAPI = new GoogleMapCnAPI();

        speedxMap.setUp(LocaleManager.isChineseTimeZone() ? MapType.BaiDu : MapType.Google, this);
        speedxMap.setMapFullScreen();
        speedxMap.setZoomIconVisibility(View.GONE);
        speedxMap.setMyLocationConfigeration();
        speedxMap.setOnMapStatusChangeListener(this);

        this.alpAm = new AlphaAnimation(0f, 1f);
        this.alpAm.setDuration(500);
        this.animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -0.5f);
        this.animation.setRepeatCount(1);
        this.animation.setDuration(300);
        this.animation.setRepeatMode(Animation.REVERSE);
        this.animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mapSelectPoint.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mapSelectPoint.setVisibility(View.VISIBLE);
                mapSelectPoint.startAnimation(alpAm);
            }
        });

        this.btnLocation.setOnClickListener(this);
        this.btnZoomIn.setOnClickListener(this);
        this.btnZoonOut.setOnClickListener(this);
        this.mapSelectPoint.setOnClickListener(this);
        this.route_map_make_back.setOnClickListener(this);
        this.routeMapMakeSearchLL.setOnClickListener(this);

        requestLocation();
    }

    @Override
    protected void onResume() {
        speedxMap.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        speedxMap.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        speedxMap.onDestroy();
        super.onDestroy();
    }

    private void requestLocation() {
        this.first = true;
        speedxMap.requestLocation();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.localtion_selsect_activity_button_location: {// 定位
                requestLocation();
                break;
            }
            case R.id.localtion_selsect_activity_button_zoom_in: {// 放大
                final float min = this.speedxMap.getZoomLevel();
                final float level = Math.max(this.zoomLevel - 1, min);
                speedxMap.zoomTo(level);
                this.zoomLevel = level;
                break;
            }
            case R.id.localtion_selsect_activity_button_zoom_out: {// 缩小
                final float min = this.speedxMap.getZoomLevel();
                final float level = Math.max(this.zoomLevel + 1, min);

                speedxMap.zoomTo(level);
                this.zoomLevel = level;
                break;
            }
            case R.id.route_map_make_select_start_point:
                if (null != currentPoiInfo) {
                    Intent it = getIntent();

                    it.putExtra(EXTRA_LAT, currentPoiInfo.getLatitude());
                    it.putExtra(EXTRA_LNG, currentPoiInfo.getLongitude());
                    it.putExtra(EXTRA_ADDR, currentPoiInfo.getAddress());

                    it.putExtra(EXTRA_CITYNAME, currentPoiInfo.getCity());
                    it.putExtra(EXTRA_PROVINCE, currentPoiInfo.getProvince());
                    it.putExtra(EXTRA_AREA,currentPoiInfo.getArea());
                    setResult(RESULT_OK, it);
                    finish();
                } else {
                    Toasts.show(this, R.string.route_map_make_activity_select_err);
                }
                break;
            case R.id.location_route_map_make_back:
                finish();
                break;
            case R.id.location_route_map_make_search: // 搜索
                RouteMapSearchGeoActivity.isLandscape = false;
                Intent intent = new Intent(this, RouteMapSearchGeoActivity.class);
                startActivityForResult(intent,
                        RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapStatusChangeFinish(double latitude, double longitude) {

        if(latitude <= 0 && longitude <= 0 ||  latitude == 4.9E-324
                || longitude == 4.9E-324){

            return;
        }

        this.selectPointView.startAnimation(this.animation);
        // /todo
        this.currentLl = new LatLng(latitude, longitude);
        if (currentPoiInfo == null) {
            currentPoiInfo = new PoiInfoDTO();
        }
        if (this.mapChanged) {
            currentPoiInfo.setLatitude(currentLl.latitude);
            currentPoiInfo.setLongitude(currentLl.longitude);

            googleMapCnAPI.geoCode(getRequestQueue(), currentLl.latitude, currentLl.longitude, this);
            mapChanged = false;
        }
    }

    @Override
    public void onMapStatusChangeStart(double latitude, double longitude) {
        this.mapChanged = true;
        this.mapSelectPoint.setVisibility(View.GONE);
        this.selectPointView.clearAnimation();

    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE:
                    if (data == null)
                        return;
                    Object poiInfoObj = data.getSerializableExtra(RoutePlanActivity.EXTRA_POIINFO);
                    if (poiInfoObj != null) {
                        this.currentPoiInfo = (PoiInfoDTO) poiInfoObj;
                        this.speedxMap.zoomTo(this.currentPoiInfo.getLatitude(), this.currentPoiInfo.getLongitude());
                    }
                    break;
            }
        }
    }

    @Override
    public void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean) {
        if (currentPoiInfo == null || googleMapCnBean == null)
            return;
        currentPoiInfo.setCity(googleMapCnBean.getCityName());
        currentPoiInfo.setProvince(googleMapCnBean.getProvince());

        if(!TextUtils.isEmpty(googleMapCnBean.getAddress()) && googleMapCnBean.getAddress().contains(",")) {
            String addrArray[] = googleMapCnBean.getAddress().split(",");
            if (addrArray != null && addrArray.length > 1) {
                String area = addrArray[1];
                currentPoiInfo.setArea(area);
            }
        }

        if (!TextUtils.isEmpty(googleMapCnBean.getAddress())) {
            if (googleMapCnBean.getAddress().contains("Unnamed Road, ")) {
                String address = googleMapCnBean.getAddress();
                address = address.substring(0, 14);
                currentPoiInfo.setAddress(address);
            } else {
                currentPoiInfo.setAddress(googleMapCnBean.getAddress());
            }
        }

        if (!TextUtils.isEmpty(googleMapCnBean.getFormattedAddress())) {
            if (googleMapCnBean.getFormattedAddress().contains("Unnamed Road, ")) {
                String address = googleMapCnBean.getFormattedAddress();
                address = address.substring(14, address.length());
                currentPoiInfo.setAddress(address);
            } else {
                currentPoiInfo.setAddress(googleMapCnBean.getFormattedAddress());
            }
        }
    }

    @Override
    public void onGetGeoInfoError(VolleyError volleyError) {

    }


}