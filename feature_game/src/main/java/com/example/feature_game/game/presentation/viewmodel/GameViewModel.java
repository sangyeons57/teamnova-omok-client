package com.example.feature_game.game.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameMode;
import com.example.application.session.MatchState;
import com.example.application.session.UserSessionStore;
import com.example.domain.user.entity.User;
import com.example.feature_game.game.presentation.model.GamePlayerSlot;
import com.example.feature_game.game.presentation.state.GameViewEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordinates the Omok game screen UI state.
 */
public class GameViewModel extends ViewModel {

    private static final String TAG = "GameViewModel";

    private final GameInfoStore gameInfoStore;
    private final UserSessionStore userSessionStore;
    private final MutableLiveData<List<GamePlayerSlot>> playerSlots = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Integer> activePlayerIndex = new MutableLiveData<>(0);
    private final MutableLiveData<GameMode> currentMode = new MutableLiveData<>(GameMode.TWO_PLAYER);
    private final MutableLiveData<MatchState> matchState = new MutableLiveData<>(MatchState.IDLE);
    private final MutableLiveData<GameViewEvent> viewEvents = new MutableLiveData<>();
    private final Observer<GameMode> modeObserver = this::onModeChanged;
    private final Observer<MatchState> matchObserver = this::onMatchStateChanged;
    private final Observer<User> userObserver = this::onUserChanged;

    private final List<GamePlayerSlot> cachedSlots = new ArrayList<>(4);
    private String selfDisplayName = "";

    public GameViewModel(@NonNull GameInfoStore gameInfoStore,
                         @NonNull UserSessionStore userSessionStore) {
        this.gameInfoStore = gameInfoStore;
        this.userSessionStore = userSessionStore;

        gameInfoStore.getModeStream().observeForever(modeObserver);
        gameInfoStore.getMatchStateStream().observeForever(matchObserver);
        userSessionStore.getUserStream().observeForever(userObserver);

        GameMode initialMode = gameInfoStore.getModeStream().getValue();
        if (initialMode == null) {
            initialMode = gameInfoStore.getCurrentMode();
        }
        onModeChanged(initialMode);

        MatchState initialMatchState = gameInfoStore.getMatchStateStream().getValue();
        if (initialMatchState != null) {
            onMatchStateChanged(initialMatchState);
        }

        User initialUser = userSessionStore.getCurrentUser();
        if (initialUser != null) {
            onUserChanged(initialUser);
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
    public LiveData<GameViewEvent> getViewEvents() {
        return viewEvents;
    }

    public void onInfoButtonClicked() {
        viewEvents.setValue(GameViewEvent.OPEN_GAME_INFO_DIALOG);
    }

    public void onShowResultClicked() {
        viewEvents.setValue(GameViewEvent.OPEN_GAME_RESULT_DIALOG);
    }

    public void onBoardTapped() {
        List<GamePlayerSlot> slots = playerSlots.getValue();
        if (slots == null || slots.isEmpty()) {
            return;
        }
        int participantCount = 0;
        for (GamePlayerSlot slot : slots) {
            if (slot.isEnabled()) {
                participantCount++;
            }
        }
        if (participantCount <= 0) {
            return;
        }
        Integer current = activePlayerIndex.getValue();
        int nextIndex = ((current != null ? current : 0) + 1) % participantCount;
        activePlayerIndex.setValue(nextIndex);
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

    private void onUserChanged(@Nullable User user) {
        if (user == null) {
            selfDisplayName = "";
        } else {
            selfDisplayName = user.getDisplayName().getValue();
        }
        rebuildSlots(currentMode.getValue());
    }

    private void rebuildSlots(@Nullable GameMode mode) {
        GameMode safeMode = mode != null ? mode : GameMode.TWO_PLAYER;
        int participantCount = resolveParticipantCount(safeMode);

        cachedSlots.clear();
        for (int i = 0; i < 4; i++) {
            boolean withinParticipantRange = i < participantCount;
            boolean isSelf = i == 0 && withinParticipantRange;
            String name;
            boolean empty;
            boolean enabled;
            if (isSelf) {
                name = selfDisplayName;
                empty = false;
                enabled = true;
            } else if (withinParticipantRange) {
                name = "";
                empty = true;
                enabled = true;
            } else {
                name = "";
                empty = true;
                enabled = false;
            }
            cachedSlots.add(new GamePlayerSlot(i, name, empty, enabled));
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
        userSessionStore.getUserStream().removeObserver(userObserver);
        super.onCleared();
    }
}
