package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
            return (T) new MatchingViewModel();
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
