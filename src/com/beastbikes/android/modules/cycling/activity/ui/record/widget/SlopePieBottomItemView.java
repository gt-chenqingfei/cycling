package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * 爬坡底部view item
 * Created by secret on 16/10/11.
 */

public class SlopePieBottomItemView extends LinearLayout {

    private String mDesc;
    private int mColor;

    private View mViewSlopeColor;
    private TextView mTVSlopeDesc;
    private TextView mTVSlopePercent;
    private TextView mTVSlopeAverageSpeed;

    public SlopePieBottomItemView(Context context) {
        super(context);
        this.initView(context, null);
    }

    public SlopePieBottomItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public SlopePieBottomItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        inflate(context, R.layout.layout_slope_bottom_item, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlopeBottomItemView);
        mDesc = typedArray.getString(R.styleable.SlopeBottomItemView_slopeDesc);
        mColor = typedArray.getColor(R.styleable.SlopeBottomItemView_slopeColor, Color.GREEN);

        mViewSlopeColor = findViewById(R.id.view_slope_bottom_item_color);
        mTVSlopeDesc = (TextView) findViewById(R.id.textView_slope_bottom_item_desc);
        mTVSlopePercent = (TextView) findViewById(R.id.textView_slope_bottom_item_percent);
        mTVSlopeAverageSpeed = (TextView) findViewById(R.id.textView_slope_bottom_item_average_speed);

        GradientDrawable bgShape = (GradientDrawable )mViewSlopeColor.getBackground();
        bgShape.setColor(mColor);

        mTVSlopeDesc.setText((null == mDesc) ? "" : mDesc);

    }

    /**
     * 设置所占百分比
     * @param charSequence percent
     */
    public void setSlopePercent(CharSequence charSequence) {
        this.mTVSlopePercent.setText(charSequence);
    }

    /**
     * 设置所占百分比
     * @param resId percent
     */
    public void setSlopePercent(int resId) {
        this.mTVSlopePercent.setText(resId);
    }

    /**
     * 设置平均速度
     * @param charSequence
     */
    public void setSlopAverageSpeed(CharSequence charSequence) {
        this.mTVSlopeAverageSpeed.setText(charSequence);
    }

    /**
     * 设置平均速度
     * @param resId
     */
    public void setSlopAverageSpeed(int resId) {
        this.mTVSlopeAverageSpeed.setText(resId);
    }
}
