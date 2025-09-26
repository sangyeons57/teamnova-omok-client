package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.UserSessionStore;
import com.example.application.usecase.SelfDataUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.home.presentation.viewmodel.HomeViewModel;

/**
 * Factory for creating {@link HomeViewModel} instances to allow future dependency injection.
 */
public final class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final SelfDataUseCase selfDataUseCase;
    private final UserSessionStore userSessionStore;

    private HomeViewModelFactory(@NonNull SelfDataUseCase selfDataUseCase,
                                 @NonNull UserSessionStore userSessionStore) {
        this.selfDataUseCase = selfDataUseCase;
        this.userSessionStore = userSessionStore;
    }

    @NonNull
    public static HomeViewModelFactory create() {
        UseCaseContainer container = UseCaseContainer.getInstance();
        SelfDataUseCase selfDataUseCase = container.registry.get(SelfDataUseCase.class);
        UserSessionStore userSessionStore = container.userSessionStore;
        return new HomeViewModelFactory(selfDataUseCase, userSessionStore);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(selfDataUseCase, userSessionStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
