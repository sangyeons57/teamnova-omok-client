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

    private GameTurnState(boolean active, int currentIndex, int remainingSeconds) {
        this.active = active;
        this.currentIndex = active ? Math.max(currentIndex, 0) : -1;
        this.remainingSeconds = Math.max(remainingSeconds, 0);
    }

    public static GameTurnState idle() {
        return new GameTurnState(false, -1, 0);
    }

    public static GameTurnState active(int currentIndex, int remainingSeconds) {
        return new GameTurnState(true, currentIndex, remainingSeconds);
    }

    private static GameTurnState idleWithSeconds(int seconds) {
        return new GameTurnState(false, -1, seconds);
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

    @NonNull
    public GameTurnState withCurrentIndex(int index, int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        return new GameTurnState(true, normalizeIndex(index, participantCount), remainingSeconds);
    }

    @NonNull
    public GameTurnState withRemainingSeconds(int seconds) {
        return new GameTurnState(active, currentIndex, seconds);
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
        return new GameTurnState(true, normalizeIndex(currentIndex, participantCount), remainingSeconds);
    }

    @NonNull
    public GameTurnState ensureActive(int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        if (!active) {
            return new GameTurnState(true, 0, remainingSeconds);
        }
        return new GameTurnState(true, normalizeIndex(currentIndex, participantCount), remainingSeconds);
    }

    @NonNull
    public GameTurnState advance(int participantCount) {
        if (participantCount <= 0) {
            return idleWithSeconds(remainingSeconds);
        }
        if (!active) {
            return new GameTurnState(true, 0, remainingSeconds);
        }
        int nextIndex = normalizeIndex(currentIndex + 1, participantCount);
        return new GameTurnState(true, nextIndex, remainingSeconds);
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
                && remainingSeconds == that.remainingSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, currentIndex, remainingSeconds);
    }

    @Override
    public String toString() {
        return "GameTurnState{"
                + "active=" + active
                + ", currentIndex=" + currentIndex
                + ", remainingSeconds=" + remainingSeconds
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
