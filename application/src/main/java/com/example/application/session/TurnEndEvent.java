package com.example.application.session;

import androidx.annotation.NonNull;

import com.example.core_api.network.model.TurnEndCause;
import com.example.core_api.network.model.TurnEndStatus;

import java.util.Objects;

public class TurnEndEvent {
    @NonNull
    private final String sessionId;
    @NonNull
    private final TurnEndCause cause;
    @NonNull
    private final TurnEndStatus status;
    private final boolean timedOut;

    public TurnEndEvent(@NonNull String sessionId, @NonNull TurnEndCause cause, @NonNull TurnEndStatus status, boolean timedOut) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.cause = Objects.requireNonNull(cause, "cause");
        this.status = Objects.requireNonNull(status, "status");
        this.timedOut = timedOut;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public TurnEndCause getCause() {
        return cause;
    }

    @NonNull
    public TurnEndStatus getStatus() {
        return status;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnEndEvent that = (TurnEndEvent) o;
        return timedOut == that.timedOut && sessionId.equals(that.sessionId) && cause == that.cause && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, cause, status, timedOut);
    }

    @Override
    public String toString() {
        return "TurnEndEvent{"
                + "sessionId='" + sessionId + "'"
                + ", cause=" + cause
                + ", status=" + status
                + ", timedOut=" + timedOut
                + "}"
                ;
    }
}
