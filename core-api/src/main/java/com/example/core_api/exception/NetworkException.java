package com.example.core_api.exception;

public class NetworkException extends Exception {
    public NetworkException(String message) {
        this(message, null);
    }
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    static class Timeout extends NetworkException{ public Timeout( Throwable cause) { super("timeout", cause); } }
    static class Connection extends NetworkException{ public Connection( Throwable cause) { super("connection", cause); } }
    static class Tls extends NetworkException{ public Tls( Throwable cause) { super("tls", cause); } }
    static class Canceled extends NetworkException{ public Canceled() { super("canceled"); } }
    static class Unknown extends NetworkException{ public Unknown(Throwable cause) { super("unknown", cause); } }
}
