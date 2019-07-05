package com.beastbikes.android.update.dto;

import com.beastbikes.android.R;
import com.beastbikes.android.utils.DateFormatUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by chenqingfei on 16/7/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionInfo {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_FORCE = 1;

    @JsonProperty("type")
    private int type;

    @JsonProperty("versionCode")
    private int versionCode;

    @JsonProperty("versionName")
    private String versionName ;

    @JsonProperty("downloadLink")
    private String downloadLink;

    @JsonProperty("releaseDate")
    private String releaseDate;

    public String getReleaseDate() {

        Date date = DateFormatUtil.stringFormat2Date(releaseDate);
        return DateFormatUtil.dateFormat2StringYearMonthDay(date);
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @JsonProperty("changeLog")
    private String changeLog;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDownloadLink() {

        return downloadLink.replace("https://","http://");
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }
}
