package com.beastbikes.android.modules.social.im.dao.entity;

import android.os.Parcel;

import com.beastbikes.android.persistence.BeastStore;
import com.beastbikes.android.persistence.BeastStore.Friend.FriendColumns;
import com.beastbikes.framework.persistence.PersistentObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = BeastStore.Friend.CONTENT_CATEGORY)
public class Friend implements PersistentObject, FriendColumns {

    private static final long serialVersionUID = -8227874145529936392L;

    /**
     * 唯一标识为：userId + ":" + friendId
     */
    @DatabaseField(columnName = _ID, canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = USER_ID)
    private String userId;

    @DatabaseField(columnName = FRIEND_ID)
    private String friendId;

    @DatabaseField(columnName = FRIEND_NICKNAME)
    private String nickname;

    @DatabaseField(columnName = FRIEND_AVATAR)
    private String avatar;

    /**
     * 0 为好友，1 非好友
     */
    @DatabaseField(columnName = STATUS)
    private int status;

    @DatabaseField(columnName = CREATE_TIME)
    private long createTime;

    //备注
    @DatabaseField(columnName = FRIEND_REMARKS)
    private String remarks;

    public Friend() {

    }

    public Friend(Parcel source) {
        this.id = source.readString();
        this.userId = source.readString();
        this.friendId = source.readString();
        this.nickname = source.readString();
        this.avatar = source.readString();
        this.status = source.readInt();
        this.createTime = source.readLong();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
