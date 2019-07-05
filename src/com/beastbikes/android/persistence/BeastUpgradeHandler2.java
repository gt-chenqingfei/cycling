package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.persistence.BeastStore.Activities;
import com.beastbikes.android.persistence.BeastStore.Activities.ActivityColumns;
import com.beastbikes.framework.persistence.android.SQLitePersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;

public class BeastUpgradeHandler2 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler2";

    public BeastUpgradeHandler2(SQLitePersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 2);// 版本号 ＋1
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
                        int newVersion) {
        try {
            db.execSQL("ALTER TABLE " + Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + ActivityColumns.ACTIVITY_URL);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + ActivityColumns.FAKE + " BIGINT DEFAULT 0;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + ActivityColumns.SPEED + " BIGINT DEFAULT 0;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

//		try {
//			db.execSQL("ALTER TABLE " + Activities.CONTENT_CATEGORY
//					+ " ADD COLUMN " + ActivityColumns.SCENERY_URL);
//		} catch (android.database.SQLException e) {
//			Log.e(TAG, e.getMessage(), e);
//		}
//		try {
//			db.execSQL("ALTER TABLE " + Activities.CONTENT_CATEGORY
//					+ " ADD COLUMN " + ActivityColumns.LOCAL_SCENERY_URL);
//		} catch (android.database.SQLException e) {
//			Log.e(TAG, e.getMessage(), e);
//		}
    }
}
