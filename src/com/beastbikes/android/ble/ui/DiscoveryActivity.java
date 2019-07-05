package com.beastbikes.android.ble.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.listener.IScanResultListener;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.WebActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by icedan on 15/11/30.
 * 扫描蓝牙设备
 */
@Alias("扫描设备页")
@LayoutResource(R.layout.bind_speed_force_activity)
public class DiscoveryActivity extends SessionFragmentActivity implements
        IScanResultListener {

    private static final Logger logger = LoggerFactory.getLogger("DiscoveryActivity");
    public static final String BLE_CONNECTED_ACTION = "com.beastbikes.android.ble.connected.action";
    public static final String BLE_DISCONNECTED_ACTION = "com.beastbikes.android.ble.disconnected.action";
    public static final String BLE_DISCOVERY_ACTION = "com.beastbikes.android.ble.discovery.action";
    public static final String EXTRA_ADD_NEW_DEVICE = "add_new_device";

    private static final int REQUEST_BLUE_ENABLE = 88;
    private static final int MIN_TIME_DELAY = 3 * 1000;

    private CentralService mService;
    private Timer timer;
    private boolean hasDiscovered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }


        CentralSessionHandler.getInstance().setAddNew(true);
        this.checkBleDevice();

        this.timer = new Timer();
        this.timer.schedule(new BleFoundTimeTask(), MIN_TIME_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
        if (null != mService) {
            mService.setOnScanResultListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(connection);

        if (!hasDiscovered) {
            CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
            if (session == null) {
                final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
                service.setPackage(getPackageName());
                service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_STOP_SCAN);
                this.startService(service);
                return;
            }

            CentralSessionHandler.getInstance().setAddNew(false);

            final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
            service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_STOP_SCAN);
            service.setPackage(getPackageName());
            this.startService(service);
//            this.manager.setAutoConnect(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_ready_bind_icon:
                final Uri browserUri = Uri.parse("https://www.speedx.com/app/smart-control-guide.html");
                final Intent browserIntent = new Intent(this, BrowserActivity.class);
                browserIntent.setData(browserUri);
                browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                browserIntent.setPackage(getPackageName());
                browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION, R.anim.activity_in_from_right);
                browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION, R.anim.activity_out_to_right);
                browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION, R.anim.activity_none);
                Bundle bundle = new Bundle();
                bundle.putString("X-User-Id", getUserId());
                browserIntent.putExtra(WebActivity.EXTRA_HTTP_HEADERS, bundle);
                this.startActivity(browserIntent);
                return true;
            case android.R.id.home:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUE_ENABLE) {
            switch (resultCode) {
                case RESULT_OK:
                    checkBleDevice();
                    break;
            }
        }
    }

    /**
     * 检测手机蓝牙状态及开启连接service
     */
    private void checkBleDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null == adapter) {
            return;
        }

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(enableBtIntent, REQUEST_BLUE_ENABLE);
        } else {
            final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
            service.setPackage(getPackageName());
            service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_START_SCAN);
            this.startService(service);
            this.bindService(service, connection, BIND_AUTO_CREATE);
        }
    }

    private void startDiscoverResultActivity() {
        startActivity(new Intent(DiscoveryActivity.this, DiscoveryResultActivity.class));
        DiscoveryActivity.this.finish();
    }

    @Override
    public void onScanResult(List<CentralSession> scanResults, CentralSession session) {
        if (DiscoveryActivity.this.timer == null && !hasDiscovered) {
            hasDiscovered = true;
            startDiscoverResultActivity();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            logger.info("onServiceConnected");
            mService = ((CentralService.ICentralBinder) binder).getService();
            mService.setOnScanResultListener(DiscoveryActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logger.info("onServiceDisconnected");
        }
    };


    /**
     * 进入该页面最短停留3秒
     */
    private class BleFoundTimeTask extends TimerTask {

        @Override
        public void run() {
            DiscoveryActivity.this.timer.cancel();
            if (DiscoveryActivity.this.hasDiscovered) {
                startDiscoverResultActivity();
            }
            DiscoveryActivity.this.timer = null;
        }
    }

}
