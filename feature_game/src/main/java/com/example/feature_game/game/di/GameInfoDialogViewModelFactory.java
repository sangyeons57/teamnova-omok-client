package com.example.feature_game.game.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel;

/**
 * Factory for {@link GameInfoDialogViewModel}.
 */
public final class GameInfoDialogViewModelFactory implements ViewModelProvider.Factory {

    private GameInfoDialogViewModelFactory() {
    }

    @NonNull
    public static GameInfoDialogViewModelFactory create() {
        return new GameInfoDialogViewModelFactory();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameInfoDialogViewModel.class)) {
            return (T) new GameInfoDialogViewModel();
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
