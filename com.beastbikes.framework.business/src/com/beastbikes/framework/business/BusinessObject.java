package com.beastbikes.framework.business;

/**
 * {@link BusinessObject} is in charge of business transaction
 *
 * @author johnson
 */
public interface BusinessObject {

    /**
     * Returns the business context
     *
     * @return the business context
     */
    public BusinessContext getContext();

}
