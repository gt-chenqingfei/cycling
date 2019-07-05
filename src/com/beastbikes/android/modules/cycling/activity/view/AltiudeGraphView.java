package com.beastbikes.android.modules.cycling.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.AreaChart;
import org.xclcharts.chart.AreaData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenqingfei on 16/5/25.
 */
public class AltiudeGraphView extends ChartView {

    public AltiudeGraphView(Context context) {
        super(context);
    }

    public AltiudeGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AltiudeGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 用来显示面积图，左边及底部的轴
    private AreaChart chartLeft = new AreaChart();

    private AreaChart chartX = new AreaChart();

    // 数据集合
    protected LinkedList<AreaData> mDatasetLeft = new LinkedList<AreaData>();
    protected LinkedList<AreaData> mDatasetX = new LinkedList<AreaData>();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 图所占范围大小
        chartX.setChartRange(w, h);
        chartLeft.setChartRange(w, h);
    }

    protected void chartXRender(double max, double min, double steps, List<String> labels) {
        try {
            // 轴数据源
            // 标签x轴
            chartX.setCategories(labels);
            chartX.getCategoryAxis().setTickLabelMargin(
                    DensityUtil.dip2px(getContext(), 12));
            // 数据轴
            chartX.setDataSource(mDatasetX);

            // 仅横向平移
            chartX.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

            // 数据轴最大值
            chartX.getDataAxis().setAxisMax(max);
            chartX.getDataAxis().setAxisMin(min);
            chartX.getDataAxis().hide();
            // 数据轴刻度间隔
            chartX.getDataAxis().setAxisSteps(steps);

            float left = DensityUtil.dip2px(getContext(), 30); // left 40
            float right = DensityUtil.dip2px(getContext(), 30); // right 20
            chartX.setPadding(left, 10, right, 50); // ltrb[2]

            // 网格
//			chartX.getPlotGrid().showHorizontalLines();
            chartX.getPlotGrid().showVerticalLines();
            chartX.getPlotGrid().getVerticalLinePaint().setColor(0xff444444);
            chartX.getPlotGrid().getHorizontalLinePaint()
                    .setColor(0xff444444);
            chartX.getPlotGrid().getHorizontalLinePaint()
                    .setStrokeWidth(0.2f);
            chartX.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.2f);
            chartX.getPlotGrid().hideHorizontalLines();
//			chartX.getPlotGrid().hideVerticalLines();

            // 把轴线和刻度线给隐藏起来
            chartX.getDataAxis().hideAxisLine();
            chartX.getDataAxis().hideTickMarks();
            chartX.getCategoryAxis().hideAxisLine();
            chartX.getCategoryAxis().hideTickMarks();

            chartX.getDataAxis().getTickLabelPaint().setColor(Color.RED);
            chartX.getCategoryAxis().getTickLabelPaint()
                    .setColor(0xff999999);

            // 透明度
            chartX.setAreaAlpha(0);
            // 显示图例
            chartX.getPlotLegend().hide();

            // 激活点击监听
            chartX.ActiveListenItemClick();
            // 为了让触发更灵敏，可以扩大5px的点击监听范围
            chartX.extPointClickRange(10);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void chartLeftRender(double max, double min, double steps, List<String> labels) {
        try {
            // 轴数据源
            // 标签x轴
            chartLeft.setCategories(labels);
            chartLeft.getCategoryAxis().setTickLabelMargin(
                    DensityUtil.dip2px(getContext(), 12));
            chartLeft.getCategoryAxis().hide();
            // 数据轴
            chartLeft.setDataSource(mDatasetLeft);

            // 仅横向平移
            chartLeft.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

            // 数据轴最大值
            chartLeft.getDataAxis().setAxisMax(max);
            chartLeft.getDataAxis().setAxisMin(min);

            // 数据轴刻度间隔
            chartLeft.getDataAxis().setAxisSteps(steps);

            float left = DensityUtil.dip2px(getContext(), 30); // left 40
            float right = DensityUtil.dip2px(getContext(), 30); // right 20
            chartLeft.setPadding(left, 10, right, 50); // ltrb[2]

            // 网格
            chartLeft.getPlotGrid().showHorizontalLines();
            chartLeft.getPlotGrid().hideVerticalLines();
            chartLeft.getPlotGrid().getVerticalLinePaint().setColor(0xff444444);
            chartLeft.getPlotGrid().getHorizontalLinePaint()
                    .setColor(0xff444444);
            chartLeft.getPlotGrid().getHorizontalLinePaint()
                    .setStrokeWidth(0.2f);
            chartLeft.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.2f);

            // 把轴线和刻度线给隐藏起来
            chartLeft.getDataAxis().hideAxisLine();
            chartLeft.getDataAxis().hideTickMarks();
            chartLeft.getCategoryAxis().hideAxisLine();
            chartLeft.getCategoryAxis().hideTickMarks();

            chartLeft.getDataAxis().getTickLabelPaint().setColor(Color.RED);
            chartLeft.getCategoryAxis().getTickLabelPaint()
                    .setColor(0xff999999);

            // 透明度
            chartLeft.setAreaAlpha(0);
            // 显示图例
            chartLeft.getPlotLegend().hide();

            // 激活点击监听
            chartLeft.ActiveListenItemClick();
            // 为了让触发更灵敏，可以扩大5px的点击监听范围
            chartLeft.extPointClickRange(10);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chartX.render(canvas);
            chartLeft.render(canvas);


        } catch (Exception e) {
        }
    }

    public void drawAltiudeGraph(List<Double> altitudes, double maxAltitude, double min, int maxDistance) {
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int label ;
            if(maxDistance > 0){
                label = maxDistance / i;
            }
            else{
                label = i;
            }
            labels.add(label+"");
        }

        AreaData lineLeft = new AreaData("", altitudes, Color.RED, Color.TRANSPARENT);
        lineLeft.setApplayGradient(false);
        lineLeft.getLinePaint().setStrokeWidth(3);
        lineLeft.setGradientMode(Shader.TileMode.MIRROR);
        lineLeft.setDotStyle(XEnum.DotStyle.HIDE);
        mDatasetLeft.add(lineLeft);



        double steps = 10;

        chartLeftRender(maxAltitude, min, steps, labels);

        chartXRender(maxAltitude, min, steps, labels);

        invalidate();
    }

}
