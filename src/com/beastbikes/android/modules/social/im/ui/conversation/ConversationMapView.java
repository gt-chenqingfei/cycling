package com.beastbikes.android.modules.social.im.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

@LayoutResource(R.layout.activity_conversationmapview)
public class ConversationMapView extends SessionFragmentActivity implements
        OnClickListener {

    @IdResource(R.id.localtion_selsect_activity_mapview)
    private MapView mapView;

    @IdResource(R.id.localtion_selsect_activity_button_zoom_out)
    private ImageView btnZoonOut;

    @IdResource(R.id.localtion_selsect_activity_button_zoom_in)
    private ImageView btnZoomIn;

    @IdResource(R.id.localtion_selsect_activity_address)
    private TextView address;

    private BaiduMap mBaiduMap;

    public static final String LATLNGLATTAG = "LATLNGLATTAG";
    public static final String LATLNGLONTAG = "LATLNGLONTAG";
    public static final String LATLNGADDRESS = "LATLNGADDRESS";
    private double lat;
    private double lng;
    private String addressText;
    private float zoomLevel = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        mBaiduMap = mapView.getMap();
        this.mapView.showZoomControls(false);
        this.mBaiduMap.getUiSettings().setCompassEnabled(false);
        // 隐藏百度地图Logo
        this.mapView.getChildAt(1).setVisibility(View.GONE);
        // 隐藏百度地图比例尺
        this.mapView.getChildAt(3).setVisibility(View.GONE);

        this.btnZoomIn.setOnClickListener(this);
        this.btnZoonOut.setOnClickListener(this);

        Intent intent = getIntent();
        if (null != intent) {
            lat = intent.getDoubleExtra(LATLNGLATTAG, 0);
            lng = intent.getDoubleExtra(LATLNGLONTAG, 0);

            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordType.COMMON);
            // sourceLatLng待转换坐标
            converter.coord(new LatLng(lat, lng));
            LatLng desLatLng = converter.convert();
            this.lat = desLatLng.latitude;
            this.lng = desLatLng.longitude;
            addressText = intent.getStringExtra(LATLNGADDRESS);
        }
        if (lat != 0 && lng != 0) {
            addMarker();
        }
        if (!TextUtils.isEmpty(addressText)) {
            address.setText(addressText);
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
        this.mBaiduMap.clear();
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none,
                R.anim.activity_out_to_right);
    }

    private void addMarker() {
        // 定义Maker坐标点
        LatLng point = new LatLng(lat, lng);
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.route_map_make_start_icon);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                .zoomTo(this.zoomLevel));
        this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                .newLatLng(new LatLng(lat, lng)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.localtion_selsect_activity_button_zoom_in: {// 放大
                final float min = this.mBaiduMap.getMinZoomLevel();
                final float level = Math.max(this.zoomLevel - 1, min);
                final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
                this.mBaiduMap.animateMapStatus(u);
                this.zoomLevel = level;
                break;
            }
            case R.id.localtion_selsect_activity_button_zoom_out: {// 缩小
                final float max = this.mBaiduMap.getMaxZoomLevel();
                final float level = Math.min(this.zoomLevel + 1, max);
                final MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level);
                this.mBaiduMap.animateMapStatus(u);
                this.zoomLevel = level;
                break;
            }
            default:
                break;
        }
    }


}
