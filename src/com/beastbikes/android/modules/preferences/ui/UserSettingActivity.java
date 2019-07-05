package com.beastbikes.android.modules.preferences.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

@Alias("个人设置页")
@LayoutResource(R.layout.user_setting_activity)
public class UserSettingActivity extends SessionFragmentActivity {

    public static final String EXTRA_FROM_SETTING = "from_setting";

    public static final String EXTRA_FROM_AUTH = "from_auth";


    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();

        if (null == bar) {
            return;
        }
        Intent intent = getIntent();
        if (intent.hasExtra(UserSettingActivity.EXTRA_FROM_SETTING)
                && intent.getBooleanExtra(UserSettingActivity.EXTRA_FROM_SETTING, false)) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Fragment fragment = Fragment.instantiate(this, UserSettingFragment.class.getName());
        getSupportFragmentManager().beginTransaction().
                add(R.id.user_setting_activity_fragment_container, fragment).
                commit();

    }



}
