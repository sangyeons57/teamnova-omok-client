package com.example.domain.auth.usecase;

import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.repository.LoginRepository;

public class LoginActionUseCase {

    private final LoginRepository repository;

    public LoginActionUseCase(LoginRepository repository) {
        this.repository = repository;
    }

    public LoginAction loginAsGuest() {
        return repository.loginAsGuest();
    }

    public LoginAction loginWithGoogle() {
        return repository.loginWithGoogle();
    }

    public HelloWorldMessage getHelloWorld() {
        return repository.getHelloWorldMessage();
    }
}
