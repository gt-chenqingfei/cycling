package com.beastbikes.android.locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.beastbikes.android.Constants;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by caoxiao on 15/11/3.
 */
public class LocaleManager implements Constants {

    private static final Logger logger = LoggerFactory.getLogger(LocaleManager.class);
    public static boolean locationIsChinese = false;

    public static final String STRINGMILE = "mi";
    public static final String STRINGFEET = "ft";
    public static final String STRINGMPH = "mph";

    private static final String TIMEZONEBJ = "Asia/Shanghai";
    private static final String TIMEZONEHK = "Asia/Hong_Kong";
    private static final String TIMEZONECQ = "Asia/Chongqing";
    private static final String TIMEZONEHRB = "Asia/Harbin";
    private static final String TIMEZONEWLMQ = "Asia/Urumqi";

    /**
     * 国内版还是国外版
     * 通过时区
     */

    public static boolean isChineseTimeZone() {
        String timeZone = getTimeZome();
        if (TextUtils.isEmpty(timeZone) || timeZone.equals(TIMEZONEBJ) || timeZone.equals(TIMEZONEHK)
                || timeZone.equals(TIMEZONECQ) || timeZone.equals(TIMEZONEHRB) || timeZone.equals(TIMEZONEWLMQ)) {
            return true;
        }
        return false;
    }

    private static String getTimeZome() {
        String timeZoneID = TimeZone.getDefault().getID();
        return timeZoneID;
    }

    public static Map<String, Integer> getCountyCodeMap() {
        Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();
        Map<String, Integer> countryMap = new HashMap<String, Integer>();
        for (String region : regions) {
            countryMap.put(region.toUpperCase(), PhoneNumberUtil.getInstance().getCountryCodeForRegion(region));
        }
        return countryMap;
    }

    /**
     * 获取国家代码
     *
     * @return
     */
    public static int getCountryCode(Context context) {
        int countryCode = 86;
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryIso = manager.getNetworkCountryIso();
            if (TextUtils.isEmpty(countryIso)) {
                countryIso = manager.getSimCountryIso();
            }
            if (!TextUtils.isEmpty(countryIso)) {
                countryIso = countryIso.toUpperCase();
                Map<String, Integer> countryMap = getCountyCodeMap();
                if (countryMap != null && countryMap.containsKey(countryIso)) {
                    countryCode = countryMap.get(countryIso);
                }
            }
        }catch (Exception e){

        }

        return countryCode;
    }

    /**
     * 如果用户没有设置默认返回km
     * 默认第一次安装会根据时区设置 是km or mi
     *
     * @param context
     * @return
     * @see com.beastbikes.android.main.TutorialActivity
     */
    public static boolean isDisplayKM(Context context) {
        if (context == null)
            return true;

        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultSp != null) {
            return defaultSp.getInt(KM_OR_MI, DISPLAY_KM) == DISPLAY_KM;
        }

        return true;
    }

    /**
     * 系统语言是否为中文
     *
     * @return
     */
    public static boolean isChineseLanunage() {
        String language = Locale.getDefault().getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    private static final double KMTOMILE = 0.621371;
    private static final double MILETOKM = 1.609344;
    private static final double MTOFEET = 3.2808399;
    private static final double KPHTOMPH = 0.621371;
    private static final double CMTOINCH = 0.3937008;
    private static final double KGTOLB = 2.2046226;
    private static final double FEETTOCM = 30.48;
    private static final double INCHTOCM = 2.54;

    /**
     * 公里to英里
     */
    public static double kilometreToMile(double kilometre) {
        return kilometre * KMTOMILE;
    }

    /**
     * 英里to公里
     */
    public static double mileToKilometre(double mile) {
        return mile * MILETOKM;
    }

    /**
     * 米to英尺
     *
     * @param metre
     * @return
     */
    public static double metreToFeet(double metre) {
        return metre * MTOFEET;
    }

    /**
     * 公里每小时to英里每小时
     */
    public static double kphToMph(double kmPerHour) {
        return kmPerHour * KPHTOMPH;
    }

    /**
     * 厘米to英寸
     *
     * @param cm
     * @return
     */
    public static double cmToInch(double cm) {
        return cm * CMTOINCH;
    }

    public static int cmToInchInt(double cm) {
        return (Math.round((float) (cm * CMTOINCH)) % 12);
    }

    public static int cm2Feet(double cm) {
        return (int) (cm * CMTOINCH) / 12;
    }

    /**
     * 千克to磅
     *
     * @param kg
     * @return
     */
    public static double kgToLb(double kg) {
        return kg * KGTOLB;
    }

    /**
     * 磅to千克
     */
    public static double lbToKg(double lb) {
        return lb / KGTOLB;
    }

    /**
     * 英尺转厘米
     */
    public static double feetTOCM(double feet) {
        return feet * FEETTOCM;
    }

    /**
     * 英寸转厘米
     */
    public static double inchTOCM(double inch) {
        return inch * INCHTOCM;
    }

    public static ArrayList<Double> kilometreToMileList(ArrayList<Double> kilometre) {
        for (int i = 0; i < kilometre.size(); i++) {
            kilometre.set(i, kilometreToMile(kilometre.get(i)));
        }
        return kilometre;
    }

    public static ArrayList<Double> kphToMphList(ArrayList<Double> kph) {
        for (int i = 0; i < kph.size(); i++) {
            kph.set(i, kphToMph(kph.get(i)));
        }
        return kph;
    }

    public static ArrayList<Double> metreToFeet(ArrayList<Double> metreList) {
        for (int i = 0; i < metreList.size(); i++) {
            metreList.set(i, metreToFeet(metreList.get(i)));
        }
        return metreList;
    }

    public static class LocaleString {
        public static final String activity_param_label_velocity = "(km/h)";
        public static final String activity_param_label_velocity_mph = "(mph/h)";
        public static final String profile_fragment_statistic_item_total_distance = "(km)";
        public static final String profile_fragment_statistic_item_total_distance_mi = "(mi)";
        public static final String profile_fragment_statistic_item_altitude_m = "(m)";
        public static final String profile_fragment_statistic_item_altitude_feet = "(feet)";
        public static final String activity_param_label_distance_unit = "mi";
        public static final String activity_max_speed_unit = "mph";
        public static final String activity_rise_total_unit = "ft";
        public static final String meter = "m";
        public static final String feet = "feet";

        public static final String activity_speed_unit = "km/h";
        public static final String activity_speed_unit_mph = "mph/h";
    }
}
