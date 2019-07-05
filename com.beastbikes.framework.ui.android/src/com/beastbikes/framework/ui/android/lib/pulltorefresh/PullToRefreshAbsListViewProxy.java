package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.os.Build.VERSION;
import android.util.Log;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;
import com.beastbikes.framework.ui.android.lib.list.OnAdapterChangeListener;
import com.beastbikes.framework.ui.android.lib.list.PageData;
import com.beastbikes.framework.ui.android.lib.list.PageRefreshData;

public class PullToRefreshAbsListViewProxy<K, D, V extends AbsListView> extends
        PullToRefreshProxy<K, V> implements AbsListProxable<K, D> {

    private static final String TAG = "absproxy";

    protected Pageable<K> pageable;

    protected BaseListAdapter<D> adapter;
    protected boolean shouldCache = false;

    private K maxId;
    protected long lastModify = -1;
    private boolean isLastPage = true;
    private ArrayList<D> newCacheData = null;
    private K newMaxId = null;

    private boolean shouldShowEmptyView = true;

    // private View mask;

    public PullToRefreshAbsListViewProxy(BaseListAdapter<D> adapter,
                                         final PullToRefreshAdapterViewBase<V> pullView, String cacheKey,
                                         PullRefeshListener<K> pullListener, Pageable<K> pageable) {
        super(pullView, cacheKey, pullListener);

        this.adapter = adapter;
        if (null != this.adapter) {
            adapter.setDataChangedListener(new OnAdapterChangeListener() {

                @Override
                public void onDataChanged(int count) {
                    if (!shouldShowEmptyView)
                        return;

                    if (count < 1) {
                        pullView.showEmptyView();
                    } else {
                        pullView.hideEmptyView();
                    }
                }
            });
        } else {
            pullView.showEmptyView();
        }

        shouldCache = false;//StaticWrapper.cacheMgr.shouldSupportCache(this.cacheKey);
        this.pageable = pageable;
        this.maxId = this.pageable.defValue();
        this.newMaxId = this.pageable.defValue();
        this.hidePullUp();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        internalView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        internalView.setOnItemLongClickListener(listener);
    }

    @Override
    public void onCreate() {

//		if (shouldCache) {
//			Object cacheData = StaticWrapper.cacheMgr
//					.getCacheData(this.cacheKey);
//			if (cacheData instanceof ArrayList<?>) {
//				ArrayList<D> data = (ArrayList<D>) cacheData;
//				adapter.add(data);
//			}
//			lastModify = StaticWrapper.cacheMgr.getCacheServerTime(cacheKey);
//			maxId = pageable.initMaxId(cacheKey);
//			newMaxId = maxId;
//			Log.d(TAG, "create max id is " + newMaxId);
//			isLastPage = pageable.initIsLastPage(maxId);
//			if (isLastPage) {
//				this.hidePullUp();
//			} else {
//				this.showPullUp();
//			}
//		}

        super.onCreate();
    }

    public void setAdapter(ListAdapter a) {
        @SuppressWarnings("deprecation")
        int sysVersion = Integer.parseInt(VERSION.SDK);
        if (sysVersion > 10)
            internalView.setAdapter(a);
        else {
            try {
                Class<?> cls = internalView.getClass();
                Method method = cls.getMethod("setAdapter", ListAdapter.class);
                method.invoke(internalView, a);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * re-set internal view's adapter, header, footer and so on.
     */
    @Override
    public void onStart() {

        setAdapter(adapter);
    }

    @Override
    public void onStop() {
        setAdapter(null);
    }

    public void saveCacheData() {
        if (shouldCache && newCacheData != null && newCacheData.size() > 0) {
            pageable.cacheData(this.cacheKey, newCacheData, lastModify,
                    newMaxId);
            Log.d(TAG, "destroy max id is " + newMaxId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveCacheData();
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

    @Override
    public void refreshList() {
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFailed(String failture) {
        this.adapter.notifyDataSetChanged();
        this.onRefreshFinished();
    }

    @Override
    public void onLoadSucessfully(List<D> data) {

        this.onRefreshFinished();

        if (data != null) {
            newCacheData = (ArrayList<D>) data;
            this.adapter.clearItems();
            this.adapter.add(data);
            this.adapter.notifyDataSetChanged();
        }
        this.showPullUp();
    }
    /*
     * (non-Javadoc)
	 * @see com.zhisland.lib.pulltorefresh.AbsListProxable#onLoadSucessAddfully(java.util.List)
	 * ���� list add  for qxh 
	 */

    @Override
    public void onLoadSucessAddfully(List<D> data) {
        this.onRefreshFinished();

        if (data != null && data.size() != 0) {
            this.adapter.add(data);
            this.adapter.notifyDataSetChanged();
            this.showPullUp();
        }

    }

    @Override
    public void onLoadSucessfully(PageRefreshData<K, D> dataList) {
        if (dataList != null) {
            this.lastModify = dataList.lastModify;
            if (dataList.isModified) {
                this.adapter.clearItems();
            }
            this.onLoadSucessfully(dataList, dataList.data);
        } else {
            this.onLoadSucessfully(dataList, null);
        }

    }

    @Override
    public void onLoadSucessfully(PageData<K, D> dataList) {
        if (dataList != null) {
            this.onLoadSucessfully(dataList, dataList.data);
        } else {
            this.onLoadSucessfully(dataList, null);
        }
    }

    protected void onLoadSucessfully(PageData<K, D> dataList,
                                     ArrayList<D> data) {

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

                    this.adapter.clearItems(); // clear cached datas
                    this.adapter.add(data);
                    isLastPage = dataList.page_is_last;

                    newCacheData = data;
                    newMaxId = curMaxId;

                    break;
                case more:
                    this.maxId = curMaxId;
                    this.adapter.add(data);
                    isLastPage = dataList.page_is_last;
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

    @Override
    public BaseListAdapter<D> getAdapter() {
        return adapter;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.pullView.setOnTouchListener(listener);
    }

    @Override
    public boolean isLastPage() {
        return isLastPage;
    }

    /**
     * when set, automatically show and hide will disable
     *
     * @param
     */
    public void hideEmptyViewShow() {
        this.shouldShowEmptyView = false;
        if (pullView != null) {
            pullView.hideEmptyView();
        }
    }

}
