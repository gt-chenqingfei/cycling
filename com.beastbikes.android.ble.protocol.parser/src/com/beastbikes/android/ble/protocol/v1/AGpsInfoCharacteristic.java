package com.beastbikes.android.ble.protocol.v1;

/**
 * Created by icedan on 16/7/18.
 */
public class AGpsInfoCharacteristic extends CommandCharacteristic {

    private int updateTime;

    public AGpsInfoCharacteristic() {

    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AGpsInfoCharacteristic{" +
                "updateTime=" + updateTime +
                '}';
    }
}
