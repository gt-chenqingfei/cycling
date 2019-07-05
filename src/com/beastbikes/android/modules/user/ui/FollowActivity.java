package com.beastbikes.android.modules.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationBean;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.android.widget.convenientbanner.ConvenientBanner;
import com.beastbikes.android.widget.convenientbanner.holder.CBViewHolderCreator;
import com.beastbikes.android.widget.convenientbanner.holder.Holder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.sina.weibo.SinaWeibo;

@LayoutResource(R.layout.activity_follow)
public class FollowActivity extends SessionFragmentActivity implements SwipeRefreshLoadRecyclerView.RecyclerCallBack,
        ItemClickListener, View.OnClickListener, TextView.OnEditorActionListener {

    // 搜索
    private static final int FOLLOW_TYPE_SEARCH = 0;
    // 手机通讯录
    private static final int FOLLOW_TYPE_CONTACT = 1;
    // 新浪微博
    private static final int FOLLOW_TYPE_SINA = 2;

    @IdResource(R.id.follow_activity_search_et)
    private EditText searchEt;

    @IdResource(R.id.activity_follow_empty_tip)
    private TextView followEmptyTip;

    @IdResource(R.id.follow_activity_list_parent_view)
    private LinearLayout parentView;

    @IdResource(R.id.activity_follow_search_bar)
    private View searchBar;

    @IdResource(R.id.follow_activity_other_banner)
    private ConvenientBanner bannerView;

    private SwipeRefreshLoadRecyclerView recyclerView;
    private List<FriendDTO> followerList = new ArrayList<>();
    private UserManager userManager;
    private FollowerAdapter followerAdapter;
    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.initBannerView();

        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        if (user.getObjectId().equals(getUserId())) {
            this.followEmptyTip.setText(R.string.activity_follow_empty_tip);
        } else {
            this.followEmptyTip.setText(R.string.activity_other_friend_follow_empty_tip);
        }

        this.userManager = new UserManager(this);
        this.recyclerView = new SwipeRefreshLoadRecyclerView(this, parentView, followerList,
                SwipeRefreshLoadRecyclerView.HASFOOTER);
        this.recyclerView.setRecyclerCallBack(this);
        this.followerAdapter = new FollowerAdapter(this, this);
        this.recyclerView.setAdapter(followerAdapter);

        this.searchEt.setOnEditorActionListener(this);
        pageIndex =1;

        this.getFollowerList();
    }



    @Override
    public void finish() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String searchContent = this.searchEt.getText().toString();
            if (TextUtils.isEmpty(searchContent)) {
                return false;
            }

            Intent intent = new Intent(this, FollowSearchResultActivity.class);
            intent.putExtra(FollowSearchResultActivity.EXTRA_SEARCH_CONTENT,
                    searchContent);
            intent.putExtra(FollowSearchResultActivity.EXTRA_FOLLOW_TYPE, FOLLOW_TYPE_SEARCH);
            intent.putExtra(FollowSearchResultActivity.EXTRA_TITLE, getString(R.string.friends_search_result_title));
            startActivity(intent);
        }

        return false;
    }

    @Override
    public void refreshCallBack() {
        this.pageIndex = 1;
        this.recyclerView.setCanLoadMore(true);
        this.recyclerView.setHasFooter(true);
        this.getFollowerList();
    }

    @Override
    public void loadMoreCallBack() {
        this.getFollowerList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follow_activity_search_et:
                break;
        }
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

    private void initBannerView() {
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        if (!user.getObjectId().equals(getUserId())) {
            this.bannerView.setVisibility(View.GONE);
            this.searchBar.setVisibility(View.GONE);
            return;
        }

        this.bannerView.setVisibility(View.VISIBLE);
        List<BannerDTO> list = new ArrayList<>();
        list.add(new BannerDTO(R.string.label_phone_contact, R.drawable.ic_follow_contact_icon,
                FOLLOW_TYPE_CONTACT, R.string.activity_finished_menu_weibo,
                R.drawable.ic_follow_weibo_icon, FOLLOW_TYPE_SINA));
        this.bannerView.setPages(new CBViewHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createHolder() {
                return new BannerViewHolder();
            }
        }, list);
//        this.bannerView.setPageIndicator(new int[]{R.drawable.circle_indicator_stroke, R.drawable.circle_indicator_solid});
//        this.bannerView.setPageIndicatorMargin(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL,
//                DimensionUtils.dip2px(this, 74));
        this.bannerView.setCanLoop(false);
        this.bannerView.setcurrentitem(0);
    }

    /**
     * 获取关注列表
     */
    private void getFollowerList() {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<FriendDTO>>() {
            @Override
            protected List<FriendDTO> doInBackground(String... params) {
                return userManager.getFollowList(getUserId(), pageIndex, 20);
            }

            @Override
            protected void onPostExecute(List<FriendDTO> friends) {
                recyclerView.finishLoad();
                if (null == friends || friends.size() <= 0) {
//                    recyclerView.noMoreData(false);
                    recyclerView.setCanLoadMore(false);
                    recyclerView.setHasFooter(false);
                    return;
                }

                followEmptyTip.setVisibility(View.GONE);

                if (friends.size() < 20) {
                    recyclerView.setCanLoadMore(false);
//                    recyclerView.noMoreData(false);
                    recyclerView.setHasFooter(false);
                }

                if (pageIndex == 1)
                    followerList.clear();

                pageIndex = pageIndex + 1;
                followerList.addAll(friends);
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
                if (null == followerList || position >= followerList.size()) {
                    return;
                }

                if (result) {
                    Toasts.show(FollowActivity.this, R.string.lable_follow_success_msg);
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
                    Toasts.show(FollowActivity.this, R.string.lable_unfollow_success_msg);
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

    public interface BannerItemClickListener {
        void onItemClick(int type, int label);
    }

    private class BannerViewHolder implements Holder<BannerDTO>, BannerItemClickListener {

        private LinearLayout item1;
        private ImageView img1;
        private TextView label1;

        private LinearLayout item2;
        private ImageView img2;
        private TextView label2;

        private LinearLayout item3;
        private ImageView img3;
        private TextView label3;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.follow_banner_item, null);
            this.item1 = (LinearLayout) view.findViewById(R.id.follow_banner_item_1);
            this.img1 = (ImageView) view.findViewById(R.id.follow_banner_item_img_1);
            this.label1 = (TextView) view.findViewById(R.id.follow_banner_item_label_1);

            this.item2 = (LinearLayout) view.findViewById(R.id.follow_banner_item_2);
            this.img2 = (ImageView) view.findViewById(R.id.follow_banner_item_img_2);
            this.label2 = (TextView) view.findViewById(R.id.follow_banner_item_label_2);

            this.item3 = (LinearLayout) view.findViewById(R.id.follow_banner_item_3);
            this.img3 = (ImageView) view.findViewById(R.id.follow_banner_item_img_3);
            this.label3 = (TextView) view.findViewById(R.id.follow_banner_item_label_3);
            return view;
        }

        @Override
        public void UpdateUI(Context context, int position, final BannerDTO data) {
            if (null == data) {
                return;
            }

            this.item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(data.type1, data.label1);
                }
            });
            this.img1.setImageResource(data.resId1);
            this.label1.setText(data.label1);

            this.img2.setImageResource(data.resId2);
            this.label2.setText(data.label2);
            this.item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(data.type2, data.label2);
                }
            });

//            this.item3.setOnClickListener(this);
//            this.img3.setImageResource(data.resId2);
//            this.label3.setText(data.label2);
        }

        @Override
        public void onItemClick(int type, int label) {
            final Intent resultIntent = new Intent(FollowActivity.this, FollowSearchResultActivity.class);
            resultIntent.putExtra(FollowSearchResultActivity.EXTRA_FOLLOW_TYPE, type);
            resultIntent.putExtra(FollowSearchResultActivity.EXTRA_TITLE, getString(label));
            if (type == 2) {
                AuthenticationFactory.getUserInfo(FollowActivity.this, SinaWeibo.NAME, new AuthenticationFactory.ShareSDKUserInfoCallBack() {
                    @Override
                    public void getShareSDKUserInfoCallBack(final AuthenticationBean shareSDKUserInfoBean) {
                        if (shareSDKUserInfoBean == null)
                            return;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                searchByWeibo(shareSDKUserInfoBean.getOpenId(), shareSDKUserInfoBean.getAccessToken());
                                resultIntent.putExtra(FollowSearchResultActivity.EXTRA_OPENID, shareSDKUserInfoBean.getOpenId());
                                resultIntent.putExtra(FollowSearchResultActivity.EXTRA_ACCESS_TOKEN, shareSDKUserInfoBean.getAccessToken());
                                startActivity(resultIntent);
                            }
                        });
                    }
                });
                return;
            }
            startActivity(resultIntent);
        }
    }

    private final class FollowerViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView nameTv;
        private TextView statusTv;
        private View view;

        protected FollowerViewHolder(View v) {
            super(v);
            this.view = v;
            this.avatar = (ImageView) v.findViewById(R.id.fans_and_follow_item_avatar);
            this.nameTv = (TextView) v.findViewById(R.id.fans_and_follow_item_name);
            this.statusTv = (TextView) v.findViewById(R.id.fans_and_follow_item_status);
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

    /**
     * banner
     */
    private class BannerDTO {
        private int label3;
        private int resId3;
        private int type3;

        private int label1;
        private int resId1;
        private int type1;

        private int label2;
        private int resId2;
        private int type2;

        public BannerDTO(int label1, int resId1, int type1, int label2, int resId2, int type2) {
            this.label1 = label1;
            this.resId1 = resId1;
            this.type1 = type1;
            this.label2 = label2;
            this.resId2 = resId2;
            this.type2 = type2;
        }
    }
}
