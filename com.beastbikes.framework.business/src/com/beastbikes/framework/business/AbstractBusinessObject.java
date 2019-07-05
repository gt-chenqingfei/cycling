package com.beastbikes.framework.business;

/**
 * An implementation of interface {@link BusinessObject}
 *
 * @author johnson
 */
public abstract class AbstractBusinessObject implements BusinessObject {

    private final BusinessContext context;

    /**
     * Create an instance with the specified {@link BusinessContext}
     *
     * @param context The business context
     */
    public AbstractBusinessObject(BusinessContext context) {
        this.context = context;
    }

    @Override
    public BusinessContext getContext() {
        return this.context;
    }

}
