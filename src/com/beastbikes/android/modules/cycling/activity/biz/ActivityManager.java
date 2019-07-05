package com.beastbikes.android.modules.cycling.activity.biz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.modules.cycling.activity.dao.LocalActivityDao;
import com.beastbikes.android.modules.cycling.activity.dao.LocalActivitySampleDao;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.activity.dao.entity.RemoteSample;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.RemoteSamplesDTO;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.beastbikes.android.modules.user.util.ActivityDataUtil;
import com.beastbikes.android.persistence.BeastStore.Activities;
import com.beastbikes.android.persistence.BeastStore.Activities.ActivityColumns;
import com.beastbikes.android.persistence.BeastStore.Activities.Samples;
import com.beastbikes.android.persistence.BeastStore.Activities.Samples.SampleColumns;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.android.utils.SerializeUtil;
import com.beastbikes.framework.android.cache.CacheManager;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.business.BusinessObject;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ActivityManager extends AbstractBusinessObject implements BusinessObject {

    private static final String TAG = "ActivityManager";

    private static final String FILE_PATH_LOCAL_ACTIVITY_SAMPLE = "local_activity_sample";

    private static final Logger logger = LoggerFactory.getLogger(ActivityManager.class);

    private static final String PREF_ACTIVITY_ID = "activity.id";

    public static String getCurrentActivityId(Context ctx) {
        final String pkg = ctx.getPackageName();
        final SharedPreferences sp = ctx.getSharedPreferences(pkg, 0);
        if (!sp.contains(PREF_ACTIVITY_ID))
            return null;

        try {
            return sp.getString(PREF_ACTIVITY_ID, null);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static boolean setCurrentActivityId(Context ctx, String id) {
        final String pkg = ctx.getPackageName();
        final SharedPreferences sp = ctx.getSharedPreferences(pkg, 0);
        final Editor editor = sp.edit();

        if (TextUtils.isEmpty(id)) { //// FIXME: 16/8/1  review logic
            editor.remove(PREF_ACTIVITY_ID);
        } else {
            editor.putString(PREF_ACTIVITY_ID, id);
        }

        return editor.commit();
    }

    private final LocalActivityDao laDao;
    private final LocalActivitySampleDao lasDao;

    private CyclingServiceStub stub;
    private Activity activity;

    public ActivityManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        final BeastBikes app = (BeastBikes) BeastBikes.getInstance().getApplicationContext();
        final ORMLitePersistenceManager pm = app
                .getPersistenceManager();
        this.laDao = new LocalActivityDao((ORMLitePersistenceSupport) pm);
        this.lasDao = new LocalActivitySampleDao((ORMLitePersistenceSupport) pm);

        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.stub = factory.create(CyclingServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
    }

    public ActivityManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.activity = activity;
        final BeastBikes app = (BeastBikes) BeastBikes.getInstance().getApplicationContext();
        final ORMLitePersistenceManager pm = app
                .getPersistenceManager();
        this.laDao = new LocalActivityDao((ORMLitePersistenceSupport) pm);
        this.lasDao = new LocalActivitySampleDao((ORMLitePersistenceSupport) pm);

        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.stub = factory.create(CyclingServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(activity));
    }

    public LocalActivity getCurrentActivity() {
        final String activityId = getCurrentActivityId((Context) getContext());
        if (TextUtils.isEmpty(activityId))
            return null;

        try {
            return this.laDao.get(activityId);
        } catch (PersistenceException e) {
            return null;
        }
    }

    /**
     * New Restful API
     *
     * @param userId    (用户Id)
     * @param pageCount （每页个数）
     * @param pageIndex （页码）
     * @return List
     * @throws BusinessException
     */
    public List<ActivityDTO> getActivitiesByUserId(String userId, int pageCount, int pageIndex,
                                                   boolean refresh, String centralId) throws BusinessException {
        final CacheManager cm = CacheManager.getInstance();
        final List<ActivityDTO> list = new ArrayList<>();

        NetworkInfo ni = ConnectivityUtils.getActiveNetwork(BeastBikes
                .getInstance().getApplicationContext());

        if (!refresh || null == ni || !ni.isConnected()) {
            try {
                List<LocalActivity> localActivities = laDao.getLocalActivities(
                        userId, pageIndex - 1, pageCount, centralId);
                for (LocalActivity localActivity : localActivities) {
                    list.add(new ActivityDTO(localActivity));
                }

                if (!list.isEmpty()) {
                    logger.trace("get activity by local");
                    return list;
                }
            } catch (PersistenceException e) {
                throw new BusinessException(e);
            }
        }

        try {
            final JSONObject result = this.stub.getCyclingRecordList(userId, pageCount, pageIndex, centralId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray routes = result.optJSONArray("result");
                String currentUserId = null;
                AVUser currentUser = AVUser.getCurrentUser();
                if (currentUser != null) {
                    currentUserId = currentUser.getObjectId();
                }
                for (int i = 0; i < routes.length(); i++) {
                    JSONObject obj = routes.optJSONObject(i);
                    if (userId.equals(currentUserId)) {
                        final ActivityDTO dto = new ActivityDTO(obj);
                        dto.setAvatarUrl(cm.getString(userId));
                        list.add(dto);

                        try {
                            LocalActivity localActivity = new LocalActivity(dto);
                            localActivity.setBleDataType(2);
                            String deviceId = localActivity.getCentralId();
                            if (!TextUtils.isEmpty(deviceId) && deviceId.length() == 12) {
                                localActivity.setDeviceId(CentralSession.centralId2Address(localActivity.getCentralId()));
                            }
                            laDao.createOrUpdate(localActivity);
                            logger.trace("Save activity to local success");
                        } catch (PersistenceException e) {
                            logger.error("Save activity to local error");
                        }
                    } else if (!obj.optBoolean("fake")) {
                        final ActivityDTO dto = new ActivityDTO(obj);
                        dto.setAvatarUrl(cm.getString(userId));
                        list.add(dto);
                    }
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * New restful api
     *
     * @param activityId 骑行ID
     * @return ActivityDTO
     * @throws BusinessException
     */
    public ActivityDTO getActivityInfoByActivityId(String userId, final String activityId)
            throws BusinessException {
        try {
            if (TextUtils.isEmpty(userId)) {
                userId = "";
            }

            if (TextUtils.isEmpty(activityId)) {
                return null;
            }

            final JSONObject result = this.stub.getActivityInfoByActivityId(userId, activityId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return new ActivityDTO(result.optJSONObject("result"));
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * New Restful API
     *
     * @param activityId 骑行id
     * @param userId     用户id
     * @return List
     * @throws BusinessException
     */
    @SuppressLint("UseSparseArrays")
    public List<SampleDTO> getActivitySamplesByActivityId(final String activityId, final String userId,
            final String activityIdentifier) throws BusinessException {
        final List<SampleDTO> dds = new ArrayList<>();

        List<LocalActivitySample> lases = getLocalActivitySamples(activityIdentifier);
        if (null != lases && !lases.isEmpty()) {
            for (LocalActivitySample las : lases) {
                dds.add(new SampleDTO(las));
            }
        }

        if (!dds.isEmpty()) {
            logger.trace("get activity samples by local");
            return dds;
        }

        if (TextUtils.isEmpty(activityId) && TextUtils.isEmpty(activityIdentifier))
            return null;

        try {
            final JSONObject result = this.stub.getActivitySamplesByActivityId(userId, activityIdentifier);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray array = result.optJSONArray("result");
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    RemoteSamplesDTO rsd = new RemoteSamplesDTO(array.optJSONObject(i));
                    List<SampleDTO> sampleDTOs = rsd.getDatas();
                    if (!dds.contains(sampleDTOs)) {
                        dds.addAll(rsd.getDatas());
                    }
                }

                // 缓存到数据库
                AVUser user = AVUser.getCurrentUser();
                if (null != user && userId.equals(user.getObjectId())) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // 保存Sample到数据库
                            List<LocalActivitySample> lases = new ArrayList<>();

                            int ordinal = 0;
                            for (int i = 0; i < dds.size(); i++) {
                                SampleDTO sample = dds.get(i);
                                LocalActivitySample las = new LocalActivitySample();
                                las.setActivityId(activityIdentifier);
                                las.setRemoteId(activityId);
                                if (TextUtils.isEmpty(activityId)) {
                                    las.setRemoteId(activityIdentifier);
                                }
                                las.setUserId(userId);
                                las.setSynced(true);
                                las.setSyncTime(System.currentTimeMillis());
                                las.setId(UUID.randomUUID().toString());
                                las.setAltitude(String.valueOf(sample
                                        .getAltitude()));
                                las.setLatitude0(String.valueOf(sample
                                        .getLatitude0()));
                                las.setLatitude1(String.valueOf(sample
                                        .getLatitude1()));
                                las.setLongitude0(String.valueOf(sample
                                        .getLongitude0()));
                                las.setLongitude1(String.valueOf(sample
                                        .getLongitude1()));
                                las.setElapsedTime(Math.round(sample
                                        .getElapsedTime()));
                                las.setDistance(sample.getDistance());
                                las.setVelocity(sample.getVelocity());
                                las.setCalorie(sample.getCalorie());
                                las.setCardiacRate(sample.getCardiacRate());

                                // v2.0.0 添加的新的字段
                                las.setCurrTime(sample.getElapsedTime());

                                // v2.5.0补缺踏频缓存
                                las.setCadence(sample.getCadence());

                                las.setOrdinal(ordinal);
                                ordinal++;
                                lases.add(las);
                            }

                            try {
                                lasDao.createOrUpdate(lases);
                            } catch (PersistenceException e) {
                                logger.error("Save activity sample to local error");
                            }
                        }

                    }).start();
                }

                return dds;
            } else {
                String message = result.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * Upload cycling record
     *
     * @param la LocalActivity
     * @return int
     * @throws BusinessException
     */
    public int saveSamples(final LocalActivity la) throws BusinessException {
        final String activityId = la.getId();

        try {
            while (true) {
                final List<LocalActivitySample> samples = this.lasDao
                        .getUnsyncedLocalActivitySamples(activityId, 0,
                                ActivityService.SAMPLING_BUFFER_SIZE);

                // upload local activity samples
                if (null == samples || samples.size() <= 0)
                    break;

                if (samples.size() >= ActivityService.SAMPLING_BUFFER_SIZE
                        || ActivityState.STATE_COMPLETE == la.getState()) {
                    logger.info(TAG + ": Found " + samples.size() + " samples");
                    final long now = System.currentTimeMillis();
                    final JSONArray data = new JSONArray();
                    int sequence = 0;

                    for (final LocalActivitySample las : samples) {
                        logger.trace(TAG + ": Packing sample " + las.getId());

                        final JSONObject json = new JSONObject();

                        try {
                            json.put(RemoteSample.LATITUDE1,
                                    Double.parseDouble(las.getLatitude1()));
                        } catch (Throwable e) {
                            logger.error(TAG, "Set sample latitude1 error", e);
                        }

                        try {
                            json.put(RemoteSample.LONGITUDE1,
                                    Double.parseDouble(las.getLongitude1()));
                        } catch (Throwable e) {
                            logger.error(TAG, "Set sample longitude1 error", e);
                        }

                        try {
                            json.put(RemoteSample.ALTITUDE,
                                    Double.parseDouble(las.getAltitude()));
                        } catch (Throwable e) {
                            logger.error(TAG, "Set sample altitude error", e);
                        }

                        // v2.0.0之前上传的固定的间隔，比如：0， 5 ，10
//                        try {
//                            json.put(RemoteSample.TIME, las.getTime());
//                        } catch (JSONException e) {
//                            logger.error(TAG, "Set sample time error", e);
//                        }

                        // v2.0.0 添加的新的字段，为打点时的时间戳
                        try {
                            json.put(RemoteSample.TIME, las.getCurrTime());
                        } catch (JSONException e) {
                            logger.error(TAG, "Set sample time error", e);
                        }

                        try {
                            json.put(RemoteSample.DISTANCE, las.getDistance());
                        } catch (JSONException e) {
                            logger.error(TAG, "Set sample distance error", e);
                        }

                        try {
                            json.put(RemoteSample.VELOCITY, las.getVelocity());
                        } catch (JSONException e) {
                            logger.error(TAG, "Set sample velocity error", e);
                        }

                        try {
                            json.put(RemoteSample.CALORIE, las.getCalorie());
                        } catch (JSONException e) {
                            logger.error(TAG, "Set sample calorie error", e);
                        }

                        try {
                            json.put(RemoteSample.CARDIAC_RATE,
                                    las.getCardiacRate());
                        } catch (JSONException e) {
                            logger.error(TAG, "Set sample cardiac rate error", e);
                        }

                        try {
                            json.put(RemoteSample.CADENCE, las.getCadence());
                        } catch (JSONException e) {
                            logger.error(TAG, "Set sample cadence error", e);
                        }

                        data.put(json);
                        sequence = las.getOrdinal() / ActivityService.SAMPLING_BUFFER_SIZE;
                    }

                    logger.info(TAG + ": Syncing samples of activity " + activityId + " sequence " + sequence);

                    final JSONObject result = this.stub.saveSample(activityId, sequence, data.toString());
                    if (null == result || !result.optBoolean("result")) {
                        return -1;
                    }

                    if (result.optBoolean("result")) {
                        logger.info(TAG + ": Sync samples of activity " + activityId
                                + "success");
                        for (final LocalActivitySample las : samples) {
                            las.setSynced(true);
                            las.setSyncTime(now);
                            las.setRemoteId(UUID.randomUUID().toString());
                        }

                        this.lasDao.update(samples);
                    }
                } else {
                    logger.info(TAG + ":Samples of activity " + activityId
                            + " isn't enough to sync");
                    break;
                }
            }

            if (ActivityState.STATE_COMPLETE == la.getState()) {
                return this.saveCyclingRecord(la);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return -1;
    }

    /**
     * 上传骑行记录
     *
     * @param la 本地骑行记录
     * @throws BusinessException
     */
    private int saveCyclingRecord(final LocalActivity la) throws BusinessException {
        if (null == la) {
            return -1;
        }

        final Context ctx = (Context) getContext();
        String title = la.getTitle();
        if (TextUtils.isEmpty(title)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String data = sdf.format(new Date(la.getStartTime()));
            title = ActivityDataUtil.formatDateTime(ctx, la.getStartTime());
            title = data + title;
        }

        final String sportIdentify = la.getId();
        final double speed = computerAvgSpeed(la.getId(), la.getTotalElapsedTime(), la.getTotalDistance());
        String startTime = DateFormatUtil.dateFormat2String(la.getStartTime());
        String stopTime = DateFormatUtil.dateFormat2String(la.getFinishTime());

        String source = null;
        if (!TextUtils.isEmpty(la.getSource())) {
            source = la.getSource();
        }

        String centralId = null;
        if (!TextUtils.isEmpty(la.getCentralId())) {
            centralId = la.getCentralId();
        }

        String centralName = null;
        if (!TextUtils.isEmpty(la.getCentralName())) {
            centralName = la.getCentralName();
        }

        try {
            final JSONObject result = this.stub.saveCyclingRecord(la.getUserId(), title,
                    sportIdentify, la.getTotalCalorie(), speed,
                    la.getMaxVelocity(), 1, stopTime, startTime,
                    la.getTotalElapsedTime(), la.getTotalRisenAltitude(),
                    la.getTotalUphillDistance(), la.getTotalDistance(), source,
                    centralId, la.getMaxCadence(), la.getMaxCardiacRate(), null, null, centralName);
            if (null == result) {
                return -1;
            }

            if (result.optInt("code") == 0) {
                final JSONObject json = result.optJSONObject("result");
                if (null == json) {
                    logger.error("Upload local activity error by activityId " + sportIdentify);
                    return -1;
                }

                la.setSynced(true);
                la.setSyncTime(System.currentTimeMillis());
                la.setTitle(json.optString("title"));
                la.setRemoteId(json.optString("objectId"));
                la.setFake(json.optBoolean("fake") ? 1 : 0);
                la.setActivityUrl(json.optString("cyclingImage"));
                la.setSpeed(json.optDouble("speed"));
                la.setMaxVelocity(json.optDouble("speedMax"));
                la.setTotalRisenAltitude(json.optDouble("riseTotal"));
                la.setTotalUphillDistance(json.optDouble("uphillDistance"));
                la.setTotalCalorie(json.optDouble("calories"));
                la.setTotalDistance(json.optDouble("distance"));
                la.setCentralName(json.optString("centralName"));
                this.laDao.updateSyncInfo(la);
                logger.info("Upload local activity success by activityId " + sportIdentify + "uid =" + la.getUserId(), "json=" + json.toString());
                return 0;
            } else {
                logger.error("Upload local activity error by activityId " + sportIdentify + ", uid=" + la.getUserId());
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return -1;
    }

    public double computerAvgSpeed(String activityId, double totalTime, double totalDistance) {
        double speed = 0;

        try {
            int pageIndex = 0;
            int pageCount = ActivityService.SAMPLING_BUFFER_SIZE;
            double totalErrorDistance = 0;

            LocalActivitySample preSample = null;
            while (true) {
                List<LocalActivitySample> samples = this.lasDao.getLocalActivitySamples(activityId,
                        pageIndex, pageCount);

                if (null == samples || samples.size() <= 0)
                    break;

                for (int i = 0; i < samples.size(); i++) {
                    LocalActivitySample currentSample = samples.get(i);

                    if (i > 1) {
                        preSample = samples.get(i - 1);
                    }
                    if (preSample != null) {

                        double distance = currentSample.getDistance() - preSample.getDistance();
                        if (distance > 200) {// m/s 数据库里存的是5秒一个点
                            totalErrorDistance += distance;
                        }
                    }
                }
                pageIndex++;
            }

            double diffDistance = totalDistance - totalErrorDistance;

            if (diffDistance > 0 && totalTime > 0) {
                speed = diffDistance / totalTime * 3.6;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("computerAvgSpeed error e=" + e.getMessage());
        }

        return speed;

    }

    /**
     * New Restful Api
     *
     * @param activityId 骑行记录id
     * @return boolean
     * @throws BusinessException
     */
    public boolean deleteActivityByActivityId(final String activityId)
            throws BusinessException {
        try {
            final JSONObject result = this.stub.deleteActivityByActivityId(activityId);
            if (null == result) {
                return false;
            }
            if (result.optInt("code") == 0) {
                Toasts.showOnUiThread(activity, activity.getResources().getString(R.string.delete_success));
                return true;
            }
            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            return result.optBoolean("result");
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }


    /**
     * @param activityId 骑行记录ID
     * @param title      骑行记录Title
     */
    public boolean updateCyclingRecordTitle(final String activityId, final String title) {
        if (TextUtils.isEmpty(activityId) || TextUtils.isEmpty(title)) {
            return false;
        }

        try {
            final JSONObject result = stub.updateCyclingRecordTitle(activityId, title);
            if (null == result)
                return false;

            if (result.optBoolean("result")) {
                logger.info("Update cycling record title is success");
                // Update Local Activity title
                laDao.updateCyclingTitle(activityId, title);
                logger.info("Update local activity title is success");
                return true;
            }
        } catch (Exception e) {
            logger.error("update cycling record title is error");
        }

        return false;
    }

    /**
     * 获取本地LocalActivity
     *
     * @param activityId 本地数据库骑行记录ID
     * @return LocalActivity
     * @throws BusinessException
     */
    public LocalActivity getLocalActivity(String activityId) throws BusinessException {
        try {
            return this.laDao.get(activityId);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 更新本地LocalActivity
     *
     * @param localActivity 数据库骑行记录
     * @throws BusinessException
     */
    public synchronized void updateLocalActivity(LocalActivity localActivity)
            throws BusinessException {
        try {
            this.laDao.update(localActivity);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 更新本地LocalActivity状态和完成时间
     *
     * @param la 数据库骑行记录
     * @throws BusinessException
     */
    public void updateStateAndFinishTime(LocalActivity la) throws BusinessException {
        try {
            this.laDao.updateStateAndFinishTime(la);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    public void updateAccumulativeData(LocalActivity la) throws BusinessException {
        try {
            this.laDao.updateAccumulativeData(la);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 删除本地LocalActivity
     *
     * @param la {@link LocalActivity}
     * @throws BusinessException
     */
    public void delete(LocalActivity la) throws BusinessException {
        try {
            laDao.delete(la);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 删除本地LocalActivity
     *
     * @param activityId activityId
     * @throws BusinessException
     */
    public synchronized void deleteLocalActivity(String activityId) throws BusinessException {
        try {
            laDao.delete(activityId);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 从本地数据库获取为同步到服务器的骑行记录
     * @param userId 用户id
     * @param centralId v2.4.2起,我的设备页的骑行记录仅展示相关设备的记录
     * @return 骑行记录列表
     * @throws BusinessException
     */
    public synchronized List<LocalActivity> getUnsyncedActivities(String userId, String centralId)
            throws BusinessException {
        try {
            if (TextUtils.isEmpty(userId))
                return null;

            if (TextUtils.isEmpty(centralId)) {
                return this.laDao
                        .query("WHERE "
                                        + ActivityColumns.USER_ID
                                        + "=? and "
                                        + ActivityColumns.STATE
                                        + "=? and length(trim(ifnull(remote_id, ''))) = 0 order by "
                                        + ActivityColumns.START_TIME + " desc", userId,
                                String.valueOf(ActivityState.STATE_COMPLETE));
            } else {
                return this.laDao
                        .query("WHERE "
                                        + ActivityColumns.USER_ID
                                        + "=? and "
                                        + ActivityColumns.CENTRAL_ID
                                        + "=? and "
                                        + ActivityColumns.STATE
                                        + "=? and length(trim(ifnull(remote_id, ''))) = 0 order by "
                                        + ActivityColumns.START_TIME + " desc", userId, centralId,
                                String.valueOf(ActivityState.STATE_COMPLETE));
            }

        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 根据activityId查询LocalActivitySample
     *
     * @param activityId 骑行记录ID
     * @return List
     * @throws BusinessException
     */
    public synchronized List<LocalActivitySample> getLocalActivitySamples(String activityId)
            throws BusinessException {
        try {
            return this.lasDao.query("WHERE " + SampleColumns.ACTIVITY_ID
                    + "=? and velocity >= 0 and velocity < 1.79769313486231570e+308", activityId);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 删除本地骑行坐标数据 ("activity_sample")
     *
     * @param activityId
     * @throws BusinessException
     */
    public void deleteLocalActivitySamples(final String activityId)
            throws BusinessException {
        try {
            final List<LocalActivitySample> lases = getLocalActivitySamples(activityId);
            this.lasDao.delete(lases);
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 删除已上传的骑行记录
     *
     * @throws BusinessException
     */
    public void deleteSyncedActivities() throws BusinessException {
        try {
            this.laDao.execute("DELETE FROM " + Activities.CONTENT_CATEGORY
                    + " WHERE synced IS NOT 0 AND remote_id IS NOT NULL");
            this.lasDao.execute("DELETE FROM " + Samples.CONTENT_CATEGORY
                    + " WHERE synced IS NOT 0 AND remote_id IS NOT NULL");
        } catch (PersistenceException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 提交申诉
     *
     * @param contact    联系方式
     * @param content    消息内容
     * @param activityId 骑行记录id
     * @return boolean
     * @throws BusinessException
     */
    public boolean postComplainForActivity(String contact, String content, String activityId)
            throws BusinessException {
        try {
            final JSONObject result = this.stub.postAppeal(contact, content,
                    activityId, Build.FINGERPRINT, Build.VERSION.RELEASE);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            return result.optBoolean("result");
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 隐藏地图
     *
     * @param activityId 骑行记录ID
     * @param isPrivate  是否私密
     * @return boolean
     * @throws BusinessException
     */
    public boolean updateCyclingRecord(final String activityId,
                                       final int isPrivate) throws BusinessException {
        try {
            final JSONObject result = this.stub.updateCyclingRecord(activityId, isPrivate);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            int code = result.optInt("code");
            if (code != 0 && !TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            } else {
                laDao.updateCyclingPrivate(activityId, isPrivate);
            }

            return code == 0;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 举报骑行记录
     *
     * @param activityId 骑行记录ID
     * @param reason     举报理由
     * @return boolean
     * @throws BusinessException
     */
    public boolean postReportSportRoute(final String activityId,
                                        final String reason) throws BusinessException {
        try {
            final JSONObject result = this.stub.postReportSportRoute(activityId, reason);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            int code = result.optInt("code");
            if (code == 0) {
                Toasts.showOnUiThread(activity, activity.getResources().getString(R.string.postReport));
            } else {
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
            return result.optBoolean("result");
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 存储LocalActivitySample
     *
     * @param las        las
     * @param activityId activityId
     */
    public void saveLocalActivitySampleUpdate(LocalActivitySample las, String activityId) {
        if (null == activity || null == las) {
            return;
        }

        try {
            byte[] cacheData = SerializeUtil.getBytesFromObject(las);
            File file = FileUtil.archivePath(activity, FILE_PATH_LOCAL_ACTIVITY_SAMPLE, activityId);
            FileUtil.saveContentToFile(cacheData, file);
        } catch (Exception e) {
            logger.error("Save localActivitySample cache error, e=" + e.getMessage());
        }
    }

    /**
     * 获取LocalActivitySample
     *
     * @param activityId activityId
     * @return LocalActivitySample
     */
    public LocalActivitySample getLocalActivitySampleCache(String activityId) {
        if (null == activity || TextUtils.isEmpty(activityId)) {
            return null;
        }

        File file = FileUtil.archivePath(activity, FILE_PATH_LOCAL_ACTIVITY_SAMPLE, activityId);
        byte[] cacheData = FileUtil.readContentBytesFromFile(file);
        if (cacheData == null)
            return null;
        try {
            LocalActivitySample las = (LocalActivitySample) SerializeUtil.getObjectFromBytes(cacheData);
            return las;
        } catch (Exception e) {
            logger.error("Get localActivitySample cache is error, " + e);
        }
        return null;
    }

    /**
     * 创建一条新的骑行记录
     * @param localActivity 骑行记录
     * @throws PersistenceException
     */
    public void createLocalActivity(LocalActivity localActivity) throws PersistenceException {
        this.laDao.create(localActivity);
    }

}
