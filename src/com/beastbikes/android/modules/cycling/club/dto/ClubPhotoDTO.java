package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by icedan on 15/12/9.
 */
public class ClubPhotoDTO implements Serializable {

    private int photoId;

    private String userId;

    private String clubId;

    private String content;

    private String imageUrl;

    private int width;

    private int height;

    private boolean isEdit;

    private int likeNum;

    private boolean hasLiked;

    private int commentNum;

    private String createDate;

    private String nickName;

    private String remarks;

    private String avatar;

    private long headerId;

    private int headerCount;

    private List<ClubUser> likeUserList;

    private List<ClubFeedComment> commentList;

    public ClubPhotoDTO(JSONObject json) {
        this.photoId = json.optInt("photoId");
        this.userId = json.optString("userId");
        this.clubId = json.optString("clubId");
        if (json.has("imageMeta")) {
            JSONObject object = json.optJSONObject("imageMeta");
            this.imageUrl = object.optString("url");
            this.width = object.optInt("width");
            this.height = object.optInt("height");
        }
        this.likeNum = json.optInt("likeNum");
        this.hasLiked = json.optBoolean("hasLiked");
        this.commentNum = json.optInt("commentNum");
        this.createDate = json.optString("createdAt");
        this.content = json.optString("content");
        if (json.has("user")) {
            JSONObject user = json.optJSONObject("user");
            this.nickName = user.optString("nickname");
            this.remarks = user.optString("remarks");
            this.avatar = user.optString("avatar");
        }
        this.headerId = DateFormatUtil.getHeardId(createDate);
    }

    public ClubPhotoDTO(String imageUrl, int width, int height, String createDate) {
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
        this.headerId = DateFormatUtil.timeFormatOfString(createDate);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(long headerId) {
        this.headerId = headerId;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public List<ClubUser> getLikeUserList() {
        return likeUserList;
    }

    public void setLikeUserList(List<ClubUser> likeUserList) {
        this.likeUserList = likeUserList;
    }

    public List<ClubFeedComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<ClubFeedComment> commentList) {
        this.commentList = commentList;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "ClubPhotoDTO{" +
                "photoId=" + photoId +
                ", userId='" + userId + '\'' +
                ", clubId='" + clubId + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", isEdit=" + isEdit +
                ", likeNum=" + likeNum +
                ", hasLiked=" + hasLiked +
                ", commentNum=" + commentNum +
                ", createDate='" + createDate + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", headerId=" + headerId +
                ", headerCount=" + headerCount +
                '}';
    }
}
