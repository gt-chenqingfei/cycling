package com.beastbikes.android.modules.cycling.sections.dto;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/4/15.
 */
public class SegmentRankDTO {
    private double duration;
    private String area;
    private String nickname;
    private long userIntId;
    private String userId;
    private String province;
    private String avatar;
    private String city;

    public SegmentRankDTO(JSONObject jsonObject) {
        this.duration = jsonObject.optDouble("duration");
        this.area = jsonObject.optString("area");
        this.nickname = jsonObject.optString("nickname");
        this.userIntId = jsonObject.optLong("userIntId");
        this.userId = jsonObject.optString("userId");
        this.province = jsonObject.optString("province");
        this.avatar = jsonObject.optString("avatar");
        this.city = jsonObject.optString("city");
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getUserIntId() {
        return userIntId;
    }

    public void setUserIntId(long userIntId) {
        this.userIntId = userIntId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
