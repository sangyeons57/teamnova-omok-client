package com.example.data.exception;

public class RankingRemoteException extends RuntimeException {
    public RankingRemoteException(String message) {
        super(message);
    }

    public RankingRemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}

