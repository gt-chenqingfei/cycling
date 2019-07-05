package com.beastbikes.framework.ui.android.lib.view.search;

import java.util.List;

interface FileterStrategy<T> {

    boolean isMatched(T item, CharSequence prefix);

    CharSequence getCharSequence(Object resultValue);

    void onDataChanged(List<T> newData);
}
