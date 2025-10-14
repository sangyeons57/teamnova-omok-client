package com.example.application.port.out.realtime;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

/**
 * Value object describing the outcome of a PLACE_STONE request.
 */
public record PlaceStoneResponse(@NonNull Status status,
                                 int turnNumber,
                                 @NonNull String rawMessage) {

    public PlaceStoneResponse {
        Objects.requireNonNull(status, "status");
        rawMessage = rawMessage != null ? rawMessage : "";
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    @NonNull
    public static PlaceStoneResponse success(int turnNumber, @NonNull String rawMessage) {
        return new PlaceStoneResponse(Status.SUCCESS, turnNumber, rawMessage);
    }

    @NonNull
    public static PlaceStoneResponse failure(@NonNull Status status,
                                             int turnNumber,
                                             @NonNull String rawMessage) {
        if (status == Status.SUCCESS) {
            throw new IllegalArgumentException("failure() cannot be called with SUCCESS status");
        }
        return new PlaceStoneResponse(status, turnNumber, rawMessage);
    }

    @NonNull
    public static Status parseStatus(@NonNull String label) {
        Objects.requireNonNull(label, "label");
        String normalized = label.trim().toUpperCase(Locale.US);
        for (Status s : Status.values()) {
            if (s.name().equals(normalized)) {
                return s;
            }
        }
        return Status.UNKNOWN;
    }

    public enum Status {
        SUCCESS,
        INVALID,
        GAME_NOT_STARTED,
        OUT_OF_TURN,
        OUT_OF_BOUNDS,
        CELL_OCCUPIED,
        UNKNOWN
    }
}
