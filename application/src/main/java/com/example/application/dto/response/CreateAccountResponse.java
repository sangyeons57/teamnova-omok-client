package com.example.application.dto.response;

import com.example.domain.user.entity.User;

public class CreateAccountResponse {
    public final User user;
    public final boolean isNew;

    public CreateAccountResponse(User user, boolean isNew) {
        this.user = user;
        this.isNew = isNew;
    }
}
