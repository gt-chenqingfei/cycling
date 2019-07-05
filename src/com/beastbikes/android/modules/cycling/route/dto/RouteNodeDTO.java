package com.beastbikes.android.modules.cycling.route.dto;

import java.io.Serializable;

import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class RouteNodeDTO implements Serializable {
    private static final long serialVersionUID = -4343512128919138872L;

    private String id;
    // 坐标系
    private String coordinate;
    // 关键点序号
    private long keyNode;
    // 海拔
    private double altitude;
    // 纬度
    private double latitude;
    // 经度
    private double longitude;
    // 路线节点名称
    private String name;
    // 路线节点序号
    private long ordinal;
    // 路线ID
    private String routeId;

    public RouteNodeDTO() {

    }

    public RouteNodeDTO(JSONObject json) {
        this.id = json.optString("id");
        this.coordinate = json.optString("coordinate");
        this.keyNode = json.optInt("keyNode");
        this.altitude = json.optDouble("altitude");
        this.latitude = json.optDouble("latitude");
        this.longitude = json.optDouble("longitude");
        if (!coordinate.equals("bd09ll")) {
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordType.COMMON);
            // sourceLatLng待转换坐标
            converter.coord(new LatLng(39.9962112, 116.4743645));
            LatLng bdLatlng = converter.convert();
            this.latitude = bdLatlng.latitude;
            this.longitude = bdLatlng.longitude;
        }

        this.name = json.optString("name");
        this.ordinal = json.optInt("ordinal");
        this.routeId = json.optString("routeId");
    }

    public RouteNodeDTO(boolean newApi, JSONObject json) {
        this.coordinate = json.optString("coordinate");
        this.keyNode = json.optInt("keyNode");
        this.altitude = json.optDouble("altitude");
        this.latitude = json.optDouble("lat");
        this.longitude = json.optDouble("lng");
        // coordinate ['bd09ll', 'gcj02', 'wgs84']
        if (coordinate.equals("wgs84")) {
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(new LatLng(latitude, longitude));
            LatLng bdLatlng = converter.convert();
            this.latitude = bdLatlng.latitude;
            this.longitude = bdLatlng.longitude;
        } else if (coordinate.equals("gcj02")) {
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordType.COMMON);
            // sourceLatLng待转换坐标
            converter.coord(new LatLng(latitude, longitude));
            LatLng bdLatlng = converter.convert();
            this.latitude = bdLatlng.latitude;
            this.longitude = bdLatlng.longitude;
        }
        this.name = json.optString("name");
        this.ordinal = json.optInt("ordinal");
        this.routeId = json.optString("routeId");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public long getKeyNode() {
        return keyNode;
    }

    public void setKeyNode(long keyNode) {
        this.keyNode = keyNode;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(long ordinal) {
        this.ordinal = ordinal;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        return "RouteNodeDTO [id=" + id + ", coordinate=" + coordinate
                + ", keyNode=" + keyNode + ", altitude=" + altitude
                + ", latitude=" + latitude + ", longitude=" + longitude
                + ", name=" + name + ", ordinal=" + ordinal + ", routeId="
                + routeId + "]";
    }

}
