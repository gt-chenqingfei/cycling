package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by zhangyao on 2016/1/11.
 */
public class ClubActivityInfo implements Serializable {
    private String startDate;

    private String endDate;

    private String userId;

    private boolean isClubPrivate = true;

    private String mobPlace;

    private boolean isRegistered;

    private String id;

    private String actId;

    private int maxMembers;

    private String clubId;

    private String objectId;

    private String title;

    private String routeId;

    private String originator;

    private String routeImage;

    private boolean isOriginator;

    private String decstiption;

    private String routeName;

    private int members;

    private double[] mobPoint = new double[2];

    private String desc;

    private String mobilephone;

    private String applyEndDate;

    private String cover;

    private int status;


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isClubPrivate() {
        return isClubPrivate;
    }

    public void setIsClubPrivate(boolean isClubPrivate) {
        this.isClubPrivate = isClubPrivate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobPlace() {
        return mobPlace;
    }

    public void setMobPlace(String mobPlace) {
        this.mobPlace = mobPlace;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(String routeImage) {
        this.routeImage = routeImage;
    }

    public boolean isOriginator() {
        return isOriginator;
    }

    public void setIsOriginator(boolean isOriginator) {
        this.isOriginator = isOriginator;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public double[] getMobPoint() {
        return mobPoint;
    }

    public void setMobPoint(double[] mobPoint) {
        this.mobPoint = mobPoint;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getApplyEndDate() {
        return applyEndDate;
    }

    public void setApplyEndDate(String applyEndDate) {
        this.applyEndDate = applyEndDate;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDecstiption() {return decstiption;}

    public void setDecstiption(String decstiption) {this.decstiption = decstiption;}

    public ClubActivityInfo(){};


    public ClubActivityInfo(JSONObject result){
        this.setStartDate(result.optString("startDate"));
        this.setRouteName(result.optString("routeName"));
        this.setEndDate(result.optString("endDate"));
        this.setClubId(result.optString("clubId"));
        this.setTitle(result.optString("title"));
        this.setRouteImage(result.optString("routeImage"));
        this.setUserId(result.optString("userId"));
        this.setRouteId(result.optString("routeId"));
        this.setStatus(result.optInt("status"));
        this.setMobPlace(result.optString("mobPlace"));
        this.setMembers(result.optInt("members"));
        this.setMobilephone(result.optString("mobilephone"));
        this.setActId(result.optString("actId"));
        this.setDesc(result.optString("desc"));
        this.setIsRegistered(result.optBoolean("isRegistered"));
        this.setIsOriginator(result.optBoolean("isOriginator"));
        this.setIsClubPrivate(result.optBoolean("isClubPrivate"));
        this.setMaxMembers(result.optInt("maxMembers"));
        this.setOriginator(result.optString("originator"));
        this.setApplyEndDate(result.optString("applyEndDate"));
        this.setCover(result.optString("cover"));
        this.setDecstiption(result.optString("decstiption"));
        if (result.optJSONArray("mobPoint")!=null) {
            for (int i = 0; i < result.optJSONArray("mobPoint").length(); i++) {
                mobPoint[i] = result.optJSONArray("mobPoint").optDouble(i);
            }
        }
    }

    @Override
    public String toString() {
        return "ClubActivityInfo{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", userId='" + userId + '\'' +
                ", isClubPrivate=" + isClubPrivate +
                ", mobPlace='" + mobPlace + '\'' +
                ", isRegistered=" + isRegistered +
                ", id='" + id + '\'' +
                ", actId='" + actId + '\'' +
                ", maxMembers=" + maxMembers +
                ", clubId='" + clubId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", title='" + title + '\'' +
                ", routeId='" + routeId + '\'' +
                ", originator='" + originator + '\'' +
                ", routeImage='" + routeImage + '\'' +
                ", isOriginator=" + isOriginator +
                ", decstiption='" + decstiption + '\'' +
                ", routeName='" + routeName + '\'' +
                ", members=" + members +
                ", mobPoint=" + Arrays.toString(mobPoint) +
                ", desc='" + desc + '\'' +
                ", mobilephone='" + mobilephone + '\'' +
                ", applyEndDate='" + applyEndDate + '\'' +
                ", cover='" + cover + '\'' +
                ", status=" + status +
                '}';
    }
}
