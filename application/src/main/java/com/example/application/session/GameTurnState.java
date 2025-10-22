package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Immutable snapshot describing the current turn progression.
 */
public final class GameTurnState {

    private final boolean active;
    private final String currentPlayerId;
    private final int remainingSeconds;

    private GameTurnState(boolean active, @Nullable String currentPlayerId, int remainingSeconds) {
        this.active = active;
        this.currentPlayerId = currentPlayerId;
        this.remainingSeconds = Math.max(remainingSeconds, 0);
    }

    public static GameTurnState idle() {
        return new GameTurnState(false, null, 0);
    }

    public static GameTurnState active(@NonNull String currentPlayerId, int remainingSeconds) {
        return new GameTurnState(true, Objects.requireNonNull(currentPlayerId, "currentPlayerId"), remainingSeconds);
    }

    private static GameTurnState idleWithSeconds(int seconds) {
        return new GameTurnState(false, null, seconds);
    }

    public boolean isActive() {
        return active;
    }

    @Nullable
    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    @NonNull
    public GameTurnState withRemainingSeconds(int seconds) {
        return new GameTurnState(active, currentPlayerId, seconds);
    }

    @NonNull
    public GameTurnState deactivate() {
        return idleWithSeconds(remainingSeconds);
    }

    // normalize and ensureActive will need to be re-evaluated in GameInfoStore
    // as they relied on participantCount and currentIndex.
    // For now, they will return the current state or an idle state.
    @NonNull
    public GameTurnState normalize() {
        if (!active) {
            return idleWithSeconds(remainingSeconds);
        }
        return this; // No normalization needed without participantCount
    }

    @NonNull
    public GameTurnState ensureActive() {
        if (!active) {
            // If it's not active, and we need to ensure it's active, we need a player ID.
            // This logic should ideally be handled by GameInfoStore with actual player data.
            // For now, returning an idle state or throwing an error.
            throw new IllegalStateException("Cannot ensure active without a player ID. This should be handled by GameInfoStore.");
        }
        return this;
    }

    @NonNull
    public GameTurnState advance() {
        // The logic for advancing a turn (determining the next player) should be handled by GameInfoStore
        // as it requires knowledge of all participants. GameTurnState no longer holds this information.
        return idle(); // For now, return idle, GameInfoStore will determine the next active state.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameTurnState that = (GameTurnState) o;
        return active == that.active
                && remainingSeconds == that.remainingSeconds
                && Objects.equals(currentPlayerId, that.currentPlayerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, currentPlayerId, remainingSeconds);
    }

    @Override
    public String toString() {
        return "GameTurnState{"
                + "active=" + active
                + ", currentPlayerId='" + currentPlayerId + '\''
                + ", remainingSeconds=" + remainingSeconds
                + '}';
    }
}

