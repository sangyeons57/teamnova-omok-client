package com.example.teamnovaomok.ui.di;

import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.in.UseCaseProviders;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.port.out.IdentifyRepository;
import com.example.application.port.out.TermsRepository;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.application.usecase.AllTermsAcceptancesUseCase;
import com.example.data.repository.IdentifyRepositoryImpl;
import com.example.data.repository.TermsRepositoryImpl;

public final class UseCaseContainer {

    public final UseCaseConfig defaultConfig = UseCaseConfig.defaultConfig();
    public final UseCaseRegistry registry = new UseCaseRegistry();
    public final IdentifyRepository identifyRepository = new IdentifyRepositoryImpl();
    public final TermsRepository termsRepository = new TermsRepositoryImpl();

    public UseCaseContainer() {
        registry.register(CreateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new CreateAccountUseCase(defaultConfig, identifyRepository)));
        registry.register(AllTermsAcceptancesUseCase.class,
                UseCaseProviders.singleton(() -> new AllTermsAcceptancesUseCase(defaultConfig, termsRepository)));
    }
}
