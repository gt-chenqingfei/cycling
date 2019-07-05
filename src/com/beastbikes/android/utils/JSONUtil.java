package com.beastbikes.android.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chenqingfei on 15/12/8.
 */
public class JSONUtil {

    public static boolean isNull(JSONObject obj)
    {
        return obj == null || obj.length()<=0;
    }

    public static boolean isNull(JSONArray array)
    {
        return array == null || array.length()<=0;
    }
}
