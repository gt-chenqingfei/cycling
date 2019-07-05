package com.beastbikes.framework.persistence;


public abstract class AbstractUpgradeHandler implements UpgradeHandler {

    private final PersistenceManager persistenceManager;

    private final int targetVersion;

    public AbstractUpgradeHandler(PersistenceManager persistenceManager,
                                  int targetVersion) {
        this.persistenceManager = persistenceManager;
        this.targetVersion = targetVersion;
    }

    @Override
    public final int getTargetVersion() {
        return this.targetVersion;
    }

    @Override
    public int compareTo(UpgradeHandler another) {
        return this.getTargetVersion() - another.getTargetVersion();
    }

    @Override
    public PersistenceManager getPersistenceManager() {
        return this.persistenceManager;
    }

}
