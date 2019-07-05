package com.beastbikes.android.ble;

import com.beastbikes.android.ble.biz.listener.OnAGPSListener;
import com.beastbikes.android.ble.biz.listener.OnOtaCheckSumListener;
import com.beastbikes.android.ble.biz.listener.OnPreviewCyclingListener;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshCyclingDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshCyclingSampleListener;
import com.beastbikes.android.ble.biz.listener.RefreshOTAPacketListener;
import com.beastbikes.android.ble.biz.listener.ResponseDeviceInfoListener;
import com.beastbikes.android.ble.biz.listener.ResponseMacAddressListener;

/**
 * Created by chenqingfei on 16/9/23.
 */
public interface Invocation {
    void writeANCSNotification(final String title, final String text, final byte type);

    void writeActivitySyncRequest(final long time, final int errorCode);

    void writeNavigationRequest(int type, int distance);

    void writeDeviceInfoRequest();

    boolean writeCheckSumRequest();

    void writeActivityPreviewRequest();

    boolean writeAGPSInfoRequest();

    void writeOTAStartRequest(final int type, final String versionName, final String path);

    boolean writeMessageConfig(boolean enable);

    boolean writeVibrationWakeConfig(boolean enable);

    boolean writeWheel(byte wheel);

    boolean writeLocaleConfig(int index);

    boolean writeMileageUnitConfig(int index);

    boolean writeCadenceConfig(int value);

    boolean writeTargetConfig(int targetType, int targetValue, int currentValue);

    boolean writeMaxHeartRateConfig(int heartRate);

    void setRefreshCyclingDataListener(RefreshCyclingDataListener refreshCyclingDataListener);

    void setRefreshOTAPacketListener(RefreshOTAPacketListener refreshOTAPacketListener);

    void setResponseDeviceInfoListener(ResponseDeviceInfoListener responseDeviceInfoListener);


    void setResponseMacAddressListener(ResponseMacAddressListener responseMacAddressListener);

    void setRefreshCyclingSampleListener(RefreshCyclingSampleListener listener);

    void setAGPSListener(OnAGPSListener listener);

    OnPreviewCyclingListener getPreviewCyclingListener();

    void setPreviewCyclingListener(OnPreviewCyclingListener previewCyclingListener);

    void setCheckSumListener(OnOtaCheckSumListener checkSumListener);

    OnUpdateDataListener getUpdateListener();

    void setUpdateListener(OnUpdateDataListener updateListener);

}
