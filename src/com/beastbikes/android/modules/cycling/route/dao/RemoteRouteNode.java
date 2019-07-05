package com.beastbikes.android.modules.cycling.route.dao;

import java.io.Serializable;

public class RemoteRouteNode implements Serializable {

    private static final long serialVersionUID = 4261487066341451510L;

    /**
     * 路线关键点序号
     */
    public static final String KEY_NODE = "keyNode";
    /**
     * 路线关键点名称
     */
    public static final String NAME = "name";
    /**
     * 路线坐标系
     */
    public static final String COORDINATE = "coordinate";
    /**
     * 纬度坐标
     */
    public static final String LATITUDE = "latitude";
    /**
     * 经度坐标
     */
    public static final String LONGITUDE = "longitude";
    /**
     * 排序
     */
    public static final String ORDINAL = "ordinal";
    /**
     * 路线Id
     */
    public static final String ROUTE_ID = "routeId";
    /**
     * 海拔
     */
    public static final String ALTITUDE = "altitude";
    /**
     * 路线
     */
    public static final String ROUTE = "route";

}
