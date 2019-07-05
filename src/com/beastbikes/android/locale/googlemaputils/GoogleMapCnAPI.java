package com.beastbikes.android.locale.googlemaputils;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

/**
 * Created by caoxiao on 15/11/13.
 */
public class GoogleMapCnAPI {
    private Logger logger = LoggerFactory.getLogger(GoogleMapCnAPI.class);
    private final String geocodeAddress = "http://maps.google.cn/maps/api/geocode/json?latlng=";
    private GoogleMapCnCallBack mGoogleMapCnCallBack;

    public void geoCode(RequestQueue mQueue, double latitude, double longitude, GoogleMapCnCallBack googleMapCnCallBack) {
        RequestQueue requestQueue = new WeakReference<>(mQueue).get();
        if (googleMapCnCallBack != null)
            mGoogleMapCnCallBack = new WeakReference<>(googleMapCnCallBack).get();
        String url = geocodeAddress + latitude + "," + longitude;
//        logger.trace("geocodeAddress url:" + geocodeAddress);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
//                        logger.trace("jsonObject:" + jsonObject.toString());
                        if (jsonObject.optString("status").equals("OK")) {
                            JSONArray results = jsonObject.optJSONArray("results");
                            if (results != null && results.length() > 0) {
                                JSONObject address = results.optJSONObject(0);
                                String formatted_address = address.optString("formatted_address");
                                JSONArray address_components = address.optJSONArray("address_components");
                                returnAddress(address_components, formatted_address);//根据规则返回地址
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                logger.trace("geocodeAddress onErrorResponse:" + volleyError.getMessage());
                if (mGoogleMapCnCallBack != null)
                    mGoogleMapCnCallBack.onGetGeoInfoError(volleyError);
            }
        });
        if (requestQueue != null)
            requestQueue.add(jsonObjectRequest);
    }

    //三级城市信息顺序
    private String[] checkList = {"administrative_area_level_1", "administrative_area_level_2"
            , "administrative_area_level_3", "sublocality_level_1", "sublocality_level_2", "sublocality_level_3", "locality"};
    private String[] checkList2 = {"locality", "sublocality_level_1", "administrative_area_level_2", "administrative_area_level_3", "administrative_area_level_1", "sublocality_level_2"};

    private static int limit = 3;//限制显示的层级数量
    private static boolean hasSublocality_level = false;
    private ArrayMap<String, String> arrayMap;

    private void returnAddress(JSONArray jsonArray, String formatted_address) {
        if (jsonArray == null)
            return;
        String cityName = "";
        String province = "";
        arrayMap = new ArrayMap<>();
        int returnLimit = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checkList.length; i++) {
            String type1 = checkList[i];
            for (int j = 0; j < jsonArray.length(); j++) {
                if (returnLimit == 3)
                    break;
                JSONObject address_component = jsonArray.optJSONObject(j);
                JSONArray types = address_component.optJSONArray("types");
                String type2 = types.optString(0);
                if (!TextUtils.isEmpty(type2)) {
                    if (type2.equals("locality")) {
                        cityName = address_component.optString("long_name");
                    }
                    if (type2.equals("administrative_area_level_1")) {
                        province = address_component.optString("long_name");
                    }
                    if (type2.equals(checkList[3]) || type2.equals(checkList[4]) || type2.equals(checkList[5])) {//如果存在hasSublocality_level，那么不需要locality
                        hasSublocality_level = true;
                    }
                    if (hasSublocality_level && type2.equals(checkList[6]))
                        continue;
                    if (type2.equals(type1)) {
                        String long_name = address_component.optString("long_name");
                        arrayMap.put(type2, long_name);
//                        logger.trace(type2 + "," + long_name);
                        sb.append(long_name + ",");
                        returnLimit++;
                    }
                }
            }
        }
        if (sb.length() - 1 >= 0)
            sb.deleteCharAt(sb.length() - 1);
        GoogleMapCnBean googleMapCnBean = new GoogleMapCnBean(sb.toString(), formatted_address);
        googleMapCnBean.setCityName(cityName);
        googleMapCnBean.setProvince(province);

        checkCityName(googleMapCnBean, jsonArray);
    }

    private void checkCityName(GoogleMapCnBean googleMapCnBean, JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = checkList2.length - 1; i >= 0; i--) {
                String type1 = checkList2[i];
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject address_component = jsonArray.optJSONObject(j);
                    JSONArray types = address_component.optJSONArray("types");
                    String type2 = types.optString(0);
                    if (type1.equals(type2)) {
                        String long_name = address_component.optString("long_name");
                        googleMapCnBean.setCityName(long_name);
                    }
                }
            }
        }
        if (mGoogleMapCnCallBack != null) {
            mGoogleMapCnCallBack.onGetGeoCodeInfo(googleMapCnBean);
        }
    }

}


