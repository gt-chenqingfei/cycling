package com.beastbikes.android.modules.cycling.club.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by icedan on 15/12/10.
 */
@LayoutResource(R.layout.club_upload_image_activity)
public class ClubUploadImageActivity extends SessionFragmentActivity implements SwipeRefreshLayout.OnRefreshListener {

    @IdResource(R.id.club_upload_image_refresh)
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onRefresh() {

    }
}
