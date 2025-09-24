package com.example.data.exception;

public class LoginRemoteException extends RuntimeException {
    public LoginRemoteException(String message) {
        super(message);
    }

    public LoginRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
