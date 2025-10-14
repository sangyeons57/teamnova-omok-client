package com.example.application.session.postgame;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Maps a user to their recorded game outcome.
 */
public final class GameOutcome {

    private final String userId;
    private final GameOutcomeResult result;

    public GameOutcome(@NonNull String userId,
                       @NonNull GameOutcomeResult result) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.result = Objects.requireNonNull(result, "result");
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public GameOutcomeResult getResult() {
        return result;
    }
}
