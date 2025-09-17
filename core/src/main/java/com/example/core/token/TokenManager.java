package com.example.core.token;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.core.network.http.HttpClientManager;
import com.example.core.network.http.HttpResponse;

import org.json.JSONObject;

import java.io.IOException;

public class TokenManager {

    private static final String PREF_NAME = "com.example.core.token";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private static volatile TokenManager instance;
    private final SharedPreferences sharedPreferences;

    private TokenManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static TokenManager getInstance(Context context) {
        if (instance == null) {
            synchronized (TokenManager.class) {
                if (instance == null) {
                    instance = new TokenManager(context);
                }
            }
        }
        return instance;
    }

    public void saveTokens(String accessToken, String refreshToken) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void clearRefreshToken() {
        sharedPreferences.edit().remove(KEY_REFRESH_TOKEN).apply();
    }

    public void clearAllTokens() {
        sharedPreferences.edit().clear().apply();
    }

    public interface TokenRefreshCallback {
        void onSuccess(String newAccessToken, String newRefreshToken);
        void onFailure(Exception e);
    }

    public void refreshTokens(TokenRefreshCallback callback) {
        new Thread(() -> {
            try {
                String refreshToken = getRefreshToken();
                if (refreshToken == null) {
                    callback.onFailure(new Exception("Refresh token is not available."));
                    return;
                }

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("refresh_token", refreshToken);

                HttpClientManager client = HttpClientManager.getInstance();
                HttpResponse response = client.postJsonToPath("/public/refresh-token.php", jsonBody.toString());

                if (response.isSuccessful()) {
                    JSONObject jsonResponse = new JSONObject(response.getBody());
                    String newAccessToken = jsonResponse.getString("access_token");
                    String newRefreshToken = jsonResponse.getString("refresh_token");

                    saveTokens(newAccessToken, newRefreshToken);
                    callback.onSuccess(newAccessToken, newRefreshToken);
                } else {
                    callback.onFailure(new Exception("Failed to refresh token: " + response.getMessage()));
                }
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }).start();
    }
}
