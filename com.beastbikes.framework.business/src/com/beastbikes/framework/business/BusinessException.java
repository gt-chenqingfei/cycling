package com.beastbikes.framework.business;

/**
 * The exception of business transaction
 *
 * @author johnson
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 7000835461642534201L;

    public BusinessException() {
    }

    public BusinessException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BusinessException(String detailMessage) {
        super(detailMessage);
    }

    public BusinessException(Throwable throwable) {
        super(throwable);
    }

}
