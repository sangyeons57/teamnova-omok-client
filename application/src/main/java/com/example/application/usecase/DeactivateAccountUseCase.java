package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.application.session.UserSessionStore;
import com.example.core_api.event.AppEventBus;
import com.example.core_api.event.SessionInvalidatedEvent;
import com.example.core_api.exception.UseCaseException;
import com.example.core_api.token.TokenStore;

public class DeactivateAccountUseCase extends UseCase<UseCase.None, UseCase.None> {
    private final IdentifyRepository identifyRepository;
    private final TokenStore tokenStore;
    private final AppEventBus eventBus;
    private final UserSessionStore userSessionStore;

    public DeactivateAccountUseCase(UseCaseConfig useCaseConfig,
                                    TokenStore tokenStore,
                                    IdentifyRepository identifyRepository,
                                    AppEventBus eventBus,
                                    UserSessionStore userSessionStore) {
        super(useCaseConfig);
        this.tokenStore = tokenStore;
        this.identifyRepository = identifyRepository;
        this.eventBus = eventBus;
        this.userSessionStore = userSessionStore;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        identifyRepository.deactivateAccount();
        eventBus.post(new SessionInvalidatedEvent(SessionInvalidatedEvent.Reason.DEACTIVATE_ACCOUNT));
        tokenStore.clearAllTokens();
        userSessionStore.clear();
        return None.INSTANCE;
    }
}
