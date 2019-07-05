package com.beastbikes.framework.ui.android.lib.view.search;

import java.util.List;

public interface MySearchListener {

    /**
     * 异步加载历史纪录数据
     */
    List<?> loadHistoryData(String searchKey);

    /**
     * 同步拉取联想结果
     */
    List<?> loadIntelligenceData(String searchKey, String keyword);

    /**
     * 转换历史数据到string
     */
    CharSequence getHistoryCharSequence(Object resultValue);

    /**
     * 转换联想数据到string
     */
    CharSequence getIntelligenceCharSequence(Object resultValue);

    /**
     * 存储历史纪录数据
     */
    void recordHistory(String searchKey, String keyword);

    /**
     * 清除历史纪录
     */
    void clearHistory(String searchKey);

    /**
     * 获取搜索的唯一索引
     */
    String getSearchKey();

    /**
     * 触发搜索操作
     */
    void goSearch(String searchKey, String keyword);

    /**
     * 历史纪录中的某一条被点击
     */
    void onHistoryItemClicked(Object historyItem);

    /**
     * 智能联想中的某一条被点击
     */
    void onIntelligenceItemClicked(Object intelligenceItem);



}
