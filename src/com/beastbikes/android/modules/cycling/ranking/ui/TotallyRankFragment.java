package com.beastbikes.android.modules.cycling.ranking.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.ui.GeoCodeMessage;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.ranking.biz.RankingManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.modules.user.ui.ProfileFragment;
import com.beastbikes.android.utils.RxBus;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by caoxiao on 16/1/9.
 */
@LayoutResource(R.layout.fragment_ranking)
public class TotallyRankFragment extends SessionFragment implements SwipeRefreshLoadRecyclerView.RecyclerCallBack,
        ItemClickListener, Constants {

    @IdResource(R.id.content)
    private LinearLayout contentLL;
    private List<RankDTO> mDataList;

    private SwipeRefreshLoadRecyclerView swipeRefreshLoadRecyclerView;
    private RankRecyclerViewAdapter rankRecyclerViewAdapter;

    private RankingManager rankingManager;

    private int page;
    private int count = 50;
    private int type = RankingManager.RANK_TOTAL;

    private RankDTO rankDTO = new RankDTO();

    private boolean isFirstLoad = true;
    private String geoCode = "";

    private CompositeSubscription subscriptions;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        geoCode = (String) getArguments().get(RANK_GEO);

        mDataList = new ArrayList<>();
        rankingManager = new RankingManager(this.getActivity());
        rankRecyclerViewAdapter = new RankRecyclerViewAdapter(this.getActivity(), this);
        swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this.getActivity(), contentLL, mDataList, SwipeRefreshLoadRecyclerView.HASHEADER);
        swipeRefreshLoadRecyclerView.setRecyclerCallBack(this);
        swipeRefreshLoadRecyclerView.setAdapter(rankRecyclerViewAdapter);
        page = 1;

        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            rankDTO.setNickname(user.getDisplayName());
            rankDTO.setCity(user.getCity());
            rankDTO.setClubName(user.getClubName());
            rankDTO.setAvatarUrl(user.getAvatar());
            swipeRefreshLoadRecyclerView.setHeaderDate(rankDTO);
        }
        getRankList();
        getMyRank();

        subscriptions = new CompositeSubscription();

        subscriptions.add(RxBus.getDefault().toObserverable(ProfileFragment.ProfileEvent.class)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event instanceof ProfileFragment.ProfileEvent) {
                            for (int i = 0; i < mDataList.size(); i++) {
                                if (TextUtils.equals(mDataList.get(i).getUserId(), ((ProfileFragment.ProfileEvent)event).userId)) {
                                    mDataList.get(i).setRemarks(((ProfileFragment.ProfileEvent) event).mark);
                                    swipeRefreshLoadRecyclerView.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                }));

        // 切换区域
        subscriptions.add(RxBus.getDefault().toObserverable(GeoCodeMessage.class).subscribe(new Action1<GeoCodeMessage>() {
            @Override
            public void call(GeoCodeMessage geoCodeMessage) {
                geoCode = geoCodeMessage.getGeoCode();
                getMyRank();
                getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<RankDTO>>() {

                    @Override
                    protected List<RankDTO> doInBackground(Void... voids) {
                        try {
                            return rankingManager.getRankList(type, geoCode, 1, count);
                        } catch (BusinessException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<RankDTO> rankDTOs) {
                        if (rankDTOs == null) {
                            return;
                        }
                        mDataList.clear();
                        mDataList.addAll(rankDTOs);
                        swipeRefreshLoadRecyclerView.notifyDataSetChanged();
                    }
                });
            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void refreshCallBack() {
        getMyRank();
        page = 1;
        swipeRefreshLoadRecyclerView.setCanLoadMore(true);
        isFirstLoad = true;
        swipeRefreshLoadRecyclerView.setHasFooter(true);
        getRankList();
    }

    @Override
    public void loadMoreCallBack() {
        if (mDataList.size() >= 200)
            return;
        page++;
        swipeRefreshLoadRecyclerView.setHasFooter(true);
        getRankList();
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        if (null == mDataList || mDataList.size() <= 0) {
            return;
        }

        final RankDTO rank = this.mDataList.get(position);
        if (null == rank) {
            return;
        }

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, rank.getUserId());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, rank.getNickname());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, rank.getAvatarUrl());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, rank.getRemarks());
        getActivity().startActivity(intent);
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    private void getMyRank() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, RankDTO>() {

            @Override
            protected RankDTO doInBackground(Void... voids) {
                try {
                    return rankingManager.getMyRank(type, geoCode);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(RankDTO dto) {
                if (rankDTO == null || dto == null)
                    return;
                rankDTO.setMilestone(dto.getMilestone());
                rankDTO.setOrdinal(dto.getOrdinal());
                swipeRefreshLoadRecyclerView.setHeaderDate(rankDTO);
            }
        });
    }

    private void getRankList() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<RankDTO>>() {

            @Override
            protected List<RankDTO> doInBackground(Void... voids) {
                try {
                    return rankingManager.getRankList(type, geoCode, page, count);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<RankDTO> rankDTOs) {
                if (rankDTOs == null || rankDTOs.size() == 0) {
                    swipeRefreshLoadRecyclerView.setHasFooter(false);
                    swipeRefreshLoadRecyclerView.finishLoad();
                    return;
                }
                if (swipeRefreshLoadRecyclerView.isRefresh())
                    mDataList.clear();
                mDataList.addAll(rankDTOs);
                if (isFirstLoad && rankDTOs.size() < count) {
                    swipeRefreshLoadRecyclerView.setCanLoadMore(false);
                }
                swipeRefreshLoadRecyclerView.setHasFooter(false);
                swipeRefreshLoadRecyclerView.notifyDataSetChanged();
                swipeRefreshLoadRecyclerView.finishLoad();
                isFirstLoad = false;
            }
        });
    }


}