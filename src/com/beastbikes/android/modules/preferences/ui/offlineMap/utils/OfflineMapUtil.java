package com.beastbikes.android.modules.preferences.ui.offlineMap.utils;

public class OfflineMapUtil {

    public static String getSizeStr(long fileLength) {
        String strSize = "";
        try {
            if (fileLength >= 1024 * 1024 * 1024) {
                strSize = (float) Math.round(10 * fileLength
                        / (1024 * 1024 * 1024))
                        / 10 + " GB";
            } else if (fileLength >= 1024 * 1024) {
                strSize = (float) Math.round(10 * fileLength
                        / (1024 * 1024 * 1.0))
                        / 10 + " MB";
            } else if (fileLength >= 1024) {
                strSize = (float) Math.round(10 * fileLength / (1024)) / 10
                        + " KB";
            } else if (fileLength >= 0) {
                strSize = fileLength + " B";
            } else {
                strSize = "0 B";
            }
        } catch (Exception e) {
            strSize = "0 B";
        }
        return strSize;
    }

}
