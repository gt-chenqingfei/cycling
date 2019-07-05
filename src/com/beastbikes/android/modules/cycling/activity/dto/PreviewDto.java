package com.beastbikes.android.modules.cycling.activity.dto;

import android.content.Context;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by icedan on 16/1/8.
 */
public class PreviewDto implements Serializable, Constants {

    public static final int PREVIEW_SVG_SPEED_KEY = 0;
    public static final int PREVIEW_MAX_SPEED_KEY = 1;
    public static final int PREVIEW_ALTITUDE_KEY = 2;
    public static final int PREVIEW_UPHILL_DISTANCE_KEY = 3;
    public static final int PREVIEW_TIME_KEY = 4;
    public static final int PREVIEW_CALORIES_KEY = 5;

    private int key1;
    private int key2;
    private String label1;
    private String value1;
    private String label2;
    private String value2;
    private boolean edit;
    private JSONObject json;

    private LocalActivity activity;
    private boolean isChineseTimeZone = false;

    public PreviewDto(String label1, String value1) {
        this.label1 = label1;
        this.value1 = value1;
    }

    public PreviewDto(String label1, String value1, String label2, String value2) {
        this.label1 = label1;
        this.value1 = value1;
        this.label2 = label2;
        this.value2 = value2;
    }

    private String[] source;

    public PreviewDto(Context context, JSONObject json) {
        if (null == context)
            return;

        this.json = json;

        if (null == source || source.length <= 0) {
            source = context.getResources().getStringArray(R.array.cycling_setting_array);
        }

        if (json.has("0")) {
            int label1Index = json.optInt("0");
            this.label1 = source[label1Index];
            this.value1 = getValue(label1Index);
        }

        if (json.has("1")) {
            int label2Index = json.optInt("1");
            this.label2 = source[label2Index];
            this.value2 = getValue(label2Index);
        }

    }

    public PreviewDto(Context context, JSONObject json, LocalActivity activity,boolean isChineseTimeZone) {
        if (null == context)
            return;

        this.isChineseTimeZone = isChineseTimeZone;
        this.activity = activity;

        if (null == source || source.length <= 0) {
            source = context.getResources().getStringArray(R.array.cycling_setting_array);
        }

        if (json.has("0")) {
            int label1Index = json.optInt("0");
            this.label1 = source[label1Index];
            this.value1 = getValue(label1Index);
        }

        if (json.has("1")) {
            int label2Index = json.optInt("1");
            this.label2 = source[label2Index];
            this.value2 = getValue(label2Index);
        }

    }

    public String getValue(int index) {
        switch (index) {
            case CYCLING_DATA_SVG_SPEED:// 平均速度
                if (null == activity) {
                    return "0.0";
                }

                final double speed;
                if (activity.getTotalDistance() > 0 && activity.getTotalElapsedTime() > 0) {
                    speed = activity.getTotalDistance() / activity.getTotalElapsedTime() * 3.6;
                } else {
                    speed = 0;
                }

                if (isChineseTimeZone) {
                    return String.format("%.1f", speed);
                } else {
                    return String.format("%.1f", LocaleManager.kphToMph(speed));
                }
            case CYCLING_DATA_MAX_SPEED:// 极速
                if (null == activity) {
                    return "0.0";
                }

                if (isChineseTimeZone) {
                    return String.format("%.1f", activity.getMaxVelocity());
                } else {
                    return String.format("%.1f", LocaleManager.kphToMph(activity.getMaxVelocity()));
                }
            case CYCLING_DATA_ALTITUDE:// 海拔
                if (null == activity) {
                    return "0";
                }

                if (isChineseTimeZone) {
                    return String.format("%.0f", activity.getMaxAltitude());
                } else {
                    return String.format("%.0f", LocaleManager.metreToFeet(activity.getMaxAltitude()));
                }
            case CYCLING_DATA_UPHILL_DISTANCE:// 爬升
                if (null == activity) {
                    return "0.0";
                }

                if (isChineseTimeZone) {
                    return String.format("%.1f", activity.getTotalUphillDistance());
                } else {
                    return String.format("%.1f", LocaleManager.metreToFeet(activity.getTotalUphillDistance()));
                }
            case CYCLING_DATA_TIME:// 时间
                if (null == activity) {
                    return "00:00:00";
                }

                final double t = activity.getTotalElapsedTime();

                final int h = (int) ((0 == t) ? 0 : (t / 3600));
                final int m = (int) ((0 == t) ? 0 : ((t % 3600) / 60L));
                final int s = (int) ((0 == t) ? 0 : (t % 60));
                return String.format(
                        "%02d:%02d:%02d", h, m, s);
            case CYCLING_DATA_CALORIE:// 卡路里
                if (null == activity) {
                    return "0.0";
                }
                return String.format("%.0f", activity.getTotalCalorie());
        }

        return "";
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public int getKey1() {
        return key1;
    }

    public void setKey1(int key1) {
        this.key1 = key1;
    }

    public int getKey2() {
        return key2;
    }

    public void setKey2(int key2) {
        this.key2 = key2;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public LocalActivity getActivity() {
        return activity;
    }

    public void setActivity(LocalActivity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "PreviewDto{" +
                "key1=" + key1 +
                ", key2=" + key2 +
                ", label1='" + label1 + '\'' +
                ", value1='" + value1 + '\'' +
                ", label2='" + label2 + '\'' +
                ", value2='" + value2 + '\'' +
                ", activity=" + activity +
                ", source=" + Arrays.toString(source) +
                '}';
    }
}
