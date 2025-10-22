package com.example.feature_game.game.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameTurnState;
import com.example.application.session.OmokBoardState;
import com.example.application.session.OmokBoardStore;

import java.util.Arrays;
import java.util.List;

/**
 * Supplies overlay state for the game info dialog.
 */
public class GameInfoDialogViewModel extends ViewModel {

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore omokBoardStore;
    private final MutableLiveData<Boolean> dismissEvent = new MutableLiveData<>();
    private final MutableLiveData<GameTurnState> turnState = new MutableLiveData<>(GameTurnState.idle());
    private final MutableLiveData<OmokBoardState> boardState = new MutableLiveData<>(OmokBoardState.empty());
    private final MutableLiveData<GameParticipantInfo> activeParticipant = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> activeRuleIds = new MutableLiveData<>(Arrays.asList(1, 2, 3));

    private final Observer<GameTurnState> turnObserver = this::onTurnUpdated;
    private final Observer<OmokBoardState> boardObserver = this::onBoardUpdated;

    public GameInfoDialogViewModel(@NonNull GameInfoStore gameInfoStore,
                                   @NonNull OmokBoardStore omokBoardStore) {
        this.gameInfoStore = gameInfoStore;
        this.omokBoardStore = omokBoardStore;

        GameTurnState initialTurn = gameInfoStore.getTurnStateStream().getValue();
        if (initialTurn == null) {
            initialTurn = gameInfoStore.getCurrentTurnState();
        }
        if (initialTurn != null) {
            turnState.setValue(initialTurn);
        }

        OmokBoardState initialBoard = omokBoardStore.getBoardStateStream().getValue();
        if (initialBoard == null) {
            initialBoard = omokBoardStore.getCurrentBoardState();
        }
        if (initialBoard != null) {
            boardState.setValue(initialBoard);
        }

        GameParticipantInfo participant = gameInfoStore.getCurrentTurnParticipant();
        if (participant != null) {
            activeParticipant.setValue(participant);
        }

        gameInfoStore.getTurnStateStream().observeForever(turnObserver);
        omokBoardStore.getBoardStateStream().observeForever(boardObserver);
    }

    @NonNull
    public LiveData<List<Integer>> getActiveRuleIds() {
        return activeRuleIds;
    }

    @NonNull
    public LiveData<GameTurnState> getTurnState() {
        return turnState;
    }

    @NonNull
    public LiveData<OmokBoardState> getBoardState() {
        return boardState;
    }

    @NonNull
    public LiveData<GameParticipantInfo> getActiveParticipant() {
        return activeParticipant;
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

    private void onTurnUpdated(@Nullable GameTurnState state) {
        if (state == null) {
            state = GameTurnState.idle();
        }
        turnState.postValue(state);
        GameParticipantInfo participant = gameInfoStore.getCurrentTurnParticipant();
        activeParticipant.postValue(participant);
    }

    private void onBoardUpdated(@Nullable OmokBoardState state) {
        if (state == null) {
            state = OmokBoardState.empty();
        }
        boardState.postValue(state);
    }

    @Override
    protected void onCleared() {
        gameInfoStore.getTurnStateStream().removeObserver(turnObserver);
        omokBoardStore.getBoardStateStream().removeObserver(boardObserver);
        super.onCleared();
    }
}
