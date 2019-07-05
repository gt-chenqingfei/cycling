package com.beastbikes.android.modules.user.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.BleManager;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.listener.OnUpdateDataListener;
import com.beastbikes.android.ble.ui.dialog.SpeedXDialogFragment;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.ui.record.CyclingCompletedActivity;
import com.beastbikes.android.modules.cycling.club.dto.RecordInfo;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.util.ActivityDataUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Alias("骑行记录列表页")
@LayoutResource(R.layout.activity_record_activity)
public class CyclingRecordActivity extends SessionFragmentActivity implements
        SwipeRefreshLoadRecyclerView.RecyclerCallBack, ItemClickListener, View.OnClickListener,
        OnUpdateDataListener {
    private static final Logger logger = LoggerFactory.getLogger("CyclingRecordActivity");

    public static final String EXTRA_REFRESH = "refresh";
    public static final String EXTRA_FORM_CLUB = "from_club";
    public static final String EXTRA_SELECT_ACTIVITY = "record_info";
    public static final String EXTRA_AVATAR_URL = "avatar_url";
    public static final String EXTRA_NICK_NAME = "nick_name";
    public static final String EXTRA_CENTRAL_ID = "central_id";
    public static final String EXTRA_DEVICE_NAME = "device_name";
    public static final String EXTRA_SYNC_ACTIVITY = "sync_activity";

    private SwipeRefreshLoadRecyclerView recyclerView;
    private ActivitiesAdapter activityAdapter;
    private MaterialDialog deleteDialog;

    @IdResource(R.id.activity_record_list_parent)
    private LinearLayout parentView;
    @IdResource(R.id.activity_record_activity_tv_norecord)
    private TextView noRecordTv;
    @IdResource(R.id.activity_record_unsync_rela)
    private RelativeLayout unSyncCyclingView;
    @IdResource(R.id.activity_record_unsync_msg)
    private TextView unSyncMsg;

    private ActivityManager am;
    //    private ActivityAdapter adapter;
    private List<ActivityDTO> activities = new ArrayList<>();

    private RecordInfo recordInfo;
    private String avatarUrl;

    private int pageIndex = 1;
    private boolean refresh;
    private boolean loginUser;
    private boolean isFormClub;
    private String nickName;
    private String centralId = null;
    private String deviceName;
    private LoadingDialog loadingDialog;
    private Invocation manager;

    private boolean syncSuccess;
    private CentralServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        Intent intent = getIntent();
        if (null == intent)
            return;

        this.isFormClub = intent.getBooleanExtra(EXTRA_FORM_CLUB, false);
        this.avatarUrl = intent.getStringExtra(EXTRA_AVATAR_URL);
        this.nickName = intent.getStringExtra(EXTRA_NICK_NAME);
        this.centralId = intent.getStringExtra(EXTRA_CENTRAL_ID);
        this.refresh = intent.getBooleanExtra(EXTRA_REFRESH, true);
        this.deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        this.am = new ActivityManager(this);

        this.activityAdapter = new ActivitiesAdapter(this, this);
        this.recyclerView = new SwipeRefreshLoadRecyclerView(this, parentView, activities,
                SwipeRefreshLoadRecyclerView.HASFOOTER);
        this.recyclerView.setRecyclerCallBack(this);
        this.recyclerView.setAdapter(this.activityAdapter);

        final AVUser current = AVUser.getCurrentUser();
        if (null != current) {
            String userId = getUserId();
            if (!TextUtils.isEmpty(userId) && userId.equals(current.getObjectId())) {
                this.loginUser = true;
            }
        }

        this.pageIndex = 1;
        this.fetchActivityDTO(centralId);

        boolean isConnected = CentralSessionHandler.getInstance().isConnected(centralId);
        if (isConnected) {
            handleSyncRelaState(centralId, deviceName);
        }

        this.bindService();
    }


    private void bindService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                !TextUtils.isEmpty(centralId)) {
            connection = new CentralServiceConnection();
            connection.bindService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connection != null) {
            connection.unBindService();
        }
    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        if (null != intent) {
            intent.putExtra(EXTRA_REFRESH, refresh);
            if (this.syncSuccess) {
                intent.putExtra(EXTRA_SYNC_ACTIVITY, true);
            }
            setResult(RESULT_OK, intent);
        }
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CyclingCompletedActivity.REQ_CYCLING_COMPLETE
                && resultCode == CyclingCompletedActivity.RESULT_UPDATE) {
            refreshCallBack();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_record_unsync_rela:// 同步数据
                SpeedXDialogFragment dialogFragment = new SpeedXDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(SpeedXDialogFragment.EXTRA_SYNC_TYPE, SpeedXDialogFragment.SyncType.SYNC_CYCLING);
                bundle.putString(SpeedXDialogFragment.EXTRA_CENTRAL_ID, centralId);
                dialogFragment.setInvocation(manager);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "SYNC_DATA");
                break;
        }
    }

    @Override
    public void onUpdateCanceled(int type) {

    }

    @Override
    public void onUpdateSuccess(int type) {
        switch (type) {
            case 0:// 同步数据完成
                this.syncSuccess = true;
                this.unSyncCyclingView.setVisibility(View.GONE);
                this.refreshCallBack();
                break;
        }
    }

    @Override
    public void refreshCallBack() {
        this.pageIndex = 1;
        this.refresh = true;
        this.fetchActivityDTO(centralId);
    }

    @Override
    public void loadMoreCallBack() {
        this.fetchActivityDTO(centralId);
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        final ActivityDTO dto = this.activities.get(position);

        if (null == dto)
            return;

        if (isFormClub && loginUser) {
            recordInfo = new RecordInfo(dto);
            Intent intent = getIntent();
            intent.putExtra(EXTRA_SELECT_ACTIVITY, recordInfo);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent(this, CyclingCompletedActivity.class);
            intent.putExtra(CyclingCompletedActivity.EXTRA_USER_ID, getUserId());
            intent.putExtra(CyclingCompletedActivity.EXTRA_AVATAR_URL, this.avatarUrl);
            intent.putExtra(CyclingCompletedActivity.EXTRA_NICK_NAME, this.nickName);
            intent.putExtra(CyclingCompletedActivity.EXTRA_CLOUD_ACTIVITY, dto);
            intent.putExtra(CyclingCompletedActivity.EXTRA_SPORT_IDENTIFY, dto.getActivityIdentifier());
            startActivityForResult(intent, CyclingCompletedActivity.REQ_CYCLING_COMPLETE);
        }
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, final int position) {
        if (!loginUser) {
            return;
        }
        final ActivityDTO ad = this.activities.get(position);
        if (null == ad) {
            return;
        }

        this.deleteDialog = new MaterialDialog(this);
        this.deleteDialog.setCanceledOnTouchOutside(true);
        View deleteView = LayoutInflater.from(this).
                inflate(R.layout.activity_record_delete_dialog, null);
        TextView deleteTv = (TextView) deleteView.findViewById(R.id.delete_dialog_delete_item);
        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                String activityId = ad.getActivityIdentifier();
                if (TextUtils.isEmpty(activityId))
                    return;

                logger.info("用户确定删除数据, activityId = " + activityId);
                deleteActivity(activityId, position);
            }
        });
        this.deleteDialog.setContentView(deleteView);

        this.deleteDialog.show();
    }

    /**
     * 查询是否有未同步的中控数据
     *
     * @param centralId  centralId
     * @param deviceName deviceName
     */
    private void handleSyncRelaState(final String centralId, final String deviceName) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<LocalActivity>>() {
            @Override
            protected List<LocalActivity> doInBackground(String... strings) {
                final BleManager manager = new BleManager(CyclingRecordActivity.this);
                return manager.getUnSyncLocalActivitiesByCentralId(getUserId(), centralId);
            }

            @Override
            protected void onPostExecute(List<LocalActivity> localActivities) {
                if (null == localActivities || localActivities.size() < 1) {
                    unSyncCyclingView.setVisibility(View.GONE);
                    return;
                }

                unSyncCyclingView.setOnClickListener(CyclingRecordActivity.this);
                unSyncCyclingView.setVisibility(View.VISIBLE);
                unSyncMsg.setText(String.format(getString(R.string.msg_ble_unsync), deviceName));
            }
        });
    }

    /**
     * 删除个人骑行记录，更新列表
     *
     * @param activityId 骑行记录id
     * @param position   index
     */
    private void deleteActivity(final String activityId, final int position) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(CyclingRecordActivity.this, getString(R.string.club_info_waiting), true);
                }
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return am.deleteActivityByActivityId(params[0]);
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    try {
                        if (activities.size() > 0 && activities.size() > position) {
                            activities.remove(position);
                        }
                        recyclerView.notifyDataSetChanged();
                        am.deleteLocalActivity(activityId);
                        am.deleteLocalActivitySamples(activityId);
                    } catch (BusinessException e) {
                        e.printStackTrace();
                    }
                }

                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

        }, activityId);
    }

    /**
     * 分页查询骑行记录
     */
    private void fetchActivityDTO(final String centralId) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ActivityDTO>>() {

                    @Override
                    protected List<ActivityDTO> doInBackground(String... params) {

                        final List<ActivityDTO> list = new ArrayList<>();
                        try {
                            final List<LocalActivity> offlineActivities = am
                                    .getUnsyncedActivities(getUserId(), centralId);
                            if (offlineActivities != null && offlineActivities.size() > 0) {
                                for (final LocalActivity la : offlineActivities) {
                                    if (la.getTotalDistance() > 0) {
                                        list.add(new ActivityDTO(la));
                                    }
                                }
                            }
                        } catch (BusinessException e) {
                            // Log.e(TAG, "Load offline record error", e);
                        }

                        final String userId = params[0];
                        try {
                            final List<ActivityDTO> lists = am
                                    .getActivitiesByUserId(userId, 20,
                                            pageIndex, refresh, centralId);
                            if (null == lists || lists.isEmpty()) {
                                return list;
                            } else {
                                lists.addAll(0, list);
                            }
                            return lists;
                        } catch (BusinessException e) {
                            e.printStackTrace();
                            return list;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ActivityDTO> result) {
                        if (result == null || result.isEmpty()) {
                            recyclerView.noMoreData(false);
                            recyclerView.finishLoad();

                            NetworkInfo ni = ConnectivityUtils
                                    .getActiveNetwork(CyclingRecordActivity.this);
                            if (ni == null || !ni.isConnected()) {
                                Toasts.show(CyclingRecordActivity.this,
                                        R.string.activity_unnetwork_err);
                            } else {
                                Toasts.show(CyclingRecordActivity.this,
                                        R.string.activity_record_no_more);
                            }

                            if (pageIndex == 1) {
                                if (!TextUtils.isEmpty(deviceName)) {
                                    noRecordTv.setVisibility(View.VISIBLE);

                                    noRecordTv.setText(deviceName + "\r\n" + CyclingRecordActivity.this.getString(
                                            R.string.activity_record_activity_no_record));
                                }
                            }
                            return;
                        }

                        if (result.size() < 20) {
                            recyclerView.noMoreData(false);
                            recyclerView.finishLoad();
                        }

                        refresh = false;
                        recyclerView.finishLoad();

                        if (pageIndex == 1)
                            activities.clear();

                        pageIndex = pageIndex + 1;
                        activities.addAll(result);
                        recyclerView.notifyDataSetChanged();
                    }

                }, getUserId());
    }

    private final class ActivityViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgRoute;
        private TextView lblTitle;
        private TextView lblDistanceValue;
        private TextView lblElapsedTimeValue;
        private ImageView lblOffline;
        private ImageView lblActivityFake;
        private TextView lblDistanceUnit;
        private TextView sourceTv;
        private View view;

        protected ActivityViewHolder(View v) {
            super(v);
            this.view = v;
            this.imgRoute = (ImageView) v.findViewById(R.id.activity_record_activity_route);
            this.lblTitle = (TextView) v.findViewById(R.id.activity_record_activity_title);
            this.lblDistanceValue = (TextView) v.findViewById(R.id.activity_record_activity_distance);
            this.lblElapsedTimeValue = (TextView) v.findViewById(R.id.activity_record_activity_elapsed_time);
            this.lblOffline = (ImageView) v.findViewById(R.id.activity_record_activity_offline);
            this.lblActivityFake = (ImageView) v.findViewById(R.id.activity_record_activity_fake);
            this.lblDistanceUnit = (TextView) v.findViewById(R.id.activity_record_activity_distance_unit);
            this.sourceTv = (TextView) v.findViewById(R.id.activity_record_activity_source);
        }

    }

    private final class ActivitiesAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {

        private Context context;
        private boolean isChineseVersion = true;

        private ItemClickListener itemClickListener;

        public ActivitiesAdapter(Context context, ItemClickListener listener) {
            this.context = context;
            this.itemClickListener = listener;
            if (!LocaleManager.isDisplayKM(context)) {
                isChineseVersion = false;
            }
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder() {
            ActivityViewHolder holder = new ActivityViewHolder(LayoutInflater.from(
                    context).inflate(R.layout.activity_record_list_item, null,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, final int position, boolean isLastItem) {
            if (null == value)
                return;

            ActivityDTO dto = (ActivityDTO) value;
            final ActivityViewHolder vh = (ActivityViewHolder) holder;
            // set activity route image
            final String activityUrl = dto.getActivityUrl();
            if (!TextUtils.isEmpty(activityUrl)) {
                Picasso.with(context)
                        .load(activityUrl)
                        .placeholder(R.drawable.ic_map_loading)
                        .error(R.drawable.ic_map_loading)
                        .fit()
                        .centerCrop()
                        .into(vh.imgRoute);
            } else {
                Picasso.with(context)
                        .load(R.drawable.ic_map_loading)
                        .placeholder(R.drawable.ic_map_loading)
                        .error(R.drawable.ic_map_loading)
                        .fit()
                        .centerCrop()
                        .into(vh.imgRoute);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String title = dto.getTitle();
            if (TextUtils.isEmpty(title) || title.equals("null")) {
                String data = sdf.format(new Date(dto.getStartTime()));
                title = ActivityDataUtil.formatDateTime(
                        CyclingRecordActivity.this, dto.getStartTime());
                vh.lblTitle.setText(data + title);
            } else {
                vh.lblTitle.setText(title);
            }

            if (isChineseVersion) {
                vh.lblDistanceValue.setText(String.format("%.1f",
                        dto.getTotalDistance() / 1000));
            } else {
                vh.lblDistanceUnit.setText(getResources().getString(R.string.activity_param_label_distance) + LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance_mi);
                vh.lblDistanceValue.setText(String.format("%.1f",
                        LocaleManager.kilometreToMile(dto.getTotalDistance() / 1000)));
            }
            sdf = new SimpleDateFormat("HH:mm:ss");
            vh.lblElapsedTimeValue.setText(sdf.format(new Date(dto
                    .getStopTime())));

            if (dto.isFake()) {
                vh.lblActivityFake.setVisibility(View.VISIBLE);
            } else {
                vh.lblActivityFake.setVisibility(View.GONE);
            }

            if (!dto.isSynced()) {
                vh.lblOffline.setVisibility(View.VISIBLE);
            } else {
                vh.lblOffline.setVisibility(View.GONE);
            }

            String centralName = dto.getCentralName();
            if (TextUtils.isEmpty(centralName) || centralName.equals("null")) {
                vh.sourceTv.setVisibility(View.INVISIBLE);
            } else {
                vh.sourceTv.setVisibility(View.VISIBLE);
                vh.sourceTv.setText(centralName);
            }

            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(vh, position);
                }
            });

            vh.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.OnItemLongClick(vh, position);
                    return true;
                }
            });

        }
    }


    class CentralServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            logger.info("onServiceConnected");
            CentralService centralService = ((CentralService.ICentralBinder) binder).getService();
            manager = centralService.getInvocation();
            manager.setUpdateListener(CyclingRecordActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logger.info("onServiceDisconnected");
        }

        private void bindService() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                    !TextUtils.isEmpty(centralId)) {
                final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
                service.setPackage(getPackageName());

                CyclingRecordActivity.this.bindService(service, this, BIND_AUTO_CREATE);
            }
        }

        private void unBindService() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                CyclingRecordActivity.this.unbindService(this);
            }
        }
    }
}
