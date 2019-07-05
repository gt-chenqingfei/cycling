package com.beastbikes.android.modules.qiniu;

/**
 * Created by caoxiao on 16/5/11.
 */
public interface QiNiuUploadCallBack {

    void onComplete(String key);
    void onError();
}
