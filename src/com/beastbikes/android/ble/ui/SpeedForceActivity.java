package com.beastbikes.android.ble.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.BleManager;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.listener.IScanResultListener;
import com.beastbikes.android.ble.biz.listener.OnAGPSListener;
import com.beastbikes.android.ble.biz.listener.OnOtaCheckSumListener;
import com.beastbikes.android.ble.biz.listener.OnPreviewCyclingListener;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.biz.listener.ResponseDeviceInfoListener;
import com.beastbikes.android.ble.biz.listener.ResponseMacAddressListener;
import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.ble.dto.BleCyclingDTO;
import com.beastbikes.android.ble.protocol.v1.AGpsInfoCharacteristic;
import com.beastbikes.android.ble.protocol.v1.BatterySensorCharacteristic;
import com.beastbikes.android.ble.protocol.v1.DeviceInfoCommandCharacteristic;
import com.beastbikes.android.ble.protocol.v1.OTAFirmwareInfoCharacteristic;
import com.beastbikes.android.ble.ui.dialog.BleActiveFailedDialog;
import com.beastbikes.android.ble.ui.dialog.BleBindDialog;
import com.beastbikes.android.ble.ui.dialog.BleConnectTipDialog;
import com.beastbikes.android.ble.ui.dialog.BleDeviceActiveDialog;
import com.beastbikes.android.ble.ui.dialog.BlePairTipDialog;
import com.beastbikes.android.ble.ui.dialog.MultiDeviceSelectPW;
import com.beastbikes.android.ble.ui.dialog.SpeedXDialogFragment;
import com.beastbikes.android.ble.ui.widget.BatteryView;
import com.beastbikes.android.ble.ui.widget.SpeedForceSettingView;
import com.beastbikes.android.ble.ui.widget.TextViewWithBoardAndCorners;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dto.MyGoalInfoDTO;
import com.beastbikes.android.modules.user.biz.AccountBindManager;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.modules.user.ui.CyclingRecordActivity;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 我的设备
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
@MenuResource(R.menu.activity_speedx_force_menu)
@LayoutResource(R.layout.activity_speedx_force)
public class SpeedForceActivity extends SessionFragmentActivity implements View.OnClickListener,
        ResponseDeviceInfoListener, OnAGPSListener, OnPreviewCyclingListener, OnOtaCheckSumListener,
        OnUpdateDataListener, ResponseMacAddressListener, IScanResultListener, ServiceConnection {

    public static final int RESULT_DEVICE_DISCONNECT_CODE = 82;

    private static final int REQUEST_AGPS_UPDATE_CODE = 83;
    private static final int REQUEST_SETTING_CODE = 84;
    private static final int REQUEST_OTA_UPDATE_CODE = 85;
    private static final int REQUEST_BLUE_ENABLE_CODE = 88;
    private static final int REQUEST_SYNC_ACTIVITY_CODE = 89;

    private static final int MSG_PAIR_DIALOG_WHAT = 1;
    private static final String TAG_SYNC_DATA_FRAGMENT = "SYNC_DATA";
    private Logger logger = LoggerFactory.getLogger("SpeedForceActivity");

    @IdResource(R.id.toolbar)
    private Toolbar mToolbar;

    @IdResource(R.id.textView_speedx_force_title)
    private TextView mTextViewTitle;

    //there are data need to sync
    @IdResource(R.id.activity_speedx_force_sync_rela)
    private RelativeLayout mSyncRela;
    @IdResource(R.id.activity_speedx_force_unsync_msg)
    private TextView mSyncMsg;

    //battery
    @IdResource(R.id.activity_speedx_force_battery_linear)
    private LinearLayout mLinearBattery;
    @IdResource(R.id.activity_speedx_force_battery_electricity_icon)
    private BatteryView mBatteryIcon;
    @IdResource(R.id.activity_speedx_force_battery_electricity)
    private TextView mTextViewBattery;

    @IdResource(R.id.activity_speedx_force_device_logo)
    private ImageView mImgLogo;

    //车型名称
    @IdResource(R.id.activity_speedx_force_device_type)
    private TextView mTextViewType;
    @IdResource(R.id.activity_speedx_force_device_type_img)
    private ImageView mImgType;

    @IdResource(R.id.activity_speedx_force_connect_to_bike)
    private TextViewWithBoardAndCorners mTextViewConnectToBike;

    //骑行里程
    @IdResource(R.id.speedx_force_total_distance)
    private ViewGroup mCyclingTotalDistance;
    private TextView mTextTotalDistance;
    //骑行时间
    @IdResource(R.id.speedx_force_total_time)
    private ViewGroup mCyclingTotalTime;
    private TextView mTextTotalTime;
    //骑行次数
    @IdResource(R.id.speedx_force_total_count)
    private ViewGroup mCyclingTotalCount;
    private TextView mTextTotalCount;

    //骑行历史
    @IdResource(R.id.speedx_force_cycling_history)
    private SpeedForceSettingView mCyclingHistory;

    //售后服务
    @IdResource(R.id.speedx_force_service_location)
    private SpeedForceSettingView mServiceLocation;

    //车辆设置
    @IdResource(R.id.speedx_force_bikes_settings)
    private SpeedForceSettingView mBikesSettings;

    //固件升级
    @IdResource(R.id.speedx_force_ota_version)
    private SpeedForceSettingView mOTAVersion;

    //定位文件
    @IdResource(R.id.speedx_force_gps_version)
    private SpeedForceSettingView mGPSVersion;

    //解除绑定
    @IdResource(R.id.activity_speedx_force_unbind_bike)
    private Button mBtnUnbindBike;

    private Invocation manager;
    private BleManager bleManager;
    // 设备类型
    private int hardwareType = 1;
    // 充电状态
    private int charge;
    // 电量
    private int battery = 100;
    // OTA版本信息
    private OTAFirmwareInfoCharacteristic otaFirmwareInfo;
    // 设备信息
    private DeviceInfoCommandCharacteristic deviceInfo;
    private SpeedXDialogFragment dialogFragment;
    private boolean onPause;
    // 当前设备的
    private BleDevice currentDevice;

    private LoadingDialog loadingDialog;
    private BleConnectTipDialog bleConnectTipDialog;
    //设备列表
    private List<BleDevice> mBleDevices;

    //设备列表View
    private MultiDeviceSelectPW multiDeviceSelectPW;
    private boolean bonded;
    // 是否有新的GPS文件需要更新
    private boolean hasNewGpsVersion;
    // 是否有新的固件需要升级
    private boolean hasNewOtaVersion;
    private PairHandler pairHandler;
    private boolean hasShowDialog;
    private CentralService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.bleManager = new BleManager(this);
        this.registerBleReceiver();
        this.initView();
        this.checkBlueState();
        this.pairHandler = new PairHandler(this);

        this.mBleDevices = new ArrayList<>();
        this.getDeviceList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.onPause = false;
        this.dismissSyncDataFragment();
        if (this.mService != null) {
            this.mService.setOnScanResultListener(this);
            this.manager.setUpdateListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.onPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != this.pairHandler) {
            this.pairHandler.removeMessages(MSG_PAIR_DIALOG_WHAT);
        }
        if (!CentralSessionHandler.getInstance().hasConnected()) {
            final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
            service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_STOP_SCAN);
            service.setPackage(getPackageName());
            this.startService(service);
            this.stopService(service);
        }

        if (mService != null) {
            mService.setOnScanResultListener(null);
            this.unbindService(this);
            this.manager.setUpdateListener(null);
            this.unRegisterBleReceiver();
            this.currentDevice = null;
            this.manager.setResponseDeviceInfoListener(null);
            this.manager.setResponseMacAddressListener(null);
            this.manager.setPreviewCyclingListener(null);
            this.manager.setAGPSListener(null);
            this.manager.setCheckSumListener(null);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.speedx_force_add:
                startActivity(new Intent(this, DiscoveryActivity.class));
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BLUE_ENABLE_CODE:// 开启蓝牙
                if (resultCode == RESULT_OK) {
                    final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
                    service.setPackage(getPackageName());
                    this.startService(service);
                }
                break;
            case REQUEST_AGPS_UPDATE_CODE:// apgs 升级返回
                if (resultCode == RESULT_OK) {
                    this.checkGpsVersionView(false);
                }

                if (resultCode == RESULT_DEVICE_DISCONNECT_CODE) {
                    Toasts.show(this, R.string.msg_device_disconnect);
                }
                break;
            case REQUEST_SETTING_CODE:// 设置返回
                if (resultCode == RESULT_OK) {
                    if (manager != null) {
                        manager.writeDeviceInfoRequest();
                    }
                }
                break;
            case REQUEST_OTA_UPDATE_CODE:// ota 升级返回
                if (resultCode == RESULT_DEVICE_DISCONNECT_CODE) {
                    Toasts.show(this, R.string.msg_device_disconnect);
                }
                break;
            case REQUEST_SYNC_ACTIVITY_CODE:// 同步数据回调
                if (resultCode == RESULT_OK) {
                    boolean syncSuccess = data.getBooleanExtra(CyclingRecordActivity.EXTRA_SYNC_ACTIVITY, false);
                    if (syncSuccess) {
                        this.mSyncRela.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onResponseBatterySensor(BatterySensorCharacteristic characteristic) {
        if (null != characteristic) {
            // 电量信息
            this.mBatteryIcon.setVisibility(View.VISIBLE);
            this.battery = characteristic.getPercentage();
            this.mBatteryIcon.setPower(battery);
            this.charge = characteristic.getChargeState();
            this.mBatteryIcon.setCharging(charge == 1);
            this.mTextViewBattery.setVisibility(View.VISIBLE);
            this.mTextViewBattery.setText(String.format("%d%%", battery));
        }
    }

    @Override
    public void onResponseDevice(BleDevice bleDevice, DeviceInfoCommandCharacteristic deviceInfo) {
        if (bleDevice != null) {
            currentDevice = bleDevice;

            this.deviceInfo = deviceInfo;
            // 电量信息
            this.mBatteryIcon.setVisibility(View.VISIBLE);
            this.battery = deviceInfo.getBattery();
            this.mBatteryIcon.setPower(battery);
            this.mTextViewBattery.setVisibility(View.VISIBLE);
            this.mTextViewBattery.setText(String.format("%d%%", battery));
            // 车辆名称
            String bikeName = BleDevice.brandType2Name(deviceInfo.getBrandType());
            this.mTextViewType.setText(bikeName);
            // 车型
            this.hardwareType = deviceInfo.getHardwareType();
            String address = CentralSession.address2CentralId(currentDevice.getDeviceId());
            this.fetchBleCycling(address);

            if (CentralSession.isWholeBike(this.hardwareType)) {   //中控
                this.onBikeConnected();
            } else {                        //整车
                this.onCentralConnected();
            }

            if (!TextUtils.isEmpty(bleDevice.getUrl())) {
                Picasso.with(this).load(bleDevice.getUrl() + "")
                        .error(R.drawable.ic_speedx_force_bike_normal_logo)
                        .placeholder(R.drawable.ic_speedx_force_bike_normal_logo)
                        .into(mImgLogo);
            }
            if (!mBleDevices.contains(bleDevice)) {
                mBleDevices.add(0, bleDevice);

                if (mBleDevices.size() > 1) {
                    mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_white, 0);
                } else {
                    mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            if (TextUtils.isEmpty(bleDevice.getFrameId()) && hardwareType != 0 && hardwareType != 2) {
                mTextViewType.setText(R.string.speed_force_complete_device_info);
            }
        }
    }

    @Override
    public void onAPGSInfoResponse(AGpsInfoCharacteristic characteristic) {
        if (null != characteristic) {
            int updateTime = characteristic.getUpdateTime();
            long current = System.currentTimeMillis() / 1000;
            long dayCount = ((System.currentTimeMillis() / 1000) - updateTime) / (3600 * 24);
            logger.info("Update A_GPS file, Days =  ：" + dayCount + ", UpdateTime = " + updateTime
                    + ",current = " + current);
            this.hasNewGpsVersion = dayCount >= 12;
            this.checkGpsVersionView(hasNewGpsVersion);
        }
    }

    @Override
    public void onSyncAGPSStart() {
        this.showAGPSDialog();
    }

    @Override
    public void onSyncing() {
        if (currentDevice == null)
            return;
        CentralSession session = CentralSessionHandler.getInstance().
                sessionMatch(currentDevice.getMacAddress());
        if (session != null) {
            if (!session.getProperty().isCancelUpdate() && !onPause) {
                this.showAGPSDialog();
            }
        }
    }

    @Override
    public void onPreviewEnd() {
        if (manager != null) {
            boolean write = this.manager.writeCheckSumRequest();
            logger.error("Preview ble cycling data end, and request ble ota checksum is " + write);
            this.handleSyncRelaState();
        }
    }

    @Override
    public void onPreviewError() {
        if (manager != null) {
            boolean write = this.manager.writeCheckSumRequest();
            logger.error("预览数据错误:" + write);
            this.handleSyncRelaState();
        }

    }

    @Override
    public void onOtaCheckSumResponse(OTAFirmwareInfoCharacteristic characteristic) {
        if (null != characteristic) {
            this.otaFirmwareInfo = characteristic;
            this.setCheckOtaCountVersion(characteristic);
        } else {
            logger.error("OTA固件版本信息解析失败");
        }

        this.writeTargetConfig();
    }

    @Override
    public void onUpdateSuccess(int type) {
        switch (type) {
            case 0:// 同步数据完成
                this.mSyncRela.setVisibility(View.GONE);
                break;
            case 1:// AGPS文件更新完成
                this.hasNewGpsVersion = false;
                this.checkGpsVersionView(false);
                break;
            case 2:// OTA升级完成
                this.hasNewOtaVersion = false;
                this.checkOtaVersionView(false);
                break;
        }
    }

    @Override
    public void onUpdateCanceled(int type) {// 取消升级
        switch (type) {
            case 0:// 取消同步数据
                break;
            case 1:// 取消AGPS文件更新
                if (manager != null) {
                    this.manager.writeActivityPreviewRequest();
                }
                break;
            case 2:// 取消OTA升级
                break;
        }
    }

    @Override
    public void onResponseMacAddress(String macAddress, float totalDistance) {
        logger.trace("onResponseMacAddress macAddress :" + macAddress
                + ", totalDistance: " + totalDistance);
        this.syncDeviceTotalDistance(macAddress, totalDistance);
        this.checkControlActive(macAddress);
    }

    /**
     * 同步设备的总里程
     */
    private void syncDeviceTotalDistance(final String controlNo, final float totalDistance) {
        if (totalDistance <= 0 || TextUtils.isEmpty(controlNo))
            return;
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                bleManager.syncDeviceTotalDistance(controlNo, totalDistance);
                return null;
            }
        });
    }

    /**
     * 检测首保是否激活
     *
     * @param controlNo controlNo
     */
    private void checkControlActive(final String controlNo) {
        if (TextUtils.isEmpty(controlNo))
            return;

        final BleManager bleManager = new BleManager(this);

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Map<String, Boolean>>() {

            @Override
            protected Map<String, Boolean> doInBackground(String... params) {
                try {
                    boolean isActive = false;
                    Map<String, Boolean> map = new HashMap<>();
                    Map ret = bleManager.checkCControlActive(controlNo);
                    if (ret != null) {
                        isActive = (Boolean) ret.get("isActive");
                    }
                    map.put("isActive", isActive);
                    return map;

                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Map<String, Boolean> ret) {
                super.onPostExecute(ret);
                boolean isActive = ret.get("isActive");

                CentralSession session = CentralSessionHandler.getInstance().sessionMatch(controlNo);
                int guaranteeTime = 0;
                if (session != null) {
                    guaranteeTime = session.getProperty().getGuaranteeTime();
                }
                if (!isActive && guaranteeTime > 0) {
                    // 未激活 并且已获得 5km激活条件
                    String brandType = "";

                    BluetoothDevice device = session.getBluetoothDevice();
                    BleDevice dev = bleManager.getBleDevice(device.getAddress(), getUserId());
                    if (dev != null) {
                        brandType = BleDevice.brandType2String(dev.getBrandType());
                    }

                    BleDeviceActiveDialog dialog = new BleDeviceActiveDialog(
                            SpeedForceActivity.this, brandType, controlNo,
                            new BleDeviceActiveDialog.OnClickListener() {
                                @Override
                                public void onClickOk() {
                                    doActive(controlNo);
                                }
                            });
                    dialog.show();
                }
            }
        });
    }

    /**
     * 检测首保是否激活及跳转
     *
     * @param controlNo controlNo
     */
    private void checkControlActiveAndroidStart(final String controlNo) {
        final BleManager bleManager = new BleManager(this);
        loadingDialog = new LoadingDialog(SpeedForceActivity.this, "", true);
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, Map<String, Object>>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (!loadingDialog.isShowing()) {
                            loadingDialog.show();
                        }
                    }

                    @Override
                    protected Map<String, Object> doInBackground(String... params) {
                        try {
                            Map<String, Object> map = new HashMap<>();
                            boolean isBind = false;

                            Map ret = bleManager.checkCControlActive(controlNo);
                            map.put("isActive", ret.get("isActive"));
                            map.put("owner", ret.get("owner"));
                            AVUser user = AVUser.getCurrentUser();
                            if (user != null) {
                                if (user.getSignType() == AuthenticationFactory.TYPE_WEIXIN) {
                                    AccountBindManager accountBindManager =
                                            new AccountBindManager(SpeedForceActivity.this);
                                    List<AccountDTO> bindList = accountBindManager.bindStatus();
                                    for (int i = 0; i < bindList.size(); i++) {
                                        AccountDTO dto = bindList.get(i);
                                        if (dto.getAuthType() == AuthenticationFactory.
                                                TYPE_MOBILE_PHONE ||
                                                dto.getAuthType() == AuthenticationFactory.TYPE_EMAIL) {
                                            isBind = dto.getStatus() == AccountDTO.STATUS_BOND;
                                            if (isBind)
                                                break;
                                        }
                                    }
                                } else {
                                    isBind = true;
                                }
                            }

                            map.put("isBind", isBind);
                            return map;
                        } catch (BusinessException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Map<String, Object> ret) {
                        super.onPostExecute(ret);
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        final boolean isActive = (Boolean) ret.get("isActive");
                        final boolean isBind = (Boolean) ret.get("isBind");
                        int owner = 0;
                        if (ret.get("owner") != null) {
                            owner = Integer.valueOf(ret.get("owner").toString());
                        }
                        if (!isActive) {

                            if (!isConnectedDevice(currentDevice)) {
                                Toasts.show(SpeedForceActivity.this, R.string.label_unconnected);
                                return;
                            }

                            String brandType = "";
                            CentralSession session = CentralSessionHandler.getInstance().sessionMatch(controlNo);
                            if (session != null) {
                                BluetoothDevice device = session.getBluetoothDevice();
                                BleDevice dev = bleManager.getBleDevice(device.getAddress(), getUserId());
                                if (dev != null) {
                                    brandType = BleDevice.brandType2String(dev.getBrandType());
                                }
                            }
                            BleDeviceActiveDialog dialog = new BleDeviceActiveDialog(SpeedForceActivity.this, brandType, controlNo, new BleDeviceActiveDialog.OnClickListener() {
                                @Override
                                public void onClickOk() {
                                    doActive(controlNo, isBind);
                                }
                            });
                            dialog.show();

                        } else {
                            AVUser user = AVUser.getCurrentUser();
                            if (null == user) {
                                return;
                            }
                            if (owner != user.getSpeedxId()) {
                                Toasts.show(SpeedForceActivity.this, R.string.dialog_ble_active_switch_tip);
                                return;
                            }

                            if (isBind) {
                                final Intent browserIntent = new Intent(SpeedForceActivity.this, BrowserActivity.class);
                                browserIntent.setData(Uri.parse(Constants.UrlConfig.DEV_SPEEDX_PRODUCTS_URL));
                                startActivity(browserIntent);
                            } else {
                                BleBindDialog dialog = new BleBindDialog(SpeedForceActivity.this);
                                dialog.show();
                            }
                        }
                    }
                }

        );
    }

    /**
     * 激活设备
     *
     * @param controlNo bluetooth device send notification mac address
     */
    private void doActive(final String controlNo) {
        if (isDestroyed() || isFinishing()) {
            return;
        }

        final LoadingDialog loadingDialog = new LoadingDialog(SpeedForceActivity.this,
                getString(R.string.loading_msg), true);
        loadingDialog.show();

        getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return new BleManager(SpeedForceActivity.this).activeCControl(controlNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean ret) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (ret) {
                    Toasts.show(SpeedForceActivity.this, R.string.dialog_ble_active_success);
                } else {
                    new BleActiveFailedDialog(SpeedForceActivity.this,
                            getString(R.string.dialog_ble_active_failed_tip)).show();
                }
            }
        });
    }

    /**
     * 激活设备并跳转保修页面
     *
     * @param controlNo controlNo
     * @param isBind    isBind
     */
    private void doActive(final String controlNo, final boolean isBind) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        final LoadingDialog loadingDialog = new LoadingDialog(SpeedForceActivity.this,
                getString(R.string.loading_msg), true);
        loadingDialog.show();

        getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return new BleManager(SpeedForceActivity.this).activeCControl(controlNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean ret) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (ret) {
                    Toasts.show(SpeedForceActivity.this, R.string.dialog_ble_active_success);
                    if (!isBind) {
                        BleBindDialog dialog = new BleBindDialog(SpeedForceActivity.this);
                        dialog.show();
                    } else {
                        final Intent browserIntent = new Intent(SpeedForceActivity.this, BrowserActivity.class);
                        browserIntent.setData(Uri.parse(Constants.UrlConfig.DEV_SPEEDX_PRODUCTS_URL));
                        startActivity(browserIntent);
                    }
                } else {
                    new BleActiveFailedDialog(SpeedForceActivity.this,
                            getString(R.string.dialog_ble_active_failed_tip)).show();
                }
            }
        });
    }

    /**
     * initiate view status
     */
    private void initView() {

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //骑行里程
        TextView mTextTotalDistanceLabel = (TextView) mCyclingTotalDistance.findViewById(R.id.speedx_force_data_item_label);
        mTextTotalDistance = (TextView) mCyclingTotalDistance.findViewById(R.id.speedx_force_data_item_value);
        if (LocaleManager.isDisplayKM(this)) {
            mTextTotalDistanceLabel.setText(getResources().getString(R.string.label_ongoing_distance_unit) + LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance);
        } else {
            mTextTotalDistanceLabel.setText(getResources().getString(R.string.label_ongoing_distance_unit) + LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance_mi);
        }
        mTextTotalDistance.setText("0.0");

        //骑行时间
        TextView mTextTotalTimeLabel = (TextView) mCyclingTotalTime.findViewById(R.id.speedx_force_data_item_label);
        mTextTotalTime = (TextView) mCyclingTotalTime.findViewById(R.id.speedx_force_data_item_value);
        mTextTotalTimeLabel.setText(R.string.label_cycling_time);
        mTextTotalTime.setText("0.0");

        //骑行次数
        TextView mTextTotalCountLabel = (TextView) mCyclingTotalCount.findViewById(R.id.speedx_force_data_item_label);
        mTextTotalCount = (TextView) mCyclingTotalCount.findViewById(R.id.speedx_force_data_item_value);
        mTextTotalCountLabel.setText(R.string.label_cycling_times);
        mTextTotalCount.setText("0");

        this.setOnClickListener();

        mLinearBattery.setVisibility(View.VISIBLE);
        mBatteryIcon.setCharging(false);

        onNoDeviceConnected(-1);
    }

    private void setOnClickListener() {

        mTextViewTitle.setOnClickListener(this);
        mSyncRela.setOnClickListener(this);

        mTextViewType.setOnClickListener(this);
        mImgType.setOnClickListener(this);

        mTextViewConnectToBike.setOnClickListener(this);

        mCyclingHistory.setOnClickListener(this);
        mServiceLocation.setOnClickListener(this);
        mBikesSettings.setOnClickListener(this);
        mOTAVersion.setOnClickListener(this);
        mGPSVersion.setOnClickListener(this);
        mBtnUnbindBike.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_speedx_force_title:
                showDeviceList();
                break;
            case R.id.activity_speedx_force_sync_rela:// 同步数据
                this.startSyncData();
                break;
            case R.id.activity_speedx_force_device_type:
            case R.id.activity_speedx_force_device_type_img:
                if (null != currentDevice && (currentDevice.getHardwareType() == 0 ||
                        currentDevice.getHardwareType() == 2)) {
                    break;
                }
                final Intent browserIntent = new Intent(SpeedForceActivity.this, BrowserActivity.class);
                browserIntent.setData(Uri.parse(Constants.UrlConfig.DEV_SPEEDX_PRODUCTS_URL));
                startActivity(browserIntent);
                break;

            case R.id.activity_speedx_force_connect_to_bike:

                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (null == adapter) {
                    break;
                }
                if (!adapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(enableBtIntent, REQUEST_BLUE_ENABLE_CODE);
                    break;
                }

                connectDevice();

                break;

            case R.id.speedx_force_cycling_history:
                final Intent it = new Intent(this, CyclingRecordActivity.class);
                it.putExtra(CyclingRecordActivity.EXTRA_USER_ID, AVUser.getCurrentUser().getObjectId());
                it.putExtra(CyclingRecordActivity.EXTRA_AVATAR_URL, AVUser.getCurrentUser().getAvatar());
                it.putExtra(CyclingRecordActivity.EXTRA_NICK_NAME, AVUser.getCurrentUser().getDisplayName());
                it.putExtra(CyclingRecordActivity.EXTRA_REFRESH, true);
                if (currentDevice != null) {
                    it.putExtra(CyclingRecordActivity.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
                    it.putExtra(CyclingRecordActivity.EXTRA_DEVICE_NAME, currentDevice.getDeviceName());
                }
                startActivityForResult(it, REQUEST_SYNC_ACTIVITY_CODE);
                break;

            case R.id.speedx_force_service_location:/** 售后服务（当前显示设备）*/
                if (currentDevice != null) {
                    this.checkControlActiveAndroidStart(this.currentDevice.getMacAddress());
                }
                break;
            case R.id.speedx_force_bikes_settings:// 车辆设置
                if (!mBikesSettings.ismEnable()) {
                    return;
                }
                CentralSession session = null;
                if (currentDevice != null) {
                    session = CentralSessionHandler.getInstance().sessionMatch(currentDevice.getMacAddress());
                    if (deviceInfo == null) {
                        deviceInfo = session.getProperty().getDeviceInfo();
                    }
                }
                if (null != deviceInfo) {
                    final Intent settingIntent = new Intent(this, SpeedXSettingActivity.class);
                    settingIntent.putExtra(SpeedXSettingActivity.EXTRA_DEVICE_INFO, deviceInfo);
                    this.startActivityForResult(settingIntent, REQUEST_SETTING_CODE);
                }
                break;
            case R.id.speedx_force_ota_version:// 固件升级
                if (!mOTAVersion.ismEnable()) {
                    return;
                }

                if (!isConnectedDevice(currentDevice) || null == otaFirmwareInfo) {
                    Toasts.show(this, R.string.label_unconnected);
                    return;
                }

                if (battery < 30 && (charge == 0)) {
                    this.showLittleBattery();
                    return;
                }

                this.showOtaUpdateDialog();
                break;
            case R.id.speedx_force_gps_version:// A_GPS文件更新
                if (!mGPSVersion.ismEnable()) {
                    return;
                }
                Intent intent = new Intent(this, SpeedXGpsUpdateActivity.class);
                if (currentDevice != null) {
                    intent.putExtra(SpeedXGpsUpdateActivity.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
                }
                this.startActivityForResult(intent,
                        REQUEST_AGPS_UPDATE_CODE);
                break;
            case R.id.activity_speedx_force_unbind_bike:// 解绑设备
                this.unBoundBike();
                break;
        }
    }

    private void connectDevice() {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        if (currentDevice != null) {
            boolean isConnect = CentralSessionHandler.getInstance().isConnected(currentDevice.getMacAddress());
            if (!isConnect) {
                final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
                service.setPackage(getPackageName());
                service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_SCAN_AND_CONNECT);
                service.putExtra(CentralService.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
                this.startService(service);
                if (mService == null) {
                    this.bindService(service, this, BIND_AUTO_CREATE);
                }
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(SpeedForceActivity.this, "", true);
                }
                loadingDialog.show(30 * 1000, getString(R.string.dialog_ble_connect_fail));
            } else {
                logger.warn("currentSession is connected !!");
            }
        } else {
            logger.error("currentDevice is null !!");
        }
    }

    private boolean isConnectedDevice(BleDevice device) {
        if (device == null)
            return false;
        if (TextUtils.isEmpty(device.getMacAddress())) {
            return false;
        }
        CentralSession session = CentralSessionHandler.getInstance().
                sessionMatch(device.getMacAddress());
        if (session != null) {
            return session.getState() == CentralSession.SESSION_STATE_DISCOVERED;
        }
        return false;
    }

    /**
     * fired when some device was connected
     */
    private void onDeviceConnected() {
        mTextViewConnectToBike.setText(R.string.speed_force_activity_connected);
        mTextViewConnectToBike.setTextColor(Color.parseColor("#0185ff"));
        mTextViewConnectToBike.setBoardColor(Color.parseColor("#0185ff"));
        mTextViewConnectToBike.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_speedx_force_connected_icon, 0, 0, 0);
        mTextViewConnectToBike.setOnClickListener(null);

        mBikesSettings.setmEnable(true);
        mOTAVersion.setmEnable(true);
        this.checkOtaVersionView(hasNewOtaVersion);
        mGPSVersion.setmEnable(true);
        this.checkGpsVersionView(hasNewGpsVersion);

        if (null != currentDevice && !TextUtils.isEmpty(currentDevice.getUrl())) {
            Picasso.with(this).load(currentDevice.getUrl() + "").error(
                    R.drawable.ic_speedx_force_bike_normal_logo).placeholder(
                    R.drawable.ic_speedx_force_bike_normal_logo).into(mImgLogo);
        }

        if (currentDevice != null) {
            final int hardwareType = currentDevice.getHardwareType();
            if (TextUtils.isEmpty(currentDevice.getFrameId()) &&
                    hardwareType != 0 && hardwareType != 2) {
                mTextViewType.setText(R.string.speed_force_complete_device_info);
            } else {
                String bikeName = BleDevice.brandType2Name(currentDevice.getBrandType());
                mTextViewType.setText(bikeName);
            }

            CentralSession session = CentralSessionHandler.getInstance().
                    sessionMatch(currentDevice.getMacAddress());
            if (session != null) {
                BluetoothDevice device = session.getBluetoothDevice();
                if (null != device && device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    mBtnUnbindBike.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 更新A_GPS view
     */
    private void checkGpsVersionView(boolean hasNewGpsVersion) {
        if (hasNewGpsVersion) {
            this.mGPSVersion.setDotVisible(true);
            this.mGPSVersion.setValue(R.string.label_updatable);
            this.mGPSVersion.setClickable(true);
        } else {
            this.mGPSVersion.setDotVisible(false);
            this.mGPSVersion.setValue(R.string.version_update_not_has_new);
            this.mGPSVersion.setClickable(false);
        }
    }

    /**
     * set UI for a bike connected
     */
    private void onBikeConnected() {
        mLinearBattery.setVisibility(View.VISIBLE);
        mServiceLocation.setVisibility(View.VISIBLE);
        mBikesSettings.setLabel(R.string.label_bike_setting);
        this.onDeviceConnected();
    }

    /**
     * set UI for central connected
     */
    private void onCentralConnected() {
        mLinearBattery.setVisibility(View.VISIBLE);
        mServiceLocation.setVisibility(View.GONE);
        mBikesSettings.setLabel(R.string.label_central_setting);

        this.onDeviceConnected();
    }

    /**
     * set UI for no bike was connected
     */
    private void onNoDeviceConnected(int hardwareType) {
        if (CentralSession.isWholeBike(hardwareType)) {
            mServiceLocation.setVisibility(View.VISIBLE);
            mBikesSettings.setLabel(R.string.label_bike_setting);
        } else {
            mServiceLocation.setVisibility(View.GONE);
            mBikesSettings.setLabel(R.string.label_central_setting);
        }
        // 设备未连接隐藏为同步记录的数据
        this.mSyncRela.setVisibility(View.GONE);

        mLinearBattery.setVisibility(View.GONE);

        mTextViewConnectToBike.setText(R.string.speed_force_activity_click_connect);
        mTextViewConnectToBike.setTextColor(getResources().getColor(R.color.member_list_item_diver));
        mTextViewConnectToBike.setBoardColor(getResources().getColor(R.color.member_list_item_diver));
        mTextViewConnectToBike.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_speedx_force_connect_icon, 0, 0, 0);
        mTextViewConnectToBike.setOnClickListener(this);

        mBikesSettings.setmEnable(false);
        mOTAVersion.setmEnable(false);
        mGPSVersion.setmEnable(false);

        mOTAVersion.setValue("");
        mOTAVersion.setDotVisible(false);

        mGPSVersion.setValue("");
        mGPSVersion.setDotVisible(false);

        mBtnUnbindBike.setVisibility(View.GONE);

        if (null != currentDevice && !TextUtils.isEmpty(currentDevice.getUrl())) {
            Picasso.with(this).load(currentDevice.getUrl() + "").error
                    (R.drawable.ic_speedx_force_bike_normal_logo).placeholder
                    (R.drawable.ic_speedx_force_bike_normal_logo).into(mImgLogo);
        }

        if (null != currentDevice) {
            int hdType = currentDevice.getHardwareType();
            if (TextUtils.isEmpty(currentDevice.getFrameId()) &&
                    hdType != 0 && hdType != 2) {
                mTextViewType.setText(R.string.speed_force_complete_device_info);
            } else {
                String bikeName = BleDevice.brandType2Name(currentDevice.getBrandType());
                mTextViewType.setText(bikeName);
            }
        }

        if (null != dialogFragment && dialogFragment.isVisible()) {
            this.dialogFragment.dismissAllowingStateLoss();
        }
    }

    /**
     * set relative cycling data into UI
     */
    private void setDataForCycling(BleCyclingDTO cycling) {
        if (null != cycling) {
            mTextTotalCount.setText(String.valueOf(cycling.getTotalCount()));
            if (LocaleManager.isDisplayKM(this)) {
                if (cycling.getTotalDistance() > 1000) {
                    this.mTextTotalDistance.setText(String.format(Locale.CHINA, "%d",
                            (long) cycling.getTotalDistance() / 1000));
                } else {
                    this.mTextTotalDistance.setText(String.format(Locale.CHINA, "%.1f",
                            cycling.getTotalDistance() / 1000));
                }
            } else {
                if (cycling.getTotalDistance() > 1000) {
                    this.mTextTotalDistance.setText(String.format(Locale.CHINA, "%d",
                            (long) cycling.getTotalDistance() / 1000));
                } else {
                    this.mTextTotalDistance.setText(String.format(Locale.CHINA, "%.1f",
                            LocaleManager.kilometreToMile(cycling.getTotalDistance() / 1000)));
                }
            }

            long h = 0, m = 0;
            long totalTime = cycling.getTotalTime();
            if (totalTime > 0) {
                h = totalTime / 3600;
                m = totalTime % 3600 / 60;
            }

            if (h <= 0) {
                this.mTextTotalTime.setText(String.format(Locale.CHINA, "%.1f", (float) m / 60));
            } else {
                this.mTextTotalTime.setText(String.format(Locale.CHINA, "%d", h));
            }
        }
    }

    /**
     * 查询硬件设备的总数据
     */
    private void fetchBleCycling(final String centralId) {
        if (TextUtils.isEmpty(centralId)) {
            return;
        }


        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, BleCyclingDTO>() {
            @Override
            protected BleCyclingDTO doInBackground(String... params) {
                final UserManager userManager = new UserManager(SpeedForceActivity.this);
                BleCyclingDTO cyclingDTO = userManager.getUserGoalInfoByCentral(centralId);
                final List<LocalActivity> list = bleManager.getUnSyncLocalActivitiesByCentralId(
                        getUserId(), centralId);
                if (null != list && list.size() > 0) {
                    for (LocalActivity activity : list) {
                        cyclingDTO.setTotalCount(cyclingDTO.getTotalCount() + 1);
                        cyclingDTO.setTotalDistance(cyclingDTO.getTotalDistance() + activity.getTotalDistance());
                        cyclingDTO.setTotalTime((long) (cyclingDTO.getTotalTime() + activity.getTotalElapsedTime()));
                    }
                }
                return cyclingDTO;
            }

            @Override
            protected void onPostExecute(BleCyclingDTO bleCyclingDTO) {
                setDataForCycling(bleCyclingDTO);
            }
        });
    }

    /**
     * set visibility for sync relative layout
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleSyncRelaState() {
        final CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (null == session) {
            return;
        }

        final BluetoothDevice device = session.getBluetoothDevice();
        if (null == device) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<LocalActivity>>() {
            @Override
            protected List<LocalActivity> doInBackground(String... strings) {
                return bleManager.getUnSyncLocalActivitiesByCentralId(getUserId(), session.getCentralId());
            }

            @Override
            protected void onPostExecute(List<LocalActivity> list) {
                if (null == list || list.size() <= 0) {
                    mSyncRela.setVisibility(View.GONE);
                } else {
                    mSyncRela.setVisibility(View.VISIBLE);
                    mSyncMsg.setText(String.format(getString(R.string.msg_ble_unsync), device.getName()));
                }
            }
        });
    }

    /**
     * start to sync data
     */
    private void startSyncData() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_SYNC_DATA_FRAGMENT);
        if (null != fragment) {
            if (!isConnectedDevice(currentDevice)) {
                fragmentTransaction.remove(fragment);
            }
            return;
        }

        this.dialogFragment = new SpeedXDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SpeedXDialogFragment.EXTRA_SYNC_TYPE, SpeedXDialogFragment.SyncType.SYNC_CYCLING);
        bundle.putString(SpeedXDialogFragment.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
        dialogFragment.setInvocation(manager);
        this.dialogFragment.setArguments(bundle);
        fragmentTransaction.add(dialogFragment, TAG_SYNC_DATA_FRAGMENT).commitAllowingStateLoss();
    }

    /**
     * unbind bike
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void unBoundBike() {
        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (null == session) {

            Toasts.show(this, R.string.speed_force_activity_unbind_fail);
            return;
        }

        BluetoothDevice device = session.getBluetoothDevice();
        if (null == device || device.getBondState() != BluetoothDevice.BOND_BONDED) {
            Toasts.show(this, R.string.speed_force_activity_unbind_fail);
            return;
        }

        try {

            final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
            service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_UNBOUND);
            service.putExtra(CentralService.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
            service.setPackage(getPackageName());
            this.startService(service);

            MobclickAgent.onEvent(this, "BLE - 解除绑定");
            Toasts.show(this, R.string.speed_force_activity_unbind_success);
            logger.info("Remove bond device " + device.getName() + ":" + device.getAddress() + " success");
        } catch (Exception e) {
            Toasts.show(this, R.string.speed_force_activity_unbind_fail);
            logger.error("Remove bond device " + device.getName() + ":" + device.getAddress() + " error, " + e);
        }
    }

    /**
     * 显示设备下拉列表
     */
    private void showDeviceList() {
        if (mBleDevices != null && mBleDevices.size() > 1) {
            if (multiDeviceSelectPW == null) {
                multiDeviceSelectPW = new MultiDeviceSelectPW(this, new MultiDeviceSelectPW.OnItemClickListener() {
                    @Override
                    public void onItemClick(BleDevice item) {

                        mTextViewTitle.setText(item.getDeviceName());

                        String macAddress = null;
                        if (null != currentDevice) {
                            macAddress = currentDevice.getMacAddress();
                        }

                        //如果有设备连接,切点击项为当前已连接设备,则显示连接界面
                        //否则,显示未连接UI

                        if (CentralSessionHandler.getInstance().isConnected(item.getMacAddress())) {
                            currentDevice = item;

                            if (CentralSession.isWholeBike(item.getHardwareType())) {
                                onBikeConnected();
                            } else {
                                onCentralConnected();
                            }
                            fetchBleCycling(item.getMacAddress());
                            return;
                        }

                        if (!TextUtils.equals(item.getMacAddress(), macAddress)) {

                            currentDevice = item;
                            fetchBleCycling(item.getMacAddress());
                            onNoDeviceConnected(item.getHardwareType());
                        }
                    }
                });

                multiDeviceSelectPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                        mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.ic_arrow_down_white, 0);
                    }
                });
            }
            CentralSession session = CentralSessionHandler.getInstance().getConnectSession();

            multiDeviceSelectPW.show(mTextViewTitle, session == null ? null : session.getCentralId(),
                    mBleDevices);
            mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_white, 0);
            backgroundAlpha(0.5f);
        }
    }

    /**
     * 获取设备列表
     */
    private void getDeviceList() {
        getAsyncTaskQueue().add(new AsyncTask<String, Void, List<BleDevice>>() {

            @Override
            protected List<BleDevice> doInBackground(String... strings) {

                //获取本地
                List<BleDevice> bleDevices = null;
                try {
                    bleDevices = bleManager.getBleDevices();

                    if (bleDevices != null && bleDevices.size() > 0) {
                        mBleDevices.addAll(bleDevices);
                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                }

                //获取远程
                bleDevices = bleManager.getBleDevicesFromServer();
                if (bleDevices != null && bleDevices.size() > 0) {
                    for (BleDevice bleDevice : bleDevices) {
                        if (mBleDevices.contains(bleDevice)) {
                            continue;
                        }
                        mBleDevices.add(bleDevice);
                    }
                }

                return mBleDevices;
            }

            @Override
            protected void onPostExecute(List<BleDevice> bleDevices) {
                super.onPostExecute(bleDevices);

                if (bleDevices.size() > 1) {
                    mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_arrow_down_white, 0);
                } else {
                    mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                resumeConnectedState();
            }
        });
    }

    /**
     * 是否为连接状态
     */
    private void resumeConnectedState() {
        CentralSession connectedSession = CentralSessionHandler.getInstance().getConnectSession();
        if (connectedSession != null) {
            for (BleDevice bleDevice : mBleDevices) {
                if (TextUtils.equals(bleDevice.getMacAddress(), connectedSession.getCentralId())) {
                    //当前有连接设备
                    currentDevice = bleDevice;
                    if (CentralSession.isWholeBike(bleDevice.getHardwareType())) { // 中控
                        onBikeConnected();
                    } else {
                        onCentralConnected();
                    }
                }
            }
        } else {
            currentDevice = mBleDevices.get(0);
            onNoDeviceConnected(currentDevice.getHardwareType());
        }

        connectDevice();
        if (currentDevice != null) {
            mTextViewTitle.setText(currentDevice.getDeviceName());
            fetchBleCycling(currentDevice.getMacAddress());
        }
    }

    /**
     * 低电量提示
     */
    private void showLittleBattery() {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.speed_force_little_battery_msg);
        dialog.setPositiveButton(R.string.label_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 弹出更新A_PGS更新的进度对话框
     */
    private void showAGPSDialog() {
        if (isDestroyed() || isFinishing() || null == currentDevice) {
            return;
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_SYNC_DATA_FRAGMENT);
        if (null != fragment) {
            if (!isConnectedDevice(currentDevice)) {
                fragmentTransaction.remove(fragment);
            }
            return;
        }

        this.dialogFragment = new SpeedXDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SpeedXDialogFragment.EXTRA_SYNC_TYPE, SpeedXDialogFragment.SyncType.SYNC_A_GPS);
        bundle.putString(SpeedXDialogFragment.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
        dialogFragment.setInvocation(manager);
        this.dialogFragment.setArguments(bundle);
        fragmentTransaction.add(dialogFragment, TAG_SYNC_DATA_FRAGMENT).commitAllowingStateLoss();
    }

    /**
     * OTA是否有更新
     *
     * @param hasUpdate hasUpdate
     */
    private void checkOtaVersionView(boolean hasUpdate) {
        if (hasUpdate) {
            this.mOTAVersion.setValue(R.string.label_updatable);
            this.mOTAVersion.setDotVisible(true);
            this.mOTAVersion.setClickable(true);
        } else {
            this.mOTAVersion.setValue(R.string.version_update_not_has_new);
            this.mOTAVersion.setDotVisible(false);
            this.mOTAVersion.setClickable(false);
        }
    }

    private void setCheckOtaCountVersion(final OTAFirmwareInfoCharacteristic character) {
        if (null == character) {
            return;
        }

        RequestQueue requestQueue = RequestQueueFactory.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.UrlConfig.DEV_SPEEDX_OTA_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null == response || response.optInt("code") != 0) {
                            hasNewOtaVersion = false;
                            checkOtaVersionView(hasNewOtaVersion);
                        } else {
                            JSONObject result = response.optJSONObject("result");
                            hasNewOtaVersion = parserOtaVersion(result, character);
                            checkOtaVersionView(hasNewOtaVersion);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkOtaVersionView(false);
            }
        });
        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * 解析是否有固件需要升级
     *
     * @param result    result
     * @param character character
     * @return boolean
     */
    private boolean parserOtaVersion(JSONObject result, OTAFirmwareInfoCharacteristic character) {
        if (null == result || null == character) {
            return false;
        }

        // OTA
        JSONObject ota = null;
        logger.trace("parserOtaVersion, HardwareType = " + hardwareType + ", \n 固件版本信息: " +
                character.toString() + ", uiCheckSum = " + character.getUiCheckSum());
        switch (hardwareType) {
            case 0x00:// B08
                ota = result.optJSONObject("speed-force-v1.0");
                break;
            case 0x02:// B09
                ota = result.optJSONObject("speedforce_B09");
                break;
            case 0x01:// S601
                ota = result.optJSONObject("whole_bike_s601");
                break;
            case 0x03:// S603
                break;
            case 0x04:// S605
                break;
        }

        if (null != ota) {
            JSONObject main = ota.optJSONObject("main");
            JSONObject ble = ota.optJSONObject("ble");
            JSONObject ui = ota.optJSONObject("ui");
            JSONObject font = ota.optJSONObject("font");
            JSONObject power = ota.optJSONObject("power");

            if (null != power) {
                logger.info("Power is not null");
                int powerChecksum = power.optInt("checksum");
                if (character.getPowerCheckSum() != powerChecksum) {
                    logger.info("Power has a new version");
                    return true;
                }
            }

            if (null != ui) {
                int uiChecksum = ui.optInt("checksum");
                if (character.getUiCheckSum() != uiChecksum) {
                    logger.info("UI has a new version");
                    return true;
                }
            }

            if (null != main) {
                int mainChecksum = main.optInt("checksum");
                if (character.getMcuCheckSum() != mainChecksum) {
                    logger.info("Main is new version");
                    return true;
                }
            }

            if (null != ble) {
                int bleChecksum = ble.optInt("checksum");
                if (character.getBleCheckSum() != bleChecksum) {
                    logger.info("Ble is new version");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测蓝牙是否开启
     */
    private void checkBlueState() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null == adapter) {
            return;
        }

        if (!adapter.isEnabled()) {// 没有开启则开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(enableBtIntent, REQUEST_BLUE_ENABLE_CODE);
        } else {// 开启则启动Service
            final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
            service.setPackage(getPackageName());
            this.startService(service);
            if (mService == null) {
                this.bindService(service, this, BIND_AUTO_CREATE);
            }
        }
    }

    /**
     * 注册广播监听
     */
    private void registerBleReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DiscoveryActivity.BLE_CONNECTED_ACTION);
        filter.addAction(DiscoveryActivity.BLE_DISCONNECTED_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(centralReceiver, filter);
    }

    /**
     * 注销广播监听
     */
    private void unRegisterBleReceiver() {
        unregisterReceiver(centralReceiver);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    private BroadcastReceiver centralReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(DiscoveryActivity.BLE_CONNECTED_ACTION)) {
                logger.trace("BroadcastReceiver 已连接");
                CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
                if (null == session) {
                    return;
                }
                checkBlueState();
                mTextViewTitle.setText(session.getName());
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                return;
            }

            if (action.equals(DiscoveryActivity.BLE_DISCONNECTED_ACTION)) {
                logger.trace("BroadcastReceiver 已断开");
                dismissSyncDataFragment();
                onNoDeviceConnected(-1);
                return;
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 10);
                switch (extra) {
                    case BluetoothAdapter.STATE_OFF:// 蓝牙关闭
                        logger.info("蓝牙关闭");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:// 打开中
                        break;
                    case BluetoothAdapter.STATE_ON://蓝牙开启
                        logger.info("中控首页－－蓝牙打开");
                        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
                        service.setPackage(getPackageName());
                        startService(service);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:// 关闭中
                        break;
                }
            }

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null)
                    return;
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        logger.info("正在配对......");
                        pairHandler.sendEmptyMessageDelayed(MSG_PAIR_DIALOG_WHAT, 10 * 1000);
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        bonded = true;

                        String macAddress = CentralSession.address2CentralId(device.getAddress());
                        if (null != currentDevice && currentDevice.getMacAddress().
                                equals(macAddress)) {
                            mBtnUnbindBike.setVisibility(View.VISIBLE);
                        }
                        logger.info("完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        logger.info("取消配对");
                    default:
                        break;
                }
            }
        }
    };

    /**
     * 关闭同步窗口
     */
    private void dismissSyncDataFragment() {
        if (isConnectedDevice(currentDevice)) {
            return;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_SYNC_DATA_FRAGMENT);
        if (null != fragment) {
            transaction.remove(fragment);
        }
    }

    /**
     * 设置用户骑行月度骑行里程
     */
    private void writeTargetConfig() {
        SharedPreferences userSp = getSharedPreferences(getUserId(), 0);
        String data = userSp.getString(Constants.PREF_CYCLING_MY_GOAL_KEY, "");
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                MyGoalInfoDTO goalInfo = new MyGoalInfoDTO(object);
                boolean isWrite = this.manager.writeTargetConfig(0, (int) goalInfo.getMyGoal(),
                        (int) goalInfo.getCurGoal());
                logger.info("设置目标里程: 目标里程 = " + goalInfo.getMyGoal() + ", 当前完成 = " +
                        goalInfo.getCurGoal() + ", " + isWrite);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class PairHandler extends Handler {

        private WeakReference<Activity> reference;

        public PairHandler(Activity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = reference.get();
            if (null != activity && !bonded && !hasShowDialog) {
                hasShowDialog = true;
                BlePairTipDialog dialog = new BlePairTipDialog(activity);
                dialog.show();
            }
        }
    }

    @Override
    public void onScanResult(final List<CentralSession> scanResults, final CentralSession session) {
        if (currentDevice == null)
            return;

        CentralSession connectSession = CentralSessionHandler.getInstance().getConnectSession();
        if (connectSession != null) {
            if (connectSession.getCentralId().equals(currentDevice.getMacAddress())) {
                return;
            }
        }

        if (session == null) {
            if (bleConnectTipDialog != null && bleConnectTipDialog.isShowing()) {
                bleConnectTipDialog.dismiss();
            }
            return;
        }

        if (session.getCentralId().equals(currentDevice.getMacAddress())) {
            if (!session.isAvailable()) {
                if (bleConnectTipDialog == null) {
                    bleConnectTipDialog = new BleConnectTipDialog(SpeedForceActivity.this,
                            CentralSession.isWholeBike(session.getHdType()));
                }
                if (!bleConnectTipDialog.isShowing())
                    bleConnectTipDialog.show();
            } else {
                if (bleConnectTipDialog != null && bleConnectTipDialog.isShowing()) {
                    bleConnectTipDialog.dismiss();
                }
            }
        }
        logger.error("onScanResult currentCentralName =[" + currentDevice.getDeviceName() + "] " +
                "currentCentralId =[" + currentDevice.getMacAddress() + "] " +
                "session = [" + session + "]");

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        logger.info("onServiceConnected");
        mService = ((CentralService.ICentralBinder) binder).getService();
        mService.setOnScanResultListener(SpeedForceActivity.this);
        manager = mService.getInvocation();
        this.manager.setResponseDeviceInfoListener(this);
        this.manager.setResponseMacAddressListener(this);
        this.manager.setPreviewCyclingListener(this);
        this.manager.setAGPSListener(this);
        this.manager.setCheckSumListener(this);
        this.manager.setUpdateListener(this);

        // 绑定Service成功后
        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (session != null && manager != null) {
            this.mTextViewTitle.setText(session.getName());
            this.hardwareType = session.getHdType();
            logger.trace("ServiceConnected, HardwareType = " + hardwareType);
            this.manager.writeDeviceInfoRequest();
            Handler handler = new Handler(getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    manager.writeAGPSInfoRequest();
                }
            }, 300);
            handleSyncRelaState();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        logger.info("onServiceDisconnected");
    }

    private void showOtaUpdateDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(R.string.label_update);
        dialog.setMessage(R.string.msg_ble_ota_update);
        dialog.setPositiveButton(R.string.update_dialog_button_update_immediately, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final Intent otaInfoIntent = new Intent(SpeedForceActivity.this, SpeedXOtaVersionActivity.class);
                otaInfoIntent.putExtra(SpeedXOtaVersionActivity.EXTRA_OTA_INFO, otaFirmwareInfo);
                otaInfoIntent.putExtra(SpeedXOtaVersionActivity.EXTRA_HARDWARE_TYPE, hardwareType);
                otaInfoIntent.putExtra(SpeedXOtaVersionActivity.EXTRA_CENTRAL_ID, currentDevice.getMacAddress());
                startActivityForResult(otaInfoIntent, REQUEST_OTA_UPDATE_CODE);
            }
        });
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

}
