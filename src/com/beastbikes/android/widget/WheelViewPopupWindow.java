package com.beastbikes.android.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.Wheelview;
import com.beastbikes.android.dialog.Wheelview.WheelSelectIndexListener;

import java.util.ArrayList;

/**
 * Created by icedan on 15/12/2.
 */
public class WheelViewPopupWindow extends PopupWindow implements OnClickListener {

    private View menuView;
    private TextView cancelTv;
    private TextView sureTv;
    private TextView titleTv;
    private Wheelview wheelView;
    private ViewGroup menuWindow;
    private WheelSelectIndexListener selectFinishListener;

    private String title;
    private ArrayList<String> list;
    private int defaultIndex;
    private Activity activity;

    public WheelViewPopupWindow(Context context, AttributeSet attribute) {
        super(context, attribute);
    }

    public WheelViewPopupWindow(Activity activity, ArrayList<String> list, int defaultIndex,
                                WheelSelectIndexListener selectFinishListener) {
        super(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        this.menuView = inflater.inflate(
                R.layout.speed_force_popup_window, null);
        this.selectFinishListener = selectFinishListener;
        this.list = list;
        this.defaultIndex = defaultIndex;
        this.activity = activity;
        this.initView();
    }

    public WheelViewPopupWindow(Activity activity, String title, ArrayList<String> list, int defaultIndex,
                                WheelSelectIndexListener selectFinishListener) {
        LayoutInflater inflater = activity.getLayoutInflater();
        this.activity = activity;
        this.menuView = inflater.inflate(
                R.layout.speed_force_popup_window, null);
        this.selectFinishListener = selectFinishListener;
        this.title = title;
        this.list = list;
        this.defaultIndex = defaultIndex;
        this.initView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_window_cancel_btn:
                dismiss();
                break;
            case R.id.popup_window_save_btn:
                if (null == selectFinishListener) {
                    return;
                }

                selectFinishListener.endSelect(this.wheelView.getSelected(), this.wheelView.getSelectedText());
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

    private void initView() {
        this.menuWindow = (ViewGroup) this.menuView.findViewById(R.id.popup_window_view);
        this.cancelTv = (TextView) this.menuView.findViewById(R.id.popup_window_cancel_btn);
        this.sureTv = (TextView) this.menuView.findViewById(R.id.popup_window_save_btn);
        this.titleTv = (TextView) this.menuView.findViewById(R.id.popup_window_title);
        this.titleTv.setVisibility(View.GONE);
        this.wheelView = (Wheelview) this.menuView.findViewById(R.id.popup_window_wheel_view);
        if (TextUtils.isEmpty(title)) {
            this.titleTv.setVisibility(View.GONE);
        } else {
            this.titleTv.setText(title);
            this.titleTv.setVisibility(View.VISIBLE);
        }

        this.cancelTv.setOnClickListener(this);
        this.sureTv.setOnClickListener(this);
        if (null == list || list.isEmpty()) {
            return;
        }
        this.wheelView.setData(list);
        this.wheelView.setDefault(defaultIndex);

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
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.menuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = menuView.findViewById(R.id.popup_window_view)
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

        setBackgroundAlpha(0.5f);
    }

}
