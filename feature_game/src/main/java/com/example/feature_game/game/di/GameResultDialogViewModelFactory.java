package com.example.feature_game.game.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.core_di.GameInfoContainer;
import com.example.feature_game.game.presentation.viewmodel.GameResultDialogViewModel;

/**
 * Factory for {@link GameResultDialogViewModel}.
 */
public final class GameResultDialogViewModelFactory implements ViewModelProvider.Factory {

    private GameResultDialogViewModelFactory() {
    }

    @NonNull
    public static GameResultDialogViewModelFactory create() {
        return new GameResultDialogViewModelFactory();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameResultDialogViewModel.class)) {
            GameInfoStore gameInfoStore = GameInfoContainer.getInstance().getStore();
            return (T) new GameResultDialogViewModel(gameInfoStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
