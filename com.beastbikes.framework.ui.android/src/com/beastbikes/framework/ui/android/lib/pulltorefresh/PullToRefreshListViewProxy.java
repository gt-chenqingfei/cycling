package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.util.ArrayList;

import android.view.View;
import android.widget.ListView;

import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;
import com.beastbikes.framework.ui.android.lib.list.PageData;

public class PullToRefreshListViewProxy<K, D> extends
        PullToRefreshAbsListViewProxy<K, D, ListView> {

    private static final int scrollBy = 150;
    private View footerView = null, headerView = null;
    private final Runnable showMoreDataRunnable = new Runnable() {

        @Override
        public void run() {
            scrollToShowMoreData();
        }
    };

    public PullToRefreshListViewProxy(BaseListAdapter<D> adapter,
                                      PullToRefreshAdapterViewBase<ListView> pullView, String cacheKey,
                                      PullRefeshListener<K> pullListener, Pageable<K> pageable) {
        super(adapter, pullView, cacheKey, pullListener, pageable);

    }

    public PullToRefreshListViewProxy(BaseListAdapter<D> adapter,
                                      PullToRefreshAdapterViewBase<ListView> pullView, String cacheKey,
                                      PullRefeshListener<K> pullListener, View headerView,
                                      View footerView, Pageable<K> pageable) {
        super(adapter, pullView, cacheKey, pullListener, pageable);

        this.headerView = headerView;
        this.footerView = footerView;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.internalView.setHeaderDividersEnabled(false);
        if (headerView != null) {
            this.internalView.addHeaderView(headerView);
        }
        if (footerView != null) {
            this.internalView.addFooterView(footerView);
        }
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void onLoadSucessfully(PageData<K, D> dataList,
                                     ArrayList<D> data) {
        Event event = this.currentEvent;
        super.onLoadSucessfully(dataList, data);

        if (data == null || data.size() < 1) {
            return;
        }

        switch (event) {
            case more:
                handler.postDelayed(showMoreDataRunnable,
                        PullToRefreshBase.ANIMATION_DURATION_MS + 100);
                break;
            default:
                break;
        }
    }

    private void scrollToShowMoreData() {

        final ListView actualListView = this.getInternalView();
        int curPos = actualListView.getLastVisiblePosition();
        int total = adapter.getCount();
        if (total > curPos) {
            final int index = actualListView.getFirstVisiblePosition();
            if (index >= 0) {
                View v = actualListView.getChildAt(0);
                if (v != null) {

                    int bottom = v.getBottom();
                    int top = v.getTop();
                    if (bottom > scrollBy) {
                        Smoothable scrollToShow = new Smoothable(handler, 150,
                                top, top - scrollBy) {

                            @Override
                            public void doSmooth(int currentDiff) {
                                actualListView.setSelectionFromTop(index,
                                        currentDiff);
                            }
                        };
                        handler.post(scrollToShow);
                    } else {
                        Smoothable scrollToShow = new Smoothable(handler, 150,
                                bottom, bottom - scrollBy) {

                            @Override
                            public void doSmooth(int currentDiff) {
                                actualListView.setSelectionFromTop(index + 1,
                                        currentDiff);
                            }
                        };
                        handler.post(scrollToShow);
                        // actualListView.setSelectionFromTop(index + 1, bottom
                        // - scrollBy);
                    }
                } else {
                    Smoothable scrollToShow = new Smoothable(handler, 150, 0,
                            30) {

                        @Override
                        public void doSmooth(int currentDiff) {
                            actualListView.scrollBy(0, currentDiff);
                        }
                    };
                    handler.post(scrollToShow);

                }
            }
        }
    }
}
