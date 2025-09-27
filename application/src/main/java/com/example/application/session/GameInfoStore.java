package com.example.application.session;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * Stores game-related selections that should survive across screens for the lifetime of the process.
 */
public interface GameInfoStore {

    /** Returns the currently selected game mode. */
    @NonNull
    GameMode getCurrentMode();

    /** Updates the current game mode. */
    void update(@NonNull GameMode mode);

    /** Live stream of mode changes for UI components to react to. */
    @NonNull
    LiveData<GameMode> getModeStream();
}
