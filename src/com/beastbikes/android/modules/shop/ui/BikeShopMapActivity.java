package com.beastbikes.android.modules.shop.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.dto.PoiInfoDTO;
import com.beastbikes.android.modules.cycling.route.ui.RouteMapSearchGeoActivity;
import com.beastbikes.android.modules.cycling.route.ui.RoutePlanActivity;
import com.beastbikes.android.modules.shop.biz.BikeShopManager;
import com.beastbikes.android.modules.shop.dto.BikeShopListDTO;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/5.
 */
@LayoutResource(R.layout.activity_bike_shop)
public class BikeShopMapActivity extends SessionFragmentActivity implements View.OnClickListener,
        Constants, BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMapLoadedCallback,
        BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener, BDLocationListener {

    @IdResource(R.id.shop_baidu_map)
    private MapView mMapView;

    @IdResource(R.id.activity_bike_shop_back)
    private RelativeLayout bikeShopBack;

    @IdResource(R.id.activity_bike_shop_search)
    private RelativeLayout bikeShopSearch;

    @IdResource(R.id.shop_baidu_map_search_result_rl)
    private RelativeLayout shopSearchResultRL;

    @IdResource(R.id.shop_baidu_map_search_result_tv)
    private TextView shopSearchResultTV;

    @IdResource(R.id.shop_baidu_map_search_result_info)
    private RelativeLayout shopSearchInfoLL;

    @IdResource(R.id.shop_baidu_map_authentication)
    private ImageView mShopAuthentication;

    @IdResource(R.id.shop_baidu_map_search_result_info_name)
    private TextView shopSearchInfoName;

    @IdResource(R.id.shop_baidu_map_search_result_info_distance)
    private TextView shopSearchInfoDistance;

    @IdResource(R.id.shop_baidu_map_search_result_address)
    private TextView shopSearchInfoAddress;

    @IdResource(R.id.bike_shop_tag_activity)
    private TextView tagActivity;

    @IdResource(R.id.bike_shop_tag_after_sell)
    private TextView tagAfterSell;

    @IdResource(R.id.bike_shop_tag_sell)
    private TextView tagSell;

    @IdResource(R.id.bike_shop_tag_care)
    private TextView tagCare;

    @IdResource(R.id.bike_shop_tag_fix)
    private TextView tagFix;

    @IdResource(R.id.bike_shop_tag_rent)
    private TextView tagRent;

    private final String SHOP_INFO = "speedx_shop_info";
    private BaiduMap baiduMap;
    private LocationClient client;

    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private static final int DEFAULT_ZOOM_LEVEL = 16;

    private double lat;
    private double lon;
    private double uLat;
    private double uLon;

    private BikeShopManager bikeShopManager;

    private int windowWidth;
    private int windowHeight;

    private CoordinateConverter converter;

    private int zoomlevelArray[] = {1, 20, 40, 60, 80, 100};
    private int currentZoomLevel = zoomlevelArray[0];

    private float zoomCurrent = DEFAULT_ZOOM_LEVEL;

    private List<BikeShopListDTO> bikeShopListDTOsList = new ArrayList<>();

    private Marker lastMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bikeShopManager = new BikeShopManager(this);
        baiduMapInit();
        SharedPreferences sharedPreferences = getSharedPreferences(UtilsLocationManager.getInstance().getClass().getName(), 0);
        this.lat = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LAT, "0"));
        this.lon = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LON, "0"));
        this.uLat = lat;
        this.uLon = lon;
        bikeShopBack.setOnClickListener(this);
        bikeShopSearch.setOnClickListener(this);
        shopSearchInfoLL.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels;
        this.windowHeight = dm.heightPixels;

        if (this.lat != 0 && this.lon != 0)
            this.baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (client != null) {
            client.stop();
            client.unRegisterLocationListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE:
                    PoiInfoDTO currentPoiInfo = (PoiInfoDTO) data
                            .getSerializableExtra(RoutePlanActivity.EXTRA_POIINFO);
                    lon = currentPoiInfo.getLongitude();
                    lat = currentPoiInfo.getLatitude();

                    CoordinateConverter converter = new CoordinateConverter();
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(new LatLng(lat, lon));
                    LatLng desLatLng = converter.convert();
                    baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(desLatLng));
                    getShopList();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_bike_shop_back:
                finish();
                break;
            case R.id.activity_bike_shop_search:
                RouteMapSearchGeoActivity.isLandscape = false;
                Intent intent = new Intent(this, RouteMapSearchGeoActivity.class);
                startActivityForResult(intent,
                        RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE);
                SpeedxAnalytics.onEvent(this, "搜索车店","search_bicycle");
                break;
            case R.id.shop_baidu_map_search_result_info:
                SpeedxAnalytics.onEvent(this, "查看车店详情","open_bicycle_detail");
                if(shopSearchInfoLL.getTag() != null) {
                    long shopID = Long.parseLong(shopSearchInfoLL.getTag().toString());
                    Intent intentShopDetail = new Intent(this, BikeShopDetailActivity.class);
                    intentShopDetail.putExtra(BikeShopDetailActivity.INTENT_SHOP_ID, shopID);
                    intentShopDetail.putExtra(BikeShopDetailActivity.SHOW_ENTER_CLUB, true);
                    startActivity(intentShopDetail);
                }
                break;
        }
    }

    @Override
    public void onMapLoaded() {
        getShopList();
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        zoomCurrent = mapStatus.zoom;
//        getMapDistance();
//        Log.e("m",mapStatus.zoom+" zoomCurrent");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getExtraInfo() == null)
            return false;
        BikeShopListDTO bikeShopListDTO = (BikeShopListDTO) marker.getExtraInfo().getSerializable(SHOP_INFO);
        if (bikeShopListDTO == null)
            return false;

        String shopDistanceStr;
        java.text.DecimalFormat df;
        double shopDistance = bikeShopListDTO.getRange() / 1000;
        if (shopDistance >= 10) {
            df = new java.text.DecimalFormat("#");
        } else {
            df = new java.text.DecimalFormat("#.#");
        }
        if (LocaleManager.isDisplayKM(this)) {
            shopDistanceStr = df.format(shopDistance) + getResources().getString(R.string.club_info_total_distance_unit);
        } else {
            shopDistance = LocaleManager.kilometreToMile(shopDistance);
            shopDistanceStr = df.format(shopDistance) + getResources().getString(R.string.mi);
        }
        shopSearchInfoDistance.setText(shopDistanceStr);

        marker.setIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_section_map_marker_unselect));

        mShopAuthentication.setVisibility((bikeShopListDTO.getLevel() == 1) ? View.VISIBLE : View.GONE);
        shopSearchInfoName.setText(bikeShopListDTO.getName());
        shopSearchInfoLL.setTag(bikeShopListDTO.getShopId());
        shopSearchInfoAddress.setText(getResources().getString(R.string.bike_shop_detail_address) + bikeShopListDTO.getAddress());
        shopSearchResultRL.setVisibility(View.GONE);
        shopSearchInfoLL.setVisibility(View.VISIBLE);

        if (bikeShopListDTO.getTagInfo() != null) {
            tagActivity.setVisibility(bikeShopListDTO.getTagInfo().isActivity()? View.VISIBLE :View.GONE);
            tagAfterSell.setVisibility(bikeShopListDTO.getTagInfo().isAfterSell()? View.VISIBLE :View.GONE);
            tagCare.setVisibility(bikeShopListDTO.getTagInfo().isCare()? View.VISIBLE :View.GONE);
            tagFix.setVisibility(bikeShopListDTO.getTagInfo().isFix()? View.VISIBLE :View.GONE);
            tagRent.setVisibility(bikeShopListDTO.getTagInfo().isRent()? View.VISIBLE :View.GONE);
            tagSell.setVisibility(bikeShopListDTO.getTagInfo().isSell()? View.VISIBLE :View.GONE);
        }

        if (lastMarker != null)
            lastMarker.setIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_speedx_shop_icon));
        lastMarker = marker;
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        refreshView();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    private void getMapDistance() {
        final Projection projection = this.baiduMap.getProjection();
        Point point1 = new Point(0, 100);
        Point point2 = new Point(windowWidth, 100);
        LatLng latLng = projection.fromScreenLocation(point1);
        LatLng latLng2 = projection.fromScreenLocation(point2);
        double distance = DistanceUtil.getDistance(latLng, latLng2) /1000;
        for (int i = zoomlevelArray.length - 1; i >= 0; i--) {
            if (distance / zoomlevelArray[i] > 0) {
                int index = 0;
                index = i + 1;
                if (index == zoomlevelArray.length)
                    index--;
                if (index == currentZoomLevel) {
                    refreshView();
                    return;
                }
                currentZoomLevel = index;
                getShopList();
                return;
            }
        }
    }

    private void getShopList() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<BikeShopListDTO>>() {

            @Override
            protected List<BikeShopListDTO> doInBackground(Void... voids) {
                try {
                    return bikeShopManager.getBikeShopList(lon, lat, -1, null, uLat, uLon,null);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<BikeShopListDTO> bikeShopListDTOs) {
                if (bikeShopListDTOs == null || bikeShopListDTOs.size() == 0) {
                    shopSearchResultTV.setText(getResources().getString(R.string.shop_search_no_result));
                    return;
                }
                bikeShopListDTOsList.clear();
                bikeShopListDTOsList.addAll(bikeShopListDTOs);
                refreshView();
            }
        });
    }

    private void refreshView() {
        this.baiduMap.clear();
        if (bikeShopListDTOsList == null || bikeShopListDTOsList.size() == 0)
            return;
        for (int i = 0; i < bikeShopListDTOsList.size(); i++) {
            BikeShopListDTO bikeShop = bikeShopListDTOsList.get(i);


            LatLng latLng = new LatLng(bikeShop.getLatitude(), bikeShop.getLongitude());
            converter.coord(latLng);
            latLng = converter.convert();

            addTextOverlay(bikeShop.getName(), latLng);

            BitmapDescriptor markerBitmap = BitmapDescriptorFactory.
                    fromResource(R.drawable.ic_speedx_shop_icon);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SHOP_INFO, bikeShop);
            OverlayOptions shopOption = new MarkerOptions().position(latLng).
                    icon(markerBitmap).extraInfo(bundle);
            baiduMap.addOverlay(shopOption);

        }
        shopSearchResultTV.setText(getResources().getString(R.string.shop_search_result1) + bikeShopListDTOsList.size() + getResources().getString(R.string.shop_search_result2));
//        shopSearchResultRL.setVisibility(View.VISIBLE);
//        shopSearchInfoLL.setVisibility(View.GONE);
    }

    private void addTextOverlay(String name, LatLng latLngArg) {
        if (baiduMap == null || zoomCurrent< 18)
            return;

        OverlayOptions textOption = new TextOptions()
                .fontSize(30)
                .fontColor(0xFF000000)
                .text(name)
                .position(latLngArg);
        baiduMap.addOverlay(textOption);
    }

    private void baiduMapInit() {

        this.converter = new CoordinateConverter();
        this.converter.from(CoordinateConverter.CoordType.COMMON);

        final View logo = this.mMapView.getChildAt(1);
        logo.setVisibility(View.GONE);
        baiduMap = mMapView.getMap();
        UiSettings uiSettings = baiduMap.getUiSettings();
        uiSettings.setOverlookingGesturesEnabled(false);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));
        this.baiduMap.setMyLocationEnabled(true);

        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));

//        this.baiduMap.setOnMapStatusChangeListener(this); //暂时需求上不需要 先注释
        this.baiduMap.setOnMapLoadedCallback(this);
        this.baiduMap.setOnMarkerClickListener(this);
        this.baiduMap.setOnMapClickListener(this);
        requestLocation();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null)
            return;
        BitmapDescriptor marker = BitmapDescriptorFactory
                .fromResource(R.drawable.route_map_make_location);
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, marker));
        final MyLocationData data = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        this.baiduMap.setMyLocationData(data);
        this.client.stop();

        LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
    }

    public void requestLocation() {
        if (this.client == null) {
            this.client = new LocationClient(this);
            this.client.registerLocationListener(this);
        }

        final LocationClientOption options = new LocationClientOption();
        options.setOpenGps(true);
        options.setPriority(LocationClientOption.GpsFirst);
        options.setCoorType("bd09ll");
        options.setScanSpan(LocationClientOption.MIN_SCAN_SPAN * 5);
        options.setAddrType("all");

        this.client.setLocOption(options);

        if (!this.client.isStarted()) {
            this.client.start();
        }
        this.client.requestLocation();
        this.baiduMap.setMyLocationEnabled(true);
        this.baiduMap.setOnMapStatusChangeListener(this);
    }
}
