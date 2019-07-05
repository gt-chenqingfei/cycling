package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

/**
 * Created by zhangyao on 2016/1/17.
 */
public class ClubActRegisterInfo {
    private String userId;
    private String actId;
    private int status;

    public ClubActRegisterInfo(JSONObject result){
        this.setUserId(result.optString("userId"));
        this.setActId(result.optString("actId"));
        this.setStatus(result.optInt("status"));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClubActRegisterInfo{" +
                "userId='" + userId + '\'' +
                ", actId='" + actId + '\'' +
                ", status=" + status +
                '}';
    }
}
