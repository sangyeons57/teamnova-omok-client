package com.example.application.port.out.user;

import com.example.application.wrapper.GetOrCreateResult;
import com.example.application.wrapper.UserSession;
import com.example.domain.common.value.AuthProvider;
import com.example.domain.user.entity.User;

public interface IdentifyRepository {
    GetOrCreateResult<User> createAccount(AuthProvider provider, String googleIdToken);
    UserSession login();
    UserSession linkGoogleAccount(String providerIdToken);
    void logout();
    void deactivateAccount();
}
