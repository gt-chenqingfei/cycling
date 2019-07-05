package com.beastbikes.android.ble.biz.listener;

/**
 * Created by icedan on 16/10/26.
 */

public interface ConnectStateListener {

    void bleConnected();

    void bleDisconnected();
}
