package com.beastbikes.android.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.GridView;
import android.widget.ListView;

import com.beastbikes.android.R;

public class SwipeRefreshAndLoadLayout extends SwipeRefreshLayout {

    private final int mTouchSlop;
    private ListView listView;
    private GridView gridView;
    private OnLoadListener mOnLoadListener;

    private float firstTouchY;
    private float lastTouchY;

    private boolean isLoading = false;
    private View mListViewFooter;

    private boolean canLoad = true;

    public SwipeRefreshAndLoadLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshAndLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,
                false);
        this.setColorSchemeResources(R.color.designcolor_c7);
    }

    // set the child view of RefreshLayout,ListView
    public void setChildListView(ListView listView) {
        this.listView = listView;
    }

    public void setChildGridView(GridView gridView) {
        this.gridView = gridView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstTouchY = event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                if(canLoad) {
                    lastTouchY = event.getRawY();
                    if (canLoadMore()) {
                        loadData();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                if(listView != null) {
                    lastTouchY = (int) event.getRawY();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean canLoadMore() {
        return isBottom() && !isLoading && isPullingUp();
    }

    private boolean isBottom() {
        if (null != gridView && gridView.getCount() > 0) {
            if (gridView.getLastVisiblePosition() == gridView.getAdapter().getCount() - 1 &&
                    gridView.getChildAt(gridView.getChildCount() - 1).getBottom() <= gridView.getHeight()) {
                return true;
            }
        }

        if (null != listView && listView.getCount() > 0) {
            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 &&
                    listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                return true;
            }
        }
        return false;
    }

    private boolean isPullingUp() {
        return (firstTouchY - lastTouchY) >= mTouchSlop;
    }

    private void loadData() {
        if (mOnLoadListener != null) {
            setLoading(true);
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (gridView != null) {
            if (isLoading) {
                if (isRefreshing()) {
                    setRefreshing(false);
                }
                gridView.setSelection(gridView.getAdapter().getCount() - 1);
                mOnLoadListener.onLoad();
            } else {
                firstTouchY = 0;
                lastTouchY = 0;
            }
        }
        else  if(listView != null)
        {
            if (isLoading) {
                if (isRefreshing()) {
                    setRefreshing(false);
                }
                listView.addFooterView(mListViewFooter);
                mOnLoadListener.onLoad();
            } else {
                listView.removeFooterView(mListViewFooter);
                firstTouchY = 0;
                lastTouchY = 0;
            }
        }
    }

    public void setCanLoad(boolean canLoad) {
        this.canLoad = canLoad;
        if(!canLoad)
        {
            if(listView != null)
            {
                listView.removeFooterView(mListViewFooter);
            }
        }
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    public interface OnLoadListener {
        public void onLoad();
    }
}