package com.beastbikes.android.modules.user.dto;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;

public class ProfileDTO implements Serializable {

    private static final long serialVersionUID = -878422186334908806L;

    private String userId;
    private String username;
    private String nickname;
    private String email;
    private int sex;
    private double weight;
    private double height;
    private String objectId;
    // 位置信息
    private String province;
    private String city;
    private String district;
    private String clubName;

    // 骑行数据
    private double totalDistance;
    private double weeklyDistance;
    private double monthlyDistance;
    private long totalElapsedTime;
    private long latestActivityTime;

    private String avatar;
    private boolean edited;

    // 穿越格子总数
    private int gridNum;
    // 共同的格子
    private int sameNum;

    private int userIntId;
    private String updatedAt;
    private String createdAt;
    private String clubId;
    private int isOk;
    private String birthday;
    private String remarks;
    private int followNum;
    private int followStatu;
    private int medalNum;
    private boolean isFriend = false;
    private int fansNum;
    private int speedxId;

    private int maxHeartRate;

    public ProfileDTO() {
    }

    public int getSpeedxId() {
        return speedxId;
    }

    public void setSpeedxId(int speedxId) {
        this.speedxId = speedxId;
    }

    public ProfileDTO(JSONObject json) {
        this.userId = json.optString("userId");
        this.username = json.optString("username");
        this.nickname = json.optString("nickname");
        this.email = json.optString("email");

        this.sex = Integer.parseInt(json.optString("sex", "1"));
        this.weight = json.optDouble("weight");
        this.height = json.optDouble("height");

        this.province = json.optString("province");
        this.city = json.optString("city");
        this.district = json.optString("area");

        this.totalDistance = json.optDouble("totalDistance", 0);
        this.monthlyDistance = json.optDouble("monthlyDistance", 0);
        this.weeklyDistance = json.optDouble("weeklyDistance", 0);
        this.totalElapsedTime = json.optLong("totalTime", 0);
        this.latestActivityTime = json.optLong("lastestCyclingTime", 0);
        this.avatar = json.optString("avatar");

        this.edited = json.optBoolean("edited");
        this.objectId = json.optString("objectId");

        this.gridNum = json.optInt("gridNum");
        this.sameNum = json.optInt("sameGrid");
        this.clubName = json.optString("clubName");

        this.userIntId = json.optInt("userIntId");
        this.updatedAt = json.optString("updatedAt");
        this.createdAt = json.optString("createdAt");
        this.clubId = json.optString("clubId");
        this.isOk = json.optInt("isOk");
        this.birthday = json.optString("birthday");
        this.remarks = json.optString("remarks");
        this.followNum = json.optInt("followNum");
        this.followStatu = json.optInt("followStatu");
        this.isFriend = json.optBoolean("isFriend");
        this.fansNum = json.optInt("fansNum");
        this.medalNum = json.optInt("badgeNum");
        this.speedxId = json.optInt("speedxId");

        this.maxHeartRate = json.optInt("cardiacRate");
    }

    public ProfileDTO(String userId, String nickname, String avatar) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUserId() {
        return this.userId;
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
        final StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(province) && !province.equals("null")
                && !province.equals("unknown")) {
            sb.append(province);
        }

        if (!TextUtils.isEmpty(city) && !city.equals("null") && !province.equals(city)
                && !city.equals("unknown")) {
            sb.append(",").append(city);
        }

        if (!TextUtils.isEmpty(district) && !district.equals("null")
                && !district.equals("unknown")) {
            sb.append(",").append(district);
        }

        return sb.toString();
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalElapsedTime() {
        return this.totalElapsedTime;
    }

    public void setTotalElapsedTime(long totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }

    public long getLatestActivityTime() {
        return this.latestActivityTime;
    }

    public void setLatestActivityTime(long latestActivityTime) {
        this.latestActivityTime = latestActivityTime;
    }

    public double getWeeklyDistance() {
        return weeklyDistance;
    }

    public void setWeeklyDistance(double weeklyDistance) {
        this.weeklyDistance = weeklyDistance;
    }

    public double getMonthlyDistance() {
        return monthlyDistance;
    }

    public void setMonthlyDistance(double monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGridNum() {
        return gridNum;
    }

    public void setGridNum(int gridNum) {
        this.gridNum = gridNum;
    }

    public int getSameNum() {
        return sameNum;
    }

    public void setSameNum(int sameNum) {
        this.sameNum = sameNum;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getUserIntId() {
        return userIntId;
    }

    public void setUserIntId(int userIntId) {
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

    public int getIsOk() {
        return isOk;
    }

    public void setIsOk(int isOk) {
        this.isOk = isOk;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public int getFollowStatu() {
        return followStatu;
    }

    public void setFollowStatu(int followStatu) {
        this.followStatu = followStatu;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getMedalNum() {
        return medalNum;
    }

    public void setMedalNum(int medalNum) {
        this.medalNum = medalNum;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }
}
