package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

/**
 * Created by zhangyao on 2016/1/14.
 */
public class ClubActivityOriginator {
    private String province;

    private String city;

    private String area;

    private String avatarImage;

    private String userId;

    private int userIntId;

    private String avatar;

    private String nickname;

    public ClubActivityOriginator(JSONObject originator){
        this.setArea(originator.optString("area"));
        this.setAvatar(originator.optString("avatar"));
        this.setAvatarImage(originator.optString("avatarImage"));
        this.setCity(originator.optString("city"));
        this.setNickname(originator.optString("nickname"));
        this.setProvince(originator.optString("province"));
        this.setUserId(originator.optString("userId"));
        this.setUserIntId(originator.optInt("userIntId"));
    }

    public void setProvince(String province){
        this.province = province;
    }
    public String getProvince(){
        return this.province;
    }
    public void setCity(String city){
        this.city = city;
    }
    public String getCity(){
        return this.city;
    }
    public void setArea(String area){
        this.area = area;
    }
    public String getArea(){
        return this.area;
    }
    public void setAvatarImage(String avatarImage){
        this.avatarImage = avatarImage;
    }
    public String getAvatarImage(){
        return this.avatarImage;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return this.userId;
    }
    public void setUserIntId(int userIntId){
        this.userIntId = userIntId;
    }
    public int getUserIntId(){
        return this.userIntId;
    }
    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
    public String getAvatar(){
        return this.avatar;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return this.nickname;
    }

    @Override
    public String toString() {
        return "ClubActivityOriginator{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", avatarImage='" + avatarImage + '\'' +
                ", userId='" + userId + '\'' +
                ", userIntId=" + userIntId +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
