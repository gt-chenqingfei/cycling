package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by caoxiao on 15/12/3.
 */
public class ClubRankBean implements Serializable {

    private String city;
    private String name;
    private String objectId;
    private long score;
    private int members;
    private double milestone;
    private String logo;
    private boolean isPrivate = false;
    private int ordinal;

    public ClubRankBean() {
    }

    public ClubRankBean(JSONObject json) {
        this.city = json.optString("city");
        this.name = json.optString("name");
        this.objectId = json.optString("objectId");
        this.score = json.optLong("score");
        this.members = json.optInt("members");
        this.milestone = json.optDouble("milestone");
        this.logo = json.optString("logo");
        this.isPrivate = json.optBoolean("isPrivate");
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public double getMilestone() {
        return milestone;
    }

    public void setMilestone(double milestone) {
        this.milestone = milestone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
