package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.sections.biz.SectionManager;
import com.beastbikes.android.modules.cycling.sections.dto.UserSegmentDTO;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/17.
 */
@LayoutResource(R.layout.activity_favor_segment)
public class FavorSegmentActivity extends SessionFragmentActivity implements ItemClickListener, SwipeRefreshLoadRecyclerView.RecyclerCallBack {

    @IdResource(R.id.favor_segment_content)
    private LinearLayout contentLL;
    @IdResource(R.id.activity_favor_segment_empty_view)
    private TextView emptyView;

    private SwipeRefreshLoadRecyclerView swipeRefreshLoadRecyclerView;
    private FavorSegmentRecyclerViewAdapter favorSegmentRecyclerViewAdapter;

    private List<UserSegmentDTO> sectionList = new ArrayList<>();

    private SectionManager sectionManager;
    private int page = 1;
    private int count = 20;
    private boolean isRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        this.sectionManager = new SectionManager(this);
        favorSegmentRecyclerViewAdapter = new FavorSegmentRecyclerViewAdapter(this, this);
        swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this, contentLL, sectionList, SwipeRefreshLoadRecyclerView.HASFOOTER);
        swipeRefreshLoadRecyclerView.setAdapter(favorSegmentRecyclerViewAdapter);
        swipeRefreshLoadRecyclerView.setRecyclerCallBack(this);
        getUserSegmentList();
    }

    @Override
    public void refreshCallBack() {
        page = 1;
        isRefresh = true;
        getUserSegmentList();
    }

    @Override
    public void loadMoreCallBack() {
        page++;
        getUserSegmentList();
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        Intent intent = new Intent(FavorSegmentActivity.this, SectionDetailActivity.class);
        intent.putExtra(SectionDetailActivity.SECTION_ID, sectionList.get(position).getSegmentId());
        startActivity(intent);
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    private void getUserSegmentList() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<UserSegmentDTO>>() {

            @Override
            protected List<UserSegmentDTO> doInBackground(Void... voids) {
                try {
                    return sectionManager.getUserSegmentList(getUserId(), page, count);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<UserSegmentDTO> sectionListDTOs) {
                if (sectionListDTOs == null || sectionListDTOs.size() == 0) {
                    if (page == 1) {
                        swipeRefreshLoadRecyclerView.setHasFooter(false);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    swipeRefreshLoadRecyclerView.finishLoad();
                    return;
                }

                emptyView.setVisibility(View.GONE);

                if (isRefresh)
                    sectionList.clear();
                isRefresh = false;
                sectionList.addAll(sectionListDTOs);
                if (page == 1 && sectionList.size() < count)
                    swipeRefreshLoadRecyclerView.setHasFooter(false);
                swipeRefreshLoadRecyclerView.notifyDataSetChanged();
                swipeRefreshLoadRecyclerView.finishLoad();
            }
        });
    }

}
