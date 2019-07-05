package com.beastbikes.android.modules.message.dto;

import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class MessageDTO implements Serializable {

    private static final long serialVersionUID = -1384512063795831984L;

    private String message;
    private Date availableTime;

    public MessageDTO(JSONObject json) {
        this.message = json.optString("message");
        this.availableTime = DateFormatUtil.stringFormat2Date(json.optString("availableTime"));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(Date availableTime) {
        this.availableTime = availableTime;
    }

}
