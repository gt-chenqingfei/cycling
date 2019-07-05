package com.beastbikes.android.ble.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by icedan on 16/6/30.
 */
public class BleCyclingDTO implements Serializable {

    private double totalDistance;
    private long totalTime;
    private int totalCount;
    private double totalAvgSpeed;

    public BleCyclingDTO() {

    }

    public BleCyclingDTO(JSONObject json) {
        this.totalDistance = json.optDouble("total_distance");
        this.totalAvgSpeed = json.optDouble("total_avgspeed");
        this.totalTime = json.optLong("total_time");
        this.totalCount = json.optInt("total_count");
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getTotalAvgSpeed() {
        return totalAvgSpeed;
    }

    public void setTotalAvgSpeed(double totalAvgSpeed) {
        this.totalAvgSpeed = totalAvgSpeed;
    }
}
