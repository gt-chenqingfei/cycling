package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by icedan on 16/6/28.
 */
public class BeastUpgradeHandler11 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler11";

    public BeastUpgradeHandler11(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 11);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        // 升级Activity表
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.MEDAL_NUM);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
