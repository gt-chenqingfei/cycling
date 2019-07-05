package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by caoxiao on 16/3/16.
 */
public class BeastUpgradeHandler8 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler8";

    public BeastUpgradeHandler8(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 8);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        //friend表升级
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Friend.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Friend.FriendColumns.FRIEND_REMARKS);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        //Activity表升级
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.ActivityColumns.IS_PRIVATE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
