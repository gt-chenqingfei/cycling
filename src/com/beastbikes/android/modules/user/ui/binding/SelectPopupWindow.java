package com.beastbikes.android.modules.user.ui.binding;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * Created by zhangyao on 2016/1/25.
 */
public class SelectPopupWindow extends PopupWindow{
    private TextView cancel, banding;
    private View mMenuView;

    public SelectPopupWindow(Activity activity,View.OnClickListener onClickListener){
        this.mMenuView = LayoutInflater.from(activity).inflate(R.layout.popup_window_account_slelect,null);
        this.cancel = (TextView) mMenuView.findViewById(R.id.popup_window_account_slelect_cencal);
        this.banding = (TextView) mMenuView.findViewById(R.id.popup_window_account_slelect_banding);
        this.banding.setOnClickListener(onClickListener);
        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);

        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.popup_window_account_slelect).getTop();
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
}
