package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.feature_home.home.presentation.viewmodel.HomeViewModel;

/**
 * Factory for creating {@link HomeViewModel} instances to allow future dependency injection.
 */
public final class HomeViewModelFactory implements ViewModelProvider.Factory {

    private HomeViewModelFactory() {
    }

    @NonNull
    public static HomeViewModelFactory create() {
        return new HomeViewModelFactory();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel();
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
