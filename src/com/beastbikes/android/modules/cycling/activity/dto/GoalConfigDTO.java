package com.beastbikes.android.modules.cycling.activity.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by icedan on 16/1/7.
 */
public class GoalConfigDTO implements Serializable {

    private String title;

    private String subTitle;

    private double distance;

    private boolean checked;

    private String key;

    public GoalConfigDTO(JSONObject json) {
        this.title = json.optString("title");
        this.subTitle = json.optString("subtitle");
        this.distance = json.optDouble("distance");
        this.key = json.optString("key");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
