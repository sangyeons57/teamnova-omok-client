package com.example.feature_game.game.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.core_di.GameInfoContainer;
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
            GameInfoStore gameInfoStore = GameInfoContainer.getInstance().getStore();
            OmokBoardStore boardStore = GameInfoContainer.getInstance().getBoardStore();
            return (T) new GameInfoDialogViewModel(gameInfoStore, boardStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
