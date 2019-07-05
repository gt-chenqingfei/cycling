package com.beastbikes.android.modules.cycling.ranking.dto;

import org.json.JSONObject;

import java.io.Serializable;

public class RankDTO implements Serializable{

    private String userId;//
    private String username;
    private String nickname;//
    private String email;
    private String province;//
    private String city;//
    private String district;
    private String area;//
    private double yearlyDistance;//
    private String avatarUrl;//
    private double totalDistance;//
    private double weeklyDistance;//
    private double monthlyDistance;//
    private long lastActivityTime;
    private int ordinal;
    private double milestone;
    private int userIntId;//
    private double rankDistance;//
    private String clubName;//

    // 俱乐部新增字段
    private double score;
    private boolean isManager;
    private int level;

    private int rankType;
    private String joined;

    private String remarks;

    public RankDTO() {

    }

    public RankDTO(JSONObject json) {
        this.userId = json.optString("userId");
        this.username = json.optString("username");
        this.nickname = json.optString("nickname");
        this.email = json.optString("email");
        this.province = json.optString("province");
        this.city = json.optString("city");
        this.district = json.optString("area");
        this.weeklyDistance = json.optDouble("weeklyDistance", 0);
        this.monthlyDistance = json.optDouble("monthlyDistance", 0);
        this.totalDistance = json.optDouble("totalDistance", 0);
        this.lastActivityTime = json.optLong("lastestCyclingTime", 0);
        this.avatarUrl = json.optString("avatar");
        this.area = json.optString("area");
        this.yearlyDistance = json.optDouble("yearlyDistance", 0);
        this.userIntId = json.optInt("userIntId", 0);
        this.rankDistance = json.optDouble("rankDistance", 0);
        this.clubName = json.optString("clubName");
        JSONObject obj = json.optJSONObject("user");
        if (null != obj) {
            this.avatarUrl = obj.optString("avatar");
        }
        this.ordinal = json.optInt("rank");
        this.rankType = json.optInt("rankType");
        this.milestone = json.optDouble("milestone");
        this.joined = json.optString("joined");
        this.isManager = json.optBoolean("ismanager");
        this.remarks = json.optString("remarks");
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

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getLastActivityTime() {
        return this.lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean isManager) {
        this.isManager = isManager;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRankType() {
        return rankType;
    }

    public void setRankType(int rankType) {
        this.rankType = rankType;
    }

    public void setMilestone(double milestone) {
        this.milestone = milestone;
    }

    public double getMilestone() {
        return this.milestone;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getYearlyDistance() {
        return yearlyDistance;
    }

    public void setYearlyDistance(double yearlyDistance) {
        this.yearlyDistance = yearlyDistance;
    }

    public int getUserIntId() {
        return userIntId;
    }

    public void setUserIntId(int userIntId) {
        this.userIntId = userIntId;
    }

    public double getRankDistance() {
        return rankDistance;
    }

    public void setRankDistance(double rankDistance) {
        this.rankDistance = rankDistance;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "RankDTO [userId=" + userId + ", username=" + username
                + ", nickname=" + nickname + ", email=" + email + ", province="
                + province + ", city=" + city + ", district=" + district
                + ", avatarUrl=" + avatarUrl + ", totalDistance="
                + totalDistance + ", weeklyDistance=" + weeklyDistance
                + ", monthlyDistance=" + monthlyDistance
                + ", lastActivityTime=" + lastActivityTime + ", ordinal="
                + ordinal + ", type=" + rankType + "]";
    }

}
