package com.beastbikes.android.widget.sharepopupwindow.dto;

/**
 * Created by caoxiao on 16/5/18.
 */
public class CommonShareImageDTO extends CommonShareBaseDTO {

    private String imagePath;

    public CommonShareImageDTO() {
    }

    public CommonShareImageDTO(String imagePath, String title) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
