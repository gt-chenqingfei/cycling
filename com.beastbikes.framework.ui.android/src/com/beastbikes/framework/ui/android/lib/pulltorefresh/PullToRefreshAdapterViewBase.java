package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

public abstract class PullToRefreshAdapterViewBase<T extends AbsListView>
        extends PullToRefreshBase<T> implements OnScrollListener {

    private int lastSavedFirstVisibleItem = -1;
    private OnScrollListener onScrollListener;
    private OnLastItemVisibleListener onLastItemVisibleListener;

    public PullToRefreshAdapterViewBase(Context context) {
        super(context);
        refreshableView.setOnScrollListener(this);

    }

    public PullToRefreshAdapterViewBase(Context context, int mode) {
        super(context, mode);
        refreshableView.setOnScrollListener(this);
    }

    public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshableView.setOnScrollListener(this);
    }

    @Override
    abstract public ContextMenuInfo getContextMenuInfo();

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
                         final int visibleItemCount, final int totalItemCount) {

        if (null != onLastItemVisibleListener) {
            // detect if last item is visible
            if (visibleItemCount > 0
                    && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                // only process first event
                if (firstVisibleItem != lastSavedFirstVisibleItem) {
                    lastSavedFirstVisibleItem = firstVisibleItem;
                    onLastItemVisibleListener.onLastItemVisible();
                }
            }
        }

        if (null != onScrollListener) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    @Override
    public final void onScrollStateChanged(final AbsListView view,
                                           final int scrollState) {
        if (null != onScrollListener) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void setBackToTopView(ImageView mTopImageView) {
        mTopImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshableView instanceof ListView) {
                    ((ListView) refreshableView).setSelection(0);
                } else if (refreshableView instanceof GridView) {
                    ((GridView) refreshableView).setSelection(0);
                }
            }
        });
    }

    public final void setOnLastItemVisibleListener(
            OnLastItemVisibleListener listener) {
        onLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    @Override
    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    private boolean isFirstItemVisible() {
        if (this.refreshableView.getCount() == 0) {
            return true;
        } else if (refreshableView.getFirstVisiblePosition() == 0) {

            final View firstVisibleChild = refreshableView.getChildAt(0);

            if (firstVisibleChild != null) {
                return firstVisibleChild.getTop() >= refreshableView.getTop();
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {
        final int count = this.refreshableView.getCount();
        final int lastVisiblePosition = refreshableView
                .getLastVisiblePosition();

        if (count == 0) {
            return true;
        } else if (lastVisiblePosition == count - 1) {

            final int childIndex = lastVisiblePosition
                    - refreshableView.getFirstVisiblePosition();
            final View lastVisibleChild = refreshableView
                    .getChildAt(childIndex);

            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= refreshableView
                        .getBottom();
            }
        }
        return false;
    }
}
