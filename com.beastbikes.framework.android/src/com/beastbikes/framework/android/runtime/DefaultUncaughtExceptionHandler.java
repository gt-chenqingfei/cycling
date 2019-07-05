package com.beastbikes.framework.android.runtime;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUncaughtExceptionHandler.class);

    private UncaughtExceptionHandler handler;

    private Context mContext;

    public DefaultUncaughtExceptionHandler(Context context) {
        this.mContext = context;
        this.handler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        logger.error(t.getMessage(), t);

        if (t instanceof ConnectException || t instanceof UnknownHostException) {
            return; // ignore network error
        }

        if (null == this.handler) {
            return;
        }

        this.handler.uncaughtException(thread, t);

//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.mContext.startActivity(intent);
    }

}
