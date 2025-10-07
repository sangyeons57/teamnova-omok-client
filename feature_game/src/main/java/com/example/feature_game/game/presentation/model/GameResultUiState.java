package com.example.feature_game.game.presentation.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Aggregates the information shown on the result dialog.
 */
public final class GameResultUiState {

    private final GameResultOutcome outcome;
    private final long durationMillis;
    private final int turnCount;
    private final int rematchVotes;
    private final boolean rematchRequested;

    public GameResultUiState(@NonNull GameResultOutcome outcome,
                             long durationMillis,
                             int turnCount,
                             int rematchVotes,
                             boolean rematchRequested) {
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.durationMillis = durationMillis;
        this.turnCount = turnCount;
        this.rematchVotes = rematchVotes;
        this.rematchRequested = rematchRequested;
    }

    @NonNull
    public GameResultOutcome getOutcome() {
        return outcome;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public int getRematchVotes() {
        return rematchVotes;
    }

    public boolean isRematchRequested() {
        return rematchRequested;
    }

    public GameResultUiState withRematchVotes(int votes, boolean requested) {
        return new GameResultUiState(outcome, durationMillis, turnCount, votes, requested);
    }
}
