package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;


public class DensityUtil {
    // public static int dividerHeight = dip2px(2);

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(float dipValue, Context context) {
        float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue, Context context) {
        float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, Context context) {
        float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue, Context context) {
        float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public static int getWidth(Context context) {
        initScreen(context);
        if (screen != null) {
            return screen.widthPixels;
        }
        return 0;
    }

    public static int getHeight(Context context) {
        initScreen(context);
        if (screen != null) {
            return screen.heightPixels;
        }
        return 0;
    }

    private static void initScreen(Context context) {
        if (screen == null) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            screen = new Screen(dm.widthPixels, dm.heightPixels);
        }
    }


    public static final void setTextSize(TextView tv, int resId) {

    }

    public static int getStatusHeight(Context context) {
        if (STATUS_HEIGHT <= 0) {
            try {
                int resourceId = context.getResources().getIdentifier(
                        "status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    STATUS_HEIGHT = context.getResources()
                            .getDimensionPixelSize(resourceId);
                }
            } catch (Exception ex) {
            }

            // Class<?> c = null;
            // Object obj = null;
            // Field field = null;
            // int x = 0, sbar = 0;
            // try {
            // c = Class.forName("com.android.internal.R$dimen");
            // obj = c.newInstance();
            // field = c.getField("status_bar_height");
            // x = Integer.parseInt(field.get(obj).toString());
            // sbar = getResources().getDimensionPixelSize(x);
            // } catch (Exception e1) {
            // e1.printStackTrace();
            // }
            // int j = sbar;

        }
        return STATUS_HEIGHT;
    }

    private static Screen screen = null;
    private static int STATUS_HEIGHT = 0;

    public static class Screen {
        public int widthPixels;
        public int heightPixels;

        public Screen() {
        }

        public Screen(int widthPixels, int heightPixels) {
            this.widthPixels = widthPixels;
            this.heightPixels = heightPixels;
        }

        @Override
        public String toString() {
            return "(" + widthPixels + "," + heightPixels + ")";
        }

    }
}
