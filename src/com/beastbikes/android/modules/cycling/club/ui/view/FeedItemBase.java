package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedBase;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by chenqingfei on 15/12/3.
 */
public abstract class FeedItemBase<K> extends LinearLayout implements View.OnClickListener, CommentEditView.CommentPostListener {

    private K k;
    protected CircleImageView avatar;
    protected TextView extra;
    protected TextView userNickName;
    protected TextView time;
    protected TextView delete;
    protected TextView text;
    private ImageView praise;
    protected ImageView comment;
    protected FrameLayout container;
    protected FrameLayout commonContainer;
    protected View converView;
    protected FeedItemComment commentItemView;
    protected SessionFragmentActivity activity;
    protected ClubFeedManager clubFeedManager;
    protected ClubFeedListener listener;
    protected CommentEditView commentEditView;
    protected AVUser currentUser;
    private Context context;
    public boolean isMyClub = false;

    public interface ClubFeedListener {
        public void onDeleteFeed(int id);

        public void onComment(int id, ClubFeedComment comment);

        public void onDeleteComment(int fid, int cid);

        public void onPraise(int id, boolean isPraise);
    }

    public void setClubFeedListener(ClubFeedListener listener) {
        this.listener = listener;
    }

    public void setMyClub(boolean isMyClub) {
        this.isMyClub = isMyClub;
    }

    public void setCommentEditView(CommentEditView v) {
        this.commentEditView = v;
        commentItemView.setCommentEditView(v);
    }

    public FeedItemBase(Context context, View converView, AVUser user) {
        super(context);
        this.context = context;
        this.currentUser = user;
        if (context != null && context instanceof SessionFragmentActivity) {

            this.activity = (SessionFragmentActivity) context;
        }
        clubFeedManager = new ClubFeedManager(activity);
        this.converView = converView;


        initBaseView();
    }

    public abstract void initView();

    public abstract void bind(K k);

    public abstract View getView();

    protected abstract void onClick(View v, K k);


    private void initBaseView() {
        this.userNickName = (TextView) this.converView.findViewById(R.id.activity_complete_nickname);
        this.time = (TextView) this.converView.findViewById(R.id.time);
        this.delete = (TextView) this.converView.findViewById(R.id.delete);
        this.praise = (ImageView) this.converView.findViewById(R.id.praise);
        this.comment = (ImageView) this.converView.findViewById(R.id.comment);
        this.avatar = (CircleImageView) this.converView.findViewById(R.id.avatar);
        this.container = (FrameLayout) this.converView.findViewById(R.id.container);
        this.extra = (TextView) this.converView.findViewById(R.id.tv_extra);
        this.text = (TextView) this.converView.findViewById(R.id.text);
        commonContainer = (FrameLayout) this.converView.findViewById(R.id.common_container);
//        this.delete.setVisibility(View.GONE);

        delete.setOnClickListener(this);
        praise.setOnClickListener(this);
        comment.setOnClickListener(this);
        avatar.setOnClickListener(this);

        View view = getView();
        if (view != null) {
            this.container.addView(view);
            initView();
        }
        if (commentItemView == null) {
            commentItemView = new FeedItemComment(getContext(), currentUser.getObjectId(), this);
            this.commonContainer.addView(commentItemView);
        }
    }

    protected void bindBase(K k) {
        this.k = k;
        if (k instanceof ClubFeedBase) {
            ClubFeedBase c = (ClubFeedBase) k;

            String timeStr = DateFormatUtil.getRelativeTimeSpanString4Feed(c.getDate());
            time.setText(timeStr);

            if (!TextUtils.isEmpty(c.getText()) && text != null) {
                text.setText(c.getText() + "");
                text.setVisibility(View.VISIBLE);
            } else {
                text.setVisibility(View.GONE);
            }
            ClubUser user = c.getUser();

            if (user != null) {
                userNickName.setText(NickNameRemarksUtil.disPlayName(user.getNickName(), user.getRemarks()));
                if (user.getUserId().equals(currentUser.getObjectId())) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(user.getAvatar())) {
                    Picasso.with(context).load(user.getAvatar()).fit().error(R.drawable.ic_avatar)
                            .placeholder(R.drawable.ic_avatar).centerCrop().into(avatar);
                } else {
                    avatar.setImageResource(R.drawable.ic_avatar);
                }
            }

            if (c.getCommentCount() + c.getLikeCount() <= 0) {
                commonContainer.setVisibility(GONE);
            } else {
                commonContainer.setVisibility(VISIBLE);
                commentItemView.bind(c);
            }

            if (c.getStatus() == ClubFeed.STATE_DOING) {
                praise.setVisibility(View.INVISIBLE);
                comment.setVisibility(View.INVISIBLE);
            }

            praise.setSelected(c.isHasLiked());
        }
    }

    @Override
    public void onClick(View v) {
        if (k != null) {
            ClubFeedBase base = (ClubFeedBase) k;
            switch (v.getId()) {
                case R.id.avatar:
                    if (activity != null) {
                        final Intent intent = new Intent();
                        intent.setClass(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USER_ID, base.getUser().getUserId());
                        intent.putExtra(ProfileActivity.EXTRA_AVATAR, base.getUser().getAvatar());
                        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, base.getUser().getNickName());
                        intent.putExtra(ProfileActivity.EXTRA_REMARKS, base.getUser().getRemarks());
                        activity.startActivity(intent);
                    }
                    break;
                case R.id.praise:
                    likeClubFeed(v.isSelected() ? 1 : 0, base.getFid());
                    break;
                case R.id.delete:
                    deleteConfirm(base.getFid(), base.getClubId(), base.getStatus());
                    break;
                case R.id.comment:
                    if (!isMyClub) {
                        Toasts.show(context, context.getResources().getString(R.string.clubfeed_handle_tip));
                        return;
                    }
                    commentEditView.toggleSoftInput();
                    commentEditView.setTextHint(getResources().getString(R.string.please_enter_comment));
                    commentEditView.setParams(base.getFid(), 0);
                    break;
                default:
                    onClick(v, k);
                    break;
            }
        }

    }


    private void likeClubFeed(final int cmd, final int feedId) {
        if (!isMyClub) {
            Toasts.show(context, context.getResources().getString(R.string.clubfeed_handle_tip));
            return;
        }
        if (activity != null) {
            activity.getAsyncTaskQueue().add(
                    new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {

                            try {
                                return clubFeedManager.likeClubFeed(cmd, feedId);
                            } catch (BusinessException e) {
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {

                            if (result) {
                                ClubFeedBase c = (ClubFeedBase) k;
                                if (c != null) {
                                    c.setHasLiked(cmd == 0);
                                }
                                praise.setSelected(cmd == 0);
                                if (listener != null) {
                                    listener.onPraise(feedId, cmd == 0);
                                }
                            }

                        }

                    });
        }
    }

    private void deleteConfirm(final int feedid, final String clubId, final int status) {
        final MaterialDialog dialog = new MaterialDialog(getContext());
        dialog.setTitle(R.string.club_feed_del_hint);
        dialog.setMessage(R.string.club_feed_del_hint_del);
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteClubFeed(feedid, clubId, status);
            }
        }).setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    private void deleteClubFeed(final int feedId, final String clubId, final int status) {
        if (activity != null) {
            activity.getAsyncTaskQueue().add(
                    new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {

                            try {
                                return clubFeedManager.deleteClubFeed(feedId, status, clubId);
                            } catch (BusinessException e) {
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {

                            if (result) {
                                if (listener != null) {
                                    listener.onDeleteFeed(feedId);
                                }
                            }

                        }

                    });
        }
    }


    @Override
    public void doPost(final String content, final int replyId, final int feedId) {
        if (activity == null) return;

        SpeedxAnalytics.onEvent(activity, "", "click_comment_send");
        activity.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubFeedComment>() {

                    @Override
                    protected ClubFeedComment doInBackground(String... params) {

                        try {
                            return clubFeedManager.postCommentClubFeed(feedId, content, replyId);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubFeedComment result) {

                        if (result != null) {
                            if (listener != null) {
                                listener.onComment(feedId, result);
                            }
                        }
                    }

                });

    }

}