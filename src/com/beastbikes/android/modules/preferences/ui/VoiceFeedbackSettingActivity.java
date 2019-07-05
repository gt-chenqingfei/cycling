package com.beastbikes.android.modules.preferences.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

@Alias("语音提示页")
@LayoutResource(R.layout.voice_feedback_setting_activity)
public class VoiceFeedbackSettingActivity extends BaseFragmentActivity
        implements Constants {

    @IdResource(R.id.voice_feedback_setting_activity_master_switch)
    private Switch btnMaster;

    @IdResource(R.id.voice_feedback_setting_activity_distance_switch)
    private Switch btnDistance;

    @IdResource(R.id.voice_feedback_setting_activity_elapsed_time_switch)
    private Switch btnElapsedTime;

    @IdResource(R.id.voice_feedback_setting_activity_velocity_switch)
    private Switch btnVelocity;

    @IdResource(R.id.voice_feedback_setting_activity_calorie_switch)
    private Switch btnCalorie;

    @IdResource(R.id.voice_feedback_setting_activity_activity_state_switch)
    private Switch btnActivityState;

    @IdResource(R.id.voice_feedback_setting_activity_sub_items)
    private ViewGroup grpSubItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final BeastBikes app = (BeastBikes) getApplication();

        this.btnMaster.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean isChecked) {
                app.setVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_MASTER,
                        isChecked);
                grpSubItems.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        this.btnActivityState.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean isChecked) {
                app.setVoiceFeedbackEnabled(
                        MASK_SETTING_VOICE_FEEDBACK_ACTIVITY_STATE, isChecked);
            }
        });


        this.btnCalorie.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean isChecked) {
                app.setVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_CALORIE,
                        isChecked);
            }
        });


        this.btnDistance.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean isChecked) {
                app.setVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_DISTANCE,
                        isChecked);
            }
        });

        this.btnElapsedTime.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean isChecked) {
                app.setVoiceFeedbackEnabled(
                        MASK_SETTING_VOICE_FEEDBACK_ELAPSED_TIME, isChecked);
            }
        });

        this.btnVelocity.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean isChecked) {
                app.setVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_VELOCITY,
                        isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final BeastBikes app = (BeastBikes) getApplication();
        final boolean enabled = app
                .isVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_MASTER);

        if (enabled) {
            this.btnMaster.setChecked(true);
            this.grpSubItems.setVisibility(View.VISIBLE);
        } else {
            this.btnMaster.setChecked(false);
            this.grpSubItems.setVisibility(View.GONE);
        }

        this.btnActivityState
                .setChecked(app
                        .isVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_ACTIVITY_STATE));
        this.btnCalorie.setChecked(app
                .isVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_CALORIE));
        this.btnDistance.setChecked(app
                .isVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_DISTANCE));
        this.btnElapsedTime
                .setChecked(app
                        .isVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_ELAPSED_TIME));
        this.btnVelocity.setChecked(app
                .isVoiceFeedbackEnabled(MASK_SETTING_VOICE_FEEDBACK_VELOCITY));
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

}
