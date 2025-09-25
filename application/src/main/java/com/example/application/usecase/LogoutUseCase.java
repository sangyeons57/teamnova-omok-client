package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.core.event.AppEventBus;
import com.example.core.event.SessionInvalidatedEvent;
import com.example.core.exception.UseCaseException;
import com.example.core.token.TokenStore;

public class LogoutUseCase extends UseCase<UseCase.None, UseCase.None> {
    private final IdentifyRepository identifyRepository;
    private final TokenStore tokenStore;
    private final AppEventBus eventBus;

    public LogoutUseCase(UseCaseConfig useCaseConfig,
                         IdentifyRepository identifyRepository,
                         TokenStore tokenStore,
                         AppEventBus eventBus) {
        super(useCaseConfig);
        this.identifyRepository = identifyRepository;
        this.tokenStore = tokenStore;
        this.eventBus = eventBus;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        identifyRepository.logout();
        eventBus.post(new SessionInvalidatedEvent(SessionInvalidatedEvent.Reason.LOGOUT));
        tokenStore.clearAllTokens();  // 토큰 먼저 제거시 메시지 전송할떄 인증불가함
        return None.INSTANCE;
    }
}
