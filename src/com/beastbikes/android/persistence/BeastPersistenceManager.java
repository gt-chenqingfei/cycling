package com.beastbikes.android.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;
import com.beastbikes.android.modules.cycling.club.dao.entity.Club;
import com.beastbikes.android.modules.cycling.grid.dao.entity.Grid;
import com.beastbikes.android.modules.social.im.dao.entity.Friend;
import com.beastbikes.android.modules.user.dao.entity.LocalAccounts;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.framework.persistence.android.ormlite.ORMLitePersistenceSupport;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class BeastPersistenceManager extends ORMLitePersistenceSupport {

    private static final String TAG = "BeastPersistenceManager";

    private static final String DATABASE_NAME = "beast.sqlite";

    /**
     * old db version = (1 * 0xff) + 1 v1.7.0
     * db version = (1 * 0xff) + 2 v1.7.1
     * db version = (1 * 0xff) + 3
     * db version = (1 * 0xff) + 4 apk version = v1.8.0, v1.8.1, v1.9.0
     * db version = (1 * 0xff) + 4 apk version = v1.8.0
     * db version = (1 * 0xff) + 5 apk version = v1.10.0
     * db version = (1 * 0xff) + 6 apk version = v2.0.0
     * db version = (1 * 0xff) + 7 apk version = v2.0.0 fix
     * db version = (1 * 0xff) + 8 apk version = v2.1.0
     * db version = (1 * 0xff) + 9 apk version = v2.2.0
     * db version = (1 * 0xff) + 10 apk version = v2.3.0 (ble)
     * db version = (1 * 0xff) + 11 apk version = v2.4.0
     * db version = (1 * 0xff) + 12 apk version = v2.4.1
     * db version = (1 * 0xff) + 13 apk version = v2.4.2
     */
    private static final int DATABASE_VERSION = (1 * 0xff) + 13;

    private final ORMLiteUpgradeHandler[] handlers = {
    /* 1 */new BeastUpgradeHandler1(this),
    /* 2 */new BeastUpgradeHandler2(this),
    /* 3 */new BeastUpgradeHandler3(this),
    /* 4 */new BeastUpgradeHandler4(this),
    /* 5 */new BeastUpgradeHandler5(this),
    /* 6 */new BeastUpgradeHandler6(this),
    /* 7 */new BeastUpgradeHandler7(this),
    /* 8 */new BeastUpgradeHandler8(this),
    /* 9 */new BeastUpgradeHandler9(this),
    /* 10 */new BeastUpgradeHandler10(this),
    /* 11 */new BeastUpgradeHandler11(this),
    /* 12 */new BeastUpgradeHandler12(this),
    /* 13 */new BeastUpgradeHandler13(this),};


    public BeastPersistenceManager(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
        try {
            TableUtils.createTableIfNotExists(cs, LocalUser.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, LocalActivity.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, LocalActivitySample.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, Club.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, Friend.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, Grid.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, LocalAccounts.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            TableUtils.createTableIfNotExists(cs, BleDevice.class);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public ORMLiteUpgradeHandler[] getUpgradeHandlers() {
        return this.handlers;
    }

}
