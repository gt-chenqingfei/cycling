package com.beastbikes.android.ble.biz.entity;

import com.beastbikes.android.ble.protocol.v1.DeviceInfoCommandCharacteristic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 16/9/22.
 */
public class InvocationParam {
    private float totalDistance;
    private int guaranteeTime;

    /**
     * 是否取消升级
     */
    private boolean cancelUpdate = false;
    /**
     * 是否取消同步
     */
    private boolean cancelSync = false;

    private boolean hasAuthkey;
    private double fileLength;

    // 实时数据Notification
    private boolean isWriteCD20;
    // Sensor Notification
    private boolean isWriteCD22;
    // Command Notification
    private boolean isWriteCD24;
    // Activity Notification
    private boolean isWriteCD26;
    private boolean hasPowerImg;

    // 是否更新GPS文件
    private boolean updateGps;
    private int otaUpdateByte;
    private int imgLength;
    private int previewIndex = 0;
    private List<Byte> activityData = new ArrayList<>();
    private DeviceInfoCommandCharacteristic deviceInfo;

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getGuaranteeTime() {
        return guaranteeTime;
    }

    public void setGuaranteeTime(int guaranteeTime) {
        this.guaranteeTime = guaranteeTime;
    }

    public boolean isCancelUpdate() {
        return cancelUpdate;
    }

    public void setCancelUpdate(boolean cancelUpdate) {
        this.cancelUpdate = cancelUpdate;
    }

    public boolean isHasAuthkey() {
        return hasAuthkey;
    }

    public void setHasAuthkey(boolean hasAuthkey) {
        this.hasAuthkey = hasAuthkey;
    }

    public double getFileLength() {
        return fileLength;
    }

    public void setFileLength(double fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isWriteCD20() {
        return isWriteCD20;
    }

    public void setWriteCD20(boolean writeCD20) {
        isWriteCD20 = writeCD20;
    }

    public boolean isWriteCD22() {
        return isWriteCD22;
    }

    public void setWriteCD22(boolean writeCD22) {
        isWriteCD22 = writeCD22;
    }

    public boolean isWriteCD24() {
        return isWriteCD24;
    }

    public void setWriteCD24(boolean writeCD24) {
        isWriteCD24 = writeCD24;
    }

    public boolean isWriteCD26() {
        return isWriteCD26;
    }

    public void setWriteCD26(boolean writeCD26) {
        isWriteCD26 = writeCD26;
    }

    public boolean isHasPowerImg() {
        return hasPowerImg;
    }

    public void setHasPowerImg(boolean hasPowerImg) {
        this.hasPowerImg = hasPowerImg;
    }

    public boolean isUpdateGps() {
        return updateGps;
    }

    public void setUpdateGps(boolean updateGps) {
        this.updateGps = updateGps;
    }

    public int getOtaUpdateByte() {
        return otaUpdateByte;
    }

    public void setOtaUpdateByte(int otaUpdateByte) {
        this.otaUpdateByte = otaUpdateByte;
    }

    public int getImgLength() {
        return imgLength;
    }

    public void setImgLength(int imgLength) {
        this.imgLength = imgLength;
    }

    public int getPreviewIndex() {
        return previewIndex;
    }

    public void setPreviewIndex(int previewIndex) {
        this.previewIndex = previewIndex;
    }

    public List<Byte> getActivityData() {
        return activityData;
    }

    public boolean isCancelSync() {
        return cancelSync;
    }

    public void setCancelSync(boolean cancelSync) {
        this.cancelSync = cancelSync;
    }

    public void cleanActivityData() {
        if (null != activityData) {
            this.activityData.clear();
        }
    }

    @Override
    public String toString() {
        return "InvocationParam{" +
                "totalDistance=" + totalDistance +
                ", guaranteeTime=" + guaranteeTime +
                ", cancelUpdate=" + cancelUpdate +
                ", hasAuthkey=" + hasAuthkey +
                ", fileLength=" + fileLength +
                ", isWriteCD20=" + isWriteCD20 +
                ", isWriteCD22=" + isWriteCD22 +
                ", isWriteCD24=" + isWriteCD24 +
                ", isWriteCD26=" + isWriteCD26 +
                ", hasPowerImg=" + hasPowerImg +
                ", updateGps=" + updateGps +
                ", otaUpdateByte=" + otaUpdateByte +
                ", imgLength=" + imgLength +
                ", previewIndex=" + previewIndex +
                ", activityData=" + activityData +
                '}';
    }

    public DeviceInfoCommandCharacteristic getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoCommandCharacteristic deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
