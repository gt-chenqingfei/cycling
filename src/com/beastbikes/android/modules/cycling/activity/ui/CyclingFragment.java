package com.beastbikes.android.modules.cycling.activity.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityService;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityState;
import com.beastbikes.android.modules.cycling.activity.biz.CyclingManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dto.MyGoalInfoDTO;
import com.beastbikes.android.modules.user.ui.CyclingRecordActivity;
import com.beastbikes.android.permission.EasyPermissions;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.NumberProgressBar;
import com.beastbikes.android.widget.NumberTextView;
import com.beastbikes.android.widget.RippleView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.StringResource;
import com.beastbikes.framework.business.BusinessException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by icedan on 16/1/6.
 */
@StringResource(R.string.empty)
@LayoutResource(R.layout.fragment_cycling)
public class CyclingFragment extends SessionFragment implements OnClickListener,
        RippleView.OnRippleCompleteListener, Constants, EasyPermissions.PermissionCallbacks {

    private static final Logger logger = LoggerFactory.getLogger(CyclingFragment.class);
    private static final int REQUEST_CODE_LOCATION = 12;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final BroadcastReceiver receiver = new ActivityBroadcastReceiver();

    private View rootView;

    // cycling target view
    @IdResource(R.id.cycling_fragment_target_setting_iv)
    private ImageView settingBtn;
    @IdResource(R.id.cycling_fragment_target_distance_label)
    private TextView distanceLabelTv;
    @IdResource(R.id.cycling_fragment_target_distance_tv)
    private NumberTextView distanceTv;
    @IdResource(R.id.cycling_fragment_monthly_count)
    private NumberTextView monthlyCountTv;
    @IdResource(R.id.cycling_fragment_monthly_time)
    private NumberTextView monthlyTimeTv;
    @IdResource(R.id.cycling_fragment_monthly_avg_speed)
    private NumberTextView monthlySpeedTv;
    @IdResource(R.id.fragment_cycling_number_progress)
    private NumberProgressBar numberProgress;
    @IdResource(R.id.fragment_cycling_progress)
    private ProgressBar progressBar;
    @IdResource(R.id.cycling_fragment_current_target_tv)
    private TextView targetTv;

    @IdResource(R.id.cycling_fragment_start)
    private RippleView rippleView;

    @IdResource(R.id.fragment_cycling_record)
    private View cyclingRecord;

    @IdResource(R.id.cycling_fragment_start_activity)
    private ImageView startIv;

    private CyclingManager cyclingManager;
    private ActivityManager activityManager;
    private Timer progressTimer;
    private SharedPreferences userSp;
    private MyGoalInfoDTO myGoalInfo;
    private Toolbar toolbar;
    private boolean isChineseTimeZone = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = super.onCreateView(inflater, container, savedInstanceState);

        this.cyclingManager = new CyclingManager(getActivity());
        this.settingBtn.setOnClickListener(this);
        this.rippleView.setOnRippleCompleteListener(this);
        this.cyclingRecord.setOnClickListener(this);

        this.toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        setHasOptionsMenu(false);
        return this.rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityService.ACTION_ACTIVITY_COMPLETE);
        filter.addAction(CyclingTargetSettingActivity.ACTION_TARGET_DISTANCE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        activity.registerReceiver(this.receiver, filter);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(this.receiver);
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activityManager = new ActivityManager(getActivity());
        this.userSp = getActivity().getSharedPreferences(getUserId(), 0);

        this.getMyGoal();
        this.refreshView();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.isChineseTimeZone = LocaleManager.isDisplayKM(getActivity());
        String label = getString(R.string.cycling_fragment_current_distance_label) + "(km)";
        if (!isChineseTimeZone) {
            label = getString(R.string.cycling_fragment_current_distance_label) + "(mi)";
        }
        this.distanceLabelTv.setText(label);
        this.updateCyclingState();
        checkCyclingState();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.toolbar.setBackgroundColor(getResources().getColor(R.color.bg_black_color));
        this.rootView.setBackgroundColor(getResources().getColor(R.color.common_bg_color));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != progressTimer) {
            this.progressTimer.cancel();
            this.progressTimer = null;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle(R.string.activity_fragment_title);
            checkCyclingState();
        }

    }

    private void checkCyclingState() {

        boolean checkOn = userSp.getBoolean(Constants.PREF_CYCLING_STATE_CHECK_KEY, true);

        if (activityManager != null && checkOn) {
            LocalActivity activity = activityManager.getCurrentActivity();
            if (null != activity) {
                final Intent cyclingIntent = new Intent(getContext(), CyclingActivity.class);
                startActivity(cyclingIntent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cycling_fragment_target_setting_iv:
                ActivityOptionsCompat options1 =
                        ActivityOptionsCompat.makeScaleUpAnimation(settingBtn, (settingBtn.getWidth() / 2),
                                settingBtn.getHeight() / 2, 0, 0);
                Intent intent = new Intent(getActivity(), CyclingTargetSettingActivity.class);
                if (null != myGoalInfo) {
                    intent.putExtra(CyclingTargetSettingActivity.EXTRA_TARGET_DISTANCE, myGoalInfo.getMyGoal() / 1000);
                }
                ActivityCompat.startActivity(getActivity(), intent, options1.toBundle());
                SpeedxAnalytics.onEvent(getActivity(), "click_ridding_goal", "click_ridding_goal");
                break;
            case R.id.fragment_cycling_record:
                final Intent it = new Intent(getActivity(), CyclingRecordActivity.class);
                if (AVUser.getCurrentUser() != null) {
                    it.putExtra(CyclingRecordActivity.EXTRA_USER_ID, AVUser.getCurrentUser().getObjectId());
                    it.putExtra(CyclingRecordActivity.EXTRA_AVATAR_URL, AVUser.getCurrentUser().getAvatar());
                    it.putExtra(CyclingRecordActivity.EXTRA_NICK_NAME, AVUser.getCurrentUser().getDisplayName());
                    it.putExtra(CyclingRecordActivity.EXTRA_REFRESH, true);
                    startActivity(it);
                    SpeedxAnalytics.onEvent(getActivity(), "查看我的骑行纪录列表", null);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                switch (requestCode) {
                    case EasyPermissions.SETTINGS_REQ_CODE:
                        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            this.startCycling();
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        logger.trace("获取权限，Permissions = " + perms.toString());
        startCycling();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        logger.trace("获取权限失败，Permissions = " + perms.toString());
    }

    @Override
//    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    public void onComplete(RippleView rippleView) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Have permissions, do the thing!
            this.startCycling();
        } else {
            logger.warn("start ActivityService has no location permission");
            EasyPermissions.requestPermissions(this, getString(R.string.msg_start_cycling_get_location_permission),
                    REQUEST_CODE_LOCATION, perms);
        }
    }

    private void updateCyclingState() {
        final LocalActivity currActivity = this.activityManager.getCurrentActivity();
        if (null == currActivity) {
            this.startIv.setImageResource(R.drawable.cycling_start_bg_btn);
            return;
        }

        final int state = currActivity.getState();
        switch (state) {
            case ActivityState.STATE_STARTED:// 开始骑行
                this.startIv.setImageResource(R.drawable.ic_cycling_pause_icon);
                break;
            case ActivityState.STATE_PAUSED:// 暂停
            case ActivityState.STATE_AUTO_PAUSED:// 自动暂停
                this.startIv.setImageResource(R.drawable.ic_cycling_start_icon);
                break;
            case ActivityState.STATE_COMPLETE:// 结束骑行
            case ActivityState.STATE_NONE:// 没有开始骑行
                this.startIv.setImageResource(R.drawable.cycling_start_bg_btn);
                break;
        }
    }

    /**
     * 获取我的目标
     */
    private void getMyGoal() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, MyGoalInfoDTO>() {
            @Override
            protected MyGoalInfoDTO doInBackground(Void... params) {
                return cyclingManager.getMyGoalInfo();
            }

            @Override
            protected void onPostExecute(MyGoalInfoDTO result) {
                if (null == result) {
                    return;
                }

                if (null != myGoalInfo && myGoalInfo.getMyGoal() == result.getMyGoal() &&
                        myGoalInfo.getCurGoal() == result.getCurGoal()) {
                    return;
                }

                myGoalInfo = result;
                refreshView();
            }
        });
    }

    /**
     * 刷新目标页面
     */
    private void refreshView() {
        MyGoalInfoDTO result = null;
        if (userSp.contains(PREF_CYCLING_MY_GOAL_KEY)) {
            String data = this.userSp.getString(PREF_CYCLING_MY_GOAL_KEY, "");
            if (!TextUtils.isEmpty(data)) {
                try {
                    JSONObject object = new JSONObject(data);
                    result = new MyGoalInfoDTO(object);
                    logger.info("GoalInfo JsonObject = " + myGoalInfo.toString());
                    this.myGoalInfo = result;
                } catch (Exception e) {
                }
            }
        }

        if (null == result) {
            return;
        }

        result = this.getUnSyncedActivities(result);

        String myGoal = String.format("%.0f", result.getMyGoal() / 1000);
        double curr = result.getCurGoal() / 1000;
        if (curr < 0) {
            curr = 0;
        }
        String currGoal = String.format("%.0f", curr);
        this.distanceTv.setText(currGoal);
        this.monthlyCountTv.setText(String.valueOf(result.getMonthCount()));
        if (LocaleManager.isDisplayKM(getContext())) {
            monthlySpeedTv.setText(String.format("%.1f", result.getMonthAvgSpeed()));
            this.distanceTv.setText(currGoal);
            this.targetTv.setText(currGoal + "/" + myGoal + "km");
        } else {
            this.monthlySpeedTv.setText(String.format("%.1f", LocaleManager.kphToMph(result.getMonthAvgSpeed())));
            currGoal = String.format("%.0f", LocaleManager.kilometreToMile(curr));
            this.distanceTv.setText(currGoal);
            myGoal = String.format("%.0f", LocaleManager.kilometreToMile(result.getMyGoal() / 1000));
            this.distanceTv.setText(currGoal);
            this.targetTv.setText(currGoal + "/" + myGoal + "mi");
        }



        long h = 0, m = 0, s = 0;
        final long et = (long) result.getMonthTime();
        if (et > 0) {
            h = et / 3600;
            m = et % 3600 / 60;
            s = et % 3600 % 60;
        }
        this.monthlyTimeTv.setText(String.format("%02d:%02d:%02d", h, m, s));

        if (result.getMyGoal() == 0) {
            return;
        }

        String p = String.format("%.0f", Double.parseDouble(currGoal) / Double.parseDouble(myGoal) * 100);
        if (!TextUtils.isDigitsOnly(p)) {
            return;
        }

        final int prece = Integer.valueOf(p);
        if (prece == this.progressBar.getProgress()) {
            return;
        }

        this.progressBar.setProgress(0);
        this.numberProgress.setProgress(0);
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressBar.getProgress() >= prece || progressBar.getProgress() >= 100) {
                            cancel();
                            return;
                        }
                        progressBar.incrementProgressBy(1);
                        numberProgress.incrementProgressBy(1);
                    }
                });
            }
        }, 1000, 50);
    }

    /**
     * 检查未上传记录
     *
     * @param goalInfo
     * @return
     */
    private MyGoalInfoDTO getUnSyncedActivities(MyGoalInfoDTO goalInfo) {
        try {
            if (null == goalInfo) {
                return null;
            }

            List<LocalActivity> activities = activityManager.getUnsyncedActivities(getUserId(), "");
            if (null == activities || activities.size() < 1) {
                return goalInfo;
            }

            for (LocalActivity localActivity : activities) {
                if (null != localActivity && localActivity.getTotalDistance() > 10) {
                    double monthDistance = goalInfo.getCurGoal() + localActivity.getTotalDistance();
                    goalInfo.setCurGoal(monthDistance);
                    double monthTime = goalInfo.getMonthTime() + localActivity.getTotalElapsedTime();
                    goalInfo.setMonthTime(monthTime);
                    double svgSpeed = monthDistance / monthTime * 3.6;
                    goalInfo.setMonthAvgSpeed(svgSpeed);
                    goalInfo.setMonthCount(goalInfo.getMonthCount() + 1);
                }
            }

            return goalInfo;
        } catch (BusinessException e) {
            logger.error("Query local activity unsynced error, " + e);
        }

        return goalInfo;
    }

    /**
     * 开始骑行
     */
    private void startCycling() {
        SpeedxAnalytics.onEvent(getActivity(), "", "click_ridding_start");
        if (!isGpsEnable()) {
            final MaterialDialog dialog = new MaterialDialog(getActivity());
            dialog.setTitle(R.string.activity_no_GPS_title);
            dialog.setMessage(R.string.activity_no_GPS_message);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(intent);
                }
            });
            dialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            LocalActivity activity = activityManager.getCurrentActivity();
            if (null == activity) {
                final Intent intent = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra(ActivityService.EXTRA_ACTION, ActivityService.ACTION_ACTIVITY_START);
                intent.setPackage(getActivity().getPackageName());
                getActivity().startService(intent);
                SpeedxAnalytics.onEvent(getActivity(), getString(R.string.activity_fragment_event_click_start_riding), null);
            } else if (activity.getState() == ActivityState.STATE_AUTO_PAUSED ||
                    activity.getState() == ActivityState.STATE_PAUSED) {
                final Intent intent = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra(ActivityService.EXTRA_ACTION, ActivityService.ACTION_ACTIVITY_RESUME);
                intent.setPackage(getActivity().getPackageName());
                getActivity().startService(intent);
            }

            final Intent cyclingIntent = new Intent(getActivity(), CyclingActivity.class);
            getActivity().startActivity(cyclingIntent);
        }
    }

    /**
     * GPS是否开启
     *
     * @return
     */
    private boolean isGpsEnable() {
        try {
            final LocationManager locationManager = ((LocationManager) this
                    .getActivity().getSystemService(Context.LOCATION_SERVICE));
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    private final class ActivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (null == intent)
                return;

            final String action = intent.getAction();
            if (CyclingTargetSettingActivity.ACTION_TARGET_DISTANCE.equals(action)) {
                getMyGoal();
                refreshView();
            }
            if (null == action)
                return;

            final LocalActivity la = (LocalActivity) intent
                    .getSerializableExtra(ActivityService.EXTRA_ACTIVITY);

            if (null == la)
                return;


            if ((ActivityService.ACTION_ACTIVITY_COMPLETE.equals(action)) && la.getTotalDistance() > 10) {
                getMyGoal();
                refreshView();
            }

        }
    }
}
