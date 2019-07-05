package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubFeedNotice extends ClubFeedBase{

    public ClubFeedNotice (){};
    public ClubFeedNotice(JSONObject object)
    {
        if(object != null) {
            this.clubId = object.optString("clubId");
            this.setText(object.optString("content"));
            this.setDate(new Date(object.optInt("timestamp")));
        }
    }
}
