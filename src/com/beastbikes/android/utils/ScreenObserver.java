package com.beastbikes.android.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;

public class ScreenObserver {

    private static final Logger logger = LoggerFactory.getLogger(ScreenObserver.class);

    private static String TAG = "ScreenObserver";
    private Context context;
    private ScreenBroadcastReceiver screenReceiver;
    private ScreenStateListener screenStateListener;
    private  Method reflectScreenState;

    public ScreenObserver(Context context) {
        this.context = context;
        this.screenReceiver = new ScreenBroadcastReceiver();
        try {
            reflectScreenState = PowerManager.class.getMethod("isScreenOn", new Class[]{});
        } catch (NoSuchMethodException nsme) {
            logger.error(TAG + ": API < 7," + nsme);
        }
    }

    /**
     * screen状态广播接收者
     *
     * @author zhangyg
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                screenStateListener.onScreenOn();
            }

            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                screenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 请求screen状态更新
     *
     * @param listener
     */
    public void requestScreenStateUpdate(ScreenStateListener listener) {
        screenStateListener = listener;
        startScreenBroadcastReceiver();
        firstGetScreenState();
    }

    /**
     * 第一次请求screen状态
     */
    private void firstGetScreenState() {
        PowerManager manager = (PowerManager) context
                .getSystemService(Activity.POWER_SERVICE);
        if (isScreenOn(manager)) {
            if (screenStateListener != null) {
                screenStateListener.onScreenOn();
            }
        } else {
            if (screenStateListener != null) {
                screenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 停止screen状态更新
     */
    public void stopScreenStateUpdate() {
        context.unregisterReceiver(screenReceiver);
    }

    /**
     * 启动screen状态广播接收器
     */
    private void startScreenBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(screenReceiver, filter);
    }

    /**
     * screen是否打开状态
     *
     * @param pm
     * @return
     */
    private  boolean isScreenOn(PowerManager pm) {
        boolean screenState;
        try {
            screenState = (Boolean) reflectScreenState.invoke(pm);
        } catch (Exception e) {
            screenState = false;
        }
        return screenState;
    }

    public interface ScreenStateListener {
        public void onScreenOn();

        public void onScreenOff();
    }
}