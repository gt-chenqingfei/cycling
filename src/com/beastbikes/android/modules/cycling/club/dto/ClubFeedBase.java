package com.beastbikes.android.modules.cycling.club.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class ClubFeedBase implements Serializable{

    private ClubUser user;
    private int fid;
    private Date date;
    private String text;
    private boolean hasLiked = false;
    private int likeCount;
    private List<ClubUser> likeList;
    private int commentCount;
    private List<ClubFeedComment> commentList;
    private int feedType;
    protected String clubId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status ;
    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getFeedType() {
        return feedType;
    }

    public void setFeedType(int feedType) {
        this.feedType = feedType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ClubUser getUser() {
        return user;
    }

    public void setUser(ClubUser user) {
        this.user = user;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<ClubUser> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<ClubUser> likeList) {
        this.likeList = likeList;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<ClubFeedComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<ClubFeedComment> commentList) {
        this.commentList = commentList;
    }

    public void addComment(ClubFeedComment comment)
    {
        if(this.commentList == null)
        {
            this.commentList = new ArrayList<>();
        }
        this.commentList.add(comment);
        commentCount ++;
    }
    public boolean isHasLiked() {
        return hasLiked;
    }

    public void addHasLiked(ClubUser user)
    {
        if(this.likeList == null){
            likeList = new ArrayList<>();
        }
        if(!likeList.contains(user)) {
            if(likeList.size() >=6){
                likeList.remove(0);
            }
            this.likeList.add(0,user);

            this.likeCount ++;
        }
    }

    public void removeLiked(String userid){
        for(int i=0; likeList!= null && i<likeList.size(); i++){
            ClubUser user = likeList.get(i);
            if(user.getUserId().equals(userid)){
                likeList.remove(i);
                this.likeCount --;
                break;
            }
        }
    }

    public void removeComment(int cid)
    {
        for(int i=0; commentList != null && i< commentList.size(); i++){
            ClubFeedComment c = commentList.get(i);
            if(c.getCid() == cid)
            {
                commentList.remove(i);
                commentCount =commentList.size();
                break;
            }
        }
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }
}
