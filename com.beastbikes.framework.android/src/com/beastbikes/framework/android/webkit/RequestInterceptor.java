package com.beastbikes.framework.android.webkit;

import java.util.Map;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public interface RequestInterceptor {

    public WebResourceResponse intercept(WebView view, String method, String url, Map<String, String> headers);

}
