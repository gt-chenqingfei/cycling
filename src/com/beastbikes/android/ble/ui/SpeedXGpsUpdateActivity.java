package com.beastbikes.android.ble.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshOTAPacketListener;
import com.beastbikes.android.ble.otadownload.OTAManage;
import com.beastbikes.android.ble.ui.dialog.SpeedXDialogFragment;
import com.beastbikes.android.ble.ui.painter.PowerView;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@LayoutResource(R.layout.activity_speedx_gps_update)
public class SpeedXGpsUpdateActivity extends SessionFragmentActivity implements RefreshOTAPacketListener,
        View.OnClickListener, OTAManage.DownloadFileListener {
    public static final String EXTRA_CENTRAL_ID = "central_id";

    private Logger logger = LoggerFactory.getLogger(SpeedXGpsUpdateActivity.class);

    @IdResource(R.id.speedx_agps_update_view)
    private PowerView versionView;

    @IdResource(R.id.speedx_agps_update_cancel)
    private Button cancelUpdateBtn;

    private Invocation manager;
    private boolean isUpdate;
    private boolean showErrorDialog;
    private CentralSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        if (getIntent() != null) {
            String centralId = getIntent().getStringExtra(EXTRA_CENTRAL_ID);
            session = CentralSessionHandler.getInstance().sessionMatch(centralId);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.versionView.setChangeColor(false);

        if (this.session != null) {
            this.session.getProperty().setCancelUpdate(false);
        }

        this.cancelUpdateBtn.setOnClickListener(this);
        this.downAGPSFile();

        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.setPackage(getPackageName());
        this.startService(service);
        this.bindService(service, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != connection) {
            this.unbindService(connection);
        }
    }

    @Override
    public void finish() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onRefreshCount(int index) {
        this.isUpdate = true;
        final double currentSize = (index + 1) * 190;
        final double totalSize = this.session.getProperty().getFileLength();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double pro = currentSize / totalSize * 100;
                versionView.setValue((float) pro, true);
            }
        });
    }

    @Override
    public void onOTADataEnd(final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == OTAManage.OTA_A_GPS_IMG && getWindow() != null) {
                    isUpdate = false;
                    Toasts.show(SpeedXGpsUpdateActivity.this, R.string.toast_ble_update_agps_success);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }

                if (type == 0) {
                    showUpdateGPSErrorDialog(true);
                }
            }
        });
    }

    @Override
    public void onParserDataError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUpdateGPSErrorDialog(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && this.isUpdate) {
            this.showCancelUpdateDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speedx_agps_update_cancel:// 取消升级
                if (this.isUpdate) {
                    this.showCancelUpdateDialog();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP)
            return true;

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (this.isUpdate) {
                this.showCancelUpdateDialog();
                return true;
            } else {
                finish();
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDownloadFileSuccess(int type, String versionName, String filePath) {
        if (type == OTAManage.OTA_A_GPS_IMG) {
            logger.info("Download A_GPS img is success");
            if (this.manager != null) {
                this.manager.writeOTAStartRequest(type, versionName, filePath);
            }
        }
    }

    @Override
    public void onDownloadFileError(int type) {
        if (type == OTAManage.OTA_A_GPS_IMG) {
            logger.error("Download A_GPS img is error");
            finish();
        }
    }

    private void showCancelUpdateDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.msg_cancel_update_agps);
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        session.getProperty().setCancelUpdate(true);
                        dialog.dismiss();
                        if (manager != null) {
                            OnUpdateDataListener listener = manager.getUpdateListener();
                            if (null != listener) {
                                listener.onUpdateCanceled(SpeedXDialogFragment.SyncType.SYNC_A_GPS);
                            }

                        }
                        finish();
                    }
                }

        );
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }

        ).

                show();
    }

    /**
     * 下载星历文件
     */

    private void downAGPSFile() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }

        OTAManage otaManage = new OTAManage(this, this);
        String agpsUrl = "http://alp.u-blox.com/current_14d.alp";
        File file = otaManage.getFile(agpsUrl);
        if (null != file && file.exists()) {
            FileUtil.deleteFile(file.getAbsolutePath());
        }

        otaManage.downLoadFile(OTAManage.OTA_A_GPS_IMG, "1.0.1", agpsUrl, 0);
    }

    /**
     * 更新apgs信息失败
     */
    private void showUpdateGPSErrorDialog(final boolean isDisconnect) {
        if (this.showErrorDialog || isFinishing()) {
            return;
        }

        this.showErrorDialog = true;
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.msg_ble_update_gps_error);
        dialog.setPositiveButton(R.string.label_i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (manager != null) {
                    OnUpdateDataListener listener = manager.getUpdateListener();
                    if (null != listener) {
                        listener.onUpdateCanceled(SpeedXDialogFragment.SyncType.SYNC_A_GPS);
                    }
                }
                if (isDisconnect) {
                    setResult(SpeedForceActivity.RESULT_DEVICE_DISCONNECT_CODE, getIntent());
                }
                finish();
            }
        }).show();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            logger.info("onServiceConnected");
            CentralService mService = ((CentralService.ICentralBinder) binder).getService();
            manager = mService.getInvocation();
            manager.setRefreshOTAPacketListener(SpeedXGpsUpdateActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logger.info("onServiceDisconnected");
        }
    };
}
