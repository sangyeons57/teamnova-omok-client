package com.example.teamnovaomok.ui.di;

import com.example.data.repository.IdentifyRepositoryImpl;
import com.example.application.port.in.UseCaseProviders;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.port.out.IdentifyRepository;
import com.example.application.usecase.CreateAccountUseCase;

public final class UseCaseContainer {

    public final UseCaseRegistry registry = new UseCaseRegistry();
    public final IdentifyRepository identifyRepository = new IdentifyRepositoryImpl();

    public UseCaseContainer() {
        registry.register(CreateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new CreateAccountUseCase(identifyRepository)));
    }
}
