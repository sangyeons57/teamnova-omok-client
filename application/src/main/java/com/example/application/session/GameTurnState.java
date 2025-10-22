package com.example.application.session;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Immutable snapshot describing the current turn progression.
 */
public final class GameTurnState {

    private final boolean active;
    private final int currentIndex;
    private final int remainingSeconds;
    private final int round;
    private final int positionInRound;

    private GameTurnState(boolean active, int currentIndex, int remainingSeconds, int round, int positionInRound) {
        this.active = active;
        this.currentIndex = active ? Math.max(currentIndex, 0) : -1;
        this.remainingSeconds = Math.max(remainingSeconds, 0);
        this.round = Math.max(round, 0);
        this.positionInRound = Math.max(positionInRound, 0);
    }

    public static GameTurnState idle() {
        return new GameTurnState(false, -1, 0, 0, 0);
    }

    public static GameTurnState active(int currentIndex, int remainingSeconds, int round, int positionInRound) {
        return new GameTurnState(true, currentIndex, remainingSeconds, round, positionInRound);
    }

    private static GameTurnState idleWithSeconds(int seconds) {
        return new GameTurnState(false, -1, seconds, 0, 0);
    }

    public boolean isActive() {
        return active;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public int getRound() {
        return round;
    }

    public int getPositionInRound() {
        return positionInRound;
    }

    @NonNull
    public GameTurnState withCurrentIndex(int index, int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        return new GameTurnState(true, normalizeIndex(index, participantCount), remainingSeconds, round, positionInRound);
    }

    @NonNull
    public GameTurnState withRemainingSeconds(int seconds) {
        return new GameTurnState(active, currentIndex, seconds, round, positionInRound);
    }

    @NonNull
    public GameTurnState deactivate() {
        return idleWithSeconds(remainingSeconds);
    }

    @NonNull
    public GameTurnState normalize(int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        if (!active) {
            return idleWithSeconds(remainingSeconds);
        }
        return new GameTurnState(true, normalizeIndex(currentIndex, participantCount), remainingSeconds, round, positionInRound);
    }

    @NonNull
    public GameTurnState ensureActive(int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        if (!active) {
            return new GameTurnState(true, 0, remainingSeconds, 0, 0); // Assuming round and position start at 0 for a new active state
        }
        return new GameTurnState(true, normalizeIndex(currentIndex, participantCount), remainingSeconds, round, positionInRound);
    }

    @NonNull
    public GameTurnState advance(int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        if (!active) {
            return new GameTurnState(true, 0, remainingSeconds, 0, 0); // Assuming round and position start at 0 for a new active state
        }
        int nextIndex = normalizeIndex(currentIndex + 1, participantCount);
        int nextPositionInRound = positionInRound + 1;
        int nextRound = round;

        // If the position in round wraps around, increment the round
        // This assumes that a full cycle of participants completes a round.
        if (nextIndex == 0 && participantCount > 0 && currentIndex == participantCount -1) { // Check if we just wrapped from the last player to the first
             nextRound++;
             nextPositionInRound = 0; // Reset position in round for the new round
        }


        return new GameTurnState(true, nextIndex, remainingSeconds, nextRound, nextPositionInRound);
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
                && currentIndex == that.currentIndex
                && remainingSeconds == that.remainingSeconds
                && round == that.round
                && positionInRound == that.positionInRound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, currentIndex, remainingSeconds, round, positionInRound);
    }

    @Override
    public String toString() {
        return "GameTurnState{"
                + "active=" + active
                + ", currentIndex=" + currentIndex
                + ", remainingSeconds=" + remainingSeconds
                + ", round=" + round
                + ", positionInRound=" + positionInRound
                + '}';
    }

    private static int normalizeIndex(int index, int participantCount) {
        if (participantCount <= 0) {
            return -1;
        }
        int mod = index % participantCount;
        if (mod < 0) {
            mod += participantCount;
        }
        return mod;
    }
}
