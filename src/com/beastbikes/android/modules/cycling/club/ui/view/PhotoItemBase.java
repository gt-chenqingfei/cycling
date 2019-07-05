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
import com.beastbikes.android.modules.cycling.club.dto.ClubPhotoDTO;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class PhotoItemBase<K> extends LinearLayout implements View.OnClickListener {

    private K k;
    protected CircleImageView avatar;
    protected TextView extra;
    protected TextView userNickName;
    protected TextView time;
    protected TextView delete;
    protected TextView text;
    protected ImageView praise;
    protected ImageView comment;
    protected ImageView container;
    protected FrameLayout commonContainer;
    protected View converView;
    protected PhotoItemComment common;
    protected String userId;
    protected SessionFragmentActivity activity;
    protected ClubFeedManager clubFeedManager;
    protected ClubFeedListener listener;
    protected CommentEditView commentEditView;

    private ClubPhotoDTO photoDTO;

    public interface ClubFeedListener {
        public void onDeleteFeed(int id);
    }

    public void setClubFeedListener(ClubFeedListener listener) {
        this.listener = listener;
    }

    public void setCommentEditView(CommentEditView v) {
        this.commentEditView = v;
    }

    public PhotoItemBase(Context context, View converView, CommentEditView commentEditView) {
        super(context);
        if (context != null && context instanceof SessionFragmentActivity) {
            this.activity = (SessionFragmentActivity) context;
        }
        clubFeedManager = new ClubFeedManager(activity);
        this.converView = converView;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        this.converView.setLayoutParams(lp);
        final AVUser current = AVUser.getCurrentUser();
        if (null != current) {
            this.userId = current.getObjectId();
        }

        this.commentEditView = commentEditView;

        initBaseView();
    }

    private void initBaseView() {
        this.userNickName = (TextView) this.converView.findViewById(R.id.activity_complete_nickname);
        this.time = (TextView) this.converView.findViewById(R.id.time);
        this.delete = (TextView) this.converView.findViewById(R.id.delete);
        this.praise = (ImageView) this.converView.findViewById(R.id.praise);
        this.comment = (ImageView) this.converView.findViewById(R.id.comment);
        this.avatar = (CircleImageView) this.converView.findViewById(R.id.avatar);
        this.container = (ImageView) this.converView.findViewById(R.id.container);
        this.extra = (TextView) this.converView.findViewById(R.id.tv_extra);
        this.text = (TextView) this.converView.findViewById(R.id.text);
        commonContainer = (FrameLayout) this.converView.findViewById(R.id.common_container);
        this.delete.setVisibility(View.GONE);
        if (common == null) {
            common = new PhotoItemComment(getContext(), this.commentEditView);
            this.commonContainer.addView(common);
        }
    }

    public void bindBase(K k) {
        this.k = k;
        if (k instanceof ClubPhotoDTO) {
            this.photoDTO = (ClubPhotoDTO) k;

            time.setText(DateFormatUtil
                    .getRelativeTimeSpanString(this.activity, photoDTO.getCreateDate()));
            delete.setOnClickListener(this);
            praise.setOnClickListener(this);
            comment.setOnClickListener(this);
            avatar.setOnClickListener(this);

            if (!TextUtils.isEmpty(photoDTO.getContent()) && text != null) {
                text.setText(photoDTO.getContent() + "");
                text.setVisibility(View.VISIBLE);
            } else {
                text.setVisibility(View.GONE);
            }

            userNickName.setText(NickNameRemarksUtil.disPlayName(photoDTO.getNickName(), photoDTO.getRemarks()));

            if (!TextUtils.isEmpty(photoDTO.getAvatar())) {
                Picasso.with(getContext()).load(photoDTO.getAvatar()).fit().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).centerCrop().into(avatar);
            } else {
                avatar.setImageResource(R.drawable.ic_avatar);
            }

            if (!TextUtils.isEmpty(photoDTO.getImageUrl())) {
                Picasso.with(getContext()).load(photoDTO.getImageUrl()).fit().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).centerCrop().into(container);
            }
            if (photoDTO.getUserId().equals(userId)) {
                delete.setVisibility(View.VISIBLE);
            }
            int showNum = 0;
            int photoCommentNum = 0;
            int likeNum = 0;
            if (photoDTO.getCommentList() == null) {
                photoCommentNum = 0;
            } else {
                photoCommentNum = photoDTO.getCommentList().size();
            }
            if (photoDTO.getLikeUserList() == null) {
                likeNum = 0;
            } else {
                likeNum = photoDTO.getLikeUserList().size();
            }
            showNum = photoCommentNum + likeNum;
            if (showNum > 0) {
                commonContainer.setVisibility(VISIBLE);
                common.bind(photoDTO);
            } else {
                commonContainer.setVisibility(GONE);
            }

            praise.setSelected(photoDTO.isHasLiked());
        }
    }

    @Override
    public void onClick(View v) {
        if (k != null) {
            ClubPhotoDTO base = (ClubPhotoDTO) k;
            switch (v.getId()) {
                case R.id.avatar:
                    if (activity != null) {
                        final Intent intent = new Intent();
                        intent.setClass(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USER_ID, base.getUserId());
                        intent.putExtra(ProfileActivity.EXTRA_AVATAR, base.getAvatar());
                        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, base.getNickName());
                        intent.putExtra(ProfileActivity.EXTRA_REMARKS, base.getRemarks());
                        activity.startActivity(intent);
                    }
                    break;
                case R.id.praise:
                    likePhoto(v.isSelected() ? 1 : 0, base.getPhotoId());
                    break;
                case R.id.delete:
                    deleteConfirm(base.getPhotoId());
                    break;
                case R.id.comment:
                    commentEditView.togglePhotoSoftInput();
                    commentEditView.setParams(base.getPhotoId(), 0);
                    break;
                default:
                    break;
            }
        }

    }


    private void likePhoto(final int cmd, final int photoId) {
        if (activity != null) {
            activity.getAsyncTaskQueue().add(
                    new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {
                            return clubFeedManager.likeClubPhoto(cmd, photoId);
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {

                            if (result) {
                                ClubPhotoDTO c = (ClubPhotoDTO) k;
                                if (c != null) {
                                    c.setHasLiked(cmd == 0);
                                }
                                praise.setSelected(cmd == 0);
                            }

                        }

                    });
        }
    }

    private void deleteConfirm(final int photoId) {
        final MaterialDialog dialog = new MaterialDialog(getContext());
        dialog.setTitle(R.string.club_feed_del_hint);
        dialog.setMessage(R.string.club_feed_del_hint_del);
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteClubPhoto(photoId);
            }
        }).setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    private void deleteClubPhoto(final int photoId) {
        if (activity != null) {
            activity.getAsyncTaskQueue().add(
                    new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {
                            return clubFeedManager.deleteClubPhoto(photoId);
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (result) {
                                activity.finish();
                            }
                        }

                    });
        }
    }

}