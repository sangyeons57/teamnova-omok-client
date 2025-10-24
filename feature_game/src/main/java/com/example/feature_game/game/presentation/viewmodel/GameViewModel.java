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
import com.example.application.session.GameInfoStore;
import com.example.application.session.GameMode;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;
import com.example.application.session.GameTurnState;
import com.example.application.session.MatchState;
import com.example.application.session.OmokBoardState;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStoneType;
import com.example.application.session.postgame.PlayerDisconnectReason;
import com.example.application.session.postgame.PostGameSessionState;
import com.example.application.session.postgame.PostGameSessionStore;
import com.example.application.session.TurnEndEvent;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.ReadyInGameSessionUseCase;
import com.example.application.usecase.PlaceStoneUseCase;
import com.example.application.port.out.realtime.PlaceStoneResponse;
import com.example.domain.user.entity.User;
import com.example.feature_game.game.presentation.model.GamePlayerSlot;
import com.example.feature_game.game.presentation.state.GameViewEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coordinates the Omok game screen UI state.
 */
public class GameViewModel extends ViewModel {

    private static final String TAG = "GameViewModel";
    private static final int DEFAULT_BOARD_SIZE = 10;
    private static final int TURN_TOTAL_SECONDS = 15;
    private static final OmokStoneType[] TURN_ORDER = new OmokStoneType[]{
            OmokStoneType.RED,
            OmokStoneType.BLUE,
            OmokStoneType.YELLOW,
            OmokStoneType.GREEN
    };

    private final GameInfoStore gameInfoStore;
    private final UserSessionStore userSessionStore;
    private final ReadyInGameSessionUseCase readyInGameSessionUseCase;
    private final PlaceStoneUseCase placeStoneUseCase;
    private final PostGameSessionStore postGameSessionStore;
    private final OmokBoardStore boardStore;
    private final ExecutorService realtimeExecutor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<GamePlayerSlot>> playerSlots = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Integer> activePlayerIndex = new MutableLiveData<>(0);
    private final MutableLiveData<GameMode> currentMode = new MutableLiveData<>(GameMode.TWO_PLAYER);
    private final MutableLiveData<MatchState> matchState = new MutableLiveData<>(MatchState.IDLE);
    private final MutableLiveData<OmokBoardState> boardState = new MutableLiveData<>(OmokBoardState.empty());
    private final MutableLiveData<GameViewEvent> viewEvents = new MutableLiveData<>();
    private final MutableLiveData<PlaceStoneResponse.Status> placementErrors = new MutableLiveData<>();
    private final MutableLiveData<Integer> remainingSeconds = new MutableLiveData<>(TURN_TOTAL_SECONDS);
    private final Observer<GameMode> modeObserver = this::onModeChanged;
    private final Observer<MatchState> matchObserver = this::onMatchStateChanged;
    private final Observer<GameSessionInfo> sessionObserver = this::onSessionChanged;
    private final Observer<User> userObserver = this::onUserChanged;
    private final Observer<OmokBoardState> boardObserver = this::onBoardStateChanged;
    private final Observer<GameTurnState> turnObserver = this::onTurnChanged;
    private final Observer<PostGameSessionState> postGameObserver = this::onPostGameStateChanged;
    private final Observer<TurnEndEvent> turnEndEventObserver = this::onTurnEndEvent;

    private final List<GamePlayerSlot> cachedSlots = new ArrayList<>(4);
    private final Map<String, PlayerDisconnectReason> disconnectedPlayers = new HashMap<>();
    private final AtomicBoolean readySignalSent = new AtomicBoolean(false);
    private final AtomicBoolean postGameNavigationDispatched = new AtomicBoolean(false);
    private String selfDisplayName = "";
    private String selfUserId = "";
    private GameSessionInfo latestSessionInfo;
    private final Handler uiCountdownHandler = new Handler(Looper.getMainLooper());
    private Runnable uiCountdownRunnable;


    public GameViewModel(@NonNull GameInfoStore gameInfoStore,
                         @NonNull UserSessionStore userSessionStore,
                         @NonNull ReadyInGameSessionUseCase readyInGameSessionUseCase,
                         @NonNull PlaceStoneUseCase placeStoneUseCase,
                         @NonNull PostGameSessionStore postGameSessionStore) {
        this.gameInfoStore = gameInfoStore;
        this.userSessionStore = userSessionStore;
        this.readyInGameSessionUseCase = readyInGameSessionUseCase;
        this.placeStoneUseCase = placeStoneUseCase;
        this.boardStore = gameInfoStore.getBoardStore();
        this.postGameSessionStore = postGameSessionStore;

        gameInfoStore.getModeStream().observeForever(modeObserver);
        gameInfoStore.getMatchStateStream().observeForever(matchObserver);
        gameInfoStore.getGameSessionStream().observeForever(sessionObserver);
        gameInfoStore.getTurnStateStream().observeForever(turnObserver);
        boardStore.getBoardStateStream().observeForever(boardObserver);
        userSessionStore.getUserStream().observeForever(userObserver);
        postGameSessionStore.getStateStream().observeForever(postGameObserver);
        gameInfoStore.getTurnEndEventStream().observeForever(turnEndEventObserver);

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
    public LiveData<OmokBoardState> getBoardState() {
        return boardState;
    }

    @NonNull
    public LiveData<GameViewEvent> getViewEvents() {
        return viewEvents;
    }

    @NonNull
    public LiveData<PlaceStoneResponse.Status> getPlacementErrors() {
        return placementErrors;
    }

    @NonNull
    public LiveData<Integer> getRemainingSeconds() {
        return remainingSeconds;
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



    public void onBoardCellTapped(int x, int y) {
        OmokBoardState currentBoard = boardStore.getCurrentBoardState();
        if (currentBoard.getWidth() <= 0 || currentBoard.getHeight() <= 0) {
            return;
        }
        if (x < 0 || y < 0 || x >= currentBoard.getWidth() || y >= currentBoard.getHeight()) {
            return;
        }
        OmokStoneType existingStone = currentBoard.getStone(x, y);
        if (existingStone.isPlaced()) {
            Log.w(TAG, "Ignoring tap on occupied cell (" + x + "," + y + ")");
            return;
        }
        if (!isSelfTurn()) {
            Log.w(TAG, "Ignoring tap → not current player's turn");
            return;
        }
        OmokStoneType nextType = resolveStoneForActiveTurn();
        if (nextType == OmokStoneType.UNKNOWN || nextType == OmokStoneType.EMPTY) {
            return;
        }
        dispatchPlaceStone(x, y, nextType);
    }

    private void dispatchPlaceStone(int x, int y, @NonNull OmokStoneType expectedStone) {
        placeStoneUseCase.executeAsync(new PlaceStoneUseCase.Params(x, y), realtimeExecutor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "PLACE_STONE request failed for (" + x + "," + y + ")", throwable);
                        placementErrors.postValue(PlaceStoneResponse.Status.UNKNOWN);
                        return;
                    }
                    if (result instanceof UResult.Err<?> err) {
                        Log.w(TAG, "PLACE_STONE rejected (" + x + "," + y + "): " + err.message());
                        placementErrors.postValue(PlaceStoneResponse.Status.UNKNOWN);
                        return;
                    }
                    if (result instanceof UResult.Ok<PlaceStoneResponse> ok && ok.value() != null) {
                        PlaceStoneResponse response = ok.value();
                        if (!response.isSuccess()) {
                            placementErrors.postValue(response.status());
                        }
                        // No error to post if successful
                    } else {
                        // This case handles null result or non-PlaceStoneResponse types, which is unexpected.
                        Log.e(TAG, "Unexpected result from PLACE_STONE: " + result);
                        placementErrors.postValue(PlaceStoneResponse.Status.UNKNOWN);
                    }
                });
    }


    private boolean isSelfTurn() {
        GameTurnState turnState = gameInfoStore.getCurrentTurnState();
        if (!turnState.isActive()) {
            return false;
        }
        String selfPlayerId = selfUserId; // Use selfUserId directly
        return selfPlayerId != null && selfPlayerId.equals(turnState.getCurrentPlayerId());
    }

     public void onEventHandled() {
        viewEvents.setValue(null);
    }

    public void onPlacementFeedbackHandled() {
        placementErrors.setValue(null);
    }

    private void onModeChanged(@Nullable GameMode mode) {
        if (mode == null) {
            mode = GameMode.TWO_PLAYER;
        }
        currentMode.postValue(mode);
        rebuildSlots(mode);
        activePlayerIndex.postValue(0);
        // lastTurnParticipantIndex = -1; // No longer needed
    }

    private void onMatchStateChanged(@Nullable MatchState state) {
        if (state == null) {
            state = MatchState.IDLE;
        }
        matchState.postValue(state);
        if (state == MatchState.MATCHED) {
            Log.d(TAG, "Players matched. Ready to begin game.");
        } else if (state == MatchState.IDLE) {
            synchronized (disconnectedPlayers) {
                if (!disconnectedPlayers.isEmpty()) {
                    disconnectedPlayers.clear();
                    rebuildSlots(currentMode.getValue());
                }
            }
            // lastTurnParticipantIndex = -1; // No longer needed
        }
    }

    private void onSessionChanged(@Nullable GameSessionInfo sessionInfo) {
        GameSessionInfo previousSession = latestSessionInfo;
        latestSessionInfo = sessionInfo;
        postGameNavigationDispatched.set(false);
        if (sessionInfo == null
                || previousSession == null
                || !previousSession.getSessionId().equals(sessionInfo.getSessionId())) {
            synchronized (disconnectedPlayers) {
                disconnectedPlayers.clear();
            }
            // lastTurnParticipantIndex = -1; // No longer needed
        }
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
            // lastTurnParticipantIndex = -1; // No longer needed
            return;
        }
        // currentIndex is no longer available in GameTurnState
        // We need to find the index of the current player based on their ID
        GameSessionInfo session = latestSessionInfo;
        if (session == null || state.getCurrentPlayerId() == null) {
            activePlayerIndex.postValue(0);
            return;
        }

        int currentParticipantIndex = -1;
        currentParticipantIndex = session.getUids().indexOf(state.getCurrentPlayerId());

        activePlayerIndex.postValue(currentParticipantIndex);
        // lastTurnParticipantIndex is no longer used for isNewTurn logic
        // The server is the source of truth for new turns.
        remainingSeconds.postValue(state.getRemainingSeconds());
        startUiCountdown(state.getEndAt());
        // lastTurnParticipantIndex = currentParticipantIndex; // No longer needed
    }

    private void onPostGameStateChanged(@Nullable PostGameSessionState state) {
        if (state == null) {
            return;
        }
        boolean disconnectedChanged = updateDisconnectedPlayers(state);
        if (disconnectedChanged) {
            rebuildSlots(currentMode.getValue());
        }
        if (postGameNavigationDispatched.get()) {
            return;
        }
        if (state.getOutcomes().isEmpty()) {
            return;
        }
        GameSessionInfo sessionInfo = latestSessionInfo;
        if (sessionInfo == null) {
            return;
        }
        String currentSessionId = sessionInfo.getSessionId();
        if (currentSessionId == null || currentSessionId.isEmpty()) {
            return;
        }
        if (!currentSessionId.equals(state.getSessionId())) {
            return;
        }
        if (postGameNavigationDispatched.compareAndSet(false, true)) {
            viewEvents.postValue(GameViewEvent.OPEN_POST_GAME_SCREEN);
        }
    }

    private void onTurnEndEvent(@Nullable TurnEndEvent event) {
        if (event == null) {
            return;
        }
        if (event.isTimedOut()) {
            viewEvents.postValue(GameViewEvent.SHOW_TURN_TIMEOUT_MESSAGE);
        }
    }

    private OmokStoneType resolveStoneForActiveTurn() {
        GameTurnState turnState = gameInfoStore.getCurrentTurnState();
        if (!turnState.isActive() || turnState.getCurrentPlayerId() == null) {
            // If no active turn or current player, we cannot resolve a stone type.
            // The server should initiate the turn.
            return OmokStoneType.BLACK;
        }
        // We need to map the currentPlayerId to an index to get the stone type.
        GameSessionInfo session = latestSessionInfo;
        if (session == null) {
            return OmokStoneType.BLACK;
        }
        int currentParticipantIndex = session.getParticipants().indexOf(turnState.getCurrentPlayerId());

        return mapStoneTypeForIndex(currentParticipantIndex);
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

        // Get participants from the session info's map values
        List<GameParticipantInfo> participants = latestSessionInfo != null
                ? new ArrayList<>(latestSessionInfo.getParticipants())
                : new ArrayList<>();

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
            String userId = "";
            boolean empty = true;
            boolean enabled = withinParticipantRange;

            if (participant != null) {
                userId = participant.getUserId();
                String participantName = participant.getDisplayName();
                if (participantName != null && !participantName.trim().isEmpty()) {
                    name = participantName;
                    empty = false;
                } else if (selfUserId != null && selfUserId.equals(participant.getUserId()) && !selfDisplayName.isEmpty()) {
                    name = selfDisplayName;
                    empty = false;
                }
            } else if (withinParticipantRange && i == 0 && !selfDisplayName.isEmpty()) {
                userId = selfUserId;
                name = selfDisplayName;
                empty = false;
            }

            if (userId == null) {
                userId = "";
            }
            PlayerDisconnectReason reason = resolveDisconnectReason(userId);
            boolean disconnected = reason != PlayerDisconnectReason.UNKNOWN;
            if (disconnected) {
                enabled = false;
            }

            int profileIconCode = participant != null ? participant.getProfileIconCode() : 0;
            cachedSlots.add(new GamePlayerSlot(i, userId, name, empty, enabled, profileIconCode, reason));
        }
        playerSlots.postValue(new ArrayList<>(cachedSlots));
    }

    private boolean updateDisconnectedPlayers(@NonNull PostGameSessionState state) {
        String sessionId = state.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            synchronized (disconnectedPlayers) {
                if (disconnectedPlayers.isEmpty()) {
                    return false;
                }
                disconnectedPlayers.clear();
                return true;
            }
        }
        GameSessionInfo sessionInfo = latestSessionInfo;
        if (sessionInfo == null || !sessionId.equals(sessionInfo.getSessionId())) {
            return false;
        }
        Map<String, PlayerDisconnectReason> incoming = state.getDisconnectedPlayers();
        synchronized (disconnectedPlayers) {
            if (disconnectedPlayers.equals(incoming)) {
                return false;
            }
            disconnectedPlayers.clear();
            disconnectedPlayers.putAll(incoming);
            return true;
        }
    }

    @NonNull
    private PlayerDisconnectReason resolveDisconnectReason(@Nullable String userId) {
        if (userId == null || userId.isEmpty()) {
            return PlayerDisconnectReason.UNKNOWN;
        }
        synchronized (disconnectedPlayers) {
            PlayerDisconnectReason reason = disconnectedPlayers.get(userId);
            return reason != null ? reason : PlayerDisconnectReason.UNKNOWN;
        }
    }

    private void startUiCountdown(long endAtMillis) {
        cancelUiCountdown();
        if (endAtMillis <= 0) {
            remainingSeconds.postValue(0);
            return;
        }

        uiCountdownRunnable = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long remainingMillis = endAtMillis - now;
                int seconds = (int) Math.max(0, remainingMillis / 1000);
                remainingSeconds.postValue(seconds);

                if (seconds > 0) {
                    uiCountdownHandler.postDelayed(this, 1000);
                } else {
                    uiCountdownRunnable = null;
                }
            }
        };
        uiCountdownHandler.post(uiCountdownRunnable);
    }

    private void cancelUiCountdown() {
        if (uiCountdownRunnable != null) {
            uiCountdownHandler.removeCallbacks(uiCountdownRunnable);
            uiCountdownRunnable = null;
        }
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
        postGameSessionStore.getStateStream().removeObserver(postGameObserver);
        gameInfoStore.getTurnEndEventStream().removeObserver(turnEndEventObserver);
        cancelUiCountdown();
        realtimeExecutor.shutdownNow();
        super.onCleared();
    }
}
