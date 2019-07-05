package com.beastbikes.framework.persistence;

import java.io.Serializable;

/**
 * {@link PersistentObject} is used by {@link DataAccessObject} to persist into
 * storage
 *
 * @author johnson
 */
public interface PersistentObject extends Serializable {

    /**
     * Returns the unique identifier of this instance
     *
     * @return the unique identifier
     */
    public String getId();

}
