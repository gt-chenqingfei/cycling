package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * 心率饼图最底部item
 * Created by secret on 16/10/10.
 */

public class HeartRateBottomItemView extends LinearLayout {

    /**
     * 区域
     */
    private TextView mTVHeartRateRange;
    /**
     * 说明
     */
    private TextView mTVHeartRateDesc;
    /**
     * 背景颜色
     */
    private View mViewColor;

    private boolean isSeleted;

    public HeartRateBottomItemView(Context context) {
        super(context);
        this.initView(context, null, 0);
    }

    public HeartRateBottomItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public HeartRateBottomItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.layout_heart_rate_bottom_item, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeartRateBottomItemView);
        String mRange = typedArray.getString(R.styleable.HeartRateBottomItemView_rateRange);
        String mDesc = typedArray.getString(R.styleable.HeartRateBottomItemView_rateDesc);
        int mColor = typedArray.getColor(R.styleable.HeartRateBottomItemView_rateColor, Color.BLACK);
        typedArray.recycle();

        mTVHeartRateRange = (TextView) findViewById(R.id.textView_heart_rate_range);
        mTVHeartRateDesc = (TextView) findViewById(R.id.textView_heart_rate_desc);
        mViewColor = findViewById(R.id.view_heart_rate_color);

        mTVHeartRateRange.setText((null == mRange) ? "" : mRange);
        mTVHeartRateDesc.setText((null == mDesc) ? "" : mDesc);
        mViewColor.setBackgroundColor(mColor);
    }

    /**
     * 设置心率范围
     * @param range 范围
     */
    public void setHeartRateRange(CharSequence range) {
        mTVHeartRateRange.setText(range);
    }

    /**
     * 设置心率范围
     * @param resId 范围
     */
    public void setHeartRateRange(int resId) {
        mTVHeartRateRange.setText(resId);
    }

    /**
     * 设置心率说明
     * @param desc 说明
     */
    public void setHeartRateDesc(CharSequence desc) {
        mTVHeartRateDesc.setText(desc);
    }

    /**
     * 设置心率说明
     * @param resId 说明
     */
    public void setHeartRateDesc(int resId) {
        mTVHeartRateDesc.setText(resId);
    }

    /**
     * 设置心率范围区域颜色
     * @param color color
     */
    public void setHeartRateColor(int color) {
        mViewColor.setBackgroundColor(color);
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }
}
