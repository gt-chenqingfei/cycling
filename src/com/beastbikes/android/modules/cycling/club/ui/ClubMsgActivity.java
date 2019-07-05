package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.PushFactory;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubMsgDTO;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by caoxiao on 15/12/10.
 */

@Alias("俱乐部消息")
@MenuResource(R.menu.club_msg_clear)
@LayoutResource(R.layout.activity_clubmsg)
public class ClubMsgActivity extends SessionFragmentActivity implements View.OnClickListener, PullRefreshListView.onListViewListener, Constants {

    @IdResource(R.id.club_msg_list)
    private PullRefreshListView clubMsgListView;

    private ClubFeedManager clubFeedManager;
    private Long stamp = null;
    private int count = 20;
    private boolean history = false;

    private List<ClubMsgDTO> list;
    private ClubMsgAdapter clubMsgAdapter;

    private LoadingDialog loadingDialog;

    private View footView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null)
            return;

        list = new ArrayList<>();
        clubMsgAdapter = new ClubMsgAdapter(list);
        clubMsgListView.setAdapter(clubMsgAdapter);
        clubMsgListView.setPullLoadEnable(false);
        clubMsgListView.setPullRefreshEnable(false);
        clubFeedManager = new ClubFeedManager(this);
        footView = LayoutInflater.from(this).inflate(R.layout.footview_club_msg, null);
        clubMsgListView.addFooterView(footView);
        clubMsgListView.setListViewListener(this);
        clubMsgListView.resetHeadViewBackground(R.color.discover_color3);
        footView.setOnClickListener(this);
        getData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clubmsg_footview:
                clubMsgListView.removeFooterView(footView);
                history = true;
                onLoadMore();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        clubMsgListView.setPullLoadEnable(true);
        getData();
    }

    private void clearAll() {
        loadingDialog = new LoadingDialog(this, "", false);
        loadingDialog.show();
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return clubFeedManager.cleanMyClubMsgList();
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (loadingDialog != null)
                    loadingDialog.dismiss();
                if (aBoolean == null)
                    return;
                if (aBoolean) {
                    list.clear();
                    clubMsgAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    private void getData() {
        loadingDialog = new LoadingDialog(this, "", false);
        loadingDialog.show();
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubMsgDTO>>() {

            @Override
            protected List<ClubMsgDTO> doInBackground(Void... params) {
                try {
                    return clubFeedManager.getPushRecordList(history, stamp, count);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<ClubMsgDTO> clubMsgDTOs) {
                if (loadingDialog != null)
                    loadingDialog.dismiss();
                if (clubMsgDTOs == null) {
                    clubMsgListView.setPullLoadEnable(false);
                    return;
                }
                stamp = clubMsgDTOs.get(clubMsgDTOs.size() - 1).getStamp();
                list.addAll(clubMsgDTOs);
                clubMsgAdapter.notifyDataSetChanged();
            }
        });
    }

    class ClubMsgAdapter extends BaseAdapter {
        private List<ClubMsgDTO> list;

        public ClubMsgAdapter(List<ClubMsgDTO> list) {
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
            final ClubMsgHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_club_msg, null);
                vh = new ClubMsgHolder(convertView);
            } else {
                vh = (ClubMsgHolder) convertView.getTag();
            }
            vh.bind(list.get(position));
            return convertView;
        }
    }

    public final class ClubMsgHolder extends ViewHolder<ClubMsgDTO> {

        @IdResource(R.id.item_club_msg)
        private RelativeLayout itemClubMsg;

        @IdResource(R.id.club_msg_list_item_avatar)
        private CircleImageView avater;

        @IdResource(R.id.club_msg_name)
        private TextView nickName;

        @IdResource(R.id.club_msg_time)
        private TextView time;

        @IdResource(R.id.thumbnailIV)
        private ImageView thumbnailIV;

        @IdResource(R.id.rightTV)
        private TextView rightTV;

        @IdResource(R.id.content)
        private TextView content;

        @IdResource(R.id.rightrl)
        private View rightrLayout;

        private boolean isShowInput;

        public ClubMsgHolder(View v) {
            super(v);
        }

        @Override
        public void bind(final ClubMsgDTO dto) {
            if (dto == null)
                return;

            String avatar = "";
            String nickNameStr = "";

            final JSONObject data = dto.getParams();
            final ProfileDTO user = dto.getUser();
            final ClubInfoCompact clubInfoCompact = dto.getClubInfoCompact();
            if (null != user) {
                avatar = user.getAvatar();
                nickNameStr = user.getNickname();
            } else if (null != clubInfoCompact) {
                avatar = clubInfoCompact.getLogo();
                nickNameStr = clubInfoCompact.getName();
            }

            if (!TextUtils.isEmpty(avatar)) {
                Picasso.with(getContext()).load(avatar).fit().centerCrop().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).into(avater);
            } else {
                this.avater.setImageResource(R.drawable.ic_avatar);
            }

            nickName.setText(nickNameStr);
            Date createDate = DateFormatUtil.stringFormat2Date(dto.getCreatedAt());
            time.setText(DateFormatUtil.getRelativeTimeSpanString4Feed(createDate));
            content.setText(dto.getContent());

            avater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (dto.getSenderType()) {
                        case ClubMsgDTO.SENDER_TYPE_CLUB:
                            IntentUtils.goClubFeedInfoActivity(v.getContext(), dto.getClubInfoCompact());
                            break;
                        case ClubMsgDTO.SENDER_TYPE_USER:
                            if (null != user) {
                                Intent intent = new Intent(ClubMsgActivity.this, ProfileActivity.class);
                                intent.putExtra(ProfileActivity.EXTRA_USER_ID, user.getUserId());
                                intent.putExtra(ProfileActivity.EXTRA_AVATAR, user.getAvatar());
                                startActivity(intent);
                            }
                            break;
                    }

                }
            });

            itemClubMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.has("page")) {
                        Intent intent = PushFactory.getInstance().buildPageIntent(data, v.getContext());
                        if (!intent.getBooleanExtra(PushFactory.EXTRA_ACTIVITY_NULL, false)) {
                            try {
                                startActivity(intent);
                            } catch (Exception e) {

                            }
                        }
                    }

                }
            });
        }
    }


}
