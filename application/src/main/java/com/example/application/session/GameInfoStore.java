package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Stores game-related selections that should survive across screens for the lifetime of the process.
 */
public class GameInfoStore {

    private final AtomicReference<GameMode> currentMode = new AtomicReference<>(GameMode.FREE);
    private final MutableLiveData<GameMode> modeStream = new MutableLiveData<>(GameMode.FREE);

    private final AtomicReference<MatchState> currentMatchState = new AtomicReference<>(MatchState.IDLE);
    private final MutableLiveData<MatchState> matchStateStream = new MutableLiveData<>(MatchState.IDLE);

    private final AtomicReference<GameSessionInfo> currentGameSession = new AtomicReference<>();
    private final MutableLiveData<GameSessionInfo> gameSessionStream = new MutableLiveData<>();

    // TurnStartState
    private final AtomicReference<GameTurnState> currentTurnState = new AtomicReference<>(GameTurnState.idle());
    private final MutableLiveData<GameTurnState> turnStateStream = new MutableLiveData<>(GameTurnState.idle());

    private final MutableLiveData<TurnEndEvent> turnEndEventStream = new MutableLiveData<>();

    private final AtomicReference<List<String>> currentActiveRules =
            new AtomicReference<>(Collections.emptyList());
    private final MutableLiveData<List<String>> activeRulesStream =
            new MutableLiveData<>(Collections.emptyList());

    private final OmokBoardStore boardStore;
    private final AtomicLong serverTimeOffsetMillis = new AtomicLong(0L);

    public GameInfoStore(@NonNull OmokBoardStore boardStore) {
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
    }

    public GameInfoStore() {
        this(new OmokBoardStore());
    }

    @NonNull
    public GameMode getCurrentMode() {
        return currentMode.get();
    }

    public void update(@NonNull GameMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode == null");
        }
        currentMode.set(mode);
        modeStream.postValue(mode);
    }

    @NonNull
    public LiveData<GameMode> getModeStream() {
        GameMode existing = currentMode.get();
        if (existing != null && modeStream.getValue() == null) {
            modeStream.setValue(existing);
        }
        return modeStream;
    }

    @NonNull
    public LiveData<MatchState> getMatchStateStream() {
        MatchState existing = currentMatchState.get();
        if (existing != null && matchStateStream.getValue() == null) {
            matchStateStream.setValue(existing);
        }
        return matchStateStream;
    }

    public void updateMatchState(@NonNull MatchState state) {
        if (state == null) {
            throw new IllegalArgumentException("state == null");
        }
        currentMatchState.set(state);
        if (state == MatchState.IDLE) {
            clearGameSession();
        }
        matchStateStream.postValue(state);
    }

    @Nullable
    public GameSessionInfo getCurrentGameSession() {
        return currentGameSession.get();
    }

    @NonNull
    public LiveData<GameSessionInfo> getGameSessionStream() {
        GameSessionInfo existing = currentGameSession.get();
        if (existing != null && gameSessionStream.getValue() == null) {
            gameSessionStream.setValue(existing);
        }
        return gameSessionStream;
    }

    public void updateGameSession(@NonNull GameSessionInfo session) {
        currentGameSession.set(session);
        gameSessionStream.postValue(session);
        clearActiveRules();
        clearTurnState(); // Reset turn state to idle when a new session is updated
    }

    public void clearGameSession() {
        currentGameSession.set(null);
        gameSessionStream.postValue(null);
        clearTurnState();
        clearActiveRules();
        boardStore.clearBoard();
    }

    @NonNull
    public GameTurnState getCurrentTurnState() {
        return currentTurnState.get();
    }

    @NonNull
    public LiveData<GameTurnState> getTurnStateStream() {
        GameTurnState existing = currentTurnState.get();
        if (existing != null && turnStateStream.getValue() == null) {
            turnStateStream.setValue(existing);
        }
        return turnStateStream;
    }

    @NonNull
    public LiveData<TurnEndEvent> getTurnEndEventStream() {
        return turnEndEventStream;
    }

    public void postTurnEndEvent(@NonNull TurnEndEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event == null");
        }
        turnEndEventStream.postValue(event);
    }

    public void clearTurnState() {
        GameTurnState idle = GameTurnState.idle();
        currentTurnState.set(idle);
        turnStateStream.postValue(idle);
    }

    public void setTurnState(@NonNull String currentPlayerId,
                             int remainingSeconds,
                             long startAt,
                             long endAt,
                             int turnNumber,
                             int roundNumber,
                             int positionInRound,
                             int playerIndex) {
        GameSessionInfo session = currentGameSession.get();
        if (session == null || !session.hasParticipants()) {
            clearTurnState();
            return;
        }
        GameTurnState next = GameTurnState.active(currentPlayerId,
                remainingSeconds,
                startAt,
                endAt,
                turnNumber,
                roundNumber,
                positionInRound,
                playerIndex)
                .normalize();
        currentTurnState.set(next);
        turnStateStream.postValue(next);
    }

    @Nullable
    public GameParticipantInfo getCurrentTurnParticipant() {
        GameTurnState turn = currentTurnState.get();
        GameSessionInfo session = currentGameSession.get();
        if (turn == null || session == null || !turn.isActive() || turn.getCurrentPlayerId() == null) {
            return null;
        }
        return session.getParticipantById(turn.getCurrentPlayerId());
    }

    @NonNull
    public OmokBoardStore getBoardStore() {
        return boardStore;
    }

    @NonNull
    public LiveData<List<String>> getActiveRulesStream() {
        List<String> existing = currentActiveRules.get();
        if (existing != null && activeRulesStream.getValue() == null) {
            activeRulesStream.setValue(existing);
        }
        return activeRulesStream;
    }

    @NonNull
    public List<String> getCurrentRules() {
        List<String> rules = currentActiveRules.get();
        if (rules == null) {
            return Collections.emptyList();
        }
        return rules;
    }

    public void updateActiveRules(@NonNull List<String> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("rules == null");
        }
        List<String> snapshot = Collections.unmodifiableList(new ArrayList<>(rules));
        currentActiveRules.set(snapshot);
        activeRulesStream.postValue(snapshot);
    }

    public void clearActiveRules() {
        currentActiveRules.set(Collections.emptyList());
        activeRulesStream.postValue(Collections.emptyList());
    }

    public void updateServerTimeOffset(long serverTimeMillis, long clientReceivedAtMillis) {
        long offset = serverTimeMillis - clientReceivedAtMillis;
        serverTimeOffsetMillis.set(offset);
    }

    public long getServerTimeOffsetMillis() {
        return serverTimeOffsetMillis.get();
    }
}
