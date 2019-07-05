package com.beastbikes.android.modules.cycling.club.dto;

import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubFeedImageTxtRecord extends ClubFeedBase {
    private String text;
    private List<ImageInfo> imageList;
    private RecordInfo recordInfo;
    private int postId;
    private String userId;


    public ClubFeedImageTxtRecord() {

    }

    public ClubFeedImageTxtRecord(JSONObject object) {

        if (object == null)
            return;
        this.postId = object.optInt("postId");
        this.clubId = object.optString("clubId");
        this.userId = object.optString("userId");

        JSONObject content = object.optJSONObject("content");
        if (content != null) {
            this.text = content.optString("text");
            JSONArray imageArray = content.optJSONArray("imageList");
            if (imageArray != null) {
                imageList = new ArrayList<ImageInfo>();
                for (int i = 0; i < imageArray.length(); i++) {
                    JSONObject image = imageArray.optJSONObject(i);
                    if (image != null) {
                        ImageInfo imageInfo = new ImageInfo(image);
                        imageList.add(imageInfo);
                    }
                }
            }

            JSONObject record = content.optJSONObject("recordInfo");
            if (!JSONUtil.isNull(record)) {
                recordInfo = new RecordInfo(record);
            }
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ImageInfo> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageInfo> imageList) {
        this.imageList = imageList;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

}
