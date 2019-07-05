package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.UserDetailDTO;
import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONObject;

/**
 * Created by caoxiao on 15/12/14.
 */
public class ClubMsgDTO {
    public static final int MSG_TYPE_FEED_LIKE = 0;
    public static final int MSG_TYPE_FEED_COMMENT = 0;
    public static final int MSG_TYPE_USER_FOLLOW = 0;
    public static final int MSG_TYPE_CLUB_TRANSFER = 0;
    public static final int MSG_TYPE_CLUB_TRANSFER_CANCEL = 0;
    public static final int MSG_TYPE_CLUB_TRANSFER_REJECT = 0;
    public static final int MSG_TYPE_CLUB_TRANSFER_QUIT = 0;

    public static final int SENDER_TYPE_USER =  0;
    public static final int SENDER_TYPE_CLUB =  1;

    private String senderId;
    private int senderType;
    private int msgType;
    private int metaId;
    private int status;
    private String content;
    private boolean isReply;
    private String imageUrl;
    private long stamp;
    private String createdAt;
    private JSONObject params;

    private ProfileDTO user;
    private ClubInfoCompact clubInfoCompact;

    public ClubMsgDTO(JSONObject jsonObject) {
        this.senderId = jsonObject.optString("senderId");
        this.msgType = jsonObject.optInt("msgType");
        this.metaId = jsonObject.optInt("metaId");
        this.status = jsonObject.optInt("status");
        this.content = jsonObject.optString("content");
        this.isReply = jsonObject.optBoolean("isReply");
        this.imageUrl = jsonObject.optString("imageUrl");
        this.stamp = jsonObject.optLong("stamp");
        this.createdAt = jsonObject.optString("createdAt");
        this.senderType = jsonObject.optInt("senderType");
        this.params= jsonObject.optJSONObject("data");

        JSONObject club = jsonObject.optJSONObject("club");
        if (!JSONUtil.isNull(club)) {
           clubInfoCompact = new ClubInfoCompact(club);
        }
        JSONObject userJson = jsonObject.optJSONObject("user");
        if(!JSONUtil.isNull(userJson)){
            user = new ProfileDTO(userJson);
        }
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMetaId() {
        return metaId;
    }

    public void setMetaId(int metaId) {
        this.metaId = metaId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setIsReply(boolean isReply) {
        this.isReply = isReply;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getSenderType() {
        return senderType;
    }

    public void setSenderType(int senderType) {
        this.senderType = senderType;
    }


    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public ProfileDTO getUser() {
        return user;
    }

    public void setUser(ProfileDTO user) {
        this.user = user;
    }

    public ClubInfoCompact getClubInfoCompact() {
        return clubInfoCompact;
    }

    public void setClubInfoCompact(ClubInfoCompact clubInfoCompact) {
        this.clubInfoCompact = clubInfoCompact;
    }
}
