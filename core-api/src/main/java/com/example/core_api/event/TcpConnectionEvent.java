package com.example.core_api.event;

import androidx.annotation.Nullable;

/**
 * Broadcasts TCP connection status changes so higher layers can react (e.g., re-authenticate).
 */
public final class TcpConnectionEvent implements AppEvent {

    public enum Status {
        CONNECTED,
        DISCONNECTED
    }

    private final Status status;
    private final boolean reconnected;
    @Nullable
    private final Throwable cause;

    private TcpConnectionEvent(Status status, boolean reconnected, @Nullable Throwable cause) {
        this.status = status;
        this.reconnected = reconnected;
        this.cause = cause;
    }

    public static TcpConnectionEvent connected(boolean reconnected) {
        return new TcpConnectionEvent(Status.CONNECTED, reconnected, null);
    }

    public static TcpConnectionEvent disconnected(@Nullable Throwable cause) {
        return new TcpConnectionEvent(Status.DISCONNECTED, false, cause);
    }

    public Status status() {
        return status;
    }

    public boolean isConnected() {
        return status == Status.CONNECTED;
    }

    public boolean isReconnected() {
        return reconnected;
    }

    @Nullable
    public Throwable cause() {
        return cause;
    }
}
