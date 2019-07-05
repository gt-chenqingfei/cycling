package com.beastbikes.android.modules.cycling.route.ui;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.avos.avoscloud.AVAnalytics;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteNodeDTO;
import com.beastbikes.android.modules.cycling.simplify.SimplifyUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Alias("查看精品路线地图详情")
@LayoutResource(R.layout.route_map_activity)
public class RouteMapActivity extends SessionFragmentActivity implements
        OnClickListener, BDLocationListener, SensorEventListener {

    public static final String EXTRA_ROUTE_ID = "route_id";
    public static final String EXTRA_ROUTE_DISTANCE = "route_distance";
    public static final String EXTRA_POINT = "point";

    private static final Logger logger = LoggerFactory
            .getLogger(RouteMapActivity.class);

    @IdResource(R.id.route_map_activity_view)
    private MapView mapView;

    @IdResource(R.id.route_map_activity_map_button_location)
    private ImageView btnLocation;

    @IdResource(R.id.route_map_activity_map_button_zoom_out)
    private ImageView btnZoonOut;

    @IdResource(R.id.route_map_activity_map_button_zoom_in)
    private ImageView btnZoomIn;

    @IdResource(R.id.route_map_activity_elevation)
    private ImageView elevationTv;

    private BaiduMap baiduMap;
    private LocationClient client;
    private float zoomLevel = 16;

    private final float[] gravity = new float[3];
    private final float[] magnetic = new float[3];

    private float azimuth = 0f;

    private Sensor aSensor;
    private Sensor mSensor;
    private SensorManager sm;

    private boolean first = true;

    private RouteManager routeManager;

    private RouteNodeComparator nodeComparator;
    private int simplifyLevel = 5;
    private List<LatLng> points = new ArrayList<LatLng>();
    private boolean isUse = false;
    private double totalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.aSensor = this.sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mSensor = this.sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.sm.registerListener(this, this.aSensor,
                SensorManager.SENSOR_DELAY_UI);
        this.sm.registerListener(this, this.mSensor,
                SensorManager.SENSOR_DELAY_UI);

        this.nodeComparator = new RouteNodeComparator();

        this.routeManager = new RouteManager(this);

        this.client = new LocationClient(this);
        this.client.registerLocationListener(this);

        this.mapView.showZoomControls(false);
        // 隐藏百度地图Logo
        this.mapView.getChildAt(1).setVisibility(View.GONE);
        // 隐藏百度地图比例尺
        this.mapView.getChildAt(3).setVisibility(View.GONE);

        final LocationClientOption options = new LocationClientOption();
        options.setOpenGps(true);
        options.setPriority(LocationClientOption.GpsFirst);
        options.setCoorType("bd09ll");
        options.setScanSpan(LocationClientOption.MIN_SCAN_SPAN * 5);
        options.setAddrType("all");

        this.baiduMap = this.mapView.getMap();
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                LocationMode.FOLLOWING, true, null));
        this.baiduMap.setMyLocationEnabled(true);
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));

        this.btnLocation.setOnClickListener(this);
        this.btnZoomIn.setOnClickListener(this);
        this.btnZoonOut.setOnClickListener(this);
        this.elevationTv.setOnClickListener(this);

        this.elevationTv.setVisibility(View.GONE);

        this.client.setLocOption(options);

        Intent intent = getIntent();
        if (null == intent)
            return;

        String routeId = intent.getStringExtra(EXTRA_ROUTE_ID);
        this.totalDistance = intent.getDoubleExtra(EXTRA_ROUTE_DISTANCE, 0);
        if (!TextUtils.isEmpty(routeId)) {
            this.getRouteById(routeId);
        }

        String point = intent.getStringExtra(EXTRA_POINT);
        if (!TextUtils.isEmpty(point) && point.length() > 2) {
            point = point.substring(1, point.length() - 2);
            String[] p = point.split(",");
            if (null != p && p.length == 2) {
                LatLng latLng = new LatLng(Double.valueOf(p[0]), Double.valueOf(p[1]));
                this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newLatLng(latLng));
                if (this.client.isStarted()) {
                    this.client.stop();
                }

                this.baiduMap.clear();

                final BitmapDescriptor originMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_muster_icon);
                final OverlayOptions originOptions = new MarkerOptions().position(
                        latLng).icon(originMarker);
                baiduMap.addOverlay(originOptions);

            }
        }

        SpeedxAnalytics.onEvent(this, "查看精品路线地图",null);

    }

    @Override
    protected void onResume() {
        this.mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (this.client.isStarted()) {
            this.client.stop();
        }
        this.baiduMap.setMyLocationEnabled(false);
        this.client.unRegisterLocationListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_map_activity_map_button_location: {
                this.first = true;
                this.client.requestLocation();
                this.client.start();
                break;
            }
            case R.id.route_map_activity_map_button_zoom_in: {
                final float min = this.baiduMap.getMinZoomLevel();
                final float level = Math.max(this.zoomLevel - 1, min);
                final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
                this.baiduMap.animateMapStatus(u);
                this.zoomLevel = level;
                this.simplifyLevel--;
                this.drawRouteLine(points);
                break;
            }
            case R.id.route_map_activity_map_button_zoom_out: {
                final float max = this.baiduMap.getMaxZoomLevel();
                final float level = Math.min(this.zoomLevel + 1, max);
                final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
                this.baiduMap.animateMapStatus(u);
                this.zoomLevel = level;
                this.simplifyLevel++;
                this.drawRouteLine(points);
                break;
            }
            case R.id.route_map_activity_elevation: {
                SpeedxAnalytics.onEvent(this, "查看精品路线海拔趋势图",null);
                this.startElevationView(points);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (null == location)
            return;

        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        final LatLng latlng = new LatLng(latitude, longitude);
        final MyLocationData data = new MyLocationData.Builder()
                .direction(this.azimuth).accuracy(location.getRadius())
                .latitude(latitude).longitude(longitude).build();
        this.baiduMap.setMyLocationData(data);
        this.zoomLevel = this.baiduMap.getMapStatus().zoom;
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));

        if (first) {
            this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newLatLng(latlng));
        }

        first = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, this.gravity, 0, 3);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, this.magnetic, 0, 3);
                break;
            default:
                return;
        }

        final float[] R = new float[9];
        final float[] I = new float[9];
        final float[] values = new float[3];

        if (!SensorManager.getRotationMatrix(R, I, this.gravity, this.magnetic))
            return;

        SensorManager.getOrientation(R, values);

        for (int i = 0; i < values.length; i++) {
            values[i] = (float) Math.toDegrees(values[i]);
        }

        this.azimuth = values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 根据routeId获取路线节点
     *
     * @param routeId
     */
    private void getRouteById(final String routeId) {
        if (TextUtils.isEmpty(routeId))
            return;

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, RouteDTO>() {

            @Override
            protected RouteDTO doInBackground(String... params) {
                try {
                    return routeManager.getRouteInfoByRouteId(params[0]);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(RouteDTO result) {
                if (null == result)
                    return;

                List<RouteNodeDTO> nodes = result.getNodes();
                if (null == nodes || nodes.size() < 2)
                    return;

                Collections.sort(nodes, nodeComparator);

                for (RouteNodeDTO rnd : nodes) {
                    points.add(new LatLng(rnd.getLatitude(), rnd.getLongitude()));
                }

                drawRouteLine(points);
                elevationTv.setVisibility(View.VISIBLE);
            }

        }, routeId);
    }

    /**
     * 跳转海拔图页面
     *
     * @param points
     */
    private void startElevationView(List<LatLng> points) {
        if (null == points || points.size() < 1)
            return;

        logger.trace("big map activity source sample line size = "
                + points.size());
        float simplifyLevel = 0.0001f;
        while (points.size() > 80) {
            try {
                points = SimplifyUtil.assertPointsEqual(simplifyLevel, points);
                simplifyLevel += 0.0003f;
            } catch (BusinessException e) {
                e.printStackTrace();
                break;
            }
        }
        logger.trace("big map activity source sample line size = "
                + points.size());

        StringBuilder latLngSb = new StringBuilder();

        for (LatLng ll : points) {
            latLngSb.append(ll.latitude + "," + ll.longitude + "|");
        }
        latLngSb.deleteCharAt(latLngSb.length() - 1);

        Intent intent = new Intent(this, RouteElevationActivity.class);
        intent.putExtra(RouteElevationActivity.EXTRA_NODES,
                latLngSb.toString());
        intent.putExtra(RouteElevationActivity.EXTRA_DISTANCE,
                this.totalDistance / 1000);
        this.startActivity(intent);
    }

    /**
     * 显示路线
     *
     * @param points
     */
    private void drawRouteLine(List<LatLng> points) {
        if (null == points || points.size() < 2)
            return;

        if (null == baiduMap)
            return;

        this.baiduMap.clear();

        final BitmapDescriptor originMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.route_map_line_start_icon);
        final OverlayOptions originOptions = new MarkerOptions().position(
                points.get(0)).icon(originMarker);
        baiduMap.addOverlay(originOptions);

        final BitmapDescriptor endMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.route_map_line_end_icon);
        final OverlayOptions endOptions = new MarkerOptions().position(
                points.get(points.size() - 1)).icon(endMarker);
        baiduMap.addOverlay(endOptions);

        logger.trace("big route map source nodes line size = " + points.size());
        try {
            if (simplifyLevel < 9)
                points = SimplifyUtil.assertPointsEqual(simplifyLevel, true,
                        points);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        logger.trace("big route map source nodes line size = " + points.size());

        while (points.size() > 10000) {
            try {
                points = SimplifyUtil.assertPointsEqual(points);
            } catch (BusinessException e) {
                e.printStackTrace();
                break;
            }
        }

        int length = points.size();
        if (length > 2 && length < 10000) {
            OverlayOptions ooPolyline = new PolylineOptions().width(6)
                    .color(0xAAFF0000).points(points);
            baiduMap.addOverlay(ooPolyline);
        }

        if (!isUse) {
            zoomToSpan(points);
            this.isUse = true;
        }

    }

    /**
     * 自动缩放地图比例
     *
     * @param points
     */
    private void zoomToSpan(final List<LatLng> points) {
        if (null == points || points.isEmpty())
            return;

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        int dmWidth = dm.widthPixels;

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (final LatLng point : points) {
            builder.include(point);
        }

        int width = this.mapView.getWidth();
        int height = this.mapView.getHeight();

        if (width <= 0)
            width = dmWidth;

        if (height <= 0)
            height = dm.heightPixels - 100;

        this.baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(
                builder.build(), width, height));

        this.zoomLevel = this.baiduMap.getMapStatus().zoom;
    }

    private final class RouteNodeComparator implements Comparator<RouteNodeDTO> {

        @Override
        public int compare(RouteNodeDTO lhs, RouteNodeDTO rhs) {
            return (int) (lhs.getOrdinal() - rhs.getOrdinal());
        }

    }

}
