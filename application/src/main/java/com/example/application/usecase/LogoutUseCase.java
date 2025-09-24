package com.example.application.usecase;

import android.util.Log;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.core.exception.UseCaseException;
import com.example.core.token.TokenStore;

public class LogoutUseCase extends UseCase<UseCase.None, UseCase.None> {
    private final IdentifyRepository identifyRepository;
    private final TokenStore tokenStore;
    public LogoutUseCase(UseCaseConfig useCaseConfig, IdentifyRepository identifyRepository, TokenStore tokenStore) {
        super(useCaseConfig);
        this.identifyRepository = identifyRepository;
        this.tokenStore = tokenStore;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        tokenStore.clearAllTokens();
        identifyRepository.logout();
        return null;
    }
}
