package com.beastbikes.android.ble.biz.listener;

public interface RefreshCyclingDataListener {
    public void onSyncStart();

    public void onSyncing(int index, int count);

    public void onSyncEnd();

    public void onSyncError(int errorCode);
}