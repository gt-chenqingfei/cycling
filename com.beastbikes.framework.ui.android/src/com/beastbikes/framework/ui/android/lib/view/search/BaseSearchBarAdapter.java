package com.beastbikes.framework.ui.android.lib.view.search;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public abstract class BaseSearchBarAdapter extends BaseAdapter implements
        Filterable {

    private static final String CLEAR_HISTORY ="清除历史纪录";

    // 筛选器
    private ArrayFilter mFilter;
    // 搜索监听器
    private final MySearchListener searchListener;

    // 搜索历史
    private final List<Object> historyData;
    // 智能联想的数据
    private List<Object> intelligenceData;

    private boolean showHistory = true;
    private final String searchKey;

    /**
     * search bar adapter
     *
     * @param
     */
    public BaseSearchBarAdapter(MySearchListener searchListener) {
        this.searchListener = searchListener;
        this.searchKey = searchListener.getSearchKey();
        historyData = new ArrayList<Object>();
        intelligenceData = new ArrayList<Object>();
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public boolean isClear(int position) {
        Object result = getItem(position);
        if (result == null)
            return false;
        return result.equals(CLEAR_HISTORY);
    }

    @Override
    public abstract View getView(final int position, View convertView,
                                 ViewGroup parent);

    /**
     * 当showhistory 是true的时候，返回的是历史数据，否则是智能提示数据
     *
     * @param showHistory
     * @param datas
     */
    private void onDataChanged(boolean showHistory, List<Object> datas) {
        this.showHistory = showHistory;
        if (showHistory) {
            historyData.clear();
            if (datas != null && datas.size() > 0) {
                historyData.addAll(datas);
            }
        } else {
            intelligenceData.clear();
            if (datas != null && datas.size() > 0) {
                intelligenceData.addAll(datas);
            }
        }
        this.notifyDataSetChanged();

    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    protected CharSequence getStringFromItem(Object resultValue) {
        if (historyData.contains(resultValue)) {
            return searchListener.getHistoryCharSequence(resultValue);
        } else {
            return searchListener.getIntelligenceCharSequence(resultValue);
        }
    }

    @Override
    public int getCount() {
        if (!showHistory) {
            // 现在展示联想数据
            return intelligenceData == null ? 0 : intelligenceData.size();
        } else {
            // 现在长时历史纪录
            return historyData == null ? 0 : historyData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (!showHistory) {
            // 现在展示联想数据
            return intelligenceData.get(position);
        } else {
            // 现在长时历史纪录
            return historyData.get(position);
        }
    }

    public void clearItems() {
        if (this.historyData != null) {
            this.historyData.clear();
            this.notifyDataSetChanged();
        }
        if (intelligenceData != null) {
            intelligenceData.clear();
            notifyDataSetChanged();
        }
    }

    public void addIntelligenceData(final List<?> items) {

        if (items == null) {
            return;
        }

        if (this.intelligenceData == null) {
            this.intelligenceData = new ArrayList<Object>();
        }
        this.intelligenceData.addAll(items);

        this.notifyDataSetChanged();
    }

    public void tryAdd(List<?> items) {
        if (items == null) {
            return;
        }

        if (this.intelligenceData == null) {
            this.intelligenceData = new ArrayList<Object>();
        }
        for (Object obj : items) {
            intelligenceData.add(obj);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ArrayFilter extends Filter {

        private final FilterResults historyResult;
        private final FilterResults intelligenceResult;
        private final List<Object> hDatas, iDatas;

        public ArrayFilter() {
            historyResult = new FilterResults();
            intelligenceResult = new FilterResults();
            hDatas = new ArrayList<Object>();
            iDatas = new ArrayList<Object>();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if (resultValue.equals(CLEAR_HISTORY)) {
                return "";
            }
            return getStringFromItem(resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            if (prefix == null || prefix.length() < 1) {
                // 当关键字为空的时候，显示历史纪录
                hDatas.clear();
                List<?> history = searchListener.loadHistoryData(searchKey);
                if (history != null && history.size() > 0) {
                    // 当历史纪录不为空的时候，默认显示“清除历史纪录”
                    hDatas.addAll(history);
                    hDatas.add(CLEAR_HISTORY);
                }
                historyResult.values = hDatas;
                historyResult.count = hDatas == null ? 0 : hDatas.size();
                return historyResult;
            }

            iDatas.clear();
            intelligenceResult.count = 0;
            List<?> iData = searchListener.loadIntelligenceData(searchKey,
                    prefix.toString());
            if (iData != null && iData.size() > 0) {
                iDatas.addAll(iData);
            }
            intelligenceResult.values = iDatas;
            intelligenceResult.count = iDatas == null ? 0 : iDatas.size();
            return intelligenceResult;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results == historyResult) {
                onDataChanged(true, (List<Object>) results.values);
            } else {
                onDataChanged(false, (List<Object>) results.values);
            }
        }
    }

}
