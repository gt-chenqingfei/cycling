package com.beastbikes.framework.android.update;

import java.io.Serializable;

public class ReleasedPackage implements Serializable {

    private static final long serialVersionUID = 7505470345810869637L;

    private String name;

    private String platform;

    private String versionName;

    private int versionCode;

    private String releaseNote;

    private String installUrl;

    private long packageSize;

    public ReleasedPackage() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getReleaseNote() {
        return this.releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public String getInstallUrl() {
        return this.installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public long getPackageSize() {
        return this.packageSize;
    }

    public void setPackageSize(long packageSize) {
        this.packageSize = packageSize;
    }

}
