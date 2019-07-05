package com.beastbikes.android.widget.sharepopupwindow.dto;

/**
 * Created by caoxiao on 16/5/18.
 */
public class CommonShareLinkDTO extends CommonShareBaseDTO {

    private String iconUrl;
    private String title;
    private String targetUrl;
    private String desc;
    private String weiboText;
    private String wechatText;

    public CommonShareLinkDTO() {
    }

    public CommonShareLinkDTO(String iconUrl, String title, String targetUrl, String desc, String weiboText, String wechatText) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.targetUrl = targetUrl;
        this.desc = desc;
        this.weiboText = weiboText;
        this.wechatText = wechatText;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getWeiboText() {
        return weiboText;
    }

    public void setWeiboText(String weiboText) {
        this.weiboText = weiboText;
    }

    public String getWechatText() {
        return wechatText;
    }

    public void setWechatText(String wechatText) {
        this.wechatText = wechatText;
    }
}
