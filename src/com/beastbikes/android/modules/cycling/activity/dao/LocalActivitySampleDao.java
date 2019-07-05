package com.beastbikes.android.modules.cycling.activity.dao;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.persistence.BeastStore.Activities.Samples.SampleColumns;
import com.beastbikes.framework.persistence.PersistenceException;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteAccessObject;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;

import java.util.List;

public class LocalActivitySampleDao extends
        ORMLiteAccessObject<LocalActivitySample> implements SampleColumns {

    public LocalActivitySampleDao(ORMLitePersistenceSupport ps) {
        super(ps, LocalActivitySample.class);
    }

    public LocalActivitySample getLatestLocalActivitySample(String activityId)
            throws PersistenceException {
        final String sql = "WHERE " + ACTIVITY_ID + "=? ORDER BY " + TIME
                + " DESC LIMIT 1";
        final List<LocalActivitySample> samples = super.query(sql, activityId);
        if (null == samples || samples.isEmpty())
            return null;

        return samples.get(0);
    }

    public List<LocalActivitySample> getLocalActivitySamples(String activityId)
            throws PersistenceException {
        final String sql = "WHERE " + ACTIVITY_ID + "=? ORDER BY " + TIME
                + " DESC LIMIT 1";
        final List<LocalActivitySample> samples = super.query(sql, activityId);
        if (null == samples || samples.isEmpty())
            return null;

        return samples;
    }

    public List<LocalActivitySample> getLocalActivitySamples(
            String activityId, int pageIndex, int pageSize)
            throws PersistenceException {
        final int i = Math.max(0, pageIndex);
        final int n = Math.max(1, pageSize);
        final int offset = i * n;
        final String sql = "WHERE " + SampleColumns.ACTIVITY_ID
                + "=? ORDER BY " + DISTANCE + " ASC "
                + "LIMIT " + n + " OFFSET " + offset;
        return super.query(sql, activityId);
    }

    public List<LocalActivitySample> getUnsyncedLocalActivitySamples(
            String activityId, int pageIndex, int pageSize)
            throws PersistenceException {
        final int i = Math.max(0, pageIndex);
        final int n = Math.max(1, pageSize);
        final int offset = i * n;
        final String sql = "WHERE " + SampleColumns.ACTIVITY_ID
                + "=? and length(trim(ifnull(remote_id, ''))) = 0 and velocity >= 0 and velocity < 1.79769313486231570e+308 ORDER BY " + TIME + " ASC "
                + "LIMIT " + n + " OFFSET " + offset;
        return super.query(sql, activityId);
    }

    public long countOfActivitySamples(String activityId) {
        try {
            return count(SampleColumns.ACTIVITY_ID + "=?", activityId);
        } catch (PersistenceException e) {
            return 0;
        }
    }

}
