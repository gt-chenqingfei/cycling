package com.beastbikes.android.modules.user.dto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icedan on 16/4/18.
 */
public class HistogramDTO implements Serializable {

    private int monthRank;
    private double max;
    private List<ItemDTO> items = new ArrayList<>();

    public HistogramDTO(JSONObject json) {
        this.monthRank = json.optInt("monthRank");
        this.max = json.optDouble("max");
        JSONArray array = json.optJSONArray("result");
        for (int i = 0; i < array.length(); i++) {
            JSONObject result = array.optJSONObject(i);
            if (null == result) {
                continue;
            }
            this.max = Math.max(max, result.optDouble("value"));
            ItemDTO item = new ItemDTO(array.optJSONObject(i));
            if (i == array.length() - 1) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
            this.items.add(item);
        }
    }

    public int getMonthRank() {
        return monthRank;
    }

    public void setMonthRank(int monthRank) {
        this.monthRank = monthRank;
    }

    public double getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

    public class ItemDTO {
        private long data;
        private int color;
        private String description;
        private double value;
        private boolean selected;

        public ItemDTO(JSONObject json) {
            this.data = json.optLong("date");
            this.color = json.optInt("color");
            this.description = json.optString("description");
            this.value = json.optDouble("value");
        }

        public long getData() {
            return data;
        }

        public void setData(long data) {
            this.data = data;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

}
