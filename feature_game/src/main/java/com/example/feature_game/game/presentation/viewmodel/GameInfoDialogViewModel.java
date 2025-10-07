package com.example.feature_game.game.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameMode;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;
import com.example.feature_game.game.presentation.model.GameInfoSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Supplies in-game session details for the game info dialog.
 */
public class GameInfoDialogViewModel extends ViewModel {

    private static final int SLOT_COUNT = 4;

    private final GameInfoStore gameInfoStore;
    private final MutableLiveData<List<GameInfoSlot>> slots = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> dismissEvent = new MutableLiveData<>();
    private final Observer<GameSessionInfo> sessionObserver = this::onSessionUpdated;
    private final Observer<GameMode> modeObserver = this::onModeUpdated;

    private GameSessionInfo latestSession;
    private GameMode latestMode = GameMode.FREE;

    public GameInfoDialogViewModel(@NonNull GameInfoStore gameInfoStore) {
        this.gameInfoStore = gameInfoStore;

        GameMode initialMode = gameInfoStore.getModeStream().getValue();
        if (initialMode == null) {
            initialMode = gameInfoStore.getCurrentMode();
        }
        latestMode = initialMode;

        GameSessionInfo initialSession = gameInfoStore.getGameSessionStream().getValue();
        if (initialSession == null) {
            initialSession = gameInfoStore.getCurrentGameSession();
        }
        latestSession = initialSession;

        gameInfoStore.getGameSessionStream().observeForever(sessionObserver);
        gameInfoStore.getModeStream().observeForever(modeObserver);

        slots.setValue(buildSlots(latestMode, latestSession));
    }

    @NonNull
    public LiveData<List<GameInfoSlot>> getSlots() {
        return slots;
    }

    @NonNull
    public LiveData<Boolean> getDismissEvent() {
        return dismissEvent;
    }

    public void onCloseClicked() {
        dismissEvent.setValue(true);
    }

    public void onEventHandled() {
        dismissEvent.setValue(null);
    }

    private void onSessionUpdated(@Nullable GameSessionInfo sessionInfo) {
        latestSession = sessionInfo;
        updateSlots();
    }

    private void onModeUpdated(@Nullable GameMode mode) {
        if (mode == null) {
            return;
        }
        latestMode = mode;
        updateSlots();
    }

    private void updateSlots() {
        slots.postValue(buildSlots(latestMode, latestSession));
    }

    private List<GameInfoSlot> buildSlots(@NonNull GameMode mode, @Nullable GameSessionInfo sessionInfo) {
        int participantCount = resolveParticipantCount(mode);
        List<GameParticipantInfo> participants = sessionInfo != null
                ? sessionInfo.getParticipants()
                : Collections.emptyList();

        List<GameInfoSlot> result = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            boolean enabled = i < participantCount;
            if (enabled && i < participants.size()) {
                GameParticipantInfo participant = participants.get(i);
                String userId = participant.getUserId();
                String displayName = participant.getDisplayName();
                if (displayName == null || displayName.trim().isEmpty()) {
                    displayName = userId;
                }
                result.add(new GameInfoSlot(
                        i,
                        userId != null ? userId : "",
                        displayName != null ? displayName : "",
                        true,
                        enabled,
                        participant.getProfileIconCode()
                ));
            } else {
                result.add(new GameInfoSlot(
                        i,
                        "",
                        "",
                        false,
                        enabled,
                        0
                ));
            }
        }
        return result;
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
                return 2;
        }
    }

    @Override
    protected void onCleared() {
        gameInfoStore.getGameSessionStream().removeObserver(sessionObserver);
        gameInfoStore.getModeStream().removeObserver(modeObserver);
        super.onCleared();
    }
}
