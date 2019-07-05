package com.beastbikes.android.ble.biz;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.internal.telephony.ITelephony;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.entity.InvocationParam;
import com.beastbikes.android.ble.biz.listener.OnAGPSListener;
import com.beastbikes.android.ble.biz.listener.OnOtaCheckSumListener;
import com.beastbikes.android.ble.biz.listener.OnPreviewCyclingListener;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshCyclingDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshCyclingSampleListener;
import com.beastbikes.android.ble.biz.listener.RefreshOTAPacketListener;
import com.beastbikes.android.ble.biz.listener.ResponseDeviceInfoListener;
import com.beastbikes.android.ble.biz.listener.ResponseMacAddressListener;
import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.ble.otadownload.OTAManage;
import com.beastbikes.android.ble.protocol.v1.AGpsInfoCharacteristic;
import com.beastbikes.android.ble.protocol.v1.BatterySensorCharacteristic;
import com.beastbikes.android.ble.protocol.v1.DeviceInfoCommandCharacteristic;
import com.beastbikes.android.ble.protocol.v1.DeviceInfoExtensionCharacteristic;
import com.beastbikes.android.ble.protocol.v1.OTAFirmwareInfoCharacteristic;
import com.beastbikes.android.ble.protocol.v1.OTARequestCommandCharacteristic;
import com.beastbikes.android.ble.protocol.v1.PreviewDataCharacteristic;
import com.beastbikes.android.ble.protocol.v1.ProtocolParserImpl;
import com.beastbikes.android.ble.protocol.v1.SynchronizationDataCharacteristic;
import com.beastbikes.android.ble.ui.DiscoveryActivity;
import com.beastbikes.android.ble.ui.dialog.SpeedXDialogFragment;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.android.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by icedan on 16/6/6.
 */
@TargetApi(19)
public class CentralInvocation implements Constants, OTAManage.DownloadFileListener, Invocation {
    private static final byte PROCESS_TYPE_OTA_BLE = 0x01;
    private static final byte PROCESS_TYPE_OTA_MCU = 0x02;
    private static final byte PROCESS_TYPE_OTA_UI = 0x04;
    private static final byte PROCESS_TYPE_OTA_FONT = 0x08;
    private static final byte PROCESS_TYPE_OTA_A_GPS = 0x10;
    private static final byte PROCESS_TYPE_OTA_POWER = 0x20;

    private static final int MSG_PREVIEW_CYCLING = 0;
    private static final int MSG_SYNC_CYCLING = 1;

    private static final Logger logger = LoggerFactory.getLogger("CentralInvocation");

    private RefreshCyclingDataListener refreshCyclingDataListener;
    private RefreshOTAPacketListener refreshOTAPacketListener;
    private ResponseDeviceInfoListener responseDeviceInfoListener;
    private ResponseMacAddressListener responseMacAddressListener;
    // 实时数据回调接口
    private RefreshCyclingSampleListener refreshCyclingSampleListener;
    // 开始同步A_GPS文件回调接口
    private OnAGPSListener aGPSListener;
    // 预览数据回调接口
    private OnPreviewCyclingListener previewCyclingListener;
    // 固件版本信息
    private OnOtaCheckSumListener checkSumListener;
    // 取消升级
    private OnUpdateDataListener onUpdateDataListener;

    private ProtocolParserImpl mParser;
    private BluetoothGatt mGatt;
    private CentralSession mSession;


    private Context mContext;
    private SharedPreferences mUserSp;
    private Looper mSyncDataLooper;
    private Handler mSyncDataHandler;
    private Handler mMainHandler;
    private BleManager mBleManager;
    private Handler mCentralServiceHandler;
    private List<Byte> otaInfoList = new ArrayList<>();


    @Override
    public void setRefreshCyclingDataListener(RefreshCyclingDataListener refreshCyclingDataListener) {
        this.refreshCyclingDataListener = refreshCyclingDataListener;
    }

    @Override
    public void setRefreshOTAPacketListener(RefreshOTAPacketListener refreshOTAPacketListener) {
        this.refreshOTAPacketListener = refreshOTAPacketListener;
    }

    @Override
    public void setResponseDeviceInfoListener(ResponseDeviceInfoListener responseDeviceInfoListener) {
        this.responseDeviceInfoListener = responseDeviceInfoListener;
    }

    @Override
    public void setResponseMacAddressListener(ResponseMacAddressListener responseMacAddressListener) {
        this.responseMacAddressListener = responseMacAddressListener;
    }

    @Override
    public void setRefreshCyclingSampleListener(RefreshCyclingSampleListener listener) {
        this.refreshCyclingSampleListener = listener;
    }

    @Override
    public void setAGPSListener(OnAGPSListener listener) {
        this.aGPSListener = listener;
    }

    @Override
    public OnPreviewCyclingListener getPreviewCyclingListener() {
        return previewCyclingListener;
    }

    @Override
    public void setPreviewCyclingListener(OnPreviewCyclingListener previewCyclingListener) {
        this.previewCyclingListener = previewCyclingListener;
    }

    @Override
    public void setCheckSumListener(OnOtaCheckSumListener checkSumListener) {
        this.checkSumListener = checkSumListener;
    }

    @Override
    public OnUpdateDataListener getUpdateListener() {
        return onUpdateDataListener;
    }

    @Override
    public void setUpdateListener(OnUpdateDataListener updateListener) {
        this.onUpdateDataListener = updateListener;
    }


    public CentralInvocation() {
    }

    public void init(Context context, Handler centralServiceHandler, SharedPreferences sp) {
        if (mContext == null) {
            this.mCentralServiceHandler = centralServiceHandler;
            this.mContext = context;
            this.mParser = new ProtocolParserImpl();
            this.mMainHandler = new Handler(BeastBikes.getInstance().getMainLooper());
            HandlerThread thread = new HandlerThread("SyncDataThread");
            thread.start();

            this.mSyncDataLooper = thread.getLooper();
            this.mSyncDataHandler = new SyncDataHandler(mSyncDataLooper);
            this.mBleManager = new BleManager(context);
            this.mUserSp = sp;
        }
    }

    public void destroy() {
        if (mSyncDataLooper != null) {
            mSyncDataLooper.quit();
        }
    }

    private final class SyncDataHandler extends Handler {
        public SyncDataHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            Object value = msg.obj;
            if (null == value) {
                return;
            }
            switch (msg.what) {
                case MSG_PREVIEW_CYCLING:// 数据预览
                    savePreviewActivity(value);
                    break;
                case MSG_SYNC_CYCLING:// 同步数据
                    saveBleCycling(value);
                    break;
            }

        }
    }

    /**
     * Command Request Characteristic
     */
    private BluetoothGattCharacteristic getCommandRequestCharacter() {
        if (null != mGatt && CentralSessionHandler.getInstance().
                isConnected(mGatt.getDevice())) {


            final BluetoothGattService blueService = mGatt.getService(BLE.UUID_SERVICE);
            if (null == blueService) {
                return null;
            }

            return blueService.getCharacteristic(BLE.UUID_COMMAND_REQUEST_WRITE);
        }
        return null;
    }

    /**
     * Configuration Characteristic
     *
     * @return
     */
    private BluetoothGattCharacteristic getConfigurationCharacter() {
        if (null != mGatt && CentralSessionHandler.getInstance().
                isConnected(mGatt.getDevice())) {

            BluetoothGattService blueService = mGatt.getService(BLE.UUID_SERVICE);
            if (null == blueService) {
                return null;
            }

            return blueService.getCharacteristic(BLE.UUID_CONFIGURATION_WRITE);
        }
        return null;
    }

    /**
     * Phone Exchange
     *
     * @return
     */
    private BluetoothGattCharacteristic getPhoneExchangeCharacter() {
        if (null != mGatt && CentralSessionHandler.getInstance().
                isConnected(mGatt.getDevice())) {

            BluetoothGattService blueService = mGatt.getService(BLE.UUID_SERVICE);
            if (null == blueService) {
                return null;
            }

            return blueService.getCharacteristic(BLE.UUID_PHONE_EXCHANGE_WRITE);
        }
        return null;
    }

    /**
     * OTA INFO
     *
     * @return
     */
    private BluetoothGattCharacteristic getOtaInfoCharacter() {
        if (null == this.mGatt) {
            return null;
        }

        final BluetoothGattService blueService = this.mGatt.getService(BLE.UUID_SERVICE);
        if (null == blueService) {
            return null;
        }

        return blueService.getCharacteristic(BLE.UUID_OTA_INFO_WRITE);
    }

    /** ------------------------------  Write Notification  --------------------------- */

    /**
     * 注册实时数据预览的notification
     *
     * @return
     */
    protected boolean writeActivitySampleNotification() {
        if (null == this.mGatt) {
            return false;
        }

        BluetoothGattService bleService = this.mGatt.getService(BLE.UUID_SERVICE);
        if (null == bleService) {
            return false;
        }

        BluetoothGattCharacteristic activitySampleCharacter = bleService.getCharacteristic(BLE.UUID_ACTIVITY_SAMPLE_NOTIFI);
        if (null == activitySampleCharacter) {
            return false;
        }

        activitySampleCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        this.mGatt.setCharacteristicNotification(activitySampleCharacter, true);
        final BluetoothGattDescriptor descriptor = activitySampleCharacter.getDescriptor(BLE.UUID_CCCD);
        if (null == descriptor) {
            return false;
        }

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean isWriteCD20 = this.mGatt.writeDescriptor(descriptor);
        logger.info("activitySampleCharacter uuid = "
                + activitySampleCharacter.getUuid().toString() + "read = " + "; write = " + isWriteCD20);
        return isWriteCD20;
    }

    /**
     * 注册同步数据及预览数据的notification
     */
    protected boolean writeActivitySyncNotification() {
        if (null == this.mGatt) {
            return false;
        }

        BluetoothGattService bleService = this.mGatt.getService(BLE.UUID_SERVICE);
        if (null == bleService) {
            return false;
        }
        final BluetoothGattCharacteristic activitySyncCharacter = bleService.getCharacteristic(BLE.UUID_ACTIVITY_SYNC_NOTIFI);
        if (null == activitySyncCharacter) {
            return false;
        }

        activitySyncCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        this.mGatt.setCharacteristicNotification(activitySyncCharacter, true);
        final BluetoothGattDescriptor descriptor = activitySyncCharacter.getDescriptor(BLE.UUID_CCCD);
        if (null == descriptor) {
            return false;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean isWriteCD26 = this.mGatt.writeDescriptor(descriptor);
        logger.info("activitySyncCharacter uuid = "
                + activitySyncCharacter.getUuid().toString() + "read = " + "; write = " + isWriteCD26);
        return isWriteCD26;
    }

    /**
     * 注册Command Response Notification
     */
    protected boolean writeCommandResponseNotification() {
        if (null == this.mGatt) {
            return false;
        }

        BluetoothGattService bleService = this.mGatt.getService(BLE.UUID_SERVICE);
        if (null == bleService) {
            return false;
        }

        final BluetoothGattCharacteristic commandResponseCharacter = bleService
                .getCharacteristic(BLE.UUID_COMMAND_NOTIFICATION);
        if (null == commandResponseCharacter) {
            return false;
        }
        commandResponseCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        this.mGatt.setCharacteristicNotification(commandResponseCharacter, true);
        final BluetoothGattDescriptor descriptor = commandResponseCharacter.getDescriptor(BLE.UUID_CCCD);
        if (null == descriptor) {
            return false;
        }

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean isWriteCD24 = this.mGatt.writeDescriptor(descriptor);
        logger.info("command notification uuid = "
                + commandResponseCharacter.getUuid().toString() + "read = " + "; write = " + isWriteCD24);
        return isWriteCD24;
    }

    /**
     * 注册Sensor Notification 及预览数据
     */
    protected boolean writeSensorNotification() {
        if (null == this.mGatt) {
            return false;
        }

        BluetoothGattService bleService = this.mGatt.getService(BLE.UUID_SERVICE);
        if (null == bleService) {
            return false;
        }
        final BluetoothGattCharacteristic sensorCharacter = bleService.getCharacteristic(BLE.UUID_SENSOR_NOTIFI);
        if (null == sensorCharacter) {
            return false;
        }
        sensorCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        this.mGatt.setCharacteristicNotification(sensorCharacter, true);
        final BluetoothGattDescriptor descriptor = sensorCharacter.getDescriptor(BLE.UUID_CCCD);
        if (null == descriptor) {
            return false;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean isWriteCD22 = this.mGatt.writeDescriptor(descriptor);
        logger.info("sensorCharacter uuid = "
                + sensorCharacter.getUuid().toString() + "read = " + "; write = " + isWriteCD22);
        return isWriteCD22;
    }


    /**  --------------------------- Command Request -------------------------------- */

    /**
     * 写入连接认证信息
     */
    protected void writeAuthKey() {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return;
        }

        // 0Yxa8Wxp!X
        byte[] data = new byte[20];
        try {
            byte[] auth = BLE.SPEEDX_BLE_AUTH_KEY.getBytes("UTF-8");
            data[0] = BLE.BLE_PROTOCOL_VERSION;
            data[1] = 0x0C;
            for (int i = 0; i < 17; i++) {
                if (i < auth.length) {
                    data[i + 2] = auth[i];
                } else {
                    data[i + 2] = 0x00;
                }
            }
            data[19] = mParser.crc8(data);
//            character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            character.setValue(data);
            boolean write = mGatt.writeCharacteristic(character);
            logger.info("write ble auth key to ble is " + write);
        } catch (UnsupportedEncodingException e) {
            logger.error("Write ble auth key to ble error, " + e);
        }
    }

    /**
     * 请求预览数据
     */
    @Override
    public void writeActivityPreviewRequest() {
        InvocationParam param = this.mSession.getProperty();
        if (null != param) {
            param.cleanActivityData();
        }
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x05;
        Calendar calendar = Calendar.getInstance();
        String year = "0x" + (calendar.get(Calendar.YEAR) - 2000);
        data[2] = Byte.decode(year);
        String month = "0x" + (calendar.get(Calendar.MONTH) + 1);
        data[3] = Byte.decode(month);
        String day = "0x" + calendar.get(Calendar.DAY_OF_MONTH);
        data[4] = Byte.decode(day);
        String hour = "0x" + calendar.get(Calendar.HOUR_OF_DAY);
        data[5] = Byte.decode(hour);
        String minute = "0x" + calendar.get(Calendar.MINUTE);
        data[6] = Byte.decode(minute);
        String second = "0x" + calendar.get(Calendar.SECOND);
        data[7] = Byte.decode(second);
        data[19] = mParser.crc8(data);
//        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        boolean write = mGatt.writeCharacteristic(character);
        logger.trace("请求预览数据，" + write);
    }

    /**
     * 结束骑行
     */
    protected boolean writeStopCycling() {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0F;
        data[19] = mParser.crc8(data);
//        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 请求同步数据
     */
    @Override
    public void writeActivitySyncRequest(final long time, final int errorCode) {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x06;

        Calendar calendar = Calendar.getInstance();
        if (time > 0) {
            calendar.setTimeInMillis(time);
        }

        String year = "0x" + (calendar.get(Calendar.YEAR) - 2000);
        data[2] = Byte.decode(year);
        String month = "0x" + (calendar.get(Calendar.MONTH) + 1);
        data[3] = Byte.decode(month);
        if (errorCode == 0x11) {
            data[3] = (byte) (calendar.get(Calendar.MONTH) + 1);
        }
        String day = "0x" + calendar.get(Calendar.DAY_OF_MONTH);
        data[4] = Byte.decode(day);
        String hour = "0x" + calendar.get(Calendar.HOUR_OF_DAY);
        data[5] = Byte.decode(hour);
        String minute = "0x" + calendar.get(Calendar.MINUTE);
        data[6] = Byte.decode(minute);
        String second = "0x" + calendar.get(Calendar.SECOND);
        data[7] = Byte.decode(second);
        data[19] = mParser.crc8(data);
        character.setValue(data);
        boolean isWrite = mGatt.writeCharacteristic(character);
        logger.info("Sync activity start, finish time = " + time + ", " + isWrite);
    }

    /**
     * OTA 升级激活
     */
    protected boolean writeOTAActive(int otaUpdateByte) {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x08;
        data[2] = (byte) otaUpdateByte;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        boolean success = mGatt.writeCharacteristic(character);
        // 每次发完激活指令后重置升级信息
        this.mSession.getProperty().setOtaUpdateByte(0);
        logger.info("OTA升级激活Byte ＝ " + otaUpdateByte + " is " + success);
        return success;
    }

    /**
     * 开始写入OTA升级包
     * 0x01 : OTA_BLE
     * 0x02 : OTA_MCU
     * 0x03 : OTA_UI
     * 0x04 : OTA_A_GPS_IMG
     */
    protected boolean writeOTAPacketStart() {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return false;
        }

        logger.info("write to ota pack start ...");
        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x09;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 回复把立收到数据
     *
     * @param flag  flag
     * @param type  type
     * @param index index
     */
    protected void writeReceiveResponse(final byte flag, final byte type, final int index) {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return;
        }
        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0B;
        data[2] = flag;
        data[3] = type;
        byte[] packetIndex = Utils.short2Byte((short) index);
        data[4] = packetIndex[1];
        data[5] = packetIndex[0];
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        mGatt.writeCharacteristic(character);
    }

    /**
     * 请求设备信息
     */
    @Override
    public void writeDeviceInfoRequest() {

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothGattCharacteristic character = getCommandRequestCharacter();
                if (null == character) {
                    return;
                }

                byte[] data = new byte[20];
                data[0] = BLE.BLE_PROTOCOL_VERSION;
                data[1] = 0x01;
                data[19] = mParser.crc8(data);
                character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                character.setValue(data);
                mGatt.writeCharacteristic(character);
            }
        }, 500 * 4);
    }

    /**
     * 检测固件版本信息
     */
    @Override
    public boolean writeCheckSumRequest() {
        BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0E;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 请求A_GPS 文件信息
     *
     * @return boolean
     */
    @Override
    public boolean writeAGPSInfoRequest() {
        BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x02;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * Device Information Extend
     *
     * @return boolean
     */
    protected void writeDeviceInfoExtensionRequest() {

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logger.error("Write device info extension ....");
                BluetoothGattCharacteristic character = getCommandRequestCharacter();
                if (null == character) {
                    logger.error("Write device info extension is true");
                    return;
                }
                byte[] data = new byte[20];
                data[0] = BLE.BLE_PROTOCOL_VERSION;
                data[1] = 0x04;
                data[19] = mParser.crc8(data);
                character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                character.setValue(data);
                boolean ret = mGatt.writeCharacteristic(character);
                if (ret) {
                    logger.error("Write device info extension is true");
                }

            }
        }, 500 * 3);
    }

    /**
     * Notification Packet Writing Start(开始写入通知)
     *
     * @return boolean
     */
    protected boolean writePhoneNotification() {
        final BluetoothGattCharacteristic commandCharacter = getCommandRequestCharacter();
        if (null == commandCharacter) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0D;
        data[19] = mParser.crc8(data);
        commandCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        commandCharacter.setValue(data);
        return mGatt.writeCharacteristic(commandCharacter);
    }

    /**
     * 导航 type:方向
     *
     * @param type
     */
    @Override
    public void writeNavigationRequest(int type, int distance) {
        BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0A;
        byte[] direction = Utils.short2Byte((short) type);
        data[2] = direction[1];
        data[3] = direction[0];
        byte[] dis = Utils.short2Byte((short) distance);
        data[4] = dis[1];
        data[5] = dis[0];
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        mGatt.writeCharacteristic(character);
        logger.info("Navigation value: " + Arrays.toString(data));
    }

    /**
     * OTA 升级开始
     *
     * @param type
     */
    @Override
    public void writeOTAStartRequest(final int type, final String versionName, final String path) {
        final BluetoothGattCharacteristic character = getCommandRequestCharacter();
        if (null == character) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(path);

                    int length = in.available();
                    in.close();

                    if (type == OTAManage.OTA_A_GPS_IMG) {
                        length = length + 512;
                    }

                    mSession.getProperty().setFileLength(length);

                    byte[] data = new byte[20];
                    data[0] = BLE.BLE_PROTOCOL_VERSION;
                    data[1] = 0x07;
                    data[2] = (byte) type;

                    String[] version = versionName.split("\\.");
                    if (version.length == 3) {
                        int v1 = Integer.valueOf(version[0]);
                        data[3] = (byte) v1;
                        int v2 = Integer.valueOf(version[1]);
                        data[4] = (byte) v2;
                        int v3 = Integer.valueOf(version[2]);
                        data[5] = (byte) v3;
                    }
                    if (length % 190 == 0) {
                        length = length / 190;
                    } else {
                        length = length / 190 + 1;
                    }

                    short count = (short) length;
                    byte[] len = Utils.short2Byte(count);
                    data[6] = len[1];
                    data[7] = len[0];
                    data[19] = mParser.crc8(data);
                    character.setValue(data);
                    mGatt.writeCharacteristic(character);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != in) {
                        try {
                            in.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }

    /**  ------------------------------  Configuration   --------------------------- */

    /**
     * 写入轮径信息
     *
     * @param wheel wheel
     */
    @Override
    public boolean writeWheel(byte wheel) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x03;
        data[2] = wheel;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 设置里程单位
     *
     * @param index 0:km 1:mile
     */
    @Override
    public boolean writeMileageUnitConfig(int index) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0C;
        if (index == 1) {
            data[2] = 0x01;
        } else {
            data[2] = 0x00;
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * Beep configuration
     *
     * @param enable enable
     */
    protected boolean writeBeepConfig(boolean enable) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x05;
        if (enable) {
            data[2] = 0x01;
        } else {
            data[2] = 0x00;
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 设置GPS位置服务配置信息
     *
     * @param enable 0:OFF 1:ON
     */
    protected boolean writeGpsConfig(boolean enable) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0B;
        if (enable) {
            data[2] = 0x01;
        } else {
            data[2] = 0x00;
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 消息通知
     *
     * @param enable enable
     */
    @Override
    public boolean writeMessageConfig(boolean enable) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x09;
        if (enable) {
            data[2] = 0x01;
        } else {
            data[2] = 0x00;
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 写入语言配置信息
     *
     * @param index index
     */
    @Override
    public boolean writeLocaleConfig(int index) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x06;
        if (index == 0) {
            data[2] = 0x00;
        } else {
            data[2] = 0x01;// en_US
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 写入背光时间
     */
    protected boolean writeBlackLightConfig(byte b) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x04;
        data[2] = b;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 设置自动灯光
     *
     * @param enable
     */
    protected boolean writeAutoLightConfig(boolean enable) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x07;
        if (enable) {
            data[2] = 0x01;
        } else {
            data[2] = 0x00;
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 同步系统时间Config
     */
    protected void writeSystemTimeConfig() {

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final BluetoothGattCharacteristic character = getConfigurationCharacter();
                if (null == character) {
                    return;
                }

                byte[] data = new byte[20];
                data[0] = BLE.BLE_PROTOCOL_VERSION;
                data[1] = 0x01;
                Calendar calendar = Calendar.getInstance();
                String year = "0x" + (calendar.get(Calendar.YEAR) - 2000);
                data[2] = Byte.decode(year);
                String month = "0x" + (calendar.get(Calendar.MONTH) + 1);
                data[3] = Byte.decode(month);
                String day = "0x" + calendar.get(Calendar.DAY_OF_MONTH);
                data[4] = Byte.decode(day);
                String hour = "0x" + calendar.get(Calendar.HOUR_OF_DAY);
                data[5] = Byte.decode(hour);
                String minute = "0x" + calendar.get(Calendar.MINUTE);
                data[6] = Byte.decode(minute);
                String second = "0x" + calendar.get(Calendar.SECOND);
                data[7] = Byte.decode(second);
                int offset = TimeZone.getDefault().getRawOffset() / 1000 / 3600;
                String timeZone = "0x";
                if (offset >= 0) {
                    timeZone = timeZone + 0 + Integer.toHexString(offset).toUpperCase();
                } else {
                    timeZone = timeZone + 1 + Integer.toHexString(Math.abs(offset)).toUpperCase();
                }
                data[8] = Byte.decode(timeZone);
                for (int i = 9; i < 19; i++) {
                    data[i] = 0x00;
                }
                data[19] = mParser.crc8(data);
                character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                character.setValue(data);
                boolean b = mGatt.writeCharacteristic(character);
                logger.info("Write system time to ble... , isWrite = " + b);
            }
        }, 500 * 2);


    }

    /**
     * 写入用户设备信息
     */
    protected void writeUserDeviceConfig() {

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final BluetoothGattCharacteristic character = getConfigurationCharacter();
                if (null == character) {
                    return;
                }

                byte[] data = new byte[20];
                data[0] = BLE.BLE_PROTOCOL_VERSION;
                data[1] = 0x0A;
                try {
                    int index = 0;
                    String deviceName = Build.MODEL;
                    if (!TextUtils.isEmpty(deviceName)) {
                        for (int i = 0; i < deviceName.length(); i++) {
                            byte[] bs = String.valueOf(deviceName.charAt(i)).getBytes("UTF-8");
                            if (index + bs.length <= 16) {
                                for (byte b : bs) {
                                    data[index + 2] = b;
                                    index += 1;
                                }
                            }
                        }
                    }
                    data[19] = mParser.crc8(data);
                    character.setValue(data);
//            character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    boolean b = mGatt.writeCharacteristic(character);
                    logger.info("writeUserDeviceConfig isWrite = " + b + "; DeviceName = " + deviceName +
                            ", data[] = " + Arrays.toString(data));
                } catch (UnsupportedEncodingException e) {
                    logger.error("writeUserDeviceConfig error, " + e);
                }
            }
        }, 500);

    }

    /**
     * 写入常用踏频
     *
     * @return boolean
     */
    @Override
    public boolean writeCadenceConfig(int value) {
        final BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0D;
        data[2] = (byte) value;
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 设置震动唤醒
     *
     * @param enable enable
     * @return boolean
     */
    @Override
    public boolean writeVibrationWakeConfig(boolean enable) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x02;
        if (enable) {
            data[2] = 0x01;
        } else {
            data[2] = 0x00;
        }
        data[19] = mParser.crc8(data);
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 用户骑行月度骑行里程设置
     *
     * @param targetType   User Cycling target type, values as below. default type: 0x00 (Monthly cycling target)
     * @param targetValue  User cycling target value, unit is meter.
     * @param currentValue User current cycling distance, according to the target type, unit is meter.
     * @return write success or fail
     */
    @Override
    public boolean writeTargetConfig(int targetType, int targetValue, int currentValue) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x08;
        data[2] = (byte) targetType;
        byte[] targetValues = Utils.int2Byte(targetValue);
        System.arraycopy(targetValues, 0, data, 3, 4);
        byte[] currentValues = Utils.int2Byte(currentValue);
        System.arraycopy(currentValues, 0, data, 7, 4);
        data[19] = mParser.crc8(data);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /**
     * 写入最大心率信息
     *
     * @param heartRate 心率
     * @return
     */
    @Override
    public boolean writeMaxHeartRateConfig(int heartRate) {
        BluetoothGattCharacteristic character = getConfigurationCharacter();
        if (null == character) {
            return false;
        }

        byte[] data = new byte[20];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = 0x0E;
        byte[] heartRates = Utils.short2Byte((short) heartRate);
        data[2] = heartRates[1];
        data[3] = heartRates[0];
        data[19] = mParser.crc8(data);
        character.setValue(data);
        return mGatt.writeCharacteristic(character);
    }

    /** --------------------------------- Phone Exchange Write ------------------------------- */

    /**
     * 写入通知
     *
     * @param title
     * @param text
     */
    @Override
    public void writeANCSNotification(final String title, final String text, final byte type) {
        boolean isStart = this.writePhoneNotification();
        if (!isStart) {
            return;
        }

        final BluetoothGattCharacteristic character = getPhoneExchangeCharacter();
        if (null == character) {
            return;
        }

        byte[] sourceData = new byte[200];
        sourceData[0] = BLE.BLE_PROTOCOL_VERSION;
        sourceData[1] = type;
        try {
            int titleIndex = 0;
            for (int i = 0; i < title.length(); i++) {
                byte b[] = String.valueOf(title.charAt(i)).getBytes("UTF-8");
                if (titleIndex + b.length <= 16) {// title的最大长度是16个字节
                    for (byte aB : b) {
                        sourceData[titleIndex + 2] = aB;
                        titleIndex += 1;
                    }
                } else {
                    break;
                }
            }

            int textIndex = 0;
            if (!TextUtils.isEmpty(text)) {
                for (int i = 0; i < text.length(); i++) {
                    byte b[] = String.valueOf(text.charAt(i)).getBytes("UTF-8");
                    if (textIndex + b.length <= 180) {// title的最大长度是16个字节，下标从18开始，所以＋18
                        for (byte aB : b) {
                            sourceData[textIndex + 18] = aB;
                            textIndex += 1;
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int crc16 = mParser.crc16(sourceData);
        byte[] crc = Utils.short2Byte((short) crc16);
        sourceData[198] = crc[1];
        sourceData[199] = crc[0];

        List<Byte> packet = new ArrayList<>();
        for (byte aSourceData : sourceData) {
            packet.add(aSourceData);
        }

        for (int i = 0; i < 10; i++) {
            List<Byte> pack = packet.subList(i * 20, i * 20 + 20);
            final byte[] data1 = new byte[20];
            for (int j = 0; j < pack.size(); j++) {
                data1[j] = pack.get(j);
            }

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            character.setValue(data1);
            character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            boolean write = mGatt.writeCharacteristic(character);
            logger.trace("Write notification to ble device is " + write);
        }
    }

    /** ---------------------------------   OTA INFO WRITE  ----------------------------- */
    /**
     * 写入OTA 升级包
     *
     * @param type
     * @param packetIndex
     * @param packetCount
     * @param list
     */
    protected void writeOTABlePacket(final int type, final int packetIndex, final int packetCount,
                                     final List<Byte> list, final int imgLength) {
        if (null == list || list.isEmpty()) {
            return;
        }

        final BluetoothGattCharacteristic character = getOtaInfoCharacter();
        if (null == character) {
            return;
        }

        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        byte[] data = new byte[200];
        data[0] = BLE.BLE_PROTOCOL_VERSION;
        data[1] = (byte) type;
        int packetTotalCount = packetCount / 190;
        byte[] count = Utils.short2Byte((short) packetTotalCount);
        data[2] = count[1];
        data[3] = count[0];
        byte[] index = Utils.short2Byte((short) packetIndex);
        data[4] = index[1];
        data[5] = index[0];
        byte[] length = Utils.short2Byte((short) 190);
        if (packetTotalCount - 1 == packetIndex) {
            int diff = packetCount - imgLength;
            logger.trace("Icedan", "写入ota数据的总个数 ＝ " + packetCount + "; 实际总长 ＝ " + imgLength + "; 差 ＝" + diff + ";长 ＝" + (190 - diff));
            length = Utils.short2Byte((short) (190 - diff));
        }

        data[6] = length[1];
        data[7] = length[0];
        for (int i = 0; i < 190; i++) {
            if (packetIndex * 190 + i >= list.size()) {
                return;
            }
            data[i + 8] = list.get(packetIndex * 190 + i);
        }
        int crc16 = mParser.crc16(data);
        byte[] crc = Utils.short2Byte((short) crc16);
        data[198] = crc[1];
        data[199] = crc[0];

        List<Byte> packet = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            packet.add(data[i]);
        }

        for (int i = 0; i < 10; i++) {
            List<Byte> pack = packet.subList(i * 20, i * 20 + 20);
            final byte[] data1 = new byte[20];
            for (int j = 0; j < pack.size(); j++) {
                data1[j] = pack.get(j);
            }

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            character.setValue(data1);
            mGatt.writeCharacteristic(character);
        }
    }

    private void sleep(long time) {
        try {
            logger.warn("Thread [" + Thread.currentThread().getName() + "] sleep " + time);
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ble Auth Response
     */
    private synchronized void handleAuthResponse() {

        if (this.mSession.getProperty().isHasAuthkey()) {
            logger.info("此次连接已经认证过");
            return;
        }

        logger.info("handleAuthResponse");
        registerNotify();
        this.mSession.getProperty().setHasAuthkey(true);
    }

    private void registerNotify() {

        if (mGatt == null)
            return;

        logger.info("registerNotify");

        Intent intent = new Intent(DiscoveryActivity.BLE_CONNECTED_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.sendBroadcast(intent);

        this.enableTxNotification();
        this.writeUserDeviceConfig();
        this.writeSystemTimeConfig();
        this.writeDeviceInfoExtensionRequest();
        this.writeDeviceInfoRequest();
        this.writeAgpsInfoRequest();


        CentralSession session = CentralSessionHandler.getInstance().sessionMatch(mGatt.getDevice());

        if (session != null) {
            session.setAutoAttach(true);
            mBleManager.createOrUpdateBleDevice(session);
        }
    }

    /**
     * 初始化BluetoothGattCharacteristic并且写入认证信息、系统时间、设备信息
     */
    private void enableTxNotification() {
        logger.trace("enableTxNotification ==============");
        InvocationParam status = this.mSession.getProperty();
        if (!status.isWriteCD20()) {
            boolean isWriteCD20 = writeActivitySampleNotification();
            status.setWriteCD20(isWriteCD20);
        }
        if (!status.isWriteCD26()) {
            boolean isWriteCD26 = writeActivitySyncNotification();
            status.setWriteCD26(isWriteCD26);
        }
        if (!status.isWriteCD24()) {
            boolean isWriteCD24 = writeCommandResponseNotification();
            status.setWriteCD24(isWriteCD24);
        }
        if (!status.isWriteCD22()) {
            boolean isWriteCD22 = writeSensorNotification();
            status.setWriteCD22(isWriteCD22);
        }
    }


    /**
     * 手机向把立写入的数据
     *
     * @param characteristic characteristic
     */
    private void fireCharacteristicWrite(BluetoothGattCharacteristic characteristic) {
        if (null == characteristic) {
            return;
        }

        final InvocationParam status = this.mSession.getProperty();

        if (!status.isWriteCD24()) {// 查看command notification 是否注册成功, 如果没有注册成功则再次注册
            logger.trace("查看command notification 是否注册成功, 如果没有注册成功则再次注册");
            boolean isWriteCD24 = this.writeCommandResponseNotification();
            status.setWriteCD24(isWriteCD24);
        }

        if (!status.isWriteCD20()) {// 查看实时预览数据的notification是否注册成功，如果没有则再次注册
            logger.trace("查看实时预览数据的notification是否注册成功，如果没有则再次注册");
            boolean isWriteCD20 = this.writeActivitySampleNotification();
            status.setWriteCD20(isWriteCD20);
        }

        if (!status.isWriteCD26()) {// 查看数据同步的notification是否注册成功，如果没有则再次注册
            logger.trace("查看数据同步的notification是否注册成功，如果没有则再次注册");
            boolean isWriteCD26 = this.writeActivitySyncNotification();
            status.setWriteCD26(isWriteCD26);
        }

        if (!status.isWriteCD22()) {// 查看Sensor Notification 是否注册成功，如果没有则再次注册
            logger.trace("查看Sensor Notification 是否注册成功，如果没有则再次注册");
            boolean isWriteCD22 = this.writeSensorNotification();
            status.setWriteCD22(isWriteCD22);
        }

        final byte[] value = characteristic.getValue();
        if (null == value || value.length < 20) {
            return;
        }

        final String uuid = characteristic.getUuid().toString();
        if (uuid.equals(BLE.UUID_CONFIGURATION_WRITE.toString())) {// Configuration 设置把立配置信息
        }

        if (uuid.equals(BLE.UUID_COMMAND_REQUEST_WRITE.toString())) {// Command Request 数据请求命令
            byte commandType = value[1];
            switch (commandType) {
                case 0x06:// Activity Synchronization 数据同步开始
                    if (null != refreshCyclingDataListener) {
                        refreshCyclingDataListener.onSyncStart();
                    }
                    break;
                case 0x07:// OTA Start (OTA升级开始指令)
                    if (null == this.otaInfoList) {
                        this.otaInfoList = new ArrayList<>();
                    }

                    this.otaInfoList.clear();
                    byte processType = value[2];
                    this.getOtaInfoList(processType);
                    break;
                case 0x0F:// 结束骑行
                    mCentralServiceHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            writeActivityPreviewRequest();
                            status.getActivityData().clear();
                        }
                    }, 3000);
                    break;
            }
        }
    }

    /**
     * Bond BlueDevice
     *
     * @param gatt
     */
    private void createBond(BluetoothGatt gatt) {
        if (null == gatt) {
            return;
        }
        try {
            BluetoothDevice device = gatt.getDevice();

            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                Method createBond = BluetoothDevice.class.getMethod("createBond");
                createBond.invoke(device);
                logger.info("createBond device = [" + device.getName() + "] start");
            }
        } catch (Exception e) {
            logger.error("createBond ble device error,  " + e);
        }
    }

    /**
     * 获取OTA INFO
     *
     * @param progressType
     */
    private void getOtaInfoList(int progressType) {
        final SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        String img = "";
        switch (progressType) {
            case 0x01:// OTA BLE
                img = sp.getString(BLE.PREF_BLE_IMG_KEY, "");
                break;
            case 0x02:// OTA MCU
                img = sp.getString(BLE.PREF_MCU_IMG_KEY, "");
                break;
            case 0x03:// OTA UI
                img = sp.getString(BLE.PREF_UI_IMG_KEY, "");
                break;
            case 0x04:// OTA APGS
                img = sp.getString(BLE.PREF_A_GPS_IMG_KEY, "");
                break;
            case 0x05:// OTA FONT
                img = sp.getString(BLE.PREF_FONT_IMG_KEY, "");
                break;
            case 0x06:// OTA POWER
                img = sp.getString(BLE.PREF_POWER_IMG_KEY, "");
                break;
        }

        if (TextUtils.isEmpty(img)) {
            return;
        }
        FileInputStream in = null;
        try {
            JSONObject imgJson = new JSONObject(img);
            String filePath = imgJson.optString("path");
            if (TextUtils.isEmpty(filePath)) {
                return;
            }

            in = new FileInputStream(filePath);
            int length = in.available();

            byte[] buffer = new byte[length];
            in.read(buffer);
            in.close();

            if (progressType == 0x04) {
                byte[] gpsHeader = new byte[512];
                int checkSum = mParser.getCheckSum(buffer);
                byte[] checkSumData = Utils.int2Byte(checkSum);
                System.arraycopy(checkSumData, 0, gpsHeader, 0, checkSumData.length);
                byte[] lengthData = Utils.int2Byte(length);
                System.arraycopy(lengthData, 0, gpsHeader, 4, lengthData.length);

                byte[] timeData = Utils.int2Byte((int) (System.currentTimeMillis() / 1000));
                System.arraycopy(timeData, 0, gpsHeader, 508, timeData.length);

                for (byte aGpsHeader : gpsHeader) {
                    this.otaInfoList.add(aGpsHeader);
                }

                length = length + 512;
            }

            this.mSession.getProperty().setImgLength(length);

            for (byte aBuffer : buffer) {
                this.otaInfoList.add(aBuffer);
            }

            int count = 0;
            if (length % 190 == 0) {
                count = length / 190;
            } else {
                count = length / 190 + 1;
                int fullCount = count * 190;
                for (int i = length; i < fullCount; i++) {
                    this.otaInfoList.add((byte) 0x00);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * 解析Ble返回的数据
     *
     * @param characteristic ble response data
     */
    public void handleCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
        if (null == characteristic)
            return;

        final String uuid = characteristic.getUuid().toString();

        byte[] data = characteristic.getValue();
        if (null == data || data.length < 20) {
            return;
        }

        if (uuid.equals(BLE.UUID_COMMAND_NOTIFICATION.toString())) {// Command Response notification
            if (data.length < 2) {
                return;
            }

            switch (data[1]) {
                case 0x01:// Device Information
                    final DeviceInfoCommandCharacteristic deviceInfo = (DeviceInfoCommandCharacteristic)
                            mParser.parseCommandCharacteristic(data);
                    if (null != deviceInfo) {

                        CentralSession session = CentralSessionHandler.getInstance().sessionMatch(mGatt.getDevice());
                        if (session == null)
                            return;
                        logger.info("设备信息：" + deviceInfo.toString());

                        boolean saveToServer = true;

                        String centralId = CentralSession.address2CentralId(mGatt.getDevice().getAddress());

                        //保存到服务器
                        BleDevice bleDevice = mBleManager.saveDeviceToServer(
                                session.getCentralId(), session.getName(),
                                deviceInfo.getHardwareType(), deviceInfo.getBrandType(), mGatt.getDevice().getAddress(), saveToServer);

                        String frameId = "";
                        String bikeImage = "";

                        if (bleDevice == null) {
                            bleDevice = new BleDevice();
                        }

                        frameId = bleDevice.getFrameId();
                        bikeImage = bleDevice.getUrl();
                        bleDevice.setDeviceName(mGatt.getDevice().getName());
                        bleDevice.setHardwareType(deviceInfo.getHardwareType());
                        bleDevice.setBrandType(deviceInfo.getBrandType());
                        bleDevice.setMacAddress(centralId);
                        bleDevice.setDeviceId(mGatt.getDevice().getAddress());

                        //更新本地数据库
                        mBleManager.updateBleDeviceInfo(centralId, mGatt.getDevice().getAddress(), deviceInfo.getHardwareType(), deviceInfo.getBrandType(), bikeImage, frameId);
                        session.getProperty().setDeviceInfo(deviceInfo);
                        if (null != this.responseDeviceInfoListener) {

                            final BleDevice finalBleDevice = bleDevice;
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    responseDeviceInfoListener.onResponseDevice(finalBleDevice, deviceInfo);
                                }
                            });
                        }
                    }


                    break;
                case 0x02:// AGPS Information
                    final AGpsInfoCharacteristic agpsInfo = (AGpsInfoCharacteristic) mParser.parseCommandCharacteristic(data);
                    if (null != agpsInfo) {
                        if (null != aGPSListener) {
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    aGPSListener.onAPGSInfoResponse(agpsInfo);
                                }
                            });
                        }

                        if (null != mGatt.getDevice()) {
                            long currTime = System.currentTimeMillis();
                            long lastUpdateTime = this.mUserSp.getLong(mGatt.getDevice().getAddress(), 0);
                            if (currTime - lastUpdateTime < 3600 * 1000 * 24) {
                                this.writeActivityPreviewRequest();
                                break;
                            }
                        }

                        int updateTime = agpsInfo.getUpdateTime();
                        long dayCount = ((System.currentTimeMillis() / 1000) - updateTime) / (3600 * 24);
                        logger.info("距离上次更新A_GPS数据的天数：" + dayCount);
                        if (dayCount >= 12) {
                            this.downAGPSFile();
                        } else {
                            logger.info("14天内已更新过A_GPS数据");
                            this.writeActivityPreviewRequest();
                        }
                    } else if (this.mSession.getProperty().isUpdateGps()) {
                        this.writeActivityPreviewRequest();
                    }
                    break;
                case 0x04:// Device Info Extension //
                    DeviceInfoExtensionCharacteristic device = (DeviceInfoExtensionCharacteristic)
                            mParser.parseCommandCharacteristic(data);
                    if (null != device) {

                        String macAddress = CentralSession.address2CentralId(mGatt.getDevice().getAddress());

                        logger.info("DeviceInfo");

                        device.setMacAddr(macAddress);
                        mSession.getProperty().setGuaranteeTime(device.getGuaranteeTime());
                        int totalDistance = device.getTotalDistance() * 1000;

                        CentralSession session = CentralSessionHandler.getInstance().sessionMatch(mGatt.getDevice());

                        mBleManager.createOrUpdateBleDevice(session);
                        if (responseMacAddressListener != null) {
                            if (session != null) {
                                responseMacAddressListener.onResponseMacAddress(session.getCentralId(), totalDistance);
                            }
                        }
                    }
                    break;
                case 0x06: {// Receive Response
                    logger.trace("write to ble receive response = " + Arrays.toString(data));
                    byte flag = data[2];
                    switch (flag) {
                        case 0x01:// Success
                            break;
                        case 0x02:// Fail
                            break;
                        case 0x03:// Timeout
                            logger.error("write to ble ota packet timeout...");
                            break;
                        case 0x04:// CRC Check Fail
                            break;
                    }
                    break;
                }
                case 0x07:// OTA Request
                    OTARequestCommandCharacteristic otaRequest = (OTARequestCommandCharacteristic)
                            mParser.parseCommandCharacteristic(data);
                    if (null == otaRequest) {
                        return;
                    }

                    logger.trace("Ota character = " + otaRequest.toString());
                    int flags = data[3];
                    switch (flags) {
                        case 0x01:// OTA request packet number 请求开始发送数据包
                            if (null != refreshOTAPacketListener) {
                                refreshOTAPacketListener.onRefreshCount(otaRequest.getRequestPacketIndex());
                            }

                            if (otaRequest.getProcessType() == 0x04) {

                                if (null != aGPSListener && otaRequest.getRequestPacketIndex() > 20) {
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            aGPSListener.onSyncing();
                                        }
                                    });
                                }
                            }

                            logger.trace("请求OTA数据包" + otaRequest.getProcessType());

                            Message message = mCentralServiceHandler.
                                    obtainMessage(CentralService.MSG_OTA_UPDATE, otaRequest);
                            mCentralServiceHandler.sendMessage(message);
                            break;
                        case 0x02:// OTA data transfer end success ble接收数据包结束
                            logger.info("ble接收Packet 数据包成功");
                            int type = otaRequest.getProcessType();
                            if (null != refreshOTAPacketListener) {
                                refreshOTAPacketListener.onOTADataEnd(type);
                            }
                            int otaUpdateByte = this.mSession.getProperty().getOtaUpdateByte();
                            switch (type) {
                                case 0x01://  OTA_BLE
                                    otaUpdateByte = otaUpdateByte | PROCESS_TYPE_OTA_BLE;
                                    this.mSession.getProperty().setOtaUpdateByte(otaUpdateByte);
                                    this.writeOTAStartRequest(0x02);
                                    break;
                                case 0x02:// OTA_MCU
                                    otaUpdateByte = otaUpdateByte | PROCESS_TYPE_OTA_MCU;
                                    this.mSession.getProperty().setOtaUpdateByte(otaUpdateByte);
                                    this.writeOTAStartRequest(0x03);
                                    break;
                                case 0x03:// OTA_UI
                                    otaUpdateByte = otaUpdateByte | PROCESS_TYPE_OTA_UI;
                                    this.mSession.getProperty().setOtaUpdateByte(otaUpdateByte);
                                    this.writeOTAStartRequest(0x05);
                                    break;
                                case 0x04:// OTA_A_GPS_IMG
                                    mUserSp.edit().putLong(BLE.PREF_BLE_AGPS_LAST_UPDATE_TIME_KEY,
                                            System.currentTimeMillis()).apply();
                                    break;
                                case 0x05:// OTA FONT
                                    otaUpdateByte = otaUpdateByte | PROCESS_TYPE_OTA_FONT;
                                    this.mSession.getProperty().setOtaUpdateByte(otaUpdateByte);
                                    if (this.mSession.getProperty().isHasPowerImg()) {
                                        this.writeOTAStartRequest(0x06);
                                    } else {
                                        this.writeOtaActive(otaUpdateByte);
                                    }
                                    break;
                                case 0x06:// OTA POWER
                                    otaUpdateByte = otaUpdateByte | PROCESS_TYPE_OTA_POWER;
                                    this.mSession.getProperty().setOtaUpdateByte(otaUpdateByte);
                                    this.writeOtaActive(otaUpdateByte);
                                    break;
                            }
                            this.deleteImg(type);
                            break;
                        case 0x03:// OTA data transfer end error
                            Message msg = mCentralServiceHandler.
                                    obtainMessage(CentralService.MSG_OTA_UPDATE, otaRequest);

                            mCentralServiceHandler.sendMessage(msg);
                            if (null != refreshOTAPacketListener) {
                                refreshOTAPacketListener.onParserDataError();
                            }
                            break;
                        case 0x04:// OTA activate end success
                            int processType = otaRequest.getProcessType();
                            switch (processType) {
                                case 0x01://  OTA_BLE
                                    break;
                                case 0x02:// OTA_MCU
                                    break;
                                case 0x03:// OTA_UI
                                    this.deleteImg(processType);
                                    break;
                                case 0x04:// OTA_A_GPS_IMG
                                    break;
                                case 0x05:// OTA FONT
                                    break;
                            }
                            break;
                        case 0x05:// OTA activate end error
                            break;
                    }
                    break;
                case 0x08:// Request Response
                    // 当APP请求把立端时，当产生相关错误信息时，回馈给客户端，并说明相关错误代码及信息
                    logger.error("当APP请求把立端时，把立回复的信息, ProcessType = " + data[2] + ", ErrorCode = " + data[3]);
                    switch (data[3]) {
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x04:
                        case 0x05:
                        case 0x06:// 预览数据出错
                            if (null != previewCyclingListener) {
                                previewCyclingListener.onPreviewError();
                            }
                            break;
                        case 0x10:
                        case 0x11:
                        case 0x12:
                        case 0x13:
                        case 0x14:
                        case 0x15:
                            if (null != refreshCyclingDataListener) {
                                refreshCyclingDataListener.onSyncError(data[3]);
                            }
                            break;
                    }
                    break;
                case 0x09:// ANCS通知应答
                    int responseType = data[2];
                    switch (responseType) {
                        case 0x01:// 挂电话
                            this.endCall();
                            break;
                    }
                    break;
                case 0x0A: //Auth Response
                    handleAuthResponse();
                    break;
            }
        }

        if (uuid.equals(BLE.UUID_SENSOR_NOTIFI.toString())) {// Sensors Notification CD22
            switch (data[1]) {
                case 0x04:// Battery Sensor
                    logger.info("onCharacteristicChanged: " + characteristic.getUuid().toString() + ", "
                            + Arrays.toString(characteristic.getValue()) + ", " +
                            "Thread [" + Thread.currentThread().getName() + "]");
                    final BatterySensorCharacteristic battery = (BatterySensorCharacteristic)
                            mParser.parseSensorCharacteristic(data);
                    if (null != responseDeviceInfoListener) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                responseDeviceInfoListener.onResponseBatterySensor(battery);
                            }
                        });
                    }
                    break;
            }
        }

        if (uuid.equals(BLE.UUID_ACTIVITY_SYNC_NOTIFI.toString())) {// Packet Notification
            for (byte b : data) {
                this.mSession.getProperty().getActivityData().add(b);
            }

            if (this.mSession.getProperty().getActivityData().size() != 200) {
                logger.trace("大数据解析: 数据收集到的个数: " + this.mSession.getProperty().getActivityData().size() + "\n" +
                    this.mSession.getProperty().getActivityData().toString());
                return;
            }

            byte cyclingType = 0x01;
            byte[] cyclingData = new byte[200];
            for (int i = 0; i < cyclingData.length; i++) {
                cyclingData[i] = this.mSession.getProperty().getActivityData().get(i);
            }
            if (cyclingData.length > 2) {
                cyclingType = cyclingData[1];
            }

            this.mSession.getProperty().cleanActivityData();
            logger.trace("大数据解析", "数据包 = " + Arrays.toString(cyclingData));

            if (cyclingType == 0x01) {// Ride Preview
                PreviewDataCharacteristic character = (PreviewDataCharacteristic)
                        mParser.parseSyncDataCharacteristic(cyclingData);

                logger.info("预览数据解析 = " + character);

                if (null != character) {
                    // 写入接收成功
                    int currentIndex = character.getCurrentPacketIndex();
                    this.writeReceiveResponse((byte) 1, (byte) 1, currentIndex);
                    Map<String, Object> map = new HashMap<>();
                    String centralId = CentralSession.address2CentralId(mGatt.getDevice().getAddress());
                    map.put("character", character);
                    map.put("macAddress", centralId);
                    Message msg = new Message();
                    msg.what = MSG_PREVIEW_CYCLING;
                    msg.obj = map;
                    mSyncDataHandler.sendMessage(msg);
                    if (null != previewCyclingListener && null != mGatt.getDevice() &&
                            character.getCurrentPacketIndex() + 1 == character.getTotalPacketCount()) {
                        previewCyclingListener.onPreviewEnd();
                    }
                } else {
                    // 写入接收失败
                    this.writeReceiveResponse((byte) 2, (byte) 1, 0);
                    if (null != previewCyclingListener) {
                        previewCyclingListener.onPreviewError();
                        logger.error("预览数据时，数据解析失败");
                    }
                }

                this.mSession.getProperty().getActivityData().clear();
            }

            if (cyclingType == 0x02) {// Ride Synchronization
                if (mSession != null && mSession.getProperty().isCancelSync()) {
                    logger.info("手动点击取消同步数据");
                    return;
                }
                SynchronizationDataCharacteristic syncCharacter = (SynchronizationDataCharacteristic)
                        mParser.parseSyncDataCharacteristic(cyclingData);

                if (null != syncCharacter) {
                    if (syncCharacter.getTotalPacketCount() > 0) {
                        logger.info("同步数据 = " + syncCharacter.toString());
                        String activityId = "";
                        if (null != mUserSp) {
                            activityId = mUserSp.getString(SpeedXDialogFragment.PREF_BLE_CYCLING_SYNC_ACTIVITY_ID, "");
                        }

                        logger.info("同步数据的activityId = " + activityId);

                        int currentIndex = syncCharacter.getCurrentPacketIndex();
                        // 写入接收成功
                        this.writeReceiveResponse((byte) 1, (byte) 2, currentIndex);
                        Map<String, Object> map = new HashMap<>();
                        map.put("character", syncCharacter);
                        map.put("activityId", activityId);
                        Message msg = new Message();
                        msg.what = MSG_SYNC_CYCLING;
                        msg.obj = map;
                        mSyncDataHandler.sendMessage(msg);
//                        this.saveBleCyclings(activityId, syncCharacter);

                        this.mSession.getProperty().setPreviewIndex(currentIndex + 1);
                        int totalPacketCount = syncCharacter.getTotalPacketCount();

                        if (null != refreshCyclingDataListener) {
                            refreshCyclingDataListener.onSyncing(currentIndex, totalPacketCount);
                        }

                        if (currentIndex == totalPacketCount - 1) {
                            logger.info("previewIndex = " + currentIndex + "; PacketCount = " + totalPacketCount);
                            if (null != refreshCyclingDataListener) {
                                refreshCyclingDataListener.onSyncEnd();
                            }

                            this.mSession.getProperty().setPreviewIndex(0);
                        }
                    }
                } else {
                    // 写入接收失败
                    this.writeReceiveResponse((byte) 2, (byte) 2, this.mSession.getProperty().getPreviewIndex());
                    logger.error("Sync data null");
                    if (null != refreshCyclingDataListener) {
                        refreshCyclingDataListener.onSyncError(-1);
                        logger.error("同步数据时，数据解析失败");
                    }
                }
                this.mSession.getProperty().getActivityData().clear();
            }

            if (cyclingType == 0x03) {
                final OTAFirmwareInfoCharacteristic otaInfo =
                        (OTAFirmwareInfoCharacteristic) mParser.parseSyncDataCharacteristic(cyclingData);
                if (null != checkSumListener) {
                    if (null == otaInfo) {
                        logger.error("OTA固件版本信息解析失败");
                    }
                    checkSumListener.onOtaCheckSumResponse(otaInfo);
                }

                this.mSession.getProperty().getActivityData().clear();
            }

            this.mSession.getProperty().getActivityData().clear();
        }
    }


    /**
     * ota 升级激活
     *
     * @param otaUpdateByte ota update byte
     */
    private void writeOtaActive(int otaUpdateByte) {

        boolean isWrite = this.writeOTAActive(otaUpdateByte);

        if (isWrite && null != refreshOTAPacketListener) {
            refreshOTAPacketListener.onOTADataEnd(-1);
        }
    }


    /**
     * 请求AGPS Info
     */
    private void writeAgpsInfoRequest() {
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (writeAGPSInfoRequest()) {
                    mSession.getProperty().setUpdateGps(true);
                    logger.info("Write agps info is true");
                }
            }
        }, 500 * 5);
    }

    /**
     * 处理OTA升级
     *
     * @param obj obj
     */
    public void handleOTAPacketUpdate(Object obj) {
        logger.info("接收到开始传输数据命令");
        if (obj != null) {
            OTARequestCommandCharacteristic ota = (OTARequestCommandCharacteristic) obj;
            if (!this.mSession.getProperty().isCancelUpdate()) {
                this.writeOTAPacketStart(ota);
            }
        }
    }

    /**
     * 处理连接断开
     */
    public void handleConnectionDisconnect() {
        if (null != this.refreshOTAPacketListener) {
            this.refreshOTAPacketListener.onOTADataEnd(0);
        }
    }

    public void handleServiceDiscovered(Object obj, int status) {
        if (obj == null) return;

        this.mSession = (CentralSession) obj;
        this.mGatt = mSession.getBluetoothGatt();

        if (!mSession.getProperty().isWriteCD24()) {
            boolean isWriteCD24 = this.writeCommandResponseNotification();
            mSession.getProperty().setWriteCD24(isWriteCD24);
        }

        logger.info("doRequestBleAuth isWriteCD24=[" + this.mSession.getProperty().isWriteCD24() + "]");
        // 耗时操作
        sleep(100);
        writeAuthKey();
    }

    /**
     * @param obj
     * @param status
     */
    public void handleCharacteristicWrite(Object obj, int status) {
        if (obj == null) return;

        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) obj;

        if (BluetoothGatt.GATT_SUCCESS == status) {
            fireCharacteristicWrite(characteristic);
        }
    }

    /**
     * 开始写入OTA升级包
     * 0x01 : OTA_BLE
     * 0x02 : OTA_MCU
     * 0x03 : OTA_UI
     * 0x04 : OTA_A_GPS_IMG
     *
     * @param characteristic Ota Request Command Characteristic
     */
    private void writeOTAPacketStart(OTARequestCommandCharacteristic characteristic) {
        if (null == characteristic) {
            return;
        }

        boolean isWrite = this.writeOTAPacketStart();
        if (!isWrite) return;

        final int packetType = characteristic.getProcessType();
        final int packetIndex = characteristic.getRequestPacketIndex();

        if (null == this.otaInfoList || this.otaInfoList.isEmpty()) {
            getOtaInfoList(packetType);
        }

        this.writeOTABlePacket(packetType, packetIndex, otaInfoList.size(), otaInfoList, mSession.getProperty().getImgLength());
    }

    /**
     * OTA 升级开始
     *
     * @param type isOta true:固件升级, false:APGS更新
     * @param type type
     */
    protected void writeOTAStartRequest(final int type) {
        final SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        String mcu = "";
        switch (type) {
            case 0x01:// ble img
                mcu = sp.getString(BLE.PREF_BLE_IMG_KEY, "");
                break;
            case 0x02:// mcu img
                mcu = sp.getString(BLE.PREF_MCU_IMG_KEY, "");
                break;
            case 0x03:// ui
                mcu = sp.getString(BLE.PREF_UI_IMG_KEY, "");
                break;
            case 0x04:// A_GPS
                mcu = "";
                break;
            case 0x05:// font
                mcu = sp.getString(BLE.PREF_FONT_IMG_KEY, "");
                break;
            case 0x06:// power
                mcu = sp.getString(BLE.PREF_POWER_IMG_KEY, "");
                break;
        }

        String powerMcu = sp.getString(BLE.PREF_POWER_IMG_KEY, "");
        this.mSession.getProperty().setHasPowerImg(!TextUtils.isEmpty(powerMcu));

        if (TextUtils.isEmpty(mcu)) {
            int nextType = type + 1;
            if (nextType <= 6) {
                writeOTAStartRequest(nextType);
            } else {
                this.writeOtaActive(mSession.getProperty().getOtaUpdateByte());
            }
            return;
        }

        try {
            JSONObject mcuJson = new JSONObject(mcu);
            final String path = mcuJson.optString("path");
            final String version = mcuJson.optString("version");

            if (TextUtils.isEmpty(path)) {
                return;
            }

            this.writeOTAStartRequest(type, version, path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除OTA镜像文件
     *
     * @param type type
     */
    private void deleteImg(int type) {
        final SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        String prefKey = "";
        String mcu = "";
        switch (type) {
            case 0x01:// ble img
                prefKey = BLE.PREF_BLE_IMG_KEY;
                mcu = sp.getString(BLE.PREF_BLE_IMG_KEY, "");
                break;
            case 0x02:// mcu img
                prefKey = BLE.PREF_MCU_IMG_KEY;
                mcu = sp.getString(BLE.PREF_MCU_IMG_KEY, "");
                break;
            case 0x03:// ui img
                prefKey = BLE.PREF_UI_IMG_KEY;
                mcu = sp.getString(BLE.PREF_UI_IMG_KEY, "");
                break;
            case 0x04:// a_gps img
                prefKey = BLE.PREF_A_GPS_IMG_KEY;
                mcu = sp.getString(BLE.PREF_A_GPS_IMG_KEY, "");
                break;
            case 0x05:// font img
                prefKey = BLE.PREF_FONT_IMG_KEY;
                mcu = sp.getString(BLE.PREF_FONT_IMG_KEY, "");
                break;
            case 0x06:// power img
                prefKey = BLE.PREF_POWER_IMG_KEY;
                mcu = sp.getString(BLE.PREF_POWER_IMG_KEY, "");
                break;
        }

        if (TextUtils.isEmpty(mcu)) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(mcu);
            String filePath = jsonObject.optString("path");
            if (TextUtils.isEmpty(filePath)) {
                return;
            }

            if (FileUtil.deleteFile(filePath)) {
                sp.edit().remove(prefKey).commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 接听电话
     */
    private void answerCall() {
        TelephonyManager telMag = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            mthEndCall.setAccessible(true);
            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
                    (Object[]) null);
            iTel.answerRingingCall();
        } catch (Exception e) {
            logger.error("Ble设备请求接听电话失败，" + e);
        }
    }

    /**
     * 挂电话
     */
    private void endCall() {
        TelephonyManager telMag = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            mthEndCall.setAccessible(true);
            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
                    (Object[]) null);
            iTel.endCall();
            logger.info("挂断电话");
        } catch (Exception e) {
            logger.error("Ble设备请求挂断电话失败，" + e);
        }
    }

    /**
     * 保存预览数据
     */
    private void savePreviewActivity(Object value) {
        if (null == mGatt.getDevice()) {
            return;
        }

        HashMap<String, Object> map = (HashMap<String, Object>) value;
        PreviewDataCharacteristic characteristic = (PreviewDataCharacteristic) map.get("character");
        String macAddress = (String) map.get("macAddress");

        mBleManager.savePreviewActivity(characteristic, mGatt.getDevice().getAddress(), macAddress);
    }

    /**
     * 保存打点数据
     */
    private void saveBleCycling(Object value) {
        logger.trace("开始保存打点数据");
        HashMap<String, Object> map = (HashMap<String, Object>) value;
        SynchronizationDataCharacteristic characteristic = (SynchronizationDataCharacteristic) map.get("character");
        String activityId = (String) map.get("activityId");

        mBleManager.saveBleSamples(activityId, characteristic);
    }

    /**
     * 下载星历文件
     */
    private void downAGPSFile() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }

        OTAManage otaManage = new OTAManage(mContext, this);
        String agpsUrl = "http://alp.u-blox.com/current_14d.alp";
        File file = otaManage.getFile(agpsUrl);
        if (null != file && file.exists()) {
            FileUtil.deleteFile(file.getAbsolutePath());
        }

        otaManage.downLoadFile(OTAManage.OTA_A_GPS_IMG, "1.0.1", agpsUrl, 0);
    }

    @Override
    public void onDownloadFileSuccess(int type, String versionName, String filePath) {
        if (type == OTAManage.OTA_A_GPS_IMG) {
            logger.info("Download A_GPS img success");
            mSession.getProperty().setCancelUpdate(false);
            this.writeOTAStartRequest(type, versionName, filePath);
            if (null != mGatt.getDevice()) {
                this.mUserSp.edit().putLong(mGatt.getDevice().getAddress(), System.currentTimeMillis()).apply();
            }
            if (null != aGPSListener) {
                aGPSListener.onSyncAGPSStart();
            }
        }
    }

    @Override
    public void onDownloadFileError(int type) {
        logger.error("Download file error, file type = " + type);
    }

}