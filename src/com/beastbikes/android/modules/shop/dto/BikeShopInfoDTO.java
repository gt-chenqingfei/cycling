package com.beastbikes.android.modules.shop.dto;

import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/13.
 */
public class BikeShopInfoDTO implements Serializable {

    private String logo;
    private double range;
    private double longitude;
    private double latitude;
    private int closeHour;
    private String city;
    private int openHour;
    private String clubId;
    private int level;
    private String address;
    private String description;
    private int shopId;
    private String name;
    private String province;
    private String clubName;
    private String clubLogo;
    private String district;
    private String telephone;
    private BikeShopTagInfoDto tagInfo;
    private String officeActivity;
    private List<String>pictures = null;
    private String ownerId;
    private int status;

    private String mainProducts = null;


    public BikeShopInfoDTO(JSONObject jsonObject) {
        this.logo = jsonObject.optString("logo");
        this.range = jsonObject.optDouble("range");
        JSONArray location = jsonObject.optJSONArray("location");
        if (location != null) {
            this.longitude = location.optDouble(0);
            this.latitude = location.optDouble(1);
        }
        this.closeHour = jsonObject.optInt("closeHour");
        this.city = jsonObject.optString("city");
        this.openHour = jsonObject.optInt("openHour");
        this.level = jsonObject.optInt("level");
        this.address = jsonObject.optString("address");
        this.description = jsonObject.optString("description");
        this.shopId = jsonObject.optInt("shopId");
        this.name = jsonObject.optString("name");
        this.province = jsonObject.optString("province");
        this.district = jsonObject.optString("district");
        this.telephone = jsonObject.optString("phone");
        this.officeActivity = jsonObject.optString("officeActivity");
        this.status = jsonObject.optInt("status");
        this.ownerId = jsonObject.optString("ownerId");

        this.mainProducts = jsonObject.optString("mainProducts");

        JSONObject clubInfo = jsonObject.optJSONObject("club");
        if(clubInfo != null){
            this.clubId = clubInfo.optString("objectId");
            this.clubName = clubInfo.optString("name");
            this.clubLogo = clubInfo.optString("logo");
        }

        JSONObject tagInfoObject = jsonObject.optJSONObject("tagInfo");
        if(!JSONUtil.isNull(tagInfoObject)){
            tagInfo = new BikeShopTagInfoDto(tagInfoObject);
        }

        JSONArray jsonArray = jsonObject.optJSONArray("shopPictures");

        if(jsonArray != null){
            pictures = new ArrayList<>();
            for(int i =0; i< jsonArray.length();i++){
                pictures.add(jsonArray.optString(i));
            }
        }
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
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

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getOpenHour() {
        return openHour;
    }

    public void setOpenHour(int openHour) {
        this.openHour = openHour;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCitygetProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public BikeShopTagInfoDto getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(BikeShopTagInfoDto tagInfo) {
        this.tagInfo = tagInfo;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getOfficeActivity() {
        return officeActivity;
    }

    public void setOfficeActivity(String officeActivity) {
        this.officeActivity = officeActivity;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
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

    public String getMainProducts() {
        return mainProducts;
    }

    public void setMainProducts(String mainProducts) {
        this.mainProducts = mainProducts;
    }
}
