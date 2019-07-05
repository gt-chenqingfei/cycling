package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.frag.FragBaseList;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 15/11/30.
 */
public class RecommendFrag extends FragBaseList<String, ClubInfoCompact, ListView> {


    public static final String EXTRA_SEARCH_RESULT = "search_result";
    public static final String EXTRA_SEARCH_KEY = "search_key";
    private ClubManager clubManager;
    private int page = 1;
    private final int pageCount = 20;
    private List<ClubInfoCompact> recommendList = null;
    private String keyWord;
    private LoadingDialog loadingDialog;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clubManager = new ClubManager(getActivity());
        pullView.disablePullDown();
        pullView.enablePullUp();
        loadingDialog = new LoadingDialog(getActivity(),
                getString(R.string.pull_to_refresh_refreshing_label), true);
        if (HomeActivity.currentPage == 1) {
            loadingDialog.show();
        }
        recommendList = new ArrayList<>();
        fetchClubCommondList(pageCount);
    }

    @Override
    protected void onItemClick(ClubInfoCompact item) {
        super.onItemClick(item);
        IntentUtils.goClubFeedInfoActivity(getContext(), item);
    }

    @Override
    public void loadNormal() {

    }

    @Override
    public void loadMore(String maxId) {
        super.loadMore(maxId);
        page++;
        fetchClubCommondList(pageCount);
    }


    /**
     * 获取所有俱乐部推荐
     */
    private void fetchClubCommondList(final int count) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ClubInfoCompact>>() {
                    @Override
                    protected List<ClubInfoCompact> doInBackground(
                            String... params) {
                        try {
                            return clubManager
                                    .getClubList(ClubManager.CLUB_ORDERBY.RECOMMEND, "",
                                            "", page, count);

                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubInfoCompact> result) {
                        if (loadingDialog != null && null != getActivity() && !getActivity().isFinishing())
                            loadingDialog.dismiss();
                        if (result == null || result.isEmpty()) {
                            Activity activity = getActivity();
                            if(null == activity)
                                return;
                            if ( page == 1) {
                                onLoadFailed(activity
                                        .getString(R.string.club_discover_load_fail));
                            } else {
                                onLoadFailed(activity
                                        .getString(R.string.club_discover_load_end));
                            }
                            return;
                        }
                        recommendList.addAll(result);
                        onLoadSucessfully(recommendList);
                    }
                });
    }


    @Override
    protected BaseListAdapter<ClubInfoCompact> adapterToDisplay(AbsListView view) {
        return new ClubSearchAdapter(null, view);
    }
}
