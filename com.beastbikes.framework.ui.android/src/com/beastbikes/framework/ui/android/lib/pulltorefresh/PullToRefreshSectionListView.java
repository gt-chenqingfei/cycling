package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;


public class PullToRefreshSectionListView extends
        PullToRefreshAdapterViewBase<ExpandableSectionList> {

    class InternalSectionListView extends ExpandableSectionList {

        public InternalSectionListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ContextMenuInfo getContextMenuInfo() {
            return super.getContextMenuInfo();
        }
    }

    public PullToRefreshSectionListView(Context context) {
        super(context);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullToRefreshSectionListView(Context context, int mode) {
        super(context, mode);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullToRefreshSectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDisableScrollingWhileRefreshing(false);
    }

    @Override
    public ContextMenuInfo getContextMenuInfo() {
        return ((InternalSectionListView) getRefreshableView())
                .getContextMenuInfo();
    }

    @Override
    protected final ExpandableSectionList createRefreshableView(
            Context context, AttributeSet attrs) {
        InternalSectionListView lv = new InternalSectionListView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        lv.setSelector(android.R.color.transparent);
        this.setOnScrollListener(lv);
        return lv;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshableView.configHeader(this.getScrollY());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        refreshableView.configHeader(this.getScrollY());
    }

}
