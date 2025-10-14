package com.example.application.session.postgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Represents the reason why a participant became inactive after the game session.
 */
public enum PlayerDisconnectReason {
    DISCONNECTED,
    LEFT,
    UNKNOWN;

    @NonNull
    public static PlayerDisconnectReason fromLabel(@Nullable String label) {
        if (label == null || label.isEmpty()) {
            return UNKNOWN;
        }
        return switch (label.toUpperCase()) {
            case "DISCONNECTED" -> DISCONNECTED;
            case "LEFT" -> LEFT;
            default -> UNKNOWN;
        };
    }
}
