package com.beastbikes.android.modules.cycling.activity.dao;

import android.text.TextUtils;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.util.ActivityType;
import com.beastbikes.android.persistence.BeastStore.Activities;
import com.beastbikes.android.persistence.BeastStore.Activities.ActivityColumns;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalActivityDao extends ORMLiteAccessObject<LocalActivity> implements ActivityColumns {

    private static final Logger logger = LoggerFactory
            .getLogger(LocalActivityDao.class);

    public LocalActivityDao(ORMLitePersistenceSupport ps) {
        super(ps, LocalActivity.class);
    }

    public LocalActivity getLatestActivity() throws PersistenceException {
        final String sql = new StringBuilder().append("ORDER BY ")
                .append(ActivityColumns.START_TIME).append(" DESC LIMIT 1")
                .toString();
        final List<LocalActivity> activities = super.query(sql);
        if (null == activities || activities.isEmpty())
            return null;

        return activities.get(0);
    }

    public void updateState(LocalActivity la) throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Activities.CONTENT_CATEGORY);
        sql.append(" SET ").append(ActivityColumns.STATE).append("=")
                .append(la.getState());
        sql.append(" WHERE ").append(ActivityColumns._ID).append("=?");
        this.execute(sql.toString(), la.getId());
    }

    public void updateCyclingTitle(final String activityId, final String title)
            throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Activities.CONTENT_CATEGORY);
        sql.append(" SET ").append(ActivityColumns.TITLE).append("=?");
        sql.append(" WHERE ").append(ActivityColumns._ID).append("=?");
        this.execute(sql.toString(), title, activityId);
    }

    public void updateCyclingPrivate(final String activityId, final int isPrivate)
            throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Activities.CONTENT_CATEGORY);
        sql.append(" SET ").append(ActivityColumns.IS_PRIVATE).append("=")
                .append(isPrivate);
        sql.append(" WHERE ").append(ActivityColumns._ID).append("=?");
        this.execute(sql.toString(), activityId);
    }

    public void updateStateAndFinishTime(LocalActivity la)
            throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Activities.CONTENT_CATEGORY);
        sql.append(" SET ").append(ActivityColumns.STATE).append("=")
                .append(la.getState());
        sql.append(" , ").append(ActivityColumns.FINISH_TIME).append("=")
                .append(la.getFinishTime());
        sql.append(" WHERE ").append(ActivityColumns._ID).append("=?");
        this.execute(sql.toString(), la.getId());
    }

    public void updateSyncInfo(LocalActivity la) {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Activities.CONTENT_CATEGORY);
        sql.append(" SET ").append("synced=").append(la.isSynced() ? 1 : 0);
        sql.append(" , ").append("fake=").append(la.getFake());
        sql.append(" , ").append("speed=").append(la.getSpeed());
        sql.append(" , ").append("total_uphill_distance=").append(la.getTotalUphillDistance());
        sql.append(" , ").append("total_risen_altitude=").append(la.getTotalRisenAltitude());
        sql.append(" , ").append("max_velocity=").append(la.getMaxVelocity());
        sql.append(" , ").append("total_calorie=").append(la.getTotalCalorie());
        sql.append(" , ").append("sync_time=").append(la.getSyncTime());
        sql.append(" , ").append("title=?");
        sql.append(" , ").append("activity_url=?");
        sql.append(" , ").append("remote_id=?");
        sql.append(" WHERE " + ActivityColumns._ID).append("=?");
        try {
            this.execute(sql.toString(), la.getTitle(), la.getActivityUrl(), la.getRemoteId(), la.getId());
            logger.info("UPDATE localActivity " + la.getId() + " success, SQL: " + sql.toString());
        } catch (PersistenceException e) {
            logger.info("UPDATE localActivity " + la.getId() + "error", e);
        }
    }

    public void updateAccumulativeData(LocalActivity la)
            throws PersistenceException {
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Activities.CONTENT_CATEGORY);
        sql.append(" SET ").append(ActivityColumns.INSTANTANEOUS_VELOCITY)
                .append("=").append(la.getInstantaneousVelocity());
        sql.append(" , ").append(ActivityColumns.TOTAL_DISTANCE).append("=")
                .append(la.getTotalDistance());
        sql.append(" , ").append(ActivityColumns.TOTAL_ELAPSED_TIME)
                .append("=").append(la.getTotalElapsedTime());
        sql.append(" , ").append(ActivityColumns.TOTAL_CALORIE).append("=")
                .append(la.getTotalCalorie());
        sql.append(" , ").append(ActivityColumns.TOTAL_DISTANCE).append("=")
                .append(la.getTotalDistance());
        sql.append(" , ").append(ActivityColumns.TOTAL_ELAPSED_TIME)
                .append("=").append(la.getTotalElapsedTime());
        sql.append(" , ").append(ActivityColumns.MAX_ALTITUDE).append("=")
                .append(la.getMaxAltitude());
        sql.append(" , ").append(ActivityColumns.MAX_CARDIAC_RATE).append("=")
                .append(la.getMaxCardiacRate());
        sql.append(" , ").append(ActivityColumns.MAX_VELOCITY).append("=")
                .append(la.getMaxVelocity());
        sql.append(" , ").append(ActivityColumns.TOTAL_UPHILL_DISTANCE)
                .append("=").append(la.getTotalUphillDistance());// 爬坡距离
        sql.append(" , ").append(ActivityColumns.TOTAL_RISEN_ALTITUDE)
                .append("=").append(la.getTotalRisenAltitude());// 累计爬升
        sql.append(" , ").append(ActivityColumns.SPEED).append("=").append(la.getSpeed());
        sql.append(" WHERE " + ActivityColumns._ID).append("=?");
        this.execute(sql.toString(), la.getId());
    }

    public List<LocalActivity> getLocalActivities(String userId, int pageIndex,
                                                  int pageSize, String centralId) throws PersistenceException {
        final int i = Math.max(0, pageIndex);
        final int n = Math.max(1, pageSize);
        final int offset = i * n;
        logger.trace("offset = " + offset + ";  n = " + n);
        String centralIdFilter = "";
        if (!TextUtils.isEmpty(centralId)) {
            centralIdFilter = "and " + ActivityColumns.CENTRAL_ID + " = '" + centralId + "'";
        }
        final String sql = "WHERE " + ActivityColumns.USER_ID
                + "=? " + centralIdFilter + " and length(trim(ifnull(remote_id, ''))) > 0 ORDER BY "
                + START_TIME + " DESC " + "LIMIT " + n + " OFFSET " + offset;
        return super.query(sql, userId);
    }

    /**
     * BLE:Query synced local activity by userId and type = SPEED_FORCE
     *
     * @param userId    userId
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @return List
     * @throws PersistenceException
     */
    public List<LocalActivity> getBleLocalActivities(String userId, int pageIndex, int pageSize)
            throws PersistenceException {
        final int i = Math.max(0, pageIndex);
        final int n = Math.max(1, pageSize);
        final int offset = i * n;
        final String sql = "WHERE " + ActivityColumns.USER_ID + "=? and " +
                ActivityColumns.TYPE + "=" + ActivityType.SPEED_FORCE.ordinal() +
                " and length(trim(ifnull(remote_id, ''))) > 0 ORDER BY " +
                START_TIME + " DESC " + "LIMIT " + n + " OFFSET " + offset;
        return super.query(sql, userId);
    }

    /**
     * BLE:Query un sync local activity by userId and type = SPEED_FORCE
     * 未上传到服务器
     *
     * @param userId userId
     * @return List
     * @throws PersistenceException
     */
    public List<LocalActivity> getUnSyncBleLocalActivity(String userId)
            throws PersistenceException {
        final String sql = "WHERE " + USER_ID + "=? AND " +
                TYPE + "=" + ActivityType.SPEED_FORCE.ordinal() + " AND " +
                "length(trim(ifnull(remote_id, ''))) = 0 ORDER BY " + START_TIME + " DESC";
        return super.query(sql, userId);
    }

    public List<LocalActivity> getBleLocalActivities(String userId, String deviceId) {
        final String sql = "WHERE " + USER_ID + "=? " +
                "AND " + DEVICE_ID + "=?" +
                "ORDER BY " + START_TIME + " DESC";
        try {
            return super.query(sql, userId, deviceId);
        } catch (PersistenceException e) {
            logger.error("Query ble local activity by userId and deviceId error, " + e);
            return null;
        }
    }

    public List<LocalActivity> getUnSyncBleLocalActivitiesByCentralId(String userId, String centralId) {
        final String sql = "WHERE " + USER_ID + "=? " +
                "AND " + CENTRAL_ID + "=? " +
                "AND " + BLE_DATA_TYPE + "!=2 " +
                "AND " + TOTAL_DISTANCE + ">0 " +
                "AND length(trim(ifnull(remote_id, ''))) = 0 ORDER BY " + START_TIME + " ASC";
        try {
            logger.info("Query unsync ble localActivity by userId = " + userId + " and centralId = " + centralId);
            return super.query(sql, userId, centralId);
        } catch (PersistenceException e) {
            logger.error("Query unsync ble local activity by userId and deviceId error, " + e);
            return null;
        }
    }

    /**
     * 查询LocalActiivty根据时间
     *
     * @param finishTime
     * @param deviceId
     * @return
     */
    public LocalActivity getBleLocalActivity(long finishTime, String deviceId) {
        final String sql = "WHERE " + FINISH_TIME + "=" + finishTime + " AND " +
                DEVICE_ID + "=?";
        try {
            List<LocalActivity> list = super.query(sql, deviceId);
            if (null == list || list.size() <= 0) {
                return null;
            }

            return list.get(0);
        } catch (PersistenceException e) {
            logger.error("Query ble local activity finish time = " + finishTime + ", device id = " + deviceId);
            return null;
        }
    }

}
