package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by caoxiao on 16/3/14.
 */
public class ClubActivityListDTO implements Serializable {

    private String startDate;

    private boolean joined;

    private String endDate;

    private String title;

    private String routeImage;

    private String routeId;

    private int applyStatus;

    private String mobPlace;

    private int members;

    private String actId;

    private String id;

    private String cover;

    private String actUrl;

    private boolean isManager;

    private String avatarImage;

    private String nickname;

    private String remarks;

    private int signInCount;

    private int maxMembers;

    public static final int CLUB_ACTIVITY_STATUS_ONGOING = 0;

    public static final int CLUB_ACTIVITY_STATUS_ENDED = 2;

    public static final int CLUB_ACTIVITY_STATUS_CANCEL = 3;

    public ClubActivityListDTO(JSONObject result) {
        this.setStartDate(result.optString("startDate"));
        this.setEndDate(result.optString("endDate"));
        this.setTitle(result.optString("title"));
        this.setRouteImage(result.optString("routeImage"));
        this.setRouteId(result.optString("routeId"));
        this.setApplyStatus(result.optInt("applyStatus"));
        this.setMobPlace(result.optString("mobPlace"));
        this.setMembers(result.optInt("members"));
        this.setActId(result.optString("actId"));
        this.setJoined(result.optBoolean("joined"));
        this.id = result.optString("id");
        this.cover = result.optString("cover");
        this.actUrl = result.optString("actUrl");
        this.isManager = result.optBoolean("isManager");
        this.avatarImage = result.optString("avatarImage");
        this.nickname = result.optString("nickname");
        this.remarks = result.optString("remarks");
        this.signInCount = result.optInt("signInCount");
        this.maxMembers = result.optInt("maxMembers");
    }

    public boolean getJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setRouteImage(String routeImage) {
        this.routeImage = routeImage;
    }

    public String getRouteImage() {
        return this.routeImage;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteId() {
        return this.routeId;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public int getApplyStatus() {
        return this.applyStatus;
    }

    public void setMobPlace(String mobPlace) {
        this.mobPlace = mobPlace;
    }

    public String getMobPlace() {
        return this.mobPlace;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getMembers() {
        return this.members;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActId() {
        return this.actId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getActUrl() {
        return actUrl;
    }

    public void setActUrl(String actUrl) {
        this.actUrl = actUrl;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    public boolean isJoined() {
        return joined;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getSignInCount() {
        return signInCount;
    }

    public void setSignInCount(int signInCount) {
        this.signInCount = signInCount;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }
}
