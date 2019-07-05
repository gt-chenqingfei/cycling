package com.beastbikes.android.embapi;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.res.AssetManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.beastbikes.framework.android.webkit.JavaScriptResponse;
import com.beastbikes.framework.android.webkit.RequestInterceptor;

public class HybridInterceptor implements RequestInterceptor {

    private static final String HYBRID_JS = "webkit" + File.separator + "hybrid.js";

    private static final Logger logger = LoggerFactory.getLogger("HybridInterceptor");

    @Override
    public WebResourceResponse intercept(WebView view, String method, String url, Map<String, String> headers) {
        final AssetManager am = view.getResources().getAssets();

        try {
            return new JavaScriptResponse(am.open(HYBRID_JS));
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
            return null;
        }
    }

}
