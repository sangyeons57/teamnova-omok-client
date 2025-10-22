package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
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
        currentGameSession.set(session);
        gameSessionStream.postValue(session);
        clearTurnState(); // Reset turn state to idle when a new session is updated
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
        GameTurnState adjusted = turnState.normalize(); // No participantCount needed here anymore
        currentTurnState.set(adjusted);
        turnStateStream.postValue(adjusted);
    }

    public void updateRemainingSeconds(int seconds) {
        GameTurnState base = currentTurnState.get();
        if (base == null) {
            base = GameTurnState.idle();
        }
        GameTurnState updated = base.withRemainingSeconds(seconds);
        GameTurnState normalized = updated.normalize(); // No participantCount needed here anymore
        currentTurnState.set(normalized);
        turnStateStream.postValue(normalized);
    }

    public void clearTurnState() {
        GameTurnState idle = GameTurnState.idle();
        currentTurnState.set(idle);
        turnStateStream.postValue(idle);
    }

    public void setTurnState(@NonNull String currentPlayerId, int remainingSeconds) {
        GameSessionInfo session = currentGameSession.get();
        if (session == null || !session.hasParticipants()) {
            clearTurnState();
            return;
        }
        GameTurnState next = GameTurnState.active(currentPlayerId, remainingSeconds)
                .normalize(); // No participantCount needed here anymore
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
        if (existing == null || !existing.isActive() || existing.getCurrentPlayerId() == null) {
            // If no active turn or current player, start with the first participant
            String firstPlayerId = session.getParticipants().iterator().next().getUserId();
            setTurnState(firstPlayerId, 0); // Assuming 0 remaining seconds for a new turn
            return;
        }

        String currentPlayerId = existing.getCurrentPlayerId();
        List<String> participantIds = new ArrayList<String>(session.getUids());
        int currentIndex = participantIds.indexOf(currentPlayerId);

        if (currentIndex == -1) {
            // Current player not found, reset turn state
            clearTurnState();
            return;
        }

        int nextIndex = (currentIndex + 1) % participantIds.size();
        String nextPlayerId = participantIds.get(nextIndex);

        setTurnState(nextPlayerId, existing.getRemainingSeconds()); // Keep remaining seconds or reset as needed
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
}
