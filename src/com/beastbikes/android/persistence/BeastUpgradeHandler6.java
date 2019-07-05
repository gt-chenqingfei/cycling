package com.beastbikes.android.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beastbikes.android.modules.cycling.grid.dao.entity.Grid;
import com.beastbikes.framework.persistence.PersistenceManager;
import com.beastbikes.framework.persistence.android.ormlite.ORMLiteUpgradeHandler;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class BeastUpgradeHandler6 extends ORMLiteUpgradeHandler {

    private static final String TAG = "BeastUpgradeHandler6";

    public BeastUpgradeHandler6(PersistenceManager persistenceManager) {
        super(persistenceManager, (1 * 0xff) + 6);
    }

    @Override
    public void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
                        int newVersion) {

        try {
            TableUtils.createTableIfNotExists(cs, Grid.class);

        } catch (SQLException exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }

        // v2.0.0 添加的新字段，表示当前定位的时间戳
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Activities.Samples.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Activities.Samples.SampleColumns.CURR_TIME);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        //user表升级
        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.USERID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.USERINTID + " BIGINT DEFAULT 0;");
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.UPDATEDAT);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.CREATEDAT);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.CLUBID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.OBJECTID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.ISOK);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.GRIDNUM);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.EDITED);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.WEEKLYDISTANCE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.SAMEGRID);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.MONTHLYDISTANCE);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.CLUBNAME);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.AVATAR);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            db.execSQL("ALTER TABLE " + BeastStore.Users.CONTENT_CATEGORY
                    + " ADD COLUMN " + BeastStore.Users.UserColumns.BIRTHDAY);
        } catch (android.database.SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
