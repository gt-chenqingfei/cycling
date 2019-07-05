package com.beastbikes.android.utils;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class Gps2GoogleUtil {

    private static final double PI = 3.14159265358979324;
    private static final double R = 6378245.0;
    private static final double EE = 0.00669342162296594323;
    private static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * 转化百度地图坐标系到gps坐标系
     */
    public static LatLng baiduMapLatToGpsLat(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(latLng);
        LatLng desLatLng = converter.convert();
        // x = 2*x1-x2，y = 2*y1-y2
        double gpsLat = 2 * latLng.latitude - desLatLng.latitude;
        double gpsLon = 2 * latLng.longitude - desLatLng.longitude;
        LatLng gpsLatLng = new LatLng(gpsLat, gpsLon);
        return gpsLatLng;
    }

    /**
     * 转化百度地图坐标系到gps坐标系
     */
    public static com.mapbox.mapboxsdk.geometry.LatLng baiduMapLatToGpsLat4MB(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(latLng);
        LatLng desLatLng = converter.convert();
        // x = 2*x1-x2，y = 2*y1-y2
        double gpsLat = 2 * latLng.latitude - desLatLng.latitude;
        double gpsLon = 2 * latLng.longitude - desLatLng.longitude;
        com.mapbox.mapboxsdk.geometry.LatLng gpsLatLng = new com.mapbox.mapboxsdk.geometry.LatLng(gpsLat, gpsLon);
        return gpsLatLng;
    }

    //标准转火星
    public static com.google.android.gms.maps.model.LatLng transform(double wgLat, double wgLon) {
        double mgLat;
        double mgLon;
        if (outOfChina(wgLat, wgLon)) {
            mgLat = wgLat;
            mgLon = wgLon;
            return new com.google.android.gms.maps.model.LatLng(wgLat, wgLon);
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((R * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (R / sqrtMagic * Math.cos(radLat) * PI);
        mgLat = wgLat + dLat;
        mgLon = wgLon + dLon;

        return new com.google.android.gms.maps.model.LatLng(mgLat, mgLon);
    }

    //标准转火星
    public static LatLng transform4Baidu(double wgLat, double wgLon) {
        double mgLat;
        double mgLon;
        if (outOfChina(wgLat, wgLon)) {
            mgLat = wgLat;
            mgLon = wgLon;
            return new LatLng(wgLat, wgLon);
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((R * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (R / sqrtMagic * Math.cos(radLat) * PI);
        mgLat = wgLat + dLat;
        mgLon = wgLon + dLon;

        return new LatLng(mgLat, mgLon);

    }

    static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0
                * PI)) * 2.0 / 3.0;
        return ret;
    }

    //百度转高德
    public static com.mapbox.mapboxsdk.geometry.LatLng BDDecrypt(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        Log.e("bd_lon", bd_lon + "");
        Log.e("bd_lat", bd_lat + "");
        Log.e("gg_lon", gg_lon + "");
        Log.e("gg_lat", gg_lat + "");
        return new com.mapbox.mapboxsdk.geometry.LatLng(gg_lat, gg_lon);
    }
}
