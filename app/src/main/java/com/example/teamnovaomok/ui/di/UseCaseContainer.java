package com.example.teamnovaomok.ui.di;

import com.example.data.common.repository.DefaultLoginRepository;
import com.example.domain.auth.repository.LoginRepository;
import com.example.domain.auth.usecase.CreateAccountUseCase;
import com.example.domain.usecase.UseCaseProviders;
import com.example.domain.usecase.UseCaseRegistry;

public final class UseCaseContainer {

    public final UseCaseRegistry registry = new UseCaseRegistry();
    public final LoginRepository loginRepository = new DefaultLoginRepository();

    public UseCaseContainer() {
        registry.register(CreateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new CreateAccountUseCase(loginRepository)));
    }
}
