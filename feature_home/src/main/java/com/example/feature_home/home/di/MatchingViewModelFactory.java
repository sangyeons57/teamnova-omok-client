package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.GameInfoStore;
import com.example.application.usecase.JoinMatchUseCase;
import com.example.application.usecase.LeaveMatchUseCase;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.home.presentation.viewmodel.MatchingViewModel;

/**
 * Factory for {@link MatchingViewModel} instances.
 */
public final class MatchingViewModelFactory implements ViewModelProvider.Factory {

    private MatchingViewModelFactory() {
    }

    @NonNull
    public static MatchingViewModelFactory create() {
        return new MatchingViewModelFactory();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MatchingViewModel.class)) {
            UseCaseContainer useCaseContainer = UseCaseContainer.getInstance();
            GameInfoStore gameInfoStore = GameInfoContainer.getInstance().getStore();
            JoinMatchUseCase joinMatchUseCase = useCaseContainer.get(JoinMatchUseCase.class);
            LeaveMatchUseCase leaveMatchUseCase = useCaseContainer.get(LeaveMatchUseCase.class);
            return (T) new MatchingViewModel(joinMatchUseCase, leaveMatchUseCase, gameInfoStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
