package com.example.application.session.postgame;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Represents the result of a finished game for a single participant.
 */
public enum GameOutcomeResult {
    WIN,
    LOSS,
    DRAW,
    UNKNOWN;

    @NonNull
    public static GameOutcomeResult fromLabel(@NonNull String label) {
        java.util.Objects.requireNonNull(label, "label");
        String normalized = label.trim().toUpperCase(Locale.US);
        for (GameOutcomeResult value : values()) {
            if (value != UNKNOWN && value.name().equals(normalized)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
