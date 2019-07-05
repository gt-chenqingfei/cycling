package com.beastbikes.android.persistence;

import java.sql.SQLException;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.modules.cycling.club.dao.entity.Club;
import com.beastbikes.framework.persistence.android.SQLitePersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BeastUpgradeHandler3 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler3";

    public BeastUpgradeHandler3(SQLitePersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 3);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
                        int newVersion) {
        try {
            TableUtils.createTableIfNotExists(cs, Club.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
