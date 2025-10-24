package com.example.infra.http.provider;

import com.example.core_api.event.AppEventBus;
import com.example.core_api.token.TokenStore;
import com.example.infra.http.php.AuthInterceptor;
import com.example.infra.http.php.LoggingInterceptor;
import com.example.infra.http.php.TokenAuthenticator;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientProvider {
    private static OkHttpClientProvider instance;

    public static OkHttpClientProvider getInstance() {
        if (instance == null) {
            throw new IllegalStateException("OkHttpClientProvider is not initialized");
        }
        return instance;
    }
    public static OkHttpClientProvider init(TokenStore tokenStore, AppEventBus eventBus) {
        if(instance != null) return instance;
        synchronized (OkHttpClientProvider.class) {
            if(instance == null) {
                instance = new OkHttpClientProvider(tokenStore, eventBus);
            }
            return instance;
        }
    }

    private final OkHttpClient defaultValue;
    private OkHttpClientProvider(TokenStore tokenStore, AppEventBus eventBus) {
        OkHttpClient refreshClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor( new LoggingInterceptor() )
                .build();
        defaultValue = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(tokenStore))
                .authenticator(new TokenAuthenticator(refreshClient, tokenStore, eventBus))
                .addInterceptor( new LoggingInterceptor() )
                .build();

    }

    public OkHttpClient get() { return defaultValue; }
}
