package com.beastbikes.android.modules.user.dto;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.activity.dao.entity.RemoteSample;
import com.beastbikes.android.modules.cycling.simplify.Point;

import org.json.JSONObject;

public class SampleDTO implements Point {

    // 百度坐标
    private double latitude0;
    private double longitude0;

    // GPS坐标
    private double latitude1;
    private double longitude1;

    // 海拔
    private double altitude;

    private long elapsedTime;

    private double distance;

    private double velocity;

    private double calorie;
    // 心率
    private double cardiacRate;
    // 踏频
    private double cadence;

    public SampleDTO(LocalActivitySample las) {
        this.latitude0 = Double.parseDouble(las.getLatitude0());
        this.longitude0 = Double.parseDouble(las.getLongitude0());
        this.latitude1 = Double.parseDouble(las.getLatitude1());
        this.longitude1 = Double.parseDouble(las.getLongitude1());
        this.altitude = Double.parseDouble(las.getAltitude());
        this.elapsedTime = las.getCurrTime();
        this.distance = las.getDistance();
        this.velocity = las.getVelocity();
        this.calorie = las.getCalorie();
        this.cardiacRate = las.getCardiacRate();
        this.cadence = las.getCadence();
    }

    public SampleDTO(JSONObject obj) {
        if (obj.has(RemoteSample.LATITUDE0)) {
            this.latitude0 = obj.optDouble(RemoteSample.LATITUDE0);
            this.longitude0 = obj.optDouble(RemoteSample.LONGITUDE0);
            this.latitude1 = obj.optDouble(RemoteSample.LATITUDE1);
            this.longitude1 = obj.optDouble(RemoteSample.LONGITUDE1);
            this.altitude = obj.optDouble(RemoteSample.ALTITUDE);
            this.elapsedTime = obj.optLong(RemoteSample.TIME);
            this.distance = obj.optDouble(RemoteSample.DISTANCE);
            this.velocity = obj.optDouble(RemoteSample.VELOCITY);
            this.calorie = obj.optDouble(RemoteSample.CALORIE);
            this.cardiacRate = obj.optDouble(RemoteSample.CARDIAC_RATE);
            this.cadence = obj.optDouble(RemoteSample.CADENCE);
        } else {
            this.latitude0 = obj.optDouble("1");
            this.longitude0 = obj.optDouble("2");
            this.latitude1 = obj.optDouble("3");
            this.longitude1 = obj.optDouble("4");
            this.altitude = obj.optDouble("5");
            this.elapsedTime = obj.optLong("6");
            this.distance = obj.optDouble("7");
            this.velocity = obj.optDouble("8");
            this.calorie = obj.optDouble("9");
            this.cardiacRate = obj.optDouble("10");
            this.cadence = obj.optDouble("11");
        }
    }

    public double getLatitude0() {
        return latitude0;
    }

    public void setLatitude0(double latitude0) {
        this.latitude0 = latitude0;
    }

    public double getLongitude0() {
        return longitude0;
    }

    public void setLongitude0(double longitude0) {
        this.longitude0 = longitude0;
    }

    public double getLatitude1() {
        return latitude1;
    }

    public void setLatitude1(double latitude1) {
        this.latitude1 = latitude1;
    }

    public double getLongitude1() {
        return longitude1;
    }

    public void setLongitude1(double longitude1) {
        this.longitude1 = longitude1;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getCardiacRate() {
        return cardiacRate;
    }

    public void setCardiacRate(double cardiacRate) {
        this.cardiacRate = cardiacRate;
    }

    public double getCadence() {
        return cadence;
    }

    public void setCadence(double cadence) {
        this.cadence = cadence;
    }

    @Override
    public String toString() {
        return "SampleDTO [latitude0=" + latitude0 + ", longitude0="
                + longitude0 + ", latitude1=" + latitude1 + ", longitude1="
                + longitude1 + ", altitude=" + altitude + ", elapsedTime="
                + elapsedTime + ", distance=" + distance + ", velocity="
                + velocity + ", calorie=" + calorie + ", cardiacRate="
                + cardiacRate + "]";
    }

    @Override
    public double getX() {
        return this.latitude1;
    }

    @Override
    public double getY() {
        return this.longitude1;
    }

}
