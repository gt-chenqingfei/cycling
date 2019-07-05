package com.beastbikes.android.modules.preferences.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.ui.CyclingSettingPageActivity;
import com.beastbikes.android.widget.materialdesign.MaterialRadioButton;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;


/**
 * Created by caoxiao on 16/3/9.
 */
@LayoutResource(R.layout.activity_cycling_setting)
public class CyclingSettingActivity extends SessionFragmentActivity implements View.OnClickListener, Switch.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, Constants {

    @IdResource(R.id.cycling_page_settings)
    private View stopWatchSettings;

    @IdResource(R.id.setting_fragment_item_voice_prompt)
    private View lblVoicePrompt;

    @IdResource(R.id.setting_fragment_item_auto_pause)
    private Switch lblAutoPause;

    @IdResource(R.id.activity_cycling_setting_miles_radiobutton)
    private MaterialRadioButton milesRadioButton;

    @IdResource(R.id.activity_cycling_setting_kilometer_radiobutton)
    private MaterialRadioButton kilometerRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        this.stopWatchSettings.setOnClickListener(this);
        this.lblVoicePrompt.setOnClickListener(this);
        this.lblAutoPause.setOnCheckedChangeListener(this);
        this.milesRadioButton.setOnCheckedChangeListener(this);
        this.kilometerRadioButton.setOnCheckedChangeListener(this);
        if (LocaleManager.isDisplayKM(CyclingSettingActivity.this)) {
            this.kilometerRadioButton.setChecked(true);
        } else {
            this.milesRadioButton.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final BeastBikes app = (BeastBikes) this.getApplication();
        this.lblAutoPause.setChecked(app.isAutoPauseEnabled());
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            SharedPreferences mPerferences = PreferenceManager
                    .getDefaultSharedPreferences(CyclingSettingActivity.this);
            switch (compoundButton.getId()) {
                case R.id.activity_cycling_setting_miles_radiobutton:
                    mPerferences.edit().putInt(KM_OR_MI, DISPLAY_MI).commit();
                    this.milesRadioButton.setIsClickable(false);
                    this.kilometerRadioButton.setIsClickable(true);
                    this.kilometerRadioButton.setChecked(false);
                    this.kilometerRadioButton.invalidate();
                    break;
                case R.id.activity_cycling_setting_kilometer_radiobutton:
                    mPerferences.edit().putInt(KM_OR_MI, DISPLAY_KM).commit();
                    this.kilometerRadioButton.setIsClickable(false);
                    this.milesRadioButton.setIsClickable(true);
                    this.milesRadioButton.setChecked(false);
                    this.milesRadioButton.invalidate();
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(Switch view, boolean open) {
        final BeastBikes app = (BeastBikes) getApplication();
        app.setAutoPauseEnabled(open);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cycling_page_settings:
                startActivity(new Intent(this, CyclingSettingPageActivity.class));
                break;
            case R.id.setting_fragment_item_voice_prompt:
                startActivity(new Intent(this, VoiceFeedbackSettingActivity.class));
                break;
        }
    }
}
