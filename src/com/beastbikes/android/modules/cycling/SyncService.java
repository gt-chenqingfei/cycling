package com.beastbikes.android.modules.cycling;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.text.TextUtils;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.business.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service {

    private static final Logger logger = LoggerFactory
            .getLogger(SyncService.class);

    /**
     * The sample data buffer size
     */
    static final int SAMPLING_BUFFER_SIZE = 500;

    /**
     * 1 sample / 5 seconds
     */
    private static final long SAMPLING_RATE = 10L;

    /**
     * Save location in each 500 milliseconds
     */
    private static final long SAMPLING_INTERVAL = 500L;

    private final Timer timer = new Timer("SampleSynchronizer", true);

    private final UploadingTask syncTask = new UploadingTask();

    /**
     * Connectivity state receiver
     */
    private final BroadcastReceiver connReceiver = new SyncReceiver();

    private ActivityManager am;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logger.info("SyncService onCreate");
        this.am = new ActivityManager(this);
        this.timer.scheduleAtFixedRate(this.syncTask, 0, SAMPLING_RATE
                * SAMPLING_INTERVAL * SAMPLING_BUFFER_SIZE);

        // register receiver to handle network state change
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(this.connReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId > 0) {
            new Thread(this.syncTask).start();
        }
        logger.info("SyncService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.timer.cancel();
        this.unregisterReceiver(this.connReceiver);
        super.onDestroy();

        // restart self
        this.startService(new Intent(this, getClass()));
    }

    private final class UploadingTask extends TimerTask {

        private static final String TAG = "Synchronizer";

        @Override
        public void run() {
            final AVUser user = AVUser.getCurrentUser();
            if (null == user) {
                logger.info(TAG, "No authenticated user found");
                return;
            }

            final String userId = user.getObjectId();
            if (TextUtils.isEmpty(userId)) {
                logger.info(TAG, "No authenticated user found");
                return;
            }

            synchronized (timer) {
                try {
                    final List<LocalActivity> activities = am.getUnsyncedActivities(userId, "");
                    if (null == activities || activities.isEmpty()) {
                        logger.info("Synchronizer : No unsynced data");
                        return;
                    }

                    for (final LocalActivity la : activities) {
                        // DO NOT SYNC IF DISTANCE <= 0
                        if (la.getTotalDistance() <= 10)
                            continue;

                        try {
                            am.saveSamples(la);
                            logger.info("Synchronizer userId:"+userId+" Sync samples of activity " + la.getId()
                                    + " success");
                        } catch (BusinessException e) {
                            logger.error(
                                    "Synchronizer : Sync samples of activity " + la.getId() + " error", e);
                        }
                    }
                } catch (BusinessException e) {
                    logger.error(TAG, "Query local activity error", e);
                }
            }
        }
    }

    private final class SyncReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            switch (ConnectivityUtils.getActiveNetworkType(context)) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_ETHERNET:
                    new Thread(syncTask).start();
                    break;
            }
        }

    }

}
