package com.beastbikes.framework.persistence.android.ormlite;

import android.database.sqlite.SQLiteDatabase;

import com.beastbikes.framework.persistence.AbstractUpgradeHandler;
import com.beastbikes.framework.persistence.PersistenceManager;
import com.j256.ormlite.support.ConnectionSource;

public abstract class ORMLiteUpgradeHandler extends AbstractUpgradeHandler {

    public ORMLiteUpgradeHandler(PersistenceManager persistenceManager,
                                 int targetVersion) {
        super(persistenceManager, targetVersion);
    }

    @Override
    public ORMLitePersistenceManager getPersistenceManager() {
        return (ORMLitePersistenceManager) super.getPersistenceManager();
    }

    public abstract void upgrade(SQLiteDatabase db, ConnectionSource cs,
                                 int oldVersion, int newVersion);

}
