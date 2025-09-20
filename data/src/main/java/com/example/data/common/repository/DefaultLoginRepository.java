package com.example.data.common.repository;

import android.util.Log;

import com.example.core.network.http.HttpClientManager;
import com.example.data.common.datasource.DefaultPhpServerDataSource;
import com.example.data.common.exception.GuestSignupRemoteException;
import com.example.data.common.exception.HelloWorldRemoteException;
import com.example.data.common.mapper.GuestSignupMapper;
import com.example.data.common.mapper.HelloWorldMapper;
import com.example.data.common.model.GuestSignupResponse;
import com.example.data.common.model.HelloWorldResponse;
import com.example.data.common.model.request.Path;
import com.example.data.common.model.request.Request;
import com.example.data.common.model.response.Error;
import com.example.data.common.model.response.ResponseSingle;
import com.example.domain.auth.model.GuestSignupResult;
import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.repository.LoginRepository;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of the login repository which orchestrates
 * login related actions and fetches data from the shared PHP server datasource.
 */
public class DefaultLoginRepository implements LoginRepository {

    private static final String DEFAULT_PROVIDER = "GUEST";

    private final DefaultPhpServerDataSource phpServerDataSource;
    private final HelloWorldMapper helloWorldMapper;
    private final GuestSignupMapper guestSignupMapper;
    private final Gson gson = new Gson();

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
            Request request = buildRequest(Path.CREATE_ACCOUNT);
            Map<String, String> body = new HashMap<>();
            body.put("provider", resolveProvider(provider));
            if (providerUserId != null && !providerUserId.trim().isEmpty()) {
                body.put("provider_user_id", providerUserId);
            }
            request.setBody(body);

            Log.d("createAccount", "request: " + request.toJson(new Gson()));
            ResponseSingle response = phpServerDataSource.postSingle(request);
            Log.d("createAccount", "response: " + response.getMeta().toString());
            if (response.isError()) {
                Log.d("createAccount", "response: " + response.getError().toString());
                throw new GuestSignupRemoteException(
                        extractErrorMessage(response.getError(), "Failed to create account"));
            }
            Log.d("createAccount", "response: " + response.getData().toString());

            GuestSignupResponse dto = new GuestSignupResponse(serializeResponseData(response));
            return guestSignupMapper.toDomain(dto);
        } catch (IOException exception) {
            throw new GuestSignupRemoteException("Failed to create account", exception);
        }
    }

    @Override
    public HelloWorldMessage getHelloWorldMessage() {
        try {
            Request request = buildRequest(Path.HELLO_WORLD);
            ResponseSingle response = phpServerDataSource.postSingle(request);
            if (!response.isSuccess()) {
                throw new HelloWorldRemoteException(
                        extractErrorMessage(response.getError(), "Failed to fetch hello world message"));
            }
            HelloWorldResponse dto = new HelloWorldResponse(extractHelloWorldMessage(response));
            return helloWorldMapper.toDomain(dto);
        } catch (IOException exception) {
            throw new HelloWorldRemoteException("Failed to fetch hello world message", exception);
        }
    }

    private String resolveProvider(LoginAction provider) {
        return provider != null ? provider.name() : DEFAULT_PROVIDER;
    }

    private Request buildRequest(Path path) {
        Request request = new Request();
        request.setPath(path);
        request.setRequestId(UUID.randomUUID());
        request.setTimestamp(Instant.now());
        return request;
    }

    private String serializeResponseData(ResponseSingle response) {
        if (response != null && response.getData() != null && response.getData().getPayload() != null) {
            return gson.toJson(response.getData().getPayload());
        }
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("success", false);
        if (response != null && response.getError() != null) {
            fallback.put("message", response.getError().getMessage());
            fallback.put("code", response.getError().getCode());
        }
        return gson.toJson(fallback);
    }

    private String extractHelloWorldMessage(ResponseSingle response) {
        if (response != null && response.getData() != null && response.getData().getPayload() != null) {
            Object value = response.getData().getPayload().get("message");
            if (value != null) {
                return String.valueOf(value);
            }
        }
        if (response != null && response.getError() != null) {
            return response.getError().getMessage();
        }
        return "";
    }

    private String extractErrorMessage(Error error, String defaultMessage) {
        if (error == null) {
            return defaultMessage;
        }
        String message = error.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            return message;
        }
        return defaultMessage;
    }
}
