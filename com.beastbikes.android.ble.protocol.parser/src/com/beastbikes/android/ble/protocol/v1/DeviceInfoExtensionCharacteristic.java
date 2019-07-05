package com.beastbikes.android.ble.protocol.v1;

import java.util.Arrays;

/**
 * Created by icedan on 16/7/19.
 */
public class DeviceInfoExtensionCharacteristic extends CommandCharacteristic {

    private byte[] macAddress;

    private int guaranteeTime;

    private String macAddr;

    // 单位：KM
    private int totalDistance;
    // 单位：minute
    private int totalTime;

    public DeviceInfoExtensionCharacteristic() {

    }

    public byte[] getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(byte[] macAddress) {
        this.macAddress = macAddress;
        this.macAddr = String.format("%02X%02X%02X%02X%02X%02X", macAddress[0], macAddress[1], macAddress[2], macAddress[3],
                macAddress[4], macAddress[5]);
    }

    public int getGuaranteeTime() {
        return guaranteeTime;
    }

    public void setGuaranteeTime(int guaranteeTime) {
        this.guaranteeTime = guaranteeTime;
    }

    public String getMacAddr() {
        return this.macAddr = String.format("%02X02X02X02X02X02X", macAddress[0], macAddress[1], macAddress[2], macAddress[3],
                macAddress[4], macAddress[5]);
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        return "DeviceInfoExtensionCharacteristic{" +
                "macAddress=" + Arrays.toString(macAddress) +
                ", guaranteeTime=" + guaranteeTime +
                ", macAddr='" + macAddr + '\'' +
                ", totalDistance=" + totalDistance +
                ", totalTime=" + totalTime +
                '}';
    }
}
