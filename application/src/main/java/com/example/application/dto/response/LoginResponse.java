package com.example.application.dto.response;

import com.example.domain.user.entity.Identity;
import com.example.domain.user.entity.User;
import com.example.domain.user.value.AccessToken;
import com.example.domain.user.value.RefreshToken;

public final class LoginResponse {
    public final AccessToken accessToken;
    public final RefreshToken refreshToken;
    public final String userId;
    LoginResponse(String accessToken, String refreshToken, String userId ){
        this.accessToken = AccessToken.of(accessToken);
        this.refreshToken = RefreshToken.of(refreshToken);
        this.userId = userId;
    }

    public LoginResponse(User identity) {
        this.userId = identity.getUserId().getValue();
        this.accessToken = identity.getIdentity().getAccessToken();
        this.refreshToken = identity.getIdentity().getRefreshToken();
    }
}
