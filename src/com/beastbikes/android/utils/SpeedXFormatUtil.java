package com.beastbikes.android.utils;

import java.math.BigDecimal;

/**
 * Created by caoxiao on 16/5/4.
 */
public class SpeedXFormatUtil {

    /**
     * 里程数十以内保留一位小数，十以上不保留
     */
    public static String BigDecimalOne(double number) {
        String numberStr = "";
        if (number < 10) {
            BigDecimal bd = new BigDecimal(number);
            bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
            numberStr = bd + "";
        } else {
            numberStr = numberStr + (int) number;
        }
        return numberStr;
    }
}
