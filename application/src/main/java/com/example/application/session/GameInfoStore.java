package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;
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

    private final AtomicReference<GameTurnState> currentTurnState = new AtomicReference<>(GameTurnState.idle());
    private final MutableLiveData<GameTurnState> turnStateStream = new MutableLiveData<>(GameTurnState.idle());

    private final OmokBoardStore boardStore;

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
        if (session == null) {
            throw new IllegalArgumentException("session == null");
        }
        currentGameSession.set(session);
        gameSessionStream.postValue(session);
        GameTurnState current = currentTurnState.get();
        GameTurnState aligned = (current != null ? current : GameTurnState.idle())
                .ensureActive(session.getParticipantCount());
        currentTurnState.set(aligned);
        turnStateStream.postValue(aligned);
    }

    public void clearGameSession() {
        currentGameSession.set(null);
        gameSessionStream.postValue(null);
        clearTurnState();
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

    public void updateTurnState(@NonNull GameTurnState turnState) {
        if (turnState == null) {
            throw new IllegalArgumentException("turnState == null");
        }
        GameSessionInfo session = currentGameSession.get();
        GameTurnState adjusted = session == null
                ? turnState.deactivate()
                : turnState.normalize(session.getParticipantCount());
        currentTurnState.set(adjusted);
        turnStateStream.postValue(adjusted);
    }

    public void updateRemainingSeconds(int seconds) {
        GameTurnState base = currentTurnState.get();
        if (base == null) {
            base = GameTurnState.idle();
        }
        GameTurnState updated = base.withRemainingSeconds(seconds);
        GameSessionInfo session = currentGameSession.get();
        GameTurnState normalized = session == null
                ? updated.deactivate()
                : updated.normalize(session.getParticipantCount());
        currentTurnState.set(normalized);
        turnStateStream.postValue(normalized);
    }

    public void clearTurnState() {
        GameTurnState idle = GameTurnState.idle();
        currentTurnState.set(idle);
        turnStateStream.postValue(idle);
    }

    public void setTurnIndex(int index) {
        GameTurnState current = currentTurnState.get();
        int remaining = current != null ? current.getRemainingSeconds() : 0;
        setTurnIndex(index, remaining);
    }

    public void setTurnIndex(int index, int remainingSeconds, int round, int position) {
        GameSessionInfo session = currentGameSession.get();
        if (session == null || !session.hasParticipants()) {
            clearTurnState();
            return;
        }
        GameTurnState next = GameTurnState.active(index, remainingSeconds, round, position)
                .normalize(session.getParticipantCount());
        currentTurnState.set(next);
        turnStateStream.postValue(next);
    }

    public void advanceTurn() {
        GameSessionInfo session = currentGameSession.get();
        if (session == null || !session.hasParticipants()) {
            clearTurnState();
            return;
        }
        GameTurnState existing = currentTurnState.get();
        if (existing == null) {
            existing = GameTurnState.idle();
        }
        GameTurnState next = existing.advance(session.getParticipantCount());
        currentTurnState.set(next);
        turnStateStream.postValue(next);
    }

    @Nullable
    public GameParticipantInfo getCurrentTurnParticipant() {
        GameTurnState turn = currentTurnState.get();
        GameSessionInfo session = currentGameSession.get();
        if (turn == null || session == null || !turn.isActive()) {
            return null;
        }
        return session.getParticipantOrNull(turn.getCurrentIndex());
    }

    @NonNull
    public OmokBoardStore getBoardStore() {
        return boardStore;
    }
}
