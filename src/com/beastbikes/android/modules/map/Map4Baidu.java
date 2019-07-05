package com.beastbikes.android.modules.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.beastbikes.android.R;
import com.beastbikes.android.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by chenqingfei on 16/5/11.
 */
public class Map4Baidu extends MapBase<LatLng> implements BDLocationListener, BaiduMap.OnMapStatusChangeListener {

    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient client;

    public Map4Baidu(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.speedx_map_with_baidu, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (client != null) {
            client.stop();
            client.unRegisterLocationListener(this);
        }
    }

    @Override
    protected void onInitView() {
        mapView = (MapView) findViewById(R.id.speedx_map_with_baidu_view);
    }

    @Override
    protected List<Point> getScreenPoints(List<LatLng> points) {
        if (points == null || baiduMap.getProjection() == null)
            return null;

        List<Point> screenPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            screenPoints.add(baiduMap.getProjection().toScreenLocation(points.get(i)));
        }
        return screenPoints;
    }

    @Override
    protected String getElevations(List<LatLng> points) {
        if (points == null)
            return null;

        final StringBuilder latLngSb = new StringBuilder();
        Iterator<LatLng> it = points.iterator();
        while (it.hasNext()) {
            LatLng latlng = it.next();
            latLngSb.append(latlng.latitude).append(",")
                    .append(latlng.longitude);
            if (it.hasNext()) {
                latLngSb.append('|');
            }
        }

        return latLngSb.toString();
    }

    @Override
    public void requestLocation() {
        if (this.client == null) {
            this.client = new LocationClient(context);
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

    @Override
    public void setMyLocationConfigeration() {
        BitmapDescriptor marker = BitmapDescriptorFactory
                .fromResource(R.drawable.route_map_make_location);
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, marker));
    }

    @Override
    public void zoomTo(float level) {
        super.zoomTo(level);
        final float min = this.baiduMap.getMinZoomLevel();
        level = Math.max(this.zoomLevel - 1, min);
        final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
        this.baiduMap.animateMapStatus(u);
        this.zoomLevel = level;
    }

    @Override
    public void init(final Activity context, final MapListener mapListener,
                     final boolean isPrivate, ScrollView s) {
        doInit(context, mapListener, isPrivate, s);

        this.mapView.setVisibility(View.VISIBLE);
        this.mapView.showZoomControls(false);
        // 隐藏百度地图Logo
        this.mapView.getChildAt(1).setVisibility(View.GONE);
        // 隐藏百度地图比例尺
        View view = mapView.getChildAt(3);
        if (view != null)
            view.setVisibility(View.GONE);

        // 屏蔽MapView的滚动事件
        this.mapView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
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

        this.baiduMap = this.mapView.getMap();
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                if (mapListener != null) {
                    mapListener.onMapLoaded();
                }
            }
        });

        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        this.baiduMap.setMyLocationEnabled(true);                                 //定位?
        this.baiduMap.getUiSettings().setCompassEnabled(false);


        if (baiduMap == null)
            return;
        this.baiduMap.addOverlay(buildMask());

        this.baiduMap.setOnMapDrawFrameCallback(new BaiduMap.OnMapDrawFrameCallback() {
            @Override
            public void onMapDrawFrame(GL10 gl10, MapStatus mapStatus) {
                if (!isPrivate) return;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (baiduMap.getProjection() != null) {
                            if (mapListener != null) {
                                mapListener.onMapDrawFrame();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void drawStartingPoint(LatLng start, LatLng end) {
        if (null == start || end == null || baiduMap == null)
            return;

        baiduMap.clear();
        BitmapDescriptor startBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_activity_detail_start);
        OverlayOptions startOption = new MarkerOptions()
                .position(start).icon(startBitmap);

        baiduMap.addOverlay(startOption);

        BitmapDescriptor stopBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_activity_finish_end);
        OverlayOptions stopOption = new MarkerOptions()
                .position(end).icon(stopBitmap);
        baiduMap.addOverlay(stopOption);

        this.baiduMap.addOverlay(buildMask());
    }

    @Override
    public void drawLine(List<LatLng> points) {
        if (null == points || points.size() < 2) {
            return;
        }

        List<Integer> colors = BitmapUtil.getColors(points.size());
        OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions()
                .width(8).colorsValues(colors).points(points)
                .visible(true).zIndex(50);
        baiduMap.addOverlay(ooPolyline);
    }

    @Override
    public void zoomToSpan(List<LatLng> points) {
        if (null == points || points.isEmpty() || baiduMap == null)
            return;

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < points.size(); i++) {
            builder.include(points.get(i));
        }

        int width = this.mapView.getWidth();
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(
                builder.build(), width, width), 1000);
    }

    @Override
    public void snapshot(final SnapshotReadyListener listener) {
        if (listener == null || this.baiduMap == null)
            return;

        this.baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                listener.onSnapshotReady(bitmap);
            }
        });
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
    public void onReceiveLocation(BDLocation bdLocation) {
        final double latitude = bdLocation.getLatitude();
        final double longitude = bdLocation.getLongitude();
        final LatLng latlng = new LatLng(latitude, longitude);

        final MyLocationData data = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius()).latitude(latitude)
                .longitude(longitude).build();

        this.baiduMap.setMyLocationData(data);
        this.client.stop();
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
        this.mapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                zoomTo(baiduMap.getMaxZoomLevel() - 2);
            }
        }, 1000);

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        if (onMapStatusChangeListener != null) {
            onMapStatusChangeListener.onMapStatusChangeStart(
                    mapStatus.target.latitude, mapStatus.target.longitude);
        }
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        if (onMapStatusChangeListener != null) {
            onMapStatusChangeListener.onMapStatusChangeFinish(
                    mapStatus.target.latitude, mapStatus.target.longitude);
        }
    }

    /**
     * 地图添加蒙层
     *
     * @return
     */
    private OverlayOptions buildMask() {

        LatLng ll0 = new LatLng(89.766343, -465.253292);
        LatLng ll1 = new LatLng(-4076.9025, -727.819512);
        LatLng ll2 = new LatLng(-5255.118995, 417.816956);
        LatLng ll3 = new LatLng(89.766343, 695.689728);
        List<LatLng> lls = new ArrayList<LatLng>();
        lls.add(ll0);
        lls.add(ll1);
        lls.add(ll2);
        lls.add(ll3);
        OverlayOptions polygonOption = new PolygonOptions().points(lls)
                .stroke(new Stroke(0, 0x8f000000)).fillColor(0x6f000000);
        return polygonOption;
    }

}
