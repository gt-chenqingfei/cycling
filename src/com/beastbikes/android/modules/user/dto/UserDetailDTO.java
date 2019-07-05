package com.beastbikes.android.modules.user.dto;

import android.text.TextUtils;

import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class UserDetailDTO implements Serializable {

    private static final long serialVersionUID = 6374588791743399277L;

    static final String TOTAL_CALORIES = "totalCalories";

    static final String TOTAL_DISTANCE = "totalDistance";

    static final String MONTHLY_DISTANCE = "monthlyDistance";

    static final String LONGEST_DISTANCE = "longestDistance";

    static final String LONGEST_DISTANCE_IDENTITY = "longestDistanceIdentity";

    static final String TOTAL_TIME = "totalTime";

    static final String AVERAGE_SPEED = "averageSpeed";

    static final String LATEST_CYCLING_TIME = "latestCyclingTime";

    static final String TOTAL_COUNT = "totalCount";

    private String userId;

    private double totalCalories;

    private double totalDistance;

    private double monthlyDistance;

    private double longestDistance;

    private String longestDistanceId;

    /**
     * 总骑行时间，以秒为单位
     */
    private long totalElapsedTime;

    private double averageSpeed;

    private long latestActivityTime;

    private int totalActivities;


    public UserDetailDTO(JSONObject json) {
        this.totalCalories = json.optDouble(TOTAL_CALORIES, 0);
        this.totalDistance = json.optDouble(TOTAL_DISTANCE, 0);
        this.monthlyDistance = json.optDouble(MONTHLY_DISTANCE, 0);
        this.longestDistance = json.optDouble(LONGEST_DISTANCE, 0);
        this.totalElapsedTime = json.optLong(TOTAL_TIME, 0);
        this.longestDistanceId = json.optString(LONGEST_DISTANCE_IDENTITY);
        this.averageSpeed = json.optDouble(AVERAGE_SPEED, 0);
        String lastCyclingTime = json.optString(LATEST_CYCLING_TIME);
        String cyclingTime = DateFormatUtil.dateFormat2String(new Date());
        String cycling = DateFormatUtil.dateFormat2String(System.currentTimeMillis());
        if (!TextUtils.isEmpty(lastCyclingTime)) {
            this.latestActivityTime = DateFormatUtil.timeFormat2Date(lastCyclingTime);
        }
        this.totalActivities = json.optInt(TOTAL_COUNT, 0);
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalCalories() {
        return this.totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getMonthlyDistance() {
        return this.monthlyDistance;
    }

    public void setMonthlyDistance(double monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public double getLongestDistance() {
        return this.longestDistance;
    }

    public void setLongestDistance(double longestDistance) {
        this.longestDistance = longestDistance;
    }

    public String getLongestDistanceIdentity() {
        return this.longestDistanceId;
    }

    public void setLongestDistanceIdentity(String longestDistanceIdentity) {
        this.longestDistanceId = longestDistanceIdentity;
    }

    public long getTotalElapsedTime() {
        return this.totalElapsedTime;
    }

    public void setTotalElapsedTime(long totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }

    public double getAverageSpeed() {
        return this.averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getLatestActivityTime() {
        return this.latestActivityTime;
    }

    public void setLatestActivityTime(long latestActivityTime) {
        this.latestActivityTime = latestActivityTime;
    }

    public int getTotalCount() {
        return this.totalActivities;
    }

    public void setTotalCount(int totalCount) {
        this.totalActivities = totalCount;
    }

}
