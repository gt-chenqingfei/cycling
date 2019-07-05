package com.beastbikes.android.embapi;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.framework.android.utils.PackageUtils;
import com.beastbikes.framework.android.webkit.JSONResponse;
import com.beastbikes.framework.android.webkit.RequestInterceptor;

public class AgentInterceptor implements RequestInterceptor {

    @Override
    public WebResourceResponse intercept(WebView view, String method, String url, Map<String, String> headers) {
        final Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", "");
        json.put("platform", "android");
        json.put("versionCode", PackageUtils.getVersionCode(BeastBikes
                .getInstance().getApplicationContext()));
        json.put("versionName", PackageUtils.getVersionName(BeastBikes
                .getInstance().getApplicationContext()));
        return new JSONResponse(json);
    }

}
