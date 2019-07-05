package com.beastbikes.android.persistence;

import java.sql.SQLException;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.persistence.BeastStore.Activities.Samples;
import com.beastbikes.framework.persistence.android.SQLitePersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BeastUpgradeHandler1 extends ORMLiteUpgradeHandler {

    private static final String TAG = "SQLiteUpgradeHandler1";

    public BeastUpgradeHandler1(SQLitePersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 1);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
                        int newVersion) {
        try {
            db.execSQL("ALTER TABLE " + Samples.CONTENT_CATEGORY
                    + " RENAME TO " + Samples.CONTENT_CATEGORY + "_old;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, LocalActivitySample.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("INSERT INTO " + Samples.CONTENT_CATEGORY
                    + " SELECT * FROM " + Samples.CONTENT_CATEGORY + "_old;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("DROP TABLE " + Samples.CONTENT_CATEGORY + "_old;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
