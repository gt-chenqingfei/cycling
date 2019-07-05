package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.ScreenshotObserver;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.map.MapType;
import com.beastbikes.android.modules.map.SpeedxMap;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.beastbikes.android.widget.slidingup_pannel.SlidingUpPanelLayout;
import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersListView;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;


public class CyclingCompletedBase extends SessionFragmentActivity implements
        AbsListView.OnScrollListener, RecordSideBar.OnSideBarItemClickListener
        , RecordActionBar.OnActionBarItemClickListener, SlidingUpPanelLayout.PanelSlideListener
        , RecordSummary.OnSummaryItemClickListener, ScreenshotObserver.OnScreenshotListener {

    protected static final Logger logger = LoggerFactory.getLogger("CyclingCompletedBase");

    public static final int RC_EDIT_ACTIVITY_TITLE = 11;
    public static final int RC_ADD_ACTIVITY_IMAGE = 12;
    public static final int RC_EDIT_ACTIVITY_COVER = 13;

    public static final int REQ_CYCLING_COMPLETE = 0X111;
    public static final int RESULT_UPDATE = 2;

    public static final int STATUS_PRIVATE = 1;
    public static final int STATUS_PUBLIC = 0;

    public static final long UNIX_TIME_2000 = 946656000;

    private static final int DEFAULT_MAX_DELAY = 40;
    private int mCounter = 0;

    protected SlidingUpPanelLayout mSliderLayout;
    private RecordSideBar mSideBar;
    private RecordActionBar mActionBar;
    protected SpeedxMap speedxMap;

    protected LinearLayout mShareView;

    private StickyListAdapter mStickyAdapter;
    protected RecordSummary mSummary;
    protected boolean isPrivate = false;
    protected MapType mapType;
    private AdapterStatistics mAdapterStatistics;

    protected ActivityDtoComparator mComparator;
    protected SharedPreferences mDefaultSp;
    protected RequestQueue mRequestQueue;
    private LoadingDialog mLoadingDialog;
    private long mZoomExpire = 0;
    private ScreenshotObserver mScreenShotObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycling_completed);

        mSliderLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        StickyListHeadersListView mStickyListView = (StickyListHeadersListView) findViewById(R.id.record_sticky_list);
        mSideBar = (RecordSideBar) findViewById(R.id.record_side_bar);
        mActionBar = (RecordActionBar) findViewById(R.id.record_action_bar);
        speedxMap = (SpeedxMap) findViewById(R.id.record_map_speedx);
        mShareView = (LinearLayout) findViewById(R.id.record_share_view);

        mSummary = new RecordSummary(this);
        mAdapterStatistics = new AdapterStatistics(this);
        mStickyAdapter = new StickyListAdapter(this, mAdapterStatistics, mSliderLayout, mStickyListView);
        mStickyListView.addHeaderView(mSummary);

        mStickyListView.setDrawingListUnderStickyHeader(true);
        mStickyListView.setAreHeadersSticky(true);
        mStickyListView.setAdapter(mStickyAdapter);
        mStickyListView.setOnScrollListener(this);
        mZoomExpire = SystemClock.currentThreadTimeMillis();
        mSliderLayout.addPanelSlideListener(this);
        mSideBar.setOnSideBarItemClickListener(this);

        mActionBar.setItemClickListener(this);
        mSummary.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSliderLayout.setPanelHeight(mSummary.getMeasuredHeight());
                mSideBar.setDefaultMargin(mSummary.getMeasuredHeight());
            }
        }, 1);

        this.mDefaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        this.mRequestQueue = RequestQueueFactory.newRequestQueue(this);
        this.mComparator = new ActivityDtoComparator();

        this.mSummary.setOnSummaryItemClickListener(this);

        mScreenShotObserver = new ScreenshotObserver(new Handler(getMainLooper()), this);
        mScreenShotObserver.subscript(this);
    }

    public List<SampleDTO> getSamples() {
        return null;
    }

    protected void setUpMap() {
        final BeastBikes app = (BeastBikes) this.getApplication();
        if (app.isMapStyleEnabled() || isPrivate) {
            mapType = MapType.MapBox;
        } else {
            if (!LocaleManager.isChineseTimeZone()) {
                mapType = MapType.Google;
            } else {
                mapType = MapType.BaiDu;
            }
        }
        speedxMap.setUp(mapType, this, isPrivate, null, new SpeedxMap.MapReadyListener() {
            @Override
            public void onMapReady() {

                List<SampleDTO> samples = getSamples();
                if (samples != null && samples.size() > 0) {
                    speedxMap.onResume();
                    speedxMap.drawMapPoint(samples);
                }

            }
        });
        speedxMap.onCreate(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        speedxMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        speedxMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speedxMap.onDestroy();
        mScreenShotObserver.unSubscript();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (mSummary.getTop() == 0) {
            mSliderLayout.setTouchEnabled(true);
        } else {
            mSliderLayout.setTouchEnabled(false);
        }
    }

    @Override
    public void onSideBarItemClick(int id) {
        switch (id) {
            case R.id.record_side_btn_zoom:

                mZoomExpire = SystemClock.currentThreadTimeMillis();
                if (mSliderLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                    mSliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                } else {
                    mSliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                mSideBar.zoom();

                break;
        }
    }

    @Override
    public void onActionBarItemClick(int id) {
        switch (id) {
            case R.id.action_bar_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        //Log.i(TAG, "onPanelSlide, offset " + slideOffset);

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState preState,
                                    SlidingUpPanelLayout.PanelState newState) {
        long timeDiff = SystemClock.currentThreadTimeMillis() - mZoomExpire;

        mCounter++;
        if (mCounter <= 2) {
            return;
        }

        if (timeDiff < DEFAULT_MAX_DELAY) {
            return;
        }

        mActionBar.animation(newState, preState);
        mSideBar.animation(newState, preState);
    }

    @Override
    public void onSummaryItemClick(int id) {

    }

    @Override
    public void onScreenshot(String path) {

    }

    protected final class ActivityDtoComparator implements Comparator<SampleDTO> {

        @Override
        public int compare(SampleDTO lhs, SampleDTO rhs) {
            if (lhs.getElapsedTime() < UNIX_TIME_2000
                    || lhs.getElapsedTime() == rhs.getElapsedTime()) {
                return (int) (lhs.getDistance() - rhs.getDistance());
            } else {
                return lhs.getElapsedTime() > rhs.getElapsedTime() ? 1 : -1;
            }
        }
    }

    protected void notifyAllDataSetChanged(ActivityDTO dto, List<SampleDTO> samples,
                                           List<Double> altitudes, boolean isMine) {
        if (mAdapterStatistics != null) {
            mAdapterStatistics.notifyDataSetChanged(dto, samples, altitudes, isMine);
        }
        if (mStickyAdapter != null) {
            mStickyAdapter.notifyDataChanged(dto);
            mStickyAdapter.notifyBottomDots();
        }
        notifyActionBarDataChanged(dto);
        notifySideBarDataChange(dto);
        notifySummaryDataChanged(dto);
    }

    protected void notifyActionBarDataChanged(ActivityDTO dto) {
        if (mActionBar != null) {
            mActionBar.onDataChanged(dto);
        }
    }

    protected void notifySummaryDataChanged(ActivityDTO dto) {
        if (mSummary != null) {
            mSummary.onDataChanged(dto);
        }
    }

    protected void notifySideBarDataChange(ActivityDTO dto) {
        if (mSideBar != null) {
            mSideBar.onDataChanged(dto);
        }
    }

    protected void notifyStatisticsElevationChanged(double totalDistance, List<Double> distances, List<Double> altitudes,
                                                    double maxAltitude,
                                                    double min) {
        if (mAdapterStatistics != null) {
            mAdapterStatistics.notifyElevationDataChanged(distances, altitudes, maxAltitude, min);
        }
    }

    protected void notifyStatisticsSlopeChanged(double totalDistance, List<SampleDTO> sampleDTOs) {
        if (mAdapterStatistics != null) {
            mAdapterStatistics.notifySlopeDataChanged(totalDistance, sampleDTOs);
        }
    }

    protected void notifyStatisticsHeartRateChanged(ActivityDTO dto, int limitHeartRate, List<Double> distances,
                                                    List<Double> heartRates) {
        if (mAdapterStatistics != null) {
            mAdapterStatistics.notifyHeartRateDataChanged(dto, limitHeartRate, distances, heartRates);
        }
    }

    protected void notifyCadencesChanged(List<Double> cadences) {
        if (mAdapterStatistics != null) {
            mAdapterStatistics.notifyCadenceDataChanged(cadences);
        }
    }

    protected void notifyStatisticsSamplesChanged(ActivityDTO dto, List<SampleDTO> samples,
                                                  double maxVelocity, double maxDistance,
                                                  List<Double> distances, List<Double> velocities) {
        if (mAdapterStatistics != null) {
            mAdapterStatistics.notifySamplesChanged(dto, samples, maxVelocity, maxDistance,
                    distances, velocities);
        }
    }


    protected void loadingShow(boolean cancelable, String msg) {
        if (getWindow() == null || isFinishing()) {
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, msg, cancelable);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    protected void loadingDismiss() {
        if (mLoadingDialog == null || isFinishing() || getWindow() == null) {
            return;
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

}