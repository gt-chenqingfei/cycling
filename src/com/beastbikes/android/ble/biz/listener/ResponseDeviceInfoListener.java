package com.beastbikes.android.ble.biz.listener;

import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.ble.protocol.v1.BatterySensorCharacteristic;
import com.beastbikes.android.ble.protocol.v1.DeviceInfoCommandCharacteristic;

public interface ResponseDeviceInfoListener {
    void onResponseBatterySensor(BatterySensorCharacteristic characteristic);

    void onResponseDevice(BleDevice bleDevice,DeviceInfoCommandCharacteristic deviceInfo);
}