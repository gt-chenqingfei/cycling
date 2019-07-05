package com.beastbikes.android.authentication.ui;

/**
 * Created by zhangyao on 2016/2/19.
 */
public class GeoCodeMessage {
    private String geoCode ;

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public GeoCodeMessage(String geoCode){
        this.geoCode = geoCode;
    }
}
