package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.modules.cycling.ranking.ui.RankViewHolder;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.club_member_rank_activity)
public class ClubMemberRankActivity extends SessionFragmentActivity implements
        OnItemClickListener {

    public static final String EXTRA_CLUB_ID = "club_id";

    @IdResource(R.id.club_member_rank_listview)
    private ListView rankLv;

    private ClubManager clubManager;
    private ClubMemberRankAdapter adapter;
    private List<RankDTO> ranks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.clubManager = new ClubManager(this);
        this.adapter = new ClubMemberRankAdapter(ranks);
        this.rankLv.setAdapter(this.adapter);
        this.rankLv.setOnItemClickListener(this);
        SpeedxAnalytics.onEvent(this, "查看成员排行",null);
        final Intent intent = getIntent();
        if (null == intent)
            return;

        String clubId = intent.getStringExtra(EXTRA_CLUB_ID);
        this.getClubMembersList(clubId);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RankDTO rank = this.ranks.get(position);
        if (null == rank)
            return;

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, rank.getUserId());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, rank.getAvatarUrl());
        intent.putExtra(ProfileActivity.EXTRA_CITY, rank.getCity());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, rank.getRemarks());
        startActivity(intent);
    }

    /**
     * 获取成员排行
     *
     * @param clubId
     */
    private void getClubMembersList(final String clubId) {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RankDTO>>() {

                    @Override
                    protected List<RankDTO> doInBackground(String... params) {
                        try {
                            return clubManager.getClubMemberList(params[0],
                                    ClubManager.CLUB_MEMBER_ORDERBY_MILESTONE, 1, 50);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RankDTO> result) {
                        if (null == result || result.isEmpty()) {
                            return;
                        }

                        ranks.clear();
                        ranks.addAll(result);
                        adapter.notifyDataSetChanged();
                    }

                }, clubId);
    }

    private final class ClubMemberRankAdapter extends BaseAdapter {

        private final List<RankDTO> list;

        public ClubMemberRankAdapter(List<RankDTO> list) {
            this.list = list;
            if (!LocaleManager.isDisplayKM(ClubMemberRankActivity.this)) {
                isChineseVersion = false;
            }
        }

        private boolean isChineseVersion = true;

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final RankViewHolder vh;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.ranking_fragment_list_item, null);
                vh = new RankViewHolder(
                        convertView, isChineseVersion);
                convertView.setTag(vh);
            } else {
                vh = (RankViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));
            return convertView;
        }

    }

}
