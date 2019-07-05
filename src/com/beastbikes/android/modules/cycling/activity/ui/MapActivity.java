package com.beastbikes.android.modules.cycling.activity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by caoxiao on 16/1/10.
 */
@LayoutResource(R.layout.layout_activity_map)
public class MapActivity extends SessionFragmentActivity implements MapFragment.OnMapActivtyFinishListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.map_activity_container, new MapFragment());
        ft.commit();
    }

    @Override
    public void finishMapActivity() {
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_bottom_and_alpha);
    }
}


