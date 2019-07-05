package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;


/**
 * Created by caoxiao on 16/4/11.
 */
public class BeastUpgradeHandler9 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler9";

    public BeastUpgradeHandler9(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 9);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        //club表升级
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Clubs.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Clubs.ClubsColumns.TYPE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Clubs.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Clubs.ClubsColumns.LINKTO);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        // user表升级
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.FANS_NUM);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.FOLLOWER_NUM);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.FOLLOW_STATUS);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
