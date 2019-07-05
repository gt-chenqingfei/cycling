package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubActivityManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubActUserList;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityMember;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityUser;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.activity_club_act_user_list)
public class ClubActUserListActivity extends SessionFragmentActivity implements SwipeRefreshLoadRecyclerView.RecyclerCallBack {

    public static final String EXTRA_DATA = "data";
    @IdResource(R.id.content)
    private LinearLayout contentLL;

    private SwipeRefreshLoadRecyclerView swipeRefreshLoadRecyclerView;

    private List<ClubActivityUser> users = new ArrayList<ClubActivityUser>();

    private ClubActivityListAdapter clubActivityAdapter;

    private int page = 1;

    private int count = 20;

    public final static String IS_CLUB_ACT_LIST_CAN_LOAD_MORE = "IS_CLUB_ACT_LIST_CAN_LOAD_MORE";

    public final static String CLUB_ACT_ID = "CLUB_ACT_ID";

    public final static String CLUB_ACT_MEMBERS = "CLUB_ACT_MEMBERS";

    private boolean canLoadMore;

    private String activityID;

    private ClubActivityManager clubActivityManager;

    private boolean isRefresh;

    private int members = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.club_activity_user_list_tittle));
        }
        Intent intent = getIntent();
        if (intent == null)
            return;
        if(intent.getSerializableExtra(EXTRA_DATA) != null) {
            ClubActUserList clubActUserList = (ClubActUserList) intent.getSerializableExtra(EXTRA_DATA);
            if (clubActUserList != null && clubActUserList.getUsers() != null) {
                users.addAll(clubActUserList.getUsers());
            }
        }
        activityID = intent.getStringExtra(CLUB_ACT_ID);
        clubActivityManager = new ClubActivityManager(this);
        canLoadMore = intent.getBooleanExtra(IS_CLUB_ACT_LIST_CAN_LOAD_MORE, false);
        members = intent.getIntExtra(CLUB_ACT_MEMBERS, 0);
        clubActivityAdapter = new ClubActivityListAdapter(this);
        if (canLoadMore) {
            swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this, contentLL, users, SwipeRefreshLoadRecyclerView.HASFOOTER);
        } else {
            swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this, contentLL, users, SwipeRefreshLoadRecyclerView.NORMAL);
        }
        swipeRefreshLoadRecyclerView.setAdapter(clubActivityAdapter);
        swipeRefreshLoadRecyclerView.setRecyclerCallBack(this);
        if (users == null || users.size() == 0)
            refreshCallBack();
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void refreshCallBack() {
        page = 1;
        swipeRefreshLoadRecyclerView.setCanLoadMore(true);
        isRefresh = true;
        swipeRefreshLoadRecyclerView.setHasFooter(true);
        clubActivityMemberList();
    }

    @Override
    public void loadMoreCallBack() {
        if (!canLoadMore)
            return;
        page++;
        swipeRefreshLoadRecyclerView.setHasFooter(true);
        clubActivityMemberList();
    }

    private void clubActivityMemberList() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, ClubActivityMember>() {

            @Override
            protected ClubActivityMember doInBackground(Void... voids) {
                if (TextUtils.isEmpty(activityID))
                    return null;
                return clubActivityManager.clubActivityMemberList(activityID, page, count);
            }

            @Override
            protected void onPostExecute(ClubActivityMember clubActivityMember) {
                List<ClubActivityUser> clubActivityUsers = clubActivityMember.getClubActivityUsers();
                if (clubActivityUsers == null || clubActivityUsers.size() == 0) {
                    swipeRefreshLoadRecyclerView.setHasFooter(false);
                    swipeRefreshLoadRecyclerView.setCanLoadMore(false);
                    swipeRefreshLoadRecyclerView.finishLoad();
                }
                if (isRefresh) {
                    isRefresh = false;
                    users.clear();
                }
                users.addAll(clubActivityUsers);
                swipeRefreshLoadRecyclerView.notifyDataSetChanged();
                swipeRefreshLoadRecyclerView.finishLoad();
                if (members == users.size() || (page == 1 && users.size() < count)) {
                    swipeRefreshLoadRecyclerView.setCanLoadMore(false);
                    swipeRefreshLoadRecyclerView.setHasFooter(false);
                }
            }
        });
    }

    class ClubActivityListAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {

        private Context context;
        private LayoutInflater mInflater;

        public ClubActivityListAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder() {
            MyViewHolder holder = new
                    MyViewHolder(mInflater.inflate(R.layout.item_act_list_user, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, int positon, boolean isLastItem) {
            if (holder instanceof MyViewHolder) {
                final MyViewHolder myViewHolder = (MyViewHolder) holder;
                final ClubActivityUser user = (ClubActivityUser) value;
                if (null == user) {
                    return;
                }

                if (!TextUtils.isEmpty(user.getAvatar())) {
                    Picasso.with(context).load(user.getAvatar()).fit().error(R.drawable.ic_avatar)
                            .placeholder(R.drawable.ic_avatar).centerCrop().into(myViewHolder.iv);
                } else {
                    myViewHolder.iv.setImageResource(R.drawable.ic_avatar);
                }
//                myViewHolder.tv.setText(user.getNickname());
                myViewHolder.tv.setText(NickNameRemarksUtil.disPlayName(user.getNickname(), user.getRemarks()));
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile_intent = new Intent(context, ProfileActivity.class);
                        profile_intent.putExtra(ProfileActivity.EXTRA_USER_ID, user.getUserId());
                        profile_intent.putExtra(ProfileActivity.EXTRA_AVATAR, user.getAvatar());
                        profile_intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, user.getNickname());
                        profile_intent.putExtra(ProfileActivity.EXTRA_REMARKS, user.getRemarks());
                        context.startActivity(profile_intent);
                    }
                });
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.item_act_list_user_tv);
                iv = (ImageView) itemView.findViewById(R.id.item_act_list_user_iv);
            }
        }
    }
}
