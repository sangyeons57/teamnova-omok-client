package com.example.data.auth.datasource.remote;

import com.example.core.navigation.NavigationHelper;
import com.example.core.network.http.HttpClientManager;
import com.example.core.network.http.HttpResponse;
import com.example.data.auth.exception.GuestSignupRemoteException;
import com.example.data.auth.model.GuestSignupResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class AuthRemoteDataSource {
    private static final String DEFAULT_PROVIDER = "GUEST";
    private static final String CREATE_ACCOUNT_PATH = "create-account.php";

    private final HttpClientManager httpClientManager;

    public AuthRemoteDataSource() {
        this(HttpClientManager.getInstance());
    }

    public AuthRemoteDataSource(HttpClientManager httpClientManager) {
        this.httpClientManager = Objects.requireNonNull(httpClientManager, "httpClientManager");
    }

    public GuestSignupResponse createAccount(String provider, String providerUserId) {
        try {
            String payload = buildPayload(resolveProvider(provider), providerUserId);
            String url = NavigationHelper.resolvePublicPath(CREATE_ACCOUNT_PATH);
            HttpResponse response = httpClientManager.postJson(url, payload);
            if (!response.isSuccessful()) {
                String message = "Unexpected HTTP " + response.getCode() + ' ' + response.getMessage();
                throw new GuestSignupRemoteException(message);
            }
            return new GuestSignupResponse(response.getBody());
        } catch (IOException exception) {
            throw new GuestSignupRemoteException("Failed to create account", exception);
        }
    }

    private String resolveProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return DEFAULT_PROVIDER;
        }
        return provider;
    }

    private String buildPayload(String provider, String providerUserId) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("provider", provider);
            if (providerUserId != null && !providerUserId.trim().isEmpty()) {
                payload.put("provider_user_id", providerUserId);
            }
            return payload.toString();
        } catch (JSONException exception) {
            throw new GuestSignupRemoteException("Failed to build account payload", exception);
        }
    }
}
