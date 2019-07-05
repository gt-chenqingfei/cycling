package com.beastbikes.android.modules.cycling.sections.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapConnectionCallBack;
import com.beastbikes.android.locale.googlemaputils.GoogleMapManager;
import com.beastbikes.android.modules.cycling.sections.dto.SectionListDTO;
import com.beastbikes.android.utils.SpeedXFormatUtil;
import com.beastbikes.android.utils.polyline.Point;
import com.beastbikes.android.utils.polyline.PolylineDecoder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/25.
 */
public class SectionMapManager implements BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener,
        BaiduMap.OnMapLoadedCallback, View.OnClickListener, OnMapReadyCallback, GoogleMapConnectionCallBack,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    private Activity activity;

    private MapView baiduMapView;
    private boolean isChinese;
    private MapView mMapView;
    private BaiduMap baiduMap;
    private Marker lastMarker;
    private final String baiduMarkerPosition = "baidu_Marker_Position";

    //谷歌地图
    private GoogleMap googleMap;
    private GoogleMapManager googleMapManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.maps.MapFragment mMapFragment;
    private boolean mRequestingLocationUpdates = false;
    private com.google.android.gms.maps.model.LatLng latlng;
    private int googlePosition;
    private com.google.android.gms.maps.model.Marker googleMarker;

    private List<SectionListDTO> sectionList;

    private RelativeLayout sectionInfo;
    private CircleImageView infoAvater;
    private TextView infoTitle;
    private TextView infoDistance;
    private TextView infoOwner;
    private RatingBar infoRating;
    private TextView infoElevation;
    private TextView infoTotalDistance;
    private TextView infoTotalDistanceUnit;

    private long segmentId;
    private float azimuth = 0f;
    private boolean isFirstTime = true;

    private static final Logger logger = LoggerFactory
            .getLogger(SectionMapManager.class);

    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private static final int DEFAULT_ZOOM_LEVEL = 16;

    public SectionMapManager(Activity activity) {
        isChinese = LocaleManager.isChineseTimeZone();
        this.activity = activity;
        baiduMapView = (MapView) this.activity.findViewById(R.id.activity_competition_section_mapview);
        sectionInfo = (RelativeLayout) this.activity.findViewById(R.id.frag_section_map_info_rl);
        infoAvater = (CircleImageView) this.activity.findViewById(R.id.item_competition_section_avater);
        infoTitle = (TextView) this.activity.findViewById(R.id.item_competition_section_title);
        infoDistance = (TextView) this.activity.findViewById(R.id.item_competition_section_diatance);
        infoOwner = (TextView) this.activity.findViewById(R.id.item_competition_section_owner);
        infoRating = (RatingBar) this.activity.findViewById(R.id.section_ratingbar);
        infoElevation = (TextView) this.activity.findViewById(R.id.section_elevation);
        infoTotalDistance = (TextView) this.activity.findViewById(R.id.item_competition_section_total_distance);
        infoTotalDistanceUnit = (TextView) this.activity.findViewById(R.id.item_competition_section_total_distance_unit);
        sectionInfo.setOnClickListener(this);
        if (isChinese) {
            baiduMapInit(baiduMapView);
        } else {
            baiduMapView.setVisibility(View.GONE);
            googleMapInit();
        }
    }

    private void baiduMapInit(MapView mapView) {
        this.mMapView = mapView;
        if (mMapView == null)
            return;
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
        //配置定位图层显示方式
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(this.zoomLevel));
        // this.baiduMap.setOnMapDrawFrameCallback(this);

        baiduMap.setOnMarkerClickListener(this);
        baiduMap.setOnMapClickListener(this);
        baiduMap.setOnMapLoadedCallback(this);
    }

    private void googleMapInit() {
        if (!GoogleMapManager.isDeviceSupportGooglePlayService(activity)) {
//            initnoGoogleLayout();
            return;
        }
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false).zoomControlsEnabled(false).zoomGesturesEnabled(true);
        mMapFragment = com.google.android.gms.maps.MapFragment.newInstance(options);

        android.app.FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.section_google_mapview, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (sectionInfo.getVisibility() == View.VISIBLE) {
            sectionInfo.setVisibility(View.GONE);
        }
        if (lastMarker != null)
            lastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_section_map_marker_unselect));
    }

    @Override
    public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
        if (sectionInfo.getVisibility() == View.VISIBLE) {
            sectionInfo.setVisibility(View.GONE);
        }
        if (googleMarker != null)
            googleMarker.setIcon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_section_map_marker_unselect));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frag_section_map_info_rl:
                Intent intent = new Intent(activity, SectionDetailActivity.class);
                intent.putExtra(SectionDetailActivity.SECTION_ID, segmentId);
                activity.startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onMapLoaded() {
        if (sectionList == null || sectionList.size() == 0)
            return;
        baiduDraw(sectionList);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (sectionList == null || sectionList.size() == 0)
            return false;
        int position = marker.getExtraInfo().getInt(baiduMarkerPosition, -1);
        if (position == -1)
            return false;
        marker.setIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_section_map_marker_selected));
        SectionListDTO sectionListDTO = sectionList.get(position);
        refreshMarkerInfo(sectionListDTO);

        if (lastMarker != null)
            lastMarker.setIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_section_map_marker_unselect));
        this.lastMarker = marker;
        return false;
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        if (sectionList == null || sectionList.size() == 0)
            return true;
        String title = marker.getTitle();
        int position = Integer.parseInt(title);
        marker.setIcon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_section_map_marker_selected));
        SectionListDTO sectionListDTO = sectionList.get(position);
        refreshMarkerInfo(sectionListDTO);

        if (googleMarker != null)
            googleMarker.setIcon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_section_map_marker_unselect));
        this.googleMarker = marker;
        return true;
    }

    private void refreshMarkerInfo(SectionListDTO sectionListDTO) {

        String avater = sectionListDTO.getLordAvatar();
        if (!TextUtils.isEmpty(avater)) {
            Picasso.with(activity).load(avater).fit().placeholder(R.drawable.ic_launcher).
                    error(R.drawable.ic_launcher).centerCrop().into(infoAvater);
        } else {
            Picasso.with(activity).load(R.drawable.ic_launcher).fit().placeholder(R.drawable.ic_launcher).
                    error(R.drawable.ic_launcher).centerCrop().into(infoAvater);
        }
        infoTitle.setText(sectionListDTO.getName());
        if (!TextUtils.isEmpty(sectionListDTO.getLordNick())) {
            infoOwner.setText(sectionListDTO.getLordNick() + activity.getResources().getString(R.string.occupy));
        } else {
            infoOwner.setText(activity.getResources().getString(R.string.section_no_lord));
        }
        infoRating.setRating(sectionListDTO.getDifficult());
        segmentId = sectionListDTO.getSegmentId();
        sectionInfo.setVisibility(View.VISIBLE);

        if (LocaleManager.isDisplayKM(activity)) {
            infoDistance.setText(activity.getResources().getString(R.string.distance_less_than) + SpeedXFormatUtil.BigDecimalOne(sectionListDTO.getRange() / 1000) + activity.getResources().getString(R.string.activity_param_label_distance_unit));
            infoElevation.setText(activity.getResources().getString(R.string.altitude_difference) + " " + (int) sectionListDTO.getAltDiff() + LocaleManager.LocaleString.meter);
            infoTotalDistance.setText("" + SpeedXFormatUtil.BigDecimalOne(sectionListDTO.getLegLength() / 1000));
            infoTotalDistanceUnit.setText(activity.getResources().getString(R.string.kilometre));
        } else {
            infoDistance.setText(activity.getResources().getString(R.string.distance_less_than) + SpeedXFormatUtil.BigDecimalOne(LocaleManager.kilometreToMile(sectionListDTO.getRange() / 1000)) + activity.getResources().getString(R.string.mi));
            infoElevation.setText(activity.getResources().getString(R.string.altitude_difference) + " " + (int) LocaleManager.metreToFeet(sectionListDTO.getAltDiff()) + LocaleManager.LocaleString.feet);
            infoTotalDistance.setText("" + SpeedXFormatUtil.BigDecimalOne(LocaleManager.kilometreToMile(sectionListDTO.getLegLength() / 1000)));
            infoTotalDistanceUnit.setText(activity.getResources().getString(R.string.miles));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMapOpitionsInit();
        googleMapManager = new GoogleMapManager();
        googleMapManager.buildGoogleApiClient(activity, this);
        googleMapManager.googleApiClientConnect();

        googleDraw(sectionList);
    }

    private void googleMapOpitionsInit() {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LocationClientOption.MIN_SCAN_SPAN * 5);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void zoomToPoint(Location location) {
        if (isChinese) {
            if (baiduMap == null)
                return;
            this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        } else {
            if (googleMap == null)
                return;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onSuccessed(Location mLastLocation, Bundle bundle) {
        if (!mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (googleMapManager != null && mLocationRequest != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleMapManager.getmGoogleApiClient(), mLocationRequest, this);
        }
    }

    @Override
    public void onFail(ConnectionResult connectionResult) {

    }

    @Override
    public void onSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            logger.info("google onLocationChanged null");
            return;
        }
        latlng = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
        if (isFirstTime) {
            isFirstTime = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void baiduDraw(List<SectionListDTO> sectionList) {
        if (baiduMap == null)
            return;
        baiduMap.clear();
        for (int i = 0; i < sectionList.size(); i++) {
            LatLng latLng = new LatLng(sectionList.get(i).getOriginLatitude(), sectionList.get(i).getOriginLongitude());
            baiduMapSectionMarker(latLng, i);
            PolylineDecoder decoder = new PolylineDecoder();
            List<Point> points = decoder.decode(sectionList.get(i).getPolyline());
            baiduMapSectionLine(points);
        }
    }

    private void googleDraw(List<SectionListDTO> sectionList) {
        if (googleMap == null || sectionList == null || sectionList.size() == 0)
            return;
        googleMap.clear();
        for (int i = 0; i < sectionList.size(); i++) {
            com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(sectionList.get(i).getOriginLatitude(), sectionList.get(i).getOriginLongitude());
            googleMapSectionMarker(latLng, i);
            PolylineDecoder decoder = new PolylineDecoder();
            List<Point> points = decoder.decode(sectionList.get(i).getPolyline());
            googleMapSectionLine(points);
        }
    }

    private void baiduMapSectionMarker(LatLng start, int position) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(start);
        LatLng desLatLng = converter.convert();
        BitmapDescriptor stopBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_section_map_marker_unselect);
        Bundle bundle = new Bundle();
        bundle.putInt(baiduMarkerPosition, position);
        OverlayOptions stopOption = new MarkerOptions()
                .position(desLatLng).icon(stopBitmap).extraInfo(bundle);
        baiduMap.addOverlay(stopOption);
    }

    private void googleMapSectionMarker(com.google.android.gms.maps.model.LatLng start, int position) {
        googlePosition = position;
        googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_section_map_marker_unselect)).position(start).title("" + position));
    }

    private void baiduMapSectionLine(List<Point> points) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        List<LatLng> baiduPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            LatLng sourceLatLng = new LatLng(points.get(i).getLat(), points.get(i).getLng());
            converter.coord(sourceLatLng);
            LatLng desLatLng = converter.convert();
            baiduPoints.add(desLatLng);
        }
        int color = Color.parseColor("#ff102d");
        if (points.size() >= 2
                && points.size() <= 10000) {
            OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions()
                    .width(8).color(color).points(baiduPoints)
                    .visible(true).zIndex(50);
            baiduMap.addOverlay(ooPolyline);
        }
    }

    private void googleMapSectionLine(List<Point> points) {
        List<com.google.android.gms.maps.model.LatLng> googlePoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            googlePoints.add(new com.google.android.gms.maps.model.LatLng(points.get(i).getLat(), points.get(i).getLng()));
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
    }

    public void filterFailed() {
        if (baiduMap != null)
            baiduMap.clear();
        if (googleMap != null)
            googleMap.clear();
    }

    public void notifyDataSetChanged(List<SectionListDTO> sectionListDTOs) {
        if (sectionListDTOs == null || sectionListDTOs.size() == 0)
            return;
        this.sectionList = sectionListDTOs;
        if (isChinese) {
            baiduDraw(sectionList);
        } else {
            googleDraw(sectionList);
        }
    }

    public void onReceiveLocation(BDLocation location) {
        if (location == null || this.mMapView == null)
            return;
        try {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            final LatLng latlng = new LatLng(latitude, longitude);
            final MyLocationData data = new MyLocationData.Builder()
                    .direction(this.azimuth).accuracy(location.getRadius())
                    .latitude(latitude).longitude(longitude).build();
            this.baiduMap.setMyLocationData(data);
            if (this.isFirstTime) {
                this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newLatLng(latlng));
            }
            this.isFirstTime = false;
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
    }
}
