package com.beastbikes.android.widget.materialdesign.progressbar;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by caoxiao on 16/1/13.
 */
public class ThemeUtils {

    private ThemeUtils() {
    }

    public static int getColorFromAttrRes(int attr, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static float getFloatFromAttrRes(int attrRes, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getFloat(0, 0);
        } finally {
            a.recycle();
        }
    }
}