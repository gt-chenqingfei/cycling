package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedBase;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemActivity;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemBase;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemImageTxtRecord;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemNotice;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.BottomScrollView;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/9.
 */
public class ClubFeedDetailsActivity extends SessionFragmentActivity implements CommentEditView.CommentPostListener, BottomScrollView.OnScrollToBottomListener,FeedItemBase.ClubFeedListener {

    public static final String EXTRA_FID = "feed_id";
    public static final String EXTRA_IS_MY_CLUB = "is_my_club";
    public static final String EXTRA_CLUBSHOWINPUT = "club_show_input";
    private FeedItemBase feedItemBase = null;
    ClubFeedBase base = null;
    private ClubFeedManager clubFeedManager;
    private int page = 1;
    private int count = 5;
    private List<ClubFeedComment> clubFeedCommentsList;
    private CommentEditView commentEditView;
    private FrameLayout commentPostContainer;
    private int fId;
    private boolean isShowInput;
    private BottomScrollView bottomScrollView;
    private boolean isLoading = false;
    View convertView;
    AVUser user;
    boolean isMyClub =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = AVUser.getCurrentUser();
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Intent it = getIntent();
        if (it == null) {
            this.finish();
            return;
        }

        isMyClub = it.getBooleanExtra(EXTRA_IS_MY_CLUB,false);
        fId = it.getIntExtra(EXTRA_FID,0);
        isShowInput =it.getBooleanExtra(EXTRA_CLUBSHOWINPUT, false);

        clubFeedManager = new ClubFeedManager(this);
        setContentView(R.layout.activity_clubfeed_deatils);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        convertView = LayoutInflater.from(this).inflate(R.layout.clubfeed_item_base, null);
        container.addView(convertView);
        commentPostContainer = (FrameLayout) findViewById(R.id.comment_post_container);
        this.commentEditView = new CommentEditView(this);
        this.commentEditView.setListener(this);
        this.commentPostContainer.addView(commentEditView);

        commentEditView.setClubFeedVisibility();
        clubFeedCommentsList = new ArrayList<>();

        getClubFeedList(fId);
        bottomScrollView = (BottomScrollView) findViewById(R.id.bottomScrollView);
        bottomScrollView.setOnScrollToBottomLintener(this);
    }

    @Override
    public void onScrollBottomListener(boolean isBottom) {
        if (isLoading)
            return;
        isLoading = true;
        page++;
        getClubFeedCommentList();
    }


    private void initView(ClubFeed result){

        switch (result.getFeedType()){
            case ClubFeed.FEED_TYPE_ACTIVITY:
                feedItemBase = new FeedItemActivity(this, convertView,user);
                base = result.getActivity();
                break;
            case ClubFeed.FEED_TYPE_NOTICE:
                feedItemBase = new FeedItemNotice(this, convertView,user);
                base = result.getNotice();
                break;
            case ClubFeed.FEED_TYPE_TEXT_IMAGE_RECORD:
                feedItemBase = new FeedItemImageTxtRecord(this, convertView,user);
                base = result.getImageTxt();
                break;
        }

        feedItemBase.setCommentEditView(commentEditView);
        feedItemBase.bind(base);
        feedItemBase.setMyClub(isMyClub);
        feedItemBase.setClubFeedListener(this);
    }

    /**
     * 获取俱乐Feed 详情
     *
     * @param fid
     */
    private void getClubFeedList(final int fid) {

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubFeed>() {

                    @Override
                    protected ClubFeed doInBackground(String... params) {
                        try {
                            return clubFeedManager.getClubFeedInfo(fid);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubFeed result) {
                        if (null == result)
                            return;

                        initView(result);


                        getClubFeedCommentList();
                        getClubFeedLikeList(fid);
                    }

                });
    }

    private void getClubFeedCommentList() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubFeedComment>>() {

            @Override
            protected List<ClubFeedComment> doInBackground(Void... params) {
                try {
                    return clubFeedManager.getClubFeedCommentList(fId, page, count);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ClubFeedComment> clubFeedComments) {
                if (clubFeedComments == null || clubFeedComments.size() == 0 || base == null) {
                    return;
                }
                clubFeedCommentsList.addAll(clubFeedComments);
                base.setCommentList(clubFeedCommentsList);
                feedItemBase.bind(base);
                isLoading = false;
            }
        });
    }

    private void getClubFeedLikeList(final int fid) {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubUser>>() {

            @Override
            protected List<ClubUser> doInBackground(Void... params) {
                try {
                    return clubFeedManager.getClubFeedLikeList(fid, 1, 5);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ClubUser> clubUsers) {
//                super.onPostExecute(clubUsers);
                if (clubUsers == null || clubUsers.size() == 0 || base == null)
                    return;
                base.setLikeList(clubUsers);
                feedItemBase.bind(base);
            }
        });
    }

    @Override
    public void doPost(final String content, final int replyId, final int feedId) {
        SpeedxAnalytics.onEvent(this, "", "click_comment_send");
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubFeedComment>() {

                    @Override
                    protected ClubFeedComment doInBackground(String... params) {
                        try {
                            return clubFeedManager.postCommentClubFeed(fId, content, replyId);
                        } catch (BusinessException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubFeedComment result) {
                        if (result != null) {
                            clubFeedCommentsList.add(result);
                            feedItemBase.bind(base);
                        }
                    }

                });
    }

    @Override
    public void onDeleteFeed(int id) {
        SharedPreferences userSp  = getSharedPreferences(getUserId() , 0);
        userSp.edit().putLong(Constants.PREF_CLUB_REFRESH,System.currentTimeMillis()).apply();
        this.finish();
    }

    @Override
    public void onComment(int id, ClubFeedComment comment) {

    }

    @Override
    public void onDeleteComment(int fid, int cid) {

    }

    @Override
    public void onPraise(int id, boolean isPraise) {

    }
}
