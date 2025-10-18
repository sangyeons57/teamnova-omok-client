package com.example.feature_game.game.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.session.UserSessionStore;
import com.example.application.session.postgame.PostGameSessionStore;
import com.example.application.usecase.PostGameDecisionUseCase;
import com.example.application.usecase.SelfDataUseCase;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.PostGameSessionContainer;
import com.example.core_di.UseCaseContainer;
import com.example.core_di.UserSessionContainer;
import com.example.feature_game.game.presentation.viewmodel.PostGameViewModel;

/**
 * Factory for {@link PostGameViewModel} instances.
 */
public final class PostGameViewModelFactory implements ViewModelProvider.Factory {

    private PostGameViewModelFactory() {
    }

    @NonNull
    public static PostGameViewModelFactory create() {
        return new PostGameViewModelFactory();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PostGameViewModel.class)) {
            GameInfoStore gameInfoStore = GameInfoContainer.getInstance().getStore();
            PostGameSessionStore postGameSessionStore = PostGameSessionContainer.getInstance().getStore();
            UserSessionStore userSessionStore = UserSessionContainer.getInstance().getStore();
            UseCaseContainer useCaseContainer = UseCaseContainer.getInstance();
            SelfDataUseCase selfDataUseCase = UseCaseContainer.getInstance().get(SelfDataUseCase.class);
            PostGameDecisionUseCase postGameDecisionUseCase = useCaseContainer.get(PostGameDecisionUseCase.class);
            return (T) new PostGameViewModel(gameInfoStore, postGameSessionStore, userSessionStore, selfDataUseCase, postGameDecisionUseCase);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
