package com.beastbikes.android.ble.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.biz.BleManager;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.listener.IScanResultListener;
import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.ble.ui.dialog.BleConnectTipDialog;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icedan on 15/12/3.
 */

@TargetApi(19)
@Alias("扫描结束返回结果页")
@MenuResource(R.menu.menu_sure)
@LayoutResource(R.layout.discovery_result_activity)
public class DiscoveryResultActivity extends SessionFragmentActivity implements
        AdapterView.OnItemClickListener, IScanResultListener, ServiceConnection {
    private static final Logger logger = LoggerFactory.getLogger("DiscoveryResultActivity");

    @IdResource(R.id.toolbar)
    private Toolbar mToolbar;

    @IdResource(R.id.speed_force_manager_list)
    private ListView devicesLv;

    private BleManager bleManager;
    private SessionAdapter adapter;
    //    private CentralInvocation manager;
    private CentralSession currentSession = null;
    private BleBroadcastReceiver receiver = new BleBroadcastReceiver();
    private LoadingDialog loadingDialog;
    private BleConnectTipDialog bleConnectTipDialog;
    private String newlyConnectId;
    private CentralService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);

        setSupportActionBar(mToolbar);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        CentralSessionHandler.getInstance().setAddNew(true);
        this.devicesLv.setOnItemClickListener(this);
        this.bleManager = new BleManager(this);
        this.adapter = new SessionAdapter(CentralSessionHandler.getInstance().getScanResult());
        this.devicesLv.setAdapter(this.adapter);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(DiscoveryActivity.BLE_CONNECTED_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        this.registerReceiver(this.receiver, filter);
        fetchJustConnectDeviceList();

        final Intent intent = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        intent.setPackage(getPackageName());
        this.bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.receiver);

        CentralSessionHandler.getInstance().setAddNew(false);
        this.unbindService(this);
        if (currentSession != null) {
            currentSession.setAutoAttach(true);
        }
        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.setPackage(getPackageName());
        service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_STOP_SCAN);
        this.startService(service);
        CentralSessionHandler.getInstance().cleanScanResult();
        currentSession = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sure) {
            connectCheck();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentSession = (CentralSession) adapter.getItem(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
        if (mService != null) {
            mService.setOnScanResultListener(null);
        }
    }

    private void connectCheck() {
        if (currentSession != null) {
            if (currentSession.isAvailable()) {
                connectDevice();
            } else {
                if (bleConnectTipDialog == null) {
                    bleConnectTipDialog = new BleConnectTipDialog(this, CentralSession.isWholeBike(currentSession.getHdType()));
                }
                bleConnectTipDialog.show();
            }
        }
    }


    private void fetchJustConnectDeviceList() {
        getAsyncTaskQueue().add(new AsyncTask<String, Void, List<BleDevice>>() {

            @Override
            protected List<BleDevice> doInBackground(String... strings) {
                return bleManager.getBleDevices();
            }

            @Override
            protected void onPostExecute(List<BleDevice> bleDevices) {
                super.onPostExecute(bleDevices);
                if (bleDevices != null && bleDevices.size() > 0) {
                    newlyConnectId = bleDevices.get(0).getMacAddress();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void connectDevice() {
        if (bleConnectTipDialog != null && bleConnectTipDialog.isShowing()) {
            bleConnectTipDialog.dismiss();
        }
        if (currentSession != null && currentSession.getState() == CentralSession.SESSION_STATE_NONE) {
            logger.error("connectDevice centralId=[" + currentSession.getCentralId() + "]");
            currentSession.setState(CentralSession.SESSION_STATE_NONE);
            final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
            service.setPackage(getPackageName());
            service.putExtra(CentralService.EXTRA_CENTRAL_ID, currentSession.getCentralId());
            service.putExtra(CentralService.EXTRA_CMD, CentralService.CMD_CENTRAL_CONNECT);
            this.startService(service);
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(this, "", true);
            }
            loadingDialog.show(30 * 1000, getString(R.string.dialog_ble_connect_fail));
        }
    }

    @Override
    public void onScanResult(final List<CentralSession> scanResults, final CentralSession session) {
        String centralId = "";
        if (currentSession != null) {
            centralId = currentSession.getCentralId();
        }
        logger.error("scanResults currentCentralId =[" + centralId + "] session = [" + session + "]");


        if (session != null && currentSession != null) {
            if (session.getCentralId().equals(currentSession.getCentralId())
                    && session.isAvailable()) {
                    connectDevice();
            }
        }

        if (scanResults == null)
            return;
        DiscoveryResultActivity.this.adapter.list = scanResults;
        DiscoveryResultActivity.this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        logger.info("onServiceConnected");
        mService = ((CentralService.ICentralBinder) binder).getService();
        mService.setOnScanResultListener(DiscoveryResultActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        logger.info("onServiceDisconnected");
    }

    private final class SessionAdapter extends BaseAdapter {

        private List<CentralSession> list = new ArrayList<>();

        public SessionAdapter(List<CentralSession> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DeviceViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.speed_force_device_item,
                        null);
                viewHolder = new DeviceViewHolder(convertView);
            } else {
                viewHolder = (DeviceViewHolder) convertView.getTag();
            }

            viewHolder.bind(this.list.get(position));
            return convertView;
        }
    }

    private final class DeviceViewHolder extends ViewHolder<CentralSession> {

        @IdResource(R.id.speed_force_device_item_icon)
        private ImageView iconIv;
        @IdResource(R.id.speed_force_device_item_just_connect)
        private TextView tvJustConnect;
        @IdResource(R.id.speed_force_device_item_name)
        private TextView deviceNameTv;
        @IdResource(R.id.speed_force_device_item_checked)
        private ImageView statusIv;

        public DeviceViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(CentralSession session) {
            if (null == session) {
                return;
            }

            String name = session.getName();
            if (!TextUtils.isEmpty(name)) {
                name = name.toUpperCase();
            }
            this.deviceNameTv.setText(name);
            if (currentSession != null && currentSession.getCentralId().equals(session.getCentralId())) {
                this.statusIv.setVisibility(View.VISIBLE);
            } else {
                this.statusIv.setVisibility(View.GONE);
            }

            if (CentralSession.isWholeBike(session.getHdType())) {
                this.iconIv.setImageResource(R.drawable.ic_mustang_icon);
            } else {
                this.iconIv.setImageResource(R.drawable.ic_speed_force);
            }

            if (TextUtils.equals(session.getCentralId(), newlyConnectId)) {
                tvJustConnect.setVisibility(View.VISIBLE);
            } else {
                tvJustConnect.setVisibility(View.GONE);
            }
        }
    }

    private class BleBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String action = intent.getAction();
            if (action.equals(DiscoveryActivity.BLE_CONNECTED_ACTION)) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                DiscoveryResultActivity.this.finish();
                startActivity(new Intent(DiscoveryResultActivity.this, SpeedForceActivity.class));
            }
        }
    }


}
