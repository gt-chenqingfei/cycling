package com.beastbikes.android.ble.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshOTAPacketListener;
import com.beastbikes.android.ble.otadownload.OTAManage;
import com.beastbikes.android.ble.protocol.v1.OTAFirmwareInfoCharacteristic;
import com.beastbikes.android.ble.ui.dialog.SpeedXDialogFragment;
import com.beastbikes.android.ble.ui.painter.PowerView;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LayoutResource(R.layout.activity_speedx_ota_version)
public class SpeedXOtaVersionActivity extends SessionFragmentActivity implements View.OnClickListener,
        RefreshOTAPacketListener, OTAManage.DownloadFileListener, RequestQueueManager {

    public static final String EXTRA_OTA_INFO = "ota_info";
    public static final String EXTRA_HARDWARE_TYPE = "hardware_type";
    public static final String EXTRA_CENTRAL_ID = "central_id";

    private static final Logger logger = LoggerFactory.getLogger("SpeedXOtaVersionActivity");

    @IdResource(R.id.speedx_update_version_view)
    private PowerView versionView;

    @IdResource(R.id.speedx_cancel_update)
    private Button cancelUpdateBtn;

    private Invocation manager;
    private OTAManage otaManage;
    private double totalSize;
    private double currentSize;
    private boolean isUpdate;
    private int lastIndex = -1;
    private SharedPreferences sp;
    private OTAFirmwareInfoCharacteristic otaInfo;
    private int hardwareType;
    private RequestQueue requestQueue;
    private String oldVersion;

    private int lastUpdateType = 1;
    private String centralId;

    private CentralSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.versionView.setChangeColor(false);
        final Intent intent = getIntent();
        if (null == intent) {
            return;
        }

        this.centralId = intent.getStringExtra(EXTRA_CENTRAL_ID);
        session = CentralSessionHandler.getInstance().sessionMatch(this.centralId);
        this.otaManage = new OTAManage(this, this);
        this.sp = getSharedPreferences(getPackageName(), 0);
        this.requestQueue = RequestQueueFactory.newRequestQueue(this);
        this.otaInfo = (OTAFirmwareInfoCharacteristic) intent.getSerializableExtra(EXTRA_OTA_INFO);
        this.hardwareType = intent.getIntExtra(EXTRA_HARDWARE_TYPE, 0);

        this.cancelUpdateBtn.setOnClickListener(this);
        if (this.session != null) {
            this.session.getProperty().setCancelUpdate(false);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.setPackage(getPackageName());
        this.startService(service);
        this.bindService(service, connection, BIND_AUTO_CREATE);

    }

    @Override
    public void finish() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            this.unbindService(connection);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && this.isUpdate) {
            this.showCancelUpdateDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speedx_cancel_update:// 取消升级
                if (this.isUpdate) {
                    this.showCancelUpdateDialog();
                }
                break;
        }
    }

    @Override
    public void onRefreshCount(int index) {
        if (this.lastIndex == index) {
            return;
        }

        this.lastIndex = index;
        this.isUpdate = true;
        this.currentSize = this.currentSize + 190;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double pro = currentSize / totalSize * 100;
                versionView.setValue((float) pro, true);
            }
        });
    }

    @Override
    public void onOTADataEnd(final int type) {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == -1 && getWindow() != null) {
                    isUpdate = false;
                    final MaterialDialog dialog = new MaterialDialog(SpeedXOtaVersionActivity.this);
                    dialog.setMessage(R.string.label_speed_force_restart_msg);
                    dialog.setPositiveButton(R.string.label_i_know, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            OnUpdateDataListener listener = manager.getUpdateListener();
                            if (null != listener) {
                                listener.onUpdateSuccess(SpeedXDialogFragment.SyncType.SYNC_OTA_DATA);
                            }
                            finish();
                        }
                    }).show();
                }

                if (type == 0) {// 连接已断开
                    showUpdateOtaErrorDialog();
                }
            }
        });
    }

    @Override
    public void onParserDataError() {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP)
            return true;

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (this.isUpdate) {
                this.showCancelUpdateDialog();
                return true;
            } else {
                finish();
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDownloadFileSuccess(int type, String versionName, String filePath) {
        if (type == this.lastUpdateType) {
            this.versionView.setVersion(oldVersion + "-" + versionName);
            this.manager.writeOTAStartRequest(type, versionName, filePath);
        }
    }

    @Override
    public void onDownloadFileError(int type) {

    }

    @Override
    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showCancelUpdateDialog() {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.msg_cancel_update_ota);
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.getProperty().setCancelUpdate(true);
                dialog.dismiss();
                OnUpdateDataListener listener = manager.getUpdateListener();
                if (null != listener) {
                    listener.onUpdateCanceled(SpeedXDialogFragment.SyncType.SYNC_OTA_DATA);
                }
                finish();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 检测ota版本
     */
    private void checkOTAVersion(final OTAFirmwareInfoCharacteristic character) {
        if (null == character) {
            return;
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }

        this.totalSize = 0;

        otaManage.deleteFolder();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.UrlConfig.DEV_SPEEDX_OTA_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null == response || response.optInt("code") != 0) {
                            Toasts.show(SpeedXOtaVersionActivity.this, R.string.label_ota_version_is_new_msg);
                        } else {
                            JSONObject result = response.optJSONObject("result");
                            parserOtaVersion(result, character);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasts.show(SpeedXOtaVersionActivity.this, R.string.label_ota_version_is_new_msg);
            }
        });
        jsonObjectRequest.setShouldCache(false);
        getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * 解析是否有固件需要升级
     *
     * @param result    result
     * @param character character
     * @return boolean
     */
    private void parserOtaVersion(JSONObject result, OTAFirmwareInfoCharacteristic character) {
        if (null == result || null == character) {
            Toasts.show(SpeedXOtaVersionActivity.this, R.string.label_ota_version_is_new_msg);
            return;
        }

        // OTA
        JSONObject ota = null;
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

            boolean hasNewVersion = false;
            if (null != power) {
                String powerVersion = power.optString("version");
                int powerChecksum = power.optInt("checksum");
                if (character.getPowerCheckSum() != powerChecksum) {
                    logger.info("Power has a new version");
                    int size = power.optInt("size");
                    int length;
                    if (size % 190 == 0) {
                        length = size / 190;
                    } else {
                        length = size / 190 + 1;
                    }
                    totalSize = totalSize + length * 190;
                    lastUpdateType = OTAManage.OTA_POWER_IMG;
                    hasNewVersion = true;
                    checkLocalImg(OTAManage.OTA_POWER_IMG, powerChecksum);
                    String location = power.optString("location");
                    otaManage.downLoadFile(OTAManage.OTA_POWER_IMG, powerVersion, location, powerChecksum);
                }
            }

//            if (null != font) {
//                String fontVersion = font.optString("version");
//                int fontChecksum = font.optInt("checksum");
//                if (character.getFontCheckSum() != fontChecksum) {
//                    logger.info("Font has a new version");
//                    int size = font.optInt("size");
//                    int length;
//                    if (size % 190 == 0) {
//                        length = size / 190;
//                    } else {
//                        length = size / 190 + 1;
//                    }
//                    totalSize = totalSize + length * 190;
//                    lastUpdateType = OTAManage.OTA_FONT_IMG;
//                    hasNewVersion = true;
//                    checkLocalImg(OTAManage.OTA_FONT_IMG, fontChecksum);
//                    String location = font.optString("location");
//                    otaManage.downLoadFile(OTAManage.OTA_FONT_IMG, fontVersion, location, fontChecksum);
//                }
//            }

            if (null != ui) {
                String uiVersion = ui.optString("version");
                int uiChecksum = ui.optInt("checksum");
                if (character.getUiCheckSum() != uiChecksum) {
                    logger.info("UI has a new version");
                    int size = ui.optInt("size");
                    int length;
                    if (size % 190 == 0) {
                        length = size / 190;
                    } else {
                        length = size / 190 + 1;
                    }
                    totalSize = totalSize + length * 190;
                    lastUpdateType = OTAManage.OTA_UI_IMG;
                    hasNewVersion = true;
                    checkLocalImg(3, uiChecksum);
                    String location = ui.optString("location");
                    otaManage.downLoadFile(OTAManage.OTA_UI_IMG, uiVersion,
                            location, uiChecksum);
                }
            }

            if (null != main) {
                String mainVersion = main.optString("version");
                int mainChecksum = main.optInt("checksum");
                if (character.getMcuCheckSum() != mainChecksum) {
                    logger.info("Main has a new version");
                    int size = main.optInt("size");
                    int length;
                    if (size % 190 == 0) {
                        length = size / 190;
                    } else {
                        length = size / 190 + 1;
                    }
                    totalSize = totalSize + length * 190;
                    lastUpdateType = OTAManage.OTA_MCU_IMG;
                    hasNewVersion = true;
                    checkLocalImg(2, mainChecksum);
                    String location = main.optString("location");
                    otaManage.downLoadFile(OTAManage.OTA_MCU_IMG, mainVersion,
                            location, mainChecksum);
                }
            }

            if (null != ble) {
                String bleVersion = ble.optString("version");
                int bleChecksum = ble.optInt("checksum");
                if (character.getBleCheckSum() != bleChecksum) {
                    logger.info("Ble has a new version");
                    int size = ble.optInt("size");
                    int length;
                    if (size % 190 == 0) {
                        length = size / 190;
                    } else {
                        length = size / 190 + 1;
                    }
                    totalSize = totalSize + length * 190;
                    lastUpdateType = OTAManage.OTA_BLE_IMG;
                    hasNewVersion = true;
                    checkLocalImg(1, bleChecksum);
                    String location = ble.optString("location");
                    otaManage.downLoadFile(OTAManage.OTA_BLE_IMG, bleVersion,
                            location, bleChecksum);
                }
            }

            if (!hasNewVersion) {
                Toasts.show(SpeedXOtaVersionActivity.this, R.string.label_ota_version_is_new_msg);
            }
        }
    }

    /**
     * 检测本地是否有ble 文件
     *
     * @param type
     */
    private void checkLocalImg(int type, int checksum) {
        String otaJson = "";
        String otaKey = "";
        switch (type) {
            case 0x01:// ble img
                otaKey = Constants.BLE.PREF_BLE_IMG_KEY;
                otaJson = this.sp.getString(Constants.BLE.PREF_BLE_IMG_KEY, "");
                break;
            case 0x02:// mcu img
                otaKey = Constants.BLE.PREF_MCU_IMG_KEY;
                otaJson = this.sp.getString(Constants.BLE.PREF_MCU_IMG_KEY, "");
                break;
            case 0x03:// ui img
                otaKey = Constants.BLE.PREF_UI_IMG_KEY;
                otaJson = this.sp.getString(Constants.BLE.PREF_UI_IMG_KEY, "");
                break;
            case 0x04:// A_GPS img
                break;
            case 0x05:// font img
                otaKey = Constants.BLE.PREF_FONT_IMG_KEY;
                otaJson = this.sp.getString(Constants.BLE.PREF_FONT_IMG_KEY, "");
                break;
            case 0x06:// power img
                otaKey = Constants.BLE.PREF_POWER_IMG_KEY;
                otaJson = this.sp.getString(Constants.BLE.PREF_POWER_IMG_KEY, "");
                break;
        }

        if (TextUtils.isEmpty(otaJson)) {
            return;
        }

        try {
            JSONObject object = new JSONObject(otaJson);
            String filePath = object.optString("path");
            int oldChecksum = object.optInt("checksum");
            if (TextUtils.isEmpty(filePath)) {
                return;
            }

            if (oldChecksum != checksum) {
                if (FileUtil.deleteFile(filePath)) {
                    this.sp.edit().remove(otaKey).apply();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新ota信息失败
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showUpdateOtaErrorDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.msg_ble_update_ota_error);
        dialog.setPositiveButton(R.string.label_i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                setResult(SpeedForceActivity.RESULT_DEVICE_DISCONNECT_CODE, getIntent());
                finish();
            }
        }).show();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            logger.info("onServiceConnected");
            CentralService mService = ((CentralService.ICentralBinder) binder).getService();
            manager = mService.getInvocation();
            manager.setRefreshOTAPacketListener(SpeedXOtaVersionActivity.this);
            if (null != otaInfo) {
                oldVersion = otaInfo.getMcuVersion();
                checkOTAVersion(otaInfo);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logger.info("onServiceDisconnected");
        }
    };
}
