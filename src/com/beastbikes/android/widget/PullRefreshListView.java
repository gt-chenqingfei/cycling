/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.beastbikes.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.beastbikes.android.R;

public class PullRefreshListView extends ListView implements OnScrollListener {

    private float lastY = -1; // save event y
    private Scroller scroller; // used for scroll back
    private OnScrollListener scrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private onListViewListener listViewListener;

    // -- header view
    private ListViewHeader headerView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout headerViewContent;
    private int headerViewHeight; // header view's height
    private boolean enablePullRefresh = true;
    private boolean pullRefreshing = false; // is refreashing.

    // -- footer view
    private ListViewFooter footerView;
    private boolean enablePullLoad;
    private boolean pullLoading;
    private boolean isFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    private int totalItemCount;

    // for mScroller, scroll back from header or footer.
    private int scrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.
    private AttributeSet attrs;
    private int defStyle;
    private boolean isWhiteHead;

    /**
     * @param context
     */
    public PullRefreshListView(Context context) {
        super(context);
        this.setDividerHeight(0);
        this.initWithContext(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        this.setDividerHeight(0);
        this.initWithContext(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.attrs = attrs;
        this.defStyle = defStyle;
        this.setDividerHeight(0);
        this.initWithContext(context);
    }

    private void initWithContext(Context context) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PullRefreshListView, defStyle, 0);
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.PullRefreshListView_isWhiteAnim:
                        isWhiteHead = a.getBoolean(attr, false);
                        break;

                }

            }
            a.recycle();
        }

        this.scroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // init header view
        this.headerView = new ListViewHeader(context, isWhiteHead);
        this.headerViewContent = (RelativeLayout) headerView
                .findViewById(R.id.list_view_head);
        this.addHeaderView(headerView);

        // init footer view
        this.footerView = new ListViewFooter(context);

        // init header height
        this.headerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        headerViewHeight = headerViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (this.isFooterReady) {
            //this.isFooterReady = true;
            this.addFooterView(footerView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        this.enablePullRefresh = enable;
        if (!this.enablePullRefresh) { // disable, hide the content
            this.headerViewContent.setVisibility(View.INVISIBLE);
        } else {
            this.headerViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        this.isFooterReady = enable;
        this.enablePullLoad = enable;
        if (!this.enablePullLoad) {
            this.footerView.hide();
            this.footerView.setOnClickListener(null);
            // make sure "pull up" don't show a line in bottom when listview
            // with one page
            this.setFooterDividersEnabled(false);
        } else {
            this.pullLoading = false;
            this.footerView.show();
            this.footerView.setState(ListViewFooter.STATE_NORMAL);
            // make sure "pull up" don't show a line in bottom when listview
            // with one page
            this.setFooterDividersEnabled(true);
            // both "pull up" and "click" will invoke load more.
            this.footerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (this.pullRefreshing == true) {
            this.pullRefreshing = false;
            this.resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore(int state) {
        if (this.pullLoading == true) {
            this.pullLoading = false;
            this.footerView.setState(state);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        // TODO
    }

    private void invokeOnScrolling() {
        if (this.scrollListener instanceof OnListScrollListener) {
            OnListScrollListener l = (OnListScrollListener) scrollListener;
            l.onListScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        this.headerView.setVisiableHeight((int) delta
                + this.headerView.getVisiableHeight());
        if (enablePullRefresh && !pullRefreshing) { // 未处于刷新状态，更新箭头
            if (this.headerView.getVisiableHeight() > headerViewHeight) {
                this.headerView.setState(ListViewHeader.STATE_READY);
            } else {
                this.headerView.setState(ListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = headerView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (pullRefreshing && height <= headerViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (pullRefreshing && height > headerViewHeight) {
            finalHeight = headerViewHeight;
        }
        this.scrollBack = SCROLLBACK_HEADER;
        this.scroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = footerView.getBottomMargin() + (int) delta;
        if (this.enablePullLoad && !this.pullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                this.footerView.setState(ListViewFooter.STATE_READY);
            } else {
                this.footerView.setState(ListViewFooter.STATE_NORMAL);
            }
        }
        this.footerView.setBottomMargin(height);

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = footerView.getBottomMargin();
        if (bottomMargin > 0) {
            this.scrollBack = SCROLLBACK_FOOTER;
            this.scroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    public void resetHeadViewAnim(int resAnim) {
        this.headerView.setRefreshAnim(resAnim);
    }

    public void resetHeadViewBackground(int resColor) {
        this.headerView.setViewBackgroundColor(resColor);
    }

    private void startLoadMore() {
        this.pullLoading = true;
        this.footerView.setState(ListViewFooter.STATE_LOADING);
        if (this.listViewListener != null) {
            this.listViewListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.lastY == -1) {
            this.lastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.lastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - lastY;
                this.lastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (this.headerView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    this.updateHeaderHeight(deltaY / OFFSET_RADIO);
                    this.invokeOnScrolling();
                } else if (getLastVisiblePosition() == this.totalItemCount - 1
                        && (this.footerView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    // if (this.footerView.getState() ==
                    // ListViewFooter.STATE_NO_MORE)
                    // break;

                    this.updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                this.lastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (this.enablePullRefresh
                            && this.headerView.getVisiableHeight() > this.headerViewHeight) {
                        this.pullRefreshing = true;
                        this.headerView.setState(ListViewHeader.STATE_REFRESHING);
                        if (this.listViewListener != null) {
                            this.listViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == totalItemCount - 1) {
                    // invoke load more.
                    if (this.enablePullLoad
                            && this.footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA
                            && !this.pullLoading) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            if (this.scrollBack == SCROLLBACK_HEADER) {
                this.headerView.setVisiableHeight(scroller.getCurrY());
            } else {
                this.footerView.setBottomMargin(scroller.getCurrY());
            }
            this.postInvalidate();
            this.invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        this.scrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.scrollListener != null) {
            this.scrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        this.totalItemCount = totalItemCount;
        if (this.scrollListener != null) {
            this.scrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    public void setListViewListener(onListViewListener l) {
        this.listViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnListScrollListener extends OnScrollListener {
        public void onListScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface onListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }
}
