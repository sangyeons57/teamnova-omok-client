package com.example.feature_game.game.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.session.GameInfoStore;
import com.example.application.session.GameMode;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;
import com.example.application.session.GameTurnState;
import com.example.application.session.MatchState;
import com.example.application.session.OmokBoardState;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.ReadyInGameSessionUseCase;
import com.example.domain.user.entity.User;
import com.example.feature_game.game.presentation.model.GamePlayerSlot;
import com.example.feature_game.game.presentation.state.GameViewEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coordinates the Omok game screen UI state.
 */
public class GameViewModel extends ViewModel {

    private static final String TAG = "GameViewModel";
    private static final int DEFAULT_BOARD_SIZE = 10;
    private static final OmokStoneType[] TURN_ORDER = new OmokStoneType[]{
            OmokStoneType.BLACK,
            OmokStoneType.WHITE,
            OmokStoneType.RED,
            OmokStoneType.BLUE
    };

    private final GameInfoStore gameInfoStore;
    private final UserSessionStore userSessionStore;
    private final ReadyInGameSessionUseCase readyInGameSessionUseCase;
    private final OmokBoardStore boardStore;
    private final ExecutorService realtimeExecutor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<GamePlayerSlot>> playerSlots = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Integer> activePlayerIndex = new MutableLiveData<>(0);
    private final MutableLiveData<GameMode> currentMode = new MutableLiveData<>(GameMode.TWO_PLAYER);
    private final MutableLiveData<MatchState> matchState = new MutableLiveData<>(MatchState.IDLE);
    private final MutableLiveData<OmokBoardState> boardState = new MutableLiveData<>(OmokBoardState.empty());
    private final MutableLiveData<GameViewEvent> viewEvents = new MutableLiveData<>();
    private final Observer<GameMode> modeObserver = this::onModeChanged;
    private final Observer<MatchState> matchObserver = this::onMatchStateChanged;
    private final Observer<GameSessionInfo> sessionObserver = this::onSessionChanged;
    private final Observer<User> userObserver = this::onUserChanged;
    private final Observer<OmokBoardState> boardObserver = this::onBoardStateChanged;
    private final Observer<GameTurnState> turnObserver = this::onTurnChanged;

    private final List<GamePlayerSlot> cachedSlots = new ArrayList<>(4);
    private final AtomicBoolean readySignalSent = new AtomicBoolean(false);
    private String selfDisplayName = "";
    private String selfUserId = "";
    private GameSessionInfo latestSessionInfo;

    public GameViewModel(@NonNull GameInfoStore gameInfoStore,
                         @NonNull UserSessionStore userSessionStore,
                         @NonNull ReadyInGameSessionUseCase readyInGameSessionUseCase) {
        this.gameInfoStore = gameInfoStore;
        this.userSessionStore = userSessionStore;
        this.readyInGameSessionUseCase = readyInGameSessionUseCase;
        this.boardStore = gameInfoStore.getBoardStore();

        gameInfoStore.getModeStream().observeForever(modeObserver);
        gameInfoStore.getMatchStateStream().observeForever(matchObserver);
        gameInfoStore.getGameSessionStream().observeForever(sessionObserver);
        gameInfoStore.getTurnStateStream().observeForever(turnObserver);
        boardStore.getBoardStateStream().observeForever(boardObserver);
        userSessionStore.getUserStream().observeForever(userObserver);

        GameMode initialMode = gameInfoStore.getModeStream().getValue();
        if (initialMode == null) {
            initialMode = gameInfoStore.getCurrentMode();
        }

        GameSessionInfo initialSession = gameInfoStore.getGameSessionStream().getValue();
        if (initialSession == null) {
            initialSession = gameInfoStore.getCurrentGameSession();
        }
        latestSessionInfo = initialSession;

        OmokBoardState initialBoard = boardStore.getBoardStateStream().getValue();
        if (initialBoard == null) {
            initialBoard = boardStore.getCurrentBoardState();
        }
        if (initialBoard == null
                || initialBoard.getWidth() <= 0
                || initialBoard.getHeight() <= 0) {
            boardStore.initializeBoard(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
            initialBoard = boardStore.getCurrentBoardState();
        }
        if (initialBoard != null) {
            boardState.setValue(initialBoard);
        }

        GameTurnState initialTurn = gameInfoStore.getTurnStateStream().getValue();
        if (initialTurn == null) {
            initialTurn = gameInfoStore.getCurrentTurnState();
        }
        onTurnChanged(initialTurn);

        onModeChanged(initialMode);

        MatchState initialMatchState = gameInfoStore.getMatchStateStream().getValue();
        if (initialMatchState != null) {
            onMatchStateChanged(initialMatchState);
        }

        User initialUser = userSessionStore.getCurrentUser();
        if (initialUser != null) {
            onUserChanged(initialUser);
        }

        if (initialSession != null) {
            onSessionChanged(initialSession);
        }

        viewEvents.setValue(GameViewEvent.AUTO_OPEN_GAME_INFO_DIALOG);
    }

    @NonNull
    public LiveData<List<GamePlayerSlot>> getPlayerSlots() {
        return playerSlots;
    }

    @NonNull
    public LiveData<Integer> getActivePlayerIndex() {
        return activePlayerIndex;
    }

    @NonNull
    public LiveData<GameMode> getCurrentMode() {
        return currentMode;
    }

    @NonNull
    public LiveData<MatchState> getMatchState() {
        return matchState;
    }

    @NonNull
    public LiveData<OmokBoardState> getBoardState() {
        return boardState;
    }

    @NonNull
    public LiveData<GameViewEvent> getViewEvents() {
        return viewEvents;
    }

    public void notifyGameReady() {
        if (readySignalSent.getAndSet(true)) {
            return;
        }
        readyInGameSessionUseCase.executeAsync(UseCase.None.INSTANCE, realtimeExecutor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Failed to send READY_IN_GAME_SESSION frame", throwable);
                        return;
                    }
                    if (result instanceof UResult.Err<?> err) {
                        Log.w(TAG, "READY_IN_GAME_SESSION rejected: " + err.message());
                    } else {
                        Log.d(TAG, "READY_IN_GAME_SESSION dispatched");
                    }
                });
    }

    public void onInfoButtonClicked() {
        viewEvents.setValue(GameViewEvent.OPEN_GAME_INFO_DIALOG);
    }

    public void onShowResultClicked() {
        viewEvents.setValue(GameViewEvent.OPEN_GAME_RESULT_DIALOG);
    }

    public void onBoardTapped() {
        gameInfoStore.advanceTurn();
    }

    public void onBoardCellTapped(int x, int y) {
        OmokBoardState currentBoard = boardStore.getCurrentBoardState();
        if (currentBoard == null
                || currentBoard.getWidth() <= 0
                || currentBoard.getHeight() <= 0) {
            return;
        }
        if (x < 0 || y < 0 || x >= currentBoard.getWidth() || y >= currentBoard.getHeight()) {
            return;
        }
        OmokStoneType nextType = resolveStoneForActiveTurn();
        if (nextType == OmokStoneType.UNKNOWN || nextType == OmokStoneType.EMPTY) {
            return;
        }
        boardStore.applyStone(new OmokStonePlacement(x, y, nextType));
        gameInfoStore.advanceTurn();
    }

    public void placeStoneExplicit(int x, int y, @NonNull OmokStoneType stoneType, boolean advanceTurn) {
        OmokBoardState currentBoard = boardStore.getCurrentBoardState();
        if (currentBoard == null
                || currentBoard.getWidth() <= 0
                || currentBoard.getHeight() <= 0) {
            return;
        }
        if (x < 0 || y < 0 || x >= currentBoard.getWidth() || y >= currentBoard.getHeight()) {
            return;
        }
        if (stoneType == null || stoneType == OmokStoneType.EMPTY || stoneType == OmokStoneType.UNKNOWN) {
            return;
        }
        boardStore.applyStone(new OmokStonePlacement(x, y, stoneType));
        if (advanceTurn) {
            gameInfoStore.advanceTurn();
        }
    }

    public void onEventHandled() {
        viewEvents.setValue(null);
    }

    private void onModeChanged(@Nullable GameMode mode) {
        if (mode == null) {
            mode = GameMode.TWO_PLAYER;
        }
        currentMode.postValue(mode);
        rebuildSlots(mode);
        gameInfoStore.setTurnIndex(0);
        activePlayerIndex.postValue(0);
    }

    private void onMatchStateChanged(@Nullable MatchState state) {
        if (state == null) {
            state = MatchState.IDLE;
        }
        matchState.postValue(state);
        if (state == MatchState.MATCHED) {
            Log.d(TAG, "Players matched. Ready to begin game.");
        }
    }

    private void onSessionChanged(@Nullable GameSessionInfo sessionInfo) {
        latestSessionInfo = sessionInfo;
        GameMode mode = currentMode.getValue();
        if (mode == null) {
            mode = gameInfoStore.getCurrentMode();
        }
        rebuildSlots(mode);
    }

    private void onUserChanged(@Nullable User user) {
        if (user == null) {
            selfDisplayName = "";
            selfUserId = "";
        } else {
            selfDisplayName = user.getDisplayName().getValue();
            selfUserId = user.getUserId().getValue();
        }
        rebuildSlots(currentMode.getValue());
    }

    private void onBoardStateChanged(@Nullable OmokBoardState state) {
        if (state == null) {
            state = OmokBoardState.empty();
        }
        boardState.postValue(state);
    }

    private void onTurnChanged(@Nullable GameTurnState state) {
        if (state == null || !state.isActive()) {
            activePlayerIndex.postValue(0);
            return;
        }
        activePlayerIndex.postValue(state.getCurrentIndex());
    }

    private OmokStoneType resolveStoneForActiveTurn() {
        GameTurnState turnState = gameInfoStore.getCurrentTurnState();
        if (turnState == null || !turnState.isActive()) {
            GameSessionInfo session = latestSessionInfo;
            if (session != null && session.hasParticipants()) {
                gameInfoStore.setTurnIndex(0);
                turnState = gameInfoStore.getCurrentTurnState();
            }
        }
        if (turnState == null || !turnState.isActive()) {
            return OmokStoneType.BLACK;
        }
        return mapStoneTypeForIndex(turnState.getCurrentIndex());
    }

    private OmokStoneType mapStoneTypeForIndex(int participantIndex) {
        if (participantIndex < 0 || TURN_ORDER.length == 0) {
            return OmokStoneType.BLACK;
        }
        return TURN_ORDER[participantIndex % TURN_ORDER.length];
    }

    private void rebuildSlots(@Nullable GameMode mode) {
        GameMode safeMode = mode != null ? mode : GameMode.TWO_PLAYER;
        int participantCount = resolveParticipantCount(safeMode);

        List<GameParticipantInfo> participants = latestSessionInfo != null
                ? new ArrayList<>(latestSessionInfo.getParticipants())
                : new ArrayList<>();

        if (!participants.isEmpty() && selfUserId != null && !selfUserId.isEmpty()) {
            for (int i = 0; i < participants.size(); i++) {
                GameParticipantInfo info = participants.get(i);
                if (selfUserId.equals(info.getUserId())) {
                    if (i != 0) {
                        participants.remove(i);
                        participants.add(0, info);
                    }
                    break;
                }
            }
        }

        if (participants.size() > participantCount) {
            participants = new ArrayList<>(participants.subList(0, participantCount));
        }

        cachedSlots.clear();
        for (int i = 0; i < 4; i++) {
            boolean withinParticipantRange = i < participantCount;
            GameParticipantInfo participant = withinParticipantRange && i < participants.size()
                    ? participants.get(i)
                    : null;

            String name = "";
            boolean empty = true;
            boolean enabled = withinParticipantRange;

            if (participant != null) {
                String participantName = participant.getDisplayName();
                if (participantName != null && !participantName.trim().isEmpty()) {
                    name = participantName;
                    empty = false;
                } else if (selfUserId != null && selfUserId.equals(participant.getUserId()) && !selfDisplayName.isEmpty()) {
                    name = selfDisplayName;
                    empty = false;
                }
            } else if (withinParticipantRange && i == 0 && !selfDisplayName.isEmpty()) {
                name = selfDisplayName;
                empty = false;
            }

            int profileIconCode = participant != null ? participant.getProfileIconCode() : 0;
            cachedSlots.add(new GamePlayerSlot(i, name, empty, enabled, profileIconCode));
        }
        playerSlots.postValue(new ArrayList<>(cachedSlots));
    }

    private int resolveParticipantCount(@NonNull GameMode mode) {
        switch (mode) {
            case FOUR_PLAYER:
                return 4;
            case THREE_PLAYER:
                return 3;
            case TWO_PLAYER:
                return 2;
            case FREE:
                return 2;
            default:
                throw new IllegalStateException("Unknown mode: " + mode);
        }
    }

    @Override
    protected void onCleared() {
        gameInfoStore.getModeStream().removeObserver(modeObserver);
        gameInfoStore.getMatchStateStream().removeObserver(matchObserver);
        gameInfoStore.getGameSessionStream().removeObserver(sessionObserver);
        gameInfoStore.getTurnStateStream().removeObserver(turnObserver);
        boardStore.getBoardStateStream().removeObserver(boardObserver);
        userSessionStore.getUserStream().removeObserver(userObserver);
        realtimeExecutor.shutdownNow();
        super.onCleared();
    }
}
