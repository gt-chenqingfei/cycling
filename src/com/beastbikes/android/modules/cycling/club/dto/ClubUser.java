package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubUser implements Serializable {
    private String userId;
    private String nickName;
    private String avatar;
    private String remarks;

    public ClubUser() {
    }

    public ClubUser(String userId, String nickName, String avatar) {
        this.userId = userId;
        this.nickName = nickName;
        this.avatar = avatar;
    }

    public ClubUser(JSONObject object) {
        if (JSONUtil.isNull(object))
            return;
        this.userId = object.optString("userId");
        this.nickName = object.optString("nickname");
        this.avatar = object.optString("avatar");
        this.remarks = object.optString("remarks");
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
