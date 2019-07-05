package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubNoticeBean;
import com.beastbikes.android.widget.ListViewFooter;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 15/12/3.
 */
@MenuResource(R.menu.add_menu)
@LayoutResource(R.layout.activity_history_notice)
public class ClubHistoryNoticeActivity extends SessionFragmentActivity implements
        PullRefreshListView.onListViewListener {

    @IdResource(R.id.history_notice_list)
    private PullRefreshListView listView;

    private int page = 1;
    private int count = 20;

    private int REQUEST = 111;

    private ClubManager clubManager;
    private List<ClubNoticeBean> list;
    private String clubId;
    private HistoryNoticeAdapter historyNoticeAdapter;
    private LoadingDialog loadingDialog;
    private int level = 0;
    private String notice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        if (intent != null) {
            clubId = intent.getStringExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID);
            level = intent.getIntExtra(ClubMoreActivity.EXTRA_CLUB_LEVLE, 0);
            notice = intent.getStringExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE);
        }
        if (TextUtils.isEmpty(clubId))
            return;
        clubManager = new ClubManager(this);
        list = new ArrayList<>();
        historyNoticeAdapter = new HistoryNoticeAdapter( list);
        listView.setAdapter(historyNoticeAdapter);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(true);
        listView.resetHeadViewBackground(R.color.bg_black_color);
        listView.setListViewListener(this);
        getClubNoticeList();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_item_add);
        if (level == 0)
            menuItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                Intent intent = new Intent(this, ClubPostNoticeActivity.class);
                intent.putExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE, notice);
                startActivityForResult(intent, REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        list.clear();
        page = 1;
        listView.setPullLoadEnable(true);
        getClubNoticeList();
    }

    @Override
    public void onLoadMore() {
        listView.setPullLoadEnable(true);
        page++;
        getClubNoticeList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                notice = data.getStringExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE);
            }
            onRefresh();
        }
    }

    private void getClubNoticeList() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.activity_record_detail_activity_loading), false);
        loadingDialog.show();
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubNoticeBean>>() {

            @Override
            protected List<ClubNoticeBean> doInBackground(Void... params) {
                try {
                    return clubManager.getClubNoticeList(clubId, page, count);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ClubNoticeBean> clubNoticeBeans) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                listView.stopRefresh();

                if (clubNoticeBeans == null || clubNoticeBeans.size() == 0) {
                    listView.stopLoadMore(ListViewFooter.STATE_NORMAL);
                    listView.setPullLoadEnable(false);
                    return;
                }
                list.addAll(clubNoticeBeans);
                historyNoticeAdapter.notifyDataSetChanged();
            }
        });
    }

    private class HistoryNoticeAdapter extends BaseAdapter {

        private List<ClubNoticeBean> list;


        public HistoryNoticeAdapter( List<ClubNoticeBean> list) {
            this.list = list;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final HistoryNoticeHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_history_notice, null);

                vh = new HistoryNoticeHolder( convertView);
            } else {
                vh = (HistoryNoticeHolder) convertView.getTag();
            }
            vh.bind(list.get(position));
            return convertView;
        }
    }

    private final class HistoryNoticeHolder extends ViewHolder<ClubNoticeBean> {
        private RequestQueueManager rqm;
        private View view;

        @IdResource(R.id.notice_history_tv)
        private TextView contenttv;

        @IdResource(R.id.notice_history_time_tv)
        private TextView timetv;

        public HistoryNoticeHolder( View v) {
            super(v);
            this.view = v;
        }

        @Override
        public void bind(ClubNoticeBean clubNoticeBean) {
            if (clubNoticeBean == null)
                return;
            contenttv.setText(clubNoticeBean.getContent());
            String date = clubNoticeBean.getCreatedAt();
            if (!TextUtils.isEmpty(date) && date.contains("T")) {
                String[] str = date.split("T");
                if (str != null && str.length > 0) {
                    date = str[0];
                    date = date.replace("-", ".");
                }
            }
            timetv.setText(date);
        }
    }
}
