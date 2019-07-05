package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ImageInfo implements Serializable {
    private String id;
    private String url;
    private int width;
    private int height;
    private String mine;

    public ImageInfo() {

    }
    public ImageInfo(JSONObject object) {
        if (object != null) {
            this.id = object.optString("id");
            this.url = object.optString("url");
            this.width = object.optInt("width");
            this.height = object.optInt("height");
            this.mine = object.optString("mine");
        }
    }

    public JSONObject obj2Json() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("id", this.id);
            jsonObj.put("url", this.url);
            jsonObj.put("width", this.width);
            jsonObj.put("height", this.height);
            jsonObj.put("mine", this.mine);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMine() {
        return mine;
    }

    public void setMine(String mine) {
        this.mine = mine;
    }
}
