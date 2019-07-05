package com.beastbikes.android.ble.biz.listener;

import com.beastbikes.android.ble.protocol.v1.AGpsInfoCharacteristic;

public interface OnAGPSListener {
        void onSyncAGPSStart();

        void onAPGSInfoResponse(AGpsInfoCharacteristic characteristic);

        void onSyncing();
    }