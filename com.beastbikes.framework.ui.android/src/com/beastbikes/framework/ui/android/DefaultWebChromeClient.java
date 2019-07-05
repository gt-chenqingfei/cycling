package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class DefaultWebChromeClient extends WebChromeClient {

    private static final String TAG = "DefaultWebChromeClient";

    private static final Logger logger = LoggerFactory.getLogger(TAG);

    private final WebActivity webActivity;

    public DefaultWebChromeClient(WebActivity webActivity) {
        this.webActivity = webActivity;
    }

    @Override
    public void onCloseWindow(WebView window) {
        final Context context = window.getContext();
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               final JsResult result) {
        new AlertDialog.Builder(view.getContext()).setCancelable(true)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int what) {
                        dialog.dismiss();
                        result.cancel();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int what) {
                        dialog.dismiss();
                        result.confirm();
                    }
                }).create().show();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, final JsPromptResult result) {
        final TextView input = new TextView(view.getContext());
        if (!TextUtils.isEmpty(defaultValue)) {
            input.setText(defaultValue);
        }

        new AlertDialog.Builder(view.getContext()).setCancelable(true)
                .setTitle(android.R.string.dialog_alert_title)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int what) {
                        dialog.dismiss();
                        result.cancel();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int what) {
                        dialog.dismiss();
                        result.confirm(input.getText().toString());
                    }
                }).create().show();
        return true;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        final Context context = view.getContext();
        if (context instanceof Activity) {
            ((Activity) context).setTitle(title);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        this.webActivity.setProgress(newProgress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, final String message,
                             final JsResult result) {
//        Toasts.show(view.getContext(), message);
        new AlertDialog.Builder(view.getContext()).setCancelable(true)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int what) {
                        dialog.dismiss();
                        result.cancel();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int what) {
                        dialog.dismiss();
                        result.confirm();
                    }
                }).create().show();
        return true;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        final String msg = String.format(Locale.getDefault(),
                "%s#%d: %s", cm.sourceId(), cm.lineNumber(),
                cm.message());

        switch (cm.messageLevel()) {
            case DEBUG:
                logger.debug(msg);
                break;
            case ERROR:
                logger.error(msg);
                break;
            case LOG:
                logger.info(msg);
                break;
            case TIP:
                logger.trace(msg);
                break;
            case WARNING:
                logger.warn(msg);
                break;
        }

        return super.onConsoleMessage(cm);
    }


}
