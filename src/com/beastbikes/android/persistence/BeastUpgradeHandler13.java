package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by icedan on 16/7/22.
 */
public class BeastUpgradeHandler13 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler13";

    public BeastUpgradeHandler13(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 13);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.CENTRAL_NAME);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.BleDevices.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.BleDevices.BleDevicesColumns.FRAME_ID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.BleDevices.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.BleDevices.BleDevicesColumns.DEVICE_URL);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
