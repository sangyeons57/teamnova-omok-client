package com.example.feature_game.game.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.session.UserSessionStore;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.UserSessionContainer;
import com.example.feature_game.game.presentation.viewmodel.GameViewModel;

/**
 * Factory for {@link GameViewModel} instances.
 */
public final class GameViewModelFactory implements ViewModelProvider.Factory {

    private GameViewModelFactory() {
    }

    @NonNull
    public static GameViewModelFactory create() {
        return new GameViewModelFactory();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameViewModel.class)) {
            GameInfoStore gameInfoStore = GameInfoContainer.getInstance().getStore();
            UserSessionStore userSessionStore = UserSessionContainer.getInstance().getStore();
            return (T) new GameViewModel(gameInfoStore, userSessionStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
