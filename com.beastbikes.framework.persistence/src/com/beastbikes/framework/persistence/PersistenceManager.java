package com.beastbikes.framework.persistence;

/**
 * {@link PersistenceManager} is used for data persistence
 *
 * @author johnson
 */
public interface PersistenceManager {

    /**
     * Returns the upgrade handlers
     *
     * @return the upgrade handlers
     */
    public UpgradeHandler[] getUpgradeHandlers();

}
