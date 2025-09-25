package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.core.event.AppEventBus;
import com.example.core.event.SessionInvalidatedEvent;
import com.example.core.exception.UseCaseException;
import com.example.core.token.TokenStore;

public class DeactivateAccountUseCase extends UseCase<UseCase.None, UseCase.None> {
    private final IdentifyRepository identifyRepository;
    private final TokenStore tokenStore;
    private final AppEventBus eventBus;
    public DeactivateAccountUseCase(UseCaseConfig useCaseConfig, TokenStore tokenStore, IdentifyRepository identifyRepository, AppEventBus eventBus) {
        super(useCaseConfig);
        this.tokenStore = tokenStore;
        this.identifyRepository = identifyRepository;
        this.eventBus = eventBus;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        identifyRepository.deactivateAccount();
        eventBus.post(new SessionInvalidatedEvent(SessionInvalidatedEvent.Reason.DEACTIVATE_ACCOUNT));
        tokenStore.clearAllTokens();
        return None.INSTANCE;
    }
}
