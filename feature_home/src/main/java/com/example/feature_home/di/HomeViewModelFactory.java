package com.example.feature_home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.SelfDataUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.presentation.viewmodel.HomeViewModel;

/**
 * Factory for creating {@link HomeViewModel} instances to allow future dependency injection.
 */
public final class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final SelfDataUseCase selfDataUseCase;
    private final UserSessionStore userSessionStore;
    private final GameInfoStore gameInfoStore;

    private HomeViewModelFactory(@NonNull SelfDataUseCase selfDataUseCase,
                                 @NonNull UserSessionStore userSessionStore,
                                 @NonNull GameInfoStore gameInfoStore) {
        this.selfDataUseCase = selfDataUseCase;
        this.userSessionStore = userSessionStore;
        this.gameInfoStore = gameInfoStore;
    }

    @NonNull
    public static HomeViewModelFactory create() {
        UseCaseContainer container = UseCaseContainer.getInstance();
        SelfDataUseCase selfDataUseCase = container.registry.get(SelfDataUseCase.class);
        UserSessionStore userSessionStore = container.userSessionStore;
        GameInfoStore gameInfoStore = container.gameInfoStore;
        return new HomeViewModelFactory(selfDataUseCase, userSessionStore, gameInfoStore);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(selfDataUseCase,userSessionStore, gameInfoStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
