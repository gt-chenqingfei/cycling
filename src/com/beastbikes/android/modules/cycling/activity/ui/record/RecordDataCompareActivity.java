package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.ChartDataCompareItemView;
import com.beastbikes.android.modules.cycling.activity.ui.record.widget.CustomVerticalLineView;
import com.beastbikes.android.widget.NumberTextView;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 数据分析
 */
public class RecordDataCompareActivity extends SessionFragmentActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private AppCompatSeekBar mSeekBar;

    private CustomVerticalLineView mCustomVerticalLineView;

    private CheckedTextView mCheckedTVSpeed;
    private CheckedTextView mCheckedTVSlope;
    private CheckedTextView mCheckedTVHeartRate;
    private CheckedTextView mCheckedTVCadence;

    /**
     * 速度
     */
    private ChartDataCompareItemView mSpeed;
    /**
     * 爬坡
     */
    private ChartDataCompareItemView mSlope;
    /**
     * 心率
     */
    private ChartDataCompareItemView mHeartRate;
    /**
     * 踏频
     */
    private ChartDataCompareItemView mCadence;

    private NumberTextView mNumTVDistance;
    private TextView mTVDistanceUnit;

    private int mWindowWidth = 0;

    private int checkedCount = 4;

    private ChartDataProvider mChartDataProvider;

    private DecimalFormat mDecimalFormat;
    private DecimalFormat mDecimalFormatI;

    private float indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_data_compare);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mWindowWidth = getWindowManager().getDefaultDisplay().getWidth();
        mChartDataProvider = ChartDataProvider.getInstatnce();

        //69为左侧margin,25为右侧margin
        indicator = (mWindowWidth - Utils.convertDpToPixel(69 + 25)) / (mChartDataProvider.getSpeedEntries().size() - 1);
        mDecimalFormat = new DecimalFormat("0.0");
        mDecimalFormatI = new DecimalFormat("0");
        initView();
    }

    private void initView() {
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.seekBar_record_data_compare);
        mSeekBar.setMax(mChartDataProvider.getSpeedEntries().size() - 1);

        mCustomVerticalLineView = (CustomVerticalLineView) findViewById(R.id.custom_vertical_line);

        mCheckedTVSpeed = (CheckedTextView) findViewById(R.id.checked_tv_speed);
        mCheckedTVSlope = (CheckedTextView) findViewById(R.id.checked_tv_slope);
        mCheckedTVHeartRate = (CheckedTextView) findViewById(R.id.checked_tv_heart_rate);
        mCheckedTVCadence = (CheckedTextView) findViewById(R.id.checked_tv_cadence);

        mSpeed = (ChartDataCompareItemView) findViewById(R.id.chart_data_compare_speed);
        mHeartRate = (ChartDataCompareItemView) findViewById(R.id.chart_data_compare_heart_rate);
        mCadence = (ChartDataCompareItemView) findViewById(R.id.chart_data_compare_cadence);
        mSlope = (ChartDataCompareItemView) findViewById(R.id.chart_data_compare_slope);

        mNumTVDistance = (NumberTextView) findViewById(R.id.tv_chart_data_compare_bottom_distance);
        mTVDistanceUnit = (TextView) findViewById(R.id.tv_chart_data_compare_bottom_unit);

        mNumTVDistance.setText("0.0");
        if (LocaleManager.isDisplayKM(this)) {
            mTVDistanceUnit.setText(R.string.label_distance_unit);
        } else {
            mTVDistanceUnit.setText(LocaleManager.LocaleString.activity_max_speed_unit);
        }

        this.setSpeedData();
        this.setSlopeData();
        this.setHeartRateData();
        this.setCadenceData();
        setData();

        setListener();
    }

    private void setSpeedData() {
        ArrayList<Entry> entries = new ArrayList<>();
        int size = mChartDataProvider.getSpeedEntries().size();
        float max = 100;
        for (int i = 0; i < size; i++) {
            float value = mChartDataProvider.getSpeedEntries().get(i).getY();
            entries.add(new Entry(i, value));
            if (value > max) {
                max = value;
            }
        }
        mSpeed.setYMaxValue(max);
        mSpeed.setData(entries);
    }

    private void setSlopeData() {
        ArrayList<Entry> entries = new ArrayList<>();
        float max = 5;
        int size = mChartDataProvider.getSlopeEntriesForCompare().size();
        for (int i = 0; i < size; i++) {
            float value = mChartDataProvider.getSlopeEntriesForCompare().get(i).getY();
            entries.add(new Entry(i, value));
            if (Math.abs(value) > max) {
                max = value;
            }
        }
        mSlope.setYMaxValue(max);
        mSlope.setYMinValue(-max);
        mSlope.setData(entries);
    }

    private void setHeartRateData() {
        if (mChartDataProvider.getActivityDTO().getMaxCardiacRate() <= 0) {
            mHeartRate.setData(null);
            mCheckedTVHeartRate.toggle();
            mHeartRate.setVisibility(View.GONE);
            checkedCount--;
            return;
        }
        int size = mChartDataProvider.getHeartRateLineEntries().size();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            entries.add(new Entry(i, mChartDataProvider.getHeartRateLineEntries().get(i).getY()));
        }
        mHeartRate.setData(entries);
    }

    private void setCadenceData() {
        if (mChartDataProvider.getActivityDTO().getMaxCadence() <= 0) {
            mCadence.setData(null);
            mCheckedTVCadence.toggle();
            mCadence.setVisibility(View.GONE);
            checkedCount--;
            return;
        }
        mCadence.setData(mChartDataProvider.getCadenceEntriesForCompare());
//        mCadence.setData(entries);
    }

    private void setData() {
        mSpeed.setValue("0.0");
        if (LocaleManager.isDisplayKM(this)) {
            mSpeed.setUnit(LocaleManager.LocaleString.activity_speed_unit);
        } else {
            mSpeed.setUnit(LocaleManager.LocaleString.activity_speed_unit_mph);
        }

        mSlope.setValue("0.0%");

        mHeartRate.setValue("0");
        mHeartRate.setUnit(getResources().getString(R.string.label_bpm));

        mCadence.setValue("0");
        mCadence.setUnit(getResources().getString(R.string.label_cadence_unit));
    }

    private void setListener() {
        mSeekBar.setOnSeekBarChangeListener(this);

        mCheckedTVSpeed.setOnClickListener(this);
        mCheckedTVSlope.setOnClickListener(this);
        mCheckedTVHeartRate.setOnClickListener(this);
        mCheckedTVCadence.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mCustomVerticalLineView.update(indicator * progress);

        mSpeed.setHighLightValue(progress);
        mSlope.setHighLightValue(progress);
        if (mChartDataProvider.getActivityDTO().getMaxCardiacRate() > 0) {
            mHeartRate.setHighLightValue(progress);
            mHeartRate.setValue(mDecimalFormatI.format(mChartDataProvider.getHeartRateLineEntries().get(progress).getY()));
        }
        if (mChartDataProvider.getActivityDTO().getMaxCadence() > 0) {
            mCadence.setHighLightValue(progress);
            mCadence.setValue(mDecimalFormatI.format(mChartDataProvider.getCadenceEntriesForCompare().get(progress).getY()));
        }

        mSpeed.setValue(mDecimalFormat.format(mChartDataProvider.getSpeedEntries().get(progress).getY()));
        mSlope.setValue(mDecimalFormat.format(mChartDataProvider.getSlopeEntriesForCompare().get(progress).getY()) + "%");
        mNumTVDistance.setText(mDecimalFormat.format(mChartDataProvider.getSpeedEntries().get(progress).getX() / (mChartDataProvider.getTotalSize() - 1) * mChartDataProvider.getmTotalDistance()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checked_tv_speed:
                if (mCheckedTVSpeed.isChecked()) {
                    if (checkedCount <= 1) {
                        return;
                    }
                    mSpeed.setVisibility(View.GONE);
                    checkedCount--;
                } else {
                    mSpeed.setVisibility(View.VISIBLE);
                    checkedCount++;
                }
                mCheckedTVSpeed.toggle();
                break;
            case R.id.checked_tv_slope:
                if (mCheckedTVSlope.isChecked()) {
                    if (checkedCount <= 1) {
                        return;
                    }
                    mSlope.setVisibility(View.GONE);
                    checkedCount--;
                } else {
                    mSlope.setVisibility(View.VISIBLE);
                    checkedCount++;
                }
                mCheckedTVSlope.toggle();
                break;
            case R.id.checked_tv_heart_rate:
                if (mCheckedTVHeartRate.isChecked()) {
                    if (checkedCount <= 1) {
                        return;
                    }
                    mHeartRate.setVisibility(View.GONE);
                    checkedCount--;
                } else {
                    if (mChartDataProvider.getActivityDTO().getMaxCardiacRate() <= 0) {
                        Toasts.show(this, R.string.toast_unrecord_cycling_data);
                        return;
                    }
                    mHeartRate.setVisibility(View.VISIBLE);
                    checkedCount++;
                }
                mCheckedTVHeartRate.toggle();
                break;
            case R.id.checked_tv_cadence:
                if (mCheckedTVCadence.isChecked()) {
                    if (checkedCount <= 1) {
                        return;
                    }
                    mCadence.setVisibility(View.GONE);
                    checkedCount--;
                } else {
                    if (mChartDataProvider.getActivityDTO().getMaxCadence() <= 0) {
                        Toasts.show(this, "未记录该数据");
                        return;
                    }
                    mCadence.setVisibility(View.VISIBLE);
                    checkedCount++;
                }
                mCheckedTVCadence.toggle();
                break;
        }
    }
}
