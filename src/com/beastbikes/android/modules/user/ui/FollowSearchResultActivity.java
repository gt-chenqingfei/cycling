package com.beastbikes.android.modules.user.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.SeekFriendDTO;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.activity_follow_serch_result)
public class FollowSearchResultActivity extends SessionFragmentActivity implements SwipeRefreshLoadRecyclerView.RecyclerCallBack,
        ItemClickListener {

    // 搜索
    public static final String EXTRA_SEARCH_CONTENT = "search_content";
    // follow type
    public static final String EXTRA_FOLLOW_TYPE = "follow_type";
    // title
    public static final String EXTRA_TITLE = "title";

    public static final String EXTRA_OPENID = "open_id";

    public static final String EXTRA_ACCESS_TOKEN = "access_token";

    @IdResource(R.id.follow_search_list_parent_view)
    private LinearLayout parentView;
    private SwipeRefreshLoadRecyclerView recyclerView;

    @IdResource(R.id.follow_search_result_activity_no_fans_view)
    private TextView noResultView;

    private UserManager userManager;
    private FollowerAdapter followerAdapter;
    private int pageIndex = 1;
    private List<FriendDTO> followerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            super.setTitle(title);
        }

        this.userManager = new UserManager(this);
        this.recyclerView = new SwipeRefreshLoadRecyclerView(this, parentView, followerList,
                SwipeRefreshLoadRecyclerView.HASFOOTER);
        this.recyclerView.setRecyclerCallBack(this);
        this.followerAdapter = new FollowerAdapter(this, this);
        this.recyclerView.setAdapter(this.followerAdapter);
        this.refreshView();
    }

    @Override
    public void refreshCallBack() {
        this.pageIndex = 1;
        this.recyclerView.setHasFooter(true);
        this.refreshView();
    }

    @Override
    public void loadMoreCallBack() {
        this.refreshView();
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        if (null == followerList || followerList.size() <= 0) {
            return;
        }

        final FriendDTO fans = followerList.get(position);
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
     * 根据类型刷新view
     */
    private void refreshView() {
        int type = getIntent().getIntExtra(EXTRA_FOLLOW_TYPE, 0);
        switch (type) {
            case 0:// 搜索
                String searchContent = getIntent().getStringExtra(EXTRA_SEARCH_CONTENT);
                if (TextUtils.isEmpty(searchContent)) {
                    return;
                }

                this.searchFriend(searchContent);
                break;
            case 1:// 手机通讯录
                try {
                    noResultView.setText(R.string.follow_search_result_empty_contacts);
                    recyclerView.setRefreshEnable(false);
                    final LoadingDialog loadingDialog = new LoadingDialog(this, null, true);
                    getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<FriendDTO>>() {

                        @Override
                        protected void onPreExecute() {
                            if (null != loadingDialog) {
                                loadingDialog.show();
                            }
                        }

                        @Override
                        protected List<FriendDTO> doInBackground(Void... voids) {
                            try {
                                return getPhoneContact();
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(List<FriendDTO> friends) {
                            if (null != loadingDialog) {
                                loadingDialog.dismiss();
                            }
                            handleResult(friends);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:// 微博
                noResultView.setText(R.string.follow_search_result_empty_weibo);
                recyclerView.setRefreshEnable(false);
                Intent intent = getIntent();
                searchByWeibo(intent.getStringExtra(EXTRA_OPENID), intent.getStringExtra(EXTRA_ACCESS_TOKEN));
                break;
        }
    }

    private void searchByWeibo(final String thirdKey, final String thirdToken) {
        final LoadingDialog loadingDialog = new LoadingDialog(this, null, true);
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<FriendDTO>>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected List<FriendDTO> doInBackground(Void... voids) {
                return userManager.seekFriends(UserManager.SEEK_TYPE_WEIBO, thirdKey, thirdToken, null);
            }

            @Override
            protected void onPostExecute(List<FriendDTO> friends) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                handleResult(friends);
            }
        });
    }

    private void handleResult(List<FriendDTO> friends) {
        recyclerView.finishLoad();
        if (null == friends || friends.size() <= 0) {
            recyclerView.noMoreData(false);
            recyclerView.setHasFooter(false);

            if (pageIndex == 1) {
                noResultView.setVisibility(View.VISIBLE);
            }
            return;
        }

        noResultView.setVisibility(View.GONE);
        if (friends.size() < 20) {
            recyclerView.noMoreData(false);
            recyclerView.setHasFooter(false);
        }

        if (pageIndex == 1)
            followerList.clear();

        pageIndex = pageIndex + 1;
        followerList.addAll(friends);
        recyclerView.notifyDataSetChanged();
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

        final LoadingDialog loadingDialog = new LoadingDialog(this, null, true);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<FriendDTO>>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected List<FriendDTO> doInBackground(String... param) {
                FriendManager friendManager = new FriendManager(FollowSearchResultActivity.this);
                return friendManager.searchUserByNickname(searchContent, pageIndex, 20);
            }

            @Override
            protected void onPostExecute(List<FriendDTO> friends) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                handleResult(friends);
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
                if (null == followerList || position >= followerList.size()) {
                    return;
                }

                if (result) {
                    Toasts.show(FollowSearchResultActivity.this, R.string.lable_follow_success_msg);
                    final FriendDTO fans = followerList.get(position);
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
                if (null == followerList || position >= followerList.size()) {
                    return;
                }

                if (result) {
                    Toasts.show(FollowSearchResultActivity.this, R.string.lable_unfollow_success_msg);
                    final FriendDTO fans = followerList.get(position);
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

    public List<FriendDTO> getPhoneContact() throws Exception {
        ContentResolver resolver = getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        List<SeekFriendDTO> seekFriendList = new ArrayList<>();
        if (phoneCursor != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            while (phoneCursor.moveToNext()) {
                //获得联系人号码
                String phoneNumber = phoneCursor.getString(2);
                if (phoneNumber == null) {
                    continue;
                }
                //联系人名称
                String name = phoneCursor.getString(1);
                SeekFriendDTO seekFriendDTO = new SeekFriendDTO();
                seekFriendDTO.setNickName(name);
                try {
                    Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "CN");
                    seekFriendDTO.setSeekValue(phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
                } catch (NumberParseException e) {
//                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
                seekFriendList.add(seekFriendDTO);
            }
            phoneCursor.close();
        }
        if (seekFriendList != null && seekFriendList.size() > 0) {
            return userManager.seekFriends(UserManager.SEEK_TYPE_PHONE, "", "", seekFriendList);
        }
        return null;
    }

    private final class FollowerViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView nameTv;
        private TextView statusTv;
        private TextView locationTv;
        private TextView clubNameTv;
        private View view;

        protected FollowerViewHolder(View v) {
            super(v);
            this.view = v;
            this.avatar = (ImageView) v.findViewById(R.id.follow_search_result_item_avatar);
            this.nameTv = (TextView) v.findViewById(R.id.follow_search_result_item_name);
            this.statusTv = (TextView) v.findViewById(R.id.follow_search_result_item_status);
            this.locationTv = (TextView) v.findViewById(R.id.follow_search_result_item_value);
            this.clubNameTv = (TextView) v.findViewById(R.id.follow_search_result_item_club_name);
        }

    }

    private final class FollowerAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {

        private Context context;
        private ItemClickListener itemClickListener;

        public FollowerAdapter(Context context, ItemClickListener listener) {
            this.context = context;
            this.itemClickListener = listener;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder() {
            FollowerViewHolder holder = new FollowerViewHolder(LayoutInflater.from(
                    context).inflate(R.layout.follow_search_result_item, null,
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

            final FollowerViewHolder vh = (FollowerViewHolder) holder;
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

            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(dto.getThirdNick())) {
                if (!TextUtils.isEmpty(dto.getProvince()) && !dto.getProvince().equals("null")) {
                    sb.append(dto.getProvince()).append(" ");
                }

                if (!dto.getProvince().equals("null")) {
                    sb.append(dto.getCity());
                }
            } else {
                sb.append(dto.getThirdNick());
            }
            vh.locationTv.setText(sb.toString());
            if (!TextUtils.isEmpty(dto.getClubName())) {
                vh.clubNameTv.setText(dto.getClubName());
            }

            switch (dto.getStatus()) {
                case 0:// 未关注
                case 1:// 被关注
                    vh.statusTv.setText(R.string.profile_fragment_follow);
                    vh.statusTv.setTextColor(getResources().getColor(R.color.designcolor_c7));
                    vh.statusTv.setBackgroundResource(R.drawable.border_1px_solid_red_radius_6dp);
                    break;
                case 2:// 已关注
                    vh.statusTv.setText(R.string.label_already_follower);
                    vh.statusTv.setTextColor(getResources().getColor(R.color.text_black_color));
                    vh.statusTv.setBackgroundResource(R.drawable.border_1px_solid_white_black_radius_2dp);
                    break;
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
