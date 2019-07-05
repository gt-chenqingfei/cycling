package com.beastbikes.android.modules.user.dto;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/2/17.
 */
public class WaterMarkSepLine extends WaterMarkWidgetBase {

    private int width;
    private int height;

    public WaterMarkSepLine(){

    }

    public WaterMarkSepLine(JSONObject jsonObject){
        super(jsonObject);
        this.width = jsonObject.optInt("width");
        this.height = jsonObject.optInt("height");
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

}
