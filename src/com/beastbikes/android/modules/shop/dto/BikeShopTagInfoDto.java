package com.beastbikes.android.modules.shop.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chenqingfei on 16/6/16.
 */
public class BikeShopTagInfoDto implements Serializable {

    private boolean isFix;
    private boolean isCare;
    private boolean isSell;
    private boolean isAfterSell;
    private boolean isRent;
    private boolean isActivity;

    public BikeShopTagInfoDto(){

    }

    public BikeShopTagInfoDto(JSONObject jsonObject){
        if(jsonObject == null)
            return;
        isFix = jsonObject.optBoolean("isFix");
        isCare = jsonObject.optBoolean("isCare");
        isSell = jsonObject.optBoolean("isSell");
        isAfterSell = jsonObject.optBoolean("isAfterSell");
        isRent = jsonObject.optBoolean("isRent");
        isActivity = jsonObject.optBoolean("isActivity");
    }

    public boolean isFix() {
        return isFix;
    }

    public void setFix(boolean fix) {
        isFix = fix;
    }

    public boolean isCare() {
        return isCare;
    }

    public void setCare(boolean care) {
        isCare = care;
    }

    public boolean isSell() {
        return isSell;
    }

    public void setSell(boolean sell) {
        isSell = sell;
    }

    public boolean isAfterSell() {
        return isAfterSell;
    }

    public void setAfterSell(boolean afterSell) {
        isAfterSell = afterSell;
    }

    public boolean isRent() {
        return isRent;
    }

    public void setRent(boolean rent) {
        isRent = rent;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }

}
