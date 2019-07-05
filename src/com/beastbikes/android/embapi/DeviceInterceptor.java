package com.beastbikes.android.embapi;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.beastbikes.framework.android.utils.TelephonyUtils;
import com.beastbikes.framework.android.webkit.JSONResponse;
import com.beastbikes.framework.android.webkit.RequestInterceptor;

public class DeviceInterceptor implements RequestInterceptor {

    @Override
    public WebResourceResponse intercept(WebView view, String method, String url, Map<String, String> headers) {
        final Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", TelephonyUtils.getDeviceId(view.getContext()));
        return new JSONResponse(json);
    }

}
