package com.beastbikes.android.ble.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * Created by icedan on 16/10/12.
 */

public class HeartRateIntervalItemView extends RelativeLayout {

    private TextView heartRateLabelTv;
    private TextView heartRateValueTv;

    public HeartRateIntervalItemView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public HeartRateIntervalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public HeartRateIntervalItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.setting_heart_rate_item, this);

        this.heartRateLabelTv = (TextView) findViewById(R.id.setting_heart_rate_label);
        this.heartRateValueTv = (TextView) findViewById(R.id.setting_heart_rate_value);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeartRateIntervalItemView);
        String label = typedArray.getString(R.styleable.HeartRateIntervalItemView_interval_label);
        int mColor = typedArray.getColor(R.styleable.HeartRateIntervalItemView_interval_background_color, Color.BLACK);
        typedArray.recycle();

        setBackgroundColor(mColor);
        this.heartRateLabelTv.setText(label);
    }

    /**
     * 设置心率区间描述
     * @param heartRate
     */
    public void setHeartRateValue(String heartRate) {
        this.heartRateValueTv.setText(heartRate);
    }

    public void setHeartRateLabel(String label) {
        this.heartRateLabelTv.setText(label);
    }

}
