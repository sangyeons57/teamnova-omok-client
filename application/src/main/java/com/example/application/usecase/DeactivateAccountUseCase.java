package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.core.exception.UseCaseException;

public class DeactivateAccountUseCase extends UseCase<UseCase.None, UseCase.None> {
    public DeactivateAccountUseCase(UseCaseConfig useCaseConfig) {
        super(useCaseConfig);
    }

    @Override
    protected None run(None input) throws UseCaseException {
        return null;
    }
}
