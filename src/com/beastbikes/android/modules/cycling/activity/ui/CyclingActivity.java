package com.beastbikes.android.modules.cycling.activity.ui;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityService;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityState;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dto.PreviewDto;
import com.beastbikes.android.modules.cycling.activity.util.GpsStatusObserve;
import com.beastbikes.android.permission.EasyPermissions;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.NumberTextView;
import com.beastbikes.android.widget.convenientbanner.ConvenientBanner;
import com.beastbikes.android.widget.convenientbanner.holder.CBViewHolderCreator;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.activity_cycing)
public class CyclingActivity extends SessionFragmentActivity implements Constants, View.OnClickListener {

    private static final Logger logger = LoggerFactory.getLogger(CyclingActivity.class);
    private static final int REQUEST_CODE_LOCATION = 12;

    @IdResource(R.id.cycling_fragment_data_viewpager)
    private ConvenientBanner cyclingDataView;
    @IdResource(R.id.fragment_cycling_svg_speed_label)
    private TextView speedLabelTv;
    @IdResource(R.id.cycling_fragment_svg_speed)
    private NumberTextView currSpeedTv;
    @IdResource(R.id.fragment_cycling_distance_unit)
    private TextView distanceUnitTv;
    @IdResource(R.id.cycling_fragment_real_distance)
    private NumberTextView currDistanceTv;
    @IdResource(R.id.cycling_fragment_cycling_finish)
    private TextView finishIv;
    @IdResource(R.id.cycling_fragment_cycling_resume_or_pause)
    private TextView resumeOrPauseIv;
    @IdResource(R.id.cycling_fragment_cycling_map)
    private TextView mapIv;
    @IdResource(R.id.cycling_data_setting_view)
    private ViewGroup dataSetting;
    @IdResource(R.id.cycling_activity_hide_cycling_view)
    private ImageView hideView;

    private final BroadcastReceiver receiver = new ActivityBroadcastReceiver();

    private ActivityManager activityManager;
    private AlphaAnimation alphaAnimation;
    private boolean isChineseTimeZone = false;
    private List<PreviewDto> previewList = new ArrayList<>();
    private LocalActivity activity;
    private SharedPreferences userSp;

    @IdResource(R.id.activity_cycling_gps_status_layout)
    private LinearLayout gpsStatusLayout;

    @IdResource(R.id.activity_cycling_gps_status_view1)
    private View gpsStatusView1;

    @IdResource(R.id.activity_cycling_gps_status_view2)
    private View gpsStatusView2;

    @IdResource(R.id.activity_cycling_gps_status_view3)
    private View gpsStatusView3;

    @IdResource(R.id.activity_cycling_gps_status_tv)
    private TextView gpsStatusTV;

    private List<View> gpsStatusViews;

    private int timerCount;

    private boolean isFirstGetGpsStatus = true;
    private GpsStatusObserve gpsStatusObserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_bottom, R.anim.activity_none);
        this.registerCyclingReceiver();
        this.activityManager = new ActivityManager(this);
        this.userSp = getSharedPreferences(getUserId(), 0);
        this.alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        this.alphaAnimation.setRepeatCount(Animation.INFINITE);
        this.alphaAnimation.setDuration(800);
        this.alphaAnimation.setRepeatMode(Animation.REVERSE);

        this.isChineseTimeZone = LocaleManager.isDisplayKM(this);

        this.initCyclingView();

        gpsStatusViews = new ArrayList<>();
        gpsStatusViews.add(gpsStatusView1);
        gpsStatusViews.add(gpsStatusView2);
        gpsStatusViews.add(gpsStatusView3);
        userSp.edit().putBoolean(Constants.PREF_CYCLING_STATE_CHECK_KEY, false).apply();

        final LocalActivity la = this.activityManager.getCurrentActivity();
        final BeastBikes app = (BeastBikes) getApplication();
        if (null == la) {
            this.syncUIWithActivityState(ActivityState.STATE_NONE);
            this.resumeOrPauseIv.clearAnimation();
        } else {
            this.syncUIWithActivityState(la.getState());
            if (la.getState() == ActivityState.STATE_PAUSED || la.getState() == ActivityState.STATE_AUTO_PAUSED) {
                this.resumeOrPauseIv.startAnimation(this.alphaAnimation);
            }
            if (app.isCyclingScreenOnEnable()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            try {
                activity = this.activityManager.getLocalActivity(la.getId());
                refreshDataView(activity);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }

        final Intent intent = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
        intent.setPackage(getPackageName());
        this.startService(intent);
        this.bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            locationStatus();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        this.initDataSp();
        if (null != this.gpsStatusObserve) {
            this.gpsStatusObserve.addGpsStatusListener();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != gpsStatusObserve) {
            this.gpsStatusObserve.removeGpsStatusListener();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unbindService(connection);
        this.unregisterReceiver(receiver);
        userSp.edit().putBoolean(Constants.PREF_CYCLING_STATE_CHECK_KEY, true).apply();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_bottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cycling_fragment_cycling_finish:// 结束骑行
                final LocalActivity la = activityManager.getCurrentActivity();
                if (null == la) {
                    this.syncUIWithActivityState(ActivityState.STATE_NONE);
                    sendActivityFinishedBroadcast();
                    return;
                }

                if (la.getTotalDistance() <= 10) {
                    final MaterialDialog dialog = new MaterialDialog(this);
                    dialog.setMessage(R.string.activity_state_label_finish_error_message)
                            .setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    sendActivityFinishedBroadcast();
                                }
                            })
                            .setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    final MaterialDialog finishedDialog = new MaterialDialog(this);
                    finishedDialog.setMessage(R.string.label_finish_cycling_dialog_msg);
                    finishedDialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishedDialog.dismiss();
                            sendActivityFinishedBroadcast();
                            Toasts.show(CyclingActivity.this, R.string.activity_state_label_finish_message);
                        }
                    }).setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishedDialog.dismiss();
                        }
                    }).show();
                }
                break;
            case R.id.cycling_fragment_cycling_map:// 查看地图
                SpeedxAnalytics.onEvent(this, "查看地图", "click_ridding_map");
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeScaleUpAnimation(mapIv, (mapIv.getWidth() / 2),
                                mapIv.getHeight() / 2, 0, 0);
                Intent startIntent = new Intent(this, MapActivity.class);
                ActivityCompat.startActivity(this, startIntent, options.toBundle());
                break;
            case R.id.cycling_fragment_cycling_resume_or_pause:// 暂停或继续骑行
                this.onPauseOrResumeCycling();
                break;

            case R.id.cycling_data_setting_view:// 设置数据
                SpeedxAnalytics.onEvent(this, "设置数据", null);
                Intent settingIntent = new Intent(this, CyclingSettingPageActivity.class);
                settingIntent.putExtra(CyclingSettingPageActivity.EXTRA_SETTING_POSITION, this.cyclingDataView.getCurrentItem());
                startActivity(settingIntent);
                break;
            case R.id.cycling_activity_hide_cycling_view:
                SpeedxAnalytics.onEvent(this, "最小化按钮", "hide_ridding_real_time_data");
                finish();
                break;
        }
    }

    /**
     * 根据GPS状态刷新UI
     * Handler
     */
    Handler gpsHandler = new Handler();
    Runnable gpsRunnable = new Runnable() {
        @Override
        public void run() {
            timerCount = timerCount % 3;
            for (int i = 0; i < gpsStatusViews.size(); i++) {
                gpsStatusViews.get(i).setBackgroundResource(R.color.club_act_manager_dialog_view_bg);
            }
            gpsStatusViews.get(timerCount).setBackgroundResource(R.color.designcolor_c7);
            gpsStatusTV.setText(getResources().getString(R.string.positioning));
            gpsStatusTV.setTextColor(getResources().getColor(R.color.designcolor_c7));
            timerCount++;
            gpsHandler.postDelayed(this, 400);
        }
    };

//    Runnable gpsSuccessRunnable = new Runnable() {
//        @Override
//        public void run() {
//            ObjectAnimator moveIn = ObjectAnimator.ofFloat(gpsStatusLayout, "translationX", 0, -500f);
//            ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(gpsStatusLayout, "alpha", 1f, 0.8f, 0.6f);
//            AnimatorSet animSet = new AnimatorSet();
//            animSet.play(moveIn).with(fadeInOut);
//            animSet.setDuration(2000);
//            animSet.start();
//        }
//    };

    /**
     * 根据GPS状态刷新UI
     */
    private void locationStatus() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // Have not permissions, do the thing!
            EasyPermissions.requestPermissions(this, getString(R.string.msg_start_cycling_get_location_permission),
                    REQUEST_CODE_LOCATION, perms);
            return;
        }

        if (!isFirstGetGpsStatus || !ActivityService.isScreenOn)
            return;
        isFirstGetGpsStatus = false;
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(gpsStatusLayout, "translationX", -500f, 0f);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(gpsStatusLayout, "alpha", 1f, 0f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(moveIn).with(fadeInOut);
        animSet.setDuration(2000);
        animSet.start();
        timerCount = 0;
        gpsHandler.postDelayed(gpsRunnable, 400);
        gpsStatusObserve = new GpsStatusObserve(this) {
            @Override
            public void onLocationSuccess() {
                gpsHandler.removeCallbacks(gpsRunnable);
                for (int i = 0; i < gpsStatusViews.size(); i++) {
                    gpsStatusViews.get(i).setBackgroundResource(R.color.location_title_success_color);
                }
                gpsStatusTV.setText(getResources().getString(R.string.position_success));
                gpsStatusTV.setTextColor(getResources().getColor(R.color.location_title_success_color));
//                gpsHandler.postAtTime(gpsSuccessRunnable, 3000);
            }

            @Override
            public void onLocationFailed() {
                timerCount = 0;
                gpsHandler.postDelayed(gpsRunnable, 400);
            }
        };
    }

    private void registerCyclingReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityService.ACTION_ACTIVITY_START);
        filter.addAction(ActivityService.ACTION_ACTIVITY_PAUSE);
        filter.addAction(ActivityService.ACTION_ACTIVITY_AUTO_PAUSE);
        filter.addAction(ActivityService.ACTION_ACTIVITY_RESUME);
        filter.addAction(ActivityService.ACTION_ACTIVITY_COMPLETE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(this.receiver, filter);
    }

    /**
     * 初始化view
     */
    private void initCyclingView() {
        this.finishIv.setOnClickListener(this);
        this.resumeOrPauseIv.setOnClickListener(this);
        this.mapIv.setOnClickListener(this);
        this.hideView.setOnClickListener(this);
        this.cyclingDataView.setPages(new CBViewHolderCreator<CyclingDataViewHolder>() {
            @Override
            public CyclingDataViewHolder createHolder() {
                return new CyclingDataViewHolder();
            }
        }, previewList);
        this.cyclingDataView.setPageIndicator(new int[]{R.drawable.circle_indicator_stroke, R.drawable.circle_indicator_solid});
        this.cyclingDataView.setPageIndicatorMargin(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL,
                DimensionUtils.dip2px(this, 94));
        this.cyclingDataView.setcurrentitem(0);
        this.dataSetting.setOnClickListener(this);
        if (isChineseTimeZone) {
            this.speedLabelTv.setText(getString(R.string.cycling_fragment_real_time_speed) + "(km/h)");
        } else {
            this.speedLabelTv.setText(getString(R.string.cycling_fragment_real_time_speed) + "(MPH)");
        }
    }

    /**
     * 初始化数据滚动条
     */
    private void initDataSp() {
        JSONArray array = null;
        if (userSp.contains(PREF_CYCLING_DATA_SETTING_KEY)) {
            String data = userSp.getString(PREF_CYCLING_DATA_SETTING_KEY, "");
            try {
                array = new JSONArray(data);
            } catch (Exception e) {
                logger.error("get cycling data setting error," + e);
            }
        } else {
            array = new JSONArray();
            JSONObject timeArray = new JSONObject();
            try {
                timeArray.put("0", CYCLING_DATA_TIME);
            } catch (Exception e) {
                logger.error("Cycling data put time error, " + e);
            }

            try {
                array.put(0, timeArray);
            } catch (Exception e) {
                logger.error("Cycling data set time error, " + e);
            }

            JSONObject speedArray = new JSONObject();
            try {
                speedArray.put("0", CYCLING_DATA_ALTITUDE);
                speedArray.put("1", CYCLING_DATA_SVG_SPEED);
            } catch (Exception e) {
                logger.error("Cycling data put altitude and svg speed error, " + e);
            }

            try {
                array.put(1, speedArray);
            } catch (Exception e) {
                logger.error("Cycling data set altitude and svg error, " + e);
            }

            JSONObject uphillArray = new JSONObject();
            try {
                uphillArray.put("0", CYCLING_DATA_UPHILL_DISTANCE);
            } catch (Exception e) {
                logger.error("Cycling data put altitude and svg error, " + e);
            }

            try {
                array.put(2, uphillArray);
            } catch (Exception e) {
                logger.error("Cycling data set uphill distance error, " + e);
            }

            userSp.edit().putString(PREF_CYCLING_DATA_SETTING_KEY, array.toString()).apply();
        }

        if (null == array || array.length() <= 0) {
            userSp.edit().remove(PREF_CYCLING_DATA_SETTING_KEY).apply();
            this.initDataSp();
            return;
        }

        previewList.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.optJSONObject(i);
            previewList.add(new PreviewDto(this, data, activity, isChineseTimeZone));
        }

        this.cyclingDataView.notifyDataSetChanged();
    }

    /**
     * 暂停或者继续骑行
     */
    private void onPauseOrResumeCycling() {
        final Intent intent = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
        intent.putExtra(ActivityService.EXTRA_ACTION, ActivityService.ACTION_ACTIVITY_PAUSE_OR_RESUME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(getPackageName());
        startService(intent);
    }

    /**
     * 发送结束骑行广播
     */
    private void sendActivityFinishedBroadcast() {
        SpeedxAnalytics.onEvent(this, "", "click_ridding_finish");
        final Intent intent = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
        intent.putExtra(ActivityService.EXTRA_ACTION, ActivityService.ACTION_ACTIVITY_COMPLETE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(getPackageName());
        startService(intent);
    }

    /**
     * 根据骑行状态刷新UI
     *
     * @param state
     */
    private void syncUIWithActivityState(int state) {
        switch (state) {
            case ActivityState.STATE_NONE:
            case ActivityState.STATE_COMPLETE:
                this.onActivityTerminated();
                break;
            case ActivityState.STATE_STARTED:
                this.onActivityResumed();
                break;
            case ActivityState.STATE_PAUSED:
            case ActivityState.STATE_AUTO_PAUSED:
                this.onActivityPaused();
                break;
        }
    }

    /**
     * 开始骑行
     */
    private void onActivityStart() {
        this.resumeOrPauseIv.setText(R.string.activity_state_label_resume);
        this.resumeOrPauseIv.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_cycling_pause_icon, 0, 0);
        this.finishIv.setVisibility(View.VISIBLE);
        final BeastBikes app = (BeastBikes) getApplication();
        if (app.isCyclingScreenOnEnable()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 结束骑行
     */
    private void onActivityTerminated() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.resumeOrPauseIv.setText(R.string.activity_state_label_start);
        this.resumeOrPauseIv.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_cycling_start_icon, 0, 0);
        this.resetActivityParameterValues();
        finish();
    }

    private void onActivityPaused() {
        this.resumeOrPauseIv.startAnimation(alphaAnimation);
        this.resumeOrPauseIv.setText(R.string.activity_state_label_resume);
        this.resumeOrPauseIv.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_cycling_start_icon, 0, 0);
        this.finishIv.setVisibility(View.VISIBLE);
        this.currSpeedTv.setText("0.0");
        this.initDataSp();
    }

    private void onActivityResumed() {
        this.resumeOrPauseIv.clearAnimation();
        this.currDistanceTv.clearAnimation();
        this.currSpeedTv.clearAnimation();

        this.finishIv.setVisibility(View.VISIBLE);
        this.resumeOrPauseIv.setText("");
        this.resumeOrPauseIv.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.ic_cycling_pause_icon, 0, 0);
    }

    private void resetActivityParameterValues() {
        this.currDistanceTv.setText("0.0");
        this.currSpeedTv.setText("0.0");
    }

    private void refreshDataView(LocalActivity localActivity) {
        if (null != localActivity) {
            activity = localActivity;
            final double d = localActivity.getTotalDistance() / 1000;
            final double v = localActivity.getInstantaneousVelocity();
            if (isChineseTimeZone) {
                currDistanceTv.setText(String.format("%.1f", d));
                currSpeedTv.setText(String.format("%.1f", v));
                distanceUnitTv.setText(R.string.activity_finished_activity_distance_unit);
            } else {
                currDistanceTv.setText(String.format("%.1f", LocaleManager.kilometreToMile(d)));
                distanceUnitTv.setText(R.string.profile_fragment_statistic_item_total_distance_mi);
                currSpeedTv.setText(String.format("%.1f", LocaleManager.kphToMph(v)));
            }

            initDataSp();
        }
    }

    private final class ActivityBroadcastReceiver extends BroadcastReceiver {

        ActivityBroadcastReceiver() {
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (null == intent)
                return;

            final String action = intent.getAction();

            if (null == action)
                return;

            final LocalActivity la = (LocalActivity) intent
                    .getSerializableExtra(ActivityService.EXTRA_ACTIVITY);

            if (null == la)
                return;

            if (ActivityService.ACTION_ACTIVITY_COMPLETE.equals(action)
                    || ActivityService.ACTION_ACTIVITY_PAUSE.equals(action)
                    || ActivityService.ACTION_ACTIVITY_AUTO_PAUSE
                    .equals(action)
                    || ActivityService.ACTION_ACTIVITY_RESUME.equals(action)) {
                syncUIWithActivityState(la.getState());
                return;
            }

            if (ActivityService.ACTION_ACTIVITY_START.equals(action)) {
                onActivityStart();
            }

        }
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ActivityService service = ((ActivityService.ICyclingBinder) binder).getService();
            service.setICyclingBinderListener(serviceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final ActivityService.ICyclingServiceListener serviceListener =
            new ActivityService.ICyclingServiceListener() {
                @Override
                public void onLocalActivityRefresh(final LocalActivity localActivity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshDataView(localActivity);
                        }
                    });
                }
            };

}
