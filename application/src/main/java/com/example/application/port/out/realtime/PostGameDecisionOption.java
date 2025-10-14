package com.example.application.port.out.realtime;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Enumerates the post-game decision options accepted by the realtime server.
 */
public enum PostGameDecisionOption {
    REMATCH,
    LEAVE,
    UNKNOWN;

    @NonNull
    public static PostGameDecisionOption fromLabel(@NonNull String label) {
        java.util.Objects.requireNonNull(label, "label");
        String normalized = label.trim().toUpperCase(Locale.US);
        for (PostGameDecisionOption option : values()) {
            if (option != UNKNOWN && option.name().equals(normalized)) {
                return option;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return name();
    }
}
