package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityInfo;
import com.beastbikes.android.utils.DateFormatUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangyao on 2016/3/17.
 */
public class PickerDialogManange implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String START_TIME_TAG = "startTimeTv";
    private static final String END_TIME_TAG = "endTimeTv";
    private static final String DEAD_LINE_TAG = "deadLineTv";

    private DatePickerDialog dpd;

    private String tag;

    private final TextView startTimeTv;
    private final TextView endTimeTv;
    private final TextView deadLineTv;
    private Activity context;
    private Calendar now = Calendar.getInstance();

    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private TimePickerDialog tpd;

    public PickerDialogManange(Activity context, TextView startTimeTv, TextView endTimeTv, TextView deadLineTv) {
        this.context = context;
        this.startTimeTv = startTimeTv;
        this.endTimeTv = endTimeTv;
        this.deadLineTv = deadLineTv;
        int year = now.get(Calendar.YEAR);
        int monthOfYear = now.get(Calendar.MONTH);
        int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
        dpd = new DatePickerDialog();
        dpd.setAccentColor(context.getResources().getColor(R.color.bg_theme_black_color));
        dpd.initialize(this, year, monthOfYear, dayOfMonth);
        dpd.setMinDate(now);
    }

    public void init(ClubActivityInfo info) {
        if (!TextUtils.isEmpty(info.getStartDate()) && !(info.getStartDate()).equals("0")) {
            startTimeTv.setText(stringToDate(info.getStartDate()));
            if (isCommit(info.getStartDate())) {
                startTimeTv.setTextColor(Color.BLACK);
            } else {
                startTimeTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
            }
        } else {
            startTimeTv.setText(stringToDate(new Date().getTime() + 3600000));
        }


        if (!TextUtils.isEmpty(info.getEndDate()) && !(info.getEndDate()).equals("0")) {
            endTimeTv.setText(stringToDate(info.getEndDate()));
            if (startTimeTv.getCurrentTextColor() !=
                    context.getResources().getColor(R.color.designcolor_c7)) {
                endTimeTv.setTextColor(Color.BLACK);
            } else {
                endTimeTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
            }
        } else {
            endTimeTv.setText(stringToDate(new Date().getTime() + 14400000));
        }


        if (!TextUtils.isEmpty(info.getApplyEndDate()) && !(info.getApplyEndDate()).equals("0")) {
            deadLineTv.setText(stringToDate(info.getApplyEndDate()));
            if (startTimeTv.getCurrentTextColor() !=
                    context.getResources().getColor(R.color.designcolor_c7)) {
                deadLineTv.setTextColor(Color.BLACK);
            } else {
                deadLineTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
            }
        } else {
            deadLineTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
            deadLineTv.setText(context.getString(R.string.club_act_time_str));
        }
    }

    public void showStartDatePicker() {
        if (!dpd.isAdded()) {
            dpd.show(context.getFragmentManager(), START_TIME_TAG);
        }
    }

    public void showEndDatePicker() {
        if (!dpd.isAdded()) {
            dpd.show(context.getFragmentManager(), END_TIME_TAG);
        }
    }

    public void showDeadLinePicker() {
//        DatePickerDialog dpd = new DatePickerDialog();
        if (dpd.isAdded()) {
            return;
        }
        dpd.setAccentColor(context.getResources().getColor(R.color.bg_theme_black_color));
        Calendar calendar = getStartCalendar();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        dpd.initialize(this, year, monthOfYear, dayOfMonth);
        dpd.setMinDate(now);
        dpd.setMaxDate(calendar);
        dpd.show(context.getFragmentManager(), DEAD_LINE_TAG);
    }

    private void initTimePicker() {
        int hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        tpd = TimePickerDialog.newInstance(this, hourOfDay, minute, 0, true);
        tpd.setAccentColor(context.getResources().getColor(R.color.bg_theme_black_color));
        if (isSameDay(now)) {
            tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);
        }
    }

    private void showStartTimePicker() {
        initTimePicker();
        tag = START_TIME_TAG;
        tpd.show(context.getFragmentManager(), START_TIME_TAG);
    }


    private void showEndTimePicker() {
        initTimePicker();
        tag = END_TIME_TAG;
        tpd.show(context.getFragmentManager(), END_TIME_TAG);

    }

    private void showDeadTimePicker() {
        int hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, hourOfDay, minute, 0, true);
        tpd.setAccentColor(context.getResources().getColor(R.color.bg_theme_black_color));
        if (isSameDay(now)) {
            tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);
        }
        Calendar calendar = getStartCalendar();
        if (isSameDay(calendar)) {
            tpd.setMaxTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
        }
        tag = DEAD_LINE_TAG;
        tpd.show(context.getFragmentManager(), DEAD_LINE_TAG);
    }

    private Calendar getStartCalendar() {
        Calendar calendar = Calendar.getInstance();
        String time = startTimeTv.getText().toString();
        if (!TextUtils.isEmpty(time)) {
            Date date = new Date();
            date.setTime(string2Date(time));
            calendar.setTime(date);
            return calendar;
        }

        return calendar;
    }

    //装换成一串日期
    private String getDate(int year, int monthOfYear,
                           int dayOfMonth, int hourOfDay, int minute) {
        String date = "";
        String monthOfYear_str = (monthOfYear + 1) + "";
        String dayOfMonth_str = dayOfMonth + "";
        String hourOfDay_str = hourOfDay + "";
        String minute_str = minute + "";
        if (monthOfYear < 10)
            monthOfYear_str = 0 + monthOfYear_str;
        if (dayOfMonth < 10)
            dayOfMonth_str = 0 + dayOfMonth_str;
        if (hourOfDay < 10)
            hourOfDay_str = 0 + hourOfDay_str;
        if (minute < 10)
            minute_str = 0 + minute_str;
        date = year + "-" + monthOfYear_str + "-" +
                dayOfMonth_str + " " + hourOfDay_str + ":" + minute_str;
        return date;
    }

    private boolean isCommit(String time) {
        long t = new Date().getTime() - DateFormatUtil.timeFormat2Date(time);
        return t < 0;
    }

    private long string2Date(String time) {
        if (TextUtils.isEmpty(time))
            return 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private String stringToDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        return sdf.format(date);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        switch (view.getTag()) {
            case START_TIME_TAG:
                showStartTimePicker();
                break;

            case END_TIME_TAG:
                showEndTimePicker();
                break;

            case DEAD_LINE_TAG:
                showDeadTimePicker();
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        switch (tag) {
            case START_TIME_TAG:
                startTimeTv.setTextColor(Color.BLACK);
                startTimeTv.setText(getDate(year, monthOfYear, dayOfMonth, hourOfDay, minute));
                long startTime = string2Date(getDate(year, monthOfYear, dayOfMonth, hourOfDay, minute));
                if (startTime > string2Date(endTimeTv.getText().toString())) {
                    endTimeTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
                } else {
                    endTimeTv.setTextColor(Color.BLACK);
                }
                if (!deadLineTv.getText().toString().equals(context.getString(R.string.club_act_time_str)) &&
                        startTime < string2Date(deadLineTv.getText().toString())) {
                    deadLineTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
                } else {
                    deadLineTv.setTextColor(Color.BLACK);
                }

                break;

            case END_TIME_TAG:
                endTimeTv.setTextColor(Color.BLACK);
                endTimeTv.setText(getDate(year, monthOfYear, dayOfMonth, hourOfDay, minute));
                long endTime = string2Date(getDate(year, monthOfYear, dayOfMonth, hourOfDay, minute));
                if (endTime < string2Date(startTimeTv.getText().toString())) {
                    startTimeTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
                    deadLineTv.setTextColor(context.getResources().getColor(R.color.designcolor_c7));
                } else {
                    startTimeTv.setTextColor(Color.BLACK);
                    deadLineTv.setTextColor(Color.BLACK);
                }
                break;

            case DEAD_LINE_TAG:
                deadLineTv.setTextColor(Color.BLACK);
                deadLineTv.setText(getDate(year, monthOfYear, dayOfMonth, hourOfDay, minute));
                break;
        }
    }

    public boolean isSameDay(Calendar calendar) {
        if (year == calendar.get(Calendar.YEAR) && monthOfYear == calendar.get(Calendar.MONTH)
                && dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else {
            return false;
        }
    }

    private String stringToDate(String str) {
        Long time = DateFormatUtil.timeFormat2Date(str);
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }


}