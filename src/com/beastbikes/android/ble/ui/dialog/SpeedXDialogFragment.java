package com.beastbikes.android.ble.ui.dialog;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.BleManager;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshCyclingDataListener;
import com.beastbikes.android.ble.biz.listener.RefreshOTAPacketListener;
import com.beastbikes.android.ble.otadownload.OTAManage;
import com.beastbikes.android.ble.ui.painter.PowerView;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.business.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by icedan on 16/8/22.
 */
public class SpeedXDialogFragment extends DialogFragment implements View.OnClickListener,
        RefreshCyclingDataListener, RefreshOTAPacketListener {

    public static final String PREF_BLE_CYCLING_SYNC_ACTIVITY_ID = "ble.cycling.sync.activity.id";
    private final Logger logger = LoggerFactory.getLogger(SpeedXDialogFragment.class);

    private TextView titleTv;
    private TextView cancelTv;
    private PowerView powerView;
    private TextView valueTv;
    private TextView messageTv;

    public static final String EXTRA_SYNC_TYPE = "sync_type";
    public static final String EXTRA_CENTRAL_ID = "central_id";

    public class SyncType {
        public static final int SYNC_CYCLING = 0x0;// 同步数据
        public static final int SYNC_A_GPS = 0x1;// 同步A_GPS
        public static final int SYNC_OTA_DATA = 0x2;// OTA升级
    }

    private BleManager bleManager;
    private Invocation manager;
    private SharedPreferences userSp;
    private List<LocalActivity> unSyncList;
    private int unSyncCount = 0;
    private int syncingIndex = 0;
    private int syncType;
    private CentralSession mSession;

    public void setInvocation(Invocation invocation) {
        this.manager = invocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bleManager = new BleManager(getActivity());

        Bundle bundle = getArguments();
        this.syncType = bundle.getInt(EXTRA_SYNC_TYPE, SyncType.SYNC_CYCLING);
        String centralId = bundle.getString(EXTRA_CENTRAL_ID);
        mSession = CentralSessionHandler.getInstance().sessionMatch(centralId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sync_data_dialog_view, null);
        int width = getResources().getDisplayMetrics().widthPixels -
                DimensionUtils.dip2px(getActivity(), 80);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = DimensionUtils.dip2px(getActivity(), 15);
        lp.setMargins(0, margin, 0, margin);
        this.titleTv = (TextView) view.findViewById(R.id.sync_data_dialog_title);
        titleTv.setLayoutParams(lp);
        this.cancelTv = (TextView) view.findViewById(R.id.sync_data_cancel_sync);
        cancelTv.setOnClickListener(this);
        this.powerView = (PowerView) view.findViewById(R.id.sync_data_progress_view);
        this.powerView.setValue(0);
        this.valueTv = (TextView) view.findViewById(R.id.sync_data_progress_value);
        this.valueTv.setText("0%");
        this.messageTv = (TextView) view.findViewById(R.id.sync_data_progress_message);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.mSession != null) {
            this.mSession.getProperty().setCancelUpdate(false);
        }
        switch (syncType) {
            case SyncType.SYNC_CYCLING:
                if (this.mSession != null) {
                    this.mSession.getProperty().setCancelSync(false);
                }
                this.titleTv.setText(R.string.label_syncing_activity);
                if (manager != null) {
                    this.manager.setRefreshCyclingDataListener(this);
                }
                this.cancelTv.setText(R.string.sync_data_cancel_sync);
                String message = getString(R.string.sync_data_message) + "(" + String.valueOf(syncingIndex + 1) + "/"
                        + String.valueOf(unSyncCount) + ")";
                this.messageTv.setText(message);
                this.syncDataStart();
                break;
            case SyncType.SYNC_A_GPS:
                if (this.mSession != null) {
                    this.mSession.getProperty().setCancelUpdate(false);
                }
                this.titleTv.setText(R.string.label_speedx_agps_update_msg_1);
                this.messageTv.setText(R.string.label_agps_update_desc);
                this.cancelTv.setText(R.string.label_cancel_update);
                if (manager != null) {
                    this.manager.setRefreshOTAPacketListener(this);
                }
                break;
            case SyncType.SYNC_OTA_DATA:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_data_cancel_sync:
                switch (syncType) {
                    case SyncType.SYNC_CYCLING:// 取消同步数据
                        if (this.mSession != null) {
                            this.mSession.getProperty().setCancelUpdate(true);
                            this.mSession.getProperty().setCancelSync(true);
                        }
                        final OnUpdateDataListener cancelListener = this.manager.getUpdateListener();
                        if (null != cancelListener) {
                            cancelListener.onUpdateCanceled(SyncType.SYNC_CYCLING);
                        }

                        if (this.unSyncList != null && this.unSyncList.size() > 0 && syncingIndex < this.unSyncList.size()) {
                            LocalActivity activity = this.unSyncList.get(syncingIndex);
                            if (null == activity) {
                                dismissAllowingStateLoss();
                                break;
                            }

                            this.deleteLocalActivitySample(activity.getId());
                        } else {
                            dismissAllowingStateLoss();
                        }
                        break;
                    case SyncType.SYNC_A_GPS:// 取消同步A_GPS文件
                        SpeedxAnalytics.onEvent(getActivity(), "", "click_my_page_my_device_stop_transfer_agps");
                        this.showCancelUpdateDialog();
                        break;
                }
                break;
        }
    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncing(final int index, final int count) {
        if (null == getActivity()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String p = String.format("%.0f", ((double) index + 1) / (double) count * 100);
                final int progress = Integer.valueOf(p);
                powerView.setValue(progress);
                valueTv.setText(String.format("%d%%", progress));
                String value = getString(R.string.sync_data_message) + "(" + String.valueOf(syncingIndex + 1) + "/"
                        + String.valueOf(unSyncCount) + ")";
                messageTv.setText(value);
            }
        });
    }

    @Override
    public void onSyncEnd() {
        this.syncingIndex += 1;
        this.syncAgain = false;

        if (null != getActivity()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    syncData(0);
                    powerView.setValue(0, false);
                    valueTv.setText("0%");
                }
            });
        }
    }

    private boolean syncAgain;

    @Override
    public void onSyncError(final int errorCode) {
        if (errorCode == 0x10 || errorCode == 0x15) {
            logger.error("Sync errorCode = " + errorCode + ", (错误描述: errorCode 16 = MCU_ERR_CODE_SYNC_RIDE_BUSY, " +
                    "errorCode 21 = MCU_ERR_CODE_SYNC_TIMEOUT");
            return;
        }

        if (null != getActivity()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (errorCode == 0x11 && !syncAgain) {
                        syncAgain = true;
                        syncData(errorCode);
                    } else {
                        syncAgain = false;
                        syncingIndex += 1;
                        syncData(0);
                    }
                    powerView.setValue(0, false);
                    valueTv.setText("0%");
                }
            });
        }
    }


    @Override
    public void onRefreshCount(int index) {
        if (null == getActivity() || mSession == null) {
            return;
        }

        final double currentSize = (index + 1) * 190;
        final double totalSize = this.mSession.getProperty().getFileLength();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double pro = 0;
                if (totalSize > 0) {
                    pro = currentSize / totalSize * 100;
                }
                powerView.setValue((float) pro, true);
                valueTv.setText(String.format("%.0f%%", pro));
            }
        });
    }

    @Override
    public void onOTADataEnd(int type) {
        if (type == OTAManage.OTA_A_GPS_IMG) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final OnUpdateDataListener updateDataListener = manager.getUpdateListener();
                    if (null != updateDataListener) {
                        updateDataListener.onUpdateSuccess(SyncType.SYNC_A_GPS);
                    }
                    SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getPackageName(), 0);
                    sp.edit().remove(Constants.BLE.PREF_BLE_GPS_KEY).apply();
                    dismissAllowingStateLoss();
                }
            });
        }
    }

    @Override
    public void onParserDataError() {
        if (null == getActivity()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
            }
        });
    }

    /**
     * 开始同步数据
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void syncDataStart() {
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            dismissAllowingStateLoss();
            return;
        }

        if (null == mSession) {
            dismissAllowingStateLoss();
            return;
        }

        this.userSp = getActivity().getSharedPreferences(user.getObjectId(), 0);
        this.unSyncList = bleManager.getUnSyncLocalActivitiesByCentralId(user.getObjectId(), mSession.getCentralId());
        if (null == unSyncList || unSyncList.isEmpty()) {
            dismissAllowingStateLoss();
            return;
        }

        this.unSyncCount = unSyncList.size();
        this.syncingIndex = 0;
        String message = getString(R.string.sync_data_message) + "(" + String.valueOf(syncingIndex + 1) + "/"
                + String.valueOf(unSyncCount) + ")";
        this.messageTv.setText(message);
        this.syncData(0);
    }

    /**
     * 同步数据
     */
    private void syncData(int errorCode) {
        if (this.manager == null)
            return;
        if (unSyncCount <= 0 || syncingIndex >= unSyncCount) {
            final OnUpdateDataListener updateLister = this.manager.getUpdateListener();
            if (null != updateLister) {
                updateLister.onUpdateSuccess(SyncType.SYNC_CYCLING);
            }
            dismissAllowingStateLoss();
            return;
        }

        LocalActivity activity = this.unSyncList.get(syncingIndex);
        if (null == activity) {
            dismissAllowingStateLoss();
            return;
        }

        logger.info("同步数据的记录: " + activity.toString());
        this.manager.writeActivitySyncRequest(activity.getFinishTime(), errorCode);
        if (null != this.userSp) {
            this.userSp.edit().putString(PREF_BLE_CYCLING_SYNC_ACTIVITY_ID, activity.getId()).apply();
        }
    }

    /**
     * 取消GPS文件更新
     */
    private void showCancelUpdateDialog() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setMessage(R.string.msg_cancel_update_agps);
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSession != null) {
                    mSession.getProperty().setCancelUpdate(true);
                }
                final OnUpdateDataListener cancelListener = manager.getUpdateListener();
                if (null != cancelListener) {
                    cancelListener.onUpdateCanceled(SyncType.SYNC_A_GPS);
                }
                SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getPackageName(), 0);
                sp.edit().remove(Constants.BLE.PREF_BLE_GPS_KEY).apply();
                dismissAllowingStateLoss();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 取消同步删除脏数据
     *
     * @param activityId 骑行记录ID
     */
    private void deleteLocalActivitySample(String activityId) {
        if (TextUtils.isEmpty(activityId)) {
            return;
        }

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "", true);
        dialog.show();
        try {
            ActivityManager am = new ActivityManager(getActivity());
            am.deleteLocalActivitySamples(activityId);
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                dismissAllowingStateLoss();
            }
        }, 3000);

    }

}
