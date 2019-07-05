package com.beastbikes.android.modules.cycling.route.dto;

import org.json.JSONObject;

public class CityDTO {

    private String objectId;

    private String cityId;

    private String name;

    private String englishName;

    private String twName;

    private boolean isList;

    private int sort;

    public CityDTO() {
    }

    public CityDTO(JSONObject json) {
        this.cityId = json.optString("cityId");
        this.name = json.optString("zh_CN");
        this.sort = json.optInt("sort");
        this.englishName = json.optString("en");
        this.twName = json.optString("zh_TW");
        this.objectId = json.optString("objectId");
        this.isList = json.optBoolean("isList");
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getTwName() {
        return twName;
    }

    public void setTwName(String twName) {
        this.twName = twName;
    }

    public boolean isList() {
        return isList;
    }

    public void setIsList(boolean isList) {
        this.isList = isList;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCityId() {
        return this.cityId;
    }

    public void setCityId(String id) {
        this.cityId = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        if (null != this.cityId)
            return this.cityId.hashCode();

        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CityDTO))
            return false;

        if (null != this.cityId) {
            return this.cityId.equals(((CityDTO) o).cityId);
        }

        return false;
    }

}
