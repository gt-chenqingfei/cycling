package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zhangyao on 2016/1/14.
 */
public class ClubActivityUser implements Serializable {
    private String province;

    private String city;

    private String area;

    private String userId;

    private int userIntId;

    private String avatar;

    private String nickname;

    private String remarks;


    public ClubActivityUser(JSONObject info) {
        this.setUserIntId(info.optInt("userIntId"));
        this.setUserId(info.optString("userId"));
        this.setNickname(info.optString("nickname"));
        this.setCity(info.optString("city"));
        this.setArea(info.optString("area"));
        this.setAvatar(info.optString("avatar"));
        this.setRemarks(info.optString("remarks"));
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return this.province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return this.area;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserIntId(int userIntId) {
        this.userIntId = userIntId;
    }

    public int getUserIntId() {
        return this.userIntId;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "ClubActivityUser{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", userId='" + userId + '\'' +
                ", userIntId=" + userIntId +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
