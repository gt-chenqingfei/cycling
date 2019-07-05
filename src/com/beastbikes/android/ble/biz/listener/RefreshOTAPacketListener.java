package com.beastbikes.android.ble.biz.listener;

public interface RefreshOTAPacketListener {
        public void onRefreshCount(int index);

        public void onOTADataEnd(int type);

        public void onParserDataError();
    }