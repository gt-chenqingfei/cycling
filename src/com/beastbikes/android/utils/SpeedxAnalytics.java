package com.beastbikes.android.utils;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by chenqingfei on 16/6/22.
 */
public class SpeedxAnalytics {
    public static void onEvent(Context context, String label, String eventId) {
        if (!TextUtils.isEmpty(label)) {
            AVAnalytics.onEvent(context, label);
        }
        if (!TextUtils.isEmpty(eventId)) {
            MobclickAgent.onEvent(context, eventId);
        }
    }
}
