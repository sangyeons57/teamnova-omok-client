package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable container holding metadata about an in-progress game session.
 */
public final class GameSessionInfo {

    private final String sessionId;
    private final long createdAt;
    private final List<GameParticipantInfo> participants;

    public GameSessionInfo(@NonNull String sessionId,
                           long createdAt,
                           @NonNull List<GameParticipantInfo> participants) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.createdAt = createdAt;
        this.participants = List.copyOf(Objects.requireNonNull(participants, "participants"));
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @NonNull
    public List<GameParticipantInfo> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public boolean hasParticipants() {
        return !participants.isEmpty();
    }

    @NonNull
    public GameParticipantInfo getParticipantAt(int index) {
        if (index < 0 || index >= participants.size()) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + participants.size());
        }
        return participants.get(index);
    }

    @Nullable
    public GameParticipantInfo getParticipantOrNull(int index) {
        if (index < 0 || index >= participants.size()) {
            return null;
        }
        return participants.get(index);
    }

    @Nullable
    public GameParticipantInfo getParticipantWrapped(int index) {
        if (participants.isEmpty()) {
            return null;
        }
        int normalized = index % participants.size();
        if (normalized < 0) {
            normalized += participants.size();
        }
        return participants.get(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameSessionInfo that = (GameSessionInfo) o;
        return createdAt == that.createdAt
                && sessionId.equals(that.sessionId)
                && participants.equals(that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, createdAt, participants);
    }

    @Override
    public String toString() {
        return "GameSessionInfo{sessionId='" + sessionId + '\'' +
                ", createdAt=" + createdAt +
                ", participants=" + participants +
                '}';
    }
}
