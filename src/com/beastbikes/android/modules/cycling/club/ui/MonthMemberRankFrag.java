package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.ranking.biz.RankingManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO2;
import com.beastbikes.android.modules.cycling.ranking.ui.RankingAdapter2;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 15/12/1.
 */
@LayoutResource(R.layout.month_member_rank_frag)
public class MonthMemberRankFrag extends
        SessionFragment implements PullRefreshListView.onListViewListener {

    @IdResource(R.id.monthly_ranking_fragment_list)
    private PullRefreshListView rankingView;

    @IdResource(R.id.monthly_nerwork_err)
    private ImageView err;

    private ClubManager clubManager;
    private RankingAdapter2 rankingAdapter;
    private List<RankDTO2> monthlyRankList = new ArrayList<>();

    private int page = 1;
    private int count = 50;

    private String clubId = "";

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        final BeastBikes app = (BeastBikes) activity.getApplication();
        this.clubManager = new ClubManager(activity);
        this.rankingAdapter = new RankingAdapter2(this.getActivity(), this, this.monthlyRankList, getUserId());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.rankingView.setPullRefreshEnable(false);
        this.rankingView.setListViewListener(this);

        this.rankingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final RankDTO2 dto = (RankDTO2) parent
                        .getItemAtPosition(position);
                if (null == dto)
                    return;
                final Intent intent = new Intent();
                intent.setClass(getActivity(), ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_ID, dto.getUserId());
                intent.putExtra(ProfileActivity.EXTRA_AVATAR, dto.getAvatar());
                intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, dto.getNickname());
                intent.putExtra(ProfileActivity.EXTRA_REMARKS, dto.getRemarks());
                startActivity(intent);
            }
        });
        this.rankingView.setAdapter(this.rankingAdapter);
        rankingView.resetHeadViewBackground(R.color.discover_color3);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubId = MemberRankingActivity.clubId;
        fetchRankDTO();
    }

    @Override
    public void onRefresh() {
//        Intent intent = new Intent(RankingFragment.EXTRA_REFRESH_MY_RANK);
//        intent.putExtra(RankingFragment.EXTRA_REFRESH_RANK_TYPE, 1);
//        getActivity().sendBroadcast(intent);
        fetchRankDTO();
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

    private void fetchRankDTO() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, List<RankDTO2>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected List<RankDTO2> doInBackground(Void... params) {
                        try {
                            return clubManager
                                    .getClubMemberRankList(RankingManager.MONTHLY_RANK, page, count, clubId);
                        } catch (final BusinessException e) {
                            return null;
                        }

                    }

                    @Override
                    protected void onPostExecute(List<RankDTO2> list) {
                        rankingView.stopRefresh();
                        if (null == list || list.isEmpty()) {
                            if (monthlyRankList.size() > 0) {
                                err.setVisibility(View.GONE);
                            } else {
                                err.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        monthlyRankList.clear();
                        monthlyRankList.addAll(list);
                        rankingAdapter.notifyDataSetChanged();
                        err.setVisibility(View.GONE);

                    }

                });
    }
}
