package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.HelloHandshakeUseCase;
import com.example.application.usecase.SelfDataUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.home.presentation.viewmodel.HomeViewModel;

/**
 * Factory for creating {@link HomeViewModel} instances to allow future dependency injection.
 */
public final class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final SelfDataUseCase selfDataUseCase;
    private final HelloHandshakeUseCase helloHandshakeUseCase;
    private final UserSessionStore userSessionStore;
    private final GameInfoStore gameInfoStore;

    private HomeViewModelFactory(@NonNull SelfDataUseCase selfDataUseCase,
                                 @NonNull HelloHandshakeUseCase helloHandshakeUseCase,
                                 @NonNull UserSessionStore userSessionStore,
                                 @NonNull GameInfoStore gameInfoStore) {
        this.selfDataUseCase = selfDataUseCase;
        this.helloHandshakeUseCase = helloHandshakeUseCase;
        this.userSessionStore = userSessionStore;
        this.gameInfoStore = gameInfoStore;
    }

    @NonNull
    public static HomeViewModelFactory create() {
        UseCaseContainer container = UseCaseContainer.getInstance();
        SelfDataUseCase selfDataUseCase = container.registry.get(SelfDataUseCase.class);
        HelloHandshakeUseCase helloHandshakeUseCase = container.registry.get(HelloHandshakeUseCase.class);
        UserSessionStore userSessionStore = container.userSessionStore;
        GameInfoStore gameInfoStore = container.gameInfoStore;
        return new HomeViewModelFactory(selfDataUseCase, helloHandshakeUseCase, userSessionStore, gameInfoStore);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(selfDataUseCase, helloHandshakeUseCase, userSessionStore, gameInfoStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
