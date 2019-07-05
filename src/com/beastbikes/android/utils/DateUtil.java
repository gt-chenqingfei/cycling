package com.beastbikes.android.utils;

import android.content.Context;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
                .getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int getDay(Date currentDate, Date endDate) {
        return (int) ((endDate.getTime() - currentDate.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取剩余时间
     *
     * @return
     */
    public static String getDate(Context context, Date currentDate, Date endDate) {
        long diff = endDate.getTime() - currentDate.getTime();
        if (diff < 0)
            return context.getString(R.string.task_info_activity_rest_timeout);

        String h = context.getString(R.string.task_info_hour);
        String m = context.getString(R.string.task_info_minute);
        String d = context.getString(R.string.task_info_day);
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
                * (1000 * 60 * 60))
                / (1000 * 60);
        String date = "";
        if (days == 0) {
            if (hours > 0) {
                date = hours + h + minutes + m;
            } else {
                date = minutes + m;
            }
        } else {
            date = days + d;
        }
        return context.getString(R.string.task_info_surplus) + date;
    }

    public static int getMinute(Date currentDate, Date endDate) {
        return (int) ((endDate.getTime() - currentDate.getTime()) / 1000 / 60 % 60);
    }

    private static final long INTERVAL_IN_MILLISECONDS = 30 * 1000;

    public static String getTimestampString(Date messageDate) {
        if (messageDate == null) return "";
        Locale curLocale = BeastBikes.getInstance().getResources().getConfiguration().locale;

        String languageCode = curLocale.getLanguage();

        boolean isChinese = languageCode.contains("zh");

        String format = null;

        long messageTime = messageDate.getTime();
        if (isSameDay(messageTime)) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(messageDate);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            format = "HH:mm";

            if (hour > 17) {
                if (isChinese) {
                    format = "晚上 hh:mm";
                }

            } else if (hour >= 0 && hour <= 6) {
                if (isChinese) {
                    format = "凌晨 hh:mm";
                }
            } else if (hour > 11 && hour <= 17) {
                if (isChinese) {
                    format = "下午 hh:mm";
                }

            } else {
                if (isChinese) {
                    format = "上午 hh:mm";
                }
            }
        } else if (isYesterday(messageTime)) {
            if (isChinese) {
                format = "昨天 HH:mm";
            } else {
                format = "MM-dd HH:mm";
            }

        } else {
            if (isChinese) {
                format = "M月d日 HH:mm";
            } else {
                format = "MM-dd HH:mm";
            }
        }

        if (isChinese) {
            return new SimpleDateFormat(format, Locale.CHINA).format(messageDate);
        } else {
            return new SimpleDateFormat(format, Locale.US).format(messageDate);
        }
    }

    public static boolean isCloseEnough(long time1, long time2) {
        // long time1 = date1.getTime();
        // long time2 = date2.getTime();
        long delta = time1 - time2;
        if (delta < 0) {
            delta = -delta;
        }
        return delta < INTERVAL_IN_MILLISECONDS;
    }

    private static boolean isSameDay(long inputTime) {

        TimeInfo tStartAndEndTime = getTodayStartAndEndTime();
        if (inputTime > tStartAndEndTime.getStartTime() && inputTime < tStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    private static boolean isYesterday(long inputTime) {
        TimeInfo yStartAndEndTime = getYesterdayStartAndEndTime();
        if (inputTime > yStartAndEndTime.getStartTime() && inputTime < yStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    public static Date StringToDate(String dateStr, String formatStr) {
        DateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param timeLength Millisecond
     * @return
     */
    public static String toTime(int timeLength) {
        timeLength /= 1000;
        int minute = timeLength / 60;
//	        int hour = 0;
        if (minute >= 60) {
//	            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        // return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * @param timeLength second
     * @return
     */
    public static String toTimeBySecond(int timeLength) {
//	      timeLength /= 1000;
        int minute = timeLength / 60;
	        int hour = 0;
        if (minute >= 60) {
	            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
//        return String.format("%02d:%02d", minute, second);
    }


    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -1);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -2);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -2);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    /**
     * endtime为今天
     *
     * @return
     */
    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
//	      calendar2.set(Calendar.HOUR_OF_DAY, 23);
//	      calendar2.set(Calendar.MINUTE, 59);
//	      calendar2.set(Calendar.SECOND, 59);
//	      calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, -1);
        calendar2.set(Calendar.DATE, 1);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        calendar2.roll(Calendar.DATE, -1);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

    //当天的零点毫秒
    public static long getTodayZeroTime() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.MILLISECOND, 0);
        long current = System.currentTimeMillis();//当前时间毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        return zero;
    }

    /**
     * 返回上一天的整点信息
     *
     * @param date
     * @return 2014-3-3 00:00:00
     */
    public static long lastDayWholePointDate() {

        long current = System.currentTimeMillis();//当前时间毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset() - 86400000;//今天零点零分零秒的毫秒数
        return zero;
    }
}
