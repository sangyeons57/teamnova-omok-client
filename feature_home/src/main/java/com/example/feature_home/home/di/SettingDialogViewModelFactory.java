package com.example.feature_home.home.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.UserSessionStore;
import com.example.application.usecase.LinkGoogleAccountUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.home.presentation.viewmodel.SettingDialogViewModel;

/**
 * Factory for creating {@link SettingDialogViewModel} instances with required dependencies.
 */
public final class SettingDialogViewModelFactory implements ViewModelProvider.Factory {

    private final LinkGoogleAccountUseCase linkGoogleAccountUseCase;
    private final UserSessionStore userSessionStore;

    private SettingDialogViewModelFactory(@NonNull LinkGoogleAccountUseCase linkGoogleAccountUseCase,
                                          @NonNull UserSessionStore userSessionStore) {
        this.linkGoogleAccountUseCase = linkGoogleAccountUseCase;
        this.userSessionStore = userSessionStore;
    }

    @NonNull
    public static SettingDialogViewModelFactory create() {
        UseCaseContainer container = UseCaseContainer.getInstance();
        return new SettingDialogViewModelFactory(
                container.registry.get(LinkGoogleAccountUseCase.class),
                container.userSessionStore
        );
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingDialogViewModel.class)) {
            return (T) new SettingDialogViewModel(linkGoogleAccountUseCase, userSessionStore);
        }
        throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
