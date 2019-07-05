package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.sections.dto.SectionListDTO;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.android.widget.helper.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/5.
 */
@LayoutResource(R.layout.frag_section_list)
public class SectionListFragment extends SectionBaseFragment implements ItemClickListener {

    @IdResource(R.id.content)
    private LinearLayout contentLL;

    @IdResource(R.id.frag_section_list_no_content_ll)
    private LinearLayout noContentLL;

    @IdResource(R.id.frag_section_list_no_content_tv)
    private TextView noContentTV;

    private List<SectionListDTO> sectionListDTOs = new ArrayList<>();
    private SwipeRefreshLoadRecyclerView swipeRefreshLoadRecyclerView;
    private SectionRecyclerViewAdapter sectionRecyclerViewAdapter;

    private boolean locationFail;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        sectionRecyclerViewAdapter = new SectionRecyclerViewAdapter(this.getContext(), this);
        swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this.getActivity(), contentLL, sectionListDTOs, SwipeRefreshLoadRecyclerView.HASFOOTER);
        swipeRefreshLoadRecyclerView.setAdapter(sectionRecyclerViewAdapter);
        swipeRefreshLoadRecyclerView.setHasFooter(false);
        swipeRefreshLoadRecyclerView.setRefreshEnable(false);
        if (locationFail) {
            noContentTV.setText(getResources().getString(R.string.section_location_failed));
            noContentLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        Intent intent = new Intent(SectionListFragment.this.getActivity(), SectionDetailActivity.class);
        intent.putExtra(SectionDetailActivity.SECTION_ID, sectionListDTOs.get(position).getSegmentId());
        startActivity(intent);
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    @Override
    public void notifyDataSetChanged(List<SectionListDTO> sectionList) {
        if (sectionList == null || sectionList.size() == 0)
            return;
        if (noContentTV != null)
            noContentLL.setVisibility(View.GONE);
        sectionListDTOs.clear();
        sectionListDTOs.addAll(sectionList);
        if (swipeRefreshLoadRecyclerView != null)
            swipeRefreshLoadRecyclerView.notifyDataSetChanged();
    }

    @Override
    public void filterFailed() {
        sectionListDTOs.clear();
        if (swipeRefreshLoadRecyclerView != null)
            swipeRefreshLoadRecyclerView.notifyDataSetChanged();
        if (noContentTV != null) {
            noContentTV.setText(getResources().getString(R.string.section_filter_failed));
            noContentLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getLocationFail(String errorMsg) {
        sectionListDTOs.clear();
        if (swipeRefreshLoadRecyclerView != null)
            swipeRefreshLoadRecyclerView.notifyDataSetChanged();
        locationFail = true;
        if (noContentTV != null) {
            noContentTV.setText(errorMsg);
            noContentLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void noData(String errorMsg) {
        sectionListDTOs.clear();
        if (swipeRefreshLoadRecyclerView != null)
            swipeRefreshLoadRecyclerView.notifyDataSetChanged();
        if (noContentTV != null) {
            noContentTV.setText(errorMsg);
            noContentLL.setVisibility(View.VISIBLE);
        }
    }
}
