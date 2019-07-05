package com.beastbikes.android.locale.googlemaputils;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by caoxiao on 15/11/5.
 */
public interface GoogleMapConnectionCallBack {
    void onSuccessed(Location mLastLocation,Bundle bundle);//返回坐标
    void onFail(ConnectionResult connectionResult);
    void onSuspended(int i);
}
