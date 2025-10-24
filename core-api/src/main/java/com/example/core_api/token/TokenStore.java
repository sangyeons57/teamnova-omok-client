package com.example.core_api.token;

public interface TokenStore {

    public void saveTokens(String accessToken, String refreshToken);
    public String getAccessToken();
    public String getRefreshToken();
    public void clearRefreshToken();
    public void clearAllTokens();
}
