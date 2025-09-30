package com.example.data.exception;

public class TcpRemoteException extends RuntimeException {
    public TcpRemoteException(String message) {
        super(message);
    }

    public TcpRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
