package com.beastbikes.android.modules.cycling.route.dto;

import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.util.Date;

public class RouteCommentDTO {

    private String id;

    /**
     * 线路ID
     */
    private String routeId;
    /**
     * 发表评论用户ID
     */
    private String userId;

    private String parentId;
    /**
     * 发表评论用户名
     */
    private String nickName;
    /**
     * 发表评论内容
     */
    private String content;
    /**
     * 发表评论的时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 线路总评论数
     */
    private int commentCount;
    private String avatarUrl;

    private String remarks;

    public RouteCommentDTO(JSONObject json) {
        this.id = json.optString("id");
        this.routeId = json.optString("routeId");
        this.userId = json.optString("userId");
        this.nickName = json.optString("nickname");
        this.content = json.optString("content");
        this.createTime = DateFormatUtil.stringFormat2Date(json.optString("createdAt"));
        this.parentId = json.optString("parentId");
        this.updateTime = DateFormatUtil.stringFormat2Date(json.optString("updatedAt"));
        this.avatarUrl = json.optString("avatar");
        this.remarks = json.optString("remarks");
    }

    public RouteCommentDTO(String id, String routeId, String userId,
                           String userName, String content, Date createTime) {
        this.id = id;
        this.routeId = routeId;
        this.userId = userId;
        this.nickName = userName;
        this.content = content;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime() {
        this.createTime = new Date(System.currentTimeMillis());
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime() {
        this.updateTime = new Date(System.currentTimeMillis());
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
