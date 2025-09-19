package com.example.data.common.exception;

/**
 * Signals an error occurred while attempting to create an account through the remote API.
 */
public class GuestSignupRemoteException extends RuntimeException {

    public GuestSignupRemoteException(String message) {
        super(message);
    }

    public GuestSignupRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
