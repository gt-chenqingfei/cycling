package com.beastbikes.android.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by icedan on 15/11/6.
 */
public class DateFormatUtil {

    /**
     * 时间戳转换为标准时间
     *
     * @param time
     * @return
     */
    public static String dateFormat2String(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(time);
    }

    /**
     * Date转换为标准时间
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Date转换为标准时间--月
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2StringMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Date转换为标准时间--日
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2StringDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Date转换为标准时间--小时:分钟
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2StringHM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Date转换为标准时间--月日
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2StringMonthDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }


    /**
     * long转换为时分秒
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2StringHMS(long date) {

        long h = 0, m = 0, s = 0;

        if (date > 0) {
            h = date / 3600;
            m = date % 3600 / 60;
            s = date % 3600 % 60;
        }

        return String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * Date转换为标准时间--月日
     *
     * @param date Date
     * @return String
     */
    public static String dateFormat2StringYearMonthDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        try {
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 标准时间转换为时间戳
     *
     * @param time 标准时间
     * @return long
     */
    public static long timeFormat2Date(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        try {
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.parse(time).getTime();
        } catch (Exception e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getDefault());
            try {
                return sdf.parse(time).getTime();
            } catch (Exception e1) {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                sdf.setTimeZone(TimeZone.getDefault());
                try {
                    return sdf.parse(time).getTime();
                } catch (ParseException e2) {
                    try {
                        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        sdf.setTimeZone(TimeZone.getDefault());
                        return sdf.parse(time).getTime();
                    } catch (Exception e3) {
                        e2.printStackTrace();
                    }
                }
            }
        }

        return 0;
    }

    public static long timeFormatOfString(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            return sdf.parse(time).getTime();
        } catch (Exception e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getDefault());
            try {
                return sdf.parse(time).getTime();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * 获取当月一号的日期
     *
     * @return String
     */
    public static String getFirstDayOfMonth() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        sdf.setTimeZone(TimeZone.getDefault());
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * 获取指定月一号的日期
     *
     * @param month month
     * @return String
     */
    public static String getFirstDayOfMonth(int month) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        sdf.setTimeZone(TimeZone.getDefault());
        Calendar lastDate = Calendar.getInstance();
        int currentMonth = lastDate.get(Calendar.MONTH) + 1;
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        if (month != currentMonth) {
            lastDate.add(Calendar.MONTH, month - currentMonth);// 减一个月，变为下月的1号
        }
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getFirstDayOfMonth(int year, int month) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(year));
        if (month < 10) {
            sb.append("-0");
        } else {
            sb.append("-");
        }
        sb.append(String.valueOf(month)).append("-01 00:00:00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            Date date = sdf.parse(sb.toString());
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取本月最后一天的时间
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(year));
        if (month < 10) {
            sb.append("-0");
        } else {
            sb.append("-");
        }
        sb.append(String.valueOf(month + 1)).append("-01 00:00:00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            Date date = sdf.parse(sb.toString());
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取年份
     *
     * @param time
     * @return
     */
    public static int getYearOfString(String time) {
        Date date = stringFormat2Date(time);
        Calendar calendar = Calendar.getInstance();
        if (null == date) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(date);
        }

        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取年份
     *
     * @param time
     * @return
     */
    public static int getMonthOfString(String time) {
        Date date = stringFormat2Date(time);
        Calendar calendar = Calendar.getInstance();
        if (null == date) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(date);
        }

        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据HeaderId获取年份
     *
     * @param headerId
     * @return
     */
    public static int getYearOfHeader(long headerId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(headerId));
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonthOfHeader(long headerId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(headerId));
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取时间（多久前）
     *
     * @param context
     * @param time
     * @return
     */
    public static CharSequence getRelativeTimeSpanString(Context context, String time) {
        long startTime = timeFormatOfString(time);
        long currentTime = System.currentTimeMillis();
        long diffTime = Math.abs(currentTime - startTime);
        if (diffTime < 5000 * 60) {
            return context.getString(R.string.feedback_activity_just_now);
        } else {
            return DateUtils
                    .getRelativeTimeSpanString(startTime);
        }
    }

    /**
     * 时间戳转换为标准时间 3.10 15:20-3.23 12:30
     * MM-dd HH:mm
     *
     * @param time
     * @return
     */
    public static String formatMDHM(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time);
    }

    /**
     * 时间戳转换为标准时间
     *
     * @param time
     * @return
     */
    public static String formatHms(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time);
    }

    /**
     * 时间戳转换为标准时间
     *
     * @param time
     * @return
     */
    public static String formatHMS(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time);
    }

    /**
     * 时间戳转换为标准时间
     *
     * @param time
     * @return
     */
    public static String formatYMDHm(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time);
    }

    /**
     * 根据feed规则返回显示在UI上的时间
     */
    public static String getRelativeTimeSpanString4FeedByTime(String time) {
        return getRelativeTimeSpanString4Feed(stringFormat2Date(time));
    }

    /**
     * 根据feed规则返回显示在UI上的时间
     *
     * @param createDate
     * @return
     */
    public static String getRelativeTimeSpanString4Feed(Date createDate) {
        if (createDate == null)
            return "";
        Context context = BeastBikes.getInstance().getApplicationContext();
        long createTime = createDate.getTime();
        long currentTime = System.currentTimeMillis();

        long intervalsTime = currentTime - createTime;
        if (intervalsTime < 300000) {//五分钟内
            return context.getResources().getText(R.string.justnow).toString();
        } else if (intervalsTime < 3600000) {//一小时内
            return (int) (intervalsTime / 1000 / 60) + "" + context.getResources().getText(R.string.minutesago);//xx分钟之前
        } else if (createTime > DateUtil.getTodayZeroTime()) {//0点后
            return (int) (intervalsTime / 1000 / 3600) + "" + context.getResources().getText(R.string.hours_ago);//小时前
        } else if (createTime > DateUtil.lastDayWholePointDate()) {//昨天
            return context.getResources().getText(R.string.yesterday).toString();
        } else {//更早之前
            return DateFormatUtil.dateFormat2StringMonthDay(createDate);//xx月xx日
        }
    }

    public static String getTime4Feed(Date time) {
//        TimeZone timeZone = TimeZone.getDefault();
//        int rawOffset = timeZone.getRawOffset(); //获取时差，返回值毫秒
        if (time == null) {
            return DateFormatUtil.dateFormat2StringYearMonthDay(new Date());
        }
        int rawOffset = 0;
        return DateFormatUtil.dateFormat2StringYearMonthDay(new Date(time.getTime() + rawOffset));//xx月xx日
    }

    public static Date getDate(String date) {
        if (TextUtils.isEmpty(date))
            return null;

        Date d = stringFormat2Date(date);

        if (date.contains("Z")) {
            TimeZone timeZone = TimeZone.getDefault();
            if (timeZone == null || d == null)
                return d;
            int rawOffset = timeZone.getRawOffset(); //获取时差，返回值毫秒
            return new Date(d.getTime() + rawOffset);
        }

        return d;
    }

    public static long getHeardId(String createTime) {
        if (TextUtils.isEmpty(createTime))
            return 0;
        Date date = stringFormat2Date(createTime);
        String time = dateFormat2String(date);
        return timeFormatOfString(time);
    }

    /**
     * 标准时间转换为Date
     *
     * @param time 标准时间
     * @return Date
     */
    public static Date stringFormat2Date(String time) {
        if (TextUtils.isEmpty(time)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        try {
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.parse(time);
        } catch (Exception e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf.parse(time);
            } catch (Exception e1) {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                try {
                    sdf.setTimeZone(TimeZone.getDefault());
                    return sdf.parse(time);
                } catch (ParseException e2) {
                    try {
                        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        return sdf.parse(time);
                    } catch (Exception e3) {
                        e2.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    public static String formatDate(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 计算日期
     *
     * @param context
     * @param time
     * @return
     */
    public static String getDay(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1) {
            int month = calendar.get(Calendar.MONTH) + 1;
            return String.format(context.getString(R.string.label_month), month) + day;
        }

        return String.valueOf(day);
    }

    /**
     * 获取星期
     *
     * @param context
     * @param time
     * @return
     */
    public static String getWeek(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        switch (week) {
            case 1:
                return context.getString(R.string.label_sunday);
            case 2:
                return context.getString(R.string.label_monday);
            case 3:
                return context.getString(R.string.label_tuesday);
            case 4:
                return context.getString(R.string.label_wednesday);
            case 5:
                return context.getString(R.string.label_thursday);
            case 6:
                return context.getString(R.string.label_friday);
            case 7:
                return context.getString(R.string.label_saturday);
            default:
                return context.getString(R.string.label_monday);
        }
    }

}
