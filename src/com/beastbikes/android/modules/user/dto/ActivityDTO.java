package com.beastbikes.android.modules.user.dto;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ActivityDTO implements Serializable {

    private static final long serialVersionUID = 6214355107311371136L;

    private String userId;
    private String email;
    private String username;
    private String nickname;
    private boolean synced = true;

    /**
     * 对应表"sportRoute"的ID
     */
    private String activityId;
    /**
     * 对应表"sportRoute"的activityIdentifier
     */
    private String activityIdentifier;
    /**
     * 骑行名称
     */
    private String title;
    /**
     * 骑行里程，以米为单位
     */
    private double totalDistance;
    /**
     * 骑行速度，千米每小时
     */
    private double velocity;
    /**
     * 极速，千米每小时
     */
    private double maxVelocity;
    /**
     * 骑行时间，以秒为单位
     */
    private double elapsedTime;
    /**
     * 卡路里
     */
    private double calories;
    /**
     * 以毫秒为单位
     */
    private long startTime;
    /**
     * 以毫秒为单位
     */
    private long stopTime;
    /**
     * 上坡距离
     */
    private double uphillDistance;
    /**
     * 累计爬升
     */
    private double riseTotal;
    /**
     * 最高海拔
     */
    private double maxAltitude;

    private String avatarUrl;
    private String activityUrl;
    //	private String coverUrl;
    private boolean fake;
    private String cityName;
//	private String localCoverUrl;

    // 是否已举报
    private boolean hasReport;

    private boolean isChecked;

    private boolean nuked = false;

    private int isPrivate;

    /**
     * 平均心率
     */
    private double cardiacRate;
    /**
     * 最大心率
     */
    private double maxCardiacRate;
    /**
     * 平均踏频
     */
    private double cadence;
    /**
     * 最大踏频
     */
    private double maxCadence;


    // 设备ID
    private String deviceId;
    private int bleDataType;
    //source如果为空,则来自apple watch
    private String source;
    //如果source不为空,central name为空,则为第三方,否则直接显示
    private String centralName;

    //设备信息
    private String note;
    private String updateAt;
    private String objectId;
    private String centralId;
    private boolean samples;
    private boolean cycling;
    private boolean baiduMap;
    private boolean automotive;
    private boolean walking;
    private String createdAt;
    private boolean running;

    private List<LatlngDTO> points;

    public ActivityDTO() {
    }

    public ActivityDTO(LocalActivity localActivity) {
        if (localActivity == null)
            return;
        this.userId = localActivity.getUserId();
        this.activityId = localActivity.getRemoteId();
        this.activityIdentifier = localActivity.getId();
        this.email = localActivity.getEmail();
        this.username = localActivity.getUsername();
        this.totalDistance = localActivity.getTotalDistance();
        this.maxVelocity = localActivity.getMaxVelocity();
        this.elapsedTime = localActivity.getTotalElapsedTime();
        this.calories = localActivity.getTotalCalorie();
        Date dateStart = DateFormatUtil.stringFormat2Date(DateFormatUtil.
                dateFormat2String(localActivity.getStartTime()));
        Date dateStop = DateFormatUtil.stringFormat2Date(DateFormatUtil.
                dateFormat2String(localActivity.getStartTime()));
        if (dateStart != null) {
            this.startTime = dateStart.getTime();
        }
        if (dateStop != null) {
            this.stopTime = dateStop.getTime();
        }
//        this.stopTime = localActivity.getFinishTime();
        this.title = localActivity.getTitle();
        this.uphillDistance = localActivity.getTotalUphillDistance();
        this.riseTotal = localActivity.getTotalRisenAltitude();
        this.activityUrl = localActivity.getActivityUrl();
        this.fake = localActivity.getFake() == 1;

        this.velocity = localActivity.getSpeed();
        if (this.velocity <= 0 && elapsedTime > 0
                && this.totalDistance > 0)
            this.velocity = totalDistance / elapsedTime * 3.6;
        this.synced = localActivity.isSynced();
        this.isPrivate = localActivity.getIsPrivate();

        this.deviceId = localActivity.getDeviceId();
        this.bleDataType = localActivity.getBleDataType();
        this.source = localActivity.getSource();

        this.cadence = localActivity.getCadence();
        this.maxCadence = localActivity.getMaxCadence();
        this.cardiacRate = localActivity.getCardiacRate();
        this.maxCardiacRate = localActivity.getMaxCardiacRate();
        this.centralName = localActivity.getCentralName();
    }

    public ActivityDTO(JSONObject json) {
        this.activityId = json.optString("id");
        this.userId = json.optString("userId");
        this.email = json.optString("email");
        this.username = json.optString("username");
        this.activityIdentifier = json.optString("sportIdentify");
        this.totalDistance = json.optDouble("distance");
        this.velocity = json.optDouble("speed");
        this.maxVelocity = json.optDouble("speedMax");
        this.elapsedTime = json.optDouble("time");
        this.calories = json.optDouble("calories");

        Date dateStart = DateFormatUtil.stringFormat2Date(json.optString("startDate"));
        Date dateStop = DateFormatUtil.stringFormat2Date(json.optString("stopDate"));

        if (dateStart != null) {
            this.startTime = dateStart.getTime();
        }
        if (dateStop != null) {
            this.stopTime = dateStop.getTime();
        }

        this.title = json.optString("title");
        this.uphillDistance = json.optDouble("uphillDistance");
        this.riseTotal = json.optDouble("riseTotal");
        this.activityUrl = json.optString("cyclingImage");
        this.fake = json.optBoolean("fake");
        this.hasReport = json.optBoolean("hasReport");
        this.nuked = json.optBoolean("nuked", false);
        this.isPrivate = json.optBoolean("isPrivate") ? 1 : 0;
        this.source = json.optString("source");

        this.cardiacRate = json.optDouble("cardiacRate");
        this.maxCardiacRate = json.optDouble("cardiacRateMax");
        this.cadence = json.optDouble("cadence");
        this.maxCadence = json.optDouble("cadenceMax");
        this.centralName = json.optString("centralName");

        this.note = json.optString("note");
        this.updateAt = json.optString("updateAt");
        this.objectId = json.optString("objectId");
        this.centralId = json.optString("centralId");
        this.samples = json.optBoolean("samples");
        this.cycling = json.optBoolean("cycling");
        this.baiduMap = json.optBoolean("baiduMap");
        this.automotive = json.optBoolean("automotive");
        this.walking = json.optBoolean("walking");
        this.createdAt = json.optString("createdAt");
        this.running = json.optBoolean("running");
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getActivityId() {
        return this.activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityIdentifier() {
        return this.activityIdentifier;
    }

    public void setActivityIdentifier(String activityIdentifier) {
        this.activityIdentifier = activityIdentifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public double getCalories() {
        return this.calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public double getUphillDistance() {
        return uphillDistance;
    }

    public void setUphillDistance(double uphillDistance) {
        this.uphillDistance = uphillDistance;
    }

    public double getRiseTotal() {
        return riseTotal;
    }

    public void setRiseTotal(double riseTotal) {
        this.riseTotal = riseTotal;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double altitude) {
        this.maxAltitude = altitude;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getActivityUrl() {
        return activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

//	public String getCoverUrl() {
//		return coverUrl;
//	}
//
//	public void setCoverUrl(String coverUrl) {
//		this.coverUrl = coverUrl;
//	}

    public List<LatlngDTO> getPoints() {
        return points;
    }

    public void setPoints(List<LatlngDTO> points) {
        this.points = points;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean isHasReport() {
        return hasReport;
    }

    public void setHasReport(boolean hasReport) {
        this.hasReport = hasReport;
    }

//	public String getLocalCoverUrl() {
//		return localCoverUrl;
//	}
//
//	public void setLocalCoverUrl(String localCoverUrl) {
//		this.localCoverUrl = localCoverUrl;
//	}

    public void setNuked(boolean nuked) {
        this.nuked = nuked;
    }

    public boolean isNuked() {
        return this.nuked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getBleDataType() {
        return bleDataType;
    }

    public void setBleDataType(int bleDataType) {
        this.bleDataType = bleDataType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getCardiacRate() {
        return cardiacRate;
    }

    public void setCardiacRate(float cardiacRate) {
        this.cardiacRate = cardiacRate;
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

    public String getCentralName() {
        return centralName;
    }

    public void setCentralName(String centralName) {
        this.centralName = centralName;
    }

    public String getCentralId() {
        return centralId;
    }

    public void setCentralId(String centralId) {
        this.centralId = centralId;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", synced=" + synced +
                ", activityId='" + activityId + '\'' +
                ", activityIdentifier='" + activityIdentifier + '\'' +
                ", title='" + title + '\'' +
                ", totalDistance=" + totalDistance +
                ", velocity=" + velocity +
                ", maxVelocity=" + maxVelocity +
                ", elapsedTime=" + elapsedTime +
                ", calories=" + calories +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", uphillDistance=" + uphillDistance +
                ", riseTotal=" + riseTotal +
                ", maxAltitude=" + maxAltitude +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", activityUrl='" + activityUrl + '\'' +
                ", fake=" + fake +
                ", cityName='" + cityName + '\'' +
                ", hasReport=" + hasReport +
                ", isChecked=" + isChecked +
                ", nuked=" + nuked +
                ", isPrivate=" + isPrivate +
                ", cardiacRate=" + cardiacRate +
                ", maxCardiacRate=" + maxCardiacRate +
                ", cadence=" + cadence +
                ", maxCadence=" + maxCadence +
                ", deviceId='" + deviceId + '\'' +
                ", bleDataType=" + bleDataType +
                ", source='" + source + '\'' +
                ", centralName='" + centralName + '\'' +
                ", note='" + note + '\'' +
                ", updateAt='" + updateAt + '\'' +
                ", objectId='" + objectId + '\'' +
                ", centralId='" + centralId + '\'' +
                ", samples=" + samples +
                ", cycling=" + cycling +
                ", baiduMap=" + baiduMap +
                ", automotive=" + automotive +
                ", walking=" + walking +
                ", createdAt='" + createdAt + '\'' +
                ", running=" + running +
                ", points=" + points +
                '}';
    }
}
