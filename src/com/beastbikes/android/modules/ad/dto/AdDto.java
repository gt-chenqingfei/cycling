package com.beastbikes.android.modules.ad.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONObject;

/**
 * Created by chenqingfei on 16/7/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdDto {
    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("linkTo")
    private String linkTo;

    public String getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
