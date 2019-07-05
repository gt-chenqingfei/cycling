package com.beastbikes.android.ble.biz.listener;

import com.beastbikes.android.ble.biz.entity.CentralSession;

import java.util.List;

/**
 * 此listener主要是把蓝牙扫描后的结果分发到页面
 * <p/>
 * 1.首先,在需要监听的activity中bind {
 *
 * @link CentralService}
 * 2.然后调用
 * @link CentralService #setOnScanResultListener(IScanResultListener)
 * 3.最后在 在需要监听的activity 中实现 {@link IScanResultListener}
 * </p>
 */
public interface IScanResultListener {

    /**
     * fired when scan result
     *
     * @param scanResults
     */
    void onScanResult(List<CentralSession> scanResults, CentralSession session);
}