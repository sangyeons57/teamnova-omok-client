package com.example.core_di;

import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.in.UseCaseProviders;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.application.port.out.user.TermsRepository;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.application.usecase.AllTermsAcceptancesUseCase;
import com.example.application.usecase.LoginUseCase;
import com.example.application.usecase.LogoutUseCase;
import com.example.core.event.AppEventBus;
import com.example.core.token.TokenStore;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.mapper.IdentityMapper;
import com.example.data.repository.user.IdentifyRepositoryImpl;
import com.example.data.repository.user.TermsRepositoryImpl;

public final class UseCaseContainer {

    private static UseCaseContainer instance;
    public static UseCaseContainer getInstance() {
        if (instance == null) {
            instance = new UseCaseContainer();
        }
        return instance;
    }

    public final DefaultPhpServerDataSource phpServerDataSource = new DefaultPhpServerDataSource(HttpClientContainer.getInstance().get());
    public final UseCaseConfig defaultConfig = UseCaseConfig.defaultConfig();
    public final UseCaseRegistry registry = new UseCaseRegistry();
    public final IdentifyRepository identifyRepository = new IdentifyRepositoryImpl(phpServerDataSource, new IdentityMapper());
    public final TermsRepository termsRepository = new TermsRepositoryImpl(phpServerDataSource);
    public final TokenStore token = TokenContainer.getInstance();
    public final AppEventBus eventBus = EventBusContainer.getInstance();

    public UseCaseContainer() {
        registry.register(CreateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new CreateAccountUseCase(defaultConfig, identifyRepository)));
        registry.register(AllTermsAcceptancesUseCase.class,
                UseCaseProviders.singleton(() -> new AllTermsAcceptancesUseCase(defaultConfig, termsRepository)));
        registry.register(LoginUseCase.class,
                UseCaseProviders.singleton(() -> new LoginUseCase(defaultConfig, identifyRepository)));
        registry.register(LogoutUseCase.class,
                UseCaseProviders.singleton(() -> new LogoutUseCase(defaultConfig, identifyRepository, token, eventBus)));
    }
}
