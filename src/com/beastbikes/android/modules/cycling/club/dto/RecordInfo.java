package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class RecordInfo implements Serializable {

    private String sportIdentify;
    private String title;
    private String cyclingImage;


    private Date startDate;
    private Date endDate;
    private double distance;
    private long time;

    public RecordInfo(JSONObject object) {
        if(object == null)
            return;

        this.sportIdentify = object.optString("sportIdentify");
        this.title = object.optString("title");
        this.cyclingImage = object.optString("cyclingImage");
        this.startDate = DateFormatUtil.stringFormat2Date(object.optString("startDate"));
        this.endDate = DateFormatUtil.stringFormat2Date(object.optString("endDate"));
        this.distance = object.optDouble("distance");
        this.time = object.optLong("time")*1000;
    }

    public RecordInfo(ActivityDTO activity) {
        if (null == activity) {
            return;
        }

        this.sportIdentify = activity.getActivityIdentifier();
        this.title = activity.getTitle();
        this.cyclingImage = activity.getActivityUrl();
        this.startDate = DateFormatUtil.stringFormat2Date( DateFormatUtil.dateFormat2String(activity.getStartTime()));
        this.endDate = DateFormatUtil.stringFormat2Date( DateFormatUtil.dateFormat2String(activity.getStopTime()));
        this.distance = activity.getTotalDistance();
        this.time = (int) activity.getElapsedTime();
    }

    public String getSportIdentify() {
        return sportIdentify;
    }

    public void setSportIdentify(String sportIdentify) {
        this.sportIdentify = sportIdentify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCyclingImage() {
        return cyclingImage;
    }

    public void setCyclingImage(String cyclingImage) {
        this.cyclingImage = cyclingImage;
    }



    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
