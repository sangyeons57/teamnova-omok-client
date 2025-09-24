package com.example.core.event;

public final class SessionInvalidatedEvent implements AppEvent {
    public enum Reason {
        TOKEN_REFRESH_FAILURE,
        LOGOUT
    }

    private final Reason reason;

    public SessionInvalidatedEvent(Reason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("reason == null");
        }
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
