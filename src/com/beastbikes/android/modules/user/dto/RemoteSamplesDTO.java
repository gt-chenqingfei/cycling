package com.beastbikes.android.modules.user.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RemoteSamplesDTO implements Serializable {

    private static final long serialVersionUID = -8710840339271193514L;

    private String activityId;
    private JSONArray dataArray;
    private int sequence;
    private String userId;
    private List<SampleDTO> datas;

    public RemoteSamplesDTO() {

    }

    public RemoteSamplesDTO(JSONObject json) {
        this.activityId = json.optString("activityId");
        this.dataArray = json.optJSONArray("data");
        this.sequence = json.optInt("sequence");
        this.userId = json.optString("userId");

        this.datas = new ArrayList<SampleDTO>();
        for (int i = 0; i < this.dataArray.length(); i++) {
            try {
                String value = this.dataArray.getString(i);
                if (value.isEmpty())
                    return;
                JSONObject obj = new JSONObject(value);
                SampleDTO sd = new SampleDTO(obj);
                if (sd.getLatitude1() != 0 && sd.getLatitude1() != 4.9E-324)
                    this.datas.add(sd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public JSONArray getDataArray() {
        return dataArray;
    }

    public void setDataArray(JSONArray dataArray) {
        this.dataArray = dataArray;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<SampleDTO> getDatas() {
        return datas;
    }

    public void setDatas(List<SampleDTO> datas) {
        this.datas = datas;
    }

}
