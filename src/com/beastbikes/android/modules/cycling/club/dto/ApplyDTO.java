package com.beastbikes.android.modules.cycling.club.dto;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * 俱乐部入队申请信息
 */
public class ApplyDTO implements Serializable {

    private static final long serialVersionUID = -3530755736139133762L;

    /**
     * 记录id
     *
     * @type String
     */
    private String objectId;

    /**
     * 附加信息
     *
     * @type String
     */
    private String extra;

    /**
     * 当前状态(0未处理，1已处理，2撤销，3已删除)
     *
     * @type int
     */
    private int status;

    /**
     * 用户id
     *
     * @type String
     */
    private String userId;

    /**
     * 用户头像url
     *
     * @type String
     */
    private String avatarUrl;

    /**
     * 用户昵称
     *
     * @type String
     */
    private String nickname;

    /**
     * 备注
     */
    private String remarks;

    public ApplyDTO() {
    }

    public ApplyDTO(JSONObject json) {
        this.objectId = json.optString("objectId");
        this.extra = json.optString("extra");
        this.nickname = json.optString("nickname");
        this.status = json.optInt("status");
        this.userId = json.optString("userId");
        this.avatarUrl = json.optString("avatarImage");
        this.remarks = json.optString("remarks");
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
