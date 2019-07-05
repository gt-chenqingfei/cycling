package com.beastbikes.android.ble.protocol.v1;

import java.io.Serializable;

/**
 * Created by icedan on 16/4/12.
 */
public class OTAFirmwareInfoCharacteristic extends SyncDataCharacteristic implements Serializable {

    private int overallFirmwareVersion;
    private String overallVersion;
    // mcu
    private int mcuFirmwareVersion;
    private String mcuVersion;
    private int mcuCheckSum;

    // ble
    private int bleFirmwareVersion;
    private String bleVersion;
    private int bleCheckSum;

    // ui
    private int uiFirmwareVersion;
    private String uiVersion;
    private int uiCheckSum;

    // font
    private int fontFirmwareVersion;
    private String fontVersion;
    private int fontCheckSum;

    // power
    private int powerFirmwareVersion;
    private String powerVersion;
    private int powerCheckSum;

    public int getOverallFirmwareVersion() {
        return overallFirmwareVersion;
    }

    public void setOverallFirmwareVersion(int overallFirmwareVersion) {
        this.overallFirmwareVersion = overallFirmwareVersion;
        int bigV = overallFirmwareVersion >>> 16;
        int centerV = overallFirmwareVersion >>> 8 & 0xFF;
        int smallV = overallFirmwareVersion & 0xFF;
        this.overallVersion = bigV + "." + centerV + "." + smallV;
    }

    public int getMcuFirmwareVersion() {
        return mcuFirmwareVersion;
    }

    public void setMcuFirmwareVersion(int mcuFirmwareVersion) {
        this.mcuFirmwareVersion = mcuFirmwareVersion;
        int bigV = mcuFirmwareVersion >>> 16;
        int centerV = mcuFirmwareVersion >>> 8 & 0xFF;
        int smallV = mcuFirmwareVersion & 0xFF;
        this.mcuVersion = bigV + "." + centerV + "." + smallV;
    }

    public int getMcuCheckSum() {
        return mcuCheckSum;
    }

    public void setMcuCheckSum(int mcuCheckSum) {
        this.mcuCheckSum = mcuCheckSum;
    }

    public int getBleFirmwareVersion() {
        return bleFirmwareVersion;
    }

    public void setBleFirmwareVersion(int bleFirmwareVersion) {
        this.bleFirmwareVersion = bleFirmwareVersion;
        int bigV = bleFirmwareVersion >>> 16;
        int centerV = bleFirmwareVersion >>> 8 & 0xFF;
        int smallV = bleFirmwareVersion & 0xFF;
        this.bleVersion = bigV + "." + centerV + "." + smallV;
    }

    public int getBleCheckSum() {
        return bleCheckSum;
    }

    public void setBleCheckSum(int bleCheckSum) {
        this.bleCheckSum = bleCheckSum;
    }

    public int getUiFirmwareVersion() {
        return uiFirmwareVersion;
    }

    public void setUiFirmwareVersion(int uiFirmwareVersion) {
        this.uiFirmwareVersion = uiFirmwareVersion;
        int bigV = uiFirmwareVersion >>> 16;
        int centerV = uiFirmwareVersion >>> 8 & 0xFF;
        int smallV = uiFirmwareVersion & 0xFF;
        this.uiVersion = bigV + "." + centerV + "." + smallV;
    }

    public int getUiCheckSum() {
        return uiCheckSum;
    }

    public void setUiCheckSum(int uiCheckSum) {
        this.uiCheckSum = uiCheckSum;
    }

    public int getFontFirmwareVersion() {
        return fontFirmwareVersion;
    }

    public void setFontFirmwareVersion(int fontFirmwareVersion) {
        this.fontFirmwareVersion = fontFirmwareVersion;
        int bigV = fontFirmwareVersion >>> 16;
        int centerV = fontFirmwareVersion >>> 8 & 0xFF;
        int smallV = fontFirmwareVersion & 0xFF;
        this.fontVersion = bigV + "." + centerV + "." + smallV;
    }

    public int getFontCheckSum() {
        return fontCheckSum;
    }

    public void setFontCheckSum(int fontCheckSum) {
        this.fontCheckSum = fontCheckSum;
    }

    public int getPowerFirmwareVersion() {
        return powerFirmwareVersion;
    }

    public void setPowerFirmwareVersion(int powerFirmwareVersion) {
        this.powerFirmwareVersion = powerFirmwareVersion;
        int bigV = powerFirmwareVersion >>> 16;
        int centerV = powerFirmwareVersion >>> 8 & 0xFF;
        int smallV = powerFirmwareVersion & 0xFF;
        this.powerVersion = bigV + "." + centerV + "." + smallV;
    }

    public int getPowerCheckSum() {
        return powerCheckSum;
    }

    public void setPowerCheckSum(int powerCheckSum) {
        this.powerCheckSum = powerCheckSum;
    }

    public String getOverallVersion() {
        return overallVersion;
    }

    public void setOverallVersion(String overallVersion) {
        this.overallVersion = overallVersion;
    }

    public String getMcuVersion() {
        return mcuVersion;
    }

    public void setMcuVersion(String mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public String getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
    }

    public String getUiVersion() {
        return uiVersion;
    }

    public void setUiVersion(String uiVersion) {
        this.uiVersion = uiVersion;
    }

    public String getFontVersion() {
        return fontVersion;
    }

    public void setFontVersion(String fontVersion) {
        this.fontVersion = fontVersion;
    }

    public String getPowerVersion() {
        return powerVersion;
    }

    public void setPowerVersion(String powerVersion) {
        this.powerVersion = powerVersion;
    }

    @Override
    public String toString() {
        return "OTAFirmwareInfoCharacteristic{" +
                "overallFirmwareVersion=" + overallFirmwareVersion +
                ", overallVersion='" + overallVersion + '\'' +
                ", mcuFirmwareVersion=" + mcuFirmwareVersion +
                ", mcuVersion='" + mcuVersion + '\'' +
                ", mcuCheckSum=" + mcuCheckSum +
                ", bleFirmwareVersion=" + bleFirmwareVersion +
                ", bleVersion='" + bleVersion + '\'' +
                ", bleCheckSum=" + bleCheckSum +
                ", uiFirmwareVersion=" + uiFirmwareVersion +
                ", uiVersion='" + uiVersion + '\'' +
                ", uiCheckSum=" + uiCheckSum +
                ", fontFirmwareVersion=" + fontFirmwareVersion +
                ", fontVersion='" + fontVersion + '\'' +
                ", fontCheckSum=" + fontCheckSum +
                ", powerFirmwareVersion=" + powerFirmwareVersion +
                ", powerVersion='" + powerVersion + '\'' +
                ", powerCheckSum=" + powerCheckSum +
                '}';
    }
}
