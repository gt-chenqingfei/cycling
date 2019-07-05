package com.beastbikes.android.modules.user.dao.entity;

import android.text.TextUtils;

import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@DatabaseTable(tableName = BeastStore.Users.CONTENT_CATEGORY)
public class LocalUser implements PersistentObject {

    private static final long serialVersionUID = 4490749315232538948L;

    @DatabaseField(columnName = BeastStore.Users.UserColumns._ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.USERNAME, canBeNull = false)
    private String username;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.NICKNAME)
    private String nickname;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.EMAIL)
    private String email;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.GENDER)
    private int gender = BeastStore.Users.GENDER_MALE;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.HEIGHT)
    private double height;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.WEIGHT)
    private double weight;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.PROVINCE)
    private String province;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.CITY)
    private String city;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.DISTRICT)
    private String district;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.TOTAL_DISTANCE)
    private double totalDistance;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.TOTAL_ELAPSED_TIME)
    private double totalElapsedTime;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.LATEST_ACTIVITY_TIME)
    private long latestActivityTime;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.USERID)
    private String userId;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.USERINTID)
    private long userIntId;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.UPDATEDAT)
    private String updatedAt;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.CREATEDAT)
    private String createdAt;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.CLUBID)
    private String clubId;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.OBJECTID)
    private String objectId;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.ISOK)
    private long isOk;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.GRIDNUM)
    private long gridNum;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.EDITED)
    private long edited;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.WEEKLYDISTANCE)
    private double weeklyDistance;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.SAMEGRID)
    private long sameGrid;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.MONTHLYDISTANCE)
    private double monthlyDistance;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.CLUBNAME)
    private String clubName;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.AVATAR)
    private String avatar;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.BIRTHDAY)
    private String birthday;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.FANS_NUM)
    private int fansNum;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.FOLLOWER_NUM)
    private int followerNum;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.FOLLOW_STATUS)
    private int followStatus;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.MEDAL_NUM)
    private int medalNum;

    @DatabaseField(columnName = BeastStore.Users.UserColumns.SPEEDX_ID)
    private int speedxId;

    private transient String password;

    public LocalUser() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        final List<String> location = new ArrayList<String>();

        final String province = this.getProvince();
        if (!TextUtils.isEmpty(province)) {
            location.add(province);
        }

        final String city = this.getCity();
        if (!TextUtils.isEmpty(city)) {
            location.add(city);
        }

        final String district = this.getDistrict();
        if (!TextUtils.isEmpty(district)) {
            location.add(district);
        }

        final StringBuilder sb = new StringBuilder();
        final Iterator<String> i = location.iterator();

        while (i.hasNext()) {
            sb.append(i.next());

            if (i.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalElapsedTime() {
        return this.totalElapsedTime;
    }

    public void setTotalElapsedTime(double totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }

    public long getLatestActivityTime() {
        return this.latestActivityTime;
    }

    public void setLatestActivityTime(long latestActivityTime) {
        this.latestActivityTime = latestActivityTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getUserIntId() {
        return userIntId;
    }

    public void setUserIntId(long userIntId) {
        this.userIntId = userIntId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getIsOk() {
        return isOk;
    }

    public void setIsOk(long isOk) {
        this.isOk = isOk;
    }

    public long getGridNum() {
        return gridNum;
    }

    public void setGridNum(long gridNum) {
        this.gridNum = gridNum;
    }

    public long getEdited() {
        return edited;
    }

    public void setEdited(long edited) {
        this.edited = edited;
    }

    public double getWeeklyDistance() {
        return weeklyDistance;
    }

    public void setWeeklyDistance(double weeklyDistance) {
        this.weeklyDistance = weeklyDistance;
    }

    public long getSameGrid() {
        return sameGrid;
    }

    public void setSameGrid(long sameGrid) {
        this.sameGrid = sameGrid;
    }

    public double getMonthlyDistance() {
        return monthlyDistance;
    }

    public void setMonthlyDistance(double monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(int followerNum) {
        this.followerNum = followerNum;
    }

    public int getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(int followStatus) {
        this.followStatus = followStatus;
    }

    public int getMedalNum() {
        return medalNum;
    }

    public void setMedalNum(int medalNum) {
        this.medalNum = medalNum;
    }

    public int getSpeedxId() {
        return speedxId;
    }

    public void setSpeedxId(int speedxId) {
        this.speedxId = speedxId;
    }
}
