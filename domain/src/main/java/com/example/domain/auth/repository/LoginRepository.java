package com.example.domain.auth.repository;

import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.model.HelloWorldMessage;

public interface LoginRepository {
    LoginAction loginAsGuest();
    LoginAction loginWithGoogle();
    HelloWorldMessage getHelloWorldMessage();
}
