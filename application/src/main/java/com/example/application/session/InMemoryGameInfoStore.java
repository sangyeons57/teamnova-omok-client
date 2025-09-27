package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory implementation backed by a {@link MutableLiveData} for UI consumption.
 */
public final class InMemoryGameInfoStore implements GameInfoStore {

    private final AtomicReference<GameMode> current = new AtomicReference<>(GameMode.FREE);
    private final MutableLiveData<GameMode> modeStream = new MutableLiveData<>(GameMode.FREE);

    @NonNull
    @Override
    public GameMode getCurrentMode() {
        return current.get();
    }

    @Override
    public void update(@NonNull GameMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode == null");
        }
        current.set(mode);
        modeStream.postValue(mode);
    }

    @NonNull
    @Override
    public LiveData<GameMode> getModeStream() {
        GameMode existing = current.get();
        if (existing != null && modeStream.getValue() == null) {
            modeStream.setValue(existing);
        }
        return modeStream;
    }
}
