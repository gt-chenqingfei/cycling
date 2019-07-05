package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubFeed implements Serializable {
    public static final int FEED_TYPE_TEXT_IMAGE_RECORD = 1;
    public static final int FEED_TYPE_ACTIVITY = 2;
    public static final int FEED_TYPE_NOTICE = 3;
    public static final int FEED_TYPE_MAX = 4;

    public static final int STATE_DONE = 0;
    public static final int STATE_DOING = 1;

    private ClubFeedNotice notice;
    private ClubFeedImageTxtRecord imageTxt;
    private ClubFeedActivity activity;
    private ClubFeedPost post;
    private int feedType;
    private int fid;
    private long stamp;
    private int state = STATE_DONE;

    public ClubFeed() {

    }

    public ClubFeed(int fid) {
        this.fid = fid;
    }

    public ClubFeed(JSONObject object) {
        if (object != null) {
            this.feedType = object.optInt("feedType");
            this.fid = object.optInt("fid");
            this.stamp = object.optLong("stamp");
            this.state = object.optInt("state");
            JSONObject feedInfo = object.optJSONObject("feedInfo");
            JSONObject userObj = object.optJSONObject("user");
            ClubUser user = null;

            if (!JSONUtil.isNull(userObj)) {
                user = new ClubUser(userObj);
            }
            int likeCount = object.optInt("likeCount");
            int commentCount = object.optInt("commentCount");
            List<ClubUser> likeList = null;
            List<ClubFeedComment> commentList = null;
            JSONArray likeListArray = object.optJSONArray("likeList");

            if (!JSONUtil.isNull(likeListArray)) {
                likeList = new ArrayList<ClubUser>();
                for (int i = 0; i < likeListArray.length(); i++) {
                    JSONObject obj = likeListArray.optJSONObject(i);
                    if (obj != null) {
                        likeList.add(new ClubUser(obj));
                    }
                }
            }

            JSONArray commontArray = object.optJSONArray("commentList");
            if (!JSONUtil.isNull(commontArray)) {
                commentList = new ArrayList<ClubFeedComment>();
                for (int i = 0; i < commontArray.length(); i++) {
                    JSONObject commontObj = commontArray.optJSONObject(i);
                    if (!JSONUtil.isNull(commontObj)) {
                        ClubFeedComment common = new ClubFeedComment(commontObj);
                        commentList.add(common);
                    }
                }
            }

            ClubFeedBase base = null;

            switch (feedType) {
                case FEED_TYPE_TEXT_IMAGE_RECORD:
                    this.imageTxt = new ClubFeedImageTxtRecord(feedInfo);
                    base = this.imageTxt;
                    break;
                case FEED_TYPE_NOTICE:
                    this.notice = new ClubFeedNotice(feedInfo);
                    base = this.notice;
                    break;
                case FEED_TYPE_ACTIVITY:
                    this.activity = new ClubFeedActivity(feedInfo);
                    base = this.activity;
                    break;
            }

            base.setLikeCount(likeCount);
            base.setCommentCount(commentCount);
            base.setLikeList(likeList);
            base.setCommentList(commentList);
            base.setUser(user);
            base.setFid(fid);
            base.setFeedType(this.feedType);
            base.setHasLiked(object.optBoolean("hasLiked"));
            base.setDate(new Date(stamp * 1000 + TimeZone.getDefault().getRawOffset()));
        }
    }


    public ClubFeedImageTxtRecord getImageTxt() {
        return imageTxt;
    }

    public void setImageTxt(ClubFeedImageTxtRecord imageTxt) {
        this.imageTxt = imageTxt;
    }

    public int getFeedType() {
        return feedType;
    }

    public void setFeedType(int feedType) {
        this.feedType = feedType;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public ClubFeedNotice getNotice() {
        return notice;
    }

    public void setNotice(ClubFeedNotice notice) {
        this.notice = notice;
    }

    public ClubFeedActivity getActivity() {
        return activity;
    }

    public void setActivity(ClubFeedActivity activity) {
        this.activity = activity;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ClubFeedPost getPost() {
        return post;
    }

    public void setPost(ClubFeedPost post) {
        this.post = post;
    }
}
