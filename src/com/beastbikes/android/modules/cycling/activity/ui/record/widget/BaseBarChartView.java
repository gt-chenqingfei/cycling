package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.ui.record.formatter.MyAxisValueFormatter;
import com.beastbikes.android.modules.cycling.activity.ui.record.formatter.UnitFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

/**
 *
 * Created by secret on 16/10/10.
 */

public class BaseBarChartView extends BaseCyclingChartView {

    private BarChart mChart;

    public BaseBarChartView(Context context) {
        super(context);
    }

    public BaseBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseBarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub_bar_chart);
        viewStub.inflate();

        mChart = (BarChart) findViewById(R.id.bar_chart);

        ViewStub mViewStub = (ViewStub) findViewById(R.id.viewStub_chart_no_data);
        mViewStub.inflate();
    }

    public void initChart() {

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setDescription("");

//        mChart.setExtraLeftOffset(10f);
//        mChart.setExtraRightOffset(10f);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);
        mChart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(11f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(1f, 10f, 0f);
        leftAxis.setLabelCount(6, true);
        mChart.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setTextColor(Color.WHITE);
        xLabels.setTextSize(11f);
        xLabels.setGranularity(1f);
        xLabels.setLabelCount(6);
        xLabels.setYOffset(4.5f);
        xLabels.setDrawGridLines(false);
        xLabels.setValueFormatter(new UnitFormatter());
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);

//        Legend l = mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setFormSize(8f);
//        l.setFormToTextSpace(4f);
//        l.setXEntrySpace(6f);

    }

    /**
     * if more than count entries are displayed in the chart, no values will be drawn
     * @param count count
     */
    public void setMaxVisibleValueCount(int count) {
        mChart.setMaxVisibleValueCount(count);
    }

    /**
     * X轴label字体大小
     * @param size
     */
    public void setXAxisLabelTextSize(float size) {
        mChart.getXAxis().setTextSize(size);
    }

    /**
     * Y轴label字体大小
     * @param size
     */
    public void setYAxisLabelTextSize(float size) {
        mChart.getAxisLeft().setTextSize(size);
    }

    /**
     * 统一设置X,Y轴字体大小
     * @param size
     */
    public void setAxisLabelTextSize(float size) {
        mChart.getXAxis().setTextSize(size);
        mChart.getAxisLeft().setTextSize(size);
    }

    /**
     * X轴label字体颜色
     * @param color
     */
    public void setXAxisLabelTextColor(int color) {
        mChart.getXAxis().setTextColor(color);
    }

    /**
     * Y轴label字体颜色
     * @param color
     */
    public void setYAxisLabelTextColor(int color) {
        mChart.getAxisLeft().setTextColor(color);
    }

    /**
     * 统一设置X,Y轴字体颜色
     * @param color
     */
    public void setAxisLabelTextColor(int color) {
        mChart.getXAxis().setTextColor(color);
        mChart.getAxisLeft().setTextColor(color);
    }

    /**
     * 设置
     * @param count
     */
    public void setXLabelCount(int count) {
        mChart.getXAxis().setLabelCount(count);
    }

    /**
     * 设置数据
     * @param yValues1
     * @param color
     */
    protected void setData(ArrayList<BarEntry> yValues1, int color) {

        BarDataSet set1;
        if (null == yValues1) {
//            yValues1 = new ArrayList<>();
            return;
        }

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yValues1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yValues1, "");
            set1.setColor(color);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setDrawValues(false);
            data.setValueTextColor(Color.WHITE);
            data.setBarWidth(0.9f);
            mChart.setData(data);
        }

        mChart.setFitBars(true);
        mChart.invalidate();
    }

    /**
     * 设置没有数据view
     */
    protected void showNoDataView() {

        RelativeLayout relaChartNoData = (RelativeLayout) findViewById(R.id.relative_chart_no_data);
        relaChartNoData.setVisibility(View.VISIBLE);

        ImageView mImgNoDataIcon = (ImageView) findViewById(R.id.img_chart_no_data_icon);
        TextView mTVNoConnectedDevices = (TextView) findViewById(R.id.textView_chart_no_data_function);
        TextView mTVFunctionDesc = (TextView) findViewById(R.id.textView_chart_no_data_function_desc);
        TextView mTVNoDataDesc = (TextView) findViewById(R.id.textView_chart_no_data_bottom_desc);

        mImgNoDataIcon.setImageResource(R.drawable.ic_chart_no_cadence);
        mTVNoConnectedDevices.setText(R.string.str_have_not_connect_cadence_device);
        mTVNoDataDesc.setText(R.string.str_cadence_only_support_desc);
        mTVFunctionDesc.setText(R.string.str_cadence_no_data_desc);
    }
}
