package com.example.data.common.exception;

/**
 * Signals that the hello world endpoint could not be reached or returned an unexpected response.
 */
public class HelloWorldRemoteException extends RuntimeException {

    public HelloWorldRemoteException(String message) {
        super(message);
    }

    public HelloWorldRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
