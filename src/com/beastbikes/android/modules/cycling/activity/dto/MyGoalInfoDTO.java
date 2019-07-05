package com.beastbikes.android.modules.cycling.activity.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by icedan on 16/1/7.
 */
public class MyGoalInfoDTO implements Serializable {

    private double curGoal;

    private double myGoal;

    private double monthAvgSpeed;

    private double monthTime;

    private int monthCount;

    public MyGoalInfoDTO(JSONObject json) {
        this.curGoal = json.optDouble("curGoal");
        this.myGoal = json.optDouble("myGoal");
        this.monthAvgSpeed = json.optDouble("monthAvgSpeed");
        this.monthTime = json.optDouble("monthTime");
        this.monthCount = json.optInt("monthCount");
    }

    public double getCurGoal() {
        return curGoal;
    }

    public void setCurGoal(double curGoal) {
        this.curGoal = curGoal;
    }

    public double getMyGoal() {
        return myGoal;
    }

    public void setMyGoal(double myGoal) {
        this.myGoal = myGoal;
    }

    public double getMonthAvgSpeed() {
        return monthAvgSpeed;
    }

    public void setMonthAvgSpeed(double monthAvgSpeed) {
        this.monthAvgSpeed = monthAvgSpeed;
    }

    public double getMonthTime() {
        return monthTime;
    }

    public void setMonthTime(double monthTime) {
        this.monthTime = monthTime;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    @Override
    public String toString() {
        return "MyGoalInfoDTO{" +
                "curGoal=" + curGoal +
                ", myGoal=" + myGoal +
                ", monthAvgSpeed=" + monthAvgSpeed +
                ", monthTime=" + monthTime +
                ", monthCount=" + monthCount +
                '}';
    }
}
