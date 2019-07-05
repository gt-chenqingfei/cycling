package com.beastbikes.android.modules.cycling.route.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.StringResource;

@Alias("精品路线")
@StringResource(R.string.routes_fragment_title)
@LayoutResource(R.layout.frag_container)
public class RoutesActivity extends SessionFragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        ActionBar abr = getSupportActionBar();
        abr.setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        RoutesFrag fragment = new RoutesFrag();

        fragmentManager.beginTransaction().
                add(R.id.frag_container, fragment).
                commit();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

}
