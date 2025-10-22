package com.example.application.session;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable container holding metadata about an in-progress game session.
 */
public final class GameSessionInfo {

    private final String sessionId;
    private final long createdAt;
    private final Map<String, GameParticipantInfo> participants;

    public GameSessionInfo(@NonNull String sessionId,
                           long createdAt,
                           @NonNull Map<String, GameParticipantInfo> participantsMap) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.createdAt = createdAt;
        this.participants = Collections.unmodifiableMap(participantsMap);
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
        List<GameParticipantInfo> participantInfos = new ArrayList<>();
        for (Map.Entry<String, GameParticipantInfo> entry : participants.entrySet()) {
            participantInfos.add(entry.getValue());
        }
        return participantInfos;
    }

    public List<String> getUids() {
        List<String> uids = new ArrayList<>();
        for (Map.Entry<String, GameParticipantInfo> entry : participants.entrySet()) {
            uids.add(entry.getKey());
        }
        return uids;
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public boolean hasParticipants() {
        return !participants.isEmpty();
    }

    @Nullable
    public GameParticipantInfo getParticipantById(@NonNull String userId) {
        return participants.get(userId);
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
