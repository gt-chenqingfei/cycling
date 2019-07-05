package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.Wheelview;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by icedan on 15/12/9.
 */
public class ClubCalendarPopupWindow extends PopupWindow implements View.OnClickListener {

    private View menuView;
    private TextView cancelTv;
    private TextView sureTv;
    private TextView titleTv;
    private Wheelview yearWheelView;
    private Wheelview monthWheelView;
    private Wheelview.SelectFinishCalendarListener selectFinishListener;

    private String title;
    private ArrayList<String> yearList;
    private ArrayList<String> monthList;
    private int defaultYearIndex;
    private int defaultMonthIndex;
    private Activity activity;

    private int defaultStartYear = 2010;
    private int defaultEndYear = 2020;

    private int currentYearIndex;
    private int currentMonthIndex;

    public ClubCalendarPopupWindow(Context context, AttributeSet attribute) {
        super(context, attribute);
    }


    public ClubCalendarPopupWindow(Activity activity, Wheelview.SelectFinishCalendarListener listener) {
        super(activity);
        this.activity = activity;
        this.menuView = LayoutInflater.from(activity).inflate(R.layout.club_calendar_popup_window, null);
        this.selectFinishListener = listener;
        this.init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_calendar_popup_window_cancel_btn:
                dismiss();
                break;
            case R.id.club_calendar_popup_window_save_btn:
                if (null == selectFinishListener) {
                    return;
                }

                this.currentYearIndex = this.yearWheelView.getSelected();
                this.currentMonthIndex = this.monthWheelView.getSelected();

                selectFinishListener.endSelect(this.yearWheelView.getSelectedText(),
                        this.monthWheelView.getSelectedText());
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setBackgroundAlpha(1f);
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    private void init() {
        this.cancelTv = (TextView) this.menuView.findViewById(R.id.club_calendar_popup_window_cancel_btn);
        this.sureTv = (TextView) this.menuView.findViewById(R.id.club_calendar_popup_window_save_btn);
        this.titleTv = (TextView) this.menuView.findViewById(R.id.club_calendar_popup_window_title);
        this.yearWheelView = (Wheelview) this.menuView.findViewById(R.id.popup_window_wheel_year_view);
        this.monthWheelView = (Wheelview) this.menuView.findViewById(R.id.popup_window_wheel_month_view);
        this.cancelTv.setOnClickListener(this);
        this.sureTv.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        this.yearList = new ArrayList<>();
        int yearIndex = 0;
        for (int i = defaultStartYear; i <= defaultEndYear; i++) {
            if (i == year) {
                this.currentYearIndex = yearIndex;
            }
            this.yearList.add(String.valueOf(i));
            yearIndex++;
        }
        this.yearWheelView.setData(this.yearList);

        this.monthList = new ArrayList<>();
        int monthIndex = 0;
        for (int i = 1; i <= 12; i++) {
            if (i == month) {
                this.currentMonthIndex = monthIndex;
            }
            this.monthList.add(String.valueOf(i));
            monthIndex++;
        }
        this.monthWheelView.setData(this.monthList);

        this.yearWheelView.setDefault(this.currentYearIndex);
        this.monthWheelView.setDefault(this.currentMonthIndex);

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.menuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = menuView.findViewById(R.id.club_calendar_popup_window_view)
                        .getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        setBackgroundAlpha(0.5f);
    }

    public void setStartYear(int startYear) {
        this.defaultStartYear = startYear;
    }

    public void setEndYear(int endYear) {
        this.defaultEndYear = endYear;
    }

    public void setCurrentYear(int year) {
        int yearIndex = 0;
        for (int i = defaultStartYear; i <= this.defaultEndYear; i++) {
            if (year == i) {
                this.defaultYearIndex = yearIndex;
                this.yearWheelView.setDefault(yearIndex);
            }
            yearIndex++;
        }
    }

    public void setCurrentMonth(int month) {
        int monthIndex = 0;
        for (int i = 1; i <= 12; i++) {
            if (month == i) {
                this.defaultMonthIndex = monthIndex;
                this.monthWheelView.setDefault(monthIndex);
            }
            monthIndex++;
        }
    }
}
