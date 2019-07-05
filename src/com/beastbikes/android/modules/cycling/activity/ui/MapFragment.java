
package com.beastbikes.android.modules.cycling.activity.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapConnectionCallBack;
import com.beastbikes.android.locale.googlemaputils.GoogleMapManager;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityState;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.PoiInfoDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteNodeDTO;
import com.beastbikes.android.modules.cycling.route.ui.RouteSelfActivity;
import com.beastbikes.android.modules.cycling.simplify.SimplifyUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.StringResource;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

@Alias("骑行页地图页")
@StringResource(R.string.activity_fragment_tab_map)
@LayoutResource(R.layout.activity_fragment_tab_map_fragment)
public class MapFragment extends SessionFragment implements BDLocationListener,
        OnClickListener, OnMapDrawFrameCallback, SensorEventListener,
        OnSharedPreferenceChangeListener, OnGetRoutePlanResultListener, OnMapReadyCallback, GoogleMapConnectionCallBack, LocationListener {

    private static final Logger logger = LoggerFactory.getLogger(MapFragment.class);

    // 是否显示路线
    private static final String SP_ROUTE_DISPLAY = "route_display";

    private static final int DEFAULT_ZOOM_LEVEL = 16;

//    private boolean isChineseVersion = true;

    private MapView mapView;

    @IdResource(R.id.activity_fragment_tab_map_fragment_button_location)
    private ImageView btnLocation;

    @IdResource(R.id.activity_fragment_tab_map_fragment_enter_squad)
    private ImageView enterSquad;

    @IdResource(R.id.activity_fragment_tab_map_fragment_show_squad)
    private ImageView showSquad;

    @IdResource(R.id.activity_fragment_tab_map_fragment_route_display)
    private ImageView routeDisplayIv;

    @IdResource(R.id.activity_fragment_tab_map_fragment_button_exit)
    private ImageView exit;

    @IdResource(R.id.maprl)
    private RelativeLayout mapRelativeLayout;

    @IdResource(R.id.mapViewFrameLayout)
    private FrameLayout mapViewFrameLayout;

    @IdResource(R.id.gps_ratingbar)
    private RatingBar gpsRatingbar;

    private final float[] gravity = new float[3];
    private final float[] magnetic = new float[3];

    private float azimuth = 0f;

    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private boolean isFirstTime = true;
    private BaiduMap baiduMap;
    private LocationClient client;
    private ActivityManager am;
    private Sensor aSensor;
    private Sensor mSensor;
    private SensorManager sm;

    // 规划路线
    private SharedPreferences sp;
    private RouteManager routeManager;
    // 搜索路线
    private RoutePlanSearch planSearch;

    private List<PoiInfoDTO> keyNodes = new ArrayList<PoiInfoDTO>();

    //    private boolean selected;
    private boolean isStartIcon;
    private int searchIndex;
    private String routeId;

    private List<List<LatLng>> nodesList = new ArrayList<List<LatLng>>();

    //谷歌
    private GoogleMap googleMap;
    private com.google.android.gms.maps.MapView googleMapView;
    private com.google.android.gms.maps.MapFragment mMapFragment;

    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleMapManager googleMapManager;
    private com.google.android.gms.maps.model.LatLng latlng;
    private boolean isFirstLocation = true;

    private LocationManager locationListener;
    private MyGpsStatusListener myGpsStatusListener;

    private OnMapActivtyFinishListener onMapActivtyFinishListener;

    private boolean isChineseTimeZone = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        isChineseTimeZone = LocaleManager.isChineseTimeZone();
        this.am = new ActivityManager(activity);
        this.sm = (SensorManager) activity
                .getSystemService(Context.SENSOR_SERVICE);
        this.aSensor = this.sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mSensor = this.sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.sm.registerListener(this, this.aSensor,
                SensorManager.SENSOR_DELAY_UI);
        this.sm.registerListener(this, this.mSensor,
                SensorManager.SENSOR_DELAY_UI);
        if (isChineseTimeZone) {
            this.client = new LocationClient(activity);
            this.client.registerLocationListener(this);
        }
        onMapActivtyFinishListener = (MapActivity) activity;
    }

    @Override
    public void onDetach() {
        this.sm.unregisterListener(this, this.aSensor);
        this.sm.unregisterListener(this, this.mSensor);
        if (isChineseTimeZone) {
            this.client.unRegisterLocationListener(this);
        }
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) super.onCreateView(inflater,
                container, savedInstanceState);
        isChineseTimeZone = LocaleManager.isChineseTimeZone();
        this.sp = getActivity().getSharedPreferences(
                getActivity().getPackageName(), 0);
        this.sp.registerOnSharedPreferenceChangeListener(this);

        this.routeManager = new RouteManager(getActivity());
        this.planSearch = RoutePlanSearch.newInstance();
        this.planSearch.setOnGetRoutePlanResultListener(this);
        this.routeDisplayIv.setOnClickListener(this);
        this.enterSquad.setOnClickListener(this);
        this.showSquad.setOnClickListener(this);
        this.exit.setOnClickListener(this);

        if (!this.sp.contains(SP_ROUTE_DISPLAY)) {
            this.routeDisplayIv.setVisibility(View.GONE);
        }

        if (isChineseTimeZone) {
            //初始化百度地图
            baiduMapInit(vg);
        } else {
            googleMapInit(vg);
        }
        return vg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationListener = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        myGpsStatusListener = new MyGpsStatusListener();
        locationListener.addGpsStatusListener(myGpsStatusListener);

        drawLine();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isChineseTimeZone) {
            baiduMapOpitionsInit();
        }
        this.btnLocation.setOnClickListener(this);

        this.showRouteLine();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_fragment_tab_map_fragment_button_location: {
                if (isChineseTimeZone) {
                    this.isFirstTime = true;
                    this.client.requestLocation();
                } else {
                    if (googleMap != null && latlng != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latlng)      // Sets the center of the map to Mountain View
                                .zoom(16)                   // Sets the zoom
                                .build();                   // Creates a CameraPosition from the builder
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
                break;
            }
            case R.id.activity_fragment_tab_map_fragment_route_display: {
                if (LocaleManager.isChineseTimeZone()) {
                    boolean display = this.sp.getBoolean(SP_ROUTE_DISPLAY, true);
                    this.sp.edit().putBoolean(SP_ROUTE_DISPLAY, !display).commit();
                }
                break;
            }
            case R.id.activity_fragment_tab_map_fragment_button_exit:
                SpeedxAnalytics.onEvent(getActivity(), "", "click_ridding_map_close");
                onMapActivtyFinishListener.finishMapActivity();
                break;
            default:
                break;
        }
    }

    // 百度地图 定义地图绘制每一帧时 OpenGL 绘制的回调接口
    @Override
    public void onMapDrawFrame(GL10 gl, MapStatus status) {
        final Projection projection = this.baiduMap.getProjection();
        if (null == projection)
            return;

        final LocalActivity la = this.am.getCurrentActivity();
        if (null == la)
            return;

        final String activityId = la.getId();
        if (TextUtils.isEmpty(activityId))
            return;

        try {
            final List<LocalActivitySample> samples = this.am
                    .getLocalActivitySamples(activityId);
            this.drawActivityRoute(gl, samples, status);
        } catch (BusinessException e) {
            logger.error("Query activity samples error", e);
        }
    }

    //百度地图定为回调
    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null || this.mapView == null)
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
            this.drawLine();
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (isChineseTimeZone) {
            if (null != key && RouteSelfActivity.SP_USE_ROUTE_ID.equals(key)) {
                this.routeId = this.sp.getString(key, "");
                if (TextUtils.isEmpty(routeId))
                    return;

                this.keyNodes.clear();
                this.nodesList.clear();
                this.routeDisplayIv.setVisibility(View.VISIBLE);
                this.getMyRouteById(routeId);
                this.baiduMap.clear();
                drawLine();
            }

            if (null != key && SP_ROUTE_DISPLAY.equals(key)) {
                boolean display = this.sp.getBoolean(SP_ROUTE_DISPLAY, true);
                if (display) {
                    this.routeDisplayIv
                            .setImageResource(R.drawable.map_fragment_route_display_icon);
                    this.routeId = this.sp.getString(
                            RouteSelfActivity.SP_USE_ROUTE_ID, "");
                    this.getMyRouteById(routeId);
                } else {
                    this.routeDisplayIv
                            .setImageResource(R.drawable.map_fragment_route_undisplay_icon);
                    this.baiduMap.clear();
                    drawLine();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onPause() {
        if (isChineseTimeZone) {
            if (mapView != null)
                this.mapView.onPause();
        } else {
            stopLocationUpdates();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        isChineseTimeZone = LocaleManager.isChineseTimeZone();
        if (isChineseTimeZone) {
            if (mapView != null)
                this.mapView.onResume();
        } else {
            if (googleMapManager != null) {
                if (googleMapManager.getmGoogleApiClient().isConnected()) {
                    if (!mRequestingLocationUpdates) {
                        startLocationUpdates();
                    }
                } else {
                    googleMapManager.googleApiClientConnect();
                }
            }
        }
        super.onResume();
        this.drawLine();
    }

    @Override
    public void onStop() {
        if (!isChineseTimeZone && googleMapManager != null && googleMapManager.getmGoogleApiClient().isConnected()) {
            googleMapManager.googleApiClientDisconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {

        if (null != sp)
            this.sp.unregisterOnSharedPreferenceChangeListener(this);
        if (isChineseTimeZone) {
            if (client != null && planSearch != null && baiduMap != null) {
                this.client.stop();
                this.planSearch.destroy();
                this.baiduMap.setMyLocationEnabled(false);
            }
            if (mapView != null)
                this.mapView.onDestroy();
        }
        locationListener.removeGpsStatusListener(myGpsStatusListener);
        super.onDestroy();
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toasts.show(getActivity(),
                    R.string.route_map_make_activity_select_err);
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {

        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (null == baiduMap)
                return;

            if (null == result || null == result.getRouteLines()
                    || null == result.getRouteLines().get(0))
                return;

            List<LatLng> wayPoints = new ArrayList<>();

            for (BikingRouteLine.BikingStep ws : result.getRouteLines().get(0).getAllStep()) {
                wayPoints.addAll(ws.getWayPoints());
            }

            this.nodesList.add(searchIndex, wayPoints);
            this.drawRouteLine();

            searchIndex++;
            if (searchIndex < this.keyNodes.size() && searchIndex > 0) {
                if ((searchIndex + 1) >= this.keyNodes.size()) {
                    return;
                }

                PoiInfoDTO startInfo = keyNodes.get(searchIndex);
                PlanNode nodeStart = PlanNode.withLocation(new LatLng(startInfo
                        .getLatitude(), startInfo.getLongitude()));
                PoiInfoDTO endInfo = keyNodes.get(searchIndex + 1);
                PlanNode nodeEnd = PlanNode.withLocation(new LatLng(endInfo
                        .getLatitude(), endInfo.getLongitude()));
//                this.planSearch.walkingSearch(new WalkingRoutePlanOption()
//                        .from(nodeStart).to(nodeEnd));
                this.planSearch.bikingSearch(new BikingRoutePlanOption().from(
                        nodeStart).to(nodeEnd));
            }

        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {

    }

    /**
     * 根据RouteId获取路线
     *
     * @param routeId
     */
    private void getMyRouteById(final String routeId) {
        if (null == routeManager)
            return;

        if (TextUtils.isEmpty(routeId))
            return;

        if (!this.keyNodes.isEmpty() && !this.nodesList.isEmpty()) {
            this.drawRouteLine();
            return;
        }

        if (this.sp.contains(routeId)) {
            this.drawRouteLineByRouteId(routeId);
            return;
        }

        AsyncTaskQueue asyncTaskQueue = this.getAsyncTaskQueue();
        if (null == asyncTaskQueue)
            return;

        asyncTaskQueue.add(new AsyncTask<String, Void, RouteDTO>() {

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
                if (null == result || null == getActivity())
                    return;

                List<RouteNodeDTO> nodes = result.getNodes();
                if (null == nodes || nodes.isEmpty())
                    return;

                keyNodes = new ArrayList<PoiInfoDTO>();
                for (RouteNodeDTO rnd : nodes) {
                    if (rnd.getKeyNode() >= 0) {
                        keyNodes.add(new PoiInfoDTO(rnd));
                    }
                }

                searchWalkingRoute();

            }

        }, routeId);
    }

    /**
     * 规划步行路线
     */
    private void searchWalkingRoute() {
        if (null == keyNodes || keyNodes.isEmpty())
            return;

        this.baiduMap.clear();
        this.nodesList.clear();
        this.searchIndex = 0;

        if (keyNodes.size() == 1) {
            LatLng ll = new LatLng(keyNodes.get(0).getLatitude(), keyNodes.get(
                    0).getLongitude());

            TextView tv = (TextView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.route_map_make_plan_ordinal, null);
            tv.setText("1");
            tv.setBackgroundResource(R.drawable.route_map_make_start_icon);
            tv.setTextColor(getResources().getColor(
                    R.color.route_map_start_color));

            OverlayOptions options = new MarkerOptions().position(ll).icon(
                    BitmapDescriptorFactory.fromView(tv));
            this.baiduMap.addOverlay(options);
            return;
        }

        if (keyNodes.isEmpty() || keyNodes.size() < 1)
            return;

        PoiInfoDTO startInfo = keyNodes.get(0);
        PlanNode nodeStart = PlanNode.withLocation(new LatLng(startInfo
                .getLatitude(), startInfo.getLongitude()));
        PoiInfoDTO endInfo = keyNodes.get(1);
        PlanNode nodeEnd = PlanNode.withLocation(new LatLng(endInfo
                .getLatitude(), endInfo.getLongitude()));
        this.planSearch.bikingSearch(new BikingRoutePlanOption().from(
                nodeStart).to(nodeEnd));

    }

    /**
     * 骑行轨迹画线
     */
    private void drawLine() {
        logger.info("drawLine");
        if (null == am) {
            return;
        }

        final LocalActivity la = this.am.getCurrentActivity();
        if (null == la || la.getState() == ActivityState.STATE_COMPLETE) {
            this.isStartIcon = false;
            return;
        }

        final String activityId = la.getId();
        if (TextUtils.isEmpty(activityId)) {
            return;
        }

        AsyncTaskQueue asyncTaskQueue = this.getAsyncTaskQueue();
        if (null == asyncTaskQueue) {
            return;
        }
        logger.info("mapfragment drawline");
        if (isChineseTimeZone) {
            baidumapTask(asyncTaskQueue, activityId);
        } else {
            googleTask(asyncTaskQueue, activityId);
        }
    }

    private void baidumapTask(AsyncTaskQueue asyncTaskQueue, String activityId) {
        asyncTaskQueue.add(new AsyncTask<String, Void, List<LatLng>>() {

            @Override
            protected List<LatLng> doInBackground(String... params) {
                try {
                    final String activityId = params[0];
                    final List<LocalActivitySample> lases = am
                            .getLocalActivitySamples(activityId);
                    if (null == lases || lases.isEmpty())
                        return null;

                    final List<LatLng> lds = new ArrayList<LatLng>();
                    for (final LocalActivitySample las : lases) {
                        final double lat = Double.parseDouble(las
                                .getLatitude0());
                        final double lng = Double.parseDouble(las
                                .getLongitude0());
                        if (lat == 0 || lng == 0 || lat == 4.9E-324
                                || lng == 4.9E-324)
                            continue;

                        lds.add(new LatLng(lat, lng));
                    }

                    LatLng latLng = lds.get(lds.size() - 1);

                    if (isFirstTime) {
                        if (null == baiduMap) {
                            baiduMapOpitionsInit();
                            if (null == baiduMap) {
                                return null;
                            }
                        }
                        baiduMap.animateMapStatus(MapStatusUpdateFactory
                                .newLatLng(latLng));
                    }
                    isFirstTime = false;

                    return lds;
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<LatLng> points) {
                System.gc();

                if (null == getActivity())
                    return;

                try {
                    if (null == points || points.isEmpty() || null == baiduMap)
                        return;

                    // 在地图上添加起点Marker，并显示
                    final LatLng origin = points.get(0);

                    if (null == origin)
                        return;

                    if (!isStartIcon) {
                        final BitmapDescriptor originMarker = BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_activity_detail_start);
                        final OverlayOptions originOptions = new MarkerOptions()
                                .position(origin).icon(originMarker);
                        baiduMap.addOverlay(originOptions);
                    }

                    isStartIcon = true;

                    // 添加中间过渡点
                    logger.info("Activity source sample size = "
                            + points.size());
                    float simplifyLevel = 0.0001f;
                    while (points.size() >= 500) {
                        try {
                            points = SimplifyUtil.assertPointsEqual(
                                    simplifyLevel, points);
                            simplifyLevel += 0.0003f;
                        } catch (BusinessException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    logger.info("Activity new sample size = " + points.size());

                    final int n = points.size();
                    if (n < 2 || n > 10000)
                        return;

                    final OverlayOptions opts = new PolylineOptions().width(6)
                            .color(0xAAFF0000).points(points);
                    baiduMap.addOverlay(opts);
                } catch (NullPointerException e) {
                    logger.error("Activity draw line ", e);
                }
            }

        }, activityId);
    }

    private void googleTask(AsyncTaskQueue asyncTaskQueue, String activityId) {
        logger.error("googleTask");
        asyncTaskQueue.add(new AsyncTask<String, Void, List<com.google.android.gms.maps.model.LatLng>>() {

            @Override
            protected List<com.google.android.gms.maps.model.LatLng> doInBackground(String... params) {
                try {
                    final String activityId = params[0];
                    final List<LocalActivitySample> lases = am
                            .getLocalActivitySamples(activityId);
                    if (null == lases || lases.isEmpty())
                        return null;

                    final List<com.google.android.gms.maps.model.LatLng> lds = new ArrayList<com.google.android.gms.maps.model.LatLng>();
                    for (final LocalActivitySample las : lases) {
                        final double lat = Double.parseDouble(las
                                .getLatitude1());
                        final double lng = Double.parseDouble(las
                                .getLongitude1());
                        if (lat == 0 || lng == 0 || lat == 4.9E-324
                                || lng == 4.9E-324)
                            continue;

                        lds.add(new com.google.android.gms.maps.model.LatLng(lat, lng));
                    }

                    return lds;
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<com.google.android.gms.maps.model.LatLng> points) {
                System.gc();

                if (null == getActivity()) {
                    logger.error("getActivity is null");
                    return;
                }

                try {
                    if (null == points || points.isEmpty() || null == googleMap) {
                        return;
                    }


                    // 在地图上添加起点Marker，并显示
                    final com.google.android.gms.maps.model.LatLng origin = points.get(0);

                    if (null == origin)
                        return;

                    if (!isStartIcon) {

                        googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                                .position(origin)
                                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_activity_detail_start)));

                    }

                    isStartIcon = true;

                    // 添加中间过渡点
                    logger.info("Google Activity source sample size = "
                            + points.size());
                    float simplifyLevel = 0.0001f;
                    while (points.size() >= 500) {
                        try {
                            points = SimplifyUtil.assertPointsEqualforGoogle(
                                    simplifyLevel, points);
                            simplifyLevel += 0.0003f;
                        } catch (BusinessException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    logger.info("Google Activity new sample size = " + points.size());

                    final int n = points.size();
                    if (n < 2 || n > 10000)
                        return;


                    final com.google.android.gms.maps.model.PolylineOptions polylineOptions = new com.google.android.gms.maps.model.PolylineOptions().width(6)
                            .color(0xAAFF0000).addAll(points);
                    googleMap.addPolyline(polylineOptions);
                } catch (NullPointerException e) {
                    logger.error("Google Activity draw line ", e);
                }
            }

        }, activityId);
    }

    private void drawActivityRoute(final GL10 gl,
                                   final List<LocalActivitySample> samples, final MapStatus status) {
        if (null == samples || samples.isEmpty())
            return;

        // filter out samples without location
        for (final Iterator<LocalActivitySample> i = samples.iterator(); i
                .hasNext(); ) {
            final LocalActivitySample las = i.next();

            if (0f == Double.parseDouble(las.getLatitude0())
                    && 0f == Double.parseDouble(las.getLongitude0())) {
                i.remove();
            }
        }

        final int n = samples.size();
        if (n < 2) {
            return; // The quantity of samples isn't enough to draw a line
        }

        int i = 0;
        final float[] vertexs = new float[3 * n];
        final PointF[] points = new PointF[n];
        final Projection projection = this.baiduMap.getProjection();

        for (final LocalActivitySample las : samples) {
            final int j = i * 3;
            final LatLng ll = new LatLng(
                    Double.parseDouble(las.getLatitude0()),
                    Double.parseDouble(las.getLongitude0()));
            final PointF p = projection.toOpenGLLocation(ll, status);
            points[i] = p;
            vertexs[j] = p.x;
            vertexs[j + 1] = p.y;
            vertexs[j + 2] = 0;
            i++;
        }

        final FloatBuffer fb = allocFloatBuffer(vertexs);
        drawPolyline(gl, Color.GREEN, fb, 20, 3, status);
    }

    private FloatBuffer allocFloatBuffer(float[] fs) {
        ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(fs);
        fb.position(0);
        return fb;
    }

    /**
     * Draw a polyline with the specified vertex
     *
     * @param gl             OpenGL interface
     * @param color          The color of stroke
     * @param vertexPointers The vertex data
     * @param lineWidth      The width of stroke
     * @param pointCount     The quantity of point
     * @param status         The status of map
     */
    private void drawPolyline(GL10 gl, int color, FloatBuffer vertexPointers,
                              float lineWidth, int pointCount, MapStatus status) {
        gl.glEnable(GL10.GL_BLEND);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        float colorA = Color.alpha(color) / 255f;
        float colorR = Color.red(color) / 255f;
        float colorG = Color.green(color) / 255f;
        float colorB = Color.blue(color) / 255f;

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexPointers);
        gl.glColor4f(colorR, colorG, colorB, colorA);
        gl.glLineWidth(lineWidth);
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointCount);

        gl.glDisable(GL10.GL_BLEND);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void drawRouteLineByRouteId(String routeId) {
        String lines = this.sp.getString(routeId, null);
        if (TextUtils.isEmpty(lines))
            return;

        List<LatLng> points = new ArrayList<LatLng>();
        String[] value = lines.split(";");
        for (int i = 0; i < value.length; i++) {
            String v = value[i];
            String[] s = v.split(",");
            points.add(new LatLng(Double.valueOf(s[0]), Double.valueOf(s[1])));
        }

        if (null == points || points.size() < 2)
            return;

        this.baiduMap.clear();
        TextView tv1 = (TextView) LayoutInflater.from(getActivity()).inflate(
                R.layout.route_map_make_plan_ordinal, null);
        tv1.setText(String.valueOf(0));
        tv1.setBackgroundResource(R.drawable.route_map_line_start_icon);
        tv1.setTextColor(getResources().getColor(android.R.color.transparent));

        final BitmapDescriptor marker = BitmapDescriptorFactory.fromView(tv1);
        final OverlayOptions oo = new MarkerOptions().position(points.get(0))
                .icon(marker);
        baiduMap.addOverlay(oo);

        TextView tv2 = (TextView) LayoutInflater.from(getActivity()).inflate(
                R.layout.route_map_make_plan_ordinal, null);
        tv2.setText(String.valueOf(0));
        tv2.setBackgroundResource(R.drawable.route_map_line_end_icon);
        tv2.setTextColor(getResources().getColor(android.R.color.transparent));

        final BitmapDescriptor end = BitmapDescriptorFactory.fromView(tv2);
        final OverlayOptions endOo = new MarkerOptions()
                .position(points.get(points.size() - 1)).icon(end);
        baiduMap.addOverlay(endOo);

        final OverlayOptions opts = new PolylineOptions().width(8)
                .color(0xFF5E80E6).points(points);
        baiduMap.addOverlay(opts);
    }

    /**
     * 手动规划线路
     */
    private void drawRouteLine() {
        if (null == this.keyNodes || this.keyNodes.size() < 2)
            return;

        this.baiduMap.clear();

        for (int i = 0; i < this.keyNodes.size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.route_map_make_plan_ordinal, null);
            tv.setText(String.valueOf(i));
            if (i == 0) {
                tv.setBackgroundResource(R.drawable.route_map_line_start_icon);
                tv.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            } else if (i == this.keyNodes.size() - 1) {
                tv.setBackgroundResource(R.drawable.route_map_line_end_icon);
                tv.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            } else {
                tv.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            }

            final BitmapDescriptor marker = BitmapDescriptorFactory
                    .fromView(tv);
            final LatLng latlng = new LatLng(
                    this.keyNodes.get(i).getLatitude(), this.keyNodes.get(i)
                    .getLongitude());
            final OverlayOptions oo = new MarkerOptions().position(latlng)
                    .icon(marker);
            baiduMap.addOverlay(oo);
        }

        StringBuilder sb = new StringBuilder();
        for (List<LatLng> value : this.nodesList) {
            if (null == value)
                continue;

            float simplifyLevel = 0.0001f;
            while (value.size() >= 1000) {
                try {
                    value = SimplifyUtil
                            .assertPointsEqual(simplifyLevel, value);
                    simplifyLevel += 0.0003f;
                } catch (BusinessException e) {
                    e.printStackTrace();
                    break;
                }
            }

            final OverlayOptions opts = new PolylineOptions().width(8)
                    .color(0xFF5E80E6).points(value);
            baiduMap.addOverlay(opts);

            Iterator<LatLng> iterator = value.iterator();
            while (iterator.hasNext()) {
                LatLng ll = iterator.next();
                sb.append(String.valueOf(ll.latitude) + ","
                        + String.valueOf(ll.longitude) + ";");
            }

        }

        this.sp.edit().putString(this.routeId, sb.toString()).commit();

    }

    /**
     * 画线  路书
     */
    private void showRouteLine() {
        if (null == sp)
            return;

        boolean display = this.sp.getBoolean(SP_ROUTE_DISPLAY, true);

        logger.trace("MapFragment showRouteLine");
        if (display) {
            this.routeDisplayIv
                    .setImageResource(R.drawable.map_fragment_route_display_icon);
            if (this.sp.contains(RouteSelfActivity.SP_USE_ROUTE_ID)) {
                this.routeId = this.sp.getString(
                        RouteSelfActivity.SP_USE_ROUTE_ID, "");
                if (!TextUtils.isEmpty(routeId)) {
                    this.getMyRouteById(routeId);
                    this.routeDisplayIv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            this.routeDisplayIv
                    .setImageResource(R.drawable.map_fragment_route_undisplay_icon);
        }
    }

    //百度地图初始化
    private void baiduMapInit(ViewGroup vg) {
        try {
            final BaiduMapOptions bmo = new BaiduMapOptions();
            bmo.scaleControlEnabled(false);
            bmo.zoomControlsEnabled(false);
            bmo.zoomGesturesEnabled(true);

            this.mapView = new MapView(getActivity(), bmo);
            final View logo = this.mapView.getChildAt(1);
            logo.setVisibility(View.GONE);
            final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            vg.addView(this.mapView, 0, lp);
        } catch (Exception e) {
            logger.error("baiduMapInit error", e);
        }
    }


    private void googleMapInit(ViewGroup vg) {
        if (!GoogleMapManager.isDeviceSupportGooglePlayService(this.getActivity())) {
            initnoGoogleLayout();
            return;
        }
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false).zoomControlsEnabled(false).zoomGesturesEnabled(true);
        mMapFragment = com.google.android.gms.maps.MapFragment.newInstance(options);

        android.app.FragmentTransaction fragmentTransaction = this.getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapViewFrameLayout, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

    }

    //百度地图设置初始化
    private void baiduMapOpitionsInit() {
        if (null == this.mapView) {
            return;
        }
        final LocationClientOption options = new LocationClientOption();
        options.setOpenGps(true);
        options.setPriority(LocationClientOption.GpsFirst);
        options.setCoorType("bd09ll");
        options.setScanSpan(LocationClientOption.MIN_SCAN_SPAN * 5);
        options.setAddrType("all");

        this.baiduMap = this.mapView.getMap();
        UiSettings uiSettings = this.baiduMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setOverlookingGesturesEnabled(false);
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                LocationMode.NORMAL, true, null));
        this.baiduMap.setMyLocationEnabled(true);
        //配置定位图层显示方式
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));
        // this.baiduMap.setOnMapDrawFrameCallback(this);

        this.client.setLocOption(options);
        this.client.start();
        this.client.requestLocation();
    }

    private void googleMapOpitionsInit() {
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapFragment.this.googleMap = googleMap;
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(LocationClientOption.MIN_SCAN_SPAN * 5);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }
        });

    }

    //googlemap加载完回调
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapOpitionsInit();
        googleMapManager = new GoogleMapManager();
        googleMapManager.buildGoogleApiClient(this.getActivity(), this);
        googleMapManager.googleApiClientConnect();
    }

    @Override
    public void onSuccessed(Location mLastLocation, Bundle bundle) {
        if (!mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onFail(ConnectionResult connectionResult) {

    }

    @Override
    public void onSuspended(int i) {

    }

    protected void startLocationUpdates() {
        if (googleMapManager != null && mLocationRequest != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleMapManager.getmGoogleApiClient(), mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (googleMapManager != null && googleMapManager.getmGoogleApiClient().isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleMapManager.getmGoogleApiClient(), this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            logger.info("google onLocationChanged null");
            return;
        }
        latlng = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
        if (isFirstLocation) {
            isFirstLocation = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        this.drawLine();
    }

    private void initnoGoogleLayout() {//不支持googleplayservice
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(this.getActivity()).inflate(R.layout.layout_nogoogleplayservice1, null);
        mapRelativeLayout.addView(relativeLayout, lp);
    }

    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号

    private class MyGpsStatusListener implements GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {

            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    logger.error("第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    GpsStatus status = locationListener.getGpsStatus(null); // 取当前状态
                    String satelliteInfo = updateGpsStatus(event, status);
//                    logger.error(satelliteInfo);
//                    logger.error("最多多少卫星:" + status.getMaxSatellites());
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    logger.error("定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    logger.error("定位结束");
                    break;
            }
        }
    }

    private String updateGpsStatus(int event, GpsStatus status) {
        StringBuilder sb2 = new StringBuilder("");
        if (status == null) {
//            sb2.append("搜索到卫星个数：" + 0);
            gpsRatingbar.setRating(0);
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
//            int count = 0;
            int gpsStatus = 0;
            while (it.hasNext()) {
                GpsSatellite s = it.next();
                numSatelliteList.add(s);

                if (s.getSnr() > 0.0) {
                    //信号信噪比不为0计入GPS信号
//                    logger.error("信噪比:" + s.getSnr());
                    gpsStatus += s.getSnr();
                }


//                    logger.error("信噪比:" + s.getSnr());
//                    count++;
            }
//            logger.error("==================总信噪比:" + gpsStatus);
            if (gpsStatus > 240) {
                gpsRatingbar.setRating(5);
            } else if (gpsStatus > 180) {
                gpsRatingbar.setRating(4);
            } else if (gpsStatus > 120) {
                gpsRatingbar.setRating(3);
            } else if (gpsStatus > 60) {
                gpsRatingbar.setRating(2);
            } else if (gpsStatus > 0) {
                gpsRatingbar.setRating(1);
            } else {
                gpsRatingbar.setRating(0);
            }
            sb2.append("搜索到卫星个数：" + numSatelliteList.size());
        }

        return sb2.toString();
    }

    public interface OnMapActivtyFinishListener {
        void finishMapActivity();
    }
}
