package com.beastbikes.android.modules.user.dto;

import org.json.JSONObject;

import java.io.Serializable;

public class UserStatisticDTO implements Serializable {

    private static final long serialVersionUID = 449971452370541428L;

    private double weeklyDistance;
    private long weeklyElapsedTime;
    private double weeklyCalories;
    private double weeklyRisenDistance;
    private int weeklyActivityCount;

    private double monthlyDistance;
    private long monthlyElapsedTime;
    private double monthlyCalories;
    private double monthlyRisenDistance;
    private int monthlyActivityCount;

    private double yearlyDistance;
    private long yearlyElapsedTime;
    private double yearlyCalories;
    private double yearlyRisenDistance;
    private int yearlyActivityCount;

    private double totalAvgVelocity;
    private double totalMaxVelocity;
    private double totalMaxCalories;
    private double totalMaxDistance;
    private long totalMaxElapsedTime;

    private double totalCalories;
    private double totalDistance;
    private int totalCount;
    private long totalTime;

    public UserStatisticDTO() {
    }

    public UserStatisticDTO(JSONObject json) {
        this.weeklyDistance = json.optDouble("weekly_total_distance", 0);
        this.weeklyElapsedTime = json.optLong("weekly_total_time", 0);
        this.weeklyCalories = json.optDouble("weekly_total_heat", 0);
        this.weeklyRisenDistance = json.optDouble("weekly_total_rise", 0);
        this.weeklyActivityCount = json.optInt("weekly_total_count", 0);
        this.monthlyDistance = json.optDouble("monthly_total_distance", 0);
        this.monthlyElapsedTime = json.optLong("monthly_total_time", 0);
        this.monthlyCalories = json.optDouble("monthly_total_heat", 0);
        this.monthlyRisenDistance = json.optDouble("monthly_total_rise", 0);
        this.monthlyActivityCount = json.optInt("monthly_total_count", 0);
        this.yearlyDistance = json.optDouble("yearly_total_distance", 0);
        this.yearlyElapsedTime = json.optLong("yearly_total_time", 0);
        this.yearlyCalories = json.optDouble("yearly_total_heat", 0);
        this.yearlyRisenDistance = json.optDouble("yearly_total_rise", 0);
        this.yearlyActivityCount = json.optInt("yearly_total_count", 0);
        this.totalAvgVelocity = json.optDouble("total_avg_speed", 0);
        this.totalMaxVelocity = json.optDouble("total_speedMax_max", 0);
        this.totalMaxCalories = json.optDouble("total_heat_max", 0);
        this.totalMaxDistance = json.optDouble("total_distance_max", 0);
        this.totalMaxElapsedTime = json.optLong("total_time_max", 0);
        this.totalCount = json.optInt("total_count");
        this.totalCalories = json.optDouble("total_heat");
        this.totalTime = json.optLong("total_time");
        this.totalDistance =json.optDouble("total_distance");
    }

    public double getWeeklyDistance() {
        return this.weeklyDistance;
    }

    public void setWeeklyDistance(double weeklyDistance) {
        this.weeklyDistance = weeklyDistance;
    }

    public long getWeeklyElapsedTime() {
        return this.weeklyElapsedTime;
    }

    public void setWeeklyElapsedTime(long weeklyElapsedTime) {
        this.weeklyElapsedTime = weeklyElapsedTime;
    }

    public double getWeeklyCalories() {
        return this.weeklyCalories;
    }

    public void setWeeklyCalories(double weeklyCalories) {
        this.weeklyCalories = weeklyCalories;
    }

    public double getWeeklyRisenDistance() {
        return this.weeklyRisenDistance;
    }

    public void setWeeklyRisenDistance(double weeklyRisenDistance) {
        this.weeklyRisenDistance = weeklyRisenDistance;
    }

    public int getWeeklyActivityCount() {
        return this.weeklyActivityCount;
    }

    public void setWeeklyActivityCount(int weeklyActivityCount) {
        this.weeklyActivityCount = weeklyActivityCount;
    }

    public double getMonthlyDistance() {
        return this.monthlyDistance;
    }

    public void setMonthlyDistance(double monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public long getMonthlyElapsedTime() {
        return this.monthlyElapsedTime;
    }

    public void setMonthlyElapsedTime(long monthlyElapsedTime) {
        this.monthlyElapsedTime = monthlyElapsedTime;
    }

    public double getMonthlyCalories() {
        return this.monthlyCalories;
    }

    public void setMonthlyCalories(double monthlyCalories) {
        this.monthlyCalories = monthlyCalories;
    }

    public double getMonthlyRisenDistance() {
        return this.monthlyRisenDistance;
    }

    public void setMonthlyRisenDistance(double monthlyRisenDistance) {
        this.monthlyRisenDistance = monthlyRisenDistance;
    }

    public int getMonthlyActivityCount() {
        return this.monthlyActivityCount;
    }

    public void setMonthlyActivityCount(int monthlyActivityCount) {
        this.monthlyActivityCount = monthlyActivityCount;
    }

    public double getYearlyDistance() {
        return this.yearlyDistance;
    }

    public void setYearlyDistance(double yearlyDistance) {
        this.yearlyDistance = yearlyDistance;
    }

    public long getYearlyElapsedTime() {
        return this.yearlyElapsedTime;
    }

    public void setYearlyElapsedTime(long yearlyElapsedTime) {
        this.yearlyElapsedTime = yearlyElapsedTime;
    }

    public double getYearlyCalories() {
        return this.yearlyCalories;
    }

    public void setYearlyCalories(double yearlyCalories) {
        this.yearlyCalories = yearlyCalories;
    }

    public double getYearlyRisenDistance() {
        return this.yearlyRisenDistance;
    }

    public void setYearlyRisenDistance(double yearlyRisenDistance) {
        this.yearlyRisenDistance = yearlyRisenDistance;
    }

    public int getYearlyActivityCount() {
        return this.yearlyActivityCount;
    }

    public void setYearlyActivityCount(int yearlyActivityCount) {
        this.yearlyActivityCount = yearlyActivityCount;
    }

    public double getTotalAverageVelocity() {
        return this.totalAvgVelocity;
    }

    public void setTotalAverageVelocity(double totalAvgVelocity) {
        this.totalAvgVelocity = totalAvgVelocity;
    }

    public double getTotalMaxVelocity() {
        return this.totalMaxVelocity;
    }

    public void setTotalMaxVelocity(double totalMaxVelocity) {
        this.totalMaxVelocity = totalMaxVelocity;
    }

    public double getTotalMaxCalories() {
        return this.totalMaxCalories;
    }

    public void setTotalMaxCalories(double totalMaxCalories) {
        this.totalMaxCalories = totalMaxCalories;
    }

    public double getTotalMaxDistance() {
        return this.totalMaxDistance;
    }

    public void setTotalMaxDistance(double totalMaxDistance) {
        this.totalMaxDistance = totalMaxDistance;
    }

    public long getTotalMaxElapsedTime() {
        return this.totalMaxElapsedTime;
    }

    public void setTotalMaxElapsedTime(long totalMaxElapsedTime) {
        this.totalMaxElapsedTime = totalMaxElapsedTime;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
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


}
