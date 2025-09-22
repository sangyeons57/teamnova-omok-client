package com.example.data.repository;

import android.util.Log;

import com.example.application.port.out.IdentifyRepository;
import com.example.core.network.http.HttpClientManager;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.GuestSignupRemoteException;
import com.example.data.mapper.IdentityMapper;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Error;
import com.example.data.model.http.response.ResponseSingle;
import com.example.domain.identity.entity.Identity;
import com.example.domain.common.value.LoginAction;
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
public class IdentifyRepositoryImpl implements IdentifyRepository {

    private static final String DEFAULT_PROVIDER = "GUEST";

    private final DefaultPhpServerDataSource phpServerDataSource;
    private final IdentityMapper identityMapper;
    private final Gson gson = new Gson();

    public IdentifyRepositoryImpl() {
        this(
                new DefaultPhpServerDataSource(HttpClientManager.getInstance()),
                new IdentityMapper()
        );
    }

    public IdentifyRepositoryImpl(DefaultPhpServerDataSource phpServerDataSource,
                                  IdentityMapper identityMapper) {
        this.phpServerDataSource = Objects.requireNonNull(phpServerDataSource, "phpServerDataSource");
        this.identityMapper = Objects.requireNonNull(identityMapper, "guestSignupMapper");
    }

    @Override
    public Identity createAccount(LoginAction provider, String providerUserId) {
        try {
            Request request = buildRequest(Path.CREATE_ACCOUNT);
            Map<String, Object> body = new HashMap<>();
            body.put("provider", resolveProvider(provider));
            if (providerUserId != null && !providerUserId.trim().isEmpty()) {
                body.put("provider_user_id", providerUserId);
            }
            request.setBody(body);

            ResponseSingle response = phpServerDataSource.postSingle(request);
            if (response.isError()) {
                throw new GuestSignupRemoteException(
                        extractErrorMessage(response.getError(), "Failed to create account"));
            }
            return identityMapper.toIdentity(response);
        } catch (IOException exception) {
            throw new GuestSignupRemoteException("Failed to create account", exception);
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
