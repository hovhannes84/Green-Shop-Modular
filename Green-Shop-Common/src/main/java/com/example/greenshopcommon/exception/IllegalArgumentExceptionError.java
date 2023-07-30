package com.example.greenshopcommon.exception;

public class IllegalArgumentExceptionError extends Exception{
    public IllegalArgumentExceptionError() {
    }

    public IllegalArgumentExceptionError(String message) {
        super(message);
    }

    public IllegalArgumentExceptionError(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentExceptionError(Throwable cause) {
        super(cause);
    }

    public IllegalArgumentExceptionError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
