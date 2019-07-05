package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapManager;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.sections.biz.SectionManager;
import com.beastbikes.android.modules.cycling.sections.dto.SectionDetailListDTO;
import com.beastbikes.android.modules.cycling.sections.dto.SegmentRankDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.polyline.Point;
import com.beastbikes.android.utils.polyline.PolylineDecoder;
import com.beastbikes.android.widget.PullRefreshListView4ScrollView;
import com.beastbikes.android.widget.ScrollView4CheckBottom;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/8.
 */
@LayoutResource(R.layout.activity_section_detail)
public class SectionDetailActivity extends SessionFragmentActivity implements OnMapReadyCallback,
        ScrollView4CheckBottom.ScrollViewLoadMoreListener, View.OnClickListener,
        Constants, BaiduMap.OnMapLoadedCallback, AdapterView.OnItemClickListener {

    @IdResource(R.id.section_detail_baidu_map)
    private MapView mMapView;

    @IdResource(R.id.section_detail_scrollview)
    private ScrollView4CheckBottom detailScrollView;

    @IdResource(R.id.transparent_image)
    private ImageView transparent_view;

    @IdResource(R.id.section_detail_map_rl)
    private RelativeLayout detailMapRL;

    @IdResource(R.id.section_detail_no_google_play_service)
    private RelativeLayout noGooglePlayService;

    private BaiduMap baiduMap;
    private LocationClient client;

    private int windowWidth;

    @IdResource(R.id.section_detail_list)
    private PullRefreshListView4ScrollView pullRefreshListView4ScrollView;

    private com.google.android.gms.maps.MapFragment mMapFragment;
    private GoogleMap googleMap;

    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private static final int DEFAULT_ZOOM_LEVEL = 16;

    private SectionDetailAdapter sectionDetailAdapter;
    private List<SegmentRankDTO> rankLists = new ArrayList<>();

    @IdResource(R.id.section_detail_title)
    private TextView detailTitle;

    @IdResource(R.id.section_detail_slopes)
    private TextView detailSlopes;

    @IdResource(R.id.section_detail_altitude)
    private TextView detailAltitude;

    @IdResource(R.id.section_detail_distance)
    private TextView detailDistance;

    @IdResource(R.id.section_detail_ratingbar)
    private RatingBar detailRatingbar;

    @IdResource(R.id.section_detail_member_count)
    private TextView memberCount;

    @IdResource(R.id.section_detail_favourite_iv)
    private ImageView favouriteIV;

    @IdResource(R.id.section_detail_favourite_count_tv)
    private TextView favouriteCountTV;

    private SectionManager sectionManager;

    private LoadingDialog loadingDialog;

    private List<LatLng> baiduPoints = new ArrayList<>();

    public static final String SECTION_ID = "speedx_section_id";

    private SectionDetailListDTO googleSectionDetailListDTO;

    private long sectionID;
    private float lat;
    private float lon;

    private boolean isShowKilometer = true;

    private int page = 1;
    private int count = 10;
    private boolean isLoadingMore = false;

    private LatLngBounds.Builder builder;
    private int mapWidth;
    private int mapHeight;

    private boolean isGoogleMapReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent == null)
            return;
        sectionID = intent.getLongExtra(SECTION_ID, -1);
        if (sectionID == -1)
            return;

        isShowKilometer = LocaleManager.isDisplayKM(this);
        if (LocaleManager.isChineseTimeZone()) {
            baiduMapInit();
        } else {
            googleMapInit();
        }
        sectionManager = new SectionManager(this);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels;
        ViewGroup.LayoutParams mapViewLP = detailMapRL.getLayoutParams();
        mapViewLP.width = windowWidth;
        mapViewLP.height = (int) (windowWidth * 0.5556);
        detailMapRL.setLayoutParams(mapViewLP);

        sectionDetailAdapter = new SectionDetailAdapter(this, rankLists);
        pullRefreshListView4ScrollView.setAdapter(sectionDetailAdapter);
        pullRefreshListView4ScrollView.resetHeadViewBackground(R.color.blackFive);
        pullRefreshListView4ScrollView.setPullRefreshEnable(false);
        pullRefreshListView4ScrollView.setOnItemClickListener(this);
        detailScrollView.smoothScrollTo(0, 20);
        detailScrollView.setScrollViewLoadMoreListener(this);
        favouriteIV.setOnClickListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences(UtilsLocationManager.getInstance().getClass().getName(), 0);
        lat = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LAT, "0"));
        lon = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LON, "0"));
        getSectionDetail();
        getSegmentRank();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.section_detail_favourite_iv:
                loadingDialog = new LoadingDialog(
                        SectionDetailActivity.this,
                        getString(R.string.activity_record_detail_activity_loading), true);
                favouriteIV.setEnabled(false);
                favorSegment();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (rankLists == null || rankLists.size() == 0)
            return;
        SegmentRankDTO segmentRankDTO = rankLists.get(i - 1);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, segmentRankDTO.getUserId());
        startActivity(intent);
    }

    @Override
    public void loadMore() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        page++;
        getSegmentRank();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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

    private void favorSegment() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog)
                    loadingDialog.show();
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    return sectionManager.favorSegment(sectionID);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (null != loadingDialog)
                    loadingDialog.dismiss();
                favouriteIV.setEnabled(true);
                if (integer == -1)
                    return;
                favouriteCountTV.setText("（" + integer + "）");
                if (favouriteIV.isSelected()) {
                    favouriteIV.setSelected(false);
                } else {
                    favouriteIV.setSelected(true);
                }
            }
        });
    }

    private void getSectionDetail() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, SectionDetailListDTO>() {

            @Override
            protected SectionDetailListDTO doInBackground(Void... voids) {
                try {
                    return sectionManager.getSegmentInfo(sectionID, lon, lat);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SectionDetailListDTO sectionDetailListDTO) {
                if (sectionDetailListDTO == null)
                    return;
                detailTitle.setText(sectionDetailListDTO.getName());
                detailSlopes.setText(sectionDetailListDTO.getSlope() + "°");
                detailRatingbar.setRating(sectionDetailListDTO.getDifficult());
                memberCount.setText(sectionDetailListDTO.getChallengeNum() + getResources().getString(R.string.have_gone_to_section));
                favouriteCountTV.setText("（" + sectionDetailListDTO.getFavorNum() + "）");
                favouriteIV.setSelected(sectionDetailListDTO.isHasFavor());
                if (isShowKilometer) {
                    detailAltitude.setText((int) sectionDetailListDTO.getAltDiff() + LocaleManager.LocaleString.meter);
//                    detailDistance.setText((int) sectionDetailListDTO.getLegLength() / 1000 + getResources().getString(R.string.club_info_total_distance_unit));
                    double legLength = sectionDetailListDTO.getLegLength() / 1000;
                    if (legLength < 10) {
                        BigDecimal bd = new BigDecimal(legLength);
                        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                        detailDistance.setText(bd + getResources().getString(R.string.club_info_total_distance_unit));
                    } else {
                        detailDistance.setText((int) legLength + getResources().getString(R.string.club_info_total_distance_unit));
                    }
                } else {
                    detailAltitude.setText((int) LocaleManager.metreToFeet(sectionDetailListDTO.getAltDiff()) + LocaleManager.LocaleString.feet);
//                    detailDistance.setText((int) LocaleManager.kilometreToMile(sectionDetailListDTO.getLegLength() / 1000) + getResources().getString(R.string.mi));
                    double legLength = LocaleManager.kilometreToMile(sectionDetailListDTO.getLegLength()) / 1000;
                    if (legLength < 10) {
                        BigDecimal bd = new BigDecimal(legLength);
                        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                        detailDistance.setText(bd + getResources().getString(R.string.mi));
                    } else {
                        detailDistance.setText((int) legLength + getResources().getString(R.string.mi));
                    }
                }

                if (LocaleManager.isChineseTimeZone()) {
                    baiduMapSectionMarker(new LatLng(sectionDetailListDTO.getOriginLatitude(), sectionDetailListDTO.getOriginLongitude()));
                    drawLine(sectionDetailListDTO.getPolyline());
                } else {
                    googleSectionDetailListDTO = sectionDetailListDTO;
                    googleDraw();
                }
            }
        });
    }

    private void googleDraw() {
        if (googleSectionDetailListDTO == null || !isGoogleMapReady)
            return;
        googleMapSectionMarker(new com.google.android.gms.maps.model.LatLng(googleSectionDetailListDTO.getOriginLatitude(), googleSectionDetailListDTO.getOriginLongitude()));
        googleDrawLine(googleSectionDetailListDTO.getPolyline());
    }

    private void getSegmentRank() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<SegmentRankDTO>>() {

            @Override
            protected List<SegmentRankDTO> doInBackground(Void... voids) {
                try {
                    return sectionManager.getSegmentRank(sectionID, page, count);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<SegmentRankDTO> segmentRankDTOs) {
                isLoadingMore = false;
                if (segmentRankDTOs == null || segmentRankDTOs.size() == 0)
                    return;
                rankLists.addAll(segmentRankDTOs);
                sectionDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    private void baiduMapSectionMarker(LatLng start) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(start);
        LatLng desLatLng = converter.convert();

        BitmapDescriptor stopBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_section_map_marker_unselect);
        Bundle bundle = new Bundle();
        OverlayOptions stopOption = new MarkerOptions()
                .position(desLatLng).icon(stopBitmap).extraInfo(bundle);
        // 在地图上添加Marker，并显示
        baiduMap.addOverlay(stopOption);
    }

    private void googleMapSectionMarker(com.google.android.gms.maps.model.LatLng start) {
        if (googleMap == null)
            return;
        googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_section_map_marker_unselect)).position(start));
    }

    private void googleDrawLine(String string) {
        PolylineDecoder decoder = new PolylineDecoder();
        List<Point> points = decoder.decode(string);
        if (points == null || points.size() == 0 || googleMap == null)
            return;
        final com.google.android.gms.maps.model.LatLngBounds.Builder builder = new com.google.android.gms.maps.model.LatLngBounds.Builder();

        List<com.google.android.gms.maps.model.LatLng> googlePoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(points.get(i).getLat(), points.get(i).getLng());
            googlePoints.add(latLng);
            builder.include(latLng);
        }
        int color = Color.parseColor("#ff102d");
        if (points.size() >= 2
                && points.size() <= 10000) {
            final com.google.android.gms.maps.model.PolylineOptions polylineOptions = new com.google.android.gms.maps.model.PolylineOptions()
                    .width(8)
                    .color(color)
                    .addAll(googlePoints)
                    .visible(true)
                    .zIndex(50);
            if (googleMap == null)
                return;
            googleMap.addPolyline(polylineOptions);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private void drawLine(String string) {
        PolylineDecoder decoder = new PolylineDecoder();
        List<Point> points = decoder.decode(string);
        if (points == null || points.size() == 0 || baiduMap == null)
            return;
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);

        builder = new LatLngBounds.Builder();
        for (int i = 0; i < points.size(); i++) {
            converter.coord(new LatLng(points.get(i).getLat(), points.get(i).getLng()));
            LatLng desLatLng = converter.convert();
            baiduPoints.add(desLatLng);
            builder.include(desLatLng);
        }
        int color = Color.parseColor("#ff102d");
        if (points.size() >= 2
                && points.size() <= 10000) {
            OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions()
                    .width(8).color(color).points(baiduPoints)
                    .visible(true).zIndex(50);

            baiduMap.addOverlay(ooPolyline);
        }

        mapWidth = this.mMapView.getWidth();
        mapHeight = this.mMapView.getHeight();
        this.baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(
                builder.build(), mapWidth, mapHeight));
    }

    private void baiduMapInit() {
        final View logo = this.mMapView.getChildAt(1);
        logo.setVisibility(View.GONE);
        baiduMap = mMapView.getMap();
        UiSettings uiSettings = baiduMap.getUiSettings();
        uiSettings.setOverlookingGesturesEnabled(false);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        baiduMap.setOnMapLoadedCallback(this);
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));
        this.baiduMap.setMyLocationEnabled(true);
        //配置定位图层显示方式
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(zoomLevel));
        // this.baiduMap.setOnMapDrawFrameCallback(this);
        final LocationClientOption options = new LocationClientOption();
        options.setOpenGps(true);
        options.setPriority(LocationClientOption.GpsFirst);
        options.setCoorType("bd09ll");
        options.setScanSpan(LocationClientOption.MIN_SCAN_SPAN * 5);
        options.setAddrType("all");
        this.client = new LocationClient(this);
        this.client.setLocOption(options);
        this.client.start();
        this.client.requestLocation();

        // 屏蔽MapView的滚动事件
        this.mMapView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    detailScrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    detailScrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
    }

    private void googleMapInit() {
        if (!GoogleMapManager.isDeviceSupportGooglePlayService(this)) {
            noGooglePlayService.setVisibility(View.VISIBLE);
            return;
        }
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false).zoomControlsEnabled(false).zoomGesturesEnabled(true);
        mMapFragment = com.google.android.gms.maps.MapFragment.newInstance(options);

        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.section_google_mapview, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
        transparent_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        detailScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        detailScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        detailScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }

        });
    }

    private void googleMapOpitionsInit() {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(LocationClientOption.MIN_SCAN_SPAN * 5);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isGoogleMapReady = true;
        this.googleMap = googleMap;
        googleMapOpitionsInit();
        googleDraw();
    }

    @Override
    public void onMapLoaded() {
        if (mMapView == null)
            return;
        mapWidth = this.mMapView.getWidth();
        mapHeight = this.mMapView.getHeight();
        if (builder != null)
            this.baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(
                    builder.build(), mapWidth, mapHeight));
    }
}
