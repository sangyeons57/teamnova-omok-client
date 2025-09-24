package com.example.core.token;

import android.content.Context;

public interface TokenStore {

    public void saveTokens(String accessToken, String refreshToken);
    public String getAccessToken();
    public String getRefreshToken();
    public void clearRefreshToken();
    public void clearAllTokens();
}
