package com.beastbikes.android.modules;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.biz.listener.ConnectStateListener;
import com.beastbikes.android.ble.ui.DiscoveryActivity;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.main.MainActivity;
import com.beastbikes.android.modules.cycling.SyncService;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.framework.android.utils.ServicesUtils;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class SessionFragmentActivity extends BaseFragmentActivity implements ConnectStateListener {

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_NOTIFY_COUNT_KEY = "extra_notify_count_key";
    private Logger logger = LoggerFactory.getLogger(SessionFragmentActivity.class);
    private Locale currentLocale;

    protected String getUserId() {
        final Intent intent = getIntent();
        if (null == intent)
            return null;

        final String userId = intent.getStringExtra(EXTRA_USER_ID);
        if (!TextUtils.isEmpty(userId)) {
            return userId;
        }

        final AVUser current = AVUser.getCurrentUser();
        if (null == current)
            return null;

        return current.getObjectId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLocale = getResources().getConfiguration().locale;
        this.registerBleReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent it = getIntent();
        if(it != null){
            String prefKey = it.getStringExtra(EXTRA_NOTIFY_COUNT_KEY);
            if(!TextUtils.isEmpty(prefKey)){
                final AVUser user = AVUser.getCurrentUser();
                if (null == user)
                    return ;
                SharedPreferences sp = getSharedPreferences(user.getObjectId(), 0);
                sp.edit().putInt(prefKey,0).apply();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unRegisterBleReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("TAG", "onConfigurationChanged is called!");
        if (currentLocale != null && !newConfig.locale.equals(currentLocale)) {
            currentLocale = newConfig.locale;
            restartActivity();

            RongCloudManager.getInstance().unsubAndSubPublishService();
        }
    }

    @Override
    public void bleConnected() {

    }

    @Override
    public void bleDisconnected() {
        Toasts.show(this, getString(R.string.toast_bluetooth_disconnect));
    }

    private void restartActivity() {
        sendBroadcast(new Intent(HomeActivity.ACTION_FINISH_HOME_ACTIVITY));
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (!ServicesUtils.isServiceRunning(this, SyncService.class.getName())) {
            try {
                stopService(new Intent(this, SyncService.class));
            } catch (Exception e) {

            }
        }
    }

    /**
     * 注册广播监听
     */
    private void registerBleReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DiscoveryActivity.BLE_CONNECTED_ACTION);
        filter.addAction(DiscoveryActivity.BLE_DISCONNECTED_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(bleConnectStateReceiver, filter);
    }

    /**
     * 注销广播监听
     */
    private void unRegisterBleReceiver() {
        unregisterReceiver(bleConnectStateReceiver);
    }


    /**
     * 设备连接状态监听
     */
    private BroadcastReceiver bleConnectStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(DiscoveryActivity.BLE_CONNECTED_ACTION)) {
                logger.trace("BroadcastReceiver 已连接");
                bleConnected();
            }

            if (action.equals(DiscoveryActivity.BLE_DISCONNECTED_ACTION)) {
                logger.trace("BroadcastReceiver 已断开");
                bleDisconnected();
            }
        }
    };
}
