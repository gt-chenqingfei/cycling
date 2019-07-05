package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.LinearLayout;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by caoxiao on 16/3/28.
 */
@LayoutResource(R.layout.activity_club_discovery_activity)
public class ClubDiscoverActivity extends SessionFragmentActivity {

    private FragmentManager fragmentManager;
    public static String ClUB_DIS_FRAG_MENU_SHOW = "club_disfragment_menu_show";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra(ClUB_DIS_FRAG_MENU_SHOW, true)) {
            ClubDiscoverFrag.SHOW_MENU = true;
        } else {
            ClubDiscoverFrag.SHOW_MENU = false;
        }
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new ClubDiscoverFrag();
        fragmentManager.beginTransaction().
                add(R.id.club_discovery_frag_container,
                        fragment).commitAllowingStateLoss();

    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }
}
