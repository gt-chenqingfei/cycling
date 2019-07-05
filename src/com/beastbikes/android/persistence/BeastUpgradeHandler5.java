package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by caoxiao on 15/12/27.
 */
public class BeastUpgradeHandler5 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler5";

    public BeastUpgradeHandler5(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 5);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
                        int newVersion) {
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Clubs.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Clubs.ClubsColumns.ISPRIVATE + " BIGINT DEFAULT 0;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Clubs.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Clubs.ClubsColumns.CLUB_LEVEL + " BIGINT DEFAULT 0;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


}
