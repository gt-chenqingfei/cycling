package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubActivityManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityInfoList;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityListDTO;
import com.beastbikes.android.modules.cycling.club.ui.widget.ClubActivityOntListFragment;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

import java.util.List;

@LayoutResource(R.layout.activity_club_activities_list)
public class ClubActivitiesListActivity extends SessionFragmentActivity {

    public static final int REQUEST_REFRESH_LIST_CODE = 14;
    public static final String CLUBACTIVITY_INFOS = "data";
    public static final String IS_MYCLUB = "isclub";

    private ClubActivityManager clubActivityManager;
    private String clubId;
    private FragmentManager fragmentManager;
    private boolean isMyClub = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        clubActivityManager = new ClubActivityManager(this);
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.club_activity_list_title));
        }
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isMyClub && item.getItemId() == R.id.menu_item_add) {
            Intent intent = new Intent(this,
                    ClubActivityReleaseActivity.class);
            intent.putExtra(ClubActivitiesListActivity.IS_MYCLUB, isMyClub);
            this.startActivityForResult(intent, REQUEST_REFRESH_LIST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_REFRESH_LIST_CODE:
                        this.networkRequest();
                        break;
                }
                break;
        }
    }

    private void init() {
        clubId = getIntent().getStringExtra(ClubMemberRankActivity.EXTRA_CLUB_ID);
        isMyClub = getIntent().getBooleanExtra(IS_MYCLUB, false);
        fragmentManager = getSupportFragmentManager();
        this.networkRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMyClub)
            getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //获取俱乐部中活动列表
    private void networkRequest() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubActivityListDTO>>() {
            @Override
            protected List<ClubActivityListDTO> doInBackground(Void... params) {
                return clubActivityManager.clubActivityList(clubId, 1, ClubActivityListFragment.COUNT);
            }

            @Override
            protected void onPostExecute(List<ClubActivityListDTO> clubActivityInfos) {
                if (isFinishing()) {
                    return;
                }
                if (clubActivityInfos != null && !clubActivityInfos.isEmpty()) {
                    ClubActivityListFragment fragment = new ClubActivityListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ClubMemberRankActivity.EXTRA_CLUB_ID, clubId);
                    bundle.putBoolean(IS_MYCLUB, isMyClub);
                    bundle.putBoolean(ClubActivityListFragment.CLUB_MEMBER_HAS_FOOTER, clubActivityInfos.size() == ClubActivityListFragment.COUNT);
                    bundle.putSerializable(CLUBACTIVITY_INFOS, new ClubActivityInfoList(clubActivityInfos));
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().
                            replace(R.id.activity_club_activities_list_fragment,
                                    fragment).commitAllowingStateLoss();
                } else if (clubActivityInfos != null && clubActivityInfos.isEmpty()) {
                    ClubActivityOntListFragment fragment = new ClubActivityOntListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(IS_MYCLUB, isMyClub);
                    bundle.putString(ClubMemberRankActivity.EXTRA_CLUB_ID, clubId);
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().
                            replace(R.id.activity_club_activities_list_fragment,
                                    fragment).commitAllowingStateLoss();
                }
            }
        });
    }

}

