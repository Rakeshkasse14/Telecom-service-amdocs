package com.amdocs.telecom.exception;

public class TSATMSException extends Exception {
    public TSATMSException(String message) {
        super(message);
    }

    public TSATMSException(String message, Throwable cause) {
        super(message, cause);
    }
}
