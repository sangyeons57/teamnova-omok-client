package com.example.teamnovaomok.ui.di;

import com.example.data.common.repository.DefaultLoginRepository;
import com.example.domain.domain.auth.repository.LoginRepository;
import com.example.domain.domain.auth.usecase.CreateAccountUseCase;
import com.example.domain.application.UseCaseProviders;
import com.example.domain.application.UseCaseRegistry;

public final class UseCaseContainer {

    public final UseCaseRegistry registry = new UseCaseRegistry();
    public final LoginRepository loginRepository = new DefaultLoginRepository();

    public UseCaseContainer() {
        registry.register(CreateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new CreateAccountUseCase(loginRepository)));
    }
}
