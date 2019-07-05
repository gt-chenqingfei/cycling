package com.beastbikes.android.modules.cycling.activity.dao.entity;

public class RemoteSample {

    static final int IDX_LATITUDE0 = 0;

    static final int IDX_LONGITUDE0 = IDX_LATITUDE0 + 1;

    static final int IDX_LATITUDE1 = IDX_LONGITUDE0 + 1;

    static final int IDX_LONGITUDE1 = IDX_LATITUDE1 + 1;

    static final int IDX_ALTITUDE = IDX_LONGITUDE1 + 1;

    static final int IDX_TIME = IDX_ALTITUDE + 1;

    static final int IDX_DISTANCE = IDX_TIME + 1;

    static final int IDX_VELOCITY = IDX_DISTANCE + 1;

    static final int IDX_CALORIE = IDX_VELOCITY + 1;

    static final int IDX_CARDIAC_RATE = IDX_CALORIE + 1;

    static final int IDX_CADENCE = IDX_CARDIAC_RATE + 1;

    public static final String LATITUDE0 = String.valueOf(IDX_LATITUDE0);

    public static final String LONGITUDE0 = String.valueOf(IDX_LONGITUDE0);

    public static final String LATITUDE1 = String.valueOf(IDX_LATITUDE1);

    public static final String LONGITUDE1 = String.valueOf(IDX_LONGITUDE1);

    public static final String ALTITUDE = String.valueOf(IDX_ALTITUDE);

    public static final String TIME = String.valueOf(IDX_TIME);

    public static final String DISTANCE = String.valueOf(IDX_DISTANCE);

    public static final String VELOCITY = String.valueOf(IDX_VELOCITY);

    public static final String CALORIE = String.valueOf(IDX_CALORIE);

    public static final String CARDIAC_RATE = String.valueOf(IDX_CARDIAC_RATE);

    public static final String CADENCE = String.valueOf(IDX_CADENCE);

}
