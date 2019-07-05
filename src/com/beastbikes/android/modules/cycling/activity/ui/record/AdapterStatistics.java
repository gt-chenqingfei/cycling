package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.beastbikes.android.modules.cycling.activity.ui.record.widget.BaseCyclingChartView;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by chenqingfei on 15/12/2.
 */
class AdapterStatistics extends PagerAdapter {

    private ArrayList<BaseCyclingChartView> viewList = null;

    private ChartViewFactory chartViewFactory;

    AdapterStatistics(Context context) {

        chartViewFactory = new ChartViewFactory(context);
        //默认排序是:速度曲线、海拔曲线、坡度分布、踏频分布、心率曲线、心率区间统计图表

        viewList = chartViewFactory.getChartViewList();// 将要分页显示的View装入数组中
    }


    /**
     *
     * @param dto
     * @param samples 点
     * @param altitudes 海拔数据
     */
    public void notifyDataSetChanged(ActivityDTO dto, List<SampleDTO> samples, List<Double> altitudes, boolean isMine) {
        if (dto.getMaxCardiacRate() <= 0) {
            viewList.remove(3);  //移除心率曲线
        }
        //如果不是查看自己的报告页,且没有踏频和心率,则隐藏踏频和心率图表
        if (!isMine) {
            if (dto.getMaxCadence() <= 0) {
                viewList.remove(4); //踏频图表
            }
            if (dto.getMaxCardiacRate() <= 0) {
                viewList.remove(3);  //心率饼图(已经移除了心率曲线了,所以饼图为4)
            }
        }

        this.notifyDataSetChanged();

    }

    /**
     *
     * @param altitudes 海拔数据
     */
    public void notifyElevationDataChanged(List<Double> distances, List<Double> altitudes, double maxAltitude, double min){
        chartViewFactory.notifyElevationData(distances, altitudes, maxAltitude, min);
    }

    /**
     *
     * @param sampleDTOs 爬坡数据
     */
    public void notifySlopeDataChanged(double totalDistance, List<SampleDTO> sampleDTOs){
        chartViewFactory.notifySlopeData(totalDistance, sampleDTOs);
    }

    /**
     *
     * @param heartRates 心率数据
     */
    public void notifyHeartRateDataChanged(ActivityDTO dto, int limitHeartRate, List<Double> distances, List<Double> heartRates){
        chartViewFactory.notifyHeartRateLineData(dto, limitHeartRate, distances, heartRates);
    }

    /**
     * 数据分析踏频数据
     * @param cadences 数据分析踏频数据
     */
    public void notifyCadenceDataChanged(List<Double> cadences){
        chartViewFactory.notifyCadenceLineData(cadences);
    }

    /**
     * 骑行轨迹的所有点
     * @param samples
     */
    public void notifySamplesChanged(ActivityDTO dto, List<SampleDTO> samples, double maxVelocity, double maxDistance,
                                     List<Double> distances, List<Double> velocities){
        chartViewFactory.notifySampleData(dto, samples);
        chartViewFactory.notifySpeedData(maxVelocity, maxDistance, distances, velocities);
    }


    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
