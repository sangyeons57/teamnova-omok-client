package com.example.application.port.out.realtime;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

/**
 * Captures acknowledgement payloads for POST_GAME_DECISION requests.
 */
public final class PostGameDecisionAck {

    private final Status status;
    private final PostGameDecisionOption decision;
    private final ErrorReason errorReason;
    private final String rawMessage;

    public PostGameDecisionAck(@NonNull Status status,
                               @NonNull PostGameDecisionOption decision,
                               @NonNull ErrorReason errorReason,
                               @NonNull String rawMessage) {
        this.status = Objects.requireNonNull(status, "status");
        this.decision = Objects.requireNonNull(decision, "decision");
        this.errorReason = Objects.requireNonNull(errorReason, "errorReason");
        this.rawMessage = rawMessage != null ? rawMessage : "";
    }

    @NonNull
    public Status status() {
        return status;
    }

    @NonNull
    public PostGameDecisionOption decision() {
        return decision;
    }

    @NonNull
    public ErrorReason errorReason() {
        return errorReason;
    }

    @NonNull
    public String rawMessage() {
        return rawMessage;
    }

    public boolean isOk() {
        return status == Status.OK;
    }

    @NonNull
    public static PostGameDecisionAck ok(@NonNull PostGameDecisionOption decision, @NonNull String rawMessage) {
        return new PostGameDecisionAck(Status.OK, decision, ErrorReason.NONE, rawMessage);
    }

    @NonNull
    public static PostGameDecisionAck error(@NonNull ErrorReason reason, @NonNull String rawMessage) {
        return new PostGameDecisionAck(Status.ERROR, PostGameDecisionOption.UNKNOWN, reason, rawMessage);
    }

    @NonNull
    public static PostGameDecisionAck unknown(@NonNull String rawMessage) {
        return new PostGameDecisionAck(Status.UNKNOWN, PostGameDecisionOption.UNKNOWN, ErrorReason.UNKNOWN, rawMessage);
    }

    @NonNull
    public static Status parseStatus(@NonNull String label) {
        java.util.Objects.requireNonNull(label, "label");
        String normalized = label.trim().toUpperCase(Locale.US);
        for (Status value : Status.values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        return Status.UNKNOWN;
    }

    @NonNull
    public static ErrorReason parseReason(@NonNull String label) {
        java.util.Objects.requireNonNull(label, "label");
        String normalized = label.trim().toUpperCase(Locale.US);
        for (ErrorReason value : ErrorReason.values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        if (normalized.isEmpty()) {
            return ErrorReason.NONE;
        }
        return ErrorReason.UNKNOWN;
    }

    public enum Status {
        OK,
        ERROR,
        UNKNOWN
    }

    public enum ErrorReason {
        NONE,
        INVALID_PLAYER,
        ALREADY_DECIDED,
        TIME_WINDOW_CLOSED,
        SESSION_CLOSED,
        SESSION_NOT_FOUND,
        INVALID_PAYLOAD,
        UNKNOWN
    }
}
