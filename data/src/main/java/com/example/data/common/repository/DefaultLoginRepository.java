package com.example.data.common.repository;

import com.example.core.network.http.HttpClientManager;
import com.example.core.network.http.HttpResponse;
import com.example.data.common.exception.GuestSignupRemoteException;
import com.example.data.common.exception.HelloWorldRemoteException;
import com.example.data.common.mapper.GuestSignupMapper;
import com.example.data.common.mapper.HelloWorldMapper;
import com.example.data.common.model.GuestSignupResponse;
import com.example.data.common.model.HelloWorldResponse;
import com.example.data.common.datasource.DefaultPhpServerDataSource;
import com.example.domain.auth.model.GuestSignupResult;
import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.repository.LoginRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

/**
 * Default implementation of the login repository which orchestrates
 * login related actions and fetches data from the shared PHP server datasource.
 */
public class DefaultLoginRepository implements LoginRepository {

    private static final String HELLO_WORLD_PATH = "hello-world.php";
    private static final String CREATE_ACCOUNT_PATH = "create-account.php";
    private static final String DEFAULT_PROVIDER = "GUEST";

    private final DefaultPhpServerDataSource phpServerDataSource;
    private final HelloWorldMapper helloWorldMapper;
    private final GuestSignupMapper guestSignupMapper;

    public DefaultLoginRepository() {
        this(
                new DefaultPhpServerDataSource(HttpClientManager.getInstance()),
                new HelloWorldMapper(),
                new GuestSignupMapper()
        );
    }

    public DefaultLoginRepository(DefaultPhpServerDataSource phpServerDataSource,
                                  HelloWorldMapper helloWorldMapper,
                                  GuestSignupMapper guestSignupMapper) {
        this.phpServerDataSource = Objects.requireNonNull(phpServerDataSource, "phpServerDataSource");
        this.helloWorldMapper = Objects.requireNonNull(helloWorldMapper, "helloWorldMapper");
        this.guestSignupMapper = Objects.requireNonNull(guestSignupMapper, "guestSignupMapper");
    }

    @Override
    public GuestSignupResult createAccount(LoginAction provider, String providerUserId) {
        try {
            String payload = buildCreateAccountPayload(resolveProvider(provider), providerUserId);
            HttpResponse response = phpServerDataSource.postJson(CREATE_ACCOUNT_PATH, payload);
            if (!response.isSuccessful()) {
                String message = "Unexpected HTTP " + response.getCode() + ' ' + response.getMessage();
                throw new GuestSignupRemoteException(message);
            }
            GuestSignupResponse dto = new GuestSignupResponse(response.getBody());
            return guestSignupMapper.toDomain(dto);
        } catch (IOException exception) {
            throw new GuestSignupRemoteException("Failed to create account", exception);
        }
    }

    @Override
    public HelloWorldMessage getHelloWorldMessage() {
        try {
            HttpResponse response = phpServerDataSource.get(HELLO_WORLD_PATH);
            if (!response.isSuccessful()) {
                String message = "Unexpected HTTP " + response.getCode() + ' ' + response.getMessage();
                throw new HelloWorldRemoteException(message);
            }
            HelloWorldResponse dto = new HelloWorldResponse(response.getBody());
            return helloWorldMapper.toDomain(dto);
        } catch (IOException exception) {
            throw new HelloWorldRemoteException("Failed to fetch hello world message", exception);
        }
    }

    private String resolveProvider(LoginAction provider) {
        return provider != null ? provider.name() : DEFAULT_PROVIDER;
    }

    private String buildCreateAccountPayload(String provider, String providerUserId) {
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

