package com.example.infra.http.prefs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.core_api.token.TokenStore;

public class PrefsTokenStore implements TokenStore {

    private static final String PREF_NAME = "com.example.core.token";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private final SharedPreferences sharedPreferences;

    private volatile String atCache;
    private volatile String rtCache;


    public PrefsTokenStore(Application application) {
        this.sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.atCache = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        this.rtCache = sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    @Override
    public synchronized void saveTokens(String accessToken, String refreshToken) {
        atCache = accessToken; rtCache = refreshToken;
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    @Override
    public synchronized String getAccessToken() {
        return atCache;
    }

    @Override
    public synchronized String getRefreshToken() {
        return rtCache;
    }

    @Override
    public synchronized void clearRefreshToken() {
        rtCache = null;
        sharedPreferences.edit().remove(KEY_REFRESH_TOKEN).apply();
    }

    @Override
    public synchronized void clearAllTokens() {
        atCache = null; rtCache = null;
        sharedPreferences.edit().remove(KEY_REFRESH_TOKEN).remove(KEY_ACCESS_TOKEN).apply();
    }

}
