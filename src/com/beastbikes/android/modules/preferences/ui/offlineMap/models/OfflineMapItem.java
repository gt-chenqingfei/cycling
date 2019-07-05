package com.beastbikes.android.modules.preferences.ui.offlineMap.models;

import java.util.Comparator;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.beastbikes.android.modules.preferences.ui.offlineMap.utils.pinyin.PinyinUtil;

public class OfflineMapItem {

    public static final Comparator<OfflineMapItem> DEFAULT_COMPARATOR = new Comparator<OfflineMapItem>() {

        @Override
        public int compare(OfflineMapItem lhs, OfflineMapItem rhs) {
            int f1 = lhs.getStatus() == MKOLUpdateElement.FINISHED ? 1 : 0;
            int f2 = rhs.getStatus() == MKOLUpdateElement.FINISHED ? 1 : 0;
            if (f1 != f2) {
                return f1 - f2;
            }

            String s1 = lhs.getPinyin();
            String s2 = rhs.getPinyin();
            int len1 = s1.length();
            int len2 = s2.length();
            int n = Math.min(len1, len2);
            char v1[] = s1.toCharArray();
            char v2[] = s2.toCharArray();
            int pos = 0;
            while (n-- != 0) {
                char c1 = v1[pos];
                char c2 = v2[pos];
                if (c1 != c2) {
                    return c1 - c2;
                }
                pos++;
            }
            return len1 - len2;
        }

    };

    public static Comparator<OfflineMapItem> TIME_DESC = new Comparator<OfflineMapItem>() {

        @Override
        public int compare(OfflineMapItem lhs, OfflineMapItem rhs) {
            final long diff = lhs.getFinishTime() - rhs.getFinishTime();
            return diff > 0 ? -1 : diff < 0 ? 1 : 0;
        }

    };

    /**
     * 下载信息
     */
    private volatile MKOLUpdateElement downInfo;
    /**
     * 城市基本信息
     */
    private volatile MKOLSearchRecord cityInfo;

    /**
     * 中文转拼音，用于排序
     */
    private String pinyin;

    /**
     * 下载完成的时间，用于排序
     */
    private long finishTime;

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public int getProgress() {
        if (downInfo != null) {
            return downInfo.ratio;
        }
        return 0;
    }

    public int getStatus() {
        if (downInfo != null) {
            return downInfo.status;
        }
        return MKOLUpdateElement.UNDEFINED;
    }

    public void setStatus(int status) {
        if (downInfo != null) {
            downInfo.status = status;
        }
    }

    public boolean isHavaUpdate() {
        if (downInfo != null) {
            return downInfo.update;
        }
        return false;
    }

    public String getCityName() {
        if (cityInfo != null) {
            return cityInfo.cityName;
        }
        return "";
    }

    public String getPinyin() {
        return pinyin;
    }

    public int getCityId() {
        if (cityInfo != null) {
            return cityInfo.cityID;
        }
        return 0;
    }

    public MKOLSearchRecord getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(MKOLSearchRecord record) {
        this.cityInfo = record;
        pinyin = PinyinUtil.getPinYin(record.cityName);
    }

    public MKOLUpdateElement getDownInfo() {
        return downInfo;
    }

    public void setDownInfo(MKOLUpdateElement element) {
        this.downInfo = element;
    }

    public int getSize() {
        if (cityInfo != null) {
            return cityInfo.size;
        }
        return 0;
    }

}
