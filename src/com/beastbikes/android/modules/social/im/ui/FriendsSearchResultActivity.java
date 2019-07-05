package com.beastbikes.android.modules.social.im.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.InputDialog;
import com.beastbikes.android.dialog.InputDialog.OnInputDialogClickListener;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.ListViewFooter;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.android.widget.PullRefreshListView.onListViewListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.friends_search_result_activity)
public class FriendsSearchResultActivity extends SessionFragmentActivity
        implements OnItemClickListener, OnInputDialogClickListener,
        onListViewListener {

    private static final Logger logger = LoggerFactory
            .getLogger(FriendsSearchResultActivity.class);

    public static final String EXTRA_SEARCH_CONTENT = "search_content";

    @IdResource(R.id.friends_search_result_list)
    private PullRefreshListView resultLv;

    @IdResource(R.id.friends_search_result_none)
    private TextView resultNoneTv;

    private String searchContent;
    private ResultAdapter adapter;
    private List<FriendDTO> resultList = new ArrayList<FriendDTO>();
    private int page = 1;

    private InputDialog messageDialog;
    private LoadingDialog loadingDialog;
    private FriendDTO currentFriend;

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

        final Intent intent = getIntent();
        if (null == intent) {
            return;
        }

        this.friendManager = new FriendManager(this);
        resultLv.resetHeadViewBackground(R.color.common_bg_color);
        this.resultLv.setDivider(getResources().getDrawable(
                R.drawable.listview_divider_line_margin_left));
        this.resultLv.setDividerHeight(1);
        this.resultLv.setOnItemClickListener(this);
        this.resultLv.setPullRefreshEnable(false);
        this.resultLv.setListViewListener(this);
        this.searchContent = intent.getStringExtra(EXTRA_SEARCH_CONTENT);

        this.adapter = new ResultAdapter(resultList);
        this.resultLv.setAdapter(adapter);

        this.searchFriend(searchContent);
        SpeedxAnalytics.onEvent(this, "搜索好友",null);
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
        final FriendDTO fd = (FriendDTO) parent.getItemAtPosition(position);
        if (null == fd) {
            return;
        }

        final Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, fd.getFriendId());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, fd.getRemarks());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR,fd.getAvatar());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME,fd.getNickname());
        this.startActivity(intent);
    }

    @Override
    public void onInputDialogClickOk(String text) {
        if (null != this.messageDialog) {
            this.messageDialog.dismiss();
        }

        if (TextUtils.isEmpty(text)) {
            text = getString(R.string.friends_add_friend_default_msg);
        }
        this.addFriend(this.currentFriend, text);
        SpeedxAnalytics.onEvent(this, "发送好友请求",null);
    }

    @Override
    public void onLoadMore() {
        this.searchFriend(searchContent);
    }

    @Override
    public void onRefresh() {

    }

    /**
     * 搜索好友
     *
     * @param searchContent
     */
    private void searchFriend(final String searchContent) {
        if (TextUtils.isEmpty(searchContent)) {
            return;
        }

        this.loadingDialog = new LoadingDialog(this, null, true);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<FriendDTO>>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected List<FriendDTO> doInBackground(String... param) {
                return friendManager.searchUserByNickname(searchContent, 1, 20);
            }

            @Override
            protected void onPostExecute(List<FriendDTO> result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                if (null == result) {
                    return;
                }

                resultLv.setPullLoadEnable(true);

                if (result.size() < 20) {
                    resultLv.setPullLoadEnable(false);
                    resultLv.stopLoadMore(ListViewFooter.STATE_NORMAL);
                }

                if (null == result || result.isEmpty()) {
                    if (page == 1) {
                        resultLv.setVisibility(View.GONE);
                        resultNoneTv.setVisibility(View.VISIBLE);
                    }
                    return;
                }

                if (page == 1) {
                    resultList.clear();
                }

                resultList.addAll(result);
                adapter.notifyDataSetChanged();
                page++;
                resultLv.setVisibility(View.VISIBLE);
                resultNoneTv.setVisibility(View.GONE);

            }

        });
    }

    /**
     * 添加好友
     *
     * @param friend
     * @param extra
     */
    private void addFriend(final FriendDTO friend, final String extra) {
        if (null == friend) {
            return;
        }

        final String userId = friend.getFriendId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

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
                return friendManager.addFriend(userId, extra);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }

                if (result) {
                    friend.setStatus(FriendDTO.FRIEND_STATUS_FOLLOW);
                    adapter.notifyDataSetChanged();
                }
            }

        });
    }

    /**
     * 接受／拒绝
     *
     * @param requestId
     */
    private void friendRequestCmd(final int requestId) {
        if (requestId == 0) {
            return;
        }

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
                return friendManager.friendRequestCmd(requestId, 0);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }

                if (result && null != currentFriend) {
                    currentFriend
                            .setStatus(FriendDTO.FRIEND_STATUS_FOLLOW_AND_FANS);
                    adapter.notifyDataSetChanged();
                }
            }

        });
    }

    private final class ResultAdapter extends BaseAdapter {

        private final List<FriendDTO> list;

        public ResultAdapter(List<FriendDTO> list) {
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
            final ResultViewHolder vh;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.apply_list_item, null);
                vh = new ResultViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ResultViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));
            return convertView;
        }

    }

    private final class ResultViewHolder extends ViewHolder<FriendDTO> {

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

        protected ResultViewHolder(View v) {
            super(v);
            this.refuse.setVisibility(View.GONE);
            this.status.setVisibility(View.GONE);
        }

        @Override
        public void bind(final FriendDTO t) {
            if (null == t) {
                return;
            }

            if (!TextUtils.isEmpty(t.getAvatar())) {
                Picasso.with(getContext()).load(t.getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).into(this.avatar);
            } else {
                this.avatar.setImageResource(R.drawable.ic_avatar);
            }

            this.nickname.setText(t.getNickname());

            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(t.getProvince())
                    && !t.getProvince().equals("null")) {
                sb.append(t.getProvince()).append("	 ");
            }

            if (!TextUtils.isEmpty(t.getCity()) && !t.getCity().equals("null")) {
                sb.append(t.getCity());
            }
            this.extraTv.setText(sb.toString());

            switch (t.getStatus()) {
                case FriendDTO.FRIEND_STATUS_ADD:// 加为好友
                    this.agree.setText(R.string.friends_add_friend);
                    this.agree.setClickable(true);
                    break;
                case FriendDTO.FRIEND_STATUS_FANS:// 接受好友申请
                    this.agree.setText(R.string.friends_apply_agree);
                    this.agree.setClickable(true);
                    break;
                case FriendDTO.FRIEND_STATUS_FOLLOW:// 等待验证
                    this.agree.setText(R.string.friends_wait_verification);
                    this.agree.setClickable(false);
                    break;
                case FriendDTO.FRIEND_STATUS_FOLLOW_AND_FANS:// 已添加
                    this.agree.setText(R.string.friends_already_add);
                    this.agree.setClickable(false);
                    break;
            }

            this.agree.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    currentFriend = t;
                    if (t.getStatus() == 0) {
                        messageDialog = new InputDialog(
                                FriendsSearchResultActivity.this, null,
                                getString(R.string.friends_add_friend_hint),
                                FriendsSearchResultActivity.this, 20, true,
                                false);
                        messageDialog.show();
                    }

                    if (t.getStatus() == 1) {
                        friendRequestCmd(t.getRequestId());
                        SpeedxAnalytics.onEvent(FriendsSearchResultActivity.this,
                                "同意好友申请",null);
                    }
                }
            });
        }
    }

}
