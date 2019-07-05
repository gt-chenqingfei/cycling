package com.beastbikes.android.locale.googlemaputils;

import com.android.volley.VolleyError;

/**
 * Created by caoxiao on 15/11/14.
 */
public interface GoogleMapCnCallBack {
    void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean);
    void onGetGeoInfoError(VolleyError volleyError);
}
