package com.beastbikes.android.modules.cycling.activity.ui.record;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.ui.record.model.HeartRateModel;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 骑行报告页数据生成
 * Created by secret on 16/10/12.
 */

public class ChartDataProvider {

    private ArrayList<Entry> mSpeedEntries = new ArrayList<>();
    private ArrayList<Entry> mElevationEntries = new ArrayList<>();
    private ArrayList<Entry> mHeartRateEntries = new ArrayList<>();
    private ArrayList<BarEntry> mCadenceEntries = new ArrayList<>();
    private ArrayList<PieEntry> mSlopeEntries = new ArrayList<>();
    private ArrayList<PieEntry> mHeartRatePieEntries = new ArrayList<>();
    private ArrayList<Entry> mSlopeEntriesForCompare = new ArrayList<>();
    private ArrayList<Entry> mCadenceEntriesForCompare = new ArrayList<>();
    private float mAverageElevation;
    private float mAverageHeartRate;
    private int mAverageCadence;
    private double mRiseTotal;
    private double mUphillDistance;
    private double[] mSlopeDistances = new double[3];
    private int[] mSlopePercent = new int[3];
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private int[] mHeartRateLabel = new int[]{R.string.label_heart_rate_recovery_area, R.string.label_heart_rate_burning_fat_area, R.string.label_heart_rate_train_area, R.string.label_heart_rate_anaerobic_area, R.string.label_heart_rate_limit_area};
    private double mTotalDistance;
    private int totalSize;

    private static ChartDataProvider instance;

    private ActivityDTO mActivityDTO;

    private ChartDataProvider() {}

    public static ChartDataProvider getInstatnce() {
        if (null == instance) {
            instance = new ChartDataProvider();
        }
        return instance;
    }

    public void setActivityDTO(ActivityDTO activityDTO) {
        this.mActivityDTO = activityDTO;
    }

    public ActivityDTO getActivityDTO() {
        return this.mActivityDTO;
    }

    public void notifyData(double totalDistance, List<SampleDTO> sampleDTOs) {
        this.generateCadenceData(sampleDTOs);
    }

    /**
     * 速度
     * @param distances
     * @param velocities
     */
    public void notifyVelocities(List<Double> distances, List<Double> velocities) {
        int size = Math.min(distances.size(), velocities.size());
        mSpeedEntries.clear();
        for (int i = 0; i < size; i++) {
            mSpeedEntries.add(new Entry(i, velocities.get(i).floatValue()));
//            mSpeedEntries.add(new Entry(distances.get(i).floatValue(), velocities.get(i).floatValue()));
        }
    }

    /**
     * 海拔
     * @param distances
     * @param altitudes
     */
    public void notifyElevations(List<Double> distances, List<Double> altitudes) {
        int size = Math.min(distances.size(), altitudes.size());
        mElevationEntries.clear();
        float sum = 0;
        for (int i = 0; i < size; i++) {
            sum += altitudes.get(i).floatValue();
            mElevationEntries.add(new Entry(i, altitudes.get(i).floatValue()));
        }
        mAverageElevation = sum / size;

        mSlopeEntriesForCompare.clear();
        mSlopeEntriesForCompare.add(new Entry(0, 0));
        double dis = mActivityDTO.getTotalDistance() * 1000 / size;
        mRiseTotal = mActivityDTO.getRiseTotal();
        mUphillDistance = mActivityDTO.getUphillDistance();
        double riseTotal = 0;
        double uphillDistance = 0;
        for (int i = 1; i < size; i++) {
            double altitude = altitudes.get(i) - altitudes.get(i - 1);
            double angle = altitude / dis;
//            double angle = Math.atan2(altitude, dis);
//            angle = 180 * angle / Math.PI; //转换为角度值
            mSlopeEntriesForCompare.add(new Entry(i, (float) (angle * 100)));

            if (altitude > 0) {
                riseTotal += altitude;
                uphillDistance += dis;
            }
        }

        if (mRiseTotal <= 0 ) {
            mRiseTotal = riseTotal;
        }
        if (mUphillDistance <= 0) {
            mUphillDistance = uphillDistance;
        }
    }

    /**
     * 心率
     * @param distances
     * @param heartRates
     */
    public void notifyHeartRate(List<Double> distances, List<Double> heartRates) {
        int size = heartRates.size();
        mHeartRateEntries.clear();
        float sum = 0;
        for (int i = 0; i < size; i++) {
            sum += heartRates.get(i).floatValue();
            mHeartRateEntries.add(new Entry(i, heartRates.get(i).floatValue()));
        }
        mAverageHeartRate = sum / size;
    }

    /**
     * 心率
     * @param totalTime
     * @param maxCardiacRate
     * @param heartRates
     */
    public void notifyHeartRatePieData(long totalTime, int maxCardiacRate, List<Double> heartRates) {
        int recovery = 0, burningFat = 0, train = 0, anaerobic = 0, limit = 0;

        int i0 = 0, i1 = 0, i2 = 0, i3 = 0, i4 = 0;
        if (maxCardiacRate > 0) {
            recovery = (int) Math.ceil(maxCardiacRate * 0.6);
            burningFat = (int) Math.ceil(maxCardiacRate * 0.7);
            train = (int) Math.ceil(maxCardiacRate * 0.8);
            anaerobic = (int) Math.ceil(maxCardiacRate * 0.9);
        }

        for (double cardiacRate : heartRates) {
            if (cardiacRate < recovery) {
                i0++;
            } else if (cardiacRate >= recovery && cardiacRate < burningFat) {
                i1++;
            } else if (cardiacRate >= burningFat && cardiacRate < train) {
                i2++;
            } else if (cardiacRate >= train && cardiacRate < anaerobic) {
                i3++;
            } else if (cardiacRate >= anaerobic) {
                i4++;
            }
        }

        mHeartRatePieEntries.clear();
        int size = heartRates.size();
        int[] arr = new int[] {i0, i1, i2, i3, i4};
        int nowTotalPercent = 0;
        for (int i = 0; i < 4; i++) {
            int percent = (int) (((float) arr[i] / size) * 100);
            HeartRateModel model = new HeartRateModel(totalTime * percent, mHeartRateLabel[i], percent);
            mHeartRatePieEntries.add(new PieEntry(percent, model));
            nowTotalPercent += percent;
        }

        int percent = 100 - nowTotalPercent;
        HeartRateModel model = new HeartRateModel(totalTime * percent, mHeartRateLabel[4], percent);
        mHeartRatePieEntries.add(new PieEntry(percent, model));
    }

    /**
     * 踏频柱状图
     * @param sampleDTOs
     */
    public void generateCadenceData(List<SampleDTO> sampleDTOs) {
        if (null == sampleDTOs || sampleDTOs.isEmpty()) {
            return;
        }

        double sum = 0;
        int i1 = 0, i2 = 0, i3 = 0, i4 = 0, i5 = 0, i6 = 0, i7 = 0, i8 = 0, i9 = 0, i10 = 0, i11 = 0, i12 = 0;
        for (SampleDTO sample : sampleDTOs) {
            double cadence = sample.getCadence();
            sum += cadence;
            if (cadence < 10) {
                i1++;
            } else if (cadence >= 10 && cadence < 20) {
                i2++;
            } else if (cadence >= 20 && cadence < 30) {
                i3++;
            } else if (cadence >= 30 && cadence < 40) {
                i4++;
            } else if (cadence >= 40 && cadence < 50) {
                i5++;
            } else if (cadence >= 50 && cadence < 60) {
                i6++;
            } else if (cadence >= 60 && cadence < 70) {
                i7++;
            } else if (cadence >= 70 && cadence < 80) {
                i8++;
            } else if (cadence >= 80 && cadence < 90) {
                i9++;
            } else if (cadence >= 90 && cadence < 100) {
                i10++;
            } else if (cadence >= 100 && cadence < 110) {
                i11++;
            } else {
                i12++;
            }
        }
        mCadenceEntries.clear();
        int[] arr = new int[] {i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12};
        int size = sampleDTOs.size();
        for (int i = 0; i < 12; i++) {
            mCadenceEntries.add(new BarEntry(i, ((float) arr[i]) / size * 100));
        }

        mAverageCadence = (int) Math.ceil(sum / size);
    }

    /**
     * 数据分析踏频
     * @param cadences
     */
    public void notifyCadences(List<Double> cadences) {
        mCadenceEntriesForCompare.clear();
        int size = cadences.size();
        for (int i = 0; i < size; i++) {
            mCadenceEntriesForCompare.add(new Entry(i, cadences.get(i).floatValue()));
        }
    }

    /**
     * 爬坡
     * @param totalDistance
     * @param sampleDTOs
     */
    public void notifySlopeData(double totalDistance, List<SampleDTO> sampleDTOs) {
        if (null == sampleDTOs || sampleDTOs.isEmpty()) {
            return;
        }

        //顺序:上坡,平路,下坡
        int i1 = 0, i2 = 0, i3 = 0;
        int size = sampleDTOs.size();
        double distance = totalDistance / size * 1000;
        for (int i = 1; i < size; i++) {

            double altitude = sampleDTOs.get(i).getAltitude() - sampleDTOs.get(i - 1).getAltitude();
//            相对直角三角形对角的角，其中 x 是临边边长，而 y 是对边边长。
            double angle = Math.atan2(Math.abs(altitude), distance);
            angle = 180 * angle / Math.PI; //转换为角度值
            if (angle >= 0.5 && altitude > 0) {
                i1++;  //上坡
            } else if (angle >= 0.5 && altitude < 0) {
                i3++;  //下坡
            } else {
                i2++;  //平路
            }
        }

        int[] arr = new int[] {i1, i2, i3};

        mSlopeEntries.clear();
        for (int i = 0; i < 2; i++) {
            int percent = (int) (((float) arr[i] / size) * 100);
            mSlopePercent[i] = percent;
            mSlopeEntries.add(new PieEntry(percent));
        }
        mSlopePercent[2] = 100 - mSlopePercent[0] - mSlopePercent[1];
        mSlopeEntries.add(new PieEntry(100f - mSlopePercent[0] - mSlopePercent[1]));

        for (int i = 0; i < 3; i++) {
            mSlopeDistances[i] = totalDistance * mSlopePercent[i] / 100;
        }

    }

    public ArrayList<Entry> getSpeedEntries() {
        return mSpeedEntries;
    }

    public ArrayList<Entry> getElevationEntries() {
        return mElevationEntries;
    }

    /**
     * 获取踏频
     * @return
     */
    public ArrayList<BarEntry> getCadenceEntries() {
        return mCadenceEntries;
    }

    /**
     * 获取平均海拔
     * @return
     */
    public float getAverageElevation() {
        return mAverageElevation;
    }

    /**
     * 获取爬坡数据
     * @return
     */
    public ArrayList<PieEntry> getSlopeEntries() {
        return mSlopeEntries;
    }

    /**
     * 获取上坡,平路,下坡距离
     * @return
     */
    public double[] getSlopeDistances() {
        return mSlopeDistances;
    }

    /**
     * 获取上坡,平路,下坡百分比
     * @return
     */
    public int[] getSlopeDPercent() {
        return mSlopePercent;
    }

    /**
     * 获取心率曲线数据
     * @return
     */
    public ArrayList<Entry> getHeartRateLineEntries() {
        return mHeartRateEntries;
    }

    /**
     * 获取平均心率
     * @return
     */
    public float getAverageHeartRate() {
        return mAverageHeartRate;
    }

    /**
     * 获取心率区间数据
     * @return
     */
    public ArrayList<PieEntry> getHeartRateEntries() {
        return mHeartRatePieEntries;
    }

    /**
     * 获取数据分析坡度曲线数据
     * @return
     */
    public ArrayList<Entry> getSlopeEntriesForCompare() {
        return mSlopeEntriesForCompare;
    }

    /**
     * 获取数据分析踏频曲线数据
     * @return
     */
    public ArrayList<Entry> getCadenceEntriesForCompare() {
        return mCadenceEntriesForCompare;
    }

    /**
     * 获取平均踏频
     * @return
     */
    public float getAverageCadence() {
        return mAverageCadence;
    }

    /**
     * 获取总距离
     * @return
     */
    public double getmTotalDistance() {
        return mTotalDistance;
    }

    /**
     * 设置总里程
     * @param mTotalDistance
     */
    public void setmTotalDistance(double mTotalDistance) {
        this.mTotalDistance = mTotalDistance;
    }

    /**
     * 获取点数大小
     * @return
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * 设置点数大小
     * @param totalSize
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * 获取爬升海拔
     * @return
     */
    public double getRiseTotal() {
        return mRiseTotal;
    }

    /**
     * 获取爬升距离
     * @return
     */
    public double getUphillDistance() {
        return mUphillDistance;
    }
}
