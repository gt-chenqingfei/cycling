package com.beastbikes.android.modules.shop.dto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by caoxiao on 16/4/12.
 */
public class BikeShopListDTO implements Serializable {

    public static final int STATUS_PASS = 1;
    public static final int STATUS_UNTREATED = 0;
    public static final int STATUS_FAIL = -1;

    private double range;
    private String address;
    private double longitude;
    private double latitude;
    private long shopId;
    private String name;
    private String avatar;
    private String ownerId;
    private int status;
    private String reason;
    private int level;

    private BikeShopTagInfoDto tagInfo;

    public BikeShopListDTO(JSONObject jsonObject) {
        this.range = jsonObject.optDouble("range");
        this.address = jsonObject.optString("address");
        JSONArray location = jsonObject.optJSONArray("location");
        if (location != null) {
            this.longitude = location.optDouble(0);
            this.latitude = location.optDouble(1);
        }
        this.shopId = jsonObject.optLong("shopId");
        this.name = jsonObject.optString("name");
        this.avatar = jsonObject.optString("logo");

        JSONObject tagInfoObject = jsonObject.optJSONObject("tagInfo");
        if(tagInfoObject != null){
            tagInfo = new BikeShopTagInfoDto(tagInfoObject);
        }

        this.status = jsonObject.optInt("status");
        this.ownerId = jsonObject.optString("ownerId");
        this.reason = jsonObject.optString("rejectReason");
        this.level = jsonObject.optInt("level");
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BikeShopTagInfoDto getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(BikeShopTagInfoDto tagInfo) {
        this.tagInfo = tagInfo;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
