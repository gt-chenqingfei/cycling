package com.beastbikes.android.embapi;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;

//import com.avos.avoscloud.AVUser;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.framework.android.webkit.JSONResponse;
import com.beastbikes.framework.android.webkit.RequestInterceptor;

public class UserInterceptor implements RequestInterceptor {

    @Override
    public WebResourceResponse intercept(WebView view, String method, String url, Map<String, String> headers) {
        final AVUser user = AVUser.getCurrentUser();
        if (user == null) {
            return new JSONResponse("");
        }

        final Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", user.getObjectId());
        json.put("username", user.getUsername());
        json.put("email", user.getEmail());
//        json.put("authenticated", username.isAuthenticated());
        return new JSONResponse(json);
    }

}
