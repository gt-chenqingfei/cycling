package com.beastbikes.android.modules.cycling.task.dto;

/**
 * Created by chenqingfei on 16/4/14.
 */
public class Bazinga {
    private String linkTo;
    private String imageUrl;
    private boolean isShow = false;
    private int counter =0;

    public Bazinga(String linkTo,String imageUrl,boolean isShow,int counter){
        this.linkTo = linkTo;
        this.imageUrl = imageUrl;
        this.isShow = isShow;
        this.counter = counter;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
