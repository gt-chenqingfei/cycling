package com.beastbikes.android.ble.biz;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.dao.entity.BleDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Created by chenqingfei on 16/9/12.
 */
public class CentralScanner {


    /**
     * Callback interface used to deliver LE scan results.
     *
     * @see #start()
     * @see #stop()
     */
    public interface ScanCallBack {
        void onScanResult(CentralSession session);

        void onScanStop(int errorCode);
    }

    /**
     * @see #parseScanResult(byte[], BluetoothDevice)
     */
    private static class ParsedAd {
        byte flags;
        String localName;
        Short manufacturer;
    }

    public static final String RELEASE_MIN_VERSION = "5.1";
    private static final Logger logger = LoggerFactory.getLogger("CentralScanner");
    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mScanCallback = null;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private boolean mScanning = false;
    private final Object mutex = new Object();
    private ScanCallBack mCallBack;
    private Context mContext;
    private BleManager bleManager;

    public CentralScanner(BluetoothAdapter bluetoothAdapter, ScanCallBack callBack, Context context) {
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mCallBack = callBack;
        this.mContext = context;
    }

    public void start(boolean isForce) {
        if (isForce) {
            mScanning = false;
        }
        start();
    }

    public void start() {
        logger.info("######## Start scanning =[" + this.mScanning + "]," +
                "enabled=[" + this.mBluetoothAdapter.isEnabled() + "] ########");

        if (this.mScanning || !this.mBluetoothAdapter.isEnabled())
            return;

        synchronized (mutex) {
            this.mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startScan();
            } else {
                startLeScan();
            }
        }
    }

    public synchronized void stop() {
        logger.info("######## Stop scanning =[" + this.mScanning + "] ########");
        if (!this.mScanning)
            return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.stopLeScan();
        } else {
            this.stopScan();
        }

        if (this.mCallBack != null) {
            this.mCallBack.onScanStop(0);
        }
        synchronized (mutex) {
            this.mScanning = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopLeScan() {
        if (this.mBluetoothAdapter != null && this.mLeScanCallback != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
            this.mLeScanCallback = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopScan() {
        if (this.mBluetoothAdapter != null && this.mBluetoothAdapter.getBluetoothLeScanner() != null
                && this.mScanCallback != null) {
            this.mBluetoothAdapter.getBluetoothLeScanner().stopScan(this.mScanCallback);
            this.mScanCallback = null;
        }
    }

    /**
     * Start Bluetooth LE scan for version code >= 21
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScan() {
        final BluetoothLeScanner leScanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        if (null != leScanner) {
            this.mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (!CentralScanner.this.mScanning) {
                        leScanner.stopScan(this);
                        CentralScanner.this.mScanning = false;
                    }
                    ScanRecord scanRecord = result.getScanRecord();
                    if (null == scanRecord) {
                        return;
                    }

                    handleScanResult(result.getDevice(), scanRecord.getBytes());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    logger.error("Bluetooth scan failed, errorCode is " + errorCode);
                    if (errorCode == ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
                        // TODO: 16/9/12 reconnect
                        leScanner.stopScan(this);
                    }
                    if (CentralScanner.this.mCallBack != null) {
                        CentralScanner.this.mCallBack.onScanStop(errorCode);
                    }
                }
            };
            leScanner.startScan(this.mScanCallback);
            logger.info("startLeScan");
        }
    }

    /**
     * Start Bluetooth LE scan for version code < 21
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startLeScan() {

        this.mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!CentralScanner.this.mScanning) {
                    CentralScanner.this.mBluetoothAdapter.stopLeScan(this);
                    return;
                }
                handleScanResult(device, scanRecord);
            }
        };

        boolean scan = this.mBluetoothAdapter.startLeScan(mLeScanCallback);
        if (!scan) {
            if (this.mCallBack != null) {
                this.mCallBack.onScanStop(-1);
            }
            this.mBluetoothAdapter.stopLeScan(mLeScanCallback);
            this.mScanning = false;
            this.start();
        }
        logger.info("Start scan ＝[ " + scan + "]");
    }

    private void handleScanResult(final BluetoothDevice device, byte[] scanRecord) {
        CentralSession session = parseScanResult(scanRecord, device);

        if (CentralScanner.this.mCallBack != null && session != null) {
            CentralScanner.this.mCallBack.onScanResult(session);
        }
    }

    /**
     * Parse Bluetooth scan data
     *
     * @param adv_data
     * @param device
     * @return
     */
    private CentralSession parseScanResult(byte[] adv_data, BluetoothDevice device) {
        if (device == null)
            return null;
        CentralSession session = null;
        ParsedAd parsedAd = new ParsedAd();
        ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(
                ByteOrder.LITTLE_ENDIAN);

        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01: // Flags
                    parsedAd.flags = buffer.get();
                    length--;
                    break;
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    parsedAd.localName = new String(sb).trim();
                    break;
                case 0x16:
                    if (length < 9) {
                        return null;
                    }


                    short speedUUid = buffer.getShort();
                    byte address[] = new byte[6];
                    buffer.get(address, 0, 6);

                    byte available = buffer.get();
                    int uuid = speedUUid & 0xFFFF;
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        // 如果是已绑定的设备，则设置为可连
                        available = 0x01;
                    }

                    if (Build.VERSION.RELEASE.equals(RELEASE_MIN_VERSION)) {
                        // FIXME: 16/9/12  为什么在本地能找到就要设置为可连接
                        if (null == bleManager) {
                            bleManager = new BleManager(mContext);
                        }
                        BleDevice bleDevice = bleManager.getBleDevice(device.getAddress());
                        if (null != bleDevice) {
                            available = 0x01;
                        }
                    }
                    logger.trace("isConnect = " + available + ", UUID = " + Integer.toHexString(uuid)
                            + ", DeviceName = " + device.getName());

                    int hdType = CentralSession.HD_TYPE_B08;
                    switch (uuid) {
                        case 0xcd10:
                            hdType = CentralSession.HD_TYPE_B08;
                            break;
                        case 0xcd12:
                            hdType = CentralSession.HD_TYPE_B09;
                            break;
                        case 0xcd11:
                            hdType = CentralSession.HD_TYPE_S601;
                            break;
                        case 0xcd13:
                            hdType = CentralSession.HD_TYPE_S603;
                            break;
                        case 0xcd14:
                            hdType = CentralSession.HD_TYPE_S605;
                            break;

                    }

                    session = CentralSessionHandler.getInstance().sessionGenerate(device, hdType,
                            available == 0x01);
                    return session;
                case (byte) 0xFF: // Manufacturer Specific Data
                    parsedAd.manufacturer = buffer.getShort();
                    length -= 2;
                    break;
                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }

        return null;
    }
}
