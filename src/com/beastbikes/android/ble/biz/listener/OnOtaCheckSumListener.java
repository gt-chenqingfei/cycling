package com.beastbikes.android.ble.biz.listener;

import com.beastbikes.android.ble.protocol.v1.OTAFirmwareInfoCharacteristic;

public interface OnOtaCheckSumListener {
        void onOtaCheckSumResponse(OTAFirmwareInfoCharacteristic characteristic);
    }