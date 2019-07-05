package com.beastbikes.android.modules.user.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.grid.biz.GridManager;
import com.beastbikes.android.modules.cycling.grid.dto.GridDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.UserDetailDTO;
import com.beastbikes.android.utils.MapBoxManagerUtils;
import com.beastbikes.android.utils.MarkersClusterizer;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by icedan on 15/12/18.
 */
@LayoutResource(R.layout.grid_explore_activity)
public class GridExploreActivity extends SessionFragmentActivity implements MapboxMap.OnMarkerClickListener,
        MapView.OnMapChangedListener, OnMapReadyCallback {

    public static final String EXTRA_PROFILE = "profile";
    public static final String EXTRA_TOTAL_TIME = "total_time";

    @IdResource(R.id.grid_explore_activity_map_view)
    private RelativeLayout mapViewRL;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private MapBoxManagerUtils mapBoxManagerUtils;

    @IdResource(R.id.grid_explore_activity_avatar)
    private CircleImageView avatar;

    @IdResource(R.id.grid_explore_activity_nick_name)
    private TextView nickNameTv;

    @IdResource(R.id.grid_explore_activity_distance)
    private TextView distanceTv;

    @IdResource(R.id.grid_explore_activity_grid_count_value)
    private TextView countTv;

    @IdResource(R.id.grid_explore_activity_time)
    private TextView timeTv;

    private GridManager gridManager;
    private List<GridDTO> gridList = new ArrayList<>();
    private ArrayList<MarkerOptions> markers = new ArrayList<>();
    private double currZoomLevel;
    private double zoomLevel;
    private boolean showMarker;

    private boolean isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mapBoxManagerUtils = new MapBoxManagerUtils();
        this.mapView = mapBoxManagerUtils.init(this);
        this.mapViewRL.addView(mapView);
        this.mapView.getMapAsync(this);
        this.mapView.onCreate(savedInstanceState);
        this.mapView.addOnMapChangedListener(this);

        ProfileDTO profile = (ProfileDTO) getIntent().getSerializableExtra(EXTRA_PROFILE);
        if (null == profile) {
            finish();
            return;
        }

        this.gridManager = new GridManager(this);


        this.initView(profile);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        if (mapboxMap == null || isFinish)
            return;
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setOnMarkerClickListener(this);
        this.getGridList(getUserId());
    }

    @Override
    public void finish() {
        isFinish = true;
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    private void initView(ProfileDTO profile) {
        ViewGroup.LayoutParams mapLp = this.mapView.getLayoutParams();
        int avatarWidth = getResources().getDimensionPixelSize(R.dimen.grid_explore_avatar_width);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(avatarWidth, avatarWidth);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        mapLp.width = windowWidth;
        mapLp.height = windowWidth;
        lp.setMargins((windowWidth - avatarWidth) / 2, dm.widthPixels - (avatarWidth / 2), 0, 0);
        this.avatar.setLayoutParams(lp);
        this.mapView.setLayoutParams(mapLp);
//        this.mapView.setCenterCoordinate(new LatLng(39.995997, 116.4737817));

        if (!TextUtils.isEmpty(profile.getAvatar())) {
            Picasso.with(this).load(profile.getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar).into(this.avatar);
        } else {
            this.avatar.setImageResource(R.drawable.ic_avatar);
        }

        this.nickNameTv.setText(profile.getNickname());
        this.distanceTv.setText(String.format("%.2f",
                profile.getTotalDistance() / 1000));
        this.countTv.setText(String.valueOf(profile.getGridNum()));

        if (profile.getTotalElapsedTime() > 0) {
            final long tet = profile.getTotalElapsedTime();
            if (tet <= 0) {
                timeTv.setText("00:00:00");
            } else {
                final long h = tet / 3600, m = (tet % 3600) / 60;
                final long s = tet % 60;
                timeTv.setText(String.format(
                        "%02d:%02d:%02d", h, m, s));
            }
        } else if (profile.getTotalDistance() > 0) {
            this.fetchUserDetail(getUserId());
        } else {
            this.timeTv.setText("00:00:00");
        }
    }

    /**
     * 获取格子列表
     *
     * @param userId
     */
    private void getGridList(final String userId) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<GridDTO>>() {
            @Override
            protected List<GridDTO> doInBackground(String... params) {
                return gridManager.getUserGridList(userId);
            }

            @Override
            protected void onPostExecute(List<GridDTO> result) {
                if (null == result || result.isEmpty() || isFinish) {
                    return;
                }

                gridList.addAll(result);
                countTv.setText(String.valueOf(result.size()));

                List<LatLng> points = new ArrayList<>();
                for (GridDTO grid : result) {
                    points.add(grid.getLatLng1());
                }
                if (!isFinish)
                    mapBoxManagerUtils.zoomByLatLngs(mapboxMap, points);
                showPolygon();
            }
        });
    }

    /**
     * 获取用户骑行数据
     *
     * @param userId
     */
    private void fetchUserDetail(final String userId) {
        if (TextUtils.isEmpty(userId))
            return;

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, UserDetailDTO>() {

                    @Override
                    protected UserDetailDTO doInBackground(String... params) {
                        try {
                            return new UserManager(GridExploreActivity.this).getUserDetailByUserId(params[0]);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(UserDetailDTO profile) {
                        if (profile == null)
                            return;
                        final long tet = profile.getTotalElapsedTime();
                        if (tet <= 0) {
                            timeTv.setText("00:00:00");
                        } else {
                            final long h = tet / 3600, m = (tet % 3600) / 60;
                            final long s = tet % 60;
                            timeTv.setText(String.format(
                                    "%02d:%02d:%02d", h, m, s));
                        }
                    }

                }, userId);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onMapChanged(int change) {
//        this.currZoomLevel = this.mapView.getZoomLevel();
        if (this.mapboxMap == null)
            return;
        this.currZoomLevel = mapBoxManagerUtils.getZoomLevel(mapView);
        if (currZoomLevel <= 10 && !showMarker) {
            this.showMarker();
        } else if (currZoomLevel > 10 && showMarker) {
            this.showPolygon();
        } else if (currZoomLevel <= 10 && Math.abs(currZoomLevel - zoomLevel) >= 1) {
            try {
                MarkersClusterizer.clusterMarkers(this.mapboxMap, this.markers);
                this.zoomLevel = this.currZoomLevel;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * show markers
     */
    private void showMarker() {
        if (isFinish)
            return;
        this.mapboxMap.removeAnnotations();
        this.showMarker = true;
        Drawable drawable = getResources().getDrawable(R.drawable.ic_grid_oval_icon);
        IconFactory factory = IconFactory.getInstance(this);
        Icon icon = factory.fromDrawable(drawable);
        for (GridDTO grid : this.gridList) {
            MarkerOptions marker = new MarkerOptions().icon(icon).position(grid.getLatLng1());
            this.markers.add(marker);
        }
        try {
            MarkersClusterizer.clusterMarkers(this.mapboxMap, this.markers);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示格子
     */
    private void showPolygon() {
        if (isFinish)
            return;
        this.showMarker = false;
        this.mapView.clearAnimation();
        this.mapboxMap.removeAnnotations();
        ArrayList<PolygonOptions> list = new ArrayList<>();
        for (GridDTO grid : this.gridList) {
            PolygonOptions options = new PolygonOptions().addAll(grid.getPolygons()).
                    fillColor(Color.parseColor(
                            "#00bcd4")).strokeColor(0xff00bcd4).alpha(0.5f);
            list.add(options);
        }

        this.mapboxMap.addPolygons(list);
        this.showMarker = false;
    }

}
