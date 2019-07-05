package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * 踏频柱状图
 * first call {@link BaseBarChartView#initChart()}
 * then call {@link BaseBarChartView}相关属性设置
 * last call {@link BaseBarChartView#setData(ArrayList, int)}
 * Created by secret on 16/10/11.
 */
public class CadenceBarChartView extends BaseBarChartView {

    /**
     * 左侧
     */
    private TextView mTVLeftValue;
    private TextView mTVLeftLabel;

    /**
     * 右侧
     */
    private TextView mTVRightValue;
    private TextView mTVRightLabel;
    private TextView mTVDataSource;

    public CadenceBarChartView(Context context) {
        super(context);
    }

    public CadenceBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CadenceBarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        ViewStub stub = (ViewStub) findViewById(R.id.viewStub_chart_title);
        stub.inflate();
        ViewStub stub1 = (ViewStub) findViewById(R.id.layout_cycling_data1);
        stub1.inflate();

        ViewStub viewStub1 = (ViewStub) findViewById(R.id.viewStub_chart_data_source);
        viewStub1.inflate();

        mTVDataSource = (TextView) findViewById(R.id.textView_chart_data_source);
        mTVLeftValue = (TextView) findViewById(R.id.textView_left_item_value);
        mTVLeftLabel = (TextView) findViewById(R.id.textView_left_item_label);

        mTVRightValue = (TextView) findViewById(R.id.textView_right_item_value);
        mTVRightLabel = (TextView) findViewById(R.id.textView_right_item_label);

        mTVLeftLabel.setText(R.string.str_chart_avg_cadence);
        mTVRightLabel.setText(R.string.str_chart_max_cadence);
    }

    public void setData(ArrayList<BarEntry> entries) {
        if (null == entries || entries.isEmpty()) {
            showNoDataView();
        }
        setData(entries, 0xff2ea0cc);
    }

    /**
     * 右上角数据来自于
     * @param charSequence
     */
    public void setDataSource(CharSequence charSequence) {
        this.mTVDataSource.setVisibility(View.VISIBLE);
        this.mTVDataSource.setText(getContext().getString(R.string.str_label_come_from) + charSequence);
    }

    /**
     * 左侧value
     * @param charSequence value
     */
    public void setLeftValue(CharSequence charSequence) {
        mTVLeftValue.setText(charSequence);
    }

    /**
     * 左侧value
     * @param resId value
     */
    public void setLeftValue(int resId) {
        mTVLeftValue.setText(resId);
    }

    /**
     * 左侧label
     * @param charSequence label
     */
    public void setLeftLabel(CharSequence charSequence) {
        mTVLeftLabel.setText(charSequence);
    }

    /**
     * 左侧label
     * @param resId label
     */
    public void setLeftLabel(int resId) {
        mTVLeftLabel.setText(resId);
    }

    /**
     * 右侧value
     * @param charSequence value
     */
    public void setRightValue(CharSequence charSequence) {
        mTVRightValue.setText(charSequence);
    }

    /**
     * 右侧value
     * @param resId value
     */
    public void setRightValue(int resId) {
        mTVRightValue.setText(resId);
    }

    /**
     * 右侧label
     * @param charSequence label
     */
    public void setRightLabel(CharSequence charSequence) {
        mTVRightLabel.setText(charSequence);
    }

    /**
     * 右侧label
     * @param resId label
     */
    public void setRightLabel(int resId) {
        mTVRightLabel.setText(resId);
    }
}
