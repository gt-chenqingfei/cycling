package com.beastbikes.android.modules.cycling.activity.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.InputDialog;
import com.beastbikes.android.dialog.InputDialog.OnInputDialogClickListener;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnBean;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnCallBack;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityService;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.view.SpeedAltitudeGraph;
import com.beastbikes.android.modules.cycling.sections.ui.RecordSegmentActivity;
import com.beastbikes.android.modules.map.MapBase;
import com.beastbikes.android.modules.map.MapType;
import com.beastbikes.android.modules.map.SpeedxMap;
import com.beastbikes.android.modules.preferences.ui.BaseEditTextActivity;
import com.beastbikes.android.modules.preferences.ui.EditTextActivity;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.beastbikes.android.modules.user.ui.ActivityComplainActivity;
import com.beastbikes.android.modules.user.ui.WatermarkCameraActivity;
import com.beastbikes.android.modules.user.ui.WatermarkFinishedActivity;
import com.beastbikes.android.modules.user.util.ActivityDataUtil;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.sharepopupwindow.CommonSharePopupWindow;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareImageDTO;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Alias("骑行完成的数据报表页")
@LayoutResource(R.layout.activity_finished_activity1)
@MenuResource(R.menu.activity_finished_detail_activity)
public class CyclingCompletedActivity extends SessionFragmentActivity implements
        OnClickListener, OnInputDialogClickListener, GoogleMapCnCallBack, RequestQueueManager {

    /**
     * Intent extra
     */
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_CLOUD_ACTIVITY = "cloud_activity";
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_EDIT_ACTIVITY_COVER = "edit_activity";// code
    public static final String EXTRA_AVATAR_URL = "avatar_url";
    public static final String EXTRA_NICK_NAME = "nick_name";
    public static final String EXTRA_IS_SYNC = "is_sync";
    public static final String EXTRA_SPORTIDENTIFY = "sportidentify";
    // 0:更改，1:删除

    public static final int RC_EDIT_ACTIVITY_TITLE = 11;
    public static final int RC_ADD_ACTIVITY_IMAGE = 12;
    public static final int RC_EDIT_ACTIVITY_COVER = 13;

    public static final int REQ_CYCLING_COMPLETE = 0X111;
    public static final int RESULT_UPDATE = 2;

    public static final int STATUS_PRIVATE = 1;
    public static final int STATUS_PUBLIC = 0;

    public static final long UNIX_TIME_2000 = 946656000;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CyclingCompletedActivity.class);

    private static final int DEFAULT_MAX_PROGRESS = 280;

    @IdResource(R.id.activity_complete_scrollview)
    private ScrollView scrollView;

    // 作弊
    @IdResource(R.id.activity_complete_cheat_prompt)
    private ViewGroup cheatView;

    @IdResource(R.id.ic_activity_complete_cheat_title)
    private TextView cheatTitleTv;

    @IdResource(R.id.activity_complete_user_avatar)
    private CircleImageView avatarIv;

    @IdResource(R.id.activity_complete_nickname)
    private TextView nickNameTv;

    // 地图层控件
    @IdResource(R.id.activity_complete_activity_name_viewgroup)
    private ViewGroup activityName;

    @IdResource(R.id.activity_complete_activity_name)
    private TextView titleName;

    @IdResource(R.id.activity_finished_activity_zoom_out_iv)
    private ImageView zoomOutIV;

    @IdResource(R.id.activity_complete_activity_edit_iv)
    private ImageView editIv;

    @IdResource(R.id.activity_complete_activity_report)
    private ViewGroup reportVG;

    @IdResource(R.id.activity_complete_activity_report_icon)
    private ImageView reportIv;

    @IdResource(R.id.activity_complete_activity_report_desc)
    private TextView reportDesc;

    @IdResource(R.id.activity_complete_map_layout)
    private ViewGroup mapLayout;

    // 数据层及图片层
    @IdResource(R.id.activity_complete_share_view)
    private ViewGroup dataView;

    @IdResource(R.id.activity_complete_record_segment_btn)
    private Button recordSegmentBtn;

    @IdResource(R.id.activity_complete_more_data_btn)
    private Button moreDataBtn;


    //里程
    @IdResource(R.id.distanceVG)
    private ViewGroup distanceVG;
    private TextView distanceValueTv;
    private ImageView distanceIcon;
    private TextView distanceLabelTv;

    //用时
    @IdResource(R.id.timeVG)
    private ViewGroup timeVG;
    private TextView timeValueTv;
    private ImageView timeIcon;
    private TextView timeLabelTv;

    //急速
    @IdResource(R.id.maxSpeedVG)
    private ViewGroup maxSpeedVG;
    private TextView maxSpeedValueTv;
    private ImageView maxSpeedIcon;
    private TextView maxSpeedLabelTv;

    //均速
    @IdResource(R.id.avgSpeedVG)
    private ViewGroup avgSpeedVG;
    private TextView avgSpeedValueTv;
    private ImageView avgSpeedIcon;
    private TextView avgSpeedLabelTv;

    // 曲线图
    @IdResource(R.id.activity_complete_activity_speed_achart)
    private LinearLayout speedChart;

    @IdResource(R.id.speedverticalunit)
    private TextView speedVerticalChartUnit;

    @IdResource(R.id.speedhorizontalunit)
    private TextView speedHorizontalChartUnit;

    @IdResource(R.id.elevationverticalunit)
    private TextView elevationVerticalChartUnit;

    // 分享
    @IdResource(R.id.activity_complete_activity_share)
    private Button shareBtn;

    @IdResource(R.id.map_speedx)
    private SpeedxMap speedxMap;

//    @IdResource(R.id.finished_avatar)
//    private LinearLayout finishedavatar;

//    @IdResource(R.id.activity_finished_activity_zoom_iv)
//    private ImageView zoomIV;

    @IdResource(R.id.activity_complete_activity_isprivate)
    private View isPrivateVg;

    @IdResource(R.id.activity_complete_activity_isprivate_icon)
    private ImageView isPrivateIcon;

    @IdResource(R.id.activity_complete_activity_isprivate_desc)
    private TextView isPrivateDesc;

    private RequestQueue requestQueue;

    private Context context;
    private CommonSharePopupWindow commonSharePopupWindow;
    private CommonShareImageDTO commonShareImageDTO;
    private ActivitySharePopupWindow stencilWindow;
    private ActivityManager activityManager;
    // 本地Activity
    private LocalActivity localActivity;
    private String sourceTitle;
    // 云端Activity
    private ActivityDTO remoteActivity;
    private String activityId;
    private double totalDistance;
    private String activityIdentifier;
    private String cityName;// 骑行开始地名
    private double velocity;
    private double velocityMax;
    private double totalElapsedTime;
    private double maxAltitude;
    private String title;

    // 分享图片路径
    private String sharePath;
    private long startTime;
    private String sceneryPath;
    private ActivityDtoComparator comparator;
    private int windowWidth;

    // 保存本地骑行图片Path
    private List<String> localSceneryUrls = new ArrayList<String>();
    // 保存服务器图片Url
    private boolean isMe;

    private InputDialog reportDialog;
    private LoadingDialog loadingDialog;
    private Bitmap mapBmp;
    private List<Double> altitudes = new ArrayList<Double>();
    private List<SampleDTO> samples = new ArrayList<>();

    private boolean isPrivate = false;
    private MapType mapType;
    private SharedPreferences defaultSp;
    private Bundle savedInstanceState;

    @Override
    public final RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    private SpeedAltitudeGraph speedAltitudeGraph = null;

    private void initView() {
        // 里程
        this.distanceValueTv = (TextView) distanceVG.findViewById(R.id.value);
        this.distanceIcon = (ImageView) distanceVG.findViewById(R.id.icon);
        this.distanceLabelTv = (TextView) distanceVG.findViewById(R.id.label);
        this.distanceIcon.setImageResource(R.drawable.ic_activity_param_distance);
        this.distanceLabelTv.setText(R.string.activity_finished_activity_distance_unit);

        // 用时
        this.timeValueTv = (TextView) timeVG.findViewById(R.id.value);
        this.timeIcon = (ImageView) timeVG.findViewById(R.id.icon);
        this.timeLabelTv = (TextView) timeVG.findViewById(R.id.label);
        this.timeIcon.setImageResource(R.drawable.ic_activity_param_elapsed_time);
        this.timeLabelTv.setText(R.string.label_total_time);

        // 极速
        this.maxSpeedValueTv = (TextView) maxSpeedVG.findViewById(R.id.value);
        this.maxSpeedIcon = (ImageView) maxSpeedVG.findViewById(R.id.icon);
        this.maxSpeedLabelTv = (TextView) maxSpeedVG.findViewById(R.id.label);
        this.maxSpeedIcon.setImageResource(R.drawable.ic_activity_param_max_velocity);
        this.maxSpeedLabelTv.setText(R.string.activity_data_max_velocity_label);

        // 均速
        this.avgSpeedValueTv = (TextView) avgSpeedVG.findViewById(R.id.value);
        this.avgSpeedIcon = (ImageView) avgSpeedVG.findViewById(R.id.icon);
        this.avgSpeedLabelTv = (TextView) avgSpeedVG.findViewById(R.id.label);
        this.avgSpeedIcon.setImageResource(R.drawable.ic_activity_param_velocity);
        this.avgSpeedLabelTv.setText(R.string.activity_param_label_speed);

        this.timeValueTv.setText(R.string.activity_param_elapsed_time_default_value);
        this.avgSpeedValueTv.setText(R.string.activity_param_velocity_default_value);
        this.maxSpeedValueTv.setText(R.string.activity_max_speed_default_value);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels;

//        int avatarWidth = getResources().getDimensionPixelSize(R.dimen.grid_explore_avatar_width);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(avatarWidth, avatarWidth);
//        int windowWidth = dm.widthPixels;
//        lp.setMargins((windowWidth - avatarWidth) / 2, dm.widthPixels - (avatarWidth / 2), 0, 0);
//        this.finishedavatar.setLayoutParams(lp);

        speedAltitudeGraph = new SpeedAltitudeGraph(CyclingCompletedActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                this.windowWidth, LayoutParams.MATCH_PARENT);
        speedChart.addView(speedAltitudeGraph, layoutParams);
        speedChart.setBackgroundColor(Color.parseColor("#101010"));

        this.activityName.setOnClickListener(this);
        this.shareBtn.setOnClickListener(this);
        this.cheatView.setOnClickListener(this);
        this.recordSegmentBtn.setOnClickListener(this);
        this.moreDataBtn.setOnClickListener(this);
//        this.zoomIV.setOnClickListener(this);
        this.zoomOutIV.setOnClickListener(this);
        this.isPrivateVg.setOnClickListener(this);

        AVUser user = AVUser.getCurrentUser();
        if (null == user || !getUserId().equals(user.getObjectId())) {
            this.isMe = false;
            this.shareBtn.setVisibility(View.GONE);
            this.editIv.setVisibility(View.GONE);
            this.cheatView.setVisibility(View.GONE);
            MarginLayoutParams lp1 = (MarginLayoutParams) this.scrollView.getLayoutParams();
            lp1.setMargins(0, 0, 0, 0);
            this.scrollView.setLayoutParams(lp1);
            this.activityName.setClickable(false);
            this.reportVG.setVisibility(View.VISIBLE);
            this.reportVG.setOnClickListener(this);
            this.isPrivateVg.setVisibility(View.GONE);
        } else {
            this.isPrivateVg.setVisibility(View.VISIBLE);
            this.reportVG.setVisibility(View.GONE);
            this.isMe = true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (null == intent)
            return;

        this.context = this;
        this.defaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        this.requestQueue = RequestQueueFactory.newRequestQueue(this);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        initView();


        this.activityManager = new ActivityManager(this);
        this.comparator = new ActivityDtoComparator();

        String avatarUrl = intent.getStringExtra(EXTRA_AVATAR_URL);
        String nickName = intent.getStringExtra(EXTRA_NICK_NAME);

        if (TextUtils.isEmpty(avatarUrl) || TextUtils.isEmpty(nickName)) {
            getUserInfo(getUserId());
        }

        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(this).load(avatarUrl).fit().error(R.drawable.ic_avatar).
                    placeholder(R.drawable.ic_avatar).centerCrop().into(this.avatarIv);
        } else {
            this.avatarIv.setImageResource(R.drawable.ic_avatar);
        }

        this.nickNameTv.setText(nickName);

        Object objectRemote;

        if ((objectRemote = intent.getSerializableExtra(EXTRA_CLOUD_ACTIVITY)) != null) {
            this.remoteActivity = (ActivityDTO) objectRemote;
            refreshRemote(remoteActivity);
            this.fillData(remoteActivity.getActivityId(), remoteActivity.getActivityIdentifier(), remoteActivity.getTotalDistance(),
                    remoteActivity.getStartTime(), remoteActivity.getIsPrivate(), remoteActivity.getTitle(),
                    remoteActivity.getElapsedTime(), remoteActivity.getVelocity(), remoteActivity.getMaxVelocity());
        }

        if (TextUtils.isEmpty(this.activityIdentifier)) {
            //从俱乐部feed和 urlSchema 跳过来 只传了 EXTRA_SPORT_IDENTIFY
            this.activityIdentifier = getIntent().getStringExtra(EXTRA_SPORTIDENTIFY);
        }

        fillView();
        refreshPrivateMapView();
        setUpMap();

        this.getActivityInfoByActivityId(this.activityIdentifier);
    }

    private void setUpMap() {

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
        speedxMap.setUp(mapType, this, isPrivate, scrollView, new SpeedxMap.MapReadyListener() {
            @Override
            public void onMapReady() {
                if (samples != null && samples.size() > 0) {
                    speedxMap.onResume();
                    speedxMap.drawMapPoint(samples);
                } else {
                    getSamples(activityIdentifier);
                }

            }
        });
        speedxMap.onCreate(savedInstanceState);
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
    }

    @Override
    public void onLowMemory() {
        speedxMap.onLowMemory();
    }

    protected void onSaveInstanceState(Bundle outState) {
        speedxMap.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        super.finish();
        speedxMap.finish();
        if (null != localActivity) {
            super.overridePendingTransition(0, R.anim.activity_out_to_bottom);
        } else {
            super.overridePendingTransition(0, R.anim.activity_out_to_right);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_complete_activity_name_viewgroup:// 修改骑行名称
                this.sourceTitle = this.titleName.getText().toString();
                final Intent editIntent = new Intent(this,
                        BaseEditTextActivity.class);
                editIntent.putExtra(EditTextActivity.EXTRA_VALUE, this.sourceTitle);
                startActivityForResult(editIntent, RC_EDIT_ACTIVITY_TITLE);

                SpeedxAnalytics.onEvent(this, "更改骑行记录名称", "edit_cycling_record_title");
                break;
            case R.id.activity_complete_cheat_prompt:// 作弊申诉
                showComplainDialog();
                break;
            case R.id.activity_complete_activity_share:// 炫耀一下
                this.showShareWindow();
                SpeedxAnalytics.onEvent(context, "报告页分享", "click_ridding_history_share");
                break;
            case R.id.activity_complete_activity_report:// 举报
                if (null != this.remoteActivity && !this.remoteActivity.isHasReport()) {
                    this.reportDialog = new InputDialog(
                            context,
                            null,
                            getString(R.string.activity_complete_activity_report_hint),
                            this, 70, false, false);
                    this.reportDialog.show();
                }
                break;
            case R.id.activity_complete_record_segment_btn:
                Intent intent = new Intent(CyclingCompletedActivity.this, RecordSegmentActivity.class);
                intent.putExtra(RecordSegmentActivity.EXTRA_ACTIVITY_ID, this.activityIdentifier);
                this.startActivity(intent);
                break;
            case R.id.activity_complete_more_data_btn:
                SpeedxAnalytics.onEvent(this, "查看更多数据", "click_ridding_history_more_date");
                Intent moreIntent = new Intent(this, CyclingDataActivity.class);
                if (null == remoteActivity) {
                    if (null != localActivity) {
                        localActivity.setMaxAltitude(maxAltitude);
                        remoteActivity = new ActivityDTO(localActivity);
                    } else {
                        return;
                    }
                }
                remoteActivity.setMaxAltitude(maxAltitude);
                moreIntent.putExtra(CyclingDataActivity.EXTRA_CYCLING, this.remoteActivity);
                startActivity(moreIntent);
                break;
//            case R.id.activity_finished_activity_zoom_iv:
//                scrollView.fullScroll(ScrollView.FOCUS_UP);
//                speedxMap.setMapFullScreen();
//
//                zoomOutIV.setVisibility(View.VISIBLE);
//                shareBtn.setVisibility(View.GONE);
//                finishedavatar.setVisibility(View.GONE);
//                scrollView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        return true;
//                    }
//                });
//                break;
            case R.id.activity_finished_activity_zoom_out_iv:
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                speedxMap.setMapWarpScreen();
                zoomOutIV.setVisibility(View.GONE);
                if (isMe) {
                    shareBtn.setVisibility(View.VISIBLE);
                }
//                finishedavatar.setVisibility(View.VISIBLE);
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
                break;
            case R.id.activity_complete_activity_isprivate:
                SpeedxAnalytics.onEvent(this, "设置地图私密", "setting_cycling_record_private");
                if (!isPrivate) {

                    boolean isFirst = defaultSp.getBoolean(Constants.PREF_SET_MAP_PRIVATE_FIRST, true);
                    if (isFirst) {
                        showSetMapPrivateDialog();
                    } else {
                        isPrivate = true;
                        isPrivateDesc.setText(R.string.activity_complete_activity_is_public);
                        isPrivateIcon.setImageResource(R.drawable.ic_route_public);
                        this.updateCyclingRecord(STATUS_PRIVATE);
                        speedxMap.switchMapHiddenState(isPrivate);
                    }
                } else {
                    isPrivate = false;
                    isPrivateDesc.setText(R.string.activity_complete_activity_isprivate);
                    isPrivateIcon.setImageResource(R.drawable.ic_route_private);
                    this.updateCyclingRecord(STATUS_PUBLIC);
                    speedxMap.switchMapHiddenState(isPrivate);
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().getBooleanExtra(EXTRA_IS_SYNC, true)) {
            AVUser user = AVUser.getCurrentUser();
            if (null != user && getUserId().equals(user.getObjectId())) {
                getMenuInflater().inflate(R.menu.activity_complete_upload_menu, menu);
                return true;
            }
        }

        if (null == localActivity)
            return false;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_record_detail_activity_action_button_finished:
                finish();
                return true;
            case R.id.activity_complete_upload_item:
                SpeedxAnalytics.onEvent(this, "", "save_ridding_goal");
                this.uploadLocalActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case RC_EDIT_ACTIVITY_COVER:// 修改或者删除风景图
                        int resultType = data
                                .getIntExtra(EXTRA_EDIT_ACTIVITY_COVER, -1);
                        if (resultType == 0)
                            this.startWatermarkCamera(true);

                        if (resultType == 1)
                            this.deleteActivityImage();
                        break;

                    case RC_EDIT_ACTIVITY_TITLE:// 修改title
                        Bundle bundle = data.getExtras();
                        String title = bundle
                                .getString(BaseEditTextActivity.EXTRA_VALUE);
                        if (TextUtils.isEmpty(title) || this.sourceTitle.equals(title))
                            return;

                        this.titleName.setText(title);

                        if (TextUtils.isEmpty(this.activityIdentifier))
                            return;

                        String remoteId = "";
                        try {
                            if (null != this.remoteActivity) {
                                this.remoteActivity.setTitle(title);
                                // update Activity
                                this.updateCyclingRecordTitle(remoteActivity.getActivityIdentifier(), title);
                            } else {
                                LocalActivity localActivity = this.activityManager
                                        .getLocalActivity(this.activityIdentifier);
                                if (null == localActivity)
                                    return;

                                // update LocalActivity
                                localActivity.setTitle(title);
                                this.activityManager.updateLocalActivity(localActivity);
                                remoteId = localActivity.getId();
                                if (!TextUtils.isEmpty(remoteId)) {
                                    // LocalActivity is sync, update Activity
                                    this.activityManager.updateCyclingRecordTitle(
                                            remoteId, title);
                                }
                            }
                        } catch (BusinessException e) {
                            logger.error("update activity title is error");
                        }
                        break;

                    case RC_ADD_ACTIVITY_IMAGE:
                        String filePath = data
                                .getStringExtra(WatermarkFinishedActivity.EXTRA_PICTURE_PATH);
                        if (TextUtils.isEmpty(filePath))
                            return;

                        final File file = new File(filePath);
                        if (!file.exists())
                            return;

                        this.sceneryPath = filePath;

                        final Options opts = new Options();
                        opts.inSampleSize = 4;

                        // 如果只保存一张图片需要clear
                        this.localSceneryUrls.clear();
                        this.localSceneryUrls.add(this.sceneryPath);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < this.localSceneryUrls.size(); i++) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("filePath" + 0, this.localSceneryUrls.get(i));
                                array.put(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (null == array || array.length() <= 0
                                || TextUtils.isEmpty(this.activityIdentifier))
                            return;

                        try {
                            LocalActivity localActivity = this.activityManager
                                    .getLocalActivity(this.activityIdentifier);
                            if (null == localActivity)
                                return;

                            this.activityManager.updateLocalActivity(localActivity);
                            logger.trace("update local activity local scenery url "
                                    + array.toString());
                        } catch (BusinessException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;

        }
    }

    @Override
    public void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean) {
        if (googleMapCnBean != null) {
            this.cityName = googleMapCnBean.getCityName();
        }
    }

    @Override
    public void onGetGeoInfoError(VolleyError volleyError) {

    }

    @Override
    public void onInputDialogClickOk(String text) {
        this.postReport(text);
    }


    private void fillData(String activityId, String activityIdentifier, double totalDistance,
                          long startTime, int isPrivate, String title, double totalElapsedTime,
                          double velocity, double velocityMax) {
        this.activityId = activityId;
        this.activityIdentifier = activityIdentifier;
        this.totalDistance = totalDistance / 1000;
        this.startTime = startTime;
        this.isPrivate = isPrivate == 1;
        this.title = title;
        this.totalElapsedTime = totalElapsedTime;
        this.velocity = velocity;
        this.velocityMax = velocityMax;

        if (TextUtils.isEmpty(this.title) || title.equals("null")
                || TextUtils.isEmpty(title.trim())) {
            this.title = ActivityDataUtil.formatDateTime(this, this.startTime);
        }

        if (this.velocity <= 0 || this.velocity == Double.NaN) {
            this.velocity = this.totalDistance / this.totalElapsedTime * 3600;
        }

        if (this.velocity > ActivityService.MAX_VELOCITY) {
            this.velocity = ActivityService.MAX_VELOCITY;
        }

        if (velocityMax > ActivityService.MAX_VELOCITY) {
            this.velocityMax = ActivityService.MAX_VELOCITY;
        }

        //国际化单位转换
        if (!LocaleManager.isDisplayKM(this)) {
            this.totalDistance = LocaleManager.kilometreToMile(this.totalDistance);
            this.velocity = LocaleManager.kphToMph(this.velocity);
            this.velocityMax = LocaleManager.kphToMph(this.velocityMax);
        }
    }

    private void fillView() {
        long h = 0, m = 0, s = 0;
        final long et = (long) totalElapsedTime;
        if (et > 0) {
            h = et / 3600;
            m = et % 3600 / 60;
            s = et % 3600 % 60;
        }

        String unitVelocity = LocaleManager.isDisplayKM(this)
                ? LocaleManager.LocaleString.activity_param_label_velocity
                : LocaleManager.LocaleString.activity_param_label_velocity_mph;
        String unitDistance = LocaleManager.isDisplayKM(this)
                ? LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance
                : LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance_mi;
        String unitAltitude = LocaleManager.isDisplayKM(this)
                ? LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m
                : LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;

        String chartVelocityLabel = getString(R.string.activity_complete_activity_achart_velocity_label)
                + unitVelocity;
        String chartAltitudeLabel = getString(R.string.activity_complete_activity_achart_altitude_label)
                + unitAltitude;
        String chartDistanceLabel = getString(R.string.activity_param_label_distance)
                + unitDistance;

        String distanceLabel = getString(R.string.activity_param_label_distance) + unitDistance;
        String speedLabel = getString(R.string.label_label_speed) + unitVelocity;
        String maxVelocityLabel = getString(R.string.label_max_velocity) + unitVelocity;

        speedVerticalChartUnit.setText(chartVelocityLabel);
        speedHorizontalChartUnit.setText(chartDistanceLabel);
        elevationVerticalChartUnit.setText(chartAltitudeLabel);

        this.distanceValueTv.setText(String.format("%.2f", totalDistance));
        this.distanceLabelTv.setText(distanceLabel);
        this.avgSpeedValueTv.setText(String.format("%.2f", velocity));
        this.avgSpeedLabelTv.setText(speedLabel);
        this.maxSpeedValueTv.setText(String.format("%.1f", velocityMax));
        this.maxSpeedLabelTv.setText(maxVelocityLabel);

        this.timeValueTv.setText(String.format("%02d:%02d:%02d", h, m, s));
        this.titleName.setText(title);

    }

    private void refreshRemote(ActivityDTO activity) {
        if (activity.isFake()) {
            this.cheatView.setVisibility(View.VISIBLE);
            if (activity.isNuked()) {
                this.cheatTitleTv.setText(R.string.activity_finished_activity_cheat_nuked);
            } else {
                this.cheatTitleTv.setText(R.string.activity_finished_activity_cheat_title);
            }
            MarginLayoutParams lp = (MarginLayoutParams) this.scrollView
                    .getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            this.scrollView.setLayoutParams(lp);
            this.shareBtn.setVisibility(View.GONE);
        }

        if (activity.isHasReport()) {
            this.reportDesc
                    .setText(R.string.activity_complete_activity_already_report);
            reportIv.setImageResource(R.drawable.ic_activity_complete_report_icon_selected);
            reportDesc.setTextColor(getResources().getColor(R.color.activity_complete_activity_report_desc_color));
            this.reportVG.setBackgroundResource(R.drawable.activity_complete_already_report_bg);
            this.reportVG.setClickable(false);
        }
    }

    /**
     * 刷新骑行路线是否为公开
     */
    private void refreshPrivateMapView() {

        if (isMe) {
            if (isPrivate) {
                isPrivateDesc.setText(R.string.activity_complete_activity_is_public);
                isPrivateIcon.setImageResource(R.drawable.ic_route_public);

            } else {
                isPrivateIcon.setImageResource(R.drawable.ic_route_private);
                isPrivateDesc.setText(R.string.activity_complete_activity_isprivate);
            }
        }
        speedxMap.switchMapHiddenState(isPrivate);

    }

    // 获取骑行轨迹打点
    private void getSamples(String activityIdentifier) {

        if (TextUtils.isEmpty(activityIdentifier))
            return;

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<SampleDTO>>() {

                    double totalMaxSpeed = 0;
                    double totalMaxSpeedDis = 0;

                    @Override
                    protected void onPreExecute() {
                        if (getWindow() == null)
                            return;
                        loadingDialog = new LoadingDialog(
                                context, getString(R.string.activity_complete_activity_loading), true);
                        loadingDialog.show();
                    }

                    @Override
                    protected List<SampleDTO> doInBackground(String... params) {
                        try {
                            List<SampleDTO> list = activityManager.getActivitySamplesByActivityId(
                                    params[0], getUserId(), params[1]);

                            if (null == list || list.isEmpty()) {
                                return null;
                            }

                            Collections.sort(list, comparator);
                            altitudes.clear();
                            List<SampleDTO> resultList = new ArrayList<SampleDTO>();

                            for (SampleDTO sd : list) {
                                double latitude = sd.getLatitude1();
                                double longitude = sd.getLongitude1();

                                if (latitude == 0 || longitude == 0
                                        || latitude == 4.9E-324
                                        || longitude == 4.9E-324)
                                    continue;


                                maxAltitude = Math.max(maxAltitude, sd.getAltitude());

                                if (sd.getVelocity() > totalMaxSpeed) {
                                    totalMaxSpeed = sd.getVelocity();
                                    totalMaxSpeedDis = sd.getDistance();
                                }
                                resultList.add(sd);
                            }


                            return resultList;
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<SampleDTO> result) {
                        if (null != loadingDialog && loadingDialog.isShowing())
                            loadingDialog.dismiss();

                        if (null == result || result.isEmpty() || isFinishing())
                            return;

                        samples = result;
                        speedxMap.drawMapPoint(result);

                        int value = result.size() / 30 + 1;
                        List<Double> speeds = new ArrayList<Double>();
                        List<Double> distances = new ArrayList<Double>();

                        boolean isAppendDistance = false;

                        for (int i = 1; i <= 30; i++) {
                            int endIndex = i * value - 1;
                            double speed = 0;
                            SampleDTO dto = null;
                            if (endIndex >= result.size()) {
                                speeds.add(0.0);
                                Double distance = result.get(result.size() - 1)
                                        .getDistance() / 1000;
                                distances.add(distance);

                                altitudes.add(result.get(result.size() - 1)
                                        .getAltitude());
                                continue;
                            }

                            dto = result.get(endIndex);
                            double diffDis = dto.getDistance()
                                    - result.get((i - 1) * value).getDistance();

                            speed = diffDis / (value * 5) * 3.6;

                            double dis = dto.getDistance() / 1000;


                            if (dis > totalMaxSpeedDis / 1000 && !isAppendDistance) {
                                speeds.add(totalMaxSpeed);
                                distances.add(totalMaxSpeedDis / 1000);
                                isAppendDistance = true;
                            }

                            if (totalMaxSpeed > ActivityService.MAX_VELOCITY) {
                                totalMaxSpeed = ActivityService.MAX_VELOCITY;
                            }

                            if (speed > totalMaxSpeed) {
                                speeds.add(totalMaxSpeed);
                            } else {
                                speeds.add(speed);
                            }
                            altitudes.add(dto.getAltitude());
                            distances.add(dis);
                        }

                        double minSpeed = speeds.get(0);
                        double minDistance = distances.get(0);
                        speeds.add(0, minSpeed / 2);
                        speeds.add(0, minSpeed / 4);
                        distances.add(0, minDistance / 2);
                        distances.add(0, minDistance / 4);
                        speeds.add(0, 0.0);
                        distances.add(0, 0.0);
                        speeds.add(0.0);
                        distances.add(totalDistance);

                        if (null != localActivity) {
                            localActivity.setMaxAltitude(maxAltitude);
                        }

                        if (null != remoteActivity) {
                            remoteActivity.setMaxAltitude(maxAltitude);
                        }

                        if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                            speedAltitudeGraph.drawSpeedGraph(listMax(speeds), totalDistance, distances, speeds);
                        } else {
                            speedAltitudeGraph.drawSpeedGraph(LocaleManager.kphToMph(listMax(speeds)), LocaleManager.kilometreToMile(totalDistance), LocaleManager.kilometreToMileList((ArrayList) distances), LocaleManager.kphToMphList((ArrayList) speeds));
                        }


                        fetchElevation();
                        getGeoCode(result);
                    }

                }, this.activityId, activityIdentifier);
    }

    // 弹出作弊提示框
    private void showComplainDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_finished_cheat_dialog, null);
        materialDialog.setContentView(view);
        materialDialog.setTitle(R.string.activity_finished_activity_cheat_title);
        materialDialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CyclingCompletedActivity.this,
                        ActivityComplainActivity.class);
                intent.putExtra(ActivityComplainActivity.EXTRA_USER_ID,
                        getUserId());
                if (null != remoteActivity)
                    intent.putExtra(ActivityComplainActivity.EXTRA_ACTIVITY_ID,
                            remoteActivity.getActivityId());
                startActivity(intent);
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }

    // 弹出作弊提示框
    private void showSetMapPrivateDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setTitle(R.string.activity_finished_activity_set_map_private);
        materialDialog.setMessage("");
        materialDialog.setPositiveButton(R.string.label_i_know, new OnClickListener() {
            @Override
            public void onClick(View v) {

                defaultSp.edit().putBoolean(Constants.PREF_SET_MAP_PRIVATE_FIRST, false).apply();

                isPrivate = true;
                isPrivateDesc.setText(R.string.activity_complete_activity_is_public);
                isPrivateIcon.setImageResource(R.drawable.ic_route_public);
                updateCyclingRecord(STATUS_PRIVATE);
                speedxMap.switchMapHiddenState(isPrivate);
                materialDialog.dismiss();
            }
        });

        materialDialog.show();

    }

    private void getGeoCode(List<SampleDTO> result) {
        if (null == result || result.isEmpty() || result.size() <= 0) {
            return;
        }

        if (isMe) {
            SampleDTO startPoint = result.get(0);
            GoogleMapCnAPI googleMapCnAPI = new GoogleMapCnAPI();
            googleMapCnAPI.geoCode(CyclingCompletedActivity.this.getRequestQueue(),
                    startPoint.getLatitude1(), startPoint.getLongitude1(), CyclingCompletedActivity.this);
        }
    }

    // 绘制海拔曲线
    private void fetchElevation() {
        String elevationStr = speedxMap.getElevations();
        if (TextUtils.isEmpty(elevationStr))
            return;

        int samplesSize = 1;
        if (elevationStr.contains("|")) {
            samplesSize = elevationStr.split("\\|").length;
            if (samplesSize > 100) {
                samplesSize = 100;
            }
        }

        final List<Double> listElevation = new ArrayList<Double>();
        final StringBuilder sb = new StringBuilder(
                "http://maps.google.cn/maps/api/elevation/json?path=");
        sb.append(elevationStr).append("&samples=" + samplesSize);
        logger.trace("the elevation request url is : " + sb.toString());
        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONArray arrayLatLng;
                        logger.info(response.toString());
                        if ("OK".equals(response.optString("status"))) {
                            arrayLatLng = response.optJSONArray("results");
                            for (int i = 0; i < arrayLatLng.length(); i++) {
                                try {
                                    JSONObject obj = (JSONObject) arrayLatLng.get(i);
                                    listElevation.add(obj.optDouble("elevation"));
                                } catch (JSONException e) {
                                    logger.error("get elevation error", e);
                                }
                            }
                            final double min = Math.min(0, listMin(listElevation));
                            maxAltitude = listMax(listElevation);

                            if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                                speedAltitudeGraph.drawAltitudeGraph(listElevation, maxAltitude, min);
                            } else {
                                speedAltitudeGraph.drawAltitudeGraph(LocaleManager.metreToFeet((ArrayList) listElevation),
                                        LocaleManager.metreToFeet(maxAltitude), LocaleManager.metreToFeet(min));
                            }

                        } else if ("INVALID_REQUEST".equals(response.optString("status"))) {
                            logger.error("get elevation error", getString(R.string.route_elevation_activity_error));
                        } else {
                            logger.error("get elevation error", getString(R.string.route_elevation_activity_error));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logger.error("get elevation error", error.getMessage());
                final double min = listMin(altitudes);
                if (LocaleManager.isDisplayKM(CyclingCompletedActivity.this)) {
                    speedAltitudeGraph.drawAltitudeGraph(altitudes, maxAltitude, min);
                } else {
                    speedAltitudeGraph.drawAltitudeGraph(LocaleManager.metreToFeet((ArrayList) altitudes),
                            LocaleManager.metreToFeet(maxAltitude), LocaleManager.metreToFeet(min));
                }
            }
        });

        this.getRequestQueue().add(request);
    }

    /**
     * 获取List中的最小值
     *
     * @param list
     */
    private double listMin(List<Double> list) {
        if (list.isEmpty()) {
            return 0;
        }

        return Collections.min(list);
    }

    /**
     * 获取List中的最大值
     *
     * @param list
     */
    private double listMax(List<Double> list) {
        if (list.isEmpty()) {
            return 50;
        }

        return Collections.max(list);
    }

    /*
     * 跳转到水印相机页面
     */
    private void startWatermarkCamera(boolean forResult) {
        Intent intent = new Intent(this, WatermarkCameraActivity.class);
        UserManager userManager = new UserManager(this);
        String nickName = "";
        String cityName = "";
        try {
            LocalUser localUser = userManager.getLocalUser(getUserId());
            if (null != localUser) {
                nickName = localUser.getNickname();
                cityName = localUser.getCity();
            }
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        if (null != localActivity)
            remoteActivity = new ActivityDTO(localActivity);

        if (null != remoteActivity) {
            remoteActivity.setNickname(nickName);
            remoteActivity.setCityName(cityName);
            if (!TextUtils.isEmpty(this.cityName))
                remoteActivity.setCityName(this.cityName);
            intent.putExtra(WatermarkCameraActivity.EXTRA_ACTIVITY_DTO,
                    remoteActivity);
        }

        if (forResult)
            startActivityForResult(intent, RC_ADD_ACTIVITY_IMAGE);
        else
            startActivity(intent);
    }

    /**
     * 删除骑行记录风景图
     */
    public void deleteActivityImage() {
        if (TextUtils.isEmpty(this.activityIdentifier))
            return;

        try {
            final LocalActivity localActivity = this.activityManager
                    .getLocalActivity(activityIdentifier);
            this.activityManager.updateLocalActivity(localActivity);

            this.sceneryPath = "";
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    private String createShareBmp(Bitmap mapBmp) {
        int marginLeft = 250 * windowWidth / 1080;
        int margin = DimensionUtils.dip2px(context, 17);
        int textMargin = DimensionUtils.dip2px(context, 15);
        int timeSize = DimensionUtils.dip2px(context, 14);
        int dateSize = DimensionUtils.dip2px(context, 12);

        Resources resources = context.getResources();
        Bitmap logoBmp = BitmapFactory.decodeResource(resources,
                R.drawable.ic_activity_complete_share_top);
        Bitmap topBmp = Bitmap.createBitmap(windowWidth,
                margin * 2 + logoBmp.getHeight(), Config.RGB_565);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        Canvas canvasTop = new Canvas(topBmp);
        canvasTop.drawColor(Color.parseColor("#111111"));
        canvasTop.drawBitmap(logoBmp, margin, margin, null);
        Paint textP = new Paint();
        textP.setColor(getResources().getColor(R.color.text_white_color));
        textP.setAntiAlias(true);
        textP.setTextSize(timeSize);
        textP.setTypeface(typeface);

        canvasTop.drawBitmap(logoBmp, margin, margin, null);
        if (null != logoBmp && !logoBmp.isRecycled()) {
            logoBmp.recycle();
            System.gc();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date startDate = new Date(startTime);
        String time = sdf.format(startDate);
        canvasTop.drawText(time, windowWidth - marginLeft, textMargin * 2, textP);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(startDate);
        textP.setColor(getResources().getColor(R.color.text_black_color));
        textP.setTextSize(dateSize);
        canvasTop.drawText(date, windowWidth - marginLeft, textMargin * 2
                + margin, textP);
        canvasTop.save(Canvas.ALL_SAVE_FLAG);
        canvasTop.restore();

        Bitmap topMapbmp = BitmapUtil.getBitmapByView(mapLayout);
        int topMapViewHeight = topMapbmp.getHeight();
        final Options mapViewOpts = new Options();
        mapViewOpts.inSampleSize = 4;

        Bitmap dataViewbmp = BitmapUtil.getBitmapByView(dataView);
        int dataViewHeight = dataViewbmp.getHeight();
        final Options dataViewOpts = new Options();
        dataViewOpts.inSampleSize = 4;

        Bitmap bottomBmp = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_activity_complete_share_bottom);
        int bottomHeight = bottomBmp.getHeight();
        int bottomWidth = bottomBmp.getWidth();

        int topHeight = 0;
        if (null != topBmp) {
            topHeight = topBmp.getHeight();
        }

        int totalHeight = topMapViewHeight + dataViewHeight;

        Bitmap shareBmp = Bitmap.createBitmap(windowWidth, totalHeight
                + bottomHeight + topHeight + margin * 2, Config.RGB_565);
        Canvas shareCanvas = new Canvas(shareBmp);
        shareCanvas.drawColor(Color.parseColor("#111111"));

        shareCanvas.drawBitmap(topBmp, 0, 0, null);
        if (null != topBmp && !topBmp.isRecycled()) {
            topBmp.recycle();
        }

        shareCanvas.drawBitmap(bottomBmp, (windowWidth - bottomWidth) / 2, totalHeight
                + topHeight + margin, null);
        if (null != bottomBmp && !bottomBmp.isRecycled()) {
            bottomBmp.recycle();
        }

        if (null != topMapbmp) {
            shareCanvas.drawBitmap(topMapbmp, 0, topHeight, null);
            if (null != topMapbmp && !topMapbmp.isRecycled()) {
                topMapbmp.recycle();
            }
        }

        if (null != dataViewbmp) {
            shareCanvas.drawBitmap(dataViewbmp, 0, topHeight + topMapViewHeight, null);
            if (null != dataViewbmp && !dataViewbmp.isRecycled()) {
                dataViewbmp.recycle();
            }
        }

        if (null != mapBmp) {
            shareCanvas.drawBitmap(mapBmp, 0, topHeight, null);
            if (null != mapBmp && !mapBmp.isRecycled()) {
                mapBmp.recycle();
            }
        }

////        Bitmap avatarBmp = BitmapUtil.getBitmapByView(this.finishedavatar);
////        int avatarWidth = avatarBmp.getWidth();
////        if (null != avatarBmp) {
////            Bitmap resultBmp = Bitmap.createBitmap(avatarWidth, avatarWidth,
////                    Config.ARGB_4444);
////            Paint paint = new Paint();
////            Canvas canvas = new Canvas(resultBmp);
////            canvas.drawColor(Color.WHITE);
////            //画圆
////            canvas.drawCircle(avatarWidth / 2, avatarWidth / 2, avatarWidth / 2, paint);
////            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 选择交集去上层图片
////            canvas.drawBitmap(avatarBmp, 0, 0, paint);
////            if (null != avatarBmp && !avatarBmp.isRecycled()) {
////                avatarBmp.recycle();
////            }
////            shareCanvas.drawBitmap(resultBmp, (windowWidth - avatarBmp.getWidth()) / 2,
////                    topHeight + windowWidth - avatarBmp.getHeight() / 2, null);
//        }

        shareCanvas.save(Canvas.ALL_SAVE_FLAG);
        shareCanvas.restore();
        String sharePath = BitmapUtil.saveImage(shareBmp);
        if (null != shareBmp && !shareBmp.isRecycled()) {
            shareBmp.recycle();
            System.gc();
        }

        return sharePath;
    }

    /**
     * 举报
     *
     * @param message
     */
    private void postReport(final String message) {
        if (TextUtils.isEmpty(this.activityId)) {
            return;
        }

        this.loadingDialog = new LoadingDialog(CyclingCompletedActivity.this, null, true);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return activityManager.postReportSportRoute(activityId,
                            params[0]);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }

                if (null != reportDialog) {
                    reportDialog.dismiss();
                }

                if (result) {
                    reportDesc.setText(R.string.activity_complete_activity_already_report);
                    reportIv.setImageResource(R.drawable.ic_activity_complete_report_icon_selected);
                    reportDesc.setTextColor(getResources().getColor(R.color.activity_complete_activity_report_desc_color));
                    reportVG.setBackgroundResource(R.drawable.activity_complete_already_report_bg);
                    reportVG.setClickable(false);
                }
            }

        }, message);
    }

    /**
     * Update Cycling Record Title
     *
     * @param activityId Cycling ID
     * @param title      Cycling Title
     */
    private void updateCyclingRecordTitle(final String activityId, final String title) {
        if (TextUtils.isEmpty(activityId) || TextUtils.isEmpty(title)) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return activityManager.updateCyclingRecordTitle(params[0], params[1]);
            }
        }, activityId, title);
    }

    /**
     * Get Activity Info by activityId
     *
     * @param activityId
     */
    private void getActivityInfoByActivityId(final String activityId) {

        if (TextUtils.isEmpty(activityId)) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ActivityDTO>() {
            @Override
            protected ActivityDTO doInBackground(String... params) {
                try {
                    return activityManager.getActivityInfoByActivityId(getUserId(), params[0]);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ActivityDTO activityDTO) {
                if (null == activityDTO) {
                    return;
                }

                remoteActivity = activityDTO;
                fillData(remoteActivity.getActivityId(), remoteActivity.getActivityIdentifier(), remoteActivity.getTotalDistance(),
                        remoteActivity.getStartTime(), remoteActivity.getIsPrivate(), remoteActivity.getTitle(),
                        remoteActivity.getElapsedTime(), remoteActivity.getVelocity(), remoteActivity.getMaxVelocity());
                fillView();

                refreshPrivateMapView();
            }
        }, activityId);
    }

    private final class ActivityDtoComparator implements Comparator<SampleDTO> {

        @Override
        public int compare(SampleDTO lhs, SampleDTO rhs) {
            if (lhs.getElapsedTime() < UNIX_TIME_2000 || lhs.getElapsedTime() == rhs.getElapsedTime()) {
                return (int) (lhs.getDistance() - rhs.getDistance());
            } else {
                return lhs.getElapsedTime() > rhs.getElapsedTime() ? 1 : -1;
            }
        }

    }

    private void uploadLocalActivity() {
        if (null == remoteActivity) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    LocalActivity localActivity = activityManager.getLocalActivity(remoteActivity.getActivityIdentifier());
                    if (null != localActivity) {
                        return activityManager.saveSamples(localActivity);
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer == 0) {
                    Toasts.show(CyclingCompletedActivity.this, R.string.setting_fragment_item_upload_error_log_success);
                } else {
                    Toasts.show(CyclingCompletedActivity.this, R.string.setting_fragment_item_upload_error);
                }
            }
        });
    }

    private void updateCyclingRecord(final int isPrivate) {
        setResult(RESULT_UPDATE);
        if (this.isPrivate && mapType != MapType.MapBox) {
            speedxMap.onDestroy();
            setUpMap();

        }
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    activityManager.updateCyclingRecord(activityIdentifier, isPrivate);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
    }

    private void getUserInfo(final String userId) {

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                ProfileDTO userInfo = null;
                try {
                    userInfo = new FriendManager
                            (CyclingCompletedActivity.this).getUserInfoById(userId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return userInfo;
            }

            @Override
            protected void onPostExecute(ProfileDTO userInfo) {
                super.onPostExecute(userInfo);
                if (userInfo != null) {

                    if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                        Picasso.with(CyclingCompletedActivity.this).load(userInfo.getAvatar()).
                                fit().error(R.drawable.ic_avatar).
                                placeholder(R.drawable.ic_avatar).centerCrop().into(avatarIv);
                    }

                    nickNameTv.setText(userInfo.getNickname());
                }
            }
        });
    }


    /**
     * 弹出分享选择框
     */
    private void showShareWindow() {
        this.loadingDialog = new LoadingDialog(this, getString(
                R.string.activity_complete_activity_create_stencil_loading), true);
        this.loadingDialog.show();

        speedxMap.snapshot(new MapBase.SnapshotReadyListener() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (null == bitmap)
                    return;

                mapBmp = bitmap;
                if (null != stencilWindow)
                    stencilWindow.dismiss();

                stencilWindow = new ActivitySharePopupWindow(
                        CyclingCompletedActivity.this, stencilListener);
                stencilWindow.showAtLocation(
                        findViewById(R.id.activity_complete_scrollview),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                if (null != loadingDialog)
                    loadingDialog.dismiss();

            }
        });

    }

    private final OnClickListener stencilListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != stencilWindow)
                stencilWindow.dismiss();
            switch (v.getId()) {
                case R.id.activity_complete_stencil_camera_btn:// 水印相机
                    startWatermarkCamera(false);
                    SpeedxAnalytics.onEvent(context, "分享水印相机", "click_ridding_history_share_digital_watermarking");
                    break;

                case R.id.activity_complete_stencil_data_btn:// 分享
                    loadingDialog = new LoadingDialog(
                            context,
                            getString(R.string.activity_complete_activity_create_share_loading),
                            true);
                    if (TextUtils.isEmpty(sharePath)) {
                        getAsyncTaskQueue().add(
                                new AsyncTask<Void, Void, String>() {

                                    @Override
                                    protected void onPreExecute() {
                                        if (null != loadingDialog)
                                            loadingDialog.show();
                                    }

                                    @Override
                                    protected String doInBackground(Void... params) {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                sharePath = createShareBmp(mapBmp);
                                            }
                                        });

                                        return sharePath;
                                    }

                                    @Override
                                    protected void onPostExecute(String result) {
                                        commonShareImageDTO = new CommonShareImageDTO();
                                        commonShareImageDTO.setImagePath(sharePath);
                                        commonSharePopupWindow = new CommonSharePopupWindow(CyclingCompletedActivity.this, commonShareImageDTO, "数据模版");
                                        commonSharePopupWindow
                                                .showAtLocation(findViewById(R.id.activity_complete_scrollview),
                                                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                                        if (null != loadingDialog)
                                            loadingDialog.dismiss();
                                    }

                                });

                    } else {
                        commonSharePopupWindow = new CommonSharePopupWindow(CyclingCompletedActivity.this, commonShareImageDTO, "数据模版");
                        commonSharePopupWindow
                                .showAtLocation(findViewById(R.id.activity_complete_scrollview),
                                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                        if (null != loadingDialog)
                            loadingDialog.dismiss();
                    }
                    SpeedxAnalytics.onEvent(context, "分享数据模版", "click_ridding_history_share_data_report");
                    break;
            }
        }

    };
}