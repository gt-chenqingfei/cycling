package com.beastbikes.framework.persistence.android;

import android.database.sqlite.SQLiteDatabase;

import com.beastbikes.framework.persistence.AbstractUpgradeHandler;

public abstract class SQLiteUpgradeHandler extends AbstractUpgradeHandler {

    public SQLiteUpgradeHandler(SQLitePersistenceManager persistenceManager,
                                int targetVersion) {
        super(persistenceManager, targetVersion);
    }

    @Override
    public SQLitePersistenceManager getPersistenceManager() {
        return (SQLitePersistenceManager) super.getPersistenceManager();
    }

    public abstract void upgrade(SQLiteDatabase db, int oldVersion,
                                 int newVersion);

}
