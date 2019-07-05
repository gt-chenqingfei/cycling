package com.beastbikes.android.modules.cycling.grid.dto;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.grid.dao.entity.Grid;
import com.beastbikes.android.utils.DateFormatUtil;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by icedan on 15/12/21.
 */
public class GridDTO implements Serializable {

    private int gridId;

    private int count;

    private String userId;

    private String unlockAt;

    private LatLng latLng1;

    private LatLng latLng2;

    private LatLng latLng3;

    private LatLng latLng4;

    private ArrayList<LatLng> polygons;

    public GridDTO(JSONObject json, String userId) {
        this.gridId = json.optInt("gridId");
        this.count = json.optInt("count");
        this.unlockAt = json.optString("unlockAt");
        this.userId = userId;
        this.initLatLng();
    }

    public GridDTO(Grid grid) {
        this.gridId = Integer.valueOf(grid.getId());
        this.count = grid.getCount();
        this.unlockAt = grid.getUnlockAt();
        this.userId = grid.getUserId();
        this.initLatLng();
    }

    public GridDTO(double latitude, double longitude) {
        this.gridId = (int) (Math.floor((longitude + 180) * 100.0) +
                    Math.floor((latitude + 90) * 100.0) * 36000);
        this.count = 1;
        this.unlockAt = DateFormatUtil.dateFormat2String(new Date());
        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            this.userId = user.getObjectId();
        }

        this.initLatLng();
    }

    private void initLatLng() {
        if (this.gridId != 0) {
            double longitude = ((gridId % 36000) / 100.0 - 180);
            double latitude = ((gridId / 36000) / 100.0 - 90);
            String lat = String.format("%.6f", latitude);
            String lng = String.format("%.6f", longitude);
            latitude = Double.valueOf(lat);
            longitude = Double.valueOf(lng);
            this.polygons = new ArrayList<>();

            this.latLng1 = new LatLng(latitude, longitude);
            this.latLng2 = new LatLng(Double.valueOf(String.format("%.6f",  latitude + 0.01)), longitude);
            this.latLng3 = new LatLng(Double.valueOf(String.format("%.6f",  latitude + 0.01)),
                    Double.valueOf(String.format("%.6f",  longitude + 0.01)));
            this.latLng4 = new LatLng(latitude, Double.valueOf(String.format("%.6f",  longitude + 0.01)));
            this.polygons.add(this.latLng1);
            this.polygons.add(this.latLng2);
            this.polygons.add(this.latLng3);
            this.polygons.add(this.latLng4);
            this.polygons.add(this.latLng1);
        }
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public String getUnlockAt() {
        return unlockAt;
    }

    public void setUnlockAt(String unlockAt) {
        this.unlockAt = unlockAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LatLng getLatLng1() {
        return latLng1;
    }

    public void setLatLng1(LatLng latLng1) {
        this.latLng1 = latLng1;
    }

    public LatLng getLatLng2() {
        return latLng2;
    }

    public void setLatLng2(LatLng latLng2) {
        this.latLng2 = latLng2;
    }

    public LatLng getLatLng3() {
        return latLng3;
    }

    public void setLatLng3(LatLng latLng3) {
        this.latLng3 = latLng3;
    }

    public LatLng getLatLng4() {
        return latLng4;
    }

    public void setLatLng4(LatLng latLng4) {
        this.latLng4 = latLng4;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<LatLng> getPolygons() {
        return polygons;
    }

    public void setPolygons(ArrayList<LatLng> polygons) {
        this.polygons = polygons;
    }
}
