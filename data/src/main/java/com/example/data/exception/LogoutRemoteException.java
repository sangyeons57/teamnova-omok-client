package com.example.data.exception;

public class LogoutRemoteException extends RuntimeException {
    public LogoutRemoteException(String message) {
        super(message);
    }

    public LogoutRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
