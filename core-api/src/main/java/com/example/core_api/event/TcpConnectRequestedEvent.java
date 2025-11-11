package com.example.core_api.event;

/**
 * Signals that the TCP layer should ensure a connection exists (and re-auth if needed).
 */
public final class TcpConnectRequestedEvent implements AppEvent {

    public static final TcpConnectRequestedEvent INSTANCE = new TcpConnectRequestedEvent();

    private TcpConnectRequestedEvent() {
    }
}
