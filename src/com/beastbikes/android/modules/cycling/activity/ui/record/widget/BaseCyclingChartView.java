package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * base cycling line chart view
 * Created by secret on 16/10/8.
 */

public abstract class BaseCyclingChartView extends LinearLayout {

    /**
     * 左上角title 例如速度,海拔
     */
    private String mChartName;
    /**
     * 左上角title左侧图标
     */
    private Drawable mNameDrawable;

    /**
     * X轴最左侧单位
     */
    protected String mXAxisUnit;

    private TextView mTVChartName;

    public BaseCyclingChartView(Context context) {
        super(context);
        this.initView(context, null);
    }

    public BaseCyclingChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public BaseCyclingChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_base_chart, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseCyclingChartView);

        mChartName = typedArray.getString(R.styleable.BaseCyclingChartView_chartName);
        mXAxisUnit = typedArray.getString(R.styleable.BaseCyclingChartView_chartXUnit);
        mNameDrawable = typedArray.getDrawable(R.styleable.BaseCyclingChartView_chartDrawable);

        typedArray.recycle();

        mTVChartName = (TextView) findViewById(R.id.textView_cycling_data_left_top);
        mTVChartName.setText(mChartName);
        mTVChartName.setCompoundDrawablesWithIntrinsicBounds(mNameDrawable, null, null, null);

    }

    public void setChartName(String mChartName) {
        this.mChartName = mChartName;
        mTVChartName.setText(mChartName);
    }

    public void setNameDrawable(Drawable mNameDrawable) {
        this.mNameDrawable = mNameDrawable;
        mTVChartName.setCompoundDrawablesWithIntrinsicBounds(mNameDrawable, null, null, null);
    }

    public void setXAxisUnit(String mXAxisUnit) {
        this.mXAxisUnit = mXAxisUnit;
    }

}
