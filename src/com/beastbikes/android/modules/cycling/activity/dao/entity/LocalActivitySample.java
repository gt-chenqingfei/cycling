package com.beastbikes.android.modules.cycling.activity.dao.entity;

import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = BeastStore.Activities.Samples.CONTENT_CATEGORY)
public class LocalActivitySample implements PersistentObject {

    private static final long serialVersionUID = -8566336474374023212L;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.ACTIVITY_ID, canBeNull = false)
    private String activityId;

    @DatabaseField(columnName = "ordinal")
    private int ordinal;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.USER_ID, canBeNull = false)
    private String userId;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.LONGITUDE_0)
    private String longitude0;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.LATITUDE_0)
    private String latitude0;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.LONGITUDE_1)
    private String longitude1;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.LATITUDE_1)
    private String latitude1;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.ALTITUDE)
    private String altitude;

    /**
     * Unit : seconds
     */
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.TIME, canBeNull = false)
    private double time;

    /**
     * Unit : milliseconds
     */
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.ELAPSED_TIME, canBeNull = false)
    private long elapsedTime;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.DISTANCE)
    private double distance;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.VELOCITY)
    private double velocity;

    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.CALORIE)
    private double calorie;

    // 心率
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.CARDIAC_RATE)
    private double cardiacRate;

    @DatabaseField(columnName = "synced")
    private boolean synced;

    /**
     * Unit : milliseconds
     */
    @DatabaseField(columnName = "sync_time")
    private long syncTime;

    @DatabaseField(columnName = "remote_id")
    private String remoteId;

    // v2.0.0 添加的新的字段，表示当前定位的时间戳（不包含毫秒）
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.CURR_TIME)
    private long currTime;

    // ble 添加的新的字段
    // 设备ID
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.MAX_SPEED)
    private double maxSpeed;
    // 最大心率
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.MAX_CARDIAC_RATE)
    private double maxCardiacRate;
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.CADENCE)
    private double cadence;
    @DatabaseField(columnName = BeastStore.Activities.Samples.SampleColumns.MAX_CADENCE)
    private double maxCadence;

    public LocalActivitySample() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLongitude0() {
        return longitude0;
    }

    public void setLongitude0(String longitude0) {
        this.longitude0 = longitude0;
    }

    public String getLatitude0() {
        return latitude0;
    }

    public void setLatitude0(String latitude0) {
        this.latitude0 = latitude0;
    }

    public String getLongitude1() {
        return longitude1;
    }

    public void setLongitude1(String longitude1) {
        this.longitude1 = longitude1;
    }

    public String getLatitude1() {
        return latitude1;
    }

    public void setLatitude1(String latitude1) {
        this.latitude1 = latitude1;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getCardiacRate() {
        return cardiacRate;
    }

    public void setCardiacRate(double cardiacRate) {
        this.cardiacRate = cardiacRate;
    }

    public boolean isSynced() {
        return this.synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public long getSyncTime() {
        return this.syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public String getRemoteId() {
        return this.remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMaxCardiacRate() {
        return maxCardiacRate;
    }

    public void setMaxCardiacRate(double maxCardiacRate) {
        this.maxCardiacRate = maxCardiacRate;
    }

    public double getCadence() {
        return cadence;
    }

    public void setCadence(double cadence) {
        this.cadence = cadence;
    }

    public double getMaxCadence() {
        return maxCadence;
    }

    public void setMaxCadence(double maxCadence) {
        this.maxCadence = maxCadence;
    }

    @Override
    public String toString() {
        return "LocalActivitySample{" +
                "id='" + id + '\'' +
                ", activityId='" + activityId + '\'' +
                ", ordinal=" + ordinal +
                ", userId='" + userId + '\'' +
                ", longitude0='" + longitude0 + '\'' +
                ", latitude0='" + latitude0 + '\'' +
                ", longitude1='" + longitude1 + '\'' +
                ", latitude1='" + latitude1 + '\'' +
                ", altitude='" + altitude + '\'' +
                ", time=" + time +
                ", elapsedTime=" + elapsedTime +
                ", distance=" + distance +
                ", velocity=" + velocity +
                ", calorie=" + calorie +
                ", cardiacRate=" + cardiacRate +
                ", synced=" + synced +
                ", syncTime=" + syncTime +
                ", remoteId='" + remoteId + '\'' +
                ", currTime=" + currTime +
                ", maxSpeed=" + maxSpeed +
                ", maxCardiacRate=" + maxCardiacRate +
                ", cadence=" + cadence +
                ", maxCadence=" + maxCadence +
                '}';
    }
}
