package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.beastbikes.android.R;

/**
 * Created by zhangyao on 2016/3/25.
 */
public class ColorPopupWindows extends PopupWindow implements View.OnClickListener {
    private Activity context;
    private LayoutInflater layoutInflater;
    private View view;
    private OnColorChangedListener listener;


    public interface OnColorChangedListener {
        /**
         * 回调函数
         * @param color 选中的颜色
         */
        void colorChanged(int color);
    }


    public ColorPopupWindows(Activity context,OnColorChangedListener listener){
        this.context = context;
        this.listener = listener;

        layoutInflater = LayoutInflater.from(context);

        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.WindowAnim);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);

        init();
    }

    private void init(){
        view =layoutInflater.inflate(R.layout.popuowindows_color,null);
        setContentView(view);

        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;    //得到宽度
        int height = dm.heightPixels;  //得到高度
        this.setWidth(width / 2);
        this.setHeight(height / 13);

        view.findViewById(R.id.view_color_circle_red).setOnClickListener(this);
        view.findViewById(R.id.view_color_circle_black).setOnClickListener(this);
        view.findViewById(R.id.view_color_circle_green).setOnClickListener(this);
        view.findViewById(R.id.view_color_circle_orange).setOnClickListener(this);
        view.findViewById(R.id.view_color_circle_violet).setOnClickListener(this);
        view.findViewById(R.id.view_color_circle_blue).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_color_circle_red:
                listener.colorChanged(context.getResources().getColor(R.color.view_color_circle_red));
                break;
            case R.id.view_color_circle_black:
                listener.colorChanged(context.getResources().getColor(R.color.view_color_circle_black));
                break;
            case R.id.view_color_circle_green:
                listener.colorChanged(context.getResources().getColor(R.color.view_color_circle_green));
                break;
            case R.id.view_color_circle_orange:
                listener.colorChanged(context.getResources().getColor(R.color.view_color_circle_orange));
                break;
            case R.id.view_color_circle_violet:
                listener.colorChanged(context.getResources().getColor(R.color.view_color_circle_violet));
                break;
            case R.id.view_color_circle_blue:
                listener.colorChanged(context.getResources().getColor(R.color.view_color_circle_blue));
                break;
        }
        dismiss();
    }
}
