package com.beastbikes.android.ble.dto;

import java.io.Serializable;

/**
 * Created by icedan on 16/10/10.
 */

public class CadenceDTO implements Serializable {

    private String title;
    private String data;
    private String desc;
    private boolean selected;

    public CadenceDTO() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
