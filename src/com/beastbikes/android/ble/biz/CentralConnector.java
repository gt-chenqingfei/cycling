package com.beastbikes.android.ble.biz;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.entity.InvocationParam;
import com.beastbikes.android.ble.ui.DiscoveryActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by chenqingfei on 16/9/12.
 */
@TargetApi(19)
public class CentralConnector extends BluetoothGattCallback {

    private static final Logger logger = LoggerFactory.getLogger("CentralConnector");

    private final Object mMutex = new Object();
    private Context mContext;
    private ConnectorHandlerListener mHandlerListener;

    public interface ConnectorHandlerListener {

        void onConnectionDisconnect();

        void onServicesDiscovered(final CentralSession session);

        void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status);

        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status);

        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic);

    }

    public CentralConnector(ConnectorHandlerListener handlerListener, Context context) {
        this.mContext = context;
        this.mHandlerListener = handlerListener;
    }

    public synchronized void connect(CentralSession session) {
        if (session == null) {
            logger.warn("Connection error, because the session to be connected is empty!");
            return;
        }

        BluetoothDevice device = session.getBluetoothDevice();
        synchronized (mMutex) {
            if (device == null)
                return;

            if (session.getState() == CentralSession.SESSION_STATE_NONE) {
                session.setState(CentralSession.SESSION_STATE_CONNECTING);
                BluetoothGatt mBluetoothGatt = device.connectGatt(mContext, false, this);
                session.setBluetoothGatt(mBluetoothGatt);
                session.setProperty(new InvocationParam());
                this.stopScan();
                logger.info("########  To Connect session =[" + session + "] ########");
            }
        }
    }

    private void stopScan() {
        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_STOP_SCAN);
        service.setPackage(mContext.getPackageName());
        mContext.startService(service);
    }

    private void startScan() {
        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_START_SCAN);
        service.setPackage(mContext.getPackageName());
        mContext.startService(service);
    }

    public void disConnect(CentralSession session) {
        if (session != null) {
            session.setUnBound(true);
            session.setState(CentralSession.SESSION_STATE_NONE);
            session.setAutoAttach(false);
            session.setAvailable(false);
            BluetoothGatt gatt = session.getBluetoothGatt();
            if (gatt != null) {
                gatt.disconnect();
                gatt.close();
            }
        } else {
            logger.warn("Connect error , because session is empty!");
        }
    }

    public void unBound(CentralSession session) {
        if (session != null) {
            BluetoothDevice device = session.getBluetoothDevice();
            if (device != null) {

                try {
                    Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasts.show(mContext, R.string.speed_force_activity_unbind_fail);
                }
                session.setUnBound(true);
                session.setAvailable(false);
                session.setAutoAttach(false);
            }
        } else {
            logger.warn("Connect error , because session is empty!");
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (gatt == null)
            return;

        logger.info("onConnectionStateChange status =[" + status + "], new State=[" + newState + "]");
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:// 正常连接成功及断开的状态
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        CentralSession session = CentralSessionHandler.getInstance()
                                .sessionMatch(gatt.getDevice());
                        if (session != null) {
                            if (session.getState() == CentralSession.SESSION_STATE_CONNECTED) {
                                logger.info("Current device is connected!!");
                                return;
                            }
                            session.setState(CentralSession.SESSION_STATE_CONNECTED);
                        }

                        final BluetoothDevice device = gatt.getDevice();
                        boolean ret = gatt.discoverServices();
                        logger.info("DiscoverServices ret =[" + ret + "] ,deviceName =[" +
                                device.getName() + "] ,address= [" + device.getAddress() + "]");
                        if (!ret) {
                            startScan();
                        }
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        handleDisconnected(gatt);
                        break;
                }
                break;
            default:// 异常连接断开的状态
                switch (newState) {
                    case BluetoothProfile.STATE_DISCONNECTED:
                        logger.error("异常连接断开， status ＝ " + status + ", state = " + newState);
                        handleDisconnected(gatt);
                        break;
                }
                break;
        }
    }

    /**
     * 设备连接已断开
     *
     * @param gatt BluetoothGatt
     */
    private synchronized void handleDisconnected(BluetoothGatt gatt) {
        if (gatt == null)
            return;
        CentralSession session = CentralSessionHandler.getInstance().sessionMatch(gatt.getDevice());
        if (session == null) {
            return;
        }
        if (session.getState() == CentralSession.SESSION_STATE_NONE) {
            logger.info("当前设备已处于连接断开状态");
            return;
        }

        session.setState(CentralSession.SESSION_STATE_NONE);
        InvocationParam status = session.getProperty();
        status.setHasAuthkey(false);
        status.setWriteCD26(false);
        status.setWriteCD24(false);
        status.setWriteCD20(false);
        status.setWriteCD22(false);
        logger.info("handleDisconnected status=[" + status + "]");

        Intent intent = new Intent(DiscoveryActivity.BLE_DISCONNECTED_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        this.mContext.sendBroadcast(intent);

        if (this.mHandlerListener != null) {
            this.mHandlerListener.onConnectionDisconnect();
        }

        if (!session.isUnBound()) {
            startScan();
        }

        BluetoothDevice device = gatt.getDevice();
        logger.error(device.getName() + "@" + device.getAddress() + " disconnected");
        gatt.close();

        if (Build.VERSION.RELEASE.equals(CentralScanner.RELEASE_MIN_VERSION)) {
            logger.error("版本号等于 5.1的做解绑处理");
            this.unBound(session);
        }

    }

    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
        if (gatt == null)
            return;

        logger.info("onServicesDiscovered gatt=[" + gatt + "],status=[" + status + "]");

        if (BluetoothGatt.GATT_SUCCESS == status) {
            CentralSession session = CentralSessionHandler.getInstance().sessionDiscovered(gatt);
            if (session != null) {
                if (this.mHandlerListener != null) {
                    this.mHandlerListener.onServicesDiscovered(session);
                }
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic, int status) {
        if (this.mHandlerListener != null) {
            this.mHandlerListener.onCharacteristicWrite(gatt, characteristic, status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic, int status) {
        if (this.mHandlerListener != null) {
            this.mHandlerListener.onCharacteristicRead(gatt, characteristic, status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic) {

        if (this.mHandlerListener != null) {
            this.mHandlerListener.onCharacteristicChanged(gatt, characteristic);
        }

    }
}