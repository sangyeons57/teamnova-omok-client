package com.example.application.port.out.user;

import com.example.domain.common.value.LoginAction;
import com.example.domain.user.entity.User;

public interface IdentifyRepository {
    User createAccount(LoginAction provider, String googleIdToken);
    User login();
    void logout();
    void deactivateAccount();
}
