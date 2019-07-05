package com.beastbikes.android.modules.user.dto;

import java.io.Serializable;

/**
 * Created by caoxiao on 16/4/21.
 */
public class SeekFriendDTO implements Serializable {
    private String nickName;
    private String seekValue;

    public SeekFriendDTO() {

    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSeekValue() {
        return seekValue;
    }

    public void setSeekValue(String seekValue) {
        this.seekValue = seekValue;
    }
}
