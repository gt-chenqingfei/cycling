package com.beastbikes.android.modules.cycling.ranking.dto;

import org.json.JSONObject;

/**
 * Created by caoxiao on 15/12/3.
 */
public class RankDTO2 {
    private boolean ismanager;
    private String clubId;
    private int level;
    private String userId;
    private String joined;
    private long score;

    private JSONObject user;
    private String province;
    private String city;
    private double weeklyDistance;
    private double totalDistance;
    private String area;
    private String avatarImage;
    private int userInt;
    private String avatar;
    private String nickname;
    private double monthlyDistance;
    private double milestone;

    private int ordinal;
    private String remarks;

    public RankDTO2(JSONObject jsonObject) {
        this.ismanager = jsonObject.optBoolean("ismanager");
        this.clubId = jsonObject.optString("clubId");
        this.level = jsonObject.optInt("level");
        this.userId = jsonObject.optString("userId");
        this.joined = jsonObject.optString("joined");
        this.score = jsonObject.optLong("score");
        this.milestone = jsonObject.optDouble("milestone");

        this.user = jsonObject.optJSONObject("user");
        if (user != null) {
            this.province = user.optString("province");
            this.city = user.optString("city");
            this.weeklyDistance = user.optLong("weeklyDistance");
            this.totalDistance = user.optLong("totalDistance");
            this.area = user.optString("area");
            this.avatarImage = user.optString("avatar");
            this.userInt = user.optInt("userInt");
            this.avatar = user.optString("avatar");
            this.nickname = user.optString("nickname");
            this.monthlyDistance = user.optLong("monthlyDistance");
            this.remarks = user.optString("remarks");
        }
    }

    public boolean ismanager() {
        return ismanager;
    }

    public void setIsmanager(boolean ismanager) {
        this.ismanager = ismanager;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getWeeklyDistance() {
        return weeklyDistance;
    }

    public void setWeeklyDistance(double weeklyDistance) {
        this.weeklyDistance = weeklyDistance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public int getUserInt() {
        return userInt;
    }

    public void setUserInt(int userInt) {
        this.userInt = userInt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getMonthlyDistance() {
        return monthlyDistance;
    }

    public void setMonthlyDistance(double monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public double getMilestone() {
        return milestone;
    }

    public void setMilestone(double milestone) {
        this.milestone = milestone;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
