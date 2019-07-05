package com.beastbikes.android.embapi;

import java.util.HashMap;
import java.util.Map;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.android.webkit.JSONResponse;
import com.beastbikes.framework.android.webkit.RequestInterceptor;

public class NetworkInterceptor implements RequestInterceptor {

    @Override
    public WebResourceResponse intercept(WebView view, String method, String url, Map<String, String> headers) {
        final NetworkInfo network = ConnectivityUtils.getActiveNetwork(view.getContext());
        if (null == network)
            return new JSONResponse("");

        final Map<String, Object> json = new HashMap<String, Object>();
        json.put("available", network.isAvailable());
        json.put("name", network.getTypeName());
        json.put("roaming", network.isRoaming());

        switch (network.getState()) {
            case UNKNOWN:
                json.put("state", 0);
                break;
            case CONNECTING:
                json.put("state", 1);
                break;
            case CONNECTED:
                json.put("state", 2);
                break;
            case SUSPENDED:
                json.put("state", 3);
                break;
            case DISCONNECTING:
                json.put("state", 4);
                break;
            case DISCONNECTED:
                json.put("state", 5);
                break;
        }

        switch (network.getType()) {
            case ConnectivityManager.TYPE_ETHERNET:
                json.put("type", 1);
                break;
            case ConnectivityManager.TYPE_WIFI:
                json.put("type", 2);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                json.put("type", 3);
                break;
            default:
                json.put("type", 0);
                break;
        }

        return new JSONResponse(json);
    }

}
