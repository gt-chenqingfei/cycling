package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.io.Serializable;

public interface Pageable<K> {

    K initMaxId(String cacheKey);

    boolean initIsLastPage(K maxId);

    void cacheData(String key, Serializable obj, long lastModify, K maxId);

    K chooseMaxId(K oldMaxId, K newMaxId);

    K defValue();
}
