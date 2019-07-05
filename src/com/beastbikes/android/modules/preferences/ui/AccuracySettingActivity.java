package com.beastbikes.android.modules.preferences.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

@Alias("精度设置")
@LayoutResource(R.layout.accuracy_setting_activity)
public class AccuracySettingActivity extends BaseFragmentActivity implements
        Constants, OnClickListener {

    @IdResource(R.id.accuracy_setting_activity_mode_high)
    private TextView lblHigh;

    @IdResource(R.id.accuracy_setting_activity_mode_power_saving)
    private TextView lblPowerSaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.lblHigh.setOnClickListener(this);
        this.lblPowerSaving.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.update();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        final BeastBikes app = (BeastBikes) getApplication();

        switch (v.getId()) {
            case R.id.accuracy_setting_activity_mode_high:
                app.setAccuracySetting(MODE_SETTING_ACCURACY_HIGH);
                break;
            case R.id.accuracy_setting_activity_mode_power_saving:
                app.setAccuracySetting(MODE_SETTING_ACCURACY_POWER_SAVING);
                break;
            default:
                return;
        }

        this.update();
    }

    @SuppressLint("NewApi")
    private void update() {
        final BeastBikes app = (BeastBikes) getApplication();

        switch (app.getAccuracySetting()) {
            case MODE_SETTING_ACCURACY_HIGH:
                this.lblPowerSaving.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                this.lblHigh.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_setting_checked, 0);
                break;
            case MODE_SETTING_ACCURACY_POWER_SAVING:
            default:
                this.lblHigh.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                this.lblPowerSaving.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_setting_checked, 0);
                break;
        }
    }

}
