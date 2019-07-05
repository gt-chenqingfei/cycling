package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;


public class PullToRefreshListView extends
        PullToRefreshAdapterViewBase<ListView> {

    class InternalListView extends ListView {

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public ContextMenuInfo getContextMenuInfo() {
            return super.getContextMenuInfo();
        }
    }

    public PullToRefreshListView(Context context) {
        super(context);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullToRefreshListView(Context context, int mode) {
        super(context, mode);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDisableScrollingWhileRefreshing(false);
    }

    @Override
    public ContextMenuInfo getContextMenuInfo() {
        return ((InternalListView) getRefreshableView()).getContextMenuInfo();
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = new InternalListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        lv.setSelector(android.R.color.transparent);
        return lv;
    }

}
