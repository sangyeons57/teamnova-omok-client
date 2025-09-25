package com.example.data.exception;

public class UserDataRemoteException extends RuntimeException {
    public UserDataRemoteException(String message) {
        super(message);
    }

    public UserDataRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}

