package com.beastbikes.android.modules.preferences.ui.offlineMap.interfaces;

import com.beastbikes.android.modules.preferences.ui.offlineMap.models.OfflineMapItem;

public interface OnOfflineItemStatusChangeListener {
    public void statusChanged(OfflineMapItem item, boolean removed);
}
