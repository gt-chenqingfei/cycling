package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewStub;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.ui.record.formatter.MAxisValueFormatter;
import com.beastbikes.android.modules.cycling.activity.ui.record.formatter.MyValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * 使用:
 * chart属性需要先设置,然后调用{@link BaseLineChartView#initChart()} ()}
 * 只收设置data{@link BaseLineChartView#setData(ArrayList)}
 * Created by secret on 16/10/10.
 */

public class BaseLineChartView extends BaseCyclingChartView {

    /**
     * 填充drawable
     */
    protected Drawable mFillDrawable;

    /**
     * 线的颜色
     */
    protected int mLineColor;

    /**
     * 平均线的高度值
     */
    protected float mAverageLineValue;

    /**
     * Y轴最大值
     */
    private float mYMaxValue;

    /**
     * Y轴最小值
     */
    private float mYMinValue;

    /**
     * X轴最大值
     */
    private float mXMaxValue;

    /**
     * X轴最小值
     */
    private float mXMinValue;

    private float mTotalDistance;

    private LineChart mLineChart;

    public BaseLineChartView(Context context) {
        super(context);
    }

    public BaseLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub_line_chart);
        viewStub.inflate();
        mLineChart = (LineChart) findViewById(R.id.line_chart);
    }

    public void initChart() {
        // enable touch gestures
        mLineChart.setTouchEnabled(false);

        // enable scaling and dragging
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(Color.parseColor("#181818"));
        mLineChart.setUnbindEnabled(true);
        mLineChart.setDescription("");

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(6, true);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.parseColor("#333333"));
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(4.5f);
//        xAxis.setGranularity(0.1f); // minimum axis-step (interval) is 1
        if (mXMaxValue != 0) {
            xAxis.setAxisMaxValue(mXMaxValue);
        }
        xAxis.setValueFormatter(new MAxisValueFormatter(mXAxisUnit, mTotalDistance));

        LimitLine ll1 = new LimitLine(mAverageLineValue);
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(15f, 15f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
//        ll1.setLineColor(Color.parseColor("#aaaaaa"));
        ll1.setLineColor(mLineColor);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.setTextColor(Color.parseColor("#999999"));
        if (mYMaxValue != 0) {
            leftAxis.setAxisMaxValue(mYMaxValue);
        }
        leftAxis.setAxisMinValue(mYMinValue);
//        leftAxis.calculate(0f, 100f);
        leftAxis.setYOffset(0f);
        leftAxis.setGridColor(Color.parseColor("#979797"));
        leftAxis.enableGridDashedLine(1f, 10f, 0f);
        leftAxis.setLabelCount(6, true);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setValueFormatter(new MyValueFormatter());

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(false);
//        leftAxis.setEnabled(false);

        mLineChart.getAxisRight().setEnabled(false);

//        mLineChart.animateX(2500);
        //mChart.invalidate();
//        this.onDataChanged(25, 100);

        // get the legend (only possible after setting data)
//        Legend l = mLineChart.getLegend();

        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);legend

        // // don't forget to refresh the drawing
        // mChart.invalidate();
    }

    public void setData(ArrayList<Entry> values) {

        if (null == values) {
            values = new ArrayList<>();
        }
        LineDataSet set1;

        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, mXAxisUnit);
//            set1.setColor(Color.parseColor("#aaaaaa"));
            set1.setColor(mLineColor);
            set1.setCircleColor(mLineColor);
//            set1.setCircleRadius(0.5f);
            set1.setLineWidth(2f);
            set1.setDrawCircles(false);
            set1.setDrawValues(false);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                set1.setFillDrawable(mFillDrawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mLineChart.setData(data);
        }
    }

    public void setFillDrawable(Drawable mFillDrawable) {
        this.mFillDrawable = mFillDrawable;
    }

    public void setLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
    }

    public void setAverageLineValue(float averageLineValue) {
        this.mAverageLineValue = averageLineValue;
    }

    public void setYMaxValue(float maxValue) {
        this.mYMaxValue = maxValue;
    }

    public void setYMinValue(float minValue) {
        this.mYMinValue = minValue;
    }

    public void setXMinValue(float minValue) {
        this.mXMinValue = minValue;
    }

    public void setXMaxValue(float maxValue) {
        this.mXMaxValue = maxValue;
    }

    public void setTotalDistance(float totalDistance) {
        this.mTotalDistance = totalDistance;
    }
}
