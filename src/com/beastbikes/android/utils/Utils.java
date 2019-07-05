package com.beastbikes.android.utils;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenqingfei on 15/12/11.
 */
public class Utils {

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static long getCurrentTimeInMillis() {
        long minute = 0L;
        try {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            minute = c.getTimeInMillis();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return minute;
    }

    public static int numericFilter(String str) {
        if (TextUtils.isEmpty(str))
            return 0;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    public  static Calendar string2Calendar(int year, int monthOfYear, int dayOfMonth) {
        String str = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte)((value >> 8 * i));
        }
        return b;
    }

    /**
     * 将short转成byte[2] 小端传输高位在前
     * @param a
     * @return
     */
    public static byte[] short2Byte(short a){
        byte[] b = new byte[2];

        b[0] = (byte) (a >>> 8);
        b[1] = (byte) (a);

        return b;
    }

    public static byte[] int2Byte(int length) {
        byte[] result = new byte[4];
        result[3] = (byte) (length >> 24);
        result[2] = (byte) (length >> 16);
        result[1] = (byte) (length >> 8);
        result[0] = (byte) (length /*>> 0*/);
        return result;
    }

}
