package com.beastbikes.android.modules.cycling.activity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by icedan on 15/12/25.
 */
@LayoutResource(R.layout.cycling_data_activity)
public class CyclingDataActivity extends SessionFragmentActivity {

    public static final String EXTRA_CYCLING = "cycling";

    @IdResource(R.id.cycling_data_max_altitude_value)
    private TextView maxAltitudeTv;
    @IdResource(R.id.cycling_data_rise_total_value)
    private TextView riseTotalTv;
    @IdResource(R.id.cycling_data_uphill_distance_value)
    private TextView uphillDistanceTv;
    @IdResource(R.id.cycling_data_max_speed_value)
    private TextView maxSpeedTv;
    @IdResource(R.id.cycling_data_speed_value)
    private TextView speedTv;
    @IdResource(R.id.cycling_data_start_time_value)
    private TextView startTimeTv;
    @IdResource(R.id.cycling_data_end_time_value)
    private TextView endTimeTv;
    @IdResource(R.id.cycling_data_calorie_value)
    private TextView calorieTv;
    @IdResource(R.id.cycling_data_max_altitude_unit)
    private TextView maxAltitudeUnitTv;
    @IdResource(R.id.cycling_data_rise_total_unit)
    private TextView riseTotalUnitTv;
    @IdResource(R.id.cycling_data_uphill_distance_unit)
    private TextView uphillDistanceUnitTv;
    @IdResource(R.id.cycling_data_max_speed_unit)
    private TextView maxSpeedUnitTv;
    @IdResource(R.id.cycling_data_speed_unit)
    private TextView speedUnitTv;

    @IdResource(R.id.cycling_data_cadence_max_unit)
    private TextView maxCadenceUnitTv;
    @IdResource(R.id.cycling_data_cardiac_rate_max_unit)
    private TextView maxCardiacRateUnitTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }

        ActivityDTO data = (ActivityDTO) intent.getSerializableExtra(EXTRA_CYCLING);
        if (null != data) {
            this.refreshView(data);
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    private void refreshView(ActivityDTO dto) {
        if (LocaleManager.isDisplayKM(CyclingDataActivity.this)) {
            this.maxAltitudeTv.setText(String.format("%.2f", dto.getMaxAltitude()));
            this.riseTotalTv.setText(String.format("%.2f", dto.getRiseTotal()));
            this.uphillDistanceTv.setText(String.format("%.2f", dto.getUphillDistance()));
            this.maxSpeedTv.setText(String.format("%.2f", dto.getMaxVelocity()));
            this.speedTv.setText(String.format("%.2f", dto.getVelocity()));
            this.startTimeTv.setText(DateFormatUtil.formatHms(dto.getStartTime()));
            this.endTimeTv.setText(DateFormatUtil.formatHms(dto.getStopTime()));
            this.calorieTv.setText(String.format("%.2f", dto.getCalories()));

        } else {
            this.maxAltitudeTv.setText(String.format("%.2f", LocaleManager.metreToFeet(dto.getMaxAltitude())));
            this.maxAltitudeUnitTv.setText(LocaleManager.LocaleString.activity_rise_total_unit);
            this.riseTotalTv.setText(String.format("%.2f", LocaleManager.metreToFeet(dto.getRiseTotal())));
            this.riseTotalUnitTv.setText(LocaleManager.LocaleString.activity_rise_total_unit);
            this.uphillDistanceTv.setText(String.format("%.2f", LocaleManager.metreToFeet(dto.getUphillDistance())));
            this.uphillDistanceUnitTv.setText(LocaleManager.LocaleString.activity_rise_total_unit);
            this.maxSpeedTv.setText(String.format("%.2f", LocaleManager.kphToMph(dto.getMaxVelocity())));
            this.maxSpeedUnitTv.setText(LocaleManager.LocaleString.activity_max_speed_unit);
            this.speedTv.setText(String.format("%.2f", LocaleManager.kphToMph(dto.getVelocity())));
            this.speedUnitTv.setText(LocaleManager.LocaleString.activity_max_speed_unit);
            this.startTimeTv.setText(DateFormatUtil.formatHms(dto.getStartTime()));
            this.endTimeTv.setText(DateFormatUtil.formatHms(dto.getStopTime()));
            this.calorieTv.setText(String.format("%.2f", dto.getCalories()));
        }

        double maxCadence = Double.isNaN(dto.getMaxCadence()) ? 0 : dto.getMaxCadence();
        double maxCardiacRate = Double.isNaN(dto.getMaxCardiacRate()) ? 0 : dto.getMaxCardiacRate();
        maxCadenceUnitTv.setText(maxCadence + "  rpm");
        maxCardiacRateUnitTv.setText(maxCardiacRate + "  bpm");
    }

}
