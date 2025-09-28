package com.example.data.exception;

/**
 * Indicates a failure while linking the current account with a Google identity.
 */
public class LinkGoogleRemoteException extends RuntimeException {

    public LinkGoogleRemoteException(String message) {
        super(message);
    }

    public LinkGoogleRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
