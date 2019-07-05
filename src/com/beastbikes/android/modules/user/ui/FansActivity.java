package com.beastbikes.android.modules.user.ui;

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
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.utils.RxBus;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by icedan on 16/4/20.
 */
@LayoutResource(R.layout.fans_activity)
public class FansActivity extends SessionFragmentActivity implements SwipeRefreshLoadRecyclerView.RecyclerCallBack,
        ItemClickListener {

    @IdResource(R.id.fans_activity_list_parent_view)
    private LinearLayout parentView;

    @IdResource(R.id.activity_fans_empty_tip)
    private TextView fansEmptyTip;

    private UserManager userManager;
    private SwipeRefreshLoadRecyclerView recyclerView;
    private FansAdapter fansAdapter;
    private List<FriendDTO> fansList = new ArrayList<>();
    private int pageIndex = 1;

    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.userManager = new UserManager(this);
        this.recyclerView = new SwipeRefreshLoadRecyclerView(this, parentView, fansList,
                SwipeRefreshLoadRecyclerView.HASFOOTER);
        this.recyclerView.setRecyclerCallBack(this);
        this.fansAdapter = new FansAdapter(this, this);
        this.recyclerView.setAdapter(this.fansAdapter);
        this.pageIndex = 1;

        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        if (user.getObjectId().equals(getUserId())) {
            this.fansEmptyTip.setText(R.string.activity_fans_empty_tip);
        } else {
            this.fansEmptyTip.setText(R.string.activity_other_friend_fans_empty_label);
        }
        this.getFansList();

        subscriptions = new CompositeSubscription();

        subscriptions.add(RxBus.getDefault().toObserverable(ProfileFragment.ProfileEvent.class)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event instanceof ProfileFragment.ProfileEvent) {
                            for (int i = 0; i < fansList.size(); i++) {
                                if (TextUtils.equals(fansList.get(i).getFriendId(), ((ProfileFragment.ProfileEvent)event).userId)) {
                                    fansList.get(i).setRemarks(((ProfileFragment.ProfileEvent) event).mark);
                                    recyclerView.notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }

    @Override
    public void refreshCallBack() {
        this.pageIndex = 1;
        this.recyclerView.setCanLoadMore(true);
        this.recyclerView.setHasFooter(true);
        this.getFansList();
    }

    @Override
    public void loadMoreCallBack() {
        this.getFansList();
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        if (null == fansList || fansList.size() <= 0) {
            return;
        }

        final FriendDTO fans = fansList.get(position);
        if (null == fans) {
            return;
        }

        final Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID, fans.getFriendId());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, fans.getNickname());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, fans.getRemarks());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, fans.getAvatar());
        super.startActivity(intent);
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    /**
     * 获取粉丝列表
     */
    private void getFansList() {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<FriendDTO>>() {
            @Override
            protected List<FriendDTO> doInBackground(String... params) {
                return userManager.getFansList(getUserId(), pageIndex, 20);
            }

            @Override
            protected void onPostExecute(List<FriendDTO> friends) {
                recyclerView.finishLoad();
                if (null == friends || friends.size() <= 0) {
                    recyclerView.setCanLoadMore(false);
                    recyclerView.setHasFooter(false);
                    return;
                }

                fansEmptyTip.setVisibility(View.GONE);

                if (friends.size() < 20) {
                    recyclerView.setCanLoadMore(false);
                    recyclerView.setHasFooter(false);
                }

                if (pageIndex == 1)
                    fansList.clear();

                pageIndex = pageIndex + 1;
                fansList.addAll(friends);
                recyclerView.notifyDataSetChanged();
            }
        });
    }

    /**
     * 关注
     *
     * @param userId
     */
    private void follow(final String userId, final int position) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return userManager.follow(userId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null == fansList || position >= fansList.size()) {
                    return;
                }

                if (result) {
                    Toasts.show(FansActivity.this, R.string.lable_follow_success_msg);

                    final FriendDTO fans = fansList.get(position);
                    int status = fans.getStatus() + 2;
                    if (status > 4) {
                        status = 3;
                    }
                    fans.setStatus(status);
                    recyclerView.notifyItemChanged(position);
                }
            }
        });
    }

    /**
     * 取消关注
     *
     * @param userId
     */
    private void unfollow(final String userId, final int position) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return userManager.unfollow(userId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null == fansList || position >= fansList.size()) {
                    return;
                }

                if (result) {
                    Toasts.show(FansActivity.this, R.string.lable_unfollow_success_msg);
                    final FriendDTO fans = fansList.get(position);
                    int status = fans.getStatus() - 2;
                    if (status < 0) {
                        status = 0;
                    }
                    fans.setStatus(status);
                    recyclerView.notifyItemChanged(position);
                }
            }
        });
    }

    /**
     * 取消关注提示窗
     *
     * @param userId
     * @param position
     */
    private void showUnfollowDialog(final String userId, final int position) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.msg_unfollow_prompt_dialog);
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow(userId, position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private final class FansViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView nameTv;
        private TextView statusTv;
        private View view;

        protected FansViewHolder(View v) {
            super(v);
            this.view = v;
            this.avatar = (ImageView) v.findViewById(R.id.fans_and_follow_item_avatar);
            this.nameTv = (TextView) v.findViewById(R.id.fans_and_follow_item_name);
            this.statusTv = (TextView) v.findViewById(R.id.fans_and_follow_item_status);
        }

    }

    private final class FansAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {

        private Context context;
        private ItemClickListener itemClickListener;

        public FansAdapter(Context context, ItemClickListener listener) {
            this.context = context;
            this.itemClickListener = listener;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder() {
            FansViewHolder holder = new FansViewHolder(LayoutInflater.from(
                    context).inflate(R.layout.fans_and_follow_item, null,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, final int position, boolean isLastItem) {
            if (null == value)
                return;

            final FriendDTO dto = (FriendDTO) value;
            if (null == dto) {
                return;
            }

            final FansViewHolder vh = (FansViewHolder) holder;
            final String activityUrl = dto.getAvatar();
            if (!TextUtils.isEmpty(activityUrl)) {
                Picasso.with(context)
                        .load(activityUrl)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .fit()
                        .centerCrop()
                        .into(vh.avatar);
            } else {
                Picasso.with(context)
                        .load(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .fit()
                        .centerCrop()
                        .into(vh.avatar);
            }

            String name = dto.getRemarks();
            if (TextUtils.isEmpty(name)) {
                name = dto.getNickname();
            }
            vh.nameTv.setText(name);

            switch (dto.getStatus()) {
                case 0:// 未关注
                case 1:// 被关注
                    vh.statusTv.setText(R.string.profile_fragment_follow);
                    vh.statusTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
                    vh.statusTv.setBackgroundResource(R.drawable.border_1px_solid_red_radius_6dp);
                    break;
                case 2:// 已关注
                case 3:// 相互关注
                    vh.statusTv.setText(R.string.label_mutual_follower);
                    vh.statusTv.setTextColor(getResources().getColor(R.color.text_black_color));
                    vh.statusTv.setBackgroundResource(R.drawable.border_1px_solid_white_black_radius_2dp);
                    break;
            }

            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(vh, position);
                }
            });

            vh.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.OnItemLongClick(vh, position);
                    return true;
                }
            });

            vh.statusTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (dto.getStatus()) {
                        case 0:
                        case 1:// 关注
                            follow(dto.getFriendId(), position);
                            break;
                        case 2:
                        case 3:// 取消关注
                            showUnfollowDialog(dto.getFriendId(), position);
                            break;
                    }
                }
            });

        }
    }
}
