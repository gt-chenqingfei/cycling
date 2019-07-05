package com.beastbikes.android.authentication;

public class AuthenticationException extends Exception {

    private static final long serialVersionUID = -3821984500386448955L;

    private final int errorNumber;

    public AuthenticationException(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public AuthenticationException(int errorNumber, String detailMessage,
                                   Throwable throwable) {
        super(detailMessage, throwable);
        this.errorNumber = errorNumber;
    }

    public AuthenticationException(int errorNumber, String detailMessage) {
        super(detailMessage);
        this.errorNumber = errorNumber;
    }

    public AuthenticationException(int errorNumber, Throwable throwable) {
        super(throwable);
        this.errorNumber = errorNumber;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

}
