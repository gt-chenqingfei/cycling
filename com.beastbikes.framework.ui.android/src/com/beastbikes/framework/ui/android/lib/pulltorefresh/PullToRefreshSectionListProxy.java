package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView.OnChildClickListener;

import com.beastbikes.framework.ui.android.lib.list.BaseSectionListAdapter;
import com.beastbikes.framework.ui.android.lib.list.OnAdapterChangeListener;
import com.beastbikes.framework.ui.android.lib.list.PageData;
import com.beastbikes.framework.ui.android.lib.list.PageRefreshData;

public class PullToRefreshSectionListProxy<K, G extends Groupable<C>, C>
        extends PullToRefreshProxy<K, ExpandableSectionList> {

    private static final String TAG = "absproxy";
    private static final int scrollBy = 150;

    protected Pageable<K> pageable;

    protected BaseSectionListAdapter<G, C> adapter;
    protected boolean shouldCache = false;

    private K maxId;
    protected long lastModify = -1;
    private boolean isLastPage = true;
    private ArrayList<G> newCacheData = null;
    private K newMaxId = null;
    private final Runnable showMoreDataRunnable = new Runnable() {

        @Override
        public void run() {
            scrollToShowMoreData();
        }
    };

    public PullToRefreshSectionListProxy(BaseSectionListAdapter<G, C> adapter,
                                         final PullToRefreshBase<ExpandableSectionList> pullView,
                                         String cacheKey, PullRefeshListener<K> pullListener,
                                         Pageable<K> pageable) {
        super(pullView, cacheKey, pullListener);

        this.adapter = adapter;
        this.adapter.setDataChangeListener(new OnAdapterChangeListener() {

            @Override
            public void onDataChanged(int count) {
                if (count < 1) {
                    pullView.showEmptyView();
                } else {
                    pullView.hideEmptyView();
                }
            }
        });
//		shouldCache = StaticWrapper.cacheMgr.shouldSupportCache(this.cacheKey);
        this.pageable = pageable;
        this.maxId = this.pageable.defValue();
        this.newMaxId = this.pageable.defValue();
        this.hidePullUp();
    }

    public void setOnChildClickListener(OnChildClickListener childClickListener) {
        internalView.setOnChildClickListener(childClickListener);
    }

    @Override
    public void onCreate() {

//		if (shouldCache) {
//			Object cacheData = StaticWrapper.cacheMgr
//					.getCacheData(this.cacheKey);
//			if (cacheData instanceof ArrayList<?>) {
//				ArrayList<G> data = (ArrayList<G>) cacheData;
//				adapter.add(data);
//			}
//			lastModify = StaticWrapper.cacheMgr.getCacheServerTime(cacheKey);
//			maxId = pageable.initMaxId(cacheKey);
//			newMaxId = maxId;
//			MLog.d(TAG, "create max id is " + newMaxId);
//			isLastPage = pageable.initIsLastPage(maxId);
//			if (isLastPage) {
//				this.hidePullUp();
//			} else {
//				this.showPullUp();
//			}
//		}

        super.onCreate();
    }

    /**
     * re-set internal view's adapter, header, footer and so on.
     */
    public void onStart() {
        if (internalView != null)
            internalView.setAdapter(adapter);
    }

    public void onStop() {
        if (internalView != null) {
            internalView.setAdapter((ExpandableListAdapter) null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shouldCache && newCacheData != null && newCacheData.size() > 0) {
            pageable.cacheData(this.cacheKey, (Serializable) newCacheData,
                    lastModify, newMaxId);
            Log.d(TAG, "destroy max id is " + newMaxId);
        }
    }

    @Override
    public void onPullUpRefresh() {
        if (this.isRefreshing() == false) {
            this.currentEvent = Event.more;
            if (lastModify >= 0) {
                pullListener.loadRefreshMore(maxId, lastModify);
            } else {
                pullListener.loadMore(maxId);
            }

        }
    }

    public void refreshList() {
        this.adapter.notifyDataSetChanged();
    }

    public void onLoadFailed(String failture) {
        this.onRefreshFinished();
    }

    public void onLoadSucessfully(ArrayList<G> data) {

        this.onRefreshFinished();

        if (data != null) {
            this.adapter.add(data);
            this.adapter.notifyDataSetChanged();

            if (shouldCache && data.size() > 0) {
                newCacheData = data;
            }
        }

    }

    public void onLoadSucessfully(PageRefreshData<K, G> dataList) {
        if (dataList != null) {
            this.lastModify = dataList.lastModify;
            if (dataList.isModified) {
                this.adapter.clear();
            }
            this.onLoadSucessfully(dataList, dataList.data);
        } else {
            this.onLoadSucessfully(dataList, null);
        }

    }

    public void onLoadSucessfully(PageData<K, G> dataList) {
        if (dataList != null) {
            this.onLoadSucessfully(dataList, dataList.data);
        } else {
            this.onLoadSucessfully(dataList, null);
        }
    }

    protected void onLoadSucessfully(PageData<K, G> dataList,
                                     ArrayList<G> data) {

        if (dataList == null) {
            isLastPage = true;
        } else {

            if (data == null || data.size() == 0) {
            }

            K curMaxId = pageable.chooseMaxId(this.maxId, dataList.maxId);
            Event currentEvent = this.getCurrentEvent();

            switch (currentEvent) {
                case normal:
                    this.maxId = curMaxId;

                    this.adapter.clear(); // clear cached datas
                    this.adapter.add(data);
                    isLastPage = dataList.page_is_last;

                    newCacheData = data;
                    newMaxId = curMaxId;

                    break;
                case more:
                    this.maxId = curMaxId;
                    this.adapter.add(data);
                    isLastPage = dataList.page_is_last;
                    handler.postDelayed(showMoreDataRunnable,
                            PullToRefreshBase.ANIMATION_DURATION_MS + 50);
                    break;
                default:
                    break;
            }
        }
        if (isLastPage) {
            this.hidePullUp();
        } else {
            this.showPullUp();
        }
        this.onRefreshFinished();
    }

    private void scrollToShowMoreData() {
        try {
            final ExpandableSectionList actualListView = this.getInternalView();
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
        } catch (Exception ex) {
            // when exception happened, just ignore it, since here's code just
            // want to scroll list view
        }
    }

    public BaseSectionListAdapter<G, C> getAdapter() {
        return adapter;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.pullView.setOnTouchListener(listener);
    }

    public boolean isLastPage() {
        return isLastPage;
    }

}
