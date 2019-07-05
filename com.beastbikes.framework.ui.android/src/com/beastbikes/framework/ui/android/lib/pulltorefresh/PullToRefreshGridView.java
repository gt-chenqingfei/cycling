package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.GridView;

public class PullToRefreshGridView extends
        PullToRefreshAdapterViewBase<GridView> {

    class InternalGridView extends GridView {

        public InternalGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ContextMenuInfo getContextMenuInfo() {
            return super.getContextMenuInfo();
        }
    }

    public PullToRefreshGridView(Context context) {
        super(context);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullToRefreshGridView(Context context, int mode) {
        super(context, mode);
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullToRefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDisableScrollingWhileRefreshing(false);
    }

    @Override
    public ContextMenuInfo getContextMenuInfo() {
        return ((InternalGridView) getRefreshableView()).getContextMenuInfo();
    }

    @Override
    protected GridView createRefreshableView(Context context, AttributeSet attrs) {
        GridView gv = new InternalGridView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        gv.setId(android.R.id.list);
        return gv;
    }

    @Override
    protected void addRefreshableView(Context context, GridView refreshableView) {
        super.addRefreshableView(context, refreshableView);
    }

    ;

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
                         final int visibleItemCount, final int totalItemCount) {
    }

}
