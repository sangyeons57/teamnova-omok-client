package com.example.application.port.out.user;

import com.example.application.port.result.GetOrCreateResult;
import com.example.domain.common.value.SignupAction;
import com.example.domain.user.entity.User;

public interface IdentifyRepository {
    GetOrCreateResult<User> createAccount(SignupAction provider, String googleIdToken);
    User login();
    void logout();
    void deactivateAccount();
}
