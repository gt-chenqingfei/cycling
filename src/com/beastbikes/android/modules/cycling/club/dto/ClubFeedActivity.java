package com.beastbikes.android.modules.cycling.club.dto;

import android.content.Context;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.utils.DateFormatUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubFeedActivity extends ClubFeedBase {


    private String actId;
    private String title;
    private String mobPlace;
    private String startDate;
    private Date endDate;
    private String routeImage;
    private int status;
    private String routeName;

    private String actUrl;

    public ClubFeedActivity() {
    }

    public ClubFeedActivity(JSONObject object) {
        if (object != null) {
            Context context = BeastBikes.getInstance().getApplicationContext();
            this.clubId = object.optString("clubId");
            this.actId = object.optString("actId");
            this.title = object.optString("title");
            this.mobPlace = object.optString("mobPlace");
            Date startDateTmp = DateFormatUtil.stringFormat2Date(object.optString("startDate"));
            startDate = context.getResources().getString(R.string.time) + DateFormatUtil.getTime4Feed(startDateTmp);//提前转
            this.endDate = DateFormatUtil.stringFormat2Date(object.optString("endDate"));
            this.routeImage = object.optString("routeImage");
            this.status = object.optInt("status");
            this.actUrl = object.optString("actUrl");
            this.routeName = object.optString("routeName");
            this.routeName = routeName + context.getResources().getString(R.string.route);
        }
    }


    public String getActId() {
        return actId;
    }

    public void setActId(String id) {
        this.actId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobPlace() {
        return mobPlace;
    }

    public void setMobPlace(String mobPlace) {
        this.mobPlace = mobPlace;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(String routeImage) {
        this.routeImage = routeImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getActUrl() {
        return actUrl;
    }

    public void setActUrl(String actUrl) {
        this.actUrl = actUrl;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
