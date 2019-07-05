package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.util.List;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;
import com.beastbikes.framework.ui.android.lib.list.PageData;
import com.beastbikes.framework.ui.android.lib.list.PageRefreshData;

public interface AbsListProxable<K, D> {

    void onStart();

    void setOnItemClickListener(OnItemClickListener listener);

    void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener);

    void onStop();

    BaseListAdapter<D> getAdapter();

    void refreshList();

    void onLoadFailed(String failure);

    void onLoadSucessfully(List<D> data);

    void onLoadSucessAddfully(List<D> data);

    void onLoadSucessfully(PageData<K, D> dataList);

    void onLoadSucessfully(PageRefreshData<K, D> dataList);

    boolean isLastPage();

    void onPullUpRefresh();

}
