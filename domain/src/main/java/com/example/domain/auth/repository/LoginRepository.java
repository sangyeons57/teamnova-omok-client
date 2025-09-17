package com.example.domain.auth.repository;

import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.model.GuestSignupResult;
import com.example.domain.auth.model.HelloWorldMessage;

public interface LoginRepository {
    GuestSignupResult createAccount(LoginAction provider, String providerUserId);
    HelloWorldMessage getHelloWorldMessage();
}
