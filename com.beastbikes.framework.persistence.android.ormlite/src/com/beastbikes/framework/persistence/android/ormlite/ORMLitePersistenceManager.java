package com.beastbikes.framework.persistence.android.ormlite;

import com.beastbikes.framework.persistence.PersistentObject;
import com.beastbikes.framework.persistence.android.SQLitePersistenceManager;

public interface ORMLitePersistenceManager extends SQLitePersistenceManager {

    /**
     * Returns the upgrade handlers
     *
     * @return the upgrade handlers
     */
    public <T extends PersistentObject> ORMLiteAccessObject<T> getDataAccessObject(
            Class<T> clazz);

}
