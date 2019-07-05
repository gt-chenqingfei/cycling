package com.beastbikes.android.modules.cycling.activity.dao.entity;

import android.os.Parcel;

import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * {@link LocalActivity} described the activity information
 *
 * @author johnson
 */
@DatabaseTable(tableName = BeastStore.Activities.CONTENT_CATEGORY)
public class LocalActivity implements PersistentObject {

    private static final long serialVersionUID = -4088024331689607983L;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.USER_ID, canBeNull = false)
    private String userId;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.USERNAME, canBeNull = false)
    private String username;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.EMAIL)
    private String email;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TYPE, canBeNull = false)
    private int type;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.STATE, canBeNull = false)
    private int state;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TITLE)
    private String title;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.DESCRIPTION)
    private String description;

    /**
     * Unit : milliseconds
     */
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.START_TIME, canBeNull = false)
    private long startTime;

    /**
     * Unit : milliseconds
     */
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.FINISH_TIME, canBeNull = false)
    private long finishTime;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.COORDINATE, canBeNull = false)
    private String coordinate;

    /**
     * Instantaneous velocity
     */
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.INSTANTANEOUS_VELOCITY)
    private double instantaneousVelocity;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TOTAL_DISTANCE, canBeNull = false)
    private double totalDistance;

    /**
     * Unit : seconds
     */
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TOTAL_ELAPSED_TIME, canBeNull = false)
    private double totalElapsedTime;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TOTAL_CALORIE)
    private double totalCalorie;

    /**
     * 累计爬升
     */
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TOTAL_RISEN_ALTITUDE)
    private double totalRisenAltitude;

    /**
     * 爬坡距离
     */
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.TOTAL_UPHILL_DISTANCE)
    private double totalUphillDistance;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.MAX_VELOCITY)
    private double maxVelocity;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.MAX_ALTITUDE)
    private double maxAltitude;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.MAX_CARDIAC_RATE)
    private double maxCardiacRate;

    @DatabaseField(columnName = "synced")
    private boolean synced;

    /**
     * Unit : milliseconds
     */
    @DatabaseField(columnName = "sync_time")
    private long syncTime;

    @DatabaseField(columnName = "remote_id")
    private String remoteId;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.ACTIVITY_URL)
    private String activityUrl;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.FAKE)
    private int fake;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.SPEED)
    private double speed;

    // ble添加字段
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.DEVICE_ID)
    private String deviceId;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.SOURCE)
    private String source;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.SAMPLE_COUNT)
    private int sampleCount;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.SAMPLE_RATE)
    private int sampleRate;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.BLE_DATA_TYPE)
    private int bleDataType;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.IS_PRIVATE)
    private int isPrivate;
    // v2.4.1
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.CARDIAC_RATE)
    private double cardiacRate;
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.CADENCE)
    private double cadence;
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.CADENCE_MAX)
    private double maxCadence;
    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.CENTRAL_ID)
    private String centralId;

    @DatabaseField(columnName = BeastStore.Activities.ActivityColumns.CENTRAL_NAME)
    private String centralName;

    //	@DatabaseField(columnName = BeastStore.Activities.ActivityColumns.SCENERY_URL)
//	private String sceneryUrl;
//	
//	@DatabaseField(columnName = BeastStore.Activities.ActivityColumns.LOCAL_SCENERY_URL)
//	private String localSceneryPath;

    private int progress;

    public LocalActivity() {
    }

    public LocalActivity(Parcel source) {
        this.id = source.readString();
        this.userId = source.readString();
        this.username = source.readString();
        this.email = source.readString();
        this.type = source.readInt();
        this.state = source.readInt();
        this.title = source.readString();
        this.description = source.readString();
        this.startTime = source.readLong();
        this.finishTime = source.readLong();
        this.coordinate = source.readString();
        this.totalDistance = source.readDouble();
        this.totalElapsedTime = source.readDouble();
        this.totalCalorie = source.readDouble();
        this.totalRisenAltitude = source.readDouble();
        this.totalUphillDistance = source.readDouble();
        this.maxAltitude = source.readDouble();
        this.maxVelocity = source.readDouble();
        this.maxCardiacRate = source.readDouble();
        this.synced = source.readByte() != 0;
        this.syncTime = source.readLong();
        this.remoteId = source.readString();
        this.activityUrl = source.readString();
        this.fake = source.readInt();
        this.speed = source.readDouble();

        // Ble 新添加字段
        this.source = source.readString();
        this.deviceId = source.readString();
        this.sampleCount = source.readInt();
        this.sampleRate = source.readInt();

        this.isPrivate = source.readInt();

        this.cadence = source.readFloat();
        this.maxCadence = source.readFloat();
        this.cardiacRate = source.readFloat();
        this.centralName = source.readString();
        this.centralId = source.readString();
    }

    public LocalActivity(ActivityDTO activity) {
        this.id = activity.getActivityIdentifier();
        this.remoteId = activity.getActivityId();
        this.username = activity.getUsername();
        this.userId = activity.getUserId();
        this.email = activity.getEmail();
        this.title = activity.getTitle();
        this.startTime = activity.getStartTime();
        this.finishTime = activity.getStopTime();
        this.totalDistance = activity.getTotalDistance();
        this.totalElapsedTime = activity.getElapsedTime();
        this.totalCalorie = activity.getCalories();
        this.totalRisenAltitude = activity.getRiseTotal();
        this.totalUphillDistance = activity.getUphillDistance();
        this.maxAltitude = activity.getMaxAltitude();
        this.maxVelocity = activity.getMaxVelocity();
        this.isPrivate = activity.getIsPrivate();
        this.speed = activity.getVelocity();
        if (this.speed <= 0 && this.totalElapsedTime > 0
                && this.totalDistance > 0)
            this.speed = totalDistance / this.totalElapsedTime * 3.6;

        // 从服务器获取的数据默认为已同步
        this.syncTime = System.currentTimeMillis();
        this.coordinate = "gcj02";
        this.synced = true;
        this.type = 2;
        this.state = 4;
        this.activityUrl = activity.getActivityUrl();
        if (activity.isFake()) {
            this.fake = 1; // 作弊数据
        } else {
            this.fake = 0;
        }

        this.deviceId = activity.getDeviceId();
        this.bleDataType = activity.getBleDataType();
        this.source = activity.getSource();


        // v2.4.1
        this.cardiacRate = activity.getCardiacRate();
        this.maxCardiacRate = activity.getMaxCardiacRate();
        this.cadence = activity.getCadence();
        this.maxCadence = activity.getMaxCadence();

        this.centralName = activity.getCentralName();
        this.centralId = activity.getCentralId();
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public double getInstantaneousVelocity() {
        return this.instantaneousVelocity;
    }

    public void setInstantaneousVelocity(double instantaneousVelocity) {
        this.instantaneousVelocity = instantaneousVelocity;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalElapsedTime() {
        return totalElapsedTime;
    }

    public void setTotalElapsedTime(double totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }

    public double getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(double totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    public double getTotalRisenAltitude() {
        return totalRisenAltitude;
    }

    public void setTotalRisenAltitude(double totalRisenAltitude) {
        this.totalRisenAltitude = totalRisenAltitude;
    }

    public double getTotalUphillDistance() {
        return totalUphillDistance;
    }

    public void setTotalUphillDistance(double totalUphillDistance) {
        this.totalUphillDistance = totalUphillDistance;
    }

    public double getMaxAltitude() {
        return this.maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public double getMaxVelocity() {
        return this.maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public double getMaxCardiacRate() {
        return this.maxCardiacRate;
    }

    public void setMaxCardiacRate(double maxCardiacRate) {
        this.maxCardiacRate = maxCardiacRate;
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

    public String getActivityUrl() {
        return activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public int getFake() {
        return fake;
    }

    public void setFake(int fake) {
        this.fake = fake;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBleDataType() {
        return bleDataType;
    }

    public void setBleDataType(int bleDataType) {
        this.bleDataType = bleDataType;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    //	public String getSceneryUrl() {
    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public double getCardiacRate() {
        return cardiacRate;
    }

    public void setCardiacRate(double cardiacRate) {
        this.cardiacRate = cardiacRate;
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

    public String getCentralId() {
        return centralId;
    }

    public void setCentralId(String centralId) {
        this.centralId = centralId;
    }

    //	public String getSceneryUrl() {
//		return sceneryUrl;
//	}
//
//	public void setSceneryUrl(String sceneryUrl) {
//		this.sceneryUrl = sceneryUrl;
//	}
//	
//	public String getLocalSceneryPath() {
//		return localSceneryPath;
//	}
//
//	public void setLocalSceneryPath(String localSceneryPath) {
//		this.localSceneryPath = localSceneryPath;
//	}


    @Override
    public String toString() {
        return "LocalActivity{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", coordinate='" + coordinate + '\'' +
                ", instantaneousVelocity=" + instantaneousVelocity +
                ", totalDistance=" + totalDistance +
                ", totalElapsedTime=" + totalElapsedTime +
                ", totalCalorie=" + totalCalorie +
                ", totalRisenAltitude=" + totalRisenAltitude +
                ", totalUphillDistance=" + totalUphillDistance +
                ", maxVelocity=" + maxVelocity +
                ", maxAltitude=" + maxAltitude +
                ", maxCardiacRate=" + maxCardiacRate +
                ", synced=" + synced +
                ", syncTime=" + syncTime +
                ", remoteId='" + remoteId + '\'' +
                ", activityUrl='" + activityUrl + '\'' +
                ", fake=" + fake +
                ", speed=" + speed +
                ", deviceId='" + deviceId + '\'' +
                ", source='" + source + '\'' +
                ", sampleCount=" + sampleCount +
                ", sampleRate=" + sampleRate +
                ", bleDataType=" + bleDataType +
                ", isPrivate=" + isPrivate +
                ", progress=" + progress +
                ",centralId=" + centralId +
                "centralName" + centralName +
                '}';
    }

    public String getCentralName() {
        return centralName;
    }

    public void setCentralName(String centralName) {
        this.centralName = centralName;
    }
}
