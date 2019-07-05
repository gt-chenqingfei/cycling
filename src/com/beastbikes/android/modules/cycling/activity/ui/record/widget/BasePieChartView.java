package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

/**
 * PieChart(饼图) 基类
 * Created by secret on 16/10/9.
 */

public class BasePieChartView extends BaseCyclingChartView implements OnChartValueSelectedListener {

    private PieChart mPieChart;

    public BasePieChartView(Context context) {
        super(context);
    }

    public BasePieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);

        ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub_pie_chart);
        viewStub.inflate();
        mPieChart = (PieChart) findViewById(R.id.pie_chart);

        ViewStub mViewStub = (ViewStub) findViewById(R.id.viewStub_chart_no_data);
        mViewStub.inflate();
    }

    public void initChart() {

        mPieChart.setUsePercentValues(true);
        mPieChart.setDescription("");
        mPieChart.setExtraOffsets(5, 10, 5, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);

        //中间的洞
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.TRANSPARENT);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        //中间洞与外围中间的透明
        mPieChart.setTransparentCircleRadius(55f);

        mPieChart.setDrawCenterText(false);

        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(false);
        mPieChart.setHighlightPerTapEnabled(true);

        mPieChart.setDrawEntryLabels(false);
        mPieChart.setDrawSlicesUnderHole(false);

        mPieChart.setOnChartValueSelectedListener(this);
    }

    protected void setTouchable(boolean touchable) {
        mPieChart.setTouchEnabled(touchable);
    }

    protected void setHighLightValue(float x) {
        mPieChart.highlightValue(x, 0, true);
    }

    protected void setNoHighLightValue() {
        mPieChart.highlightValue(null, true);
    }

    protected void setData(ArrayList<PieEntry> entries, int[] colors) {

        if (null == entries || entries.isEmpty()) {
            entries = new ArrayList<>();
        }
        //second param is label
        PieDataSet dataSet = new PieDataSet(entries, "");

        //每块区域之间的间隙
        dataSet.setSliceSpace(0f);
        dataSet.setDrawValues(false);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);
        mPieChart.invalidate();
    }

    protected void setData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {

        if (null == entries || entries.isEmpty()) {
            entries = new ArrayList<>();
        }
        //second param is label
        PieDataSet dataSet = new PieDataSet(entries, "");

        //每块区域之间的间隙
        dataSet.setSliceSpace(0f);
        dataSet.setDrawValues(false);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);
        mPieChart.invalidate();
    }

    /**
     * 设置没有数据view
     */
    protected void showNoDataView() {

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_chart_no_data);
        relativeLayout.setVisibility(View.VISIBLE);

        ImageView mImgNoDataIcon = (ImageView) findViewById(R.id.img_chart_no_data_icon);
        TextView mTVNoConnectedDevices = (TextView) findViewById(R.id.textView_chart_no_data_function);
        TextView mTVFunctionDesc = (TextView) findViewById(R.id.textView_chart_no_data_function_desc);
        TextView mTVNoDataDesc = (TextView) findViewById(R.id.textView_chart_no_data_bottom_desc);

        mImgNoDataIcon.setImageResource(R.drawable.ic_chart_no_heart_rage);
        mTVNoConnectedDevices.setText(R.string.str_have_not_connect_heart_rate_device);
        mTVNoDataDesc.setText(R.string.str_heart_rate_only_support_desc);
        mTVFunctionDesc.setText(R.string.str_heart_rate_no_data_desc);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        onValueSelected(e);
    }

    @Override
    public void onNothingSelected() {
        onNoValueSelected();
    }

    protected void onValueSelected(Entry e) {

    }

    protected void onNoValueSelected() {

    }
}
