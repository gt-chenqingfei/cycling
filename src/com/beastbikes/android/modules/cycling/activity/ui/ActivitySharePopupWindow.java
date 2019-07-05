package com.beastbikes.android.modules.cycling.activity.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.beastbikes.android.R;

public class ActivitySharePopupWindow extends PopupWindow {

    private View menuView;
    private LinearLayout cameraBtn;
    private LinearLayout dataBtn;
    private Activity activity;

    public ActivitySharePopupWindow(Activity context, OnClickListener listener) {
        this.activity = context;
        LayoutInflater inflater = context.getLayoutInflater();
        this.menuView = inflater.inflate(
                R.layout.activity_complete_share_stencil_view, null);
        this.cameraBtn = (LinearLayout) this.menuView
                .findViewById(R.id.activity_complete_stencil_camera_btn);
        this.dataBtn = (LinearLayout) this.menuView
                .findViewById(R.id.activity_complete_stencil_data_btn);

        this.cameraBtn.setOnClickListener(listener);
        this.dataBtn.setOnClickListener(listener);

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.menuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = menuView.findViewById(R.id.activity_complete_window)
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
        this.setBackgroundAlpha(0.5f);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        this.setBackgroundAlpha(1f);
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }
}
