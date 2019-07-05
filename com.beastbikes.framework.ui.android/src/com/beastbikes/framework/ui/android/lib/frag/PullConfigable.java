package com.beastbikes.framework.ui.android.lib.frag;

import android.view.View;

import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshBase;

public interface PullConfigable<V extends View> {

    public void configView(PullToRefreshBase<V> pullView, V internalView);

    public void configRefresh();

}
