package com.beastbikes.android.modules.map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.beastbikes.android.utils.Gps2GoogleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 16/5/10.
 */
public class SpeedxMap extends RelativeLayout implements MapListener {

    private MapBase mapBase;
    private FrameLayout mapContainer;
    private MapType mapType;
    private CoordinateConverter converter;
    private List<LatLng> baiduPoints = null;
    private List<com.google.android.gms.maps.model.LatLng> googlePoints = null;
    private List<com.mapbox.mapboxsdk.geometry.LatLng> mapBoxPoints = null;
    private MapReadyListener mapReadyListener;
    private boolean isPrivate;

    public SpeedxMap(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.speedx_map_layout, this);
        this.converter = new CoordinateConverter();
        this.converter.from(CoordinateConverter.CoordType.GPS);
    }

    public void onResume() {
        if (mapBase == null)
            return;
        mapBase.onResume();
    }

    public void onPause() {
        if (mapBase == null)
            return;
        mapBase.onPause();
    }

    public void onDestroy() {
        if (mapBase == null)
            return;
        mapBase.onDestroy();
    }

    public void onLowMemory() {
        if (mapBase == null)
            return;
        mapBase.onLowMemory();
    }

    public void onCreate(Bundle savedInstanceState) {
        if (mapBase == null)
            return;
        mapBase.onCreate(savedInstanceState);
    }

    public void finish() {
        if (mapBase == null)
            return;
        mapBase.finish();
    }

    public void onSaveInstanceState(Bundle outState) {
        if (mapBase == null)
            return;
        mapBase.onSaveInstanceState(outState);
    }

    public void setMapFullScreen() {
        if (mapBase == null)
            return;
        mapBase.setMapFullScreen();
    }

    public void setMapWarpScreen() {
        if (mapBase == null)
            return;
        mapBase.setMapWarpScreen();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mapContainer = (FrameLayout) findViewById(R.id.map_container);
    }

    public void setUp(MapType type, Activity context, boolean isPrivate, ScrollView scrollView,
                      MapReadyListener l) {
        this.isPrivate = isPrivate;
        this.mapType = type;
        this.mapReadyListener = l;

        setUp(type);
        mapBase.init(context, this, isPrivate, scrollView);

    }

    public void setUp(MapType type, Activity context) {
        setUp(type);
        this.mapType = type;
        this.mapType = type;
        if (mapBase == null)
            return;
        mapBase.init(context, this, false, null);
    }

    private void setUp(MapType type) {
        switch (type) {
            case BaiDu:
                mapBase = new Map4Baidu(getContext());
                break;
            case Google:
                mapBase = new Map4Google(getContext());
                break;
            case MapBox:
                mapBase = new Map4MapBox(getContext());
                break;
            default:
                mapBase = new Map4Baidu(getContext());
                break;
        }
        if (mapContainer != null) {
            mapContainer.removeAllViews();
            mapContainer.addView(mapBase);
        }
    }

    public void drawMapPoint(List<SampleDTO> points) {

        if (points == null || points.size() <= 1 || mapBase == null)
            return;
        switch (mapType) {
            case BaiDu:
                baiduPoints = convertBaiduPoints(points);
                if (null == baiduPoints || baiduPoints.size() < 2) {
                    break;
                }
                if (baiduPoints != null) {
                    mapBase.drawStartingPoint(baiduPoints.get(0), baiduPoints.get(baiduPoints.size() - 1));
                    mapBase.drawLine(baiduPoints);
                    mapBase.zoomToSpan(baiduPoints);
                }
                break;
            case Google:
                googlePoints = convertGooglePoints(points);
                if (null == googlePoints || googlePoints.size() < 2) {
                    break;
                }
                if (googlePoints != null) {
                    mapBase.drawStartingPoint(googlePoints.get(0), googlePoints.get(googlePoints.size() - 1));
                    mapBase.drawLine(googlePoints);
                    mapBase.zoomToSpan(googlePoints);
                }
                break;
            case MapBox:
                mapBoxPoints = convertMapBoxPoints(points);
                if (null == mapBoxPoints || mapBoxPoints.size() < 2) {
                    break;
                }
                if (mapBoxPoints != null) {
                    mapBase.drawStartingPoint(mapBoxPoints.get(0), mapBoxPoints.get(mapBoxPoints.size() - 1));
                    mapBase.drawLine(mapBoxPoints);
                    mapBase.zoomToSpan(mapBoxPoints);
                }
                break;
        }

        switchMapHiddenState(isPrivate);
    }

    public void switchMapHiddenState(boolean isPrivate) {

        if (mapBase == null)
            return;

        switch (mapType) {
            case BaiDu:
                break;
            case Google:
                break;
            case MapBox:
                mapBase.setMapStyle(isPrivate);
                break;
        }
    }

    public String getElevations() {
        if (mapBase == null)
            return null;
        switch (mapType) {
            case BaiDu:
                return mapBase.getElevations(baiduPoints);
            case Google:
                return mapBase.getElevations(googlePoints);
            case MapBox:
                return mapBase.getElevations(mapBoxPoints);
        }
        return null;
    }

    public void snapshot(MapBase.SnapshotReadyListener listener) {
        if (mapBase == null)
            return;

        if (isPrivate && mapType != MapType.MapBox) {

//            Bitmap mapBmp = BitmapUtil.getBitmapByView(activityBlurLayer);
//            listener.onSnapshotReady(mapBmp);
            return;
        }

        if (listener != null) {
            mapBase.snapshot(listener);
        }
    }

    public void zoomTo(double latitude, double longitude) {
        if (mapBase == null)
            return;
        switch (mapType) {
            case BaiDu:
                List<LatLng> baiduList = new ArrayList<>();
                baiduList.add(new LatLng(latitude, longitude));
                mapBase.zoomToSpan(baiduList);
                break;
            case Google:
                List<com.google.android.gms.maps.model.LatLng> googleList = new ArrayList<>();
                googleList.add(new com.google.android.gms.maps.model.LatLng(latitude, longitude));
                mapBase.zoomToSpan(googleList);
                break;
            case MapBox:
                List<com.mapbox.mapboxsdk.geometry.LatLng> mapBoxList = new ArrayList<>();
                mapBoxList.add(new com.mapbox.mapboxsdk.geometry.LatLng(latitude, longitude));
                mapBase.zoomToSpan(mapBoxList);
                break;
        }
    }

    public void zoomTo(float level) {
        mapBase.zoomTo(level);
    }

    public float getZoomLevel() {
        return mapBase.getZoomLevel();
    }

    public void requestLocation() {
        mapBase.requestLocation();
    }

    public void setOnMapStatusChangeListener(OnMapStatusChangeListener listener) {
        if (mapBase == null)
            return;
        mapBase.setOnMapStatusChangeListener(listener);
    }

    public void setMyLocationConfigeration() {
        mapBase.setMyLocationConfigeration();
    }

    public void setZoomIconVisibility(int visibility) {
//        this.ivZoom.setVisibility(visibility);
    }

    private List<LatLng> convertBaiduPoints(List<SampleDTO> list) {
        if (converter == null || list == null || list.size() <= 0) {
            return null;
        }

        List<LatLng> points = new ArrayList<>();
        for (SampleDTO dto : list) {
            double latitude = dto.getLatitude1();
            double longitude = dto.getLongitude1();
            if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                continue;
            }
            LatLng latLng = new LatLng(latitude, longitude);
            converter.coord(latLng);
            latLng = converter.convert();
            points.add(latLng);
        }
        return points;
    }

    private List<com.google.android.gms.maps.model.LatLng> convertGooglePoints(List<SampleDTO> list) {
        if (list == null || list.size() <= 0)
            return null;

        List<com.google.android.gms.maps.model.LatLng> points = new ArrayList<>();
        for (SampleDTO dto : list) {
            double latitude = dto.getLatitude1();
            double longitude = dto.getLongitude1();
            if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                continue;
            }
            com.google.android.gms.maps.model.LatLng latLng = Gps2GoogleUtil.transform(latitude, longitude);
            com.google.android.gms.maps.model.LatLng ll = new com.google.android.gms.maps.model.LatLng(latLng.latitude, latLng.longitude);

            points.add(ll);
        }
        return points;
    }

    private List<com.mapbox.mapboxsdk.geometry.LatLng> convertMapBoxPoints(List<SampleDTO> list) {
        if (list == null || list.size() <= 0)
            return null;

        List<com.mapbox.mapboxsdk.geometry.LatLng> points = new ArrayList<>();
        for (SampleDTO dto : list) {
            double latitude = dto.getLatitude1();
            double longitude = dto.getLongitude1();
            if (latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                continue;
            }
            points.add(new com.mapbox.mapboxsdk.geometry.LatLng(latitude, longitude));

        }
        return points;
    }

    @Override
    public void onMapLoaded() {

        if (mapReadyListener != null) {
            mapReadyListener.onMapReady();
        }
    }

    @Override
    public void onMapDrawFrame() {
        switchMapHiddenState(isPrivate);
    }

    public interface MapReadyListener {
        public void onMapReady();
    }

    public interface OnMapStatusChangeListener {
        void onMapStatusChangeStart(double latitude, double longitude);

        void onMapStatusChangeFinish(double latitude, double longitude);
    }
}
