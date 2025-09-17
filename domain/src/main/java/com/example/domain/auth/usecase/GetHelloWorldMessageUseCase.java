package com.example.domain.auth.usecase;

import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.repository.LoginRepository;
import com.example.domain.usecase.UseCase;

import java.util.Objects;

public class GetHelloWorldMessageUseCase implements UseCase<UseCase.None, HelloWorldMessage> {

    private final LoginRepository repository;

    public GetHelloWorldMessageUseCase(LoginRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public HelloWorldMessage execute(UseCase.None none) {
        return repository.getHelloWorldMessage();
    }
}
