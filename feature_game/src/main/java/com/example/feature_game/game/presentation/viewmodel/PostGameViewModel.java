package com.example.feature_game.game.presentation.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.application.session.GameInfoStore;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;
import com.example.application.session.MatchState;
import com.example.application.session.UserSessionStore;
import com.example.application.session.postgame.GameOutcome;
import com.example.application.session.postgame.GameOutcomeResult;
import com.example.application.session.postgame.PostGameDecisionPrompt;
import com.example.application.session.postgame.PostGameDecisionStatus;
import com.example.application.session.postgame.PostGameSessionState;
import com.example.application.session.postgame.PostGameSessionStore;
import com.example.application.usecase.PostGameDecisionUseCase;
import com.example.application.usecase.SelfDataUseCase;
import com.example.domain.user.entity.User;
import com.example.feature_game.game.presentation.model.GameResultOutcome;
import com.example.feature_game.game.presentation.model.PostGameUiState;
import com.example.feature_game.game.presentation.state.PostGameViewEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coordinates the post-game decision screen.
 */
public final class PostGameViewModel extends ViewModel {

    private static final String TAG = "PostGameViewModel";

    private final GameInfoStore gameInfoStore;
    private final PostGameSessionStore postGameSessionStore;
    private final UserSessionStore userSessionStore;
    private final SelfDataUseCase selfDataUseCase;
    private final PostGameDecisionUseCase postGameDecisionUseCase;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final Handler countdownHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<PostGameUiState> uiState = new MutableLiveData<>(PostGameUiState.empty());
    private final MutableLiveData<PostGameViewEvent> viewEvents = new MutableLiveData<>();

    private final Observer<PostGameSessionState> postGameObserver = this::onPostGameStateChanged;
    private final Observer<GameSessionInfo> sessionObserver = this::onSessionChanged;
    private final Observer<User> userObserver = this::onUserChanged;

    private PostGameSessionState latestPostGameState = PostGameSessionState.empty();
    private GameSessionInfo latestSessionInfo;
    private String selfUserId = "";
    private String selfDisplayName = "";
    private String activeSessionId = "";
    private boolean rematchEventEmitted;
    private boolean terminatedEventEmitted;
    private long activeDeadlineAtMillis;
    private Runnable countdownRunnable;
    private final AtomicBoolean exitDispatched = new AtomicBoolean(false);
    private final AtomicBoolean profileRefreshInProgress = new AtomicBoolean(false);

    public PostGameViewModel(@NonNull GameInfoStore gameInfoStore,
                             @NonNull PostGameSessionStore postGameSessionStore,
                             @NonNull UserSessionStore userSessionStore,
                             @NonNull SelfDataUseCase selfDataUseCase,
                             @NonNull PostGameDecisionUseCase postGameDecisionUseCase) {
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.postGameSessionStore = Objects.requireNonNull(postGameSessionStore, "postGameSessionStore");
        this.userSessionStore = Objects.requireNonNull(userSessionStore, "userSessionStore");
        this.selfDataUseCase = Objects.requireNonNull(selfDataUseCase, "selfDataUseCase");
        this.postGameDecisionUseCase = Objects.requireNonNull(postGameDecisionUseCase, "postGameDecisionUseCase");

        postGameSessionStore.getStateStream().observeForever(postGameObserver);
        gameInfoStore.getGameSessionStream().observeForever(sessionObserver);
        userSessionStore.getUserStream().observeForever(userObserver);

        PostGameSessionState initialState = postGameSessionStore.getCurrentState();
        if (initialState != null) {
            latestPostGameState = initialState;
            rebuildUiState();
        }

        GameSessionInfo initialSession = gameInfoStore.getCurrentGameSession();
        if (initialSession != null) {
            latestSessionInfo = initialSession;
        }

        User initialUser = userSessionStore.getCurrentUser();
        if (initialUser != null) {
            onUserChanged(initialUser);
        }
    }

    @NonNull
    public LiveData<PostGameUiState> getUiState() {
        return uiState;
    }

    @NonNull
    public LiveData<PostGameViewEvent> getViewEvents() {
        return viewEvents;
    }

    public void onRematchClicked() {
        sendDecision(PostGameDecisionOption.REMATCH);
    }

    public void onLeaveClicked() {
        if (exitDispatched.getAndSet(true)) {
            return;
        }
        terminatedEventEmitted = true;
        viewEvents.postValue(new PostGameViewEvent(PostGameViewEvent.Type.EXIT_TO_HOME));
        gameInfoStore.updateMatchState(MatchState.IDLE);
        postGameSessionStore.clear();
        triggerSelfProfileRefresh();
        sendDecision(PostGameDecisionOption.LEAVE);
    }

    public void onEventHandled() {
        viewEvents.setValue(null);
    }

    private void onPostGameStateChanged(@Nullable PostGameSessionState state) {
        latestPostGameState = state != null ? state : PostGameSessionState.empty();
        rebuildUiState();
    }

    private void onSessionChanged(@Nullable GameSessionInfo sessionInfo) {
        if (sessionInfo != null) {
            latestSessionInfo = sessionInfo;
            rebuildUiState();
        }
    }

    private void onUserChanged(@Nullable User user) {
        if (user == null) {
            selfUserId = "";
            selfDisplayName = "";
        } else {
            selfUserId = user.getUserId().getValue();
            selfDisplayName = user.getDisplayName().getValue();
        }
        rebuildUiState();
    }

    private void rebuildUiState() {
        PostGameSessionState state = latestPostGameState != null ? latestPostGameState : PostGameSessionState.empty();
        String sessionId = state.getSessionId();
        if (sessionId == null) {
            sessionId = "";
        }

        if (!sessionId.equals(activeSessionId)) {
            activeSessionId = sessionId;
            rematchEventEmitted = false;
            terminatedEventEmitted = false;
            activeDeadlineAtMillis = 0L;
            cancelCountdown();
            exitDispatched.set(false);
        }

        if (sessionId.isEmpty()) {
            uiState.postValue(PostGameUiState.empty());
            return;
        }

        GameResultOutcome outcome = resolveOutcomeForSelf(state.getOutcomes());

        PostGameDecisionStatus decisionStatus = state.getDecisionStatus();
        Map<String, PostGameDecisionOption> decisions = decisionStatus != null
                ? decisionStatus.getDecisions()
                : Collections.emptyMap();

        int rematchCount = 0;
        int leaveCount = 0;
        for (PostGameDecisionOption option : decisions.values()) {
            if (option == PostGameDecisionOption.REMATCH) {
                rematchCount++;
            } else if (option == PostGameDecisionOption.LEAVE) {
                leaveCount++;
            }
        }

        PostGameDecisionOption selfDecision = decisions.getOrDefault(selfUserId, PostGameDecisionOption.UNKNOWN);
        boolean decisionSubmitted = selfDecision != PostGameDecisionOption.UNKNOWN;

        List<String> waitingUserIds = new ArrayList<>();
        if (decisionStatus != null && !decisionStatus.getRemainingUserIds().isEmpty()) {
            waitingUserIds.addAll(decisionStatus.getRemainingUserIds());
        } else {
            waitingUserIds.addAll(resolveRemainingUserIds(decisions));
        }

        List<String> waitingNames = new ArrayList<>();
        for (String userId : waitingUserIds) {
            waitingNames.add(resolveDisplayName(userId));
        }

        PostGameDecisionPrompt prompt = state.getPrompt();
        long deadlineAt = prompt != null ? prompt.getDeadlineAt() : 0L;
        PostGameDecisionOption autoAction = prompt != null ? prompt.getAutoAction() : PostGameDecisionOption.UNKNOWN;
        long remainingMillis = deadlineAt > 0L ? Math.max(0L, deadlineAt - System.currentTimeMillis()) : 0L;
        long durationMillis = state.getDurationMillis();
        int turnCount = state.getTurnCount();

        PostGameUiState newState = new PostGameUiState(
                sessionId,
                outcome,
                rematchCount,
                leaveCount,
                waitingNames,
                deadlineAt,
                remainingMillis,
                decisionSubmitted,
                selfDecision,
                autoAction,
                state.isRematchStarted(),
                state.isTerminated(),
                durationMillis,
                turnCount
        );
        uiState.postValue(newState);

        handleCountdown(deadlineAt, remainingMillis);
        handleFlowEvents(state);
    }

    private void handleCountdown(long deadlineAt, long initialRemainingMillis) {
        if (deadlineAt <= 0L) {
            cancelCountdown();
            activeDeadlineAtMillis = 0L;
            return;
        }
        if (activeDeadlineAtMillis == deadlineAt && countdownRunnable != null) {
            return;
        }
        activeDeadlineAtMillis = deadlineAt;
        cancelCountdown();
        updateRemainingMillis(initialRemainingMillis);
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                long remaining = Math.max(0L, activeDeadlineAtMillis - System.currentTimeMillis());
                updateRemainingMillis(remaining);
                if (remaining > 0L) {
                    countdownHandler.postDelayed(this, 1_000L);
                } else {
                    countdownRunnable = null;
                }
            }
        };
        countdownHandler.postDelayed(countdownRunnable, 1_000L);
    }

    private void updateRemainingMillis(long millis) {
        PostGameUiState current = uiState.getValue();
        if (current == null) {
            current = PostGameUiState.empty();
        }
        uiState.postValue(current.withRemainingMillis(millis));
    }

    private void cancelCountdown() {
        if (countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
            countdownRunnable = null;
        }
    }

    private void handleFlowEvents(@NonNull PostGameSessionState state) {
        if (state.isRematchStarted() && !rematchEventEmitted) {
            rematchEventEmitted = true;
            viewEvents.postValue(new PostGameViewEvent(PostGameViewEvent.Type.REMATCH_STARTED));
        }
        if (state.isTerminated() && !terminatedEventEmitted) {
            terminatedEventEmitted = true;
            triggerSelfProfileRefresh();
            viewEvents.postValue(new PostGameViewEvent(PostGameViewEvent.Type.SESSION_TERMINATED));
        }
    }

    private List<String> resolveRemainingUserIds(Map<String, PostGameDecisionOption> decisions) {
        if (latestSessionInfo == null || latestSessionInfo.getParticipants().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> remaining = new ArrayList<>();
        for (GameParticipantInfo participant : latestSessionInfo.getParticipants()) {
            if (!decisions.containsKey(participant.getUserId())) {
                remaining.add(participant.getUserId());
            }
        }
        return remaining;
    }

    private void sendDecision(@NonNull PostGameDecisionOption decision) {
        postGameDecisionUseCase.executeAsync(new PostGameDecisionUseCase.Params(decision), ioExecutor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Failed to send decision", throwable);
                        viewEvents.postValue(new PostGameViewEvent(PostGameViewEvent.Type.SHOW_ERROR,
                                "결정을 전송할 수 없습니다.", null));
                        return;
                    }
                    if (result instanceof UResult.Err<?> err) {
                        viewEvents.postValue(new PostGameViewEvent(PostGameViewEvent.Type.SHOW_ERROR,
                                err.message(), null));
                    } else if (result instanceof UResult.Ok<?>) {
                        Object value = ((UResult.Ok<?>) result).value();
                        if (value instanceof PostGameDecisionAck ack) {
                            handleDecisionAck(ack);
                        }
                    }
                });
    }

    private void handleDecisionAck(@NonNull PostGameDecisionAck ack) {
        if (ack.isOk()) {
            PostGameUiState current = uiState.getValue();
            if (current != null) {
                uiState.postValue(current.withDecision(ack.decision(), true));
            }
            return;
        }
        viewEvents.postValue(new PostGameViewEvent(
                PostGameViewEvent.Type.SHOW_ERROR,
                ack.rawMessage(),
                ack.errorReason()
        ));
    }

    private void triggerSelfProfileRefresh() {
        if (profileRefreshInProgress.getAndSet(true)) {
            return;
        }
        selfDataUseCase.executeAsync(UseCase.None.INSTANCE, ioExecutor)
                .whenComplete((result, throwable) -> {
                    try {
                        if (throwable != null) {
                            Log.w(TAG, "Failed to refresh self profile after post game exit", throwable);
                            return;
                        }
                        if (result instanceof UResult.Err<?> err) {
                            Log.w(TAG, "Failed to refresh self profile after post game exit: " + err.message());
                        }
                    } finally {
                        profileRefreshInProgress.set(false);
                    }
                });
    }

    @NonNull
    private GameResultOutcome resolveOutcomeForSelf(@NonNull List<GameOutcome> outcomes) {
        GameOutcomeResult matched = GameOutcomeResult.UNKNOWN;
        for (GameOutcome outcome : outcomes) {
            if (selfUserId.equals(outcome.getUserId())) {
                matched = outcome.getResult();
                break;
            }
        }
        if (matched == GameOutcomeResult.UNKNOWN && !outcomes.isEmpty()) {
            matched = outcomes.get(0).getResult();
        }
        return mapOutcomeResult(matched);
    }

    @NonNull
    private GameResultOutcome mapOutcomeResult(@NonNull GameOutcomeResult result) {
        return switch (result) {
            case WIN -> GameResultOutcome.WIN;
            case LOSS -> GameResultOutcome.LOSS;
            case DRAW, UNKNOWN -> GameResultOutcome.DRAW;
        };
    }

    @NonNull
    private String resolveDisplayName(@NonNull String userId) {
        if (latestSessionInfo != null) {
            for (GameParticipantInfo participant : latestSessionInfo.getParticipants()) {
                if (userId.equals(participant.getUserId())) {
                    String name = participant.getDisplayName();
                    if (name != null && !name.trim().isEmpty()) {
                        return name;
                    }
                }
            }
        }
        if (userId.equals(selfUserId) && selfDisplayName != null && !selfDisplayName.isEmpty()) {
            return selfDisplayName;
        }
        return userId;
    }

    @Override
    protected void onCleared() {
        cancelCountdown();
        postGameSessionStore.getStateStream().removeObserver(postGameObserver);
        gameInfoStore.getGameSessionStream().removeObserver(sessionObserver);
        userSessionStore.getUserStream().removeObserver(userObserver);
        ioExecutor.shutdownNow();
        super.onCleared();
    }
}
