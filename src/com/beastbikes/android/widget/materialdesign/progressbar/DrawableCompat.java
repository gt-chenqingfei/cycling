package com.beastbikes.android.widget.materialdesign.progressbar;

import android.graphics.PorterDuff;

/**
 * Created by caoxiao on 16/1/13.
 */

public class DrawableCompat {

    /**
     * Parses a {@link android.graphics.PorterDuff.Mode} from a tintMode attribute's enum value.
     */
    public static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3: return PorterDuff.Mode.SRC_OVER;
            case 5: return PorterDuff.Mode.SRC_IN;
            case 9: return PorterDuff.Mode.SRC_ATOP;
            case 14: return PorterDuff.Mode.MULTIPLY;
            case 15: return PorterDuff.Mode.SCREEN;
            case 16: return PorterDuff.Mode.ADD;
            default: return defaultMode;
        }
    }
}
