package com.beastbikes.android.modules.cycling.activity.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.AreaData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineData;
import org.xclcharts.renderer.XEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chenqingfei on 16/5/3.
 */
public class SpeedAltitudeGraph extends SpeedAltitudeGraphBase {


    public SpeedAltitudeGraph(Context context) {
        super(context);
    }

    public SpeedAltitudeGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeedAltitudeGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    private double stepCount =0;

    /**
     * 画速度曲线
     */
    public void drawSpeedGraph(double maxVelocity, double maxDistance,
                               List<Double> distances, List<Double> velocities) {

        drawXGraph(maxVelocity, maxDistance, distances, velocities);
        List<String> laybels = new ArrayList<>();

        for (int i = 0; i < distances.size(); i++) {
            laybels.add(String.format("%.1f", distances.get(i)) + "");
        }

        if (laybels.size() != velocities.size()) {
            return;
        }

        List<PointD> pointDs = new ArrayList<>();
        int size = laybels.size();
        for (int i = 0; i < size; i++) {
            PointD pointD = new PointD(i, velocities.get(i));
            pointDs.add(pointD);
        }

        SplineData lineLeft = new SplineData("", pointDs, Color.RED);

//        lineLeft.setApplayGradient(false);
        lineLeft.getLinePaint().setStrokeWidth(3);
//        lineLeft.setGradientMode(Shader.TileMode.MIRROR);
        lineLeft.setDotStyle(XEnum.DotStyle.HIDE);

        mDatasetLeft.clear();
        mDatasetLeft.add(lineLeft);
        int step = 10;
        if(maxVelocity >= 100){
            step = 20;
        }
        if (maxVelocity < 15) {
            step = 2;
        }
        if(maxVelocity < 8){
            step = 1;
        }
        if(maxVelocity <1 && maxVelocity >0.5){
            step =2;
        }

        double max = 0;
        if (maxVelocity % step == 0) {
            max = maxVelocity + step;
        } else {
            max = ((int) (maxVelocity + step) / step) * step;
        }

        stepCount = max/step;

//        Log.d("m",count+"");

        chartLeftRender(max, 0, step, size - 1, laybels);
        invalidate();
    }


    /**
     * 画X轴
     */
    public void drawXGraph(double maxVelocity, double maxDistance,
                           List<Double> distances, List<Double> velocities) {
        double n = maxDistance / 10;
        List<Double> mVelocities = new ArrayList<Double>();
        List<String> laybels = new ArrayList<>();

        mVelocities.addAll(distances.subList(0, 9));

        laybels.add(0.0 + "");
        for (int i = 1; i < 9; i++) {
            laybels.add(String.format("%.1f", i * n) + "");
        }
        laybels.add(String.format("%.2f", maxDistance) + "");


        AreaData lineLeft = new AreaData("", mVelocities, Color.TRANSPARENT, Color.TRANSPARENT);

        lineLeft.setApplayGradient(false);
        lineLeft.setGradientMode(Shader.TileMode.MIRROR);
        lineLeft.setDotStyle(XEnum.DotStyle.HIDE);
        mDatasetX.clear();
        mDatasetX.add(lineLeft);
        int step = 10;
        if(maxVelocity>50){
            step = 20;
        }
        if (maxVelocity < 15) {
            step = 2;
        }
        if(maxVelocity < 8){
            step = 1;
        }
        if(maxVelocity <1 && maxVelocity >0.5){
            step =2;
        }
        double max = 0;
        if (maxVelocity % 10 == 0) {
            max = maxVelocity;
        } else {
            max = maxVelocity + step;
        }
        chartXRender(max, 0, step, laybels);
        invalidate();
    }

    /**
     * 画海拔曲线
     */
    public void drawAltitudeGraph(List<Double> altitudes, double maxAltitude,double min) {

        List<String> lables = new ArrayList<>();
        for (int i = 0; i < altitudes.size(); i++) {
            if(altitudes.get(i)<-1000)
                return;
            lables.add(String.format("%.1f", altitudes.get(i)) + "");
        }

        AreaData lineRight = new AreaData("", altitudes, Color.RED, 0xff999999);
        // 设置点标签
        lineRight.setLabelVisible(false);
        lineRight.setLineColor(Color.TRANSPARENT);
        lineRight.setAreaFillColor(0x444444);
        lineRight.setApplayGradient(false);
        lineRight.setGradientMode(Shader.TileMode.MIRROR);
        lineRight.setDotStyle(XEnum.DotStyle.HIDE);
        mDatasetRight.clear();
        mDatasetRight.add(lineRight);


        int step = 1;

        int max = (int)Math.ceil(maxAltitude);

        step = (int)(max/Math.floor(stepCount));
        min = Math.min(0,(int)Math.floor(min));

        chartRightRender(min,max*2, step*2, lables);
        invalidate();
    }
}
