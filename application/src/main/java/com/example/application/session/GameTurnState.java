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
    private final long startAt;
    private final long endAt;
    private final int turnNumber;
    private final int roundNumber;
    private final int positionInRound;
    private final int playerIndex;

    private GameTurnState(boolean active,
                          @Nullable String currentPlayerId,
                          int remainingSeconds,
                          long startAt,
                          long endAt,
                          int turnNumber,
                          int roundNumber,
                          int positionInRound,
                          int playerIndex) {
        this.active = active;
        this.currentPlayerId = currentPlayerId;
        this.remainingSeconds = Math.max(remainingSeconds, 0);
        this.startAt = startAt;
        this.endAt = endAt;
        this.turnNumber = Math.max(0, turnNumber);
        this.roundNumber = Math.max(0, roundNumber);
        this.positionInRound = Math.max(0, positionInRound);
        this.playerIndex = playerIndex;
    }

    public static GameTurnState idle() {
        return new GameTurnState(false, null, 0, 0L, 0L, 0, 0, 0, -1);
    }

    public static GameTurnState active(@NonNull String currentPlayerId,
                                       int remainingSeconds,
                                       long startAt,
                                       long endAt,
                                       int turnNumber,
                                       int roundNumber,
                                       int positionInRound,
                                       int playerIndex) {
        return new GameTurnState(true,
                Objects.requireNonNull(currentPlayerId, "currentPlayerId"),
                remainingSeconds,
                startAt,
                endAt,
                turnNumber,
                roundNumber,
                positionInRound,
                playerIndex);
    }

    private static GameTurnState idleWithSeconds(int seconds) {
        return new GameTurnState(false, null, seconds, 0L, 0L, 0, 0, 0, -1);
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

    public long getStartAt() {
        return startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getPositionInRound() {
        return positionInRound;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    @NonNull
    public GameTurnState withRemainingSeconds(int seconds) {
        return new GameTurnState(active, currentPlayerId, seconds, startAt, endAt,
                turnNumber, roundNumber, positionInRound, playerIndex);
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
                && startAt == that.startAt
                && endAt == that.endAt
                && turnNumber == that.turnNumber
                && roundNumber == that.roundNumber
                && positionInRound == that.positionInRound
                && playerIndex == that.playerIndex
                && Objects.equals(currentPlayerId, that.currentPlayerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, currentPlayerId, remainingSeconds, startAt, endAt,
                turnNumber, roundNumber, positionInRound, playerIndex);
    }

    @Override
    public String toString() {
        return "GameTurnState{"
                + "active=" + active
                + ", currentPlayerId='" + currentPlayerId + '\''
                + ", remainingSeconds=" + remainingSeconds
                + ", startAt=" + startAt
                + ", endAt=" + endAt
                + ", turnNumber=" + turnNumber
                + ", roundNumber=" + roundNumber
                + ", positionInRound=" + positionInRound
                + ", playerIndex=" + playerIndex
                + '}';
    }
}

