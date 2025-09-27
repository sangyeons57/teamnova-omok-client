package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.UserSessionStore;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.home.presentation.viewmodel.ScoreViewModel;

/**
 * Factory for {@link ScoreViewModel} instances.
 */
public final class ScoreViewModelFactory implements ViewModelProvider.Factory {

    private final UserSessionStore userSessionStore;

    private ScoreViewModelFactory(@NonNull UserSessionStore userSessionStore) {
        this.userSessionStore = userSessionStore;
    }

    @NonNull
    public static ScoreViewModelFactory create() {
        UseCaseContainer container = UseCaseContainer.getInstance();
        return new ScoreViewModelFactory(container.userSessionStore);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ScoreViewModel.class)) {
            return (T) new ScoreViewModel(userSessionStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
