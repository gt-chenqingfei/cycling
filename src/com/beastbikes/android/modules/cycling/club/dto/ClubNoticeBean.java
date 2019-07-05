package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

/**
 * Created by caoxiao on 15/12/4.
 */
public class ClubNoticeBean {
    private String content;
    private long timestamp;
    private String clubId;
    private String createdAt;

    public ClubNoticeBean(JSONObject jsonObject) {
        this.content = jsonObject.optString("content");
        this.timestamp = jsonObject.optLong("timestamp");
        this.clubId = jsonObject.optString("clubId");
        this.createdAt = jsonObject.optString("createdAt");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
