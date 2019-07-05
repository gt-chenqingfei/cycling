package com.beastbikes.android.modules.cycling.club.dto;

import android.text.TextUtils;

import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubFeedComment implements Serializable {

    private ClubUser user;
    private ClubUser replyUser;
    private int cid;
    private int fid;
    private int replyId;
    private String content;
    private String createdAt;
    public ClubFeedComment(){}
    public ClubFeedComment(JSONObject object)
    {
        if(object == null)
            return;
        this.cid = object.optInt("cid");
        this.fid = object.optInt("fid");
        if (object.has("photoId")) {
            this.fid = object.optInt("photoId");
        }

        this.createdAt = object.optString("createdAt");
        if(!TextUtils.isEmpty(createdAt)){
            this.createdAt = DateFormatUtil.getRelativeTimeSpanString4FeedByTime(createdAt);
        }

        this.replyId = object.optInt("replyId");
        this.content = object.optString("content");
        this.user = new ClubUser(object.optJSONObject("user"));
        JSONObject replyUserObject = object.optJSONObject("replyUser");
        if(!JSONUtil.isNull(replyUserObject)) {
            this.replyUser = new ClubUser(object.optJSONObject("replyUser"));
        }

    }
    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ClubUser getUser() {
        return user;
    }

    public void setUser(ClubUser user) {
        this.user = user;
    }

    public ClubUser getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(ClubUser replyUser) {
        this.replyUser = replyUser;
    }
    public String getCreateAt() {
        return createdAt;
    }

    public void setCreateAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
