package com.beastbikes.android.ble.protocol.v1;

import java.io.Serializable;

public class DeviceInfoCommandCharacteristic extends CommandCharacteristic implements Serializable {

    private long systime;

    private int firmwareVersion;

    private int frequency;

    private int locale;

    private int backlight;

    private int battery;

    private int autolight;

    private int mute;

    private int gpsService;

    private int mileageUnit;

    /**
     * 硬件类型
     * 0x00 HD_TYPE_B08
     * 0x01 HD_TYPE_S601
     * 0x02 HD_TYPE_B09
     * 0x03 HD_TYPE_S603
     * 0x04 HD_TYPE_S605
     */
    private int hardwareType;
    /**
     * 车型
     * 0 : Speedforce
     * 1 : Mustang
     * 2 : Lepoard
     * 3 : Lepoard Pro
     * 4 : Giant Customed
     */
    private int brandType;

    private int wheelType;

    /**
     * 消息通知
     * 0:OFF,1:On
     */
    private int notification;

    private int favouriteCadence;

    private int shakeUp;

    public DeviceInfoCommandCharacteristic() {
    }

    public long getSystime() {
        return systime;
    }

    public void setSystime(long systime) {
        this.systime = systime;
    }

    public int getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(int firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLocale() {
        return locale;
    }

    public void setLocale(int locale) {
        this.locale = locale;
    }

    public int getBacklight() {
        return backlight;
    }

    public void setBacklight(int backlight) {
        this.backlight = backlight;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getAutolight() {
        return autolight;
    }

    public void setAutolight(int autolight) {
        this.autolight = autolight;
    }

    public int getMute() {
        return mute;
    }

    public void setMute(int mute) {
        this.mute = mute;
    }

    public int getGpsService() {
        return gpsService;
    }

    public void setGpsService(int gpsService) {
        this.gpsService = gpsService;
    }

    public int getMileageUnit() {
        return mileageUnit;
    }

    public void setMileageUnit(int mileageUnit) {
        this.mileageUnit = mileageUnit;
    }

    public int getHardwareType() {
        return hardwareType;
    }

    public void setHardwareType(int hardwareType) {
        this.hardwareType = hardwareType;
    }

    public int getBrandType() {
        return brandType;
    }

    public void setBrandType(int brandType) {
        this.brandType = brandType;
    }

    public int getWheelType() {
        return wheelType;
    }

    public void setWheelType(int wheelType) {
        this.wheelType = wheelType;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getShakeUp() {
        return shakeUp;
    }

    public void setShakeUp(int shakeUp) {
        this.shakeUp = shakeUp;
    }

    public int getFavouriteCadence() {
        return favouriteCadence;
    }

    public void setFavouriteCadence(int favouriteCadence) {
        this.favouriteCadence = favouriteCadence;
    }

    @Override
    public String toString() {
        return "DeviceInfoCommandCharacteristic{" +
                "systime=" + systime +
                ", firmwareVersion=" + firmwareVersion +
                ", frequency=" + frequency +
                ", locale=" + locale +
                ", backlight=" + backlight +
                ", battery=" + battery +
                ", autolight=" + autolight +
                ", mute=" + mute +
                ", gpsService=" + gpsService +
                ", mileageUnit=" + mileageUnit +
                ", hardwareType=" + hardwareType +
                ", brandType=" + brandType +
                ", wheelType=" + wheelType +
                ", notification=" + notification +
                ", favouriteCadence=" + favouriteCadence +
                ", shakeUp=" + shakeUp +
                '}';
    }
}
