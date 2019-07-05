package com.beastbikes.android.modules.user.dto;

import android.graphics.Bitmap;

import com.squareup.picasso.Target;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/2/17.
 */
public class WaterMarkImage extends WaterMarkWidgetBase {

    private String whiteURL;
    private String blackURL;
    private int width;
    private int height;
    private Bitmap whiteBitmap;
    private Bitmap blackBitmap;
    //    private Target target;

    public WaterMarkImage() {

    }

    public WaterMarkImage(WaterMarkImage waterMarkImage) {
        this.whiteURL = waterMarkImage.getWhiteURL();
        this.blackURL = waterMarkImage.getBlackURL();
        this.width = waterMarkImage.getWidth();
        this.height = waterMarkImage.getHeight();
        this.setPosition(waterMarkImage.getPosition());
        this.setLeft(waterMarkImage.getLeft());
        this.setRight(waterMarkImage.getRight());
        this.setTop(waterMarkImage.getTop());
        this.setBottom(waterMarkImage.getBottom());
        this.setCanvasHeight(waterMarkImage.getCanvasHeight());
        this.setCanvasWidth(waterMarkImage.getCanvasWidth());
    }

    public WaterMarkImage(JSONObject jsonObject) {
        super(jsonObject);
        this.whiteURL = jsonObject.optString("white_url");
        this.blackURL = jsonObject.optString("black_url");
        this.width = jsonObject.optInt("width");
        this.height = jsonObject.optInt("height");
    }

    public String getWhiteURL() {
        return whiteURL;
    }

    public void setWhiteURL(String whiteURL) {
        this.whiteURL = whiteURL;
    }

    public String getBlackURL() {
        return blackURL;
    }

    public void setBlackURL(String blackURL) {
        this.blackURL = blackURL;
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

    public Bitmap getWhiteBitmap() {
        return whiteBitmap;
    }

    public void setWhiteBitmap(Bitmap whiteBitmap) {
        this.whiteBitmap = whiteBitmap;
    }

    public Bitmap getBlackBitmap() {
        return blackBitmap;
    }

    public void setBlackBitmap(Bitmap blackBitmap) {
        this.blackBitmap = blackBitmap;
    }

//    public Target getTarget() {
//        return target;
//    }
//
//    public void setTarget(Target target) {
//        this.target = target;
//    }
}
