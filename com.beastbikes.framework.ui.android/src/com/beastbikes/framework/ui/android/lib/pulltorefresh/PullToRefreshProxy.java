package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;

import com.beastbikes.framework.ui.android.BuildConfig;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;

public class PullToRefreshProxy<K, V extends View> implements OnRefreshListener {

    protected PullRefeshListener<K> pullListener;
    private static final long MIN_INTERVAL = 5 * 60 * 1000;

    protected PullToRefreshBase<V> pullView;
    protected V internalView;
    protected Event currentEvent = Event.none;
    protected String cacheKey;

    protected boolean pullUpDisabled = false;
    protected boolean pullDownDisabled = false;
    protected long minInterval = MIN_INTERVAL;
    protected Handler handler;

    public PullToRefreshProxy(PullToRefreshBase<V> pullView, String cacheKey,
                              PullRefeshListener<K> pullListener) {

        this.handler = new Handler();
        this.cacheKey = cacheKey;
        this.pullView = pullView;
        this.pullListener = pullListener;
        this.pullView.setOnRefreshListener(this);
        internalView = this.pullView.getRefreshableView();

        restoreUpdateTime();

    }

    /**
     * this maybe invoked multi-time, when you want to reuse an internal view,
     * you can invoke this to reset it
     */
    public void onDestroy() {
        this.onRefreshFinished();
    }

    public void setOnCreateContextMenuListener(
            OnCreateContextMenuListener listener) {
        this.internalView.setOnCreateContextMenuListener(listener);
    }

    public void setBackGround(int resId) {
        pullView.setBackgroundResource(resId);
        internalView.setBackgroundResource(resId);
    }

    public void setBackGroudColor(int color) {
        pullView.setBackgroundColor(color);
        internalView.setBackgroundColor(color);
    }

    public void showContextMenu() {
        this.internalView.showContextMenu();
    }

    /**
     * which will invoked only once time through its life cycle.
     */
    public void onCreate() {

//		long lastRefreshTime = StaticWrapper.cacheMgr.getCacheTime(cacheKey);
        long lastRefreshTime = 0l;
        long interval = System.currentTimeMillis() - lastRefreshTime;

        if(BuildConfig.DEBUG)Log.d("cacheinterval", cacheKey + " " + interval + " " + minInterval);

        if (minInterval == 0) {
            this.currentEvent = Event.none;
            pullView.setRefreshing(true);
        } else if (minInterval > 0 && interval > minInterval) {

            if(BuildConfig.DEBUG)Log.d("cacheinterval", cacheKey + " refreshed");

            if (this.pullListener.shouldRefreshingHeaderOnStart()) {
                this.currentEvent = Event.none;
                pullView.setRefreshing(true);
            } else {
                if (pullListener != null) {
                    pullListener.loadNormal();
                }
            }
        }
    }

    public void setMinInterval(long minInterval) {
        this.minInterval = minInterval;
    }

    public PullToRefreshBase<V> getPullView() {
        return pullView;
    }

    public V getInternalView() {
        return internalView;
    }

    public Event getCurrentEvent() {
        return this.currentEvent;
    }

    // ===========================================

    @Override
    public void onPullDownRefresh() {

        if (this.isRefreshing() == false) {
            this.currentEvent = Event.normal;
            pullListener.loadNormal();
        }
    }

    public void pullDownToRefresh() {
        pullDownToRefresh(true);
    }

    public void pullDownToRefresh(boolean showRefreshingHeader) {
        if (!this.isRefreshing()) {
            pullView.setRefreshing(showRefreshingHeader);
        }
    }

    @Override
    public void onPullUpRefresh() {
        if (this.isRefreshing() == false) {
            this.currentEvent = Event.more;
            pullListener.loadMore(null);
        }
    }

    public void disablePull() {
        this.pullDownDisabled = true;
        this.pullUpDisabled = true;
        this.pullView.disablePull();
    }

    public void enablePull() {
        this.pullDownDisabled = false;
        this.pullUpDisabled = false;
        this.pullView.enablePull();
    }

    public boolean isPullUpEnabled() {
        return (this.pullUpDisabled == false);
    }

    public void enablePullUp() {
        enablePull();
        disablePullDown();
    }

    public boolean isPullDownEnabled() {
        return (this.pullDownDisabled == false);
    }

    public void enablePullDown() {
        enablePull();
        disablePullUp();
    }

    public void disablePullUp() {
        this.pullUpDisabled = true;
        this.pullView.disablePullUp();
    }

    public void disablePullDown() {
        this.pullDownDisabled = true;
        this.pullView.disablePullDown();
    }

    public void showPullUp() {
        if (!pullUpDisabled) {
            this.pullView.enablePullUp();
        }
    }

    public void hidePullUp() {
        this.pullView.disablePullUp();
    }

    public void setPullHeader(PullableView pullHeader) {
        if (pullHeader == null)
            return;
        this.pullView.setPullHeaderView(pullHeader);
    }

    public void setRefreshing(boolean showPullHeader) {
        this.pullView.setRefreshing(showPullHeader);
    }

    // ================================

    public void onRefreshFinished() {
        this.pullView.onRefreshComplete();
        this.currentEvent = Event.none;
    }

    protected boolean isRefreshing() {
        return (this.currentEvent != Event.none);
    }

    private void restoreUpdateTime() {
//		long updateTime = StaticWrapper.cacheMgr.getCacheTime(this.cacheKey);
        this.pullView.setUpdateTime(0l);
    }

}
