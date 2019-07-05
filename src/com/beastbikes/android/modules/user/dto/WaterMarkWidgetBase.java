package com.beastbikes.android.modules.user.dto;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/2/17.
 */
public class WaterMarkWidgetBase {

    private int position;
    private int top;
    private int left;
    private int right;
    private int bottom;
    private int canvasWidth;
    private int canvasHeight;

    public WaterMarkWidgetBase() {
    }

    public WaterMarkWidgetBase(JSONObject jsonObject) {
        this.position = jsonObject.optInt("position");
        this.top = jsonObject.optInt("top");
        this.left = jsonObject.optInt("left");
        this.right = jsonObject.optInt("right");
        this.bottom = jsonObject.optInt("bottom");
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }
}
