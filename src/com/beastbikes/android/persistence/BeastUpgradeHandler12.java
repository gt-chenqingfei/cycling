package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by icedan on 16/7/22.
 */
public class BeastUpgradeHandler12 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler12";

    public BeastUpgradeHandler12(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 12);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        // 升级BleDevice
        try {
            db.execSQL("ALTER TABLE " + BeastStore.BleDevices.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.BleDevices.BleDevicesColumns.MAC_ADDRESS);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.BleDevices.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.BleDevices.BleDevicesColumns.GUARANTEE_TIME);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        // 升级LocalActivity
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.CADENCE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.CADENCE_MAX);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.CARDIAC_RATE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.CENTRAL_ID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        //升级LocalUser
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.SPEEDX_ID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
