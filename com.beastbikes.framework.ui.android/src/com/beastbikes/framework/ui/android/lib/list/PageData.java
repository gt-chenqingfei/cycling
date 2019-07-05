package com.beastbikes.framework.ui.android.lib.list;

import java.io.Serializable;
import java.util.ArrayList;

public class PageData<K, T> implements Serializable {

    private static final long serialVersionUID = -5607262592780784855L;

    public int newCount;

    public boolean page_is_last;

    public K maxId;

    public K minId;

    public long total;

    public ArrayList<T> data;

    // userinfo
    public String nickname;

    public boolean enable;

    public void copyTo(PageData<K, ?> data) {
        data.enable = enable;
        data.maxId = maxId;
        data.newCount = newCount;
        data.nickname = nickname;
        data.minId = minId;
        data.page_is_last = page_is_last;
        data.total = total;
    }

}
