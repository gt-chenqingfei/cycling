package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.beastbikes.android.modules.user.dao.entity.LocalAccounts;
import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by caoxiao on 16/2/15.
 */
public class BeastUpgradeHandler7 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler7";

    public BeastUpgradeHandler7(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 7);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        try {
            TableUtils.createTableIfNotExists(cs, LocalAccounts.class);

        } catch (SQLException exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }
    }
}
