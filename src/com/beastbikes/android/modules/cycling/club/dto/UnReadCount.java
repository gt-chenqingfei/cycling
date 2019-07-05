package com.beastbikes.android.modules.cycling.club.dto;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * 未读消息
 *
 * @author icedan
 */
public class UnReadCount implements Serializable {

    private static final long serialVersionUID = 5821005022419858014L;

    /**
     * 未读俱乐部消息count
     */
    private int clubMsg;

    /**
     * 未读用户消息count
     */
    private int userMsg;

    public UnReadCount(JSONObject json) {
        this.clubMsg = json.optInt("clubMsg");
        this.userMsg = json.optInt("userMsg");
    }

    public int getClubMsg() {
        return clubMsg;
    }

    public void setClubMsg(int clubMsg) {
        this.clubMsg = clubMsg;
    }

    public int getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(int userMsg) {
        this.userMsg = userMsg;
    }

}
