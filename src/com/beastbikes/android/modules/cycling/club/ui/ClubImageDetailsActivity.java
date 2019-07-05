package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubPhotoDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.ui.view.PhotoItemBase;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icedan on 15/12/14.
 */
@LayoutResource(R.layout.club_image_details)
public class ClubImageDetailsActivity extends SessionFragmentActivity implements CommentEditView.CommentPostListener {

    public static final String EXTRA_PHOTO = "photo";

    @IdResource(R.id.comment_post_container)
    private FrameLayout editView;
    @IdResource(R.id.container)
    private FrameLayout container;
    private PhotoItemBase imageBase;
    private ClubFeedManager clubFeedManager;
    private ClubPhotoDTO photo;
    private CommentEditView commentEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }

        this.photo = (ClubPhotoDTO) intent.getSerializableExtra(EXTRA_PHOTO);
        if (null == photo) {
            finish();
            return;
        }

        this.clubFeedManager = new ClubFeedManager(this);
        this.commentEditView = new CommentEditView(this);
        this.commentEditView.setClubFeedVisibility();
        this.commentEditView.setListener(this);
        this.editView.addView(commentEditView);
        View convertView = LayoutInflater.from(this).inflate(R.layout.club_photo_item_base, null);
        container.addView(convertView);
        this.imageBase = new PhotoItemBase(this, convertView, this.commentEditView);
        this.imageBase.setCommentEditView(this.commentEditView);
        this.imageBase.bindBase(photo);
        this.getImageCommentList(photo.getPhotoId());
        this.getImageLikeList(photo.getPhotoId());
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void doPost(final String content, final int replyId, final int feedId) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubFeedComment>() {

                    @Override
                    protected ClubFeedComment doInBackground(String... params) {
                        return clubFeedManager.postClubPhotoComment(photo.getPhotoId(), content, replyId);
                    }

                    @Override
                    protected void onPostExecute(ClubFeedComment result) {
                        if (result != null) {
                            if(photo.getCommentList() == null)
                            {
                                photo.setCommentList(new ArrayList<ClubFeedComment>());
                            }
                            photo.getCommentList().add(result);
                            imageBase.bindBase(photo);
                            getImageCommentList(photo.getPhotoId());
                        }
                    }

                });
    }

    /**
     * 获取评论列表
     *
     * @param photoId
     */
    private void getImageCommentList(final int photoId) {
        this.getAsyncTaskQueue().add(new AsyncTask<Integer, Void, List<ClubFeedComment>>() {
            @Override
            protected List<ClubFeedComment> doInBackground(Integer... params) {
                return clubFeedManager.getClubPhotoCommentList(photoId, 1, 1000);
            }

            @Override
            protected void onPostExecute(List<ClubFeedComment> clubFeedComments) {
                if (null == clubFeedComments || clubFeedComments.size() <= 0) {
                    return;
                }

                photo.setCommentList(clubFeedComments);
                imageBase.bindBase(photo);
            }
        });

    }

    /**
     * 获取点赞列表
     *
     * @param photoId
     */
    private void getImageLikeList(final int photoId) {
        this.getAsyncTaskQueue().add(new AsyncTask<Integer, Void, List<ClubUser>>() {
            @Override
            protected List<ClubUser> doInBackground(Integer... params) {
                try {
                    return clubFeedManager.getClubPhotoLikeList(photoId, 1, 1000);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ClubUser> clubUsers) {
                if (null == clubUsers || clubUsers.size() <= 0) {
                    return;
                }

                photo.setLikeUserList(clubUsers);
                imageBase.bindBase(photo);
            }
        });
    }
}
