package com.beastbikes.android.modules.cycling.activity.biz;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.main.MainActivity;
import com.beastbikes.android.modules.cycling.SyncService;
import com.beastbikes.android.modules.cycling.activity.dao.LocalActivitySampleDao;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.activity.ui.record.CyclingCompletedActivity;
import com.beastbikes.android.modules.cycling.activity.util.ActivityType;
import com.beastbikes.android.modules.cycling.activity.util.CalorieCalculator;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.persistence.BeastPersistenceManager;
import com.beastbikes.android.utils.NotificationUtil;
import com.beastbikes.android.utils.ScreenObserver;
import com.beastbikes.android.utils.ScreenObserver.ScreenStateListener;
import com.beastbikes.framework.android.utils.PackageUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.persistence.DataAccessObject;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ActivityService extends Service implements Constants, LocationListener,
        TextToSpeech.OnInitListener, ScreenStateListener {
    private static final String TAG_ACTIVITY_START = "\n" +
            "######## ######## ######## ######## ########--------------------------------######## ######## ######## ######## ######## \n" +
            "######## ######## ######## ######## ########\t\t\t Start Activity \t\t      ######## ######## ######## ######## ######## \n" +
            "######## ######## ######## ######## ########--------------------------------######## ######## ######## ######## ########";

    public static final String ACTION_ACTIVITY_STATE = "com.beastbikes.intent.action.ACTIVITY_STATE";

    public static final String ACTION_ACTIVITY_MANAGER = "com.beastbikes.intent.action.ACTIVITY_MANAGER";

    /**
     * @see #EXTRA_ACTIVITY
     */
    public static final String ACTION_ACTIVITY_START = "com.beastbikes.intent.action.ACTIVITY_START";

    /**
     * @see #EXTRA_ACTIVITY
     */
    public static final String ACTION_ACTIVITY_PAUSE = "com.beastbikes.intent.action.ACTIVITY_PAUSE";

    /**
     * @see #EXTRA_ACTIVITY
     */
    public static final String ACTION_ACTIVITY_AUTO_PAUSE = "com.beastbikes.intent.action.ACTIVITY_AUTO_PAUSE";

    /**
     * @see #EXTRA_ACTIVITY
     */
    public static final String ACTION_ACTIVITY_PAUSE_OR_RESUME = "com.beastbikes.intent.action.ACTIVITY_PAUSE_OR_RESUME";

    /**
     * @see #EXTRA_ACTIVITY
     */
    public static final String ACTION_ACTIVITY_RESUME = "com.beastbikes.intent.action.ACTIVITY_RESUME";

    /**
     * @see #EXTRA_ACTIVITY
     */
    public static final String ACTION_ACTIVITY_COMPLETE = "com.beastbikes.intent.action.ACTIVITY_COMPLETE";


    public static final String EXTRA_ACTION = "action";

    /**
     * Intent extra
     */
    public static final String EXTRA_ACTIVITY = "activity";

    /**
     * The sample data buffer size
     */
    static final int SAMPLING_BUFFER_SIZE = 500;

    /**
     * The minimum update time for GPS location provider in milliseconds
     */
    static final int LOCATING_INTERVAL = 1000;

    /**
     * The threshold duration for auto pause
     */
    static final long AUTO_PAUSE_THRESHOLD = 5000L;

    /**
     * The minimum update distance for GPS location provider
     */
    static final int MIN_LOCATING_DISTANCE = 1;

    /**
     * The minimum speed to resume activity from
     * {@link ActivityState#STATE_AUTO_PAUSED} to
     * {@link ActivityState#STATE_STARTED}
     * 1.8km/h
     */
    static final float MIN_AUTO_RESUME_SPEED = 1.8f;
    /**
     * The Maximum effective accuracy
     */
    static final float MAX_NETWORK_LOCATION_ACC = 80f;

    /**
     * The Maximum effective accuracy
     */
    static final float MAX_LOCATION_ACC = 80f;

    /**
     * Speed maximum effective accuracy
     */
    static final float MAX_SPEED_ACC = 50f;

    /**
     * Speed max
     */
    public static final int MAX_VELOCITY = 100;

    /**
     * 1 sample / 5 seconds
     */
    public static final long SAMPLING_RATE = 5L;

    /**
     * Save location in each 500 milliseconds
     */
    private static final long SAMPLING_INTERVAL = 1000L;

//    private static final float MAX_DIFF_DISTANCE = 2500f;

    private static final Logger logger = LoggerFactory.getLogger("ActivityService");

    /**
     * Sample counter
     */
    private final AtomicInteger ordinal = new AtomicInteger(0);

    /**
     * Main looper handler
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private SamplingTask samplingTask;
    private final Timer timer = new Timer("SamplingTimer", true);
    private final Timer ScreenObserverTimer = new Timer("ScreenObserverTimer", true);


    private long lastStationaryTime;
    private ActivityManager am;
    private LocalActivity localActivity;
    private Location lastGpsLocation;
    private DataAccessObject<LocalUser> luDao;
    private LocalActivitySampleDao lasDao;
    private LocationManager lm;
    private PowerManager pm;
    private WakeLock pwl;
    private CoordinateConverter converter;
    private NotificationManager notificationManager;
    private ScreenObserver screenObserver;
    private boolean calling;

    // 极速的打点
    private LocalActivitySample maxSpeedActivitySample;

    private SharedPreferences userSp;

    private ICyclingServiceListener mICyclingBinderListener;

    public ActivityService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ICyclingBinder();
    }

    @Override
    public void onCreate() {
        logger.info("Creating service " + getClass().getName());

        super.onCreate();
        final BeastBikes app = (BeastBikes) getApplication();
        final BeastPersistenceManager bpm = app.getPersistenceManager();

        this.lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.pm = (PowerManager) getSystemService(POWER_SERVICE);
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        SpeedXPhoneStateListener phoneStateListener = new SpeedXPhoneStateListener();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        this.screenObserver = new ScreenObserver(this);
        this.screenObserver.requestScreenStateUpdate(this);

        this.converter = new CoordinateConverter();
        this.converter.from(CoordType.GPS);

        this.am = app.getActivityManager();
        this.localActivity = this.am.getCurrentActivity();
        this.lasDao = new LocalActivitySampleDao(bpm);
        this.luDao = bpm.getDataAccessObject(LocalUser.class);
        this.lastStationaryTime = Long.MAX_VALUE;

        if (AVUser.getCurrentUser() != null) {
            userSp = getSharedPreferences(AVUser.getCurrentUser().getObjectId(), 0);
        }

        this.samplingTask = new SamplingTask();
        this.timer.scheduleAtFixedRate(this.samplingTask, 0, SAMPLING_INTERVAL);
        // restore from crash
        if (localActivity != null) {
            switch (localActivity.getState()) {
                case ActivityState.STATE_STARTED:
                case ActivityState.STATE_AUTO_PAUSED:
                    logger.info("Restore activity " + localActivity.getId());
                    startSampling();
                    // show notification
                    this.showNotification(localActivity.getState());
                    break;
                case ActivityState.STATE_COMPLETE:
                case ActivityState.STATE_NONE:
                    try {
                        this.completeActivity();
                        this.stopSelf();
                    } catch (BusinessException e) {
                        logger.error(e.getMessage(), e);
                    }
                    break;
                case ActivityState.STATE_PAUSED:
                    logger.info("Activity " + localActivity.getId() + " has been paused");
                    break;
            }

        }

        logger.info("Service " + getClass().getName() + " created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent)
            return super.onStartCommand(intent, flags, startId);

        final String cmd = intent.getStringExtra(EXTRA_ACTION);
        if (TextUtils.isEmpty(cmd)) {
            return super.onStartCommand(intent, flags, startId);
        }

        logger.info("Start command " + cmd + "#" + startId);

        try {
            if (ACTION_ACTIVITY_START.equals(cmd)) {
                this.startActivity();
            } else if (ACTION_ACTIVITY_PAUSE.equals(cmd)) {
                this.pauseActivity(false);
            } else if (ACTION_ACTIVITY_RESUME.equals(cmd)) {
                this.resumeActivity(false);
            } else if (ACTION_ACTIVITY_COMPLETE.equals(cmd)) {
                this.completeActivity();
                this.stopSelf();
            } else if (ACTION_ACTIVITY_PAUSE_OR_RESUME.equals(cmd)) {
                this.pauseOrResumeActivity();
            } else if (null == this.localActivity) {
                //如果service启动后没有收到命令,且没有正在骑行的记录,则停止Service
                this.stopSelf();
            }
        } catch (BusinessException e) {
            logger.error("Operation error", e);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (localActivity != null) {
            this.stopSampling();
        }
        this.timer.cancel();
        this.ScreenObserverTimer.cancel();
        this.screenObserver.stopScreenStateUpdate();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        logger.info("onLocationChanged " + location.toString());
        if (location.getLongitude() == 0 || location.getLatitude() == 0)
            return;
        this.lastGpsLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                logger.info("Location provider " + provider + " is available");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                logger.info("Location provider " + provider + " is out of service");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                logger.info("Location provider " + provider
                        + " is temporatily unavailable");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        logger.info("Location provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        logger.info("Location provider " + provider + " is disabled");
    }

    @Override
    public void onInit(int status) {
        switch (status) {
            case TextToSpeech.SUCCESS:
                logger.info("Initialize TTS engine success");
                break;
            case TextToSpeech.ERROR:
                logger.error("Initialize TTS engine error");
                break;
            default:
                logger.warn("Invalid status of TTS engine");
                break;
        }
    }

    private synchronized void startSampling() {
        logger.info("Start sampling");
        if (null == this.pwl) {
            this.pwl = this.pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "ActivityService");
            this.pwl.acquire();
        }

        // register location listener
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    logger.warn("has no location permission");
                    return;
                }
                this.lm.sendExtraCommand("gps", "force_xtra_injection", null);
                this.lm.sendExtraCommand("gps", "force_time_injection", null);
                logger.warn("gps requestLocationUpdates");
//                this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3, this);
            } else {
                logger.warn("The device not support GPS_PROVIDER");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private synchronized void stopSampling() {
        logger.info("Stop sampling");

        if (null != this.pwl && this.pwl.isHeld()) {
            this.pwl.release();
            this.pwl = null;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            logger.warn("has no location permission");
            return;
        }
        this.lm.removeUpdates(ActivityService.this);
        this.lastGpsLocation = null;
    }

    private synchronized void restartSampling() {
        logger.info("Restart sampling");
        this.stopSampling();
        this.startSampling();
    }

    private void playVoiceFeedback(String action) {
        if (!LocaleManager.isChineseLanunage()) {
            return;
        }

        final BeastBikes app = (BeastBikes) this.getApplication();

        if (ACTION_ACTIVITY_START.equals(action)) {
            final String s = getString(R.string.voice_feedback_activity_started);

            this.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toasts.show(app, s);
                }
            });
        } else if (ACTION_ACTIVITY_PAUSE.equals(action)) {
            final String s = getString(R.string.voice_feedback_activity_paused);

            this.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toasts.show(app, s);
                }
            });
        } else if (ACTION_ACTIVITY_AUTO_PAUSE.equals(action)) {
            final String s = getString(R.string.voice_feedback_activity_auto_paused);

            this.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toasts.show(app, s);
                }
            });
        } else if (ACTION_ACTIVITY_RESUME.equals(action)) {
            final String s = getString(R.string.voice_feedback_activity_resumed);

            this.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toasts.show(app, s);
                }
            });
        } else if (ACTION_ACTIVITY_COMPLETE.equals(action)) {
            final String s = getString(R.string.voice_feedback_activity_completed);

            this.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toasts.show(app, s);
                }
            });
        }
    }

    /**
     * Collect an activity sample, and then persist it into local storage
     *
     * @param user The current user
     * @param la   The current activity
     */
    private LocalActivitySample collectSample(final LocalUser user, final LocalActivity la,
                                              final Location gpsCurr, boolean save) {

        //每次采集点的时候更新LocalActivity的信息
        try {
            this.am.updateAccumulativeData(la);
        } catch (BusinessException e) {
            logger.error("Update total distance error", e);
        }

        final String activityId = la.getId();
        final int n = this.ordinal.get();

        double velocity = 0;
        double altitude = 0;
        double latitude0 = 0;
        double longitude0 = 0;
        double latitude1 = 0;
        double longitude1 = 0;

        if (null != gpsCurr) {
            altitude = gpsCurr.getAltitude();
            latitude1 = gpsCurr.getLatitude();
            longitude1 = gpsCurr.getLongitude();

            velocity = gpsCurr.getSpeed() * 3.6;

            this.converter.coord(new LatLng(latitude1, longitude1));

            final LatLng ll = this.converter.convert();
            longitude0 = ll.longitude;
            latitude0 = ll.latitude;
        }

        if (latitude1 == 0 || longitude1 == 0
                || latitude1 == 4.9E-324
                || longitude1 == 4.9E-324) {
            return null;
        }

        // sampling activity data
        final LocalActivitySample las = new LocalActivitySample();
        try {
            las.setActivityId(activityId);
            las.setOrdinal(n);
            las.setCalorie(la.getTotalCalorie());
            las.setCardiacRate(0);
            las.setDistance(la.getTotalDistance());
            las.setId(UUID.randomUUID().toString());

            // v2.0.0 添加的新字段时间戳（不包含毫秒）
            las.setCurrTime(System.currentTimeMillis() / 1000);

            las.setTime(n * SAMPLING_RATE);
            las.setElapsedTime(SystemClock.elapsedRealtime());
            las.setUserId(user.getId());
            las.setAltitude(String.valueOf(altitude));
            las.setLatitude0(String.valueOf(latitude0));
            las.setLongitude0(String.valueOf(longitude0));
            las.setLatitude1(String.valueOf(latitude1));
            las.setLongitude1(String.valueOf(longitude1));
            las.setVelocity(velocity);
            if (save) {
                this.lasDao.create(las);
            }

            return las;
        } catch (PersistenceException pe) {
            logger.error("Persist activity sample error", pe);
            return null;
        }
    }

    /**
     * collect sample
     *
     * @param sample {@link LocalActivitySample}
     */
    public void collectSample(LocalActivitySample sample) {
        try {
            this.lasDao.create(sample);
        } catch (PersistenceException e) {
            e.printStackTrace();
            logger.error("Persist activity sample error", e);
        }
    }

    private synchronized LocalActivity startActivity() throws BusinessException {
        if (null != localActivity) {
            this.completeActivity();
        }

        final AVUser usr = AVUser.getCurrentUser();
        if (usr == null) {
            logger.error("startActivity error! because no authenticated user found! ");
            return null;
        }

        final String uid = usr.getObjectId();
        final String aid = UUID.randomUUID().toString();
        final ActivityStateMachine asm = new ActivityStateMachine();
        final LocalActivity la = new LocalActivity();
        if (TextUtils.isEmpty(usr.getEmail())) {
            usr.setEmail("");
        }

        la.setId(aid);
        la.setCoordinate("gcj02");
        la.setEmail(usr.getEmail());
        la.setStartTime(System.currentTimeMillis());
        la.setFinishTime(0);
        la.setState(ActivityState.STATE_STARTED);
        la.setType(ActivityType.CYCLING.ordinal());
        la.setUserId(uid);
        la.setUsername(usr.getUsername());
        la.setTotalCalorie(0);
        la.setTotalDistance(0);
        la.setTotalRisenAltitude(0);
        la.setTotalElapsedTime(0);
        la.setTotalUphillDistance(0);
        la.setIsPrivate(0);
        logger.info("Start activity aid=" + aid + " uid=" + uid + "\r\n\r\n" + TAG_ACTIVITY_START);
        if (ActivityManager.setCurrentActivityId(this, aid)) {
            try {
                asm.start();
//                this.laDao.create(la);
                this.am.createLocalActivity(la);
                localActivity = la;
                this.ordinal.set(0);
                this.startSampling();

                // broadcast activity start
                final Intent brc = new Intent();
                brc.setAction(ACTION_ACTIVITY_START);
                brc.putExtra(EXTRA_ACTIVITY, la);
                brc.addCategory(Intent.CATEGORY_DEFAULT);
                this.sendBroadcast(brc);

                // voice feedback
//                this.playVoiceFeedback(brc.getAction());

                // show notification
                this.showNotification(la.getState());

            } catch (Exception e) {
                logger.error("Start activity " + aid + " error", e);

                try {
                    this.am.delete(la);
                    // // FIXME: 16/8/1 remove sp
                } catch (BusinessException pe) {
                    logger.error("Delete local activity error", pe);
                }

                localActivity = null;
                //启动失败,停止Service
                Toasts.showOnUiThreadWithText(this, getString(R.string.start_cycling_failed));
                stopSelf();
                throw new BusinessException(e);
            }
        } else {
            logger.error("set activityId error aid " + aid);
        }

        return la;
    }

    private synchronized LocalActivity pauseActivity(boolean autoPause)
            throws BusinessException {
        if (null == localActivity) {
            localActivity = this.am.getCurrentActivity();
            if (null == localActivity)
                throw new BusinessException("No activity found");
        }

        final int state = localActivity.getState();
        final ActivityStateMachine asm = new ActivityStateMachine(state);

        try {
            asm.pause(autoPause);
            localActivity.setState(asm.getState());
            this.am.updateLocalActivity(localActivity);
            this.lastGpsLocation = null;

            if (!autoPause) {
                this.stopSampling();
                this.samplingTask.tmpGpsLocation = null;
                this.samplingTask.hasAltitudeLocation = null;
            }

            // broadcast activity start
            final Intent brc = new Intent();
            brc.setAction(ACTION_ACTIVITY_PAUSE);
            brc.putExtra(EXTRA_ACTIVITY, localActivity);
            brc.addCategory(Intent.CATEGORY_DEFAULT);
            this.sendBroadcast(brc);

            // voice feedback
//            this.playVoiceFeedback(brc.getAction());

            showNotification(localActivity.getState());
        } catch (Exception e) {
            logger.error("Pause activity " + localActivity.getId() + " error", e);
            throw new BusinessException(e);
        }
        logger.info("Pause activity " + localActivity.getId());
        return localActivity;
    }

    private synchronized LocalActivity resumeActivity(boolean autoPaused)
            throws BusinessException {
        if (null == localActivity) {
            localActivity = this.am.getCurrentActivity();
            if (null == localActivity)
                throw new BusinessException("No activity found");
        }

        final ActivityStateMachine asm = new ActivityStateMachine(localActivity.getState());
        try {
            asm.resume();
            localActivity.setState(asm.getState());
            this.am.updateLocalActivity(localActivity);
            this.lastStationaryTime = Long.MAX_VALUE;
            if (!autoPaused) {
                this.startSampling();
            }

            // broadcast activity start
            final Intent brc = new Intent();
            brc.setAction(ACTION_ACTIVITY_RESUME);
            brc.putExtra(EXTRA_ACTIVITY, localActivity);
            brc.addCategory(Intent.CATEGORY_DEFAULT);
            this.sendBroadcast(brc);

            // voice feedback
//            this.playVoiceFeedback(brc.getAction());
            showNotification(localActivity.getState());
        } catch (Exception e) {
            logger.error("Resume activity " + localActivity.getId() + " error", e);
            throw new BusinessException(e);
        }

        logger.info("Resume activity " + localActivity.getId());
        return localActivity;
    }

    private synchronized LocalActivity pauseOrResumeActivity()
            throws BusinessException {
        if (null == localActivity) {
            localActivity = this.am.getCurrentActivity();
            if (null == localActivity)
                throw new BusinessException("No activity found");
        }
        logger.info("Pause/Resume activity " + localActivity.getId() +
                " state =[" + localActivity.getState() + "]");
        try {
            switch (localActivity.getState()) {
                case ActivityState.STATE_PAUSED:
                    return this.resumeActivity(false);
                case ActivityState.STATE_AUTO_PAUSED:
                    return this.resumeActivity(true);
                case ActivityState.STATE_STARTED:
                    return this.pauseActivity(false);
                default:
                    throw new BusinessException("Invalid activity state");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Pause/Resume activity " + localActivity.getId() + " error", e);
            throw new BusinessException(e);
        }

    }

    private synchronized void completeActivity() throws BusinessException {
        new AsyncTask<Void, Void, LocalActivity>() {

            @Override
            protected LocalActivity doInBackground(Void... params) {
                if (null == localActivity) {
                    localActivity = am.getCurrentActivity();
                    if (null == localActivity) {
                        logger.info("获取当前正在骑行的记录为null");
                        return null;
                    }
                }

                final int state = localActivity.getState();
                final ActivityStateMachine asm = new ActivityStateMachine(state);
                try {
                    asm.complete();

                    // v2.2.0-rc 存储极速打点到数据库，以便上传到服务器
                    if (null != maxSpeedActivitySample) {
                        try {
                            lasDao.createOrUpdate(maxSpeedActivitySample);
                        } catch (PersistenceException e) {
                            logger.info("CreateOrUpdate maxSpeedActivitySample failed, " + e);
                            e.printStackTrace();
                        }
                    }
                    double speed = am.computerAvgSpeed(localActivity.getId(),
                            localActivity.getTotalElapsedTime(),
                            localActivity.getTotalDistance());
                    localActivity.setSpeed(speed);
                    localActivity.setState(asm.getState());
                    localActivity.setFinishTime(System.currentTimeMillis());

                    am.updateStateAndFinishTime(localActivity);
                    ActivityManager.setCurrentActivityId(ActivityService.this, null);
                } catch (Exception e) {
                    logger.info("updateStateAndFinishTime failed, " + e);
                }

                return localActivity;
            }

            @Override
            protected void onPostExecute(LocalActivity localActivity) {
                stopSampling();

                // broadcast activity start

                final Intent intent = new Intent();

                intent.setAction(ACTION_ACTIVITY_COMPLETE);

                ActivityDTO activityDTO = new ActivityDTO(localActivity);
                intent.putExtra(EXTRA_ACTIVITY, localActivity);
                intent.putExtra(CyclingCompletedActivity.EXTRA_CLOUD_ACTIVITY, activityDTO);
                intent.putExtra(CyclingCompletedActivity.EXTRA_SPORT_IDENTIFY,
                        activityDTO.getActivityIdentifier());
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                AVUser user = AVUser.getCurrentUser();
                if (user != null) {
                    intent.putExtra(CyclingCompletedActivity.EXTRA_USER_ID, user.getObjectId());
                    intent.putExtra(CyclingCompletedActivity.EXTRA_AVATAR_URL, user.getAvatar());
                    intent.putExtra(CyclingCompletedActivity.EXTRA_NICK_NAME, user.getDisplayName());
                }
                sendBroadcast(intent);

                // hide notification
                hideNotification();

                if (localActivity.getTotalDistance() <= 10) {
                    return;
                }

                // activity complete
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // voice feedback
//            this.playVoiceFeedback(intent.getAction());

                // upload samples immediately
                try {
                    startService(new Intent(ActivityService.this, SyncService.class));
                } catch (Exception e) {
                    logger.info("OPPO Service SecurityException, " + e);
                }

                logger.info("Complete activity " + localActivity.getId() + "\r\n\r\n");
            }
        }.execute();
    }

    /**
     * Sampling task runs in every 1 second to acquire the GPS data and
     * collection a sample data in every 5 seconds
     */
    private final class SamplingTask extends TimerTask {

        /**
         * The last location from LocationService
         */
        private Location tmpGpsLocation;

        private AVUser avUser;

        private LocalUser localUser;

        /**
         * Has altitude gps location
         */
        private Location hasAltitudeLocation;

        /**
         * A counter for sampling times counting
         */
        private final AtomicLong counter = new AtomicLong(0);

        private SamplingTask() {
            avUser = AVUser.getCurrentUser();

            if (null == avUser || TextUtils.isEmpty(avUser.getObjectId())) {
                logger.error("No authenticated user found");
                return;
            }

            try {
                localUser = luDao.get(avUser.getObjectId());
            } catch (PersistenceException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {

            long n = this.counter.getAndIncrement();

            if (null == localUser) {
                logger.warn("No local user found...");
                return;
            }

            if (null == localActivity) {
                localActivity = am.getCurrentActivity();
                if (null == localActivity)
                    logger.warn("No activity found...");
                return;
            }

            switch (localActivity.getState()) {
                case ActivityState.STATE_AUTO_PAUSED:

                    logger.warn("Activity  AUTO_PAUSED, Location: " + lastGpsLocation);

                    if (null != lastGpsLocation) {
                        double speed = lastGpsLocation.getSpeed() * 3.6f;

                        double diffDistance = 0;
                        long diffTime = 0;
                        if (tmpGpsLocation != null) {

                            converter.coord(new LatLng(tmpGpsLocation.getLatitude(), tmpGpsLocation.getLongitude()));
                            final LatLng tmpLl = converter.convert();

                            converter.coord(new LatLng(lastGpsLocation.getLatitude(), lastGpsLocation.getLongitude()));
                            final LatLng latLng = converter.convert();

                            diffDistance = DistanceUtil.getDistance(tmpLl, latLng);
                            diffTime = Math.abs(lastGpsLocation.getTime() - tmpGpsLocation.getTime()) / 1000;
                            if (diffDistance > 0 && diffTime > 0) {
                                speed = Math.max((diffDistance / diffTime) * 3.6, speed);
                            }
                        } else {
                            logger.warn("Activity  AUTO_PAUSED, tmpGpsLocation is null");
                        }

                        final float acc = lastGpsLocation.getAccuracy();
                        logger.info("AUTO_PAUSED GpsSpeed = [" + lastGpsLocation.getSpeed() * 3.6f + "] speed=[" + speed + "]" + "\ndiffDistance=" + diffDistance + "\nacc=" + acc);

                        // auto resume if speed is greater than 4.5 km/h
                        if ((speed >= MIN_AUTO_RESUME_SPEED || diffDistance >= 50) && acc <= MAX_LOCATION_ACC) {
                            try {
                                resumeActivity(true);
                            } catch (BusinessException e) {
                            }
                        } else {
                            lastGpsLocation = null;
                        }

                    }

                    return;
                case ActivityState.STATE_PAUSED:
                    logger.warn("Activity " + localActivity.getId()
                            + " PAUSED, Location: " + lastGpsLocation);
                    return;
                case ActivityState.STATE_NONE:
                case ActivityState.STATE_COMPLETE:
                    logger.warn("Activity " + localActivity.getId() + " COMPLETE");
                    return;
                case ActivityState.STATE_STARTED:
                    break;
            }

            this.updateActivity(n, localActivity, localUser);
        }

        /**
         * @param n  The times of sampling
         * @param la The current activity
         * @param lu The current user
         */
        private void updateActivity(long n, final LocalActivity la,
                                    final LocalUser lu) {

            Location gpsLocation = lastGpsLocation;

            double speed = 0;
            double totalCalorie = la.getTotalCalorie();
            double avgVelocity = la.getSpeed();
            double velocity = 0;
            double altitude = 0;
            double cardiacRate = 0;
            double distance = 0;
            double totalDistance = la.getTotalDistance();
            double totalElapsedTime = la.getTotalElapsedTime() * 1000
                    + SAMPLING_INTERVAL;

            // 爬坡距离
            double totalUphillDistance = la.getTotalUphillDistance();
            // 累计上升
            double riseTotal = la.getTotalRisenAltitude();

            boolean changed = true;

            if (null == gpsLocation) {
                changed = false;
            } else {

                // calculate speed
                if (gpsLocation.hasSpeed() && gpsLocation.getAccuracy() <= MAX_SPEED_ACC) {
                    velocity = gpsLocation.getSpeed() * 3.6;
                    speed = velocity;//初始默认用 gps Location的值
                }

                // accumulate distance
                if (null != this.tmpGpsLocation) {
                    double tmpLat = this.tmpGpsLocation.getLatitude();
                    try {
                        tmpLat = Double.parseDouble(String.format("%.6f", tmpLat));
                    } catch (NumberFormatException e) {
                    }
                    double tmpLng = this.tmpGpsLocation.getLongitude();
                    try {
                        tmpLng = Double.parseDouble(String.format("%.6f", tmpLng));
                    } catch (NumberFormatException e) {
                    }
                    double gpsLat = gpsLocation.getLatitude();
                    try {
                        gpsLat = Double.parseDouble(String.format("%.6f", gpsLat));
                    } catch (NumberFormatException e) {
                    }
                    double gpsLng = gpsLocation.getLongitude();
                    try {
                        gpsLng = Double.parseDouble(String.format("%.6f", gpsLng));
                    } catch (NumberFormatException e) {
                    }
                    if (tmpLat == 0 || tmpLng == 0
                            || tmpLat == 4.9E-324
                            || tmpLng == 4.9E-324) {
                        changed = false;
                        logger.error("Location is error, latitude = " + tmpLat + ", longitude = "
                                + tmpLng);
                    } else if (tmpLat == gpsLat && tmpLng == gpsLng) {
                        changed = false;
                        logger.info("tmpGpsLocation = gpsLocation");
                    } else {

                        // baidu
                        converter.coord(new LatLng(tmpLat, tmpLng));
                        final LatLng tmpLl = converter.convert();

                        converter.coord(new LatLng(gpsLat, gpsLng));
                        final LatLng latLng = converter.convert();

                        distance = DistanceUtil.getDistance(tmpLl, latLng);
                        double diffTime = Math.abs(lastGpsLocation.getTime() -
                                tmpGpsLocation.getTime()) / 1000;
                        if (distance > 0 && diffTime >= 1) {//diffTime >= 1 防止diffTime 差值太小算出来的速度太大
                            speed = distance / diffTime;
                        }
                        velocity = Math.max(velocity, speed);
                        boolean available = true;
                        if (distance > 200 && velocity > 40) {// m
                            //TODO: 飘点
                            available = false;
                            logger.info("======飘点了====== speed=" + velocity + " m/s");
                        }

                        if (distance > 0 && available) {
                            this.tmpGpsLocation = gpsLocation;
                            totalDistance = totalDistance + distance;

                            // 速度的倒数
                            final double speedInverse = SAMPLING_INTERVAL / (distance * 0.06);
                            final double k = CalorieCalculator.getCoefficient(ActivityType.CYCLING,
                                    speedInverse);
                            double weight = lu.getWeight();
                            if (weight <= 0)
                                weight = 65;
                            totalCalorie = totalCalorie
                                    + CalorieCalculator.calculate(weight,
                                    SAMPLING_INTERVAL / 3600f, k);

                            if (null != hasAltitudeLocation) {
                                altitude = gpsLocation.getAltitude();
                                double lastAltitude = hasAltitudeLocation.getAltitude();

                                if (altitude > lastAltitude) {
                                    double diffAltitude = altitude
                                            - lastAltitude;
                                    logger.info("diffAltitude = "
                                            + diffAltitude);

                                    if (diffAltitude > 0 && diffAltitude < 2.5) {
                                        // 爬坡距离
                                        double uphillDistance = Math
                                                .sqrt((distance * distance)
                                                        + (diffAltitude * diffAltitude));
                                        totalUphillDistance = totalUphillDistance
                                                + uphillDistance;

                                        // 累计爬升
                                        riseTotal = riseTotal + diffAltitude;
                                    }

                                }
                            }

                            // v2.2.0-rc 存储极速的打点
                            if (velocity > la.getMaxVelocity() && velocity < MAX_VELOCITY) {
                                la.setMaxVelocity(velocity);
                                maxSpeedActivitySample = collectSample(lu, la, gpsLocation, false);
                                n--; //防止重复点
                            }
                        }
                    }
                } else {
                    logger.info("tempGpsLocation is null");
                }
            }

            if (totalDistance > 0 && totalElapsedTime > 0) {
                avgVelocity = totalDistance / totalElapsedTime * 3.6;
            }

            la.setSpeed(avgVelocity);
            la.setInstantaneousVelocity(velocity);
            la.setTotalCalorie(totalCalorie);
            la.setTotalDistance(totalDistance);
            la.setTotalElapsedTime(totalElapsedTime / 1000f);

            // v1.5.0 add 爬坡距离，累计爬升
            la.setTotalUphillDistance(totalUphillDistance);
            la.setTotalRisenAltitude(riseTotal);

            la.setMaxAltitude(Math.max(la.getMaxAltitude(), altitude));
            la.setMaxCardiacRate(Math.max(la.getMaxCardiacRate(), cardiacRate));

            //通过binder通知activity更新ui
            if (null != mICyclingBinderListener) {
                mICyclingBinderListener.onLocalActivityRefresh(la);
            }

            if ((velocity * 3.6 <= 1) || !changed) {
                lastStationaryTime = Math.min(lastStationaryTime,
                        SystemClock.uptimeMillis());
            } else {
                lastStationaryTime = Long.MAX_VALUE;
            }

            final BeastBikes app = (BeastBikes) getApplication();
            if (app.isAutoPauseEnabled()) {
                final long span = SystemClock.uptimeMillis()
                        - lastStationaryTime;

                if (span >= AUTO_PAUSE_THRESHOLD) {
                    logger.info("Activity has been paused for " + span + " ms");
                    try {
                        pauseActivity(true);
                    } catch (BusinessException e) {
                        logger.error("Auto pause failed", e);
                    }
                }
            }


            if (0 == n % SAMPLING_RATE) {
                collectSample(lu, la, gpsLocation, true);

                synchronized (ActivityService.this) {
                    ordinal.incrementAndGet();
                }
            }
            //只有第一次才会触发
            if (tmpGpsLocation == null && gpsLocation != null) {
                tmpGpsLocation = gpsLocation;
            }
            if (gpsLocation != null && gpsLocation.hasAltitude()) {
                this.hasAltitudeLocation = gpsLocation;
            }
            logger.info("updateActivity: changed=" + changed + ", totalDistance = "
                    + la.getTotalDistance() + " ,totalTime = " + la.getTotalElapsedTime());
        }

    }

    private void showNotification(Notification notification, int id) {
        if (notification == null)
            return;

        notification.flags |= 0x00000080 /* Notification.FLAG_HIGH_PRIORITY */;

        // set highest priority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Field priority = Notification.class.getField("priority");
                priority.setAccessible(true);
                priority.set(notification, 2 /* Notification.PRIORITY_MAX */);
            } catch (Throwable t) {
                // ignore
            }
        }

        this.notificationManager.notify(id, notification);
        this.startForeground(id, notification);
    }

    private void hideNotification() {
        final int id = getClass().getName().hashCode();
        if (this.notificationManager != null) {
            this.notificationManager.cancel(id);
        }
        this.stopForeground(true);

        if (userSp != null) {
            userSp.edit().putLong(PREF_HOME_NAV_CYCLING_STATE, System.currentTimeMillis()).apply();
        }
    }

    private void showNotification(int state) {
        Notification notification = null;
        if (state == ActivityState.STATE_AUTO_PAUSED || state == ActivityState.STATE_PAUSED) {
            notification = NotificationUtil.getNotification(R.string.notification_riding_stop);
            notification.tickerText = getText(R.string.notification_riding_stop);
        } else if (state == ActivityState.STATE_STARTED) {
            notification = NotificationUtil.getNotification(R.string.notification_riding);
            notification.tickerText = getText(R.string.notification_riding);
        }
        if (notification == null)
            return;
        this.notificationManager.cancel(getClass().getName().hashCode());
        final int id = getClass().getName().hashCode();
        showNotification(notification, id);
        if (userSp != null)
            userSp.edit().putLong(PREF_HOME_NAV_CYCLING_STATE, System.currentTimeMillis()).apply();
    }

    private long screenOffTimeMillis = 0;
    public static boolean isScreenOn = true;

    @Override
    public void onScreenOn() {
        logger.info("ActivityService: screen onScreenOn");
        isScreenOn = true;
        screenOffTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void onScreenOff() {
        try {
            isScreenOn = false;
            logger.info("ActivityService: screen onScreenOff");
            screenOffTimeMillis = System.currentTimeMillis();
            final BeastBikes app = (BeastBikes) getApplication();
            if (app.isForeGroundEnabled() &&
                    PackageUtils.isApplicationBroughtToBackground(getApplicationContext())) {
                ScreenObserverTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (isScreenOn) {
                            logger.info("ActivityService: screen ScreenOn is on");
                            return;
                        }
                        if (System.currentTimeMillis() - screenOffTimeMillis > 19 * 1000 && !calling) {
                            Intent intent = new Intent(ActivityService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            logger.info("ActivityService: screen start main activity...");

                            screenOffTimeMillis = System.currentTimeMillis();
                        }
                    }
                }, 0, 20 * 1000);

            } else {
                logger.info("ActivityService: screen the activity is background...");
            }
        } catch (Exception e) {
            logger.error("onScreenOff error");
        }

    }

    public class ICyclingBinder extends Binder {

        /**
         * get instance of {@link ActivityService}
         *
         * @return instance of {@link ActivityService}
         */
        public ActivityService getService() {

            return ActivityService.this;
        }

    }

    /**
     * set listener for {@link ActivityService}
     *
     * @param iCyclingBinderListener
     */
    public void setICyclingBinderListener(ICyclingServiceListener iCyclingBinderListener) {
        this.mICyclingBinderListener = iCyclingBinderListener;
    }

    /**
     * callback when location changed
     */
    public interface ICyclingServiceListener {

        /**
         * fired when location changed
         *
         * @param localActivity {@link LocalActivity} instance
         */
        void onLocalActivityRefresh(LocalActivity localActivity);
    }

    /**
     * 监听来电状态
     */
    private final class SpeedXPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    logger.trace("手机空闲起来了");
                    calling = false;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    logger.trace("来电");
                    calling = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    logger.trace("电话挂断...");
                    calling = true;
                default:
                    break;
            }
        }

    }

}
