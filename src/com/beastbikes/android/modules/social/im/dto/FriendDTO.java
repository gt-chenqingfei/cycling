package com.beastbikes.android.modules.social.im.dto;

import org.json.JSONObject;

import java.io.Serializable;

public class FriendDTO implements Serializable {

    private static final long serialVersionUID = -5382704204584792514L;

    /**
     * 加为好友
     * v2.2.0 未关注
     */
    public static final int FRIEND_STATUS_ADD = 0;
    /**
     * 接受好友申请
     * <p/>
     * v2.2.0 被关注
     */
    public static final int FRIEND_STATUS_FANS = 1;
    /**
     * 等待验证
     * v2.2.0 已关注
     */
    public static final int FRIEND_STATUS_FOLLOW = 2;
    /**
     * 好友
     * <p/>
     * v2.2.0 相互关注
     */
    public static final int FRIEND_STATUS_FOLLOW_AND_FANS = 3;
    /**
     * 未处理
     */
    public static final int FRIEND_STATUS_UNTREATED = 0;
    /**
     * 已处理（添加）
     */
    public static final int FRIEND_STATUS_PROCESSED = 1;

    private String objectId;

    private String friendId;

    private String avatar;

    private String nickname;

    private String extra;
    // 省份
    private String province;
    // 城市
    private String city;
    // 区县
    private String area;

    private String clubName;

    private String clubId;

    private int requestId;

    /**
     * 好友请求：0 未处理， 1 已添加
     * <p/>
     * 好友关系：0 加为好友, 1 接受好友申请, 2 等待同意, 3 已添加
     * <p/>
     * 好友列表：0 在对方好友列表中，2 仅在自己列表中, 3 好友
     * <p/>
     * 粉丝关系中：# 0 未关注, 1 被关注, 2 已关注, 3 相互关注
     */
    private int status;

    private long createTime;

    // 备注
    private String remarks;

    // 第三方昵称
    private String thirdNick;

    public FriendDTO() {

    }

    public FriendDTO(JSONObject json) {
        this.objectId = json.optString("objectId");
        this.friendId = json.optString("userId");
        this.avatar = json.optString("avatar");
        if (json.has("avatarImage")) {
            this.avatar = json.optString("avatarImage");
        }
        this.nickname = json.optString("nickname");
        this.extra = json.optString("extra");
        if (json.has("followStatu")) {
            this.status = json.optInt("followStatu");
        } else {
            this.status = json.optInt("status");
        }
        this.province = json.optString("province");
        this.city = json.optString("city");
        this.requestId = json.optInt("requestId");
        this.createTime = json.optLong("timestamp");
        this.remarks = json.optString("remarks");
        this.area = json.optString("area");

        this.clubId = json.optString("clubId");
        this.clubName = json.optString("clubName");
        this.thirdNick = json.optString("thirdNick");
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public void setStatus(int state) {
        this.status = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getCreateTime() {
        if (createTime <= 0) {
            createTime = System.currentTimeMillis() / 1000;
        }
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getThirdNick() {
        return thirdNick;
    }

    public void setThirdNick(String thirdNick) {
        this.thirdNick = thirdNick;
    }
}
