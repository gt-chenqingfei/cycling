package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapView;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.locationutils.UtilsLocationCallBack;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.dto.PoiInfoDTO;
import com.beastbikes.android.modules.cycling.route.ui.RouteMapSearchGeoActivity;
import com.beastbikes.android.modules.cycling.route.ui.RoutePlanActivity;
import com.beastbikes.android.modules.cycling.sections.biz.SectionManager;
import com.beastbikes.android.modules.cycling.sections.dto.SectionListDTO;
import com.beastbikes.android.modules.cycling.sections.ui.widget.CustomEditText;
import com.beastbikes.android.modules.cycling.sections.ui.widget.DrawableClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caoxiao on 16/4/5.
 */
@LayoutResource(R.layout.activity_competition_section)
public class CompetitionSectionActivity extends SessionFragmentActivity implements TextView.OnEditorActionListener,
        View.OnClickListener, Constants, BDLocationListener, UtilsLocationCallBack {

    private Toolbar mToolbar;

    @IdResource(R.id.activity_competition_section_search_et)
    private CustomEditText searchET;

    @IdResource(R.id.activity_competition_section_search)
    private ImageView searchIV;

    @IdResource(R.id.activity_competition_section_filter)
    private ImageView filterIV;

    @IdResource(R.id.activity_competition_section_switch)
    private ImageView switchIV;

    @IdResource(R.id.section_activity_location)
    private ImageView locationIV;

    private FragmentManager fragmentManager;

    private List<SectionListDTO> sectionList = new ArrayList<>();

    private int showStatus = 1;//1列表页,2地图页
    private int showList = 1;
    private int showMap = 2;

    private PoiInfoDTO currentPoiInfo;

    private SectionBaseFragment sectionListFragment;

    private SectionManager sectionManager;

    private double lat;
    private double lon;

    private boolean isFilter;

    private String difficult = "";
    private String distance = "";
    private String altdiff = "";
    private String slope = "";

    @IdResource(R.id.section_activity_map_layout)
    private RelativeLayout mapLayout;

    @IdResource(R.id.activity_competition_section_mapview)
    private MapView baiduMapView;

    private SectionMapManager sectionMapManager;

    private LocationClient client;

    private boolean isChineseVersion = true;

    private Timer timer;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        mToolbar = (Toolbar) findViewById(R.id.activity_competition_section_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompetitionSectionActivity.this.finish();
            }
        });
        isChineseVersion = LocaleManager.isChineseTimeZone();
        searchET.setOnEditorActionListener(this);
        searchET.addTextChangedListener(mTextWatcher);
        searchIV.setOnClickListener(this);
        filterIV.setOnClickListener(this);
        switchIV.setOnClickListener(this);
        locationIV.setOnClickListener(this);
        searchET.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        searchET.setText("");
                        break;
                    default:
                        break;
                }
            }

        });
        searchET.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchIV.setVisibility(View.GONE);
                } else {
                    searchIV.setVisibility(View.VISIBLE);
                }
            }
        });
        fragmentManager = getSupportFragmentManager();
        sectionListFragment = new SectionListFragment();
        fragmentManager.beginTransaction()
                .add(R.id.activity_competition_section_content, sectionListFragment)
                .commitAllowingStateLoss();
        sectionMapManager = new SectionMapManager(this);
        showStatus = showList;
        getData();
        if (isChineseVersion)
            initBaiduLocation();
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences(UtilsLocationManager.getInstance().getClass().getName(), 0);
        lat = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LAT, "0"));
        lon = Float.parseFloat(sharedPreferences.getString(BLE.PREF_LOCATION_LON, "0"));
        if (lat == 0 && lon == 0) {
            sectionListFragment.getLocationFail(getResources().getString(R.string.section_location_failed));
            return;
        }
        sectionManager = new SectionManager(this);
        sectionList.clear();
        getSectionList(lon, lat, 300, difficult, distance, altdiff, slope, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (baiduMapView != null)
            baiduMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (baiduMapView != null)
            baiduMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.unRegisterLocationListener(this);
            client.stop();
        }
        if (baiduMapView.getMap() != null)
            baiduMapView.getMap().setMyLocationEnabled(false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_competition_section_search:
//                if (!LocaleManager.isChineseTimeZone()) {
//                    searchET.setVisibility(View.VISIBLE);
//                    searchIV.setVisibility(View.GONE);
//                } else {
                RouteMapSearchGeoActivity.isLandscape = false;
                Intent geoIntent = new Intent(this, RouteMapSearchGeoActivity.class);
                startActivityForResult(geoIntent,
                        RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE);
//                }
                break;
            case R.id.activity_competition_section_filter:
                Intent intent = new Intent(CompetitionSectionActivity.this, SectionFiltersActivity.class);
                startActivityForResult(intent, SectionFiltersActivity.EXTRA_FILTER_RESULT_CODE);
                break;
            case R.id.activity_competition_section_switch:
                switchIV.setEnabled(false);
                if (showStatus == showList) {
                    showFragment(showMap);
                    showStatus = showMap;
                } else {
                    showFragment(showList);
                    showStatus = showList;
                }
                break;
            case R.id.section_activity_location:
                locationIV.setEnabled(false);
                UtilsLocationManager.getInstance().getLocation(this, this);
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasts.show(CompetitionSectionActivity.this, getResources().getString(R.string.location_fail));
                                locationIV.setEnabled(true);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 10000);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        if (timer != null)
            timer.cancel();
        locationIV.setEnabled(true);
        distance = "0,100";
        sectionMapManager.zoomToPoint(location);
        getData();
    }

    @Override
    public void onLocationFail() {

    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case RouteMapSearchGeoActivity.EXTRA_SEARCH_RESULT_CODE:
                    this.currentPoiInfo = (PoiInfoDTO) data
                            .getSerializableExtra(RoutePlanActivity.EXTRA_POIINFO);
                    lon = currentPoiInfo.getLongitude();
                    lat = currentPoiInfo.getLatitude();
                    difficult = "";
                    distance = "";
                    altdiff = "";
                    slope = "";
                    getSectionList(lon, lat, 300, difficult, distance, altdiff, slope, "");
                    break;
                case SectionFiltersActivity.EXTRA_FILTER_RESULT_CODE:
                    isFilter = true;
                    difficult = data.getStringExtra(SectionFiltersActivity.SECTION_DIFFICULT);
                    distance = data.getStringExtra(SectionFiltersActivity.SECTION_DISTANCE);
                    altdiff = data.getStringExtra(SectionFiltersActivity.SECTION_ALTDIFF);
                    slope = data.getStringExtra(SectionFiltersActivity.SECTION_SLOPE);
                    getSectionList(lon, lat, 300, difficult, distance, altdiff, slope, "");
                    break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v,
                                  int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() ==
                        KeyEvent.KEYCODE_ENTER)) {
            //处理事件
            Toasts.showOnUiThread(this, searchET.getText().toString());

            return true;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void initBaiduLocation() {
        final LocationClientOption options = new LocationClientOption();
        options.setOpenGps(true);
        options.setPriority(LocationClientOption.GpsFirst);
        options.setCoorType("bd09ll");
        options.setScanSpan(LocationClientOption.MIN_SCAN_SPAN * 5);
        options.setAddrType("all");
        this.client = new LocationClient(this);
        this.client.registerLocationListener(this);
        this.client.setLocOption(options);
        this.client.start();
        this.client.requestLocation();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (sectionMapManager != null)
            sectionMapManager.onReceiveLocation(bdLocation);
    }

    private void getSectionList(final double longitude, final double latitude, final float range, final String difficult, final String legRange,
                                final String altRange, final String slopeRange, final String orderby) {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<SectionListDTO>>() {

            @Override
            protected void onPreExecute() {
                loadingDialog = new LoadingDialog(CompetitionSectionActivity.this, getResources().getString(R.string.activity_record_detail_activity_loading), true);
                loadingDialog.show();
            }

            @Override
            protected List<SectionListDTO> doInBackground(Void... voids) {
                try {
                    return sectionManager.getSegmentList(longitude, latitude, range, difficult, legRange, altRange, slopeRange, orderby);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<SectionListDTO> sectionListDTOs) {
                if (loadingDialog != null)
                    loadingDialog.dismiss();
                if (sectionListDTOs == null || sectionListDTOs.size() == 0) {
                    if (isFilter) {
                        if (sectionListFragment != null)
                            sectionListFragment.filterFailed();
                        if (sectionMapManager != null)
                            sectionMapManager.filterFailed();
                    } else {
                        if (sectionListFragment != null)
                            sectionListFragment.noData(getResources().getString(R.string.section_filter_failed));
                        if (sectionMapManager != null)
                            sectionMapManager.filterFailed();
                    }
                    return;
                }
                sectionList = sectionListDTOs;
                if (sectionListFragment != null)
                    sectionListFragment.notifyDataSetChanged(sectionListDTOs);
                if (sectionMapManager != null)
                    sectionMapManager.notifyDataSetChanged(sectionListDTOs);
            }
        });
    }

    public void showFragment(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (index) {
            case 1:
                switchIV.setImageResource(R.drawable.ic_section_location);
                if (sectionListFragment != null) {
                    ft.show(sectionListFragment);
                } else {
                    sectionListFragment = new SectionListFragment();
                    ft.add(R.id.activity_competition_section_content, sectionListFragment);
                }
                mapLayout.setVisibility(View.GONE);
                break;
            case 2:
                switchIV.setImageResource(R.drawable.ic_section_search_list);
                mapLayout.setVisibility(View.VISIBLE);
                if (sectionListFragment != null) {
                    ft.hide(sectionListFragment);
                }
                break;
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commitAllowingStateLoss();
        switchIV.setEnabled(true);
    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Drawable leftDrawable = getResources().getDrawable(R.drawable.ic_section_search);
            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
            if (TextUtils.isEmpty(searchET.getText().toString())) {
                searchET.setCompoundDrawables(leftDrawable, null, null, null);
            } else {
                Drawable rightDrawable = getResources().getDrawable(R.drawable.ic_section_search_clear);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                searchET.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
            }
        }
    };
}
