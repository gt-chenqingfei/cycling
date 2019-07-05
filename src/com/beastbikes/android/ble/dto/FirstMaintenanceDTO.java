package com.beastbikes.android.ble.dto;

import org.json.JSONObject;

/**
 * Created by chenqingfei on 16/7/28.
 */
public class FirstMaintenanceDTO {
    public static final int STATUS_UN_USE = 0;
    private int status;
    private String name;
    private String redeemCode;
    private String expireAt;
    private int duration;
    private String desc;
    private String title;
    private String description;

    public FirstMaintenanceDTO(JSONObject jsonObject) {
        if (jsonObject == null) return;

        JSONObject params = jsonObject.optJSONObject("params");
        if (params == null) return;

        JSONObject text = params.optJSONObject("text");

        JSONObject coupons = text.optJSONObject("coupons");
        if (coupons != null) {
            this.status = coupons.optInt("status");
            this.expireAt = coupons.optString("expireAt");
            this.redeemCode = coupons.optString("redeemCode");
            this.name = coupons.optString("name");
            this.description = coupons.optString("description");
        }

        this.title = text.optString("title");
        this.desc = text.optString("desc");
        this.duration = params.optInt("duration");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
