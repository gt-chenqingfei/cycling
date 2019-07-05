package com.beastbikes.android.ble.ui;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.ui.dialog.SpeedxHeartRateSettingDialog;
import com.beastbikes.android.ble.ui.widget.HeartRateIntervalItemView;
import com.beastbikes.android.dialog.Wheelview;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.utils.Utils;
import com.beastbikes.android.widget.WheelViewPopupWindow;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by icedan on 16/10/12.
 */
@MenuResource(R.menu.activity_speedx_heart_rate_menu)
@LayoutResource(R.layout.activity_speedx_heart_rate_setting)
public class SpeedXHeartRateSettingActivity extends SessionFragmentActivity implements View.OnClickListener,
        SpeedxHeartRateSettingDialog.EditTextCommitListener {

    public static final String EXTRA_HEART_RATE_VALUE = "heart_rate_value";
    private final Logger logger = LoggerFactory.getLogger(SpeedXHeartRateSettingActivity.class);

    @IdResource(R.id.heart_rate_recovery_area_view)
    private HeartRateIntervalItemView recoveryView;
    @IdResource(R.id.heart_rate_burning_fat_area_view)
    private HeartRateIntervalItemView burningFatView;
    @IdResource(R.id.heart_rate_target_area_view)
    private HeartRateIntervalItemView targetView;
    @IdResource(R.id.heart_rate_anaerobic_area_view)
    private HeartRateIntervalItemView anaerobicView;
    @IdResource(R.id.heart_rate_limit_area_view)
    private HeartRateIntervalItemView limitView;
    @IdResource(R.id.heart_rate_setting_value_tv)
    private TextView heartRateValueTv;
    @IdResource(R.id.heart_rate_setting_btn)
    private Button heartRateSettingBtn;

    private int heartRate;
    private UserManager userManager;
    private Invocation manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.setPackage(getPackageName());
        this.bindService(service, connection, BIND_AUTO_CREATE);
        this.userManager = new UserManager(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.heartRate = getIntent().getIntExtra(EXTRA_HEART_RATE_VALUE, 0);
        if (heartRate == 0) {
            this.getHeartRate();
        }

        this.heartRateSettingBtn.setOnClickListener(this);
        this.refreshHeartRateIntervalView(heartRate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != manager) {
            unbindService(connection);
        }
    }

    @Override
    public void finish() {
        getIntent().putExtra(EXTRA_HEART_RATE_VALUE, heartRate);
        setResult(RESULT_OK, getIntent());
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_heart_rate_help:
                final Intent browserIntent = new Intent(this, BrowserActivity.class);
                browserIntent.setData(Uri.parse("https://hybrid.speedx.com/hr-notice"));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.heart_rate_setting_btn:// 自定义心率设置
                String DIALOG_FRAGMENT_TAG = "dialog_fragment";
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment fragment = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
                if (null != fragment) {
                    return;
                }
                SpeedxHeartRateSettingDialog dialog = new SpeedxHeartRateSettingDialog();
                dialog.setCommitListener(this);
                fragmentTransaction.add(dialog, DIALOG_FRAGMENT_TAG).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public void onHeartRateValue(int value) {
        if (value == -1) {
            this.showSelectAgeWindow();
            return;
        }

        this.heartRate = value;
        this.refreshHeartRateIntervalView(value);
        this.updateHeartRate(heartRate);
    }

    /**
     * 获取最大心率
     */
    private void getHeartRate() {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {
            @Override
            protected ProfileDTO doInBackground(String... params) {
                UserManager userManager = new UserManager(SpeedXHeartRateSettingActivity.this);
                try {
                    return userManager.getProfileByUserId(getUserId());
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ProfileDTO profileDTO) {
                if (null != profileDTO) {
                    heartRate = profileDTO.getMaxHeartRate();
                    if (heartRate > 0) {
                        refreshHeartRateIntervalView(heartRate);
                    } else {
                        String[] split = profileDTO.getBirthday().split("-");
                        int year = Utils.numericFilter(split[0]);
                        if (year > 0) {
                            Calendar calendar = Calendar.getInstance();
                            heartRate = getHeartRateByAge(calendar.get(Calendar.YEAR) - year);
                            refreshHeartRateIntervalView(heartRate);
                        }
                    }
                }
            }
        });
    }

    /**
     * 根据年龄计算心率值
     *
     * @param age age
     * @return
     */
    private int getHeartRateByAge(int age) {
        if (age < 1) {
            return 205;
        }
        return 205 - age / 2;
    }

    /**
     * 显示通过选择年龄计算心率的选择框
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showSelectAgeWindow() {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        String title = getString(R.string.title_heart_rate_setting_by_age);
        final ArrayList<String> ages = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ages.add(String.valueOf(i));
        }

        WheelViewPopupWindow ageWindow = new WheelViewPopupWindow(this, title, ages, 23,
                new Wheelview.WheelSelectIndexListener() {
                    @Override
                    public void endSelect(int index, String value) {
                        heartRate = getHeartRateByAge(Integer.valueOf(value));
                        refreshHeartRateIntervalView(heartRate);
                        updateHeartRate(heartRate);
                    }
                });
        ageWindow.showAtLocation(findViewById(R.id.activity_speedx_heart_rate_setting), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 刷新心率区间信息
     *
     * @param heartRate heart rate
     */
    private void refreshHeartRateIntervalView(int heartRate) {
        this.heartRateValueTv.setVisibility(View.VISIBLE);
        this.heartRateValueTv.setText(String.valueOf(heartRate));
        String recovery = getString(R.string.label_heart_rate_recovery_default);
        String burningFat = getString(R.string.label_heart_rate_burning_fat_default);
        String target = getString(R.string.label_heart_rate_target_default);
        String anaerobic = getString(R.string.label_heart_rate_anaerobic_default);
        String limit = getString(R.string.label_heart_rate_limit_default);
        if (heartRate > 0) {
            recovery = (int) Math.ceil((heartRate * 0.5)) + "-" + (int) Math.ceil((heartRate * 0.6)) + "bpm";
            burningFat = ((int) Math.ceil((heartRate * 0.6)) + 1) + "-" + (int) Math.ceil((heartRate * 0.7)) + "bpm";
            target = ((int) Math.ceil((heartRate * 0.7)) + 1) + "-" + (int) Math.ceil((heartRate * 0.8)) + "bpm";
            anaerobic = ((int) Math.ceil((heartRate * 0.8)) + 1) + "-" + (int) Math.ceil((heartRate * 0.9)) + "bpm";
            limit = ((int) Math.ceil((heartRate * 0.9)) + 1) + "-" + heartRate + "bpm";
        }

        this.recoveryView.setHeartRateValue(recovery);
        this.burningFatView.setHeartRateValue(burningFat);
        this.targetView.setHeartRateValue(target);
        this.anaerobicView.setHeartRateValue(anaerobic);
        this.limitView.setHeartRateValue(limit);
    }

    /**
     * 上传最大心率
     *
     * @param heartRate 心率
     */
    private void updateHeartRate(final int heartRate) {
        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (null == session) {
            Toasts.show(this, getString(R.string.toast_bluetooth_disconnect_try_again));
            return;
        }

        getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return userManager.updateUserHeartRate(getUserId(), heartRate);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result) {
                    Toasts.show(SpeedXHeartRateSettingActivity.this, getString(R.string.network_not_awesome));
                    return;
                }

                if (null != manager) {
                    boolean isWrite = manager.writeMaxHeartRateConfig(heartRate);
                    logger.info("写入心率信息: 心率值 = " + heartRate + ", 结果:" + isWrite);
                }
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            CentralService mService = ((CentralService.ICentralBinder) binder).getService();
            manager = mService.getInvocation();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

}
