package com.beastbikes.android.modules.cycling.activity.ui.record.model;

/**
 * 心率区间
 * Created by secret on 16/10/18.
 */

public class HeartRateModel {

    private long time;

    private int label;

    private int percent;

    public HeartRateModel(long time, int label, int percent) {
        this.time = time;
        this.label = label;
        this.percent = percent;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
