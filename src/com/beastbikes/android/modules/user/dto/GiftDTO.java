package com.beastbikes.android.modules.user.dto;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by icedan on 16/6/27.
 */
public class GiftDTO implements Serializable {

    private int id;
    private String detail;
    private String image;
    // # 奖品类型 0: '实物',1: '虚拟',2: '流量',3: '抽奖'
    private int type;
    private String name;

    public GiftDTO(JSONObject json) {
        this.id = json.optInt("id");
        this.detail = json.optString("detail");
        this.image = json.optString("image");
        this.type = json.optInt("type");
        this.name = json.optString("name");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
