package com.amdocs.telecom.exception;

public class AuthenticationException extends TSATMSException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
