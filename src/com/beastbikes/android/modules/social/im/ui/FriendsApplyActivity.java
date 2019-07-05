package com.beastbikes.android.modules.social.im.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.widget.ListViewFooter;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.android.widget.PullRefreshListView.onListViewListener;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

@Alias("好友列表")
@MenuResource(R.menu.apply_clear_menu)
@LayoutResource(R.layout.friends_apply_activity)
public class FriendsApplyActivity extends SessionFragmentActivity implements
        OnItemClickListener, OnItemLongClickListener,
        onListViewListener, Constants {

    @IdResource(R.id.friend_apply_activity_none)
    private TextView applyNoneTv;

    @IdResource(R.id.friends_apply_activity_list)
    private PullRefreshListView applyLv;

    private SharedPreferences sp;
    private ApplyAdapter adapter;
    private List<FriendDTO> applyList = new ArrayList<FriendDTO>();
    private LoadingDialog loadingDialog;
    private int page = 1;

    private FriendManager friendManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.friendManager = new FriendManager(this);
        this.sp = getSharedPreferences(getUserId(), 0);
        this.applyLv.setDivider(getResources().getDrawable(
                R.drawable.listview_divider_line_margin_left));
        this.applyLv.setDividerHeight(1);
        this.applyLv.setOnItemClickListener(this);
        this.applyLv.setPullRefreshEnable(false);
        this.applyLv.setListViewListener(this);
        this.adapter = new ApplyAdapter(applyList);
        this.applyLv.setAdapter(adapter);
        this.applyLv.setOnItemClickListener(this);
        this.applyLv.setOnItemLongClickListener(this);
        this.registerForContextMenu(applyLv);
        this.friendRequestsList();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none,
                R.anim.activity_out_to_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final FriendDTO fad = (FriendDTO) parent.getItemAtPosition(position);
        if (null == fad) {
            return;
        }

        final Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, fad.getFriendId());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, fad.getAvatar());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, fad.getNickname());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, fad.getRemarks());
        this.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final FriendDTO fad = (FriendDTO) parent.getItemAtPosition(position);
        if (null == fad) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.apply_clear_item:
                if (null == applyList || applyList.isEmpty()) {
                    return true;
                }

                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setMessage(R.string.friends_clear_applys_dialog_title);
                dialog.setPositiveButton(R.string.clear, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        cleanFriendRequests();
                    }
                }).setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(v.getContext());
        inflater.inflate(R.menu.apply_delete_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        int position = menuInfo.position;
        if (position > 0)
            position = position - 1;
        final FriendDTO fad = (FriendDTO) this.adapter.getItem(position);
        if (null == fad)
            return true;

        switch (item.getItemId()) {
            case R.id.apply_delete_item:
                int requestId = fad.getRequestId();
                if (requestId == 0)
                    return true;

                this.friendRequestCmd(requestId, 1, position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onLoadMore() {
        this.friendRequestsList();
    }

    @Override
    public void onRefresh() {

    }

    /**
     * @param requestId
     * @param command   0 接受， 1 删除（拒绝）
     */
    private void friendRequestCmd(final int requestId, final int command,
                                  final int position) {
        this.loadingDialog = new LoadingDialog(this, null, true);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                return friendManager.friendRequestCmd(requestId, command);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }

                if (result) {
                    if (command == 0) {
                        FriendDTO fd = applyList.get(position);
                        fd.setStatus(FriendDTO.FRIEND_STATUS_FOLLOW_AND_FANS);
                        adapter.notifyDataSetChanged();

                        FriendManager manager = new FriendManager(
                                FriendsApplyActivity.this);
                        manager.saveFriend(getUserId(), fd);
                        AVAnalytics
                                .onEvent(FriendsApplyActivity.this, "同意好友申请");
                    }

                    if (command == 1) {
                        applyList.remove(position);
                        adapter.notifyDataSetChanged();
                        AVAnalytics
                                .onEvent(FriendsApplyActivity.this, "拒绝好友申请");
                    }

                }
            }

        });
    }

    /**
     * 获取好友请求列表
     */
    private void friendRequestsList() {
        this.loadingDialog = new LoadingDialog(this, null, true);
        this.getAsyncTaskQueue().add(
                new AsyncTask<Integer, Void, List<FriendDTO>>() {

                    @Override
                    protected void onPreExecute() {
                        if (null == ConnectivityUtils
                                .getActiveNetwork(getApplicationContext())) {
                            Toasts.show(
                                    getApplicationContext(),
                                    R.string.setting_fragment_item_upload_error_log_failed);
                            return;
                        }
                        if (null != loadingDialog) {
                            loadingDialog.show();
                        }
                    }

                    @Override
                    protected List<FriendDTO> doInBackground(Integer... params) {
                        return friendManager.friendRequestsList(1, 50);
                    }

                    @Override
                    protected void onPostExecute(List<FriendDTO> result) {
                        if (null != loadingDialog) {
                            loadingDialog.dismiss();
                        }
                        if (null == result || result.isEmpty()) {
                            return;
                        }

                        applyLv.setPullLoadEnable(true);

                        if (result.size() < 50) {
                            applyLv.setPullLoadEnable(false);
                            applyLv.stopLoadMore(ListViewFooter.STATE_NORMAL);
                        }

                        if (null == applyList) {
                            applyList = new ArrayList<>();
                        }

                        if (page == 1) {
                            applyList.clear();
                        }

                        applyList.addAll(result);
                        adapter.notifyDataSetChanged();

                        if (null == applyList || applyList.isEmpty()) {
                            applyLv.setVisibility(View.GONE);
                            applyNoneTv.setVisibility(View.VISIBLE);
                        } else {
                            applyLv.setVisibility(View.VISIBLE);
                            applyNoneTv.setVisibility(View.GONE);
                            Editor editor = sp.edit();
                            editor.putLong(PREF_FRIEND_APPLY_LAST_TIME,
                                    System.currentTimeMillis() / 1000);
                            editor.apply();
                        }
                    }

                });
    }

    /**
     * 清空好友请求
     */
    private void cleanFriendRequests() {
        if (null == applyList || applyList.isEmpty()) {
            return;
        }

        this.loadingDialog = new LoadingDialog(this, null, true);
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return friendManager.cleanFriendRequests();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }

                if (result) {
                    applyList.clear();
                    adapter.notifyDataSetChanged();
                    applyLv.setVisibility(View.GONE);
                    applyNoneTv.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private final class ApplyAdapter extends BaseAdapter {

        private final List<FriendDTO> list;

        public ApplyAdapter(List<FriendDTO> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ApplyViewHolder vh;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.apply_list_item, null);
                vh = new ApplyViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ApplyViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position), position);
            return convertView;
        }

    }

    private final class ApplyViewHolder extends ViewHolder<FriendDTO> {

        @IdResource(R.id.apply_list_item_avatar)
        private CircleImageView avatar;

        @IdResource(R.id.apply_list_item_nickname)
        private TextView nickname;

        @IdResource(R.id.apply_list_item_extra)
        private TextView extraTv;

        @IdResource(R.id.apply_list_item_btn_agree)
        private TextView agree;

        @IdResource(R.id.apply_list_item_btn_refuse)
        private TextView refuse;

        @IdResource(R.id.apply_list_item_status)
        private TextView status;

        protected ApplyViewHolder(View v) {
            super(v);
            this.refuse.setVisibility(View.GONE);
        }

        @Override
        public void bind(FriendDTO t) {

        }

        ;

        public void bind(final FriendDTO fat, final int position) {
            if (null == fat) {
                return;
            }

            if (!TextUtils.isEmpty(fat.getAvatar())) {
                Picasso.with(getContext()).load(fat.getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).into(this.avatar);
            } else {
                this.avatar.setImageResource(R.drawable.ic_avatar);
            }

            this.nickname.setText(NickNameRemarksUtil.disPlayName(fat.getNickname(), fat.getRemarks()));
            this.extraTv.setText(fat.getExtra());

            if (fat.getStatus() == 0) {
                this.agree.setVisibility(View.VISIBLE);
                this.agree.setText(R.string.friends_apply_agree);
                this.agree.setOnClickListener(new OnClickListener() {// 接受

                    @Override
                    public void onClick(View v) {
                        friendRequestCmd(fat.getRequestId(), 0,
                                position);
                    }
                });
                this.status.setVisibility(View.GONE);
            } else {
                this.agree.setVisibility(View.GONE);
                this.status.setText(R.string.friends_already_add);
                this.status.setVisibility(View.VISIBLE);
            }
        }

    }

}
