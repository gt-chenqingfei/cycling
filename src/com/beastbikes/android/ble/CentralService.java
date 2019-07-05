package com.beastbikes.android.ble;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.biz.CentralConnector;
import com.beastbikes.android.ble.biz.CentralInvocation;
import com.beastbikes.android.ble.biz.CentralScanner;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.listener.IScanResultListener;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@TargetApi(19)
public class CentralService extends NotificationListenerService implements Constants,
        CentralScanner.ScanCallBack, CentralConnector.ConnectorHandlerListener {

    public static final String EXTRA_CENTRAL_ID = "extra_central_id";
    public static final String ACTION_CENTRAL_CONTROL = "com.beastbikes.android.ble.intent.action.CENTRAL_CONTROL";
    public static final String EXTRA_CMD = "action";
    public static final String CMD_CENTRAL_STOP_SCAN = "com.beastbikes.android.ble.intent.action.CENTRAL_STOP_SCAN";
    public static final String CMD_CENTRAL_START_SCAN = "com.beastbikes.android.ble.intent.action.CENTRAL_START_SCAN";
    public static final String CMD_CENTRAL_SCAN_AND_CONNECT = "com.beastbikes.android.ble.intent.action.CENTRAL_SCAN_AND_CONNECT";
    public static final String CMD_CENTRAL_CONNECT = "com.beastbikes.android.ble.intent.action.CENTRAL_CONNECT";
    public static final String CMD_CENTRAL_DISCONNECT = "com.beastbikes.android.ble.intent.action.CENTRAL_DISCONNECT";
    public static final String CMD_CENTRAL_UNBOUND = "com.beastbikes.android.ble.intent.action.CENTRAL_UNBOUND";
    private static final String PACKAGE_NAME_ANDROID_INCALL = "com.android.incallui";
    private static final String PACKAGE_NAME_SETTING = "com.android.settings";

    public static final int MSG_OTA_UPDATE = 1;
    public static final int MSG_GATT_CONNECTION_DISCONNECT = 2;
    public static final int MSG_GATT_SERVICES_DISCOVERED = 3;
    public static final int MSG_GATT_CHARACTERISTIC_WRITE = 4;

    private static final Logger logger = LoggerFactory.getLogger("CentralService");
    private BluetoothAdapter mBluetoothAdapter;
    private SharedPreferences mUserSp;
    private CentralScanner mScanner;
    private CentralConnector mConnector;
    private CentralInvocation mCBManager;
    private volatile Looper mCentralServiceLooper;
    private volatile CentralServiceHandler mCentralServiceHandler;
    private CentralStateReceiver mStateReceiver;
    private IScanResultListener mICentralServiceListener;

    /**
     * 通过Binder的机制实现activity 和 service的通信
     * <p>
     * 1. 在activity中通过bind的方式 得到 service 实例
     * <p>
     * 2. 用得到的实例去注册 listener
     * </p>
     */
    public class ICentralBinder extends Binder {
        /**
         * get instance of {@link CentralService}
         *
         * @return instance of {@link CentralService}
         */
        public CentralService getService() {

            return CentralService.this;
        }
    }

    private final class CentralServiceHandler extends Handler {
        public CentralServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OTA_UPDATE:
                    mCBManager.handleOTAPacketUpdate(msg.obj);
                    break;
                case MSG_GATT_CONNECTION_DISCONNECT:
                    mCBManager.handleConnectionDisconnect();
                    break;
                case MSG_GATT_SERVICES_DISCOVERED:
                    mCBManager.handleServiceDiscovered(msg.obj, msg.arg1);
                    break;
                case MSG_GATT_CHARACTERISTIC_WRITE:
                    mCBManager.handleCharacteristicWrite(msg.obj, msg.arg1);
                    break;
            }
        }
    }

    private final class CentralStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                logger.info("BondState =[" + device.getBondState() + "]");
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 10);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        logger.info("Bluetooth close !");
                        CentralService.this.mScanner.stop();
                        break;

                    case BluetoothAdapter.STATE_ON:
                        logger.info("Bluetooth open !");
                        CentralService.this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        mScanner.start(true);
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.info("......... CentralService onCreate .........");

        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            logger.error("Unregistered user !");
            return;
        }

        final BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null == bm) {
            Toasts.show(this, R.string.speed_force_alert_bluetooth_not_support);
            logger.error("Bluetooth not support!");
            stopSelf();
        }

        HandlerThread thread = new HandlerThread("CentralService]");
        thread.start();
        this.mCentralServiceLooper = thread.getLooper();
        this.mCentralServiceHandler = new CentralServiceHandler(mCentralServiceLooper);


        this.mBluetoothAdapter = bm.getAdapter();
        this.mUserSp = getSharedPreferences(user.getObjectId(), 0);
        this.mCBManager = new CentralInvocation();
        this.mCBManager.init(this, mCentralServiceHandler, mUserSp);
        this.mScanner = new CentralScanner(mBluetoothAdapter, this, this);
        this.mConnector = new CentralConnector(this, this);
        this.registerStateReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }

        String cmd = intent.getStringExtra(EXTRA_CMD);
        String centralId = intent.getStringExtra(EXTRA_CENTRAL_ID);

        if (TextUtils.isEmpty(cmd)) {
            return super.onStartCommand(intent, flags, startId);
        }

        logger.info("###### onStartCommand cmd=" + cmd + ",startId" + startId + ", " +
                "centralId=" + centralId + " ######");

        CentralSession session = CentralSessionHandler.getInstance().sessionGenerate(centralId);

        if (TextUtils.equals(cmd, CMD_CENTRAL_START_SCAN)) {
            mScanner.start();
        } else if (TextUtils.equals(cmd, CMD_CENTRAL_SCAN_AND_CONNECT)) {
            CentralSession connectSession = CentralSessionHandler.getInstance().getConnectSession();
            if (connectSession != null) {
                mConnector.disConnect(connectSession);
            }
            if (session != null) {
                session.setUnBound(false);
                session.setAutoAttach(true);
            }
            mScanner.start();

        } else if (TextUtils.equals(cmd, CMD_CENTRAL_CONNECT)) {
            mConnector.connect(session);
        } else if (TextUtils.equals(cmd, CMD_CENTRAL_DISCONNECT)) {
            mConnector.disConnect(session);
        } else if (TextUtils.equals(cmd, CMD_CENTRAL_UNBOUND)) {
            mConnector.unBound(session);
        } else if (TextUtils.equals(cmd, CMD_CENTRAL_STOP_SCAN)) {
            mScanner.stop();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ICentralBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.info("......... CentralService onDestroy .........");

        if (mStateReceiver != null) {
            unregisterReceiver(mStateReceiver);
            mStateReceiver = null;
        }
        if (mScanner != null) {
            mScanner.stop();
        }
        if (this.mCentralServiceLooper != null) {
            this.mCentralServiceLooper.quit();
        }
        if (mCBManager != null) {
            mCBManager.destroy();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) {
            return;
        }

        logger.trace("Notification = [" + sbn.toString() + "]");

        CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
        if (session == null)
            return;

        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }

        BluetoothDevice device = session.getBluetoothDevice();
        String settingPackageName = sbn.getPackageName();

        if (settingPackageName.equals(PACKAGE_NAME_SETTING) && null != device) {

            Bundle bundle = notification.extras;
            if (null != bundle) {
                String text = bundle.getString(Notification.EXTRA_TEXT);
                String deviceName = device.getName();
                if (!TextUtils.isEmpty(text) && text.contains(deviceName)) {
                    PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        pendingIntent.send();
                        logger.info("确认是配对请求的通知，主动弹出成功");
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                        logger.info("确认是配对请求的通知，主动弹出失败");
                    }
                }
            }

        }

        if (null == this.mUserSp) {
            return;
        }

        int on = this.mUserSp.getInt(BLE.PREF_BLE_MESSAGE_ON_KEY, 1);
        if (on != 1) {// 1为开启消息通知
            return;
        }

        String packageName = sbn.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            return;
        }

        Bundle bundle = notification.extras;
        if (null == bundle) {
            return;
        }

        byte type = 0x01;
        if (packageName.equals(PACKAGE_NAME_ANDROID_INCALL)) {
            type = 0x00;
        }

        String title = bundle.getString(Notification.EXTRA_TITLE);
        if (TextUtils.isEmpty(title) && type == 0x00) {
            title = getString(R.string.label_call_in_msg);
        }

        if (TextUtils.isEmpty(title)) {
            return;
        }

        String text = bundle.getString(Notification.EXTRA_TEXT);
        this.mCBManager.writeANCSNotification(title, text, type);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if (TextUtils.isEmpty(packageName) ||
                !packageName.equals(PACKAGE_NAME_ANDROID_INCALL)) {
            return;
        }

        Notification notification = sbn.getNotification();
        if (null == notification) {
            return;
        }

        Bundle bundle = notification.extras;
        String title = bundle.getString(Notification.EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            return;
        }

        String text = bundle.getString(Notification.EXTRA_TEXT);
        byte type = 0x02;// 挂电话
        this.mCBManager.writeANCSNotification(title, text, type);
    }


    @Override
    public void onScanResult(final CentralSession scanResult) {
        logger.info("onScanResult isAddNew=[" + CentralSessionHandler.getInstance()
                .isAddNew() + "]," + " session =[" + scanResult + "]");
        if (scanResult == null) {
            return;
        }

        final List<CentralSession> scanResults = CentralSessionHandler.getInstance()
                .updateScanResult(scanResult);

        if (mICentralServiceListener != null) {
            mICentralServiceListener.onScanResult(scanResults, scanResult);
        }

        if (CentralSessionHandler.getInstance().isAddNew()) {
            return;
        }

        if (scanResult.isAvailable() && scanResult.isAutoAttach()) {
            mConnector.connect(scanResult);
        }
    }

    @Override
    public void onScanStop(int errorCode) {
        if (mICentralServiceListener != null) {
            mICentralServiceListener.onScanResult(
                    CentralSessionHandler.getInstance().getScanResult(), null);
        }
    }

    @Override
    public void onConnectionDisconnect() {
        Message msg = mCentralServiceHandler.obtainMessage(MSG_GATT_CONNECTION_DISCONNECT);
        mCentralServiceHandler.sendMessage(msg);
    }

    @Override
    public void onServicesDiscovered(CentralSession session) {
        Message msg = mCentralServiceHandler.obtainMessage(MSG_GATT_SERVICES_DISCOVERED,
                0, 0, session);
        mCentralServiceHandler.sendMessage(msg);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic, int status) {

        logger.info("onCharacteristicWrite: " + characteristic.getUuid().toString() + ", "
                + Arrays.toString(characteristic.getValue()) + ", " + status + " Thread [" +
                Thread.currentThread().getName() + "]");

        Message msg = mCentralServiceHandler.obtainMessage(MSG_GATT_CHARACTERISTIC_WRITE,
                status, 0, characteristic);
        mCentralServiceHandler.sendMessage(msg);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic, int status) {

        logger.info("onCharacteristicRead: " + characteristic.getUuid().toString() + ", "
                + Arrays.toString(characteristic.getValue()) + ", " + status + " Thread [" +
                Thread.currentThread().getName() + "]");

        mCBManager.handleCharacteristicChanged(characteristic);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (null != characteristic) {
            String uuid = characteristic.getUuid().toString();
            if (!uuid.equals(BLE.UUID_ACTIVITY_SAMPLE_NOTIFI.toString()) &&
                    !uuid.equals(BLE.UUID_SENSOR_NOTIFI.toString())) {
                logger.info("onCharacteristicChanged: " + characteristic.getUuid().toString() + ", "
                        + Arrays.toString(characteristic.getValue()) + ", " +
                        "Thread [" + Thread.currentThread().getName() + "]");
            }
        }

        mCBManager.handleCharacteristicChanged(characteristic);
    }

    /**
     * set listener for {@link CentralService}
     *
     * @param iCyclingBinderListener
     */
    public void setOnScanResultListener(IScanResultListener iCyclingBinderListener) {
        this.mICentralServiceListener = iCyclingBinderListener;
    }

    public Invocation getInvocation() {
        return mCBManager;
    }

    private void registerStateReceiver() {
        if (mStateReceiver == null) {
            mStateReceiver = new CentralStateReceiver();
        }

        if (mStateReceiver.isOrderedBroadcast()) {
            return;
        }

        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mStateReceiver, intent);
    }

}
