package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Stores the mutable Omok board state and exposes updates via LiveData.
 */
public final class OmokBoardStore {

    private final AtomicReference<OmokBoardState> currentBoardState = new AtomicReference<>(OmokBoardState.empty());
    private final MutableLiveData<OmokBoardState> boardStateStream = new MutableLiveData<>(OmokBoardState.empty());

    @NonNull
    public OmokBoardState getCurrentBoardState() {
        OmokBoardState state = currentBoardState.get();
        return state != null ? state : OmokBoardState.empty();
    }

    @NonNull
    public LiveData<OmokBoardState> getBoardStateStream() {
        OmokBoardState existing = currentBoardState.get();
        if (existing != null && boardStateStream.getValue() == null) {
            boardStateStream.setValue(existing);
        }
        return boardStateStream;
    }

    public void initializeBoard(int width, int height) {
        updateBoardState(OmokBoardState.create(width, height));
    }

    public void updateBoardSnapshot(int width, int height, @NonNull OmokStoneType[] cells) {
        updateBoardState(OmokBoardState.fromCells(width, height, cells));
    }

    private void updateBoardState(@NonNull OmokBoardState boardState) {
        currentBoardState.set(boardState);
        boardStateStream.postValue(boardState);
    }

    public void applyStone(@NonNull OmokStonePlacement placement) {
        OmokBoardState next = getCurrentBoardState().withStone(placement);
        updateBoardState(next);
    }

    public void clearBoard() {
        updateBoardState(OmokBoardState.empty());
    }
}
