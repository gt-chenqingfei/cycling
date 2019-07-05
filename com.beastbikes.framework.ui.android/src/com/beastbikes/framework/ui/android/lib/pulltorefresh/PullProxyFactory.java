package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.io.Serializable;


public class PullProxyFactory {
    private static final Long defaultLong = Long.valueOf(-1);

    @SuppressWarnings("unchecked")
    public static <T> Pageable<T> getPageable(Class<T> cls) {
        if (cls == Long.class) {
            return (Pageable<T>) getDefaultLongPageable();
        } else if (cls == String.class) {
            return (Pageable<T>) getDefaultStringPageable();
        }
        return null;
    }

    public static Pageable<Long> getDefaultLongPageable() {
        Pageable<Long> pageable = new Pageable<Long>() {

            @Override
            public Long initMaxId(String cacheKey) {
//				return StaticWrapper.cacheMgr.getMaxId(cacheKey);
                return 0l;
            }

            @Override
            public boolean initIsLastPage(Long maxId) {
                return maxId <= 0;
            }

            @Override
            public Long chooseMaxId(Long oldMaxId, Long newMaxId) {
                return newMaxId != null && newMaxId > 0 ? newMaxId : oldMaxId;
            }

            @Override
            public void cacheData(String key, Serializable obj,
                                  long lastModify, Long maxId) {
//				StaticWrapper.cacheMgr.cacheData(key, obj, lastModify, maxId);

            }

            @Override
            public Long defValue() {
                return defaultLong;
            }
        };
        return pageable;
    }

    public static Pageable<String> getDefaultStringPageable() {
        Pageable<String> pageable = new Pageable<String>() {

            @Override
            public String initMaxId(String cacheKey) {
//				return StaticWrapper.cacheMgr.getMaxIdAsString(cacheKey);
                return "";
            }

            @Override
            public boolean initIsLastPage(String maxId) {
                return maxId == null;
            }

            @Override
            public String chooseMaxId(String oldMaxId, String newMaxId) {
                return newMaxId != null ? newMaxId : oldMaxId;
            }

            @Override
            public void cacheData(String key, Serializable obj,
                                  long lastModify, String maxId) {
//				StaticWrapper.cacheMgr.cacheData(key, obj, lastModify, maxId);

            }

            @Override
            public String defValue() {
                return null;
            }
        };
        return pageable;
    }
}
