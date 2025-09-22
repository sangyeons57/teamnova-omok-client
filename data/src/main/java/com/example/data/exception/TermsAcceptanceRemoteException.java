package com.example.data.exception;

/**
 * Signals a failure while attempting to accept terms through the remote API.
 */
public class TermsAcceptanceRemoteException extends RuntimeException {

    public TermsAcceptanceRemoteException(String message) {
        super(message);
    }

    public TermsAcceptanceRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
