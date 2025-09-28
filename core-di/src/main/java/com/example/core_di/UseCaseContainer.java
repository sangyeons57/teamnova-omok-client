package com.example.core_di;

import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.in.UseCaseProviders;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.application.port.out.user.TermsRepository;
import com.example.application.port.out.user.UserRepository;
import com.example.application.usecase.AllTermsAcceptancesUseCase;
import com.example.application.usecase.ChangeNameUseCase;
import com.example.application.usecase.ChangeProfileIconUseCase;
import com.example.application.usecase.CreateAccountUseCase;
import com.example.application.usecase.DeactivateAccountUseCase;
import com.example.application.usecase.LinkGoogleAccountUseCase;
import com.example.application.usecase.LoginUseCase;
import com.example.application.usecase.LogoutUseCase;
import com.example.application.usecase.RankingDataUseCase;
import com.example.application.usecase.SelfDataUseCase;
import com.example.application.usecase.UserDataUseCase;
import com.example.core.event.AppEventBus;
import com.example.application.session.GameInfoStore;
import com.example.application.session.InMemoryGameInfoStore;
import com.example.application.session.InMemoryUserSessionStore;
import com.example.application.session.UserSessionStore;
import com.example.core.token.TokenStore;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.mapper.IdentityMapper;
import com.example.data.mapper.UserResponseMapper;
import com.example.data.repository.user.IdentifyRepositoryImpl;
import com.example.data.repository.user.TermsRepositoryImpl;
import com.example.data.repository.user.UserRepositoryImpl;

public final class UseCaseContainer {

    private static UseCaseContainer instance;
    public static UseCaseContainer getInstance() {
        if (instance == null) {
            instance = new UseCaseContainer();
        }
        return instance;
    }

    public final DefaultPhpServerDataSource phpServerDataSource = new DefaultPhpServerDataSource(HttpClientContainer.getInstance().get());
    public final UserResponseMapper userResponseMapper = new UserResponseMapper();
    public final UseCaseConfig defaultConfig = UseCaseConfig.defaultConfig();
    public final UseCaseRegistry registry = new UseCaseRegistry();
    public final IdentifyRepository identifyRepository = new IdentifyRepositoryImpl(phpServerDataSource, new IdentityMapper(), userResponseMapper);
    public final UserRepository userRepository = new UserRepositoryImpl(phpServerDataSource, userResponseMapper);
    public final TermsRepository termsRepository = new TermsRepositoryImpl(phpServerDataSource);
    public final TokenStore token = TokenContainer.getInstance();
    public final UserSessionStore userSessionStore = new InMemoryUserSessionStore();
    public final GameInfoStore gameInfoStore = new InMemoryGameInfoStore();
    public final AppEventBus eventBus = EventBusContainer.getInstance();

    public UseCaseContainer() {
        registry.register(CreateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new CreateAccountUseCase(defaultConfig, identifyRepository)));
        registry.register(AllTermsAcceptancesUseCase.class,
                UseCaseProviders.singleton(() -> new AllTermsAcceptancesUseCase(defaultConfig, termsRepository)));
        registry.register(LoginUseCase.class,
                UseCaseProviders.singleton(() -> new LoginUseCase(defaultConfig, identifyRepository, userSessionStore)));
        registry.register(LinkGoogleAccountUseCase.class,
                UseCaseProviders.singleton(() -> new LinkGoogleAccountUseCase(defaultConfig, identifyRepository, userSessionStore)));
        registry.register(LogoutUseCase.class,
                UseCaseProviders.singleton(() -> new LogoutUseCase(defaultConfig, identifyRepository, token, eventBus, userSessionStore)));
        registry.register(DeactivateAccountUseCase.class,
                UseCaseProviders.singleton(() -> new DeactivateAccountUseCase(defaultConfig, token, identifyRepository, eventBus, userSessionStore)));

        registry.register(ChangeNameUseCase.class,
                UseCaseProviders.singleton(() -> new ChangeNameUseCase(defaultConfig, userRepository, userSessionStore)));
        registry.register(ChangeProfileIconUseCase.class,
                UseCaseProviders.singleton(() -> new ChangeProfileIconUseCase(defaultConfig, userRepository, userSessionStore)));
        registry.register(RankingDataUseCase.class,
                UseCaseProviders.singleton(() -> new RankingDataUseCase(defaultConfig, userRepository)));
        registry.register(SelfDataUseCase.class,
                UseCaseProviders.singleton(() -> new SelfDataUseCase(defaultConfig, userRepository, userSessionStore)));
        registry.register(UserDataUseCase.class,
                UseCaseProviders.singleton(() -> new UserDataUseCase(defaultConfig, userRepository)));
    }
}
