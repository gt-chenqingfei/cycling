package com.beastbikes.android.modules.cycling.club.dto;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClubFeedPost implements Serializable {


    public static final int TYPE_FEED = 0;

    public static final int TYPE_ABLUM = 1;

    private int needSync;

    private String clubId;

    private String content;

    private String sportIdentify;

    private int postError; // 0 表示成功 1表示 失败

    private int type; //0 表示是feed动态，1表示相册照片

    private List<ImageInfo> postImageList;


    public ClubFeedPost( int needSync,  String clubId, String content,
                         String sportIdentify, int type) {
        this.needSync = needSync;
        this.clubId = clubId;
        this.content = content;
        this.sportIdentify = sportIdentify;
        this.type = type;

    }

    public int isNeedSync() {
        return needSync;
    }

    public void setNeedSync(int needSync) {
        this.needSync = needSync;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSportIdentify() {
        return sportIdentify;
    }

    public void setSportIdentify(String sportIdentify) {
        this.sportIdentify = sportIdentify;
    }

    public int getPostError() {
        return postError;
    }

    public void setPostError(int postError) {
        this.postError = postError;
    }

    public List<ImageInfo> getCompletedList() {
        return postImageList;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void addInComplete(ImageInfo info) {
        if (this.postImageList == null) {
            this.postImageList = new ArrayList<ImageInfo>();
        }
        this.postImageList.add(info);
    }

    public void setPostImageList(List<ImageInfo> infos) {
        this.postImageList = infos;
    }

    public String getCompleteListJsonStr() {
        JSONArray array = new JSONArray();
        for (int i = 0; postImageList != null && i < postImageList.size(); i++) {
            ImageInfo info = postImageList.get(i);
            try {
                array.put(i, info.obj2Json());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  array.toString();
    }


}
