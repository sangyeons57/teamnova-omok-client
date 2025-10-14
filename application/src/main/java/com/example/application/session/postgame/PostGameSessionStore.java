package com.example.application.session.postgame;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * Thread-safe store holding the latest post-game state for observers.
 */
public final class PostGameSessionStore {

    private final AtomicReference<PostGameSessionState> currentState = new AtomicReference<>(PostGameSessionState.empty());
    private final MutableLiveData<PostGameSessionState> stateStream = new MutableLiveData<>(PostGameSessionState.empty());

    @NonNull
    public PostGameSessionState getCurrentState() {
        return currentState.get();
    }

    @NonNull
    public LiveData<PostGameSessionState> getStateStream() {
        PostGameSessionState existing = currentState.get();
        if (existing != null && stateStream.getValue() == null) {
            stateStream.setValue(existing);
        }
        return stateStream;
    }

    public void clear() {
        update(previous -> previous.cleared());
    }

    public void updateOutcomes(@NonNull String sessionId,
                               @NonNull List<GameOutcome> outcomes) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(outcomes, "outcomes");
        if (sessionId.isEmpty()) {
            return;
        }
        updateOutcomes(sessionId, outcomes, 0L, 0L, 0L, 0);
    }

    public void updateOutcomes(@NonNull String sessionId,
                               @NonNull List<GameOutcome> outcomes,
                               long startedAtMillis,
                               long endedAtMillis,
                               long durationMillis,
                               int turnCount) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(outcomes, "outcomes");
        if (sessionId.isEmpty()) {
            return;
        }
        final long safeStarted = Math.max(0L, startedAtMillis);
        final long safeEnded = Math.max(0L, endedAtMillis);
        final long safeDuration = Math.max(0L, durationMillis);
        final int safeTurnCount = Math.max(0, turnCount);
        update(previous -> previous.withOutcomes(sessionId, outcomes, safeStarted, safeEnded, safeDuration, safeTurnCount));
    }

    public void updatePrompt(@NonNull PostGameDecisionPrompt prompt) {
        Objects.requireNonNull(prompt, "prompt");
        if (prompt.getSessionId().isEmpty()) {
            return;
        }
        update(previous -> previous.withPrompt(prompt));
    }

    public void updateDecisionStatus(@NonNull PostGameDecisionStatus status) {
        Objects.requireNonNull(status, "status");
        if (status.getSessionId().isEmpty()) {
            return;
        }
        update(previous -> previous.withDecisionStatus(status));
    }

    public void markRematchStarted(@NonNull String sessionId,
                                   @NonNull String rematchSessionId,
                                   @NonNull List<String> participants) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(rematchSessionId, "rematchSessionId");
        Objects.requireNonNull(participants, "participants");
        if (sessionId.isEmpty() || rematchSessionId.isEmpty()) {
            return;
        }
        update(previous -> previous.withRematchStarted(sessionId, rematchSessionId, participants));
    }

    public void markPlayerDisconnected(@NonNull String sessionId,
                                       @NonNull String userId,
                                       @NonNull PlayerDisconnectReason reason) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(reason, "reason");
        if (sessionId.isEmpty() || userId.isEmpty()) {
            return;
        }
        update(previous -> previous.withPlayerDisconnected(sessionId, userId, reason));
    }

    public void markTerminated(@NonNull String sessionId,
                               @NonNull List<String> disconnectedUserIds) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(disconnectedUserIds, "disconnectedUserIds");
        if (sessionId.isEmpty()) {
            return;
        }
        markTerminated(sessionId, disconnectedUserIds, 0L, 0L, 0L, 0);
    }

    public void markTerminated(@NonNull String sessionId,
                               @NonNull List<String> disconnectedUserIds,
                               long startedAtMillis,
                               long endedAtMillis,
                               long durationMillis,
                               int turnCount) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(disconnectedUserIds, "disconnectedUserIds");
        if (sessionId.isEmpty()) {
            return;
        }
        final long safeStarted = Math.max(0L, startedAtMillis);
        final long safeEnded = Math.max(0L, endedAtMillis);
        final long safeDuration = Math.max(0L, durationMillis);
        final int safeTurnCount = Math.max(0, turnCount);
        update(previous -> previous.withTerminated(sessionId, disconnectedUserIds, safeStarted, safeEnded, safeDuration, safeTurnCount));
    }

    private void update(@NonNull UnaryOperator<PostGameSessionState> transformer) {
        Objects.requireNonNull(transformer, "transformer");
        PostGameSessionState previous;
        PostGameSessionState next;
        do {
            previous = currentState.get();
            next = transformer.apply(previous != null ? previous : PostGameSessionState.empty());
        } while (!currentState.compareAndSet(previous, next));
        stateStream.postValue(next);
    }
}
