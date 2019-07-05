package com.beastbikes.android.modules.user.dto;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.activity.dao.entity.RemoteSample;

public class LatlngDTO implements Serializable {
    private static final long serialVersionUID = -39976637880634666L;

    /**
     * 第一次修改，Android old index， 从0开始
     * <p/>
     * 第二次修改，兼容老的 Samples Index， 从1开始
     */
    private static final String LATITUDE0 = "1";
    private static final String LONGITUDE0 = "2";
    private static final String LATITUDE1 = "3";
    private static final String LONGITUDE1 = "4";

    private double latitude0;
    private double longitude0;
    private double latitude1;
    private double longitude1;

    private double distance;
    private double velocity;

    public LatlngDTO() {

    }

    public LatlngDTO(JSONObject obj) {
        try {
            if (obj.has(RemoteSample.LATITUDE0)) {
                this.latitude0 = obj.getDouble(RemoteSample.LATITUDE0);
                this.longitude0 = obj.getDouble(RemoteSample.LONGITUDE0);
                this.latitude1 = obj.getDouble(RemoteSample.LATITUDE1);
                this.longitude1 = obj.getDouble(RemoteSample.LONGITUDE1);
            } else {
                this.latitude0 = obj.getDouble(LATITUDE0);
                this.longitude0 = obj.getDouble(LONGITUDE0);
                this.latitude1 = obj.getDouble(LATITUDE1);
                this.longitude1 = obj.getDouble(LONGITUDE1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LatlngDTO(LocalActivitySample las) {
        try {
            this.latitude0 = Double.parseDouble(las.getLatitude0());
        } catch (NumberFormatException e) {
        }

        try {
            this.longitude0 = Double.parseDouble(las.getLongitude0());
        } catch (NumberFormatException e) {
        }

        try {
            this.latitude1 = Double.parseDouble(las.getLatitude1());
        } catch (NumberFormatException e) {
        }

        try {
            this.longitude1 = Double.parseDouble(las.getLongitude1());
        } catch (NumberFormatException e) {
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

}
