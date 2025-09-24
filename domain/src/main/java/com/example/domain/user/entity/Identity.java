package com.example.domain.user.entity;

import com.example.domain.user.value.AccessToken;
import com.example.domain.user.value.RefreshToken;
import com.example.domain.user.value.UserDisplayName;

import java.sql.Ref;
import java.util.Objects;

/**
 * Aggregate representing the authenticated session produced after creating an account.
 */
public final class Identity {
    public static final Identity EMPTY = new Identity(AccessToken.EMPTY, RefreshToken.EMPTY);

    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private Identity(AccessToken accessToken, RefreshToken refreshToken) {
        this.accessToken = Objects.requireNonNull(accessToken, "accessToken");
        this.refreshToken = Objects.requireNonNull(refreshToken, "refreshToken");

    }

    public static Identity of(String accessToken, String refreshToken) {
        return new Identity(AccessToken.of(accessToken), RefreshToken.of(refreshToken));
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
    public RefreshToken getRefreshToken() {
        return refreshToken;
    }
}
