package com.beastbikes.android.modules.qiniu;

/**
 * Created by caoxiao on 16/5/21.
 */
public interface QiNiuProgressCallback {

    void progressCallBack(String key, double percent);
}
