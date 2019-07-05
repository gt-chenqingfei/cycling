package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.activity.ui.record.model.HeartRateModel;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.BaseCyclingChartView;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.CadenceBarChartView;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.FourItemsLineChartView;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.HeartPieChartView;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.SlopePieChartView;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.TwoItemsLineChartView;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by secret on 16/10/12.
 */

class ChartViewFactory {

    private Context mContext;

    private FourItemsLineChartView mElevationLineChartView;
    private TwoItemsLineChartView mSpeedLineChartView;
    private TwoItemsLineChartView mHeartLineChartView;
    private HeartPieChartView mHeartPieChartView;
    private SlopePieChartView mSlopePieChartView;
    private CadenceBarChartView mCadenceBarChartView;

    private ActivityDTO mActivityDTO;

    private ChartDataProvider mChartDataProvider;

    private DecimalFormat mDecimalFormat;
    private DecimalFormat mDecimalFormatI;

    ChartViewFactory(Context context) {
        this.mContext = context;
        this.mChartDataProvider = ChartDataProvider.getInstatnce();

        mDecimalFormat = new DecimalFormat("0.0");
        mDecimalFormatI = new DecimalFormat("0");

        mElevationLineChartView = new FourItemsLineChartView(mContext);
        mSpeedLineChartView = new TwoItemsLineChartView(mContext);
        mHeartLineChartView = new TwoItemsLineChartView(mContext);
        mHeartPieChartView = new HeartPieChartView(mContext);
        mSlopePieChartView = new SlopePieChartView(mContext);
        mCadenceBarChartView = new CadenceBarChartView(mContext);

    }

    public void notifySampleData(ActivityDTO activityDTO, List<SampleDTO> sampleDTOs) {
        if (null == activityDTO || null == sampleDTOs || sampleDTOs.isEmpty()) {
            return;
        }
        this.mChartDataProvider.setmTotalDistance(activityDTO.getTotalDistance());

        mChartDataProvider.notifyData(activityDTO.getTotalDistance(), sampleDTOs);

        this.mActivityDTO = activityDTO;
        mChartDataProvider.setActivityDTO(activityDTO);

        this.setDataForCadenceBarChartView();
    }

    /**
     * 刷新速度曲线
     *
     * @param distances
     * @param velocities
     */
    public void notifySpeedData(double maxVelocity, double maxDistance, List<Double> distances, List<Double> velocities) {
        if (null == distances || distances.isEmpty() || null == velocities || velocities.isEmpty()) {
            return;
        }

        this.mChartDataProvider.setTotalSize(velocities.size());
        mChartDataProvider.notifyVelocities(distances, velocities);

        this.setDataForSpeedLineChartView(velocities.size(), maxVelocity, maxDistance);
    }

    /**
     * 刷新海拔曲线
     *
     * @param altitudes
     * @param maxAltitude
     * @param minAltitude
     */
    public void notifyElevationData(List<Double> distances, List<Double> altitudes, double maxAltitude, double minAltitude) {
        this.mChartDataProvider.notifyElevations(distances, altitudes);
        this.setDataForElevationChartView(maxAltitude, minAltitude);
    }

    /**
     * 刷新海拔曲线
     *
     * @param totalDistance
     * @param sampleDTOs
     */
    public void notifySlopeData(double totalDistance, List<SampleDTO> sampleDTOs) {
        this.mChartDataProvider.notifySlopeData(totalDistance, sampleDTOs);
        this.setDataForSlopePieChartView();
    }

    /**
     * 刷新心率曲线
     *
     * @param heartRates
     */
    public void notifyHeartRateLineData(ActivityDTO dto, int limitHeartRate, List<Double> distances, List<Double> heartRates) {
        this.mActivityDTO = dto;

        this.mChartDataProvider.notifyHeartRate(distances, heartRates);

        this.setDataForHeartLineChartView(limitHeartRate, (float) dto.getMaxCardiacRate(), (float) dto.getCardiacRate());

        this.mChartDataProvider.notifyHeartRatePieData((long) dto.getElapsedTime(), limitHeartRate, heartRates);
        this.setDataForHeartPieChartView(limitHeartRate);
    }

    /**
     * 刷新数据分析踏频曲线
     *
     * @param cadences
     */
    public void notifyCadenceLineData(List<Double> cadences) {

        this.mChartDataProvider.notifyCadences(cadences);
    }

    //默认排序是:速度曲线、海拔曲线、坡度分布、心率曲线、心率区间、踏频分布统计图表
    public ArrayList<BaseCyclingChartView> getChartViewList() {
        ArrayList<BaseCyclingChartView> baseCyclingChartViews = new ArrayList<>();
        baseCyclingChartViews.add(mSpeedLineChartView);
        baseCyclingChartViews.add(mElevationLineChartView);
        baseCyclingChartViews.add(mSlopePieChartView);
        baseCyclingChartViews.add(mHeartLineChartView);
        baseCyclingChartViews.add(mHeartPieChartView);
        baseCyclingChartViews.add(mCadenceBarChartView);
        return baseCyclingChartViews;
    }

    /**
     * 设置海拔曲线数据
     */
    public void setDataForElevationChartView(double maxAltitude, double minAltitude) {
        mElevationLineChartView.setLineColor(0xffaaaaaa);
        mElevationLineChartView.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_fade_gray));
        mElevationLineChartView.setChartName(mContext.getString(R.string.str_elevation));
        mElevationLineChartView.setNameDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chart_elevation));
        mElevationLineChartView.setXAxisUnit(mContext.getString(R.string.kilometre));

        mElevationLineChartView.setAverageLineValue(mChartDataProvider.getAverageElevation());
        mElevationLineChartView.setYMaxValue((float) maxAltitude * 1.3f);
        mElevationLineChartView.setYMinValue((float) minAltitude < 0 ? 0 : (float) minAltitude);
        mElevationLineChartView.setXMaxValue((float) mChartDataProvider.getElevationEntries().size() - 1);
        if (mActivityDTO != null) {
            mElevationLineChartView.setTotalDistance((float) mActivityDTO.getTotalDistance());
        }
        mElevationLineChartView.initChart();

        if (LocaleManager.isDisplayKM(mContext)) {
            String leftTopLabel = mContext.getString(R.string.label_max_altitude) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m;
            mElevationLineChartView.setLeftTopLabel(leftTopLabel.toUpperCase());
            String rightTopLabel = mContext.getString(R.string.label_rise_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m;
            mElevationLineChartView.setRightTopLabel(rightTopLabel.toUpperCase());
            String leftBottomLabel = mContext.getString(R.string.label_uphill_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m;
            mElevationLineChartView.setLeftBottomLabel(leftBottomLabel.toUpperCase());
        } else {
            String leftTopLabel = mContext.getString(R.string.label_max_altitude) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;
            mElevationLineChartView.setLeftTopLabel(leftTopLabel.toUpperCase());
            String rightTopLabel = mContext.getString(R.string.label_rise_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;
            mElevationLineChartView.setRightTopLabel(rightTopLabel.toUpperCase());
            String leftBottomLabel = mContext.getString(R.string.label_uphill_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;
            mElevationLineChartView.setLeftBottomLabel(leftBottomLabel.toUpperCase());
        }

        //最高海拔
        if (mActivityDTO != null) {
            mElevationLineChartView.setLeftTopValue(mDecimalFormatI.format(mActivityDTO.getMaxAltitude()));
        }
        //爬升海拔
        mElevationLineChartView.setRightTopValue(mDecimalFormatI.format(mChartDataProvider.getRiseTotal()));
        //爬升距离
        mElevationLineChartView.setLeftBottomValue(mDecimalFormatI.format(mChartDataProvider.getUphillDistance()));
        //卡路里
        String rightBottomLabel = mContext.getString(R.string.activity_param_label_calorie);
        mElevationLineChartView.setRightBottomLabel(rightBottomLabel.toUpperCase());
        if (mActivityDTO != null) {
            mElevationLineChartView.setRightBottomValue(mDecimalFormatI.format(mActivityDTO.getCalories()));
        }

        mElevationLineChartView.setData(mChartDataProvider.getElevationEntries());
    }

    /**
     * 设置速度曲线数据
     */
    public void setDataForSpeedLineChartView(int size, double maxVelocity, double maxDistance) {
        mSpeedLineChartView.setLineColor(0xff279c59);
        mSpeedLineChartView.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_fade_green));
        mSpeedLineChartView.setChartName(mContext.getString(R.string.voice_feedback_setting_activity_label_velocity));
        mSpeedLineChartView.setNameDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chart_speed));
        mSpeedLineChartView.setYMaxValue((float) (maxVelocity * 1.3f));
        mSpeedLineChartView.setXMaxValue(size - 1);
        mSpeedLineChartView.setXAxisUnit(mContext.getString(R.string.kilometre));
        if (mActivityDTO != null) {
            mSpeedLineChartView.setAverageLineValue((float) mActivityDTO.getVelocity());
            mSpeedLineChartView.setTotalDistance((float) mActivityDTO.getTotalDistance());
        }
        mSpeedLineChartView.initChart();

        if (LocaleManager.isDisplayKM(mContext)) {
            String leftLabel = mContext.getString(R.string.label_label_speed) +
                    LocaleManager.LocaleString.activity_param_label_velocity;
            mSpeedLineChartView.setLeftLabel(leftLabel.toUpperCase());
            String rightLabel = mContext.getString(R.string.label_max_speed) +
                    LocaleManager.LocaleString.activity_param_label_velocity;
            mSpeedLineChartView.setRightLabel(rightLabel.toUpperCase());
        } else {
            String leftLabel = mContext.getString(R.string.label_label_speed) +
                    LocaleManager.LocaleString.activity_param_label_velocity_mph;
            mSpeedLineChartView.setLeftLabel(leftLabel.toUpperCase());
            String rightLabel = mContext.getString(R.string.label_max_speed) +
                    LocaleManager.LocaleString.activity_param_label_velocity_mph;
            mSpeedLineChartView.setRightLabel(rightLabel.toUpperCase());
        }
        if (mActivityDTO != null) {
            //平均速度
            mSpeedLineChartView.setLeftValue(mDecimalFormat.format(mActivityDTO.getVelocity()));
            //最大速度
            mSpeedLineChartView.setRightValue(mDecimalFormat.format(mActivityDTO.getMaxVelocity()));
        }
        mSpeedLineChartView.setData(mChartDataProvider.getSpeedEntries());
    }

    /**
     * 设置心率曲线数据
     */
    public void setDataForHeartLineChartView(int limitHeartRate, float maxHeartRate, float averageHeartRate) {
        mHeartLineChartView.setLineColor(0xffc02222);
        mHeartLineChartView.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_fade_red));
        mHeartLineChartView.setChartName(mContext.getString(R.string.str_heart_rate));
        mHeartLineChartView.setNameDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chart_heart_rate));
        mHeartLineChartView.setXAxisUnit(mContext.getString(R.string.kilometre));
        mHeartLineChartView.setYMaxValue(maxHeartRate * 1.5f);
        mHeartLineChartView.setTotalDistance((float) mActivityDTO.getTotalDistance());
        mHeartLineChartView.setAverageLineValue(mChartDataProvider.getAverageHeartRate());
        mHeartLineChartView.initChart();

        if (mActivityDTO.getMaxCardiacRate() <= 0) {
            mHeartLineChartView.showNodataView();
            mHeartLineChartView.setData(null);
            return;
        }

        if (TextUtils.isEmpty(mActivityDTO.getSource())) {
            mHeartLineChartView.setDataSource(mContext.getString(R.string.str_label_apple_watch));
        } else {
            if (TextUtils.isEmpty(mActivityDTO.getCentralName())) {
                mHeartLineChartView.setDataSource(mContext.getString(R.string.str_label_third_part_device));
            } else {
                mHeartLineChartView.setDataSource(mActivityDTO.getCentralName());
            }
        }

        //平均心率
        String leftLabel = mContext.getString(R.string.label_heart_rate) + mContext.getString(R.string.label_bpm);
        mHeartLineChartView.setLeftLabel(leftLabel.toUpperCase());
        mHeartLineChartView.setLeftValue(mDecimalFormatI.format(mChartDataProvider.getAverageHeartRate()));
        //最大心率
        String rightLabel = mContext.getString(R.string.label_max_heart_rate) + mContext.getString(R.string.label_bpm);
        mHeartLineChartView.setRightLabel(rightLabel.toUpperCase());
        mHeartLineChartView.setRightValue(mDecimalFormatI.format(mActivityDTO.getMaxCardiacRate()));

        mHeartLineChartView.setAverageLineValue((float) mActivityDTO.getCardiacRate());
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < 40; i++) {

            float val = (float) (Math.random() * 100) + 3;
            values.add(new Entry(i, val));
        }
//        mHeartLineChartView.setData(values);
        mHeartLineChartView.setData(mChartDataProvider.getHeartRateLineEntries());
    }

    /**
     * 设置心率饼图数据
     */
    public void setDataForHeartPieChartView(int maxHeartRate) {
        mHeartPieChartView.initChart();
        mHeartPieChartView.setChartName(mContext.getString(R.string.str_heart_rate_zone));
        mHeartPieChartView.setNameDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chart_heart_rate));
        if (mActivityDTO.getMaxCardiacRate() <= 0) {
            mHeartPieChartView.setData(null);
            return;
        }
        mHeartPieChartView.setHeartRateMAxValue(maxHeartRate);
        if (TextUtils.isEmpty(mActivityDTO.getSource())) {
            mHeartPieChartView.setDataSource(mContext.getString(R.string.str_label_apple_watch));
        } else {
            if (TextUtils.isEmpty(mActivityDTO.getCentralName())) {
                mHeartPieChartView.setDataSource(mContext.getString(R.string.str_label_third_part_device));
            } else {
                mHeartPieChartView.setDataSource(mActivityDTO.getCentralName());
            }
        }

        ArrayList<PieEntry> entries = mChartDataProvider.getHeartRateEntries();
//        Collections.sort(entries, new MComparator());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
        PieEntry maxEntry = entries.get(0);
        for (PieEntry pieEntry : entries) {
            if (pieEntry.getValue() > maxEntry.getValue()) {
                maxEntry = pieEntry;
            }
        }
        HeartRateModel heartRateModel = (HeartRateModel) maxEntry.getData();
        mHeartPieChartView.setMaxPercentHeartRateData(heartRateModel.getLabel(), formatter.format(heartRateModel.getTime()), heartRateModel.getPercent() + "%");
//        mHeartPieChartView.setData(mChartDataProvider.getHeartRateEntries());
        mHeartPieChartView.setData(entries);
    }

    /**
     * 设置爬坡数据
     */
    public void setDataForSlopePieChartView() {
        mSlopePieChartView.initChart();
        mSlopePieChartView.setChartName(mContext.getString(R.string.str_slope));
        mSlopePieChartView.setNameDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chart_slope));

        int[] slopePercent = mChartDataProvider.getSlopeDPercent();
        mSlopePieChartView.setUpSlopePercent(slopePercent[0] + "%");
        mSlopePieChartView.setFlatRoadPercent(slopePercent[1] + "%");
        mSlopePieChartView.setDownSlopePercent(slopePercent[2] + "%");

        double[] slopeDistances = mChartDataProvider.getSlopeDistances();
        mSlopePieChartView.setUpSlopeAverageSpeed(String.format(Locale.CHINA, "%.1f", slopeDistances[0]) + "km");
        mSlopePieChartView.setFlatRoadAverageSpeed(String.format(Locale.CHINA, "%.1f", slopeDistances[1]) + "km");
        mSlopePieChartView.setDownSlopeAverageSpeed(String.format(Locale.CHINA, "%.1f", slopeDistances[2]) + "km");

        ArrayList<PieEntry> pieEntries = mChartDataProvider.getSlopeEntries();

        mSlopePieChartView.setData(pieEntries);
    }

    /**
     * 设置踏频数据
     */
    public void setDataForCadenceBarChartView() {
        mCadenceBarChartView.initChart();

        mCadenceBarChartView.setChartName(mContext.getString(R.string.label_cadence));
        mCadenceBarChartView.setNameDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_chart_cadence));

        if (TextUtils.isEmpty(mActivityDTO.getCentralId()) || TextUtils.isEmpty(mActivityDTO.getCentralName()) || mActivityDTO.getMaxCadence() <= 0) {
            mCadenceBarChartView.setData(null);
            return;
        }

        if (TextUtils.isEmpty(mActivityDTO.getSource())) {
            mCadenceBarChartView.setDataSource(mContext.getString(R.string.str_label_apple_watch));
        } else {
            if (TextUtils.isEmpty(mActivityDTO.getCentralName())) {
                mCadenceBarChartView.setDataSource(mContext.getString(R.string.str_label_third_part_device));
            } else {
                mCadenceBarChartView.setDataSource(mActivityDTO.getCentralName());
            }
        }

        mCadenceBarChartView.setLeftValue(mDecimalFormatI.format(mChartDataProvider.getAverageCadence()));
        mCadenceBarChartView.setRightValue(mDecimalFormatI.format(mActivityDTO.getMaxCadence()));

        mCadenceBarChartView.setData(mChartDataProvider.getCadenceEntries());
    }

}
