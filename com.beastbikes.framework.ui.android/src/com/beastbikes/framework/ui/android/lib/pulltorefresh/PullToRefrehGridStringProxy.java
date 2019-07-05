package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.widget.GridView;

import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;

public class PullToRefrehGridStringProxy<D> extends
        PullToRefreshAbsListViewProxy<String, D, GridView> {

    public PullToRefrehGridStringProxy(BaseListAdapter<D> adapter,
                                       PullToRefreshAdapterViewBase<GridView> pullView, String cacheKey,
                                       PullRefeshListener<String> pullListener, Pageable<String> pageable) {
        super(adapter, pullView, cacheKey, pullListener, pageable);
    }

}
