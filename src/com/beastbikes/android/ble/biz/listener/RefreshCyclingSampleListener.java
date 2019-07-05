package com.beastbikes.android.ble.biz.listener;

import com.beastbikes.android.ble.protocol.v1.SampleCharacteristic;

public interface RefreshCyclingSampleListener {
        void onSampleResponse(SampleCharacteristic characteristic);
    }
