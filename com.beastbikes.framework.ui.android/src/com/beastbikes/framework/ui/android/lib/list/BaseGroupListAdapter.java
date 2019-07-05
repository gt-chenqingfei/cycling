package com.beastbikes.framework.ui.android.lib.list;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.RecyclerListener;

import com.beastbikes.framework.ui.android.R;

public class BaseGroupListAdapter extends BaseListAdapter<Object> {

    private ArrayList<BaseListAdapter<?>> adapters;
    private HashMap<BaseListAdapter<?>, Integer> viewTypeMaps = new HashMap<BaseListAdapter<?>, Integer>();
    private BaseListAdapter<?> curAdapter;
    protected RecyclerListener recycleListener = new CatchableRecyclerListener() {
        @Override
        public void intlOnMovedToScrapHeap(final View view) {
            if (view != null) {
                view.destroyDrawingCache();
            }
            BaseGroupListAdapter.this.recycleView(view);
        }
    };

    public BaseGroupListAdapter(Handler handler, final AbsListView listView,
                                ArrayList<BaseListAdapter<?>> adapters) {
        super(handler, listView, null);
        if (adapters == null || listView == null)
            throw new UnsupportedOperationException();

        listView.setRecyclerListener(this.recycleListener);
        this.adapters = adapters;
        if (adapters.size() < 1) {
            this.curAdapter = null;
        } else {
            this.curAdapter = adapters.get(0);
        }
    }

    protected void recycleView(View view) {
        BaseListAdapter<?> adapter = (BaseListAdapter<?>) view
                .getTag(R.id.group_list_view_type);

        if (adapter != null) {
            adapter.recycleView(view);
        }
    }

    public boolean show(BaseListAdapter<?> adapter) {

        if (this.adapters.contains(adapter)) {
            this.curAdapter = adapter;
            this.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void addAdapter(BaseListAdapter<?> adapter) {
        if (this.adapters.contains(adapter))
            return;

        adapters.add(adapter);
    }

    @Override
    public int getItemViewType(int position) {
        if (viewTypeMaps == null || curAdapter == null || viewTypeMaps.get(curAdapter) == null)
            return 0;
        int itemType = viewTypeMaps.get(curAdapter) + curAdapter.getItemViewType(position);
        return itemType;
    }

    @Override
    public int getViewTypeCount() {
        int viewCount = 0;
        for (BaseListAdapter<?> adapter : adapters) {
            viewTypeMaps.put(adapter, viewCount);
            viewCount += adapter.getViewTypeCount();
        }
        return viewCount;
    }

    @Override
    public int getCount() {
        return curAdapter == null ? 0 : curAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return curAdapter == null ? null : curAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return curAdapter == null ? 0 : curAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = curAdapter.getView(position, convertView, parent);
        view.setTag(R.id.group_list_view_type, curAdapter);
        return view;
    }

    public BaseListAdapter<?> getCurAdapter() {
        return curAdapter;
    }

}
