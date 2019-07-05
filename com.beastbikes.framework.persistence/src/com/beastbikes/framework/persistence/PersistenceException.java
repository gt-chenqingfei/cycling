package com.beastbikes.framework.persistence;

public class PersistenceException extends Exception {

    private static final long serialVersionUID = -9106340481863581144L;

    public PersistenceException() {
        super();
    }

    public PersistenceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PersistenceException(String detailMessage) {
        super(detailMessage);
    }

    public PersistenceException(Throwable throwable) {
        super(throwable);
    }

}
