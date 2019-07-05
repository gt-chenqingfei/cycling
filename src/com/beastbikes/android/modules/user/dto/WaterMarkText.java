package com.beastbikes.android.modules.user.dto;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/2/17.
 */
public class WaterMarkText extends WaterMarkWidgetBase {

    private int type;
    private String unit;
    private String title;
    private int fontSize;
    private int height;
    private String fontName;

    public static final String TEXTFONTREGULAR = "AvenirNext-Regular";
    public static final String TEXTFONTBOLD = "AvenirNext-Bold";

    public WaterMarkText() {

    }

    public WaterMarkText(JSONObject jsonObject) {
        super(jsonObject);
        this.type = jsonObject.optInt("type");
        this.unit = jsonObject.optString("unit");
        this.title = jsonObject.optString("title");
        this.fontSize = jsonObject.optInt("font-size");
        this.height = jsonObject.optInt("height");
        this.fontName = jsonObject.optString("font-name");
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
