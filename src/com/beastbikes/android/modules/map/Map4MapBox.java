package com.beastbikes.android.modules.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.utils.BitmapUtil;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.UiSettings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenqingfei on 16/5/11.
 */
public class Map4MapBox extends MapBase<LatLng> implements OnMapReadyCallback {

    private RelativeLayout mapboxMapviewRL;
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap mapboxMap;
    private IconFactory factory;
    private boolean isFinish = false;
    private boolean hasResume = false;
    // new mapbox style:    http://mapbox-101-890045911.cn-north-1.elb.amazonaws.com.cn:2999/pages/streets-v8/style.json
    private final String privateStyle = "mapbox://styles/speedx/cilixxfb3000jazkr6zt4qcgn";

    public Map4MapBox(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.speedx_map_with_mapbox, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        hasResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasResume) {
            mapView.onPause();
            hasResume = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void finish() {
        super.finish();
        isFinish = true;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

            }
        });
    }

    @Override
    protected void onInitView() {
        mapboxMapviewRL = (RelativeLayout) findViewById(R.id.mapbox_mapview_rl);
    }

    public void setMapStyle(boolean isPrivate) {
        if (mapView != null) {
            if (isPrivate) {
                mapView.setStyleUrl(privateStyle);
                if (mapboxMap != null) {
                    UiSettings settings = mapboxMap.getUiSettings();
                    settings.setAllGesturesEnabled(false);
                }

            } else {
                mapView.setStyleUrl(Style.DARK);
            }
        }
    }

    @Override
    protected List<Point> getScreenPoints(List<LatLng> points) {

        if (points == null || mapboxMap == null || mapboxMap.getProjection() == null || isFinish)
            return null;

        List<Point> screenPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            PointF pointF = mapboxMap.getProjection().toScreenLocation(points.get(i));
            screenPoints.add(new Point((int) pointF.x, (int) pointF.y));
        }
        return screenPoints;
    }

    @Override
    protected String getElevations(List<LatLng> points) {
        if (points == null)
            return null;

        final StringBuilder latLngSb = new StringBuilder();
        Iterator<com.mapbox.mapboxsdk.geometry.LatLng> it = points.iterator();
        while (it.hasNext()) {
            com.mapbox.mapboxsdk.geometry.LatLng latlng = it.next();
            latLngSb.append(latlng.getLatitude()).append(",")
                    .append(latlng.getLongitude());
            if (it.hasNext()) {
                latLngSb.append('|');
            }
        }
        return latLngSb.toString();
    }

    @Override
    public void requestLocation() {

    }

    @Override
    public void setMyLocationConfigeration() {

    }

    @Override
    public void zoomTo(float level) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void init(final Activity context, final MapListener mapListener,
                     final boolean isPrivate, ScrollView s) {
        doInit(context, mapListener, isPrivate, s);


        MapboxMapOptions mapboxMapOptions = new MapboxMapOptions();
        mapboxMapOptions.accessToken(BeastBikes.getMapBoxAccessToken());
        mapboxMapOptions.attributionEnabled(false);
        mapboxMapOptions.logoEnabled(false);
        mapboxMapOptions.zoomControlsEnabled(false);
        mapboxMapOptions.rotateGesturesEnabled(false);
        mapboxMapOptions.zoomGesturesEnabled(true);
        mapboxMapOptions.compassEnabled(false);
        mapboxMapOptions.scrollGesturesEnabled(true);
        mapboxMapOptions.styleUrl(Style.DARK);
        mapView = new MapView(context, mapboxMapOptions);
        factory = IconFactory.getInstance(context);
        this.mapboxMapviewRL.addView(mapView);
        this.mapView.getMapAsync(this);

        // 屏蔽MapView的滚动事件
        this.mapView.getChildAt(0).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (scrollView != null) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        scrollView.requestDisallowInterceptTouchEvent(false);
                    } else {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void drawStartingPoint(LatLng start, LatLng end) {
        if (null == start || end == null || mapboxMap == null)
            return;

        Icon sprite = factory.fromResource(R.drawable.ic_activity_detail_start);
        mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().icon(sprite).position(start));

        Icon sprite2 = factory.fromResource(R.drawable.ic_activity_finish_end);
        mapboxMap.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().icon(sprite2).position(end));

    }

    @Override
    public void drawLine(List<LatLng> points) {
        if (points == null || points.size() < 2 || null == mapboxMap || isFinish)
            return;

        int color = isPrivate ? Color.parseColor("#dedede") : Color.parseColor("#ff102d");
        final int size = points.size();
        if (size <= 5 || isPrivate) {
            PolylineOptions options = new PolylineOptions().addAll(points).color(color).width(4);
            mapboxMap.addPolyline(options);
        } else {
            List<Integer> colors = BitmapUtil.getColors(size);
            for (int i = 5; i < points.size(); i++) {
                if (i < colors.size()) {
                    color = colors.get(i);
                }

                List<LatLng> lls = new ArrayList<>();
                for (int j = 0; j <= 5; j++) {
                    lls.add(points.get(i - j));
                }
                PolylineOptions options = new PolylineOptions().addAll(lls).color(color).width(4);
                mapboxMap.addPolyline(options);
            }
        }
    }

    @Override
    public void zoomToSpan(List<LatLng> points) {
        if (null == points || points.isEmpty() || mapboxMap == null || isFinish)
            return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < points.size(); i++) {
            builder.include(points.get(i));
        }
        LatLngBounds latLngBounds = builder.build();
        DisplayMetrics dm = getDm();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 20, 100, 20, dm.heightPixels - dm.widthPixels + 20);
        mapboxMap.animateCamera(cameraUpdate, 1000);
    }

    @Override
    public void snapshot(final SnapshotReadyListener listener) {
        if (listener == null)
            return;
        if (mapboxMap != null) {
            mapboxMap.snapshot(new MapboxMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    listener.onSnapshotReady(snapshot);
                }
            });
        }
    }

    @Override
    public void setMapFullScreen() {
        super.setMapFullScreen();
        DisplayMetrics dm = getDm();
        ViewGroup.LayoutParams mapLp1 = this.mapView.getLayoutParams();
        mapLp1.width = dm.widthPixels;
        mapLp1.height = dm.heightPixels;
        this.mapView.setLayoutParams(mapLp1);
    }

    @Override
    public void setMapWarpScreen() {
        super.setMapWarpScreen();
        DisplayMetrics dm = getDm();
        ViewGroup.LayoutParams mapLp1 = this.mapView.getLayoutParams();
        mapLp1.width = dm.widthPixels;
        mapLp1.height = dm.widthPixels;
        this.mapView.setLayoutParams(mapLp1);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        if (mapboxMap == null || isFinish)
            return;
        this.mapboxMap = mapboxMap;

        if (mapListener != null) {
            mapListener.onMapLoaded();
        }
    }

}
