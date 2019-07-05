package com.beastbikes.android.ble.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.protocol.v1.DeviceInfoCommandCharacteristic;
import com.beastbikes.android.dialog.Wheelview;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.WheelViewPopupWindow;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.umeng.analytics.MobclickAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@LayoutResource(R.layout.activity_speedx_setting)
public class SpeedXSettingActivity extends SessionFragmentActivity implements View.OnClickListener {
    private static final Logger logger = LoggerFactory.getLogger("SpeedXSettingActivity");
    public static final String EXTRA_DEVICE_INFO = "device_info";

    private static final int REQUEST_CODE_CADENCE_SETTING = 10;
    private static final int REQUEST_CODE_HEART_RATE_SETTING = 11;

    // 轮径设置 B09
    @IdResource(R.id.speed_force_setting_diameter)
    private ViewGroup diameterView;
    private TextView diameterValueTv;
    // 语言设置
    @IdResource(R.id.speed_force_setting_language)
    private ViewGroup languageView;
    private TextView languageValueTv;
    // 里程单位
    @IdResource(R.id.speed_force_setting_mileage_unit)
    private ViewGroup mileageUnitVG;
    private TextView mileageValueTv;
    // 常用踏频
    @IdResource(R.id.speed_force_setting_cadence)
    private ViewGroup cadenceVG;
    private TextView cadenceValueTv;
    // 心率设置
    @IdResource(R.id.speed_force_setting_heart_rate)
    private ViewGroup heartRateVG;
    private TextView heartRateValueTv;
    // 消息通知
    @IdResource(R.id.speed_force_setting_message)
    private ViewGroup messageView;
    private Switch messageSw;
    // 震动唤醒
    @IdResource(R.id.speed_force_setting_vibration_wake)
    private ViewGroup vibrationVG;
    private Switch vibrationSw;

    private Invocation manager;
    private int cadenceIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }


        this.initView();
        DeviceInfoCommandCharacteristic deviceInfo = (DeviceInfoCommandCharacteristic) getIntent().
                getSerializableExtra(EXTRA_DEVICE_INFO);
        if (null != deviceInfo) {
            this.initData(deviceInfo);
            if (CentralSession.isWholeBike(deviceInfo.getHardwareType())) {
                setTitle(R.string.label_bike_setting);
            } else {
                setTitle(R.string.label_central_setting);
            }
        }

        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.setPackage(getPackageName());
        this.bindService(service, connection, BIND_AUTO_CREATE);

        this.getHeartRate();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, getIntent());
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.manager != null) {
            this.unbindService(connection);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.speed_force_setting_diameter:// 轮径设置
                String diameterValue = this.diameterValueTv.getText().toString();
                this.selectDiameterWindow(diameterValue);
                break;
            case R.id.speed_force_setting_language:// 语言设置
                String languageValue = this.languageValueTv.getText().toString();
                this.selectLanguageWindow(languageValue);
                break;
            case R.id.speed_force_setting_mileage_unit:// 设置里程单位
                String mileageUnit = this.mileageValueTv.getText().toString();
                this.selectMileageUnitWindow(mileageUnit);
                break;
            case R.id.speed_force_setting_cadence:// 常用踏频
                Intent intent = new Intent(this, SpeedXCadenceSettingActivity.class);
                intent.putExtra(SpeedXCadenceSettingActivity.EXTRA_SELECT_INDEX, cadenceIndex);
                startActivityForResult(intent, REQUEST_CODE_CADENCE_SETTING);
                break;
            case R.id.speed_force_setting_heart_rate:// 心率极限值
                Intent heartRateIntent = new Intent(this, SpeedXHeartRateSettingActivity.class);
                startActivityForResult(heartRateIntent, REQUEST_CODE_HEART_RATE_SETTING);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_CODE_CADENCE_SETTING:// 踏频设置
                        cadenceIndex = data.getIntExtra(SpeedXCadenceSettingActivity.EXTRA_SELECT_INDEX, cadenceIndex);
                        setCadenceConfig(cadenceIndex);
                        break;
                    case REQUEST_CODE_HEART_RATE_SETTING:// 心率设置
                        int heartRate = data.getIntExtra(SpeedXHeartRateSettingActivity.EXTRA_HEART_RATE_VALUE, 0);
                        this.heartRateValueTv.setText(heartRate + "BPM");
                        break;
                }
                break;
        }
    }

    private void initView() {
        // 轮径
        this.diameterView.setVisibility(View.GONE);
        this.diameterView.setOnClickListener(this);
        TextView diameterLabelTv = (TextView) this.diameterView.findViewById(R.id.speed_force_setting_item_label);
        diameterLabelTv.setText(R.string.label_diameter);
        this.diameterValueTv = (TextView) this.diameterView.findViewById(R.id.speed_force_setting_item_value);

        // 语言
        this.languageView.setOnClickListener(this);
        TextView languageLabelTv = (TextView) this.languageView.findViewById(R.id.speed_force_setting_item_label);
        languageLabelTv.setText(R.string.label_language);
        this.languageValueTv = (TextView) this.languageView.findViewById(R.id.speed_force_setting_item_value);

        // 里程单位
        this.mileageUnitVG.setOnClickListener(this);
        TextView mileageLabelTv = (TextView) this.mileageUnitVG.findViewById(R.id.speed_force_setting_item_label);
        mileageLabelTv.setText(R.string.ble_distance_unit_label);
        this.mileageValueTv = (TextView) this.mileageUnitVG.findViewById(R.id.speed_force_setting_item_value);

        // 常用踏频
        this.cadenceVG.setOnClickListener(this);
        TextView cadenceLabelTv = (TextView) this.cadenceVG.findViewById(R.id.speed_force_setting_item_label);
        this.cadenceValueTv = (TextView) this.cadenceVG.findViewById(R.id.speed_force_setting_item_value);
        cadenceLabelTv.setText(R.string.label_common_cadence);

        // 心率设置
        this.heartRateVG.setOnClickListener(this);
        TextView heartRateLabelTv = (TextView) this.heartRateVG.findViewById(R.id.speed_force_setting_item_label);
        heartRateLabelTv.setText(R.string.label_speedx_max_heart_rate_setting);
        this.heartRateValueTv = (TextView) this.heartRateVG.findViewById(R.id.speed_force_setting_item_value);

        // 消息通知
        TextView messageLabelTv = (TextView) this.messageView.findViewById(R.id.speed_force_setting_switch_label);
        messageLabelTv.setText(R.string.label_message);
        this.messageSw = (Switch) this.messageView.findViewById(R.id.speed_force_setting_switch_value);
        this.messageSw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, final boolean checked) {
                if (manager != null) {
                    manager.writeMessageConfig(checked);
                }
            }
        });
        // 震动唤醒
        TextView vibrationLabelTv = (TextView) this.vibrationVG.findViewById(R.id.speed_force_setting_switch_label);
        vibrationLabelTv.setText(R.string.label_vibration_wake);
        this.vibrationSw = (Switch) this.vibrationVG.findViewById(R.id.speed_force_setting_switch_value);
        this.vibrationSw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (manager != null) {
                    manager.writeVibrationWakeConfig(checked);
                }
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param deviceInfo
     */
    private void initData(DeviceInfoCommandCharacteristic deviceInfo) {
        if (null != deviceInfo) {
            int localIndex = deviceInfo.getLocale();
            String[] local = getResources().getStringArray(R.array.select_language_array);
            if (localIndex >= 0 && localIndex < local.length) {
                this.languageValueTv.setText(local[localIndex]);
            }

            int mileageIndex = deviceInfo.getMileageUnit();
            String[] mileageUnit = getResources().getStringArray(R.array.select_mileage_unit_array);
            if (mileageIndex >= 0 && mileageIndex < mileageUnit.length) {
                this.mileageValueTv.setText(mileageUnit[mileageIndex]);
            }

            int cadence = deviceInfo.getFavouriteCadence();
            int[] cadences = getResources().getIntArray(R.array.select_cadence_value);
            String[] cadenceArray = getResources().getStringArray(R.array.select_cadence_array);
            String[] titles = getResources().getStringArray(R.array.select_cadence_title);
            String title = titles[0];
            String cadenceStr = cadenceArray[0];
            for (int i = 0; i < cadences.length; i++) {
                if (cadences[i] == cadence) {
                    cadenceStr = cadenceArray[i];
                    title = titles[i];
                    cadenceIndex = i;
                }
            }
            this.cadenceValueTv.setText(title + cadenceStr);

            this.messageSw.setChecked(deviceInfo.getNotification() == 1);
            this.vibrationSw.setChecked(deviceInfo.getShakeUp() == 1);

            int hardwareType = deviceInfo.getHardwareType();

            if (!CentralSession.isWholeBike(hardwareType)) {// 中控
                this.diameterView.setVisibility(View.VISIBLE);
                int wheel = deviceInfo.getWheelType();
                int[] values = getResources().getIntArray(R.array.select_wheel_value_array);
                int wheelIndex = 0;
                for (int i = 0; i < values.length; i++) {
                    int value = values[i];
                    if (value == wheel) {
                        wheelIndex = i;
                        break;
                    }
                }
                String[] wheelLabel = getResources().getStringArray(R.array.select_wheel_array);
                if (wheelIndex >= 0 && wheelIndex < wheelLabel.length) {
                    this.diameterValueTv.setText(wheelLabel[wheelIndex]);
                }
            }
        }
    }

    /**
     * 选择轮径
     *
     * @param defaultValue default value
     */
    private void selectDiameterWindow(String defaultValue) {
        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (null == session) {
            Toasts.show(this, getString(R.string.toast_bluetooth_disconnect_try_again));
            return;
        }
        String[] array = getResources().getStringArray(R.array.select_wheel_array);
        if (null == array || array.length <= 0) {
            return;
        }

        final ArrayList<String> list = new ArrayList<>();
        int defaultIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (defaultValue.equals(array[i])) {
                defaultIndex = i;
            }
            list.add(array[i]);
        }
        WheelViewPopupWindow popupWindow = new WheelViewPopupWindow(this, list, defaultIndex,
                new Wheelview.WheelSelectIndexListener() {
                    @Override
                    public void endSelect(int index, String value) {
                        if (!TextUtils.isEmpty(value)) {
                            diameterValueTv.setText(value);
                            int[] values = getResources().getIntArray(R.array.select_wheel_value_array);
                            byte data = (byte) values[0];
                            if (index < values.length) {
                                data = (byte) values[index];
                            }
                            if (manager != null) {
                                boolean isWrite = manager.writeWheel(data);
                                if (isWrite) {
                                    MobclickAgent.onEvent(SpeedXSettingActivity.this, "BLE - 修改轮径");
                                }
                            }
                        }
                    }
                });
        popupWindow.showAtLocation(findViewById(R.id.activity_speedx_setting), Gravity.BOTTOM |
                Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择语言
     *
     * @param defaultValue default value
     */
    private void selectLanguageWindow(String defaultValue) {
        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (null == session) {
            Toasts.show(this, getString(R.string.toast_bluetooth_disconnect_try_again));
            return;
        }
        String[] array = getResources().getStringArray(R.array.select_language_array);
        if (null == array || array.length <= 0) {
            return;
        }

        final ArrayList<String> list = new ArrayList<>();
        int defaultIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (defaultValue.equals(array[i])) {
                defaultIndex = i;
            }
            list.add(array[i]);
        }
        WheelViewPopupWindow popupWindow = new WheelViewPopupWindow(this, list, defaultIndex,
                new Wheelview.WheelSelectIndexListener() {
                    @Override
                    public void endSelect(final int index, String value) {
                        if (!TextUtils.isEmpty(value)) {
                            languageValueTv.setText(value);
                            if (manager != null) {
                                manager.writeLocaleConfig(index);
                            }
                            SpeedxAnalytics.onEvent(SpeedXSettingActivity.this, "BLE - 修改语言", "");
                        }
                    }
                });
        popupWindow.showAtLocation(findViewById(R.id.activity_speedx_setting), Gravity.BOTTOM |
                Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示里程设置选择框
     *
     * @param defaultValue default value
     */
    private void selectMileageUnitWindow(String defaultValue) {
        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (null == session) {
            Toasts.show(this, getString(R.string.toast_bluetooth_disconnect_try_again));
            return;
        }
        String[] array = getResources().getStringArray(R.array.select_mileage_unit_array);
        if (null == array || array.length <= 0) {
            return;
        }

        final ArrayList<String> list = new ArrayList<>();
        int defaultIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (defaultValue.equals(array[i])) {
                defaultIndex = i;
            }
            list.add(array[i]);
        }
        WheelViewPopupWindow popupWindow = new WheelViewPopupWindow(this, list, defaultIndex,
                new Wheelview.WheelSelectIndexListener() {
                    @Override
                    public void endSelect(final int index, String value) {
                        if (!TextUtils.isEmpty(value)) {
                            mileageValueTv.setText(value);
                            if (manager != null) {
                                manager.writeMileageUnitConfig(index);
                            }
                            SpeedxAnalytics.onEvent(SpeedXSettingActivity.this, "BLE - 修改里程单位", "");
                        }
                    }
                });
        popupWindow.showAtLocation(findViewById(R.id.activity_speedx_setting), Gravity.BOTTOM |
                Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择常用踏频
     *
     * @param cadenceIndex cadence value
     */
    private void setCadenceConfig(int cadenceIndex) {
        String[] array = getResources().getStringArray(R.array.select_cadence_array);
        String[] titles = getResources().getStringArray(R.array.select_cadence_title);
        int[] values = getResources().getIntArray(R.array.select_cadence_value);
        if (manager != null && cadenceIndex < array.length) {
            cadenceValueTv.setText(titles[cadenceIndex] + array[cadenceIndex]);
            if (manager.writeCadenceConfig(values[cadenceIndex])) {
                Toasts.show(this, getString(R.string.label_setting_success));
            }
        }
    }

    /**
     * 获取最大心率
     */
    private void getHeartRate() {
        UserManager userManager = new UserManager(this);
        int heartRate = userManager.getUserHeartRate(getUserId());
        this.heartRateValueTv.setText(heartRate + "BPM");
        logger.trace("userId = " + getUserId() + ", heartRate = " + heartRate);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            logger.info("onServiceConnected");
            CentralService mService = ((CentralService.ICentralBinder) binder).getService();
            manager = mService.getInvocation();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logger.info("onServiceDisconnected");
        }
    };
}
