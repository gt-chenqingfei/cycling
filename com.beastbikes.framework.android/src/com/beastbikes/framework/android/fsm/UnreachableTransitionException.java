package com.beastbikes.framework.android.fsm;

public class UnreachableTransitionException extends Exception {

    private static final long serialVersionUID = 6122844439479059878L;

    public UnreachableTransitionException() {
    }

    public UnreachableTransitionException(String detailMessage,
                                          Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UnreachableTransitionException(String detailMessage) {
        super(detailMessage);
    }

    public UnreachableTransitionException(Throwable throwable) {
        super(throwable);
    }

}
