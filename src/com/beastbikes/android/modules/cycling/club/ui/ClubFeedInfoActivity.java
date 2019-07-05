package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by chenqingfei on 15/12/7.
 */
@LayoutResource(R.layout.clubfeed_activity)
public class ClubFeedInfoActivity extends SessionFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        ActionBar abr = getSupportActionBar();
        if (null != abr) {
            abr.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {

            Bundle bundle = new Bundle();
            ClubInfoCompact clubInfoCompact = (ClubInfoCompact) intent.
                    getSerializableExtra(ClubFeedInfoFrag.EXTRA_CLUB_INFO);
            if (clubInfoCompact != null) {
                setTitle(clubInfoCompact.getName());
                bundle.putSerializable(ClubFeedInfoFrag.EXTRA_CLUB_INFO, clubInfoCompact);
            }

            String clubId = intent.getStringExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID);
            if (!TextUtils.isEmpty(clubId)) {
                bundle.putString(ClubFeedInfoFrag.EXTRA_CLUB_ID, clubId);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            ClubFeedInfoFrag fragment = new ClubFeedInfoFrag();
            fragment.setArguments(bundle);

            fragmentManager.beginTransaction().
                    add(R.id.frag_container, fragment).
                    commit();
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }
}