package com.beastbikes.framework.android.utils;

import android.content.Context;

/**
 * Dimension utility
 *
 * @author johnson
 */
public class DimensionUtils {

    /**
     * Convert dip to pixel
     *
     * @param context The android context
     * @param dip     The dip value
     * @return the dimension in pixel
     */
    public static int dip2px(Context context, float dip) {
        final float m = context.getResources().getDisplayMetrics().density;
        return (int) (dip * m + 0.5f);
    }

    public static int getSizeInPixels(float dip, Context context) {
        final float m = context.getResources().getDisplayMetrics().density;
        return (int) (dip * m + 0.5f);
    }

    /**
     * Convert pixel to dip
     *
     * @param context The android context
     * @param px      The pixel value
     * @return the dimension in dip
     */
    public static int px2dip(Context context, float px) {
        final float m = context.getResources().getDisplayMetrics().density;
        return (int) (px / m + 0.5f);
    }

    private DimensionUtils() {
    }

}
