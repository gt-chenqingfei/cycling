package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.view.View;

public interface PullableView {

    /**
     * set view to default state
     */
    void reset();

    /**
     * show release to refresh information
     *
     * @param footerHeight
     * @param newHeight
     */
    void releaseToRefresh();

    /**
     * update your view when pull progress
     *
     * @param curValue
     * @param maxValue
     */
    void updateRefresh(int curValue, int maxValue);

    /**
     * set pull label
     *
     * @param pullLabel
     */
    void setPullLabel(String pullLabel);

    /**
     * show refresh state state
     */
    void refreshing();

    /**
     * @param refreshingLabel
     */
    void setRefreshingLabel(String refreshingLabel);

    void setReleaseLabel(String releaseLabel);

    void pullToRefresh();

    void setUpdateTime(long time);

    long getUpdateTime();

    void updateTimeLabel();

    View getView();

    void setTextColor(int color);
}
