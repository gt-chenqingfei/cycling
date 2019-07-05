package com.beastbikes.android.modules.cycling.route.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.AreaChart;
import org.xclcharts.chart.AreaData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 路书曲线图view
 * Created by secret on 16/8/18.
 */
public class RouteElevationView extends ChartView {

    private static final String TAG = RouteElevationView.class.getName();

    //标签集合
    private LinkedList<String> mLabels = new LinkedList<String>();
    //数据集合
    private LinkedList<AreaData> mElevationDatas = new LinkedList<>();

    private AreaChart mChart = new AreaChart();

    private LinkedList<String> mVerticalLabels = new LinkedList<>();
    private SplineChart mVerticalChart = new SplineChart();

    public RouteElevationView(Context context) {
        super(context);
    }

    public RouteElevationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RouteElevationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 图所占范围大小
        mChart.setChartRange(w, h);
        mVerticalChart.setChartRange(w, h);
    }


    @Override
    public void render(Canvas canvas) {
        try {
            mChart.render(canvas);
            mVerticalChart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * set label data
     * @param size
     */
    public void setLabelsData(int size) {
        this.mLabels.clear();
        for (int i = 0; i < size; i++) {
            this.mLabels.add(String.valueOf(i));
        }
    }

    /**
     * set line data
     * @param dataSource
     */
    public void setData(ArrayList<Double> dataSource) {
        AreaData line = new AreaData("", dataSource,
                0xff1bb888, Color.WHITE, 0xffedfff8);

        line.getLinePaint().setStrokeWidth(3.0f);
        //不显示点
        line.setDotStyle(XEnum.DotStyle.HIDE);
        //line1.setDotStyle(XEnum.DotStyle.RECT);
        //line1.setLabelVisible(true);
        line.setApplayGradient(true);
//        line.setLineColor(0xff61b435);
        line.setDataSet(dataSource);

        mElevationDatas.add(line);
    }

    /**
     * 绘制区域图
     */
    public void chartRender(double maxElevation, double minElevation, int size) {

        try{
            mChart.setPadding(80, 30 ,0, 50);
            int[] graduations = new int[]{10, 20, 40, 50, 100, 200, 300, 400,
                    1000, 1200, 2000};
            int index = binarySearch(graduations, Math.abs((maxElevation - minElevation) / 5));

            int steps = graduations[index];
            //数据轴最小值
            mChart.getDataAxis().setAxisMin(Math.min(0, minElevation));
            //数据轴最大值
            mChart.getDataAxis().setAxisMax(graduations[index] * 5);
            //数据轴刻度间隔
            mChart.getDataAxis().setAxisSteps(steps);

            mChart.hideBorder();

            //轴数据源
            //标签轴
            mChart.setCategories(mLabels);
            //数据轴
            mChart.setDataSource(mElevationDatas);
            //仅横向平移
            mChart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

            //网格
            mChart.getPlotGrid().showHorizontalLines();
            mChart.getPlotGrid().hideVerticalLines();

            mChart.getPlotGrid().getHorizontalLinePaint().setColor(0x55797C7E);
            mVerticalChart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(0.1f);

            //把轴线和刻度线给隐藏起来
            mChart.getDataAxis().hideAxisLine();
            mChart.getDataAxis().hideTickMarks();
            mChart.getDataAxis().getTickLabelPaint().setColor(0x55797C7E);
            mChart.getCategoryAxis().hideAxisLine();
            mChart.getCategoryAxis().hideTickMarks();
            mChart.getCategoryAxis().hideAxisLabels();

            //透明度
            mChart.setAreaAlpha(180);
            //显示图例
            mChart.getPlotLegend().show();

            //激活点击监听
            mChart.ActiveListenItemClick();
            //为了让触发更灵敏，可以扩大5px的点击监听范围
            mChart.extPointClickRange(5);

            //图例显示在正下方
            mChart.getPlotLegend().setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
            mChart.getPlotLegend().setHorizontalAlign(XEnum.HorizontalAlign.CENTER);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        verticalChartRender(maxElevation, minElevation, size);

    }

    private LinkedList<SplineData> getVerticalData(int dataMax, int categoryMax) {

        LinkedList<SplineData> splineDatas = new LinkedList<>();
        ArrayList<Double> datas = new ArrayList<>();

        double step = categoryMax / 10;
        mVerticalLabels.add("0");
        datas.add(0.0);
        ArrayList<PointD> pointDs = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            mVerticalLabels.add(String.valueOf(i * step));
            datas.add((double) i);
        }

        mVerticalLabels.add(String.valueOf(categoryMax));
        datas.add((double) dataMax);

        for (int i = 0; i < 10; i++) {
            PointD pointD = new PointD(Double.valueOf(mVerticalLabels.get(i)), datas.get(i));
            pointDs.add(pointD);
        }

        SplineData splineData = new SplineData();
        splineData.setDotStyle(XEnum.DotStyle.HIDE);
        splineData.setLineDataSet(pointDs);

        splineDatas.add(splineData);
        return splineDatas;
    }

    /**
     * 绘制区域图
     */
    public void verticalChartRender(double maxElevation, double minElevation, int size) {
        try{
            mVerticalChart.setPadding(80, 30 ,0, 50);
            int[] graduations = new int[]{10, 20, 40, 50, 100, 200, 300, 400,
                    1000, 1200, 2000};
            int index = binarySearch(graduations, Math.abs((maxElevation - minElevation) / 5));

            int steps = graduations[index];
            //数据轴最小值
            mVerticalChart.getDataAxis().setAxisMin(Math.min(0, minElevation));
            //数据轴最大值
            mVerticalChart.getDataAxis().setAxisMax(graduations[index] * 5);
            //数据轴刻度间隔
            mVerticalChart.getDataAxis().setAxisSteps(steps);

            mVerticalChart.hideBorder();

            //轴数据源
            //标签轴
            mVerticalChart.setCategories(mVerticalLabels);
            //数据轴
            mVerticalChart.setDataSource(getVerticalData(graduations[index] * 5, size));
            //仅横向平移
            mVerticalChart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

//            mVerticalChart.getDataAxis().hide();

            // 网格
            mVerticalChart.getPlotGrid().showVerticalLines();
            mVerticalChart.getPlotGrid().getVerticalLinePaint().setColor(0x55797C7E);
            mVerticalChart.getPlotGrid().getHorizontalLinePaint()
                    .setColor(0x55797C7E);
//            mVerticalChart.getPlotGrid().getHorizontalLinePaint()
//                    .setStrokeWidth(0.1f);
            mVerticalChart.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.1f);
            mVerticalChart.getPlotGrid().hideHorizontalLines();

            // 把轴线和刻度线给隐藏起来
            mVerticalChart.getDataAxis().hideAxisLine();
            mVerticalChart.getDataAxis().hideTickMarks();
            mVerticalChart.getDataAxis().hideAxisLabels();
            mVerticalChart.getCategoryAxis().hideAxisLine();
            mVerticalChart.getCategoryAxis().hideTickMarks();

            mVerticalChart.getCategoryAxis().getTickLabelPaint()
                    .setColor(0xffffff);

            // 显示图例
            mVerticalChart.getPlotLegend().hide();

            // 激活点击监听
            mVerticalChart.ActiveListenItemClick();
            // 为了让触发更灵敏，可以扩大5px的点击监听范围
            mVerticalChart.extPointClickRange(10);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    }

    /**
     * * 二分查找算法 * *
     *
     * @param srcArray 有序数组 *
     * @param des      查找元素 *
     * @return des的数组下标，没找到返回-1
     */
    public static int binarySearch(int[] srcArray, double des) {
        int low = 0;
        int high = srcArray.length - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (middle == low)
                return low + 1;

            if (middle == high)
                return high;

            if (des == srcArray[middle]) {
                return middle;
            } else if (des < srcArray[middle]) {
                high = middle;
            } else {
                if (middle + 1 == high) {
                    return high;
                }

                low = middle;
            }
        }
        return -1;
    }
}
