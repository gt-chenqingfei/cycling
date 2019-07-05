package com.beastbikes.android.modules.user.dto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/2/17.
 */
public class WaterMark {

    private String name;
    private int canvasWidth;
    private int canvasHeight;
    private List<WaterMarkImage> waterMarkImages;
    private List<WaterMarkText> waterMarkTexts;
    private List<WaterMarkSepLine> waterMarkSepLines;

    public WaterMark() {

    }

    public WaterMark(JSONObject jsonObject) {
        this.name = jsonObject.optString("name");
        this.canvasWidth = jsonObject.optInt("canvas_width");
        this.canvasHeight = jsonObject.optInt("canvas_height");

        JSONArray imagesJsonArray = jsonObject.optJSONArray("images");
        if (imagesJsonArray != null && imagesJsonArray.length() > 0) {
            waterMarkImages = new ArrayList<>();
            for (int i = 0; i < imagesJsonArray.length(); i++) {
                JSONObject jsonObject1 = imagesJsonArray.optJSONObject(i);
                if (jsonObject1 == null)
                    continue;
                WaterMarkImage waterMarkImage = new WaterMarkImage(jsonObject1);
                waterMarkImage.setCanvasWidth(canvasWidth);
                waterMarkImage.setCanvasHeight(canvasHeight);

                waterMarkImages.add(waterMarkImage);
            }
        }

        JSONArray textJsonArray = jsonObject.optJSONArray("text");
        if (textJsonArray != null && textJsonArray.length() > 0) {
            waterMarkTexts = new ArrayList<>();
            for (int i = 0; i < textJsonArray.length(); i++) {
                JSONObject jsonObject1 = textJsonArray.optJSONObject(i);
                if (jsonObject1 == null)
                    continue;
                WaterMarkText waterMarkText = new WaterMarkText(jsonObject1);
                waterMarkTexts.add(waterMarkText);
            }
        }

        JSONArray sepLineJsonArray = jsonObject.optJSONArray("sep_line");
        if (sepLineJsonArray != null && sepLineJsonArray.length() > 0) {
            for (int i = 0; i < sepLineJsonArray.length(); i++) {
                waterMarkSepLines = new ArrayList<>();
                JSONObject jsonObject1 = sepLineJsonArray.optJSONObject(i);
                if (jsonObject1 == null)
                    continue;
                WaterMarkSepLine waterMarkSepLine = new WaterMarkSepLine(jsonObject1);
                waterMarkSepLines.add(waterMarkSepLine);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<WaterMarkImage> getWaterMarkImages() {
        return waterMarkImages;
    }

    public void setWaterMarkImages(List<WaterMarkImage> waterMarkImages) {
        this.waterMarkImages = waterMarkImages;
    }

    public List<WaterMarkText> getWaterMarkTexts() {
        return waterMarkTexts;
    }

    public void setWaterMarkTexts(List<WaterMarkText> waterMarkTexts) {
        this.waterMarkTexts = waterMarkTexts;
    }

    public List<WaterMarkSepLine> getWaterMarkSepLines() {
        return waterMarkSepLines;
    }

    public void setWaterMarkSepLines(List<WaterMarkSepLine> waterMarkSepLines) {
        this.waterMarkSepLines = waterMarkSepLines;
    }
}
