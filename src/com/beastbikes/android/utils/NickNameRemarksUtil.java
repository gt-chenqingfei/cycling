package com.beastbikes.android.utils;

import android.text.TextUtils;

/**
 * Created by caoxiao on 16/3/16.
 */
public class NickNameRemarksUtil {

    public static String disPlayName(String nickName, String remarks) {
        if (TextUtils.isEmpty(remarks)) {
            return nickName;
        }
        return remarks;
    }
}
