package com.example.domain.domain.auth.repository;

import com.example.domain.domain.auth.model.LoginAction;
import com.example.domain.domain.auth.model.GuestSignupResult;

public interface LoginRepository {
    GuestSignupResult createAccount(LoginAction provider, String providerUserId);
}
