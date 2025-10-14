package com.example.feature_game.game.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.session.UserSessionStore;
import com.example.application.session.postgame.PostGameSessionStore;
import com.example.application.usecase.ReadyInGameSessionUseCase;
import com.example.application.usecase.PlaceStoneUseCase;
import com.example.core.sound.SoundManager;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.PostGameSessionContainer;
import com.example.core_di.SoundManagerContainer;
import com.example.core_di.UseCaseContainer;
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
            PostGameSessionStore postGameSessionStore = PostGameSessionContainer.getInstance().getStore();
            SoundManager soundManager = SoundManagerContainer.getInstance().getSoundManager();
            UseCaseContainer useCaseContainer = UseCaseContainer.getInstance();
            ReadyInGameSessionUseCase readyInGameSessionUseCase = useCaseContainer.get(ReadyInGameSessionUseCase.class);
            PlaceStoneUseCase placeStoneUseCase = useCaseContainer.get(PlaceStoneUseCase.class);
            return (T) new GameViewModel(gameInfoStore, userSessionStore, readyInGameSessionUseCase, placeStoneUseCase, postGameSessionStore, soundManager);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
