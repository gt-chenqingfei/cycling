package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by icedan on 16/4/25.
 */
public class BeastUpgradeHandler10 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler10";

    public BeastUpgradeHandler10(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 10);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        try {
            TableUtils.createTableIfNotExists(cs, BleDevice.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        // 升级Activity表
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.SOURCE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.DEVICE_ID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.SAMPLE_COUNT);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.SAMPLE_RATE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.BLE_DATA_TYPE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        // 升级Activity Sample表
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.Samples.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.Samples.SampleColumns.MAX_SPEED);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.Samples.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.Samples.SampleColumns.CADENCE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.Samples.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.Samples.SampleColumns.MAX_CADENCE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.Samples.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.Samples.SampleColumns.MAX_CARDIAC_RATE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

}
