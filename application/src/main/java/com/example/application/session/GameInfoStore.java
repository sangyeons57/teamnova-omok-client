package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Stores game-related selections that should survive across screens for the lifetime of the process.
 */
public class GameInfoStore {

    private final AtomicReference<GameMode> currentMode = new AtomicReference<>(GameMode.FREE);
    private final MutableLiveData<GameMode> modeStream = new MutableLiveData<>(GameMode.FREE);

    private final AtomicReference<MatchState> currentMatchState = new AtomicReference<>(MatchState.IDLE);
    private final MutableLiveData<MatchState> matchStateStream = new MutableLiveData<>(MatchState.IDLE);

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
        matchStateStream.postValue(state);
    }
}
