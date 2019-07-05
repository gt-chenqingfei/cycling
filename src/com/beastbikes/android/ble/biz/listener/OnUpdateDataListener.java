package com.beastbikes.android.ble.biz.listener;

public interface OnUpdateDataListener {
        void onUpdateCanceled(int type);

        void onUpdateSuccess(int type);
    }