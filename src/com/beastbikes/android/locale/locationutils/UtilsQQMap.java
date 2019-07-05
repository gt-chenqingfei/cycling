package com.beastbikes.android.locale.locationutils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beastbikes.android.locale.LocaleManager;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

/**
 * Created by caoxiao on 15/11/7.
 */
public class UtilsQQMap {
    private final static String COUNTRYNAME = "中国";
    private final static String PROVINCENAMEAOMEN = "澳门特别行政区";
    private final static String PROVINCENAMETAIWAN = "台湾省";
//    private static WeakReference<Context> mContextReference;

    private final static Logger logger = LoggerFactory.getLogger(UtilsQQMap.class);

    public static void checkChineseVersion(Context context, Location location, UtilsQQMapCallBack utilsQQMapCallBack) {
        checkChineseVersion(context, location.getLatitude(), location.getLongitude(), utilsQQMapCallBack);
    }

    public static void checkChineseVersion(RequestQueue mQueue, Location location, UtilsQQMapCallBack utilsQQMapCallBack) {
        checkChineseVersion(mQueue, location.getLatitude(), location.getLongitude(), utilsQQMapCallBack);
    }

    public static void checkChineseVersion(Context context, double latitude, double longitude, UtilsQQMapCallBack utilsQQMapCallBack) {
        if (context == null)
            return;
        WeakReference<Context> mContextReference = new WeakReference<Context>(context);
        RequestQueue mQueue = Volley.newRequestQueue(mContextReference.get());
        checkChineseVersion(mQueue, latitude, longitude, utilsQQMapCallBack);
    }

    public static void checkChineseVersion(RequestQueue mQueue, double latitude, double longitude, UtilsQQMapCallBack utilsQQMapCallBack) {
        if (mQueue == null)
            return;
        final WeakReference<RequestQueue> weakReferenceRequestQueue = new WeakReference<RequestQueue>(mQueue);
        final WeakReference<UtilsQQMapCallBack> mUtilsQQMapCallBack = new WeakReference<UtilsQQMapCallBack>(utilsQQMapCallBack);
        String url = "http://apis.map.qq.com/ws/geocoder/v1/?location="
                + latitude + ","
                + longitude
                + "&key=UJXBZ-EARR3-ZZ63I-3CWGZ-IOGY7-4KFG5&get_poi=1";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        if (response.optInt("status") == 0) {
                            logger.error("checkChineseVersion query ok");
                            JSONObject result = response.optJSONObject("result");
                            if (result != null) {
                                JSONObject address_component = result.optJSONObject("address_component");
                                if (address_component != null) {
                                    String nation = address_component.optString("nation");
                                    String province = address_component.optString("province");
                                    if (nation.equals("中国")) {
                                        if (!province.equals("澳门特别行政区") && !province.equals("台湾省")) {
                                            if (mUtilsQQMapCallBack.get() != null) {
                                                mUtilsQQMapCallBack.get().isChineseVersion(true);
                                                LocaleManager.locationIsChinese = true;
                                            }
                                            Log.e("UtilsQQMap", "isChineseVersion true");
                                            logger.info("isChineseVersion true");
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        logger.error("can't checkChineseVersion by mapapi");
                        Log.e("UtilsQQMap", "can't checkChineseVersion by mapapi");
                        if (mUtilsQQMapCallBack != null) {
                            mUtilsQQMapCallBack.get().isChineseVersion(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UtilsQQMap", error.getMessage(), error);
                logger.error("checkChineseVersion query onErrorResponse");
                if (mUtilsQQMapCallBack != null) {
                    mUtilsQQMapCallBack.get().isChineseVersion(true);
                }
            }
        });
        if (weakReferenceRequestQueue.get() != null)
            weakReferenceRequestQueue.get().add(jsonObjectRequest);
    }

}
