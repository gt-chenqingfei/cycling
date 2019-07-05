package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.widget.NumberTextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 *
 * Created by secret on 16/10/18.
 */

public class ChartDataCompareItemView extends RelativeLayout {

    private NumberTextView mTVValue;
    private TextView mTVUnit;
    private LineChart mLineChart;

    private int mLineColor;

    public ChartDataCompareItemView(Context context) {
        super(context);
        this.initView(context, null);
    }

    public ChartDataCompareItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public ChartDataCompareItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_chart_data_compare_item, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChartDataCompareItemView);

        mLineColor = typedArray.getColor(R.styleable.ChartDataCompareItemView_chartDataLineColor, 0xffffffff);
        Drawable drawable = typedArray.getDrawable(R.styleable.ChartDataCompareItemView_valueDrawable);

        mTVValue = (NumberTextView) findViewById(R.id.tv_chart_data_compare_value);
        mTVValue.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

        mTVUnit = (TextView) findViewById(R.id.tv_chart_data_compare_unit);
        mLineChart = (LineChart) findViewById(R.id.linear_chart_data_compare);

        typedArray.recycle();

        this.initChart();
    }

    public void setValue(CharSequence charSequence) {
        mTVValue.setText(charSequence);
    }

    public void setUnit(CharSequence charSequence) {
        mTVUnit.setText(charSequence);
    }

    private void initChart() {
        // enable touch gestures
        mLineChart.setTouchEnabled(false);

        // enable scaling and dragging
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(Color.parseColor("#00111111"));
        mLineChart.setUnbindEnabled(true);
        mLineChart.setDescription("");

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.layout_marker);
//        mv.setChar(mLineChart); // For bounds control
//        mLineChart.setMarker(mv); // Set the marker to the chart
        mLineChart.setMarkerView(mv);

        mLineChart.setViewPortOffsets(Utils.convertDpToPixel(4), 0, Utils.convertDpToPixel(4), Utils.convertDpToPixel(1));

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineColor(0xff282828);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setYOffset(0f);
        leftAxis.enableGridDashedLine(1f, 10f, 0f);
        leftAxis.setAxisLineColor(0xff282828);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setAxisLineColor(0xff282828);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawGridLines(false);

        Legend l = mLineChart.getLegend();
        l.setEnabled(false);

    }

    /**
     * 设置marker位
     * @param x
     */
    public void setHighLightValue(float x) {
        mLineChart.highlightValue(x, 0, false);
    }

    /**
     * 设置Y轴最大值
     * @param maxValue
     */
    public void setYMaxValue(float maxValue) {
        mLineChart.getAxisLeft().setAxisMaxValue(maxValue);
    }

    /**
     * 设置Y轴最小值
     * @param minValue
     */
    public void setYMinValue(float minValue) {
        mLineChart.getAxisLeft().setAxisMinValue(minValue);
    }

    public void setData(ArrayList<Entry> entries) {

        if (null == entries) {
            entries = new ArrayList<>();
        }

        LineDataSet lineDataSet;
        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(entries);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(entries, "");
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setColor(mLineColor);
            lineDataSet.setCircleColor(Color.WHITE);
            lineDataSet.setLineWidth(1f);
            lineDataSet.setCircleRadius(3f);
            lineDataSet.setFillAlpha(65);
            lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
            lineDataSet.setHighLightColor(0x00ffffff);
            lineDataSet.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            lineDataSet.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a data object with the datasets
            LineData data = new LineData(lineDataSet);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mLineChart.setData(data);
            mLineChart.invalidate();
        }

        mLineChart.highlightValue(0f, 0, false);

    }

}
