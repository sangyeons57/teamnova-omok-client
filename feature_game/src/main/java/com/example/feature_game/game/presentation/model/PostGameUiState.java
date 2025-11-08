package com.example.feature_game.game.presentation.model;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PostGameDecisionOption;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the UI state of the post-game decision screen.
 */
public final class PostGameUiState {

    private final String sessionId;
    private final GameResultOutcome outcome;
    private final int rematchCount;
    private final int leaveCount;
    private final List<String> waitingNames;
    private final long deadlineAtMillis;
    private final long remainingMillis;
    private final boolean decisionSubmitted;
    private final PostGameDecisionOption selfDecision;
    private final PostGameDecisionOption autoAction;
    private final boolean rematchStarted;
    private final boolean terminated;
    private final long durationMillis;
    private final int turnCount;

    public PostGameUiState(@NonNull String sessionId,
                           @NonNull GameResultOutcome outcome,
                           int rematchCount,
                           int leaveCount,
                           @NonNull List<String> waitingNames,
                           long deadlineAtMillis,
                           long remainingMillis,
                           boolean decisionSubmitted,
                           @NonNull PostGameDecisionOption selfDecision,
                           @NonNull PostGameDecisionOption autoAction,
                           boolean rematchStarted,
                           boolean terminated,
                           long durationMillis,
                           int turnCount) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.rematchCount = Math.max(0, rematchCount);
        this.leaveCount = Math.max(0, leaveCount);
        this.waitingNames = Collections.unmodifiableList(List.copyOf(Objects.requireNonNull(waitingNames, "waitingNames")));
        this.deadlineAtMillis = deadlineAtMillis;
        this.remainingMillis = Math.max(0L, remainingMillis);
        this.decisionSubmitted = decisionSubmitted;
        this.selfDecision = Objects.requireNonNull(selfDecision, "selfDecision");
        this.autoAction = Objects.requireNonNull(autoAction, "autoAction");
        this.rematchStarted = rematchStarted;
        this.terminated = terminated;
        this.durationMillis = Math.max(0L, durationMillis);
        this.turnCount = Math.max(0, turnCount);
    }

    @NonNull
    public static PostGameUiState empty() {
        return new PostGameUiState("", GameResultOutcome.DRAW, 0, 0, List.of(), 0L, 0L,
                false, PostGameDecisionOption.UNKNOWN, PostGameDecisionOption.UNKNOWN, false, false, 0L, 0);
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public GameResultOutcome getOutcome() {
        return outcome;
    }

    public int getRematchCount() {
        return rematchCount;
    }

    public int getLeaveCount() {
        return leaveCount;
    }

    @NonNull
    public List<String> getWaitingNames() {
        return waitingNames;
    }

    public long getDeadlineAtMillis() {
        return deadlineAtMillis;
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    public boolean isDecisionSubmitted() {
        return decisionSubmitted;
    }

    @NonNull
    public PostGameDecisionOption getSelfDecision() {
        return selfDecision;
    }

    @NonNull
    public PostGameDecisionOption getAutoAction() {
        return autoAction;
    }

    public boolean isRematchStarted() {
        return rematchStarted;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public int getTurnCount() {
        return turnCount;
    }

    @NonNull
    public PostGameUiState withRemainingMillis(long millis) {
        return new PostGameUiState(sessionId, outcome, rematchCount, leaveCount, waitingNames,
                deadlineAtMillis, Math.max(0L, millis), decisionSubmitted, selfDecision,
                autoAction, rematchStarted, terminated, durationMillis, turnCount);
    }

    @NonNull
    public PostGameUiState withFlowFlags(boolean rematchStartedFlag, boolean terminatedFlag) {
        return new PostGameUiState(sessionId, outcome, rematchCount, leaveCount, waitingNames,
                deadlineAtMillis, remainingMillis, decisionSubmitted, selfDecision,
                autoAction, rematchStartedFlag, terminatedFlag, durationMillis, turnCount);
    }

}
