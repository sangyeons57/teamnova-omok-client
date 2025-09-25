package com.example.application.dto.response;

import com.example.domain.user.entity.User;

import java.util.Objects;

public final class SelfDataResponse {

    public final String userId;
    public final String displayName;
    public final int profileIconCode;
    public final String role;
    public final String status;
    public final int score;

    public SelfDataResponse(User user) {
        Objects.requireNonNull(user, "user");
        this.userId = user.getUserId().getValue();
        this.displayName = user.getDisplayName().getValue();
        this.profileIconCode = user.getProfileIcon().getValue();
        this.role = user.getRole().getValue();
        this.status = user.getStatus().getValue();
        this.score = user.getScore().getValue();
    }
}

