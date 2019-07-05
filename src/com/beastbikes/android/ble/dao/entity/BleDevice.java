package com.beastbikes.android.ble.dao.entity;

import android.os.Parcel;
import android.text.TextUtils;

import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by icedan on 15/12/3.
 */
@DatabaseTable(tableName = BeastStore.BleDevices.CONTENT_CATEGORY)
public class BleDevice implements PersistentObject {

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.USER_ID)
    private String userId;

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.DEVICE_ID)
    private String deviceId;

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.DEVICE_NAME)
    private String deviceName;

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.LAST_BIND_TIME)
    private long lastBindTime;

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.STATUS)
    private int status;

    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.DEVICE_URL)
    private String url;

    /**
     * 硬件类型
     * 0:Central Computer
     * 1:Whole Bike
     */
    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.HARDWARE_TYPE)
    private int hardwareType;
    /**
     * 车型
     * 0 : SpeedForce
     * 1 : Mustang
     * 2 : Leopard
     * 3 : Leopard Pro
     * 4 : Giant Customed
     */
    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.BRAND_TYPE)
    private int brandType;
    /**
     * 中控返回的mac_address
     */
    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.MAC_ADDRESS)
    private String macAddress;
    /**
     * 中控保修期
     */
    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.GUARANTEE_TIME)
    private int guaranteeTime;

    /**
     * 车间编号
     */
    @DatabaseField(columnName = BeastStore.BleDevices.BleDevicesColumns.FRAME_ID)
    private String frameId;

    public BleDevice() {

    }

    public BleDevice(String deviceName) {
        this.deviceName = deviceName;
    }

    public BleDevice(Parcel source) {
        this.id = source.readString();
        this.userId = source.readString();
        this.deviceId = source.readString();
        this.deviceName = source.readString();
        this.lastBindTime = source.readLong();
        this.status = source.readInt();
        this.url = source.readString();
        this.hardwareType = source.readInt();
        this.brandType = source.readInt();
        this.macAddress = source.readString();
        this.guaranteeTime = source.readInt();
        this.frameId = source.readString();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getLastBindTime() {
        return lastBindTime;
    }

    public void setLastBindTime(long lastBindTime) {
        this.lastBindTime = lastBindTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getGuaranteeTime() {
        return guaranteeTime;
    }

    public void setGuaranteeTime(int guaranteeTime) {
        this.guaranteeTime = guaranteeTime;
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public static String brandType2String(int brandType) {
        String brandTypeStr = "SpeedForce";
        switch (brandType) {
            case 0:
                brandTypeStr = "SpeedForce";
                break;
            case 1:
                brandTypeStr = "Mustang";
                break;
            case 2:
                brandTypeStr = "Leopard";
                break;
            case 3:
                brandTypeStr = "Leopard_Pro";
                break;
            case 4:
                brandTypeStr = "Giant_Customed";
                break;
            default:
                brandTypeStr = "SpeedForce";
                break;
        }
        return brandTypeStr;
    }

    public static String brandType2Name(int brandType) {
        String brandTypeStr = "SpeedForce";
        switch (brandType) {
            case 0:
                brandTypeStr = "SpeedForce";
                break;
            case 1:
                brandTypeStr = "Mustang";
                break;
            case 2:
                brandTypeStr = "Leopard";
                break;
            case 3:
                brandTypeStr = "Leopard Pro";
                break;
            case 4:
                brandTypeStr = "Giant Customed";
                break;
            default:
                brandTypeStr = "SpeedForce";
                break;
        }
        return brandTypeStr;
    }

    public static int brandType2Int(String brandType) {
        if (TextUtils.equals(brandType, "SpeedForce")) {
            return 0;
        } else if (TextUtils.equals(brandType, "Mustang")) {
            return 1;
        } else if (TextUtils.equals(brandType, "Leopard")) {
            return 2;
        } else if (TextUtils.equals(brandType, "Leopard Pro")) {
            return 3;
        } else if (TextUtils.equals(brandType, "Giant Customed")) {
            return 4;
        } else if (TextUtils.equals(brandType, "SpeedForce")) {
            return 0;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return super.equals(obj);
        return TextUtils.equals(macAddress, ((BleDevice) obj).getMacAddress());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "BleDevice{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", lastBindTime=" + lastBindTime +
                ", status=" + status +
                ", url='" + url + '\'' +
                ", hardwareType=" + hardwareType +
                ", brandType=" + brandType +
                ", macAddress='" + macAddress + '\'' +
                ", guaranteeTime=" + guaranteeTime +
                ", frameId='" + frameId + '\'' +
                '}';
    }
}
