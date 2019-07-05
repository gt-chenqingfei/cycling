package com.beastbikes.android.modules.user.filter.models;

import com.beastbikes.android.modules.user.filter.utils.FilterTools.FilterType;

public class FilterItem {

    private String filterName;
    private FilterType type;

    public FilterItem(String filterName, FilterType type) {
        this.filterName = filterName;
        this.type = type;
    }

    public FilterItem() {
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

}
