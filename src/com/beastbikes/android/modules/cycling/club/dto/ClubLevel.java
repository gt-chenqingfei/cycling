package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

/**
 * Created by chenqingfei on 15/12/19.
 */
public class ClubLevel {
    private int progress;
    private int progressMax;

    private String param1;
    private String param2;

    public ClubLevel() {
    }

    public ClubLevel(JSONObject result) {
        this.progress = result.optInt("progress");
        this.progressMax = result.optInt("maxProgress");
        this.param1 = result.optString("name");
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(int progressMax) {
        this.progressMax = progressMax;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }
}
