package com.example.feature_home.home.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameMode;
import com.example.core_di.UseCaseContainer;

/**
 * Captures user selections inside the Game Mode dialog for analytics.
 */
public class GameModeDialogViewModel extends ViewModel {

    private final GameInfoStore gameInfoStore;

    public GameModeDialogViewModel() {
        gameInfoStore = UseCaseContainer.getInstance().gameInfoStore;
    }

    public void onModeSelected(@NonNull GameMode mode) {
        gameInfoStore.update(mode);
    }

    @NonNull
    public GameMode getCurrentMode() {
        return gameInfoStore.getCurrentMode();
    }

    @NonNull
    public LiveData<GameMode> getModeStream() {
        return gameInfoStore.getModeStream();
    }
}
