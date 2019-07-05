package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 15/12/10.
 */
@Alias("谁赞过我")
@LayoutResource(R.layout.activity_thumbslist)
public class ThumbsListActivity extends SessionFragmentActivity implements
        PullRefreshListView.onListViewListener, AdapterView.OnItemClickListener {
    public static final String EXTRA_FEEDID = "feed_id";
    public static final String EXTRA_PHOTOID = "photo_id";
    @IdResource(R.id.thumbs_list)
    private PullRefreshListView thumbsListView;

    @IdResource(R.id.loadfailiv)
    private ImageView loadfailIV;

    @IdResource(R.id.loadfailtv)
    private TextView loadfailTV;

    private List<ClubUser> clubList;

    private LoadingDialog loadingDialog;

    private int page = 1;
    private int count = 50;

    private ClubFeedManager clubFeedManager;
    private ThumbsListAdapter thumbsListAdapter;

    private int feedId = -1;
    private int photoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            //获取需要获取
            feedId = intent.getIntExtra(EXTRA_FEEDID, -1);
            photoId = intent.getIntExtra(EXTRA_PHOTOID, -1);

        }
        clubList = new ArrayList<>();
        thumbsListAdapter = new ThumbsListAdapter(clubList);
        clubFeedManager = new ClubFeedManager(this);
        thumbsListView.setAdapter(thumbsListAdapter);
        thumbsListView.setPullRefreshEnable(false);
        thumbsListView.setPullLoadEnable(true);
        thumbsListView.resetHeadViewBackground(R.color.discover_color2);
        thumbsListView.setListViewListener(this);
        this.thumbsListView.setOnItemClickListener(this);
        getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ClubUser dto = clubList.get(position - 1);
        if (dto == null)
            return;
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, dto.getUserId());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, dto.getAvatar());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, dto.getNickName());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, dto.getRemarks());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        page++;
        getData();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void getData() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.activity_record_detail_activity_loading), false);
        loadingDialog.show();
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubUser>>() {

            @Override
            protected List<ClubUser> doInBackground(Void... params) {
                try {
                    if (feedId == -1) {
                        return clubFeedManager.getClubPhotoLikeList(photoId, page, count);
                    } else {
                        return clubFeedManager.getClubFeedLikeList(feedId, page, count);
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ClubUser> clubUsers) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (clubUsers == null) {
                    if (page == 1) {
                        loadfailIV.setVisibility(View.VISIBLE);
                        loadfailTV.setVisibility(View.VISIBLE);
                    }
                    thumbsListView.setPullLoadEnable(false);
                    return;
                }
                clubList.addAll(clubUsers);
                thumbsListAdapter.notifyDataSetChanged();
            }
        });
    }

    class ThumbsListAdapter extends BaseAdapter {

        private List<ClubUser> list;

        public ThumbsListAdapter(List<ClubUser> list) {
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
            final ThumbsViewHolder vh;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_thumbs, null);

                vh = new ThumbsViewHolder(convertView);
            } else {
                vh = (ThumbsViewHolder) convertView.getTag();
            }
            vh.bind(this.list.get(position));
            if (position == list.size() - 1) {
                vh.shortView.setVisibility(View.GONE);
                vh.longView.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    class ThumbsViewHolder extends ViewHolder<ClubUser> {

        @IdResource(R.id.thumb_list_item_avatar)
        private CircleImageView avatar;

        @IdResource(R.id.thumbTV)
        private TextView thumbTV;

        @IdResource(R.id.shortView)
        public View shortView;

        @IdResource(R.id.longView)
        public View longView;

        private View view;

        public ThumbsViewHolder(View v) {
            super(v);
            this.view = v;
        }

        @Override
        public void bind(ClubUser dto) {
            if (dto == null)
                return;

            if (!TextUtils.isEmpty(dto.getAvatar())) {
                Picasso.with(getContext()).load(dto.getAvatar()).fit().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).centerCrop().into(avatar);
            } else {
                this.avatar.setImageResource(R.drawable.ic_avatar);
            }
//            thumbTV.setText(dto.getNickName());

            thumbTV.setText(NickNameRemarksUtil.disPlayName(dto.getNickName(), dto.getRemarks()));
        }
    }
}
