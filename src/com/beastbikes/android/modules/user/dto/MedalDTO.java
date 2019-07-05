package com.beastbikes.android.modules.user.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by icedan on 16/6/27.
 */
public class MedalDTO implements Serializable {

    private int id;
    private String name;
    private String lightUrl;
    private String lightText;
    private String detail;
    private String unLightUrl;

//    'giftType': 0, # 奖品类型 0: '实物',1: '虚拟',2: '流量',3: '抽奖'
//            'giftSituation': 0, # 领奖状态 0 不可领奖, 1 可领奖, 2 已抽奖(完成抽奖但未填写详细信息领取奖励) , 3 已领奖
    private int giftType;
    private int giftSituation;

    //  # 勋章状态 -2 失败, -1 未参与活动, 0 活动进行中, 1 可点亮, 2 已点亮, 3 已领奖
    private int status;
    private int rank;
    private int giftId;
    private int totalLight;
    private GiftDTO gift;
    //v2.5.0
    private String giftName;
    private int activityId;

    public MedalDTO(JSONObject result) {
        this.id = result.optInt("id");
        this.name = result.optString("name");
        this.lightUrl = result.optString("lightUrl");
        this.lightText = result.optString("lightText");
        this.detail = result.optString("detail");
        this.unLightUrl = result.optString("unLightUrl");

        this.giftType = result.optInt("giftType");
        this.giftSituation = result.optInt("giftSituation");

        this.status = result.optInt("status");
        this.rank = result.optInt("rank");
        this.giftId = result.optInt("giftId");
        this.totalLight = result.optInt("totalLight");
        //v2.5.0礼品名称
        this.giftName = result.optString("giftName");
        this.activityId = result.optInt("activityId");
        JSONObject giftJson = result.optJSONObject("gift");
        if (null != giftJson) {
            this.gift = new GiftDTO(giftJson);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLightUrl() {
        return lightUrl;
    }

    public void setLightUrl(String lightUrl) {
        this.lightUrl = lightUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLightText() {
        return lightText;
    }

    public void setLightText(String lightText) {
        this.lightText = lightText;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUnLightUrl() {
        return unLightUrl;
    }

    public void setUnLightUrl(String unLightUrl) {
        this.unLightUrl = unLightUrl;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public int getGiftSituation() {
        return giftSituation;
    }

    public void setGiftSituation(int giftSituation) {
        this.giftSituation = giftSituation;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getTotalLight() {
        return totalLight;
    }

    public void setTotalLight(int totalLight) {
        this.totalLight = totalLight;
    }

    public GiftDTO getGift() {
        return gift;
    }

    public void setGift(GiftDTO gift) {
        this.gift = gift;
    }

    @Override
    public String toString() {
        return "MedalDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lightUrl='" + lightUrl + '\'' +
                ", lightText='" + lightText + '\'' +
                ", detail='" + detail + '\'' +
                ", unLightUrl='" + unLightUrl + '\'' +
                ", giftType='" + giftType + '\'' +
                ", giftSituation='" + giftSituation + '\'' +
                ", status=" + status +
                ", rank=" + rank +
                ", giftId=" + giftId +
                ", totalLight=" + totalLight +
                ", gift=" + gift +
                '}';
    }
}
