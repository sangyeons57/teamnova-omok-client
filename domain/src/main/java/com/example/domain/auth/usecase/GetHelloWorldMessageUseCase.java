package com.example.domain.auth.usecase;

import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.repository.LoginRepository;
import com.example.domain.usecase.SyncUseCase;
import com.example.domain.usecase.UseCase;
import com.example.domain.usecase.UseCaseException;

import java.util.Objects;

public class GetHelloWorldMessageUseCase extends SyncUseCase<UseCase.None, HelloWorldMessage> {

    private final LoginRepository repository;

    public GetHelloWorldMessageUseCase(LoginRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    protected HelloWorldMessage run(UseCase.None none) throws UseCaseException {
        return repository.getHelloWorldMessage();
    }
}
