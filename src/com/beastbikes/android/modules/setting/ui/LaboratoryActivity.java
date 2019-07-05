package com.beastbikes.android.modules.setting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.RelativeLayout;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by icedan on 16/1/19.
 */
@LayoutResource(R.layout.laboratory_activity)
public class LaboratoryActivity extends SessionFragmentActivity implements Constants, View.OnClickListener {

    @IdResource(R.id.laboratory_activity_grid_switch)
    private Switch gridSwitch;

    @IdResource(R.id.laboratory_activity_cycling_screen_on)
    private RelativeLayout cyclingScreenView;
    @IdResource(R.id.laboratory_activity_cycling_screen_switch)
    private Switch cyclingScreenSwitch;
    @IdResource(R.id.laboratory_activity_foreground_switch)
    private Switch foregroundSwitch;
    @IdResource(R.id.laboratory_activity_cycling_keepalive_suggest)
    private View keepAliveSuggest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final BeastBikes beast = (BeastBikes) BeastBikes.getInstance();
        this.gridSwitch.setChecked(beast.isMapStyleEnabled());
        this.gridSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                beast.setMapStyleEnabled(checked);
            }
        });

        this.cyclingScreenSwitch.setChecked(beast.isCyclingScreenOnEnable());
        this.cyclingScreenSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                beast.setCyclingScreenOnEnable(checked);
            }
        });

        this.foregroundSwitch.setChecked(beast.isForeGroundEnabled());
        this.foregroundSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                beast.setForeGroundEnabled(checked);
                if (checked) {
                    SpeedxAnalytics.onEvent(beast, "允许前台运行",null);
                } else {
                    SpeedxAnalytics.onEvent(beast, "关闭前台运行",null);
                }
            }
        });
        if (LocaleManager.isChineseTimeZone()) {
            keepAliveSuggest.setVisibility(View.VISIBLE);
            keepAliveSuggest.setOnClickListener(this);
        } else {
            keepAliveSuggest.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.laboratory_activity_cycling_keepalive_suggest) {
            this.startActivity(new Intent(this, KeepAliveHelperActivity.class));
        }
    }
}
