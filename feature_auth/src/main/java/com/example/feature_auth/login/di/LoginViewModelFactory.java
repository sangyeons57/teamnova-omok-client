package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.port.in.UseCaseRegistry;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.application.usecase.HelloHandshakeUseCase;
import com.example.application.usecase.LoginUseCase;
import com.example.application.usecase.TcpAuthUseCase;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core.token.TokenStore;
import com.example.core_di.TokenContainer;
import com.example.core_di.UseCaseContainer;
import com.example.feature_auth.login.presentation.viewmodel.LoginViewModel;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final UseCaseRegistry useCaseRegistry;
    private final TokenStore tokenManager;
    private final ExecutorService executorService;
    private final FragmentNavigationHost<AppNavigationKey> host;

    private LoginViewModelFactory(@NonNull ExecutorService executorService, FragmentNavigationHost<AppNavigationKey> host) {
        this.useCaseRegistry = UseCaseContainer.getInstance().registry;
        this.tokenManager = TokenContainer.getInstance();
        this.executorService = Objects.requireNonNull(executorService, "executorService");
        this.host = host;
    }

    @NonNull
    public static LoginViewModelFactory create(FragmentNavigationHostOwner<AppNavigationKey> owner) {
        return new LoginViewModelFactory(Executors.newSingleThreadExecutor(), owner.getFragmentNavigatorHost());
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            CreateAccountUseCase createAccountUseCase = useCaseRegistry.get(CreateAccountUseCase.class);
            LoginUseCase loginUseCase = useCaseRegistry.get(LoginUseCase.class);
            HelloHandshakeUseCase helloHandshakeUseCase = useCaseRegistry.get(HelloHandshakeUseCase.class);
            TcpAuthUseCase tcpAuthUseCase = useCaseRegistry.get(TcpAuthUseCase.class);
            return (T) new LoginViewModel(createAccountUseCase, loginUseCase, helloHandshakeUseCase, tcpAuthUseCase, tokenManager, executorService, host);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
