package com.example.feature_game.game.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.session.GameInfoStore;
import com.example.application.session.MatchState;
import com.example.feature_game.game.presentation.model.GameResultOutcome;
import com.example.feature_game.game.presentation.model.GameResultUiState;
import com.example.feature_game.game.presentation.state.GameResultDialogEvent;

/**
 * Presents snapshot information about a finished match.
 */
public class GameResultDialogViewModel extends ViewModel {

    private final GameInfoStore gameInfoStore;
    private final MutableLiveData<GameResultUiState> uiState = new MutableLiveData<>();
    private final MutableLiveData<GameResultDialogEvent> events = new MutableLiveData<>();

    public GameResultDialogViewModel(@NonNull GameInfoStore gameInfoStore) {
        this.gameInfoStore = gameInfoStore;
        GameResultUiState defaultState = new GameResultUiState(GameResultOutcome.WIN,
                3 * 60 * 1000L,
                48,
                0,
                false);
        uiState.setValue(defaultState);
    }

    @NonNull
    public LiveData<GameResultUiState> getUiState() {
        return uiState;
    }

    @NonNull
    public LiveData<GameResultDialogEvent> getEvents() {
        return events;
    }

    public void onExitClicked() {
        gameInfoStore.updateMatchState(MatchState.IDLE);
        events.setValue(GameResultDialogEvent.DISMISS);
    }

    public void onRematchClicked() {
        GameResultUiState current = uiState.getValue();
        if (current == null) {
            return;
        }
        boolean nextRequested = !current.isRematchRequested();
        int nextVotes = Math.max(0, current.getRematchVotes() + (nextRequested ? 1 : -1));
        uiState.setValue(current.withRematchVotes(nextVotes, nextRequested));
    }

    public void onEventHandled() {
        events.setValue(null);
    }
}
