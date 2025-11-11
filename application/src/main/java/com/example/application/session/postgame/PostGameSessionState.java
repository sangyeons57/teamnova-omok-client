package com.example.application.session.postgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the aggregated post-game state for the active session.
 */
public final class PostGameSessionState {

    private final String sessionId;
    private final List<GameOutcome> outcomes;
    private final PostGameDecisionPrompt prompt;
    private final PostGameDecisionStatus decisionStatus;
    private final boolean rematchStarted;
    private final String rematchSessionId;
    private final List<String> rematchParticipants;
    private final boolean terminated;
    private final Map<String, PlayerDisconnectReason> disconnectedPlayers;
    private final long startedAtMillis;
    private final long endedAtMillis;
    private final long durationMillis;
    private final int turnCount;

    private PostGameSessionState(@NonNull String sessionId,
                                 @NonNull List<GameOutcome> outcomes,
                                 @Nullable PostGameDecisionPrompt prompt,
                                 @Nullable PostGameDecisionStatus decisionStatus,
                                 boolean rematchStarted,
                                 @NonNull String rematchSessionId,
                                 @NonNull List<String> rematchParticipants,
                                 boolean terminated,
                                 @NonNull Map<String, PlayerDisconnectReason> disconnectedPlayers,
                                 long startedAtMillis,
                                 long endedAtMillis,
                                 long durationMillis,
                                 int turnCount) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.outcomes = Collections.unmodifiableList(List.copyOf(Objects.requireNonNull(outcomes, "outcomes")));
        this.prompt = prompt;
        this.decisionStatus = decisionStatus;
        this.rematchStarted = rematchStarted;
        this.rematchSessionId = Objects.requireNonNull(rematchSessionId, "rematchSessionId");
        this.rematchParticipants = Collections.unmodifiableList(List.copyOf(Objects.requireNonNull(rematchParticipants, "rematchParticipants")));
        this.terminated = terminated;

        Map<String, PlayerDisconnectReason> copy = new LinkedHashMap<>();
        for (Map.Entry<String, PlayerDisconnectReason> entry : Objects.requireNonNull(disconnectedPlayers, "disconnectedPlayers").entrySet()) {
            String userId = Objects.requireNonNull(entry.getKey(), "disconnected userId");
            PlayerDisconnectReason reason = entry.getValue() != null
                    ? entry.getValue()
                    : PlayerDisconnectReason.UNKNOWN;
            copy.put(userId, reason);
        }
        this.disconnectedPlayers = Collections.unmodifiableMap(copy);
        this.startedAtMillis = clampTime(startedAtMillis);
        this.endedAtMillis = clampTime(endedAtMillis);
        this.durationMillis = clampTime(durationMillis);
        this.turnCount = Math.max(0, turnCount);
    }

    @NonNull
    public static PostGameSessionState empty() {
        return new PostGameSessionState(
                "",
                List.of(),
                null,
                null,
                false,
                "",
                List.of(),
                false,
                Map.of(),
                0L,
                0L,
                0L,
                0
        );
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public List<GameOutcome> getOutcomes() {
        return outcomes;
    }

    @Nullable
    public PostGameDecisionPrompt getPrompt() {
        return prompt;
    }

    @Nullable
    public PostGameDecisionStatus getDecisionStatus() {
        return decisionStatus;
    }

    public boolean isRematchStarted() {
        return rematchStarted;
    }

    @NonNull
    public String getRematchSessionId() {
        return rematchSessionId;
    }

    @NonNull
    public List<String> getRematchParticipants() {
        return rematchParticipants;
    }

    public boolean isTerminated() {
        return terminated;
    }

    @NonNull
    public List<String> getDisconnectedUserIds() {
        return List.copyOf(disconnectedPlayers.keySet());
    }

    @NonNull
    public Map<String, PlayerDisconnectReason> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    public long getStartedAtMillis() {
        return startedAtMillis;
    }

    public long getEndedAtMillis() {
        return endedAtMillis;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public int getTurnCount() {
        return turnCount;
    }

    @NonNull
    public PostGameSessionState withOutcomes(@NonNull String nextSessionId,
                                             @NonNull List<GameOutcome> nextOutcomes,
                                             long startedAtMillis,
                                             long endedAtMillis,
                                             long durationMillis,
                                             int turnCount) {
        PostGameSessionState base = normalizeForSession(nextSessionId);
        return new PostGameSessionState(
                nextSessionId,
                List.copyOf(nextOutcomes),
                base.prompt,
                base.decisionStatus,
                false,
                "",
                List.of(),
                false,
                Map.of(),
                startedAtMillis,
                endedAtMillis,
                durationMillis,
                turnCount
        );
    }

    @NonNull
    public PostGameSessionState withPrompt(@NonNull PostGameDecisionPrompt nextPrompt) {
        Objects.requireNonNull(nextPrompt, "nextPrompt");
        PostGameSessionState base = normalizeForSession(nextPrompt.getSessionId());
        return new PostGameSessionState(
                base.sessionId,
                base.outcomes,
                nextPrompt,
                base.decisionStatus,
                base.rematchStarted,
                base.rematchSessionId,
                base.rematchParticipants,
                base.terminated,
                base.disconnectedPlayers,
                base.startedAtMillis,
                base.endedAtMillis,
                base.durationMillis,
                base.turnCount
        );
    }

    @NonNull
    public PostGameSessionState withDecisionStatus(@NonNull PostGameDecisionStatus status) {
        Objects.requireNonNull(status, "status");
        PostGameSessionState base = normalizeForSession(status.getSessionId());
        return new PostGameSessionState(
                base.sessionId,
                base.outcomes,
                base.prompt,
                status,
                base.rematchStarted,
                base.rematchSessionId,
                base.rematchParticipants,
                base.terminated,
                base.disconnectedPlayers,
                base.startedAtMillis,
                base.endedAtMillis,
                base.durationMillis,
                base.turnCount
        );
    }

    @NonNull
    public PostGameSessionState withRematchStarted(@NonNull String sourceSessionId,
                                                   @NonNull String nextSessionId,
                                                   @NonNull List<String> participants) {
        Objects.requireNonNull(sourceSessionId, "sourceSessionId");
        Objects.requireNonNull(nextSessionId, "nextSessionId");
        Objects.requireNonNull(participants, "participants");
        PostGameSessionState base = normalizeForSession(sourceSessionId);
        return new PostGameSessionState(
                base.sessionId,
                base.outcomes,
                base.prompt,
                base.decisionStatus,
                true,
                nextSessionId,
                List.copyOf(participants),
                false,
                Map.of(),
                0L,
                0L,
                0L,
                0
        );
    }

    @NonNull
    public PostGameSessionState withTerminated(@NonNull String sourceSessionId,
                                               @NonNull List<String> disconnected,
                                               long startedAtMillis,
                                               long endedAtMillis,
                                               long durationMillis,
                                               int turnCount) {
        Objects.requireNonNull(sourceSessionId, "sourceSessionId");
        Objects.requireNonNull(disconnected, "disconnected");
        PostGameSessionState base = normalizeForSession(sourceSessionId);
        LinkedHashMap<String, PlayerDisconnectReason> updated = new LinkedHashMap<>(base.disconnectedPlayers);
        for (String userId : disconnected) {
            if (userId == null || userId.isEmpty()) {
                continue;
            }
            updated.put(userId, PlayerDisconnectReason.DISCONNECTED);
        }
        return new PostGameSessionState(
                base.sessionId,
                base.outcomes,
                base.prompt,
                base.decisionStatus,
                false,
                "",
                List.of(),
                true,
                updated,
                startedAtMillis,
                endedAtMillis,
                durationMillis,
                turnCount
        );
    }

    @NonNull
    public PostGameSessionState withPlayerDisconnected(@NonNull String sourceSessionId,
                                                       @NonNull String userId,
                                                       @NonNull PlayerDisconnectReason reason) {
        Objects.requireNonNull(sourceSessionId, "sourceSessionId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(reason, "reason");
        if (userId.isEmpty()) {
            return this;
        }
        PostGameSessionState base = normalizeForSession(sourceSessionId);
        LinkedHashMap<String, PlayerDisconnectReason> updated = new LinkedHashMap<>(base.disconnectedPlayers);
        updated.put(userId, reason);
        return new PostGameSessionState(
                base.sessionId,
                base.outcomes,
                base.prompt,
                base.decisionStatus,
                base.rematchStarted,
                base.rematchSessionId,
                base.rematchParticipants,
                base.terminated,
                updated,
                base.startedAtMillis,
                base.endedAtMillis,
                base.durationMillis,
                base.turnCount
        );
    }

    @NonNull
    public PostGameSessionState withPlayerReconnected(@NonNull String sourceSessionId,
                                                      @NonNull String userId) {
        Objects.requireNonNull(sourceSessionId, "sourceSessionId");
        Objects.requireNonNull(userId, "userId");
        if (userId.isEmpty()) {
            return this;
        }
        PostGameSessionState base = normalizeForSession(sourceSessionId);
        LinkedHashMap<String, PlayerDisconnectReason> updated = new LinkedHashMap<>(base.disconnectedPlayers);
        if (!updated.containsKey(userId)) {
            return base;
        }
        updated.remove(userId);
        return new PostGameSessionState(
                base.sessionId,
                base.outcomes,
                base.prompt,
                base.decisionStatus,
                base.rematchStarted,
                base.rematchSessionId,
                base.rematchParticipants,
                base.terminated,
                updated,
                base.startedAtMillis,
                base.endedAtMillis,
                base.durationMillis,
                base.turnCount
        );
    }

    @NonNull
    public PostGameSessionState cleared() {
        return empty();
    }

    private PostGameSessionState normalizeForSession(@NonNull String targetSessionId) {
        Objects.requireNonNull(targetSessionId, "targetSessionId");
        if (targetSessionId.isEmpty()) {
            return empty();
        }
        if (sessionId.equals(targetSessionId)) {
            return this;
        }
        return new PostGameSessionState(
                targetSessionId,
                List.of(),
                null,
                null,
                false,
                "",
                List.of(),
                false,
                Map.of(),
                0L,
                0L,
                0L,
                0
        );
    }

    private static long clampTime(long value) {
        return Math.max(0L, value);
    }
}
