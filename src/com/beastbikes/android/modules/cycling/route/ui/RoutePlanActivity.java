package com.beastbikes.android.modules.cycling.route.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
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
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
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
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.PoiInfoDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteNodeDTO;
import com.beastbikes.android.modules.cycling.route.ui.widget.DragSortController;
import com.beastbikes.android.modules.cycling.route.ui.widget.DragSortListView;
import com.beastbikes.android.modules.cycling.simplify.SimplifyUtil;
import com.beastbikes.android.modules.user.ui.ProgressDialog;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@LayoutResource(R.layout.route_map_make_activity)
public class RoutePlanActivity extends SessionFragmentActivity implements
        BDLocationListener, OnClickListener, OnGetRoutePlanResultListener,
        OnGetGeoCoderResultListener, OnMapStatusChangeListener {

    public static final String EXTRA_POIINFO = "poiinfo";
    public static final String EXTRA_EDIT = "show_list";
    private static final Logger logger = LoggerFactory
            .getLogger(RoutePlanActivity.class);

    @IdResource(R.id.route_map_make_activity_view)
    private MapView mapView;

    @IdResource(R.id.route_map_make_back)
    private ImageView backIv;

    @IdResource(R.id.route_map_make_point_list_title)
    private TextView listTitle;

    @IdResource(R.id.route_map_make_search)
    private ViewGroup searchView;

    @IdResource(R.id.route_map_make_point_list)
    private ViewGroup listGrp;

    @IdResource(R.id.route_map_make_activity_map_button_location)
    private ImageView btnLocation;

    @IdResource(R.id.route_map_make_activity_map_button_zoom_out)
    private ImageView btnZoonOut;

    @IdResource(R.id.route_map_make_activity_map_button_zoom_in)
    private ImageView btnZoomIn;

    @IdResource(R.id.route_map_make_point_list_view)
    private DragSortListView listView;

    @IdResource(R.id.route_map_make_distance_view)
    private ViewGroup distanceView;

    @IdResource(R.id.route_map_make_distance)
    private TextView distanceTv;

    @IdResource(R.id.route_map_make_activity_distance_unit)
    private TextView distanceNuitTv;

    @IdResource(R.id.route_map_make_select_point_view)
    private ViewGroup selectPointView;

    @IdResource(R.id.route_map_make_select_start_point)
    private Button selectPointBtn;

    @IdResource(R.id.route_map_make_save_points)
    private TextView saveLineTv;

    @IdResource(R.id.route_map_make_point_list_edit)
    private TextView editTv;

    @IdResource(R.id.route_map_make_activity_elevation)
    private ImageView btnElevation;

    @IdResource(R.id.route_map_make_upload)
    private RelativeLayout loading;

    private LocationClient client;
    private BaiduMap baiduMap;

    private boolean first = true;
    private float zoomLevel = 16;

    // 搜索路线
    private RoutePlanSearch planSearch;

    private GeoCoder geoCoder;

    // 路线节点源数据
    private List<PoiInfoDTO> sources = new ArrayList<PoiInfoDTO>();
    private List<PoiInfoDTO> poiInfoList = new ArrayList<PoiInfoDTO>();
    private PoiInfoAdapter adapter;

    private PoiInfoDTO currentPoiInfo;
    private boolean mapChanged;

    private RouteManager routeManager;
    // 规划路线点
    List<RouteNodeDTO> nodes = new ArrayList<RouteNodeDTO>();
    private double totalDistance = 0;

    private int searchIndex = 0;
    private boolean isSave;
    private boolean deleteOrSort;

    private boolean isEdit = false;

    private LatLng currentLl;

    private TranslateAnimation animation;
    private AlphaAnimation alpAm;

    private String routeMapPath;
    private ProgressDialog pd;
    private boolean changed;
    private String routeId;
    private SharedPreferences sp;

    // 存放每段路线的坐标点
    private List<List<LatLng>> nodesList = new ArrayList<List<LatLng>>();

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                Object item = adapter.getItem(from);
                poiInfoList.remove(item);
                poiInfoList.add(to, (PoiInfoDTO) item);
                deleteOrSort = true;
                searchIndex = 0;
                totalDistance = 0;
                nodesList.clear();
                adapter.notifyDataSetChanged();
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            poiInfoList.remove(adapter.getItem(which));
            deleteOrSort = true;
            searchIndex = 0;
            totalDistance = 0;
            nodesList.clear();
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        this.backIv.setOnClickListener(this);
        this.searchView.setOnClickListener(this);
        this.selectPointBtn.setOnClickListener(this);
        this.saveLineTv.setOnClickListener(this);

        this.routeManager = new RouteManager(this);
        this.sp = getSharedPreferences(getPackageName(), 0);

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
        // options.setScanSpan(LocationClientOption.MIN_SCAN_SPAN * 5);
        options.setAddrType("all");

        this.baiduMap = this.mapView.getMap();

        BitmapDescriptor marker = BitmapDescriptorFactory
                .fromResource(R.drawable.route_map_make_location);

        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                LocationMode.FOLLOWING, true, marker));
        this.baiduMap.setMyLocationEnabled(true);
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));
        this.baiduMap.setOnMapStatusChangeListener(this);
        this.baiduMap.getUiSettings().setCompassEnabled(false);

        this.btnLocation.setOnClickListener(this);
        this.btnZoomIn.setOnClickListener(this);
        this.btnZoonOut.setOnClickListener(this);
        this.editTv.setOnClickListener(this);
        this.btnElevation.setOnClickListener(this);

        this.client.setLocOption(options);

        // 路线规划
        this.planSearch = RoutePlanSearch.newInstance();
        this.planSearch.setOnGetRoutePlanResultListener(this);

        // 地理编码
        this.geoCoder = GeoCoder.newInstance();
        this.geoCoder.setOnGetGeoCodeResultListener(this);

        this.adapter = new PoiInfoAdapter(poiInfoList);
        this.pd = new ProgressDialog(RoutePlanActivity.this);

        ViewGroup addPoint = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.route_map_make_list_footer, null)
                .findViewById(R.id.route_map_make_add_point);
        addPoint.setOnClickListener(this);

        this.listView.addFooterView(addPoint, null, false);
        this.listView.setAdapter(this.adapter);

        DragSortController controller = new DragSortController(listView);
        controller.setDragHandleId(R.id.route_map_make_item_drag);
        // controller.setFlingHandleId(R.id.route_map_make_item_name);
        controller.setClickRemoveId(R.id.route_map_make_item_delete);
        controller.setRemoveEnabled(true);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setRemoveMode(DragSortController.CLICK_REMOVE);

        this.listView.setFloatViewManager(controller);
        this.listView.setOnTouchListener(controller);
        this.listView.setDragEnabled(true);
        this.listView.setRemoveListener(onRemove);
        this.listView.setDropListener(onDrop);

        this.alpAm = new AlphaAnimation(0f, 1f);
        this.alpAm.setDuration(500);

        this.animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -0.5f);
        this.animation.setRepeatCount(1);
        this.animation.setDuration(300);
        this.animation.setRepeatMode(Animation.REVERSE);
        this.animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                selectPointBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                selectPointBtn.setVisibility(View.VISIBLE);
                selectPointBtn.startAnimation(alpAm);
            }
        });

        Intent intent = getIntent();
        if (null == intent)
            return;

        this.routeId = intent.getStringExtra(RouteSelfActivity.EXTRA_ROUTE_ID);
        boolean isShow = intent.getBooleanExtra(EXTRA_EDIT, true);
        if (!isShow) {
            listGrp.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            selectPointView.setVisibility(View.GONE);
            this.baiduMap.setMyLocationEnabled(false);
        }

        if (!TextUtils.isEmpty(routeId)) {
            this.getMyRouteById(routeId);
        } else {
            this.client.start();
            this.client.requestLocation();
        }

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
        if (null != this.pd)
            pd = null;

        this.baiduMap.clear();
        this.planSearch.destroy();
        this.baiduMap.setMyLocationEnabled(false);
        this.client.unRegisterLocationListener(this);
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        this.planSearch.setOnGetRoutePlanResultListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_map_make_back: {// 返回
                this.isSave();
                break;
            }
            case R.id.route_map_make_search: {// 搜索
                RouteMapSearchGeoActivity.isLandscape = true;
                Intent intent = new Intent(this, RouteMapSearchGeoActivity.class);
                startActivityForResult(intent,
                        RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE);
                break;
            }
            case R.id.route_map_make_activity_elevation: {
                SpeedxAnalytics.onEvent(this, "查看路线制作海拔趋势图", null);
                final StringBuilder latLngSb = new StringBuilder();

                if (!this.poiInfoList.isEmpty()) {
                    Iterator<PoiInfoDTO> it = this.poiInfoList.iterator();
                    while (it.hasNext()) {
                        PoiInfoDTO dto = it.next();
                        latLngSb.append(dto.getLatitude()).append(",")
                                .append(dto.getLongitude());
                        if (it.hasNext()) {
                            latLngSb.append('|');
                        }
                    }
                }

                final Intent intent = new Intent(this, RouteElevationActivity.class);
                intent.putExtra(RouteElevationActivity.EXTRA_NODES,
                        latLngSb.toString());
                intent.putExtra(RouteElevationActivity.EXTRA_DISTANCE,
                        this.totalDistance / 1000);
                this.startActivity(intent);
                break;
            }
            case R.id.route_map_make_select_start_point: {// 选择节点
                final LatLng ll = this.baiduMap.getMapStatus().target;
                this.currentLl = ll;
                if (this.mapChanged) {
                    geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
                } else if (null != currentPoiInfo) {
                    this.currentPoiInfo.setLatitude(this.currentLl.latitude);
                    this.currentPoiInfo.setLongitude(this.currentLl.longitude);
                    this.poiInfoList.add(currentPoiInfo);
                    this.adapter.notifyDataSetChanged();
                }

                break;
            }
            case R.id.route_map_make_add_point: {// 添加坐标点
                Intent intent = new Intent(this, RouteMapSearchGeoActivity.class);
                startActivityForResult(intent,
                        RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE);
                break;
            }
            case R.id.route_map_make_activity_map_button_location: {// 定位
                this.first = true;
                if (!this.client.isStarted()) {
                    this.client.start();
                }
                this.client.requestLocation();
                this.baiduMap.setMyLocationEnabled(true);
                break;
            }
            case R.id.route_map_make_activity_map_button_zoom_in: {// 放大
                final float min = this.baiduMap.getMinZoomLevel();
                final float level = Math.max(this.zoomLevel - 1, min);
                final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
                this.baiduMap.animateMapStatus(u);
                this.zoomLevel = level;
                break;
            }
            case R.id.route_map_make_activity_map_button_zoom_out: {// 缩小
                final float max = this.baiduMap.getMaxZoomLevel();
                final float level = Math.min(this.zoomLevel + 1, max);
                final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
                this.baiduMap.animateMapStatus(u);
                this.zoomLevel = level;
                break;
            }
            case R.id.route_map_make_save_points: {// 保存路线
                this.isSave = true;
                this.saveLineTv.setClickable(false);
                this.drawLine();
                SpeedxAnalytics.onEvent(this,
                        getString(R.string.route_map_activity_event_save_route), "click_my_page_my_road_book_save");
                break;
            }
            case R.id.route_map_make_point_list_edit: {// 修改路线
                this.isEdit = !isEdit;
                if (this.adapter.getCount() > 0) {
                    ((PoiInfoDTO) this.adapter.getItem(0)).setEdit(true);
                }
                this.adapter.notifyDataSetChanged();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        this.selectPointView.startAnimation(this.animation);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        this.mapChanged = true;
        this.selectPointBtn.setVisibility(View.GONE);
        this.selectPointView.clearAnimation();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (null == location)
            return;

        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        final LatLng latlng = new LatLng(latitude, longitude);
        final MyLocationData data = new MyLocationData.Builder()
                .accuracy(location.getRadius()).latitude(latitude)
                .longitude(longitude).build();
        this.baiduMap.setMyLocationData(data);
        this.zoomLevel = this.baiduMap.getMapStatus().zoom;
        this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));

        this.client.stop();

        if (this.first) {
            this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newLatLng(latlng));
        }

        this.first = false;
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        final LatLng ll = this.baiduMap.getMapStatus().target;
        if (null == ll) {
            Toasts.show(this, R.string.route_map_make_activity_select_err);
            return;
        }

        this.currentLl = ll;
        String city = "";
        this.currentPoiInfo = new PoiInfoDTO();
        if (null != result && result.error == SearchResult.ERRORNO.NO_ERROR) {
            city = result.getAddressDetail().city;
            List<PoiInfo> list = result.getPoiList();
            if (null != list && !list.isEmpty()) {
                PoiInfo info = result.getPoiList().get(0);
                if (null != info) {
                    this.currentPoiInfo.setName(info.name);
                    this.currentPoiInfo.setAddress(info.address);
                }
            }
        }

        if (TextUtils.isEmpty(this.currentPoiInfo.getName())) {
            city = String.valueOf(this.currentLl.latitude) + ", "
                    + String.valueOf(this.currentLl.longitude);
            this.currentPoiInfo.setName(city);
        }

        this.currentPoiInfo.setLatitude(this.currentLl.latitude);
        this.currentPoiInfo.setLongitude(this.currentLl.longitude);
        this.currentPoiInfo.setCity(city);

        this.poiInfoList.add(this.currentPoiInfo);
        this.adapter.notifyDataSetChanged();
        this.currentPoiInfo = null;
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
            Toasts.show(this, R.string.route_map_make_activity_plain_err_msg);
            if (null != this.pd)
                this.pd.dismiss();
            this.poiInfoList.remove(this.poiInfoList.size() - 1);
            this.adapter.notifyDataSetChanged();
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            Toasts.show(this, R.string.route_map_make_activity_plain_err_msg);
            if (null != this.pd)
                this.pd.dismiss();
            this.poiInfoList.remove(this.poiInfoList.size() - 1);
            this.adapter.notifyDataSetChanged();
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (null == result || null == result.getRouteLines()
                    || null == result.getRouteLines().get(0))
                return;

            this.totalDistance += result.getRouteLines().get(0).getDistance();

            List<LatLng> wayPoints = new ArrayList<LatLng>();

            for (BikingRouteLine.BikingStep ws : result.getRouteLines().get(0).getAllStep()) {
                wayPoints.addAll(ws.getWayPoints());
            }

            this.nodesList.add(searchIndex, wayPoints);
            this.drawLine();

            searchIndex++;
            if (searchIndex < this.poiInfoList.size() && searchIndex > 0
                    && deleteOrSort) {
                if ((searchIndex + 1) >= this.poiInfoList.size()) {
                    if (null != this.pd)
                        this.pd.dismiss();
                    return;
                }

                PoiInfoDTO startInfo = poiInfoList.get(searchIndex);
                PlanNode nodeStart = PlanNode.withLocation(new LatLng(startInfo
                        .getLatitude(), startInfo.getLongitude()));
                PoiInfoDTO endInfo = poiInfoList.get(searchIndex + 1);
                PlanNode nodeEnd = PlanNode.withLocation(new LatLng(endInfo
                        .getLatitude(), endInfo.getLongitude()));
                if (planSearch != null)
                    this.planSearch.bikingSearch(new BikingRoutePlanOption()
                            .from(nodeStart).to(nodeEnd));
            } else {
                this.deleteOrSort = false;
                if (null != this.pd)
                    this.pd.dismiss();
            }

        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {


    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE:
                    this.currentPoiInfo = (PoiInfoDTO) data
                            .getSerializableExtra(EXTRA_POIINFO);

                    this.baiduMap.animateMapStatus(MapStatusUpdateFactory
                            .newLatLng(new LatLng(
                                    this.currentPoiInfo.getLatitude(),
                                    this.currentPoiInfo.getLongitude())));

                    this.mapChanged = false;
                    break;
            }
        }
    }

    /**
     * 根据RouteId获取route
     *
     * @param routeId
     */
    private void getMyRouteById(final String routeId) {
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
                if (null == nodes || nodes.isEmpty())
                    return;

                poiInfoList.clear();
                for (RouteNodeDTO rnd : nodes) {
                    if (rnd.getKeyNode() >= 0) {
                        poiInfoList.add(new PoiInfoDTO(rnd));
                    }
                }

                sources.addAll(poiInfoList);
                deleteOrSort = true;
                adapter.notifyDataSetChanged();
            }

        }, routeId);
    }

    /**
     * 规划步行路线
     *
     * @param poiInfoList
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void searchWalkingRoute(List<PoiInfoDTO> poiInfoList) {
        if (null == poiInfoList || poiInfoList.isEmpty() || isFinishing() || isDestroyed()) {
            this.baiduMap.clear();
            return;
        }

        if (poiInfoList.size() == 1) {
            this.baiduMap.clear();
            LatLng ll = new LatLng(poiInfoList.get(0).getLatitude(),
                    poiInfoList.get(0).getLongitude());

            TextView tv = (TextView) LayoutInflater
                    .from(RoutePlanActivity.this).inflate(
                            R.layout.route_map_make_plan_ordinal, null);
            tv.setText("1");
            tv.setBackgroundResource(R.drawable.route_map_line_start_icon);
            tv.setTextColor(getResources()
                    .getColor(android.R.color.transparent));

            OverlayOptions options = new MarkerOptions().position(ll).icon(
                    BitmapDescriptorFactory.fromView(tv));
            this.baiduMap.addOverlay(options);
            return;
        }

        if ((searchIndex + 1) >= poiInfoList.size())
            return;

        if (null != pd) {
            this.pd.setCancelable(true);
            this.pd.setMessage(R.string.route_map_make_activity_plain);
            this.pd.show();
        }

        PoiInfoDTO startInfo = poiInfoList.get(searchIndex);
        PlanNode nodeStart = PlanNode.withLocation(new LatLng(startInfo
                .getLatitude(), startInfo.getLongitude()));
        PoiInfoDTO endInfo = poiInfoList.get(searchIndex + 1);
        PlanNode nodeEnd = PlanNode.withLocation(new LatLng(endInfo
                .getLatitude(), endInfo.getLongitude()));
        if (planSearch != null)
            this.planSearch.bikingSearch(new BikingRoutePlanOption().from(
                    nodeStart).to(nodeEnd));
    }

    /**
     * 手动规划线路
     */
    private void drawLine() {
        if (null == this.poiInfoList || this.poiInfoList.size() < 2)
            return;

        this.baiduMap.clear();
        this.distanceView.setVisibility(View.VISIBLE);

        double distance = totalDistance / 1000;
        String unit = "km";
        if (!LocaleManager.isDisplayKM(RoutePlanActivity.this)) {
            distance = LocaleManager.kilometreToMile(distance);
            unit = "mi";
        }

        this.distanceTv.setText(String.format("%.0f", distance));
        this.distanceNuitTv.setText(unit);

        for (int i = 0; i < this.poiInfoList.size(); i++) {
            TextView tv = (TextView) LayoutInflater
                    .from(RoutePlanActivity.this).inflate(
                            R.layout.route_map_make_plan_ordinal, null);
            tv.setText(String.valueOf(i));
            if (i == 0) {
                tv.setBackgroundResource(R.drawable.route_map_line_start_icon);
                tv.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            } else if (i == this.poiInfoList.size() - 1) {
                tv.setBackgroundResource(R.drawable.route_map_line_end_icon);
                tv.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            } else if (!isSave && !first) {
                tv.setBackgroundResource(R.drawable.route_map_make_center_icon);
                tv.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                tv.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            }

            final BitmapDescriptor marker = BitmapDescriptorFactory
                    .fromView(tv);
            final LatLng latlng = new LatLng(this.poiInfoList.get(i)
                    .getLatitude(), this.poiInfoList.get(i).getLongitude());
            final OverlayOptions oo = new MarkerOptions().position(latlng)
                    .icon(marker);
            baiduMap.addOverlay(oo);
        }

        for (List<LatLng> value : this.nodesList) {
            if (null == value)
                continue;

            logger.trace("Rotue line source value size = " + value.size());
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
            logger.trace("Rotue line new value size = " + value.size());

            final OverlayOptions opts = new PolylineOptions().width(8)
                    .color(0xFF5E80E6).points(value);
            baiduMap.addOverlay(opts);

        }

        if (first) {
            this.zoomToSpans(poiInfoList);
        }

        if (isSave) {
            this.loading.setVisibility(View.VISIBLE);
            this.zoomToSpans(poiInfoList);
            this.uploadRoute(poiInfoList);
        }
    }

    /**
     * 自动缩放地图比例
     *
     * @param points
     */
    private void zoomToSpans(final List<PoiInfoDTO> points) {
        if (null == points || points.isEmpty())
            return;

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (final PoiInfoDTO point : points) {
            builder.include(new LatLng(point.getLatitude(), point
                    .getLongitude()));
        }

        int width = getResources().getDisplayMetrics().widthPixels * 2 / 3 * 6
                / 10;
        int height = getResources().getDisplayMetrics().heightPixels * 7 / 10;

        this.baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(
                builder.build(), height, width));
    }

    String md5Aid = null;

    @SuppressWarnings("unchecked")
    private void uploadRoute(final List<PoiInfoDTO> list) {
        if (null == list || list.size() < 1)
            return;

        this.baiduMap.setMyLocationEnabled(false);
        double distance = this.totalDistance;

        final RouteDTO route = new RouteDTO();
        PoiInfoDTO startInfo = list.get(0);
        PoiInfoDTO endInfo = list.get(list.size() - 1);
        route.setName(startInfo.getName() + "-" + endInfo.getName());
        route.setTotalDistance(distance);
        route.setOriginAltitude(0);
        route.setOriginLatitude(startInfo.getLatitude());
        route.setOriginLongitude(startInfo.getLongitude());
        route.setDestinationAltitude(0);
        route.setDestinationLatitude(endInfo.getLatitude());
        route.setDestinationLongitude(endInfo.getLongitude());
        route.setUserId(getUserId());

        final HandlerThread thread = new HandlerThread("Upload Route");
        thread.start();
        Handler uploadHandler = new Handler(thread.getLooper());
        uploadHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                baiduMap.snapshot(new SnapshotReadyCallback() {

                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        String routeMapPathName = "";
                        if (null != bitmap) {
                            routeMapPath = BitmapUtil.saveImage(bitmap,
                                    route.getName());
                            routeMapPathName = AlgorithmUtils.md5(routeMapPath);
                            if (!bitmap.isRecycled())
                                bitmap.recycle();
                        }

                        File file = new File(routeMapPath);

                        try {
                            md5Aid = AlgorithmUtils.md5(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        QiNiuManager qiNiuManager = new QiNiuManager(RoutePlanActivity.this);
                        String routeTokenKey = qiNiuManager.getRouteMapTokenKey() + md5Aid;
                        final String finalRouteTokenKey = routeMapPathName;
                        qiNiuManager.setQiNiuUploadCallBack(new QiNiuUploadCallBack() {
                            @Override
                            public void onComplete(String key) {
                                if (RoutePlanActivity.this == null)
                                    return;
                                getAsyncTaskQueue().add(new AsyncTask<List<PoiInfoDTO>, Void, Boolean>() {

                                    @Override
                                    protected Boolean doInBackground(
                                            List<PoiInfoDTO>... params) {
                                        final List<PoiInfoDTO> list = params[0];

                                        int index = 0;
                                        for (int i = 0; i < list.size(); i++) {
                                            long keyNode = i;
                                            PoiInfoDTO pd = list.get(i);
                                            RouteNodeDTO rnd = new RouteNodeDTO();
                                            rnd.setName(pd.getName());
                                            rnd.setKeyNode(keyNode);

                                            long ordinal = (keyNode << 48) | index;
                                            rnd.setOrdinal(ordinal);
                                            rnd.setAltitude(0);
                                            rnd.setLatitude(pd.getLatitude());
                                            rnd.setLongitude(pd.getLongitude());
                                            rnd.setCoordinate("bd09ll");

                                            index += 1;

                                            nodes.add(rnd);
                                        }


                                        try {
                                            if (TextUtils.isEmpty(routeId)) {
                                                return routeManager
                                                        .uploadRoute(
                                                                route, nodes,
                                                                md5Aid);
                                            } else {
                                                route.setId(routeId);
                                                return routeManager
                                                        .updateRoute(route,
                                                                nodes,
                                                                md5Aid);
                                            }
                                        } catch (BusinessException e) {
                                            return null;
                                        }

                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        saveLineTv.setClickable(true);
                                        loading.setVisibility(View.GONE);

                                        if (!TextUtils.isEmpty(routeId)
                                                && sp.contains(routeId))
                                            sp.edit().remove(routeId).apply();

                                        if (result) {
                                            Intent intent = new Intent();
                                            RoutePlanActivity.this.setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }

                                }, list);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                        qiNiuManager.uploadFile(routeTokenKey, routeMapPath, routeTokenKey);
                    }
                });

            }
        }, 800);

    }

    /**
     * 按返回是否退出
     */
    private void isSave() {
        if (!changed)
            finish();

        if (null != this.adapter && 2 <= this.adapter.getCount()) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setMessage(R.string.route_map_activity_exit_dialog_msg);
            dialog.setPositiveButton(R.string.route_map_activity_exit_dialog_ok, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            }).setNegativeButton(R.string.route_map_activity_exit_dialog_cancel, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show();
        } else {
            this.finish();
        }
    }

    /**
     * 比较是否改变
     *
     * @param list
     * @return
     */
    private void compare(List<PoiInfoDTO> list) {
        if (null == list)
            return;

        this.changed = false;
        this.first = !changed;

        if (list.size() != sources.size()) {
            this.changed = true;
            this.first = !changed;
            return;
        }

        for (int i = 0; i < sources.size(); i++) {
            PoiInfoDTO pid = sources.get(i);
            PoiInfoDTO currPid = list.get(i);
            if (pid.hashCode() != currPid.hashCode()) {
                this.changed = true;
                this.first = !changed;
                return;
            }
        }

    }

    private final class PoiInfoAdapter extends BaseAdapter {

        private final List<PoiInfoDTO> list;

        public PoiInfoAdapter(List<PoiInfoDTO> list) {
            this.list = list;
        }

        @Override
        public void notifyDataSetChanged() {
            if (null != this.list && this.list.size() >= 0) {

                if (this.list.size() > 0) {

                    saveLineTv.setVisibility(View.VISIBLE);
                    editTv.setVisibility(View.VISIBLE);
                    if (isEdit) {
                        editTv.setText(R.string.route_map_make_activity_point_edit_finish);
                        listTitle
                                .setText(R.string.route_map_make_activity_point_title);
                    } else {
                        editTv.setText(R.string.route_map_make_activity_point_edit);
                    }
                } else {
                    listTitle
                            .setText(R.string.route_map_make_activity_point_title);
                    saveLineTv.setVisibility(View.GONE);
                    editTv.setVisibility(View.GONE);
                }

                compare(list);

                final int size = list.size();

                if (changed && size > 1) {
                    saveLineTv.setVisibility(View.VISIBLE);
                } else {
                    saveLineTv.setVisibility(View.GONE);
                }

                if (1 < size) {
                    btnElevation.setVisibility(View.VISIBLE);
                } else {
                    btnElevation.setVisibility(View.INVISIBLE);
                }

                searchWalkingRoute(this.list);
            }
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PoiInfoViewHolder vh;

            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.route_map_make_item_edit, null);
                vh = new PoiInfoViewHolder(convertView, this.list);
            } else {
                vh = (PoiInfoViewHolder) convertView.getTag();
            }

            final PoiInfoDTO pd = this.list.get(position);
            pd.setIndex(position);

            vh.bind(pd);

            return convertView;
        }
    }

    private final class PoiInfoViewHolder extends ViewHolder<PoiInfoDTO> {

        @IdResource(R.id.route_map_make_item_name)
        private TextView nameView;

        @IdResource(R.id.route_map_make_item_ordinal_text)
        private TextView numView;

        @IdResource(R.id.route_map_make_item_ordinal_icon)
        private ImageView numIcon;

        @IdResource(R.id.route_map_make_item_delete)
        private ImageView delView;

        @IdResource(R.id.route_map_make_item_drag)
        private ImageView dragView;

        private List<PoiInfoDTO> list;

        protected PoiInfoViewHolder(View v, List<PoiInfoDTO> list) {
            super(v);
            this.list = list;
        }

        @Override
        public void bind(PoiInfoDTO t) {

            if (null == t)
                return;

            delView.setVisibility(isEdit ? View.VISIBLE : View.GONE);
            dragView.setVisibility(isEdit ? View.VISIBLE : View.GONE);

            int position = t.getIndex();
            int size = this.list.size();

            if (position == 0) {
                numIcon.setImageResource(R.drawable.route_map_make_list_start_icon);
                numView.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            } else if (position == size - 1 && size > 1) {
                numIcon.setImageResource(R.drawable.route_map_make_list_end_icon);
                numView.setTextColor(getResources().getColor(
                        android.R.color.transparent));
            } else {
                numIcon.setImageResource(R.drawable.route_map_make_list_pass_icon);
                numView.setTextColor(getResources().getColor(
                        android.R.color.white));
            }

            numView.setText(String.valueOf(position));
            nameView.setText(t.getName());
        }

    }

    @Override
    public void onBackPressed() {
        this.isSave();
    }

}
