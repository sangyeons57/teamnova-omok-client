package com.example.core_api.event;

/**
 * Signals that the client should (re)send the TCP AUTH handshake
 * using the currently stored access token.
 */
public final class TcpAuthRequestedEvent implements AppEvent {

    public static final TcpAuthRequestedEvent INSTANCE = new TcpAuthRequestedEvent();

    private TcpAuthRequestedEvent() {
    }
}
