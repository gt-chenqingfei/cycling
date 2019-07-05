package com.beastbikes.framework.ui.android.lib.pulltorefresh;

public interface PullRefeshListener<K> {

    void loadNormal();

    void loadMore(K maxId);

    void loadRefreshMore(K maxId, long lastModify);

    boolean shouldRefreshingHeaderOnStart();

}
