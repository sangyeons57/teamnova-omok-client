package com.example.domain.identity.entity;

import com.example.domain.user.value.AccessToken;
import com.example.domain.user.value.RefreshToken;

import java.util.Objects;

/**
 * Aggregate representing the authenticated session produced after creating an account.
 */
public final class Identity {

    private final String  userId;
    private final AccessToken accessToken;
    private final RefreshToken refreshToken;

    public Identity(String userId, AccessToken accessToken, RefreshToken refreshToken) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.accessToken = Objects.requireNonNull(accessToken, "accessToken");
        this.refreshToken = Objects.requireNonNull(refreshToken, "refreshToken");
    }

    public String getUserId() {
        return userId;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }
}
