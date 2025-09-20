package com.example.application.port.out;

import com.example.domain.common.value.LoginAction;
import com.example.domain.identity.entity.Identity;

public interface IdentifyRepository {
    Identity createAccount(LoginAction provider, String providerUserId);
}
