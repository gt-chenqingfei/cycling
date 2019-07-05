package com.beastbikes.android.modules.cycling.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.AreaChart;
import org.xclcharts.chart.AreaData;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import java.util.LinkedList;
import java.util.List;

public class SpeedAltitudeGraphBase extends ChartView {
	private String TAG = "SpeedAltitudeGraphBase";

	// 用来显示面积图，左边及底部的轴
	private SplineChart chartLeft = new SplineChart();

	private AreaChart chartX = new AreaChart();
	// x轴签集合
//	protected List<String> xLabels = new ArrayList<String>();

	// 数据集合
	protected LinkedList<SplineData> mDatasetLeft = new LinkedList<SplineData>();
	protected LinkedList<AreaData> mDatasetRight = new LinkedList<AreaData>();
	protected LinkedList<AreaData> mDatasetX = new LinkedList<AreaData>();


	// 用来显示折线,右边及顶部的轴
	private AreaChart chartRight = new AreaChart();

	public SpeedAltitudeGraphBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SpeedAltitudeGraphBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SpeedAltitudeGraphBase(Context context, AttributeSet attrs,
								  int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chartX.setChartRange(w,h);
		chartLeft.setChartRange(w, h);
		chartRight.setChartRange(w, h);
	}

	protected void chartXRender(double max,double min,double steps,List<String> labels) {
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
			Log.e(TAG, e.toString());
		}
	}

	protected void chartLeftRender(double max, double min, double steps, int categoryMax, List<String> labels) {
		try {
			//数据源
			chartLeft.setCategories(labels);
			chartLeft.setDataSource(mDatasetLeft);

			//坐标系
			//数据轴最大值
			chartLeft.getDataAxis().setAxisMax(max);
			chartLeft.getDataAxis().setAxisMin(min);
			//数据轴刻度间隔
			chartLeft.getDataAxis().setAxisSteps(steps);
//			chart.setCustomLines(mYCustomLineDataset); //y轴

			//标签轴最大值
			chartLeft.setCategoryAxisMax(categoryMax);
			//标签轴最小值
			chartLeft.setCategoryAxisMin(0);

			// 网格
			chartLeft.getPlotGrid().showHorizontalLines();
			chartLeft.getPlotGrid().showVerticalLines();
			chartLeft.getPlotGrid().getVerticalLinePaint().setColor(0xff444444);
			chartLeft.getPlotGrid().getHorizontalLinePaint()
					.setColor(0xff444444);
			chartLeft.getPlotGrid().getHorizontalLinePaint()
					.setStrokeWidth(0.2f);
			chartLeft.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.2f);

			// 把轴线和刻度线给隐藏起来
			chartLeft.getDataAxis().hideAxisLine();
			chartLeft.getDataAxis().hideTickMarks();
			chartLeft.getCategoryAxis().hideAxisLabels();
			chartLeft.getCategoryAxis().hideAxisLine();
			chartLeft.getCategoryAxis().showTickMarks();

			chartLeft.getDataAxis().getTickLabelPaint().setColor(Color.RED);

			float left = DensityUtil.dip2px(getContext(), 30); // left 40
			float right = DensityUtil.dip2px(getContext(), 30); // right 20
			chartLeft.setPadding(left, 10, right, 50); // ltrb[2]

			//激活点击监听
			chartLeft.ActiveListenItemClick();
			//为了让触发更灵敏，可以扩大5px的点击监听范围
			chartLeft.extPointClickRange(10);

			//显示平滑曲线
			chartLeft.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);

			//图例显示在正下方
			chartLeft.getPlotLegend().setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
			chartLeft.getPlotLegend().setHorizontalAlign(XEnum.HorizontalAlign.CENTER);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}

	protected void chartRightRender(double min,double max,double steps,List<String> labels) {
		try {

			// 轴数据源
			// 标签x轴
			chartRight.setCategories(labels);
			chartRight.getCategoryAxis().hide();
			// 数据轴
			chartRight.setDataSource(mDatasetRight);
			// 数据轴最大值
			chartRight.getDataAxis().setAxisMax(max);
			// 数据轴刻度间隔
			chartRight.getDataAxis().setAxisSteps(steps);

			chartRight.getDataAxis().setAxisMin(min);

			float left = DensityUtil.dip2px(getContext(), 30); // left 40
			float right = DensityUtil.dip2px(getContext(), 30); // right 20
			chartRight.setPadding(left, 10, right, 50); // ltrb[2]

			// 仅横向平移
			chartRight.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

			chartRight.getDataAxis().hideAxisLine();
			chartRight.getDataAxis().hideTickMarks();
			chartRight.getCategoryAxis().hideAxisLine();
			chartRight.getCategoryAxis().hideTickMarks();

			chartRight.getDataAxis().getTickLabelPaint().setColor(0xff999999);
			chartRight.getDataAxis().setHorizontalTickAlign(Align.RIGHT);
			chartRight.getDataAxis().getTickLabelPaint()
					.setTextAlign(Align.LEFT);

			chartRight.setAreaAlpha(200);

			// 调整轴显示位置
			chartRight.setDataAxisLocation(XEnum.AxisLocation.RIGHT);
			chartRight.setCategoryAxisLocation(XEnum.AxisLocation.TOP);

			// 图例显示在正下方
			chartRight.getPlotLegend().setVerticalAlign(
					XEnum.VerticalAlign.BOTTOM);
			chartRight.getPlotLegend().setHorizontalAlign(
					XEnum.HorizontalAlign.CENTER);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void render(Canvas canvas) {
		try {
			chartX.render(canvas);
			chartRight.render(canvas);
			chartLeft.render(canvas);


		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

}
