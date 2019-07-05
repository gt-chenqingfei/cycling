package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubRankBean;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 15/12/3.
 */
@LayoutResource(R.layout.month_member_rank_frag)


public class TotalClubRankFrag extends SessionFragment implements PullRefreshListView.onListViewListener {

    @IdResource(R.id.monthly_ranking_fragment_list)
    private PullRefreshListView rankingView;

    @IdResource(R.id.monthly_nerwork_err)
    private ImageView err;

    private ClubManager clubManager;
    private ClubRankActivity.ClubRankAdapter adapter;
    private List<ClubRankBean> monthlyRankList = new ArrayList<>();

    private int page = 1;
    private int count = 50;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        final BeastBikes app = (BeastBikes) activity.getApplication();
        this.clubManager = new ClubManager(activity);
//        this.rankingAdapter = new RankingAdapter(this, this.monthlyRankList,true);
        this.adapter = new ClubRankActivity.ClubRankAdapter(this.getActivity(), monthlyRankList);
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
                final ClubRankBean dto = (ClubRankBean) parent
                        .getItemAtPosition(position);
                if (null == dto)
                    return;
                ClubInfoCompact clubInfoCompact = new ClubInfoCompact(dto.getName(), dto.getLogo(),
                        dto.getMembers(), dto.getMilestone(), dto.getCity(), dto.getObjectId());
                clubInfoCompact.setIsPrivate(dto.isPrivate());

                IntentUtils.goClubFeedInfoActivity(getContext(), clubInfoCompact);
            }
        });
        this.rankingView.setAdapter(this.adapter);
        rankingView.resetHeadViewBackground(R.color.discover_color3);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                new AsyncTask<Void, Void, List<ClubRankBean>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected List<ClubRankBean> doInBackground(Void... params) {
                        try {
                            return clubManager
                                    .getClubRankList(ClubManager.CLUB_RANK_TOTAL, page, count);
                        } catch (BusinessException e) {
                            return null;
                        }

                    }

                    @Override
                    protected void onPostExecute(List<ClubRankBean> list) {
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
                        adapter.notifyDataSetChanged();
                        err.setVisibility(View.GONE);

                    }

                });
    }
}

