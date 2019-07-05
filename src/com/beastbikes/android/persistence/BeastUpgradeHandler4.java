package com.beastbikes.android.persistence;

import java.sql.SQLException;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.modules.social.im.dao.entity.Friend;
import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BeastUpgradeHandler4 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler4";

    public BeastUpgradeHandler4(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 4);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
                        int newVersion) {
        try {
            TableUtils.createTableIfNotExists(cs, Friend.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
