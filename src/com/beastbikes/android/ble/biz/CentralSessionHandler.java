package com.beastbikes.android.ble.biz;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;
import android.text.TextUtils;

import com.beastbikes.android.ble.biz.entity.CentralSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenqingfei on 16/9/14.
 */
public class CentralSessionHandler {

    private CentralSessionHandler() {
    }

    private static final Logger logger = LoggerFactory.getLogger("CentralSessionHandler");
    private static CentralSessionHandler mInstance = new CentralSessionHandler();
    private LinkedHashMap<String, CentralSession> mSessionMap = new LinkedHashMap<>();
    private List<CentralSession> mScanResult = new ArrayList<>();
    private boolean isAddNew = false;

    public static CentralSessionHandler getInstance() {
        return mInstance;
    }

    /**
     * Generate a new session when start scan
     * {@link  CentralScanner #parseScanResult()}
     *
     * @param device
     * @param hdType
     * @param available
     * @return
     */
    public CentralSession sessionGenerate(BluetoothDevice device, int hdType, boolean available) {
        CentralSession session = null;
        if (device == null)
            return session;

        String centralId = CentralSession.address2CentralId(device.getAddress());
        session = sessionMatch(centralId);

        if (session == null) {
            session = new CentralSession(device, CentralSession.SESSION_STATE_NONE, available,
                    hdType);
            mSessionMap.put(session.getCentralId(), session);
        } else {
            session.setState(CentralSession.SESSION_STATE_NONE);
            session.setBluetoothDevice(device);
            session.setAvailable(available);
            session.setHdType(hdType);
        }

        return session;
    }

    public CentralSession sessionGenerate(String centralId) {
        CentralSession session = null;

        if (TextUtils.isEmpty(centralId)) {
            logger.debug("centralId is empty !");
            return session;
        }

        session = sessionMatch(centralId);

        if (session == null) {
            session = new CentralSession(centralId);
            mSessionMap.put(session.getCentralId(), session);
        }
        return session;
    }

    public CentralSession sessionMatch(String centralId) {
        return mSessionMap.get(centralId);
    }

    public CentralSession sessionMatch(BluetoothDevice device) {
        if (device == null)
            return null;
        String centralId = CentralSession.address2CentralId(device.getAddress());
        return mSessionMap.get(centralId);
    }


    public CentralSession getConnectSession() {
        for (Map.Entry<String, CentralSession> entry : mSessionMap.entrySet()) {
            CentralSession session = entry.getValue();
            if (session.getState() == CentralSession.SESSION_STATE_DISCOVERED) {
                return session;
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public CentralSession sessionDiscovered(BluetoothGatt gatt) {
        if (gatt == null)
            return null;

        CentralSession s = sessionMatch(gatt.getDevice());
        if (s == null)
            return null;

        for (Map.Entry<String, CentralSession> entry : mSessionMap.entrySet()) {
            CentralSession session = entry.getValue();
            if (session.getCentralId() != s.getCentralId()) {
                session.setState(CentralSession.SESSION_STATE_NONE);
                session.setBluetoothGatt(null);
                session.setBluetoothDevice(null);
                session.setAvailable(false);
                session.setAutoAttach(false);
                session.setUnBound(false);
                session.setProperty(null);
            } else {
                session.setAutoAttach(true);
                session.setState(CentralSession.SESSION_STATE_DISCOVERED);
            }
        }

        return s;
    }

    public List<CentralSession> getScanResult() {
        return mScanResult;
    }

    /**
     * 更新扫描到是设备列表
     * <p/>
     * 如果列表中含有此设备  (通过isConnect状态以及deviceId判断是否相同)
     * 不做任何更新
     * 否则 会刷新UI
     * <p/>
     *
     * @param session
     * @return
     */
    public List<CentralSession> updateScanResult(CentralSession session) {
        if (this.mScanResult.contains(session)) {

            logger.info("updateScanResult scanResults contains[" + session.getCentralId() + "]");
            return null;
        } else {
            int size = this.mScanResult.size();
            for (int i = 0; i < size; i++) {
                CentralSession s = this.mScanResult.get(i);
                if (TextUtils.equals(session.getCentralId(), s.getCentralId())) {
                    this.mScanResult.remove(i);
                    this.mScanResult.add(i, session);


                    return mScanResult;
                }
            }
            this.mScanResult.add(session);
        }
        return mScanResult;
    }

    /**
     * 清空配对列表
     * <p>
     * 一般会在结束扫描的时候清空
     * </p>
     *
     * @param
     */
    public void cleanScanResult() {

        if (this.mScanResult != null) {
            this.mScanResult.clear();
        }
    }

    public boolean isConnected(String centralId) {
        CentralSession session = sessionMatch(centralId);
        if (session != null) {
            return session.getState() == CentralSession.SESSION_STATE_DISCOVERED;
        }
        return false;
    }

    public boolean isConnected(BluetoothDevice device) {
        if (device != null) {
            String centralId = CentralSession.address2CentralId(device.getAddress());
            return isConnected(centralId);
        }
        return false;
    }

    public boolean hasConnected() {
        CentralSession session = getConnectSession();
        return session != null;
    }


    public void resetAutoConnect() {
        for (Map.Entry<String, CentralSession> entry : mSessionMap.entrySet()) {
            CentralSession session = entry.getValue();
            session.setAutoAttach(false);
        }
    }

    public boolean isAddNew() {
        return isAddNew;
    }

    public void setAddNew(boolean addNew) {
        isAddNew = addNew;
    }
}
