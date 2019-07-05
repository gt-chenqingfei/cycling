package com.beastbikes.android.modules.cycling.club.ui.widget.richeditor;

import android.os.Message;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by caoxiao on 16/3/31.
 */
public class PrivateApiBridgeMode {
    // Message added in commit:
    // http://omapzoom.org/?p=platform/frameworks/base.git;a=commitdiff;h=9497c5f8c4bc7c47789e5ccde01179abc31ffeb2
    // Which first appeared in 3.2.4ish.
    private static final int EXECUTE_JS = 194;

    Method sendMessageMethod;
    Object webViewCore;
    boolean initFailed;

    @SuppressWarnings("rawtypes")
    private void initReflection(WebView webView) {
        Object webViewObject = webView;
        Class webViewClass = WebView.class;
        try {
            Field f = webViewClass.getDeclaredField("mProvider");
            f.setAccessible(true);
            webViewObject = f.get(webView);
            webViewClass = webViewObject.getClass();
        } catch (Throwable e) {
            // mProvider is only required on newer Android releases.
        }

        try {
            Field f = webViewClass.getDeclaredField("mWebViewCore");
            f.setAccessible(true);
            webViewCore = f.get(webViewObject);

            if (webViewCore != null) {
                sendMessageMethod = webViewCore.getClass().getDeclaredMethod("sendMessage", Message.class);
                sendMessageMethod.setAccessible(true);
            }
        } catch (Throwable e) {
            initFailed = true;
        }
    }

    public void onNativeToJsMessageAvailable(WebView webView,String js) {
        if (sendMessageMethod == null && !initFailed) {
            initReflection(webView);
        }
        // webViewCore is lazily initialized, and so may not be available right away.
        if (sendMessageMethod != null) {
            Message execJsMessage = Message.obtain(null, EXECUTE_JS, js);
            try {
                sendMessageMethod.invoke(webViewCore, execJsMessage);
            } catch (Throwable e) {
            }
        }
    }

}