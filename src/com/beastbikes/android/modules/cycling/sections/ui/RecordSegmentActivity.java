package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.sections.biz.SectionManager;
import com.beastbikes.android.modules.cycling.sections.dto.RecordSegmentDTO;
import com.beastbikes.android.modules.cycling.sections.dto.UserSegmentDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.SeekFriendDTO;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/19.
 */

@LayoutResource(R.layout.activity_record_segment)
public class RecordSegmentActivity extends SessionFragmentActivity implements ItemClickListener {

    @IdResource(R.id.record_segment_content)
    private LinearLayout contentLL;

    public final static String EXTRA_ACTIVITY_ID = "activity_id";
    private String activityIdentifier;

    private SwipeRefreshLoadRecyclerView swipeRefreshLoadRecyclerView;
    private FavorSegmentRecyclerViewAdapter favorSegmentRecyclerViewAdapter;

    private List<UserSegmentDTO> sectionList;

    private SectionManager sectionManager;

    @IdResource(R.id.frag_section_list_no_content_ll)
    private LinearLayout noContentLL;

    @IdResource(R.id.frag_section_list_no_content_tv)
    private TextView noContentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        activityIdentifier = intent.getStringExtra(EXTRA_ACTIVITY_ID);
        if (TextUtils.isEmpty(activityIdentifier))
            finish();
        sectionList = new ArrayList<>();
        this.sectionManager = new SectionManager(this);
        favorSegmentRecyclerViewAdapter = new FavorSegmentRecyclerViewAdapter(this, this);
        swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this, contentLL, sectionList, SwipeRefreshLoadRecyclerView.HASFOOTER);
        swipeRefreshLoadRecyclerView.setAdapter(favorSegmentRecyclerViewAdapter);
        swipeRefreshLoadRecyclerView.setHasFooter(false);
        swipeRefreshLoadRecyclerView.setRefreshEnable(false);
        getRecordSegmentList();
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        Intent intent = new Intent(RecordSegmentActivity.this, SectionDetailActivity.class);
        intent.putExtra(SectionDetailActivity.SECTION_ID, sectionList.get(position).getSegmentId());
        startActivity(intent);
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    private void getRecordSegmentList() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, RecordSegmentDTO>() {

            @Override
            protected RecordSegmentDTO doInBackground(Void... voids) {
                try {
                    return sectionManager.getRecordSegmentList(activityIdentifier);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(RecordSegmentDTO recordSegmentDTO) {
                swipeRefreshLoadRecyclerView.finishLoad();
                swipeRefreshLoadRecyclerView.setRefreshEnable(false);
                if (recordSegmentDTO == null || recordSegmentDTO.isNeedWait())
                    return;
                List<UserSegmentDTO> userSegmentDTOs = recordSegmentDTO.getUserSegmentDTOs();
                if (userSegmentDTOs == null || userSegmentDTOs.size() == 0) {
                    noContentTV.setText(getResources().getText(R.string.no_pass_any_section));
                    return;
                }
                noContentLL.setVisibility(View.GONE);
                sectionList.addAll(userSegmentDTOs);
                swipeRefreshLoadRecyclerView.notifyDataSetChanged();
            }
        });
    }
}
