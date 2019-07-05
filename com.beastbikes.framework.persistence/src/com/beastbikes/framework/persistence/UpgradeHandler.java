package com.beastbikes.framework.persistence;

/**
 * The handler for data storage upgrade
 *
 * @author johnson
 */
public interface UpgradeHandler extends Comparable<UpgradeHandler> {

    /**
     * Returns the {@link PersistenceManager}
     *
     * @return
     */
    public PersistenceManager getPersistenceManager();

    /**
     * Returns the target version to upgrade
     *
     * @return the target version
     */
    public int getTargetVersion();

}
