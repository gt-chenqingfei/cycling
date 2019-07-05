package com.beastbikes.android.locale.locationutils;

import android.location.Location;

/**
 * Created by caoxiao on 15/11/6.
 */
public interface UtilsLocationCallBack {
    void onLocationChanged(Location location);
    void onLocationFail();
}
