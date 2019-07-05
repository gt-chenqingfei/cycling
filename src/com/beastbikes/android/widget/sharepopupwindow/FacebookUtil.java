package com.beastbikes.android.widget.sharepopupwindow;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by caoxiao on 16/5/18.
 */
public class FacebookUtil {

    public static Boolean checkFbInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean flag;
        try {
            pm.getPackageInfo("com.facebook.katana",
                    PackageManager.GET_ACTIVITIES);
            flag = true;
        } catch (PackageManager.NameNotFoundException e) {
            flag = false;
        }
        return flag;
    }
}
