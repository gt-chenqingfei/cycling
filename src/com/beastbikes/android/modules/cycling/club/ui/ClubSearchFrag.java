package com.beastbikes.android.modules.cycling.club.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager.CLUB_ORDERBY;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.frag.FragBaseList;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;

import java.util.List;

public class ClubSearchFrag extends
        FragBaseList<String, ClubInfoCompact, ListView>

{
    public static final String EXTRA_SEARCH_RESULT = "search_result";
    public static final String EXTRA_SEARCH_KEY = "search_key";
    private ClubManager clubManager;
    private int page = 1;
    private final int pageCount = 20;
    private List<ClubInfoCompact> searchResult = null;
    private String keyWord;

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
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        clubManager = new ClubManager(getActivity());
        keyWord = getActivity().getIntent().getStringExtra(EXTRA_SEARCH_KEY);
        searchResult = (List<ClubInfoCompact>) getActivity().getIntent().getSerializableExtra(EXTRA_SEARCH_RESULT);
        if (null == searchResult || searchResult.isEmpty()) {
            if(getActivity() != null) {
                getActivity().finish();
            }
            return;
        }
        pullView.disablePullDown();
        onLoadSucessAddfully(searchResult);
        if (searchResult.size() < pageCount)
            pullView.disablePullUp();
        else
            pullView.enablePullUp();
    }

    @Override
    protected void onItemClick(ClubInfoCompact item) {
        super.onItemClick(item);
        IntentUtils.goClubFeedInfoActivity(getContext(), item);
    }

    @Override
    public void loadNormal() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadMore(String maxId) {
        // TODO Auto-generated method stub
        super.loadMore(maxId);
        page++;
        fetchClubByKey(keyWord);
    }

    /**
     * 获取所有俱乐部同城
     */
    private void fetchClubByKey(final String keyword) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ClubInfoCompact>>() {
                    @Override
                    protected List<ClubInfoCompact> doInBackground(
                            String... params) {
                        try {
                            return clubManager.getClubList(CLUB_ORDERBY.NONE,
                                    null, keyword, page, pageCount);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubInfoCompact> result) {
                        if (result == null || result.isEmpty()) {
                            onLoadFailed(getString(R.string.club_search_fail_msg));
                            pullView.disablePull();
                            return;
                        }
                        onLoadSucessAddfully(result);
                    }
                });
    }

    @Override
    protected BaseListAdapter<ClubInfoCompact> adapterToDisplay(AbsListView view) {
        // TODO Auto-generated method stub
        return new ClubSearchAdapter(null, view);
    }

}
