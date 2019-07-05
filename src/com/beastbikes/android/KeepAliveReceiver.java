package com.beastbikes.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityService;
import com.beastbikes.android.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveReceiver.class);

    private static final int EXTRA_TIME_DELAY = 60 * 1000;
    private long currentTimeMill = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utils.getCurrentTimeInMillis() - currentTimeMill < EXTRA_TIME_DELAY) {
            return;
        }

        currentTimeMill = Utils.getCurrentTimeInMillis();
        String activityId = ActivityManager.getCurrentActivityId(context);
        if (!TextUtils.isEmpty(activityId)) {
            String action = intent.getAction();
            logger.info("Action = " + action);
            Intent it = new Intent(ActivityService.ACTION_ACTIVITY_MANAGER);
            it.setPackage(context.getPackageName());
            context.startService(it);
        }
    }

}
