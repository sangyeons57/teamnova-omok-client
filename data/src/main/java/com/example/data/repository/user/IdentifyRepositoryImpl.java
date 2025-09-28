package com.example.data.repository.user;

import android.util.Log;

import com.example.application.port.out.user.IdentifyRepository;
import com.example.application.wrapper.GetOrCreateResult;
import com.example.application.wrapper.UserSession;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.GuestSignupRemoteException;
import com.example.data.exception.LinkGoogleRemoteException;
import com.example.data.exception.LoginRemoteException;
import com.example.data.exception.LogoutRemoteException;
import com.example.data.mapper.IdentityMapper;
import com.example.data.mapper.UserResponseMapper;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Response;
import com.example.domain.common.value.AuthProvider;
import com.example.domain.user.entity.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of the login repository which orchestrates
 * login related actions and fetches data from the shared PHP server datasource.
 */
public class IdentifyRepositoryImpl implements IdentifyRepository {

    private static final String DEFAULT_PROVIDER = "GUEST";

    private final DefaultPhpServerDataSource phpServerDataSource;
    private final IdentityMapper identityMapper;
    private final UserResponseMapper userResponseMapper;

    public IdentifyRepositoryImpl(DefaultPhpServerDataSource phpServerDataSource,
                                  IdentityMapper identityMapper,
                                  UserResponseMapper userResponseMapper) {
        this.phpServerDataSource = Objects.requireNonNull(phpServerDataSource, "phpServerDataSource");
        this.identityMapper = Objects.requireNonNull(identityMapper, "guestSignupMapper");
        this.userResponseMapper = Objects.requireNonNull(userResponseMapper, "userResponseMapper");
    }

    @Override
    public GetOrCreateResult<User> createAccount(AuthProvider provider, String providerIdToken) {
        try {
            Request request = Request.defaultRequest(Path.CREATE_ACCOUNT);
            Map<String, Object> body = new HashMap<>();
            body.put("provider", resolveProvider(provider));
            if (providerIdToken != null && !providerIdToken.trim().isEmpty()) {
                body.put("provider_id_token", providerIdToken);
            }
            request.setBody(body);

            Response response = phpServerDataSource.post(request);
            if (!response.isSuccess()) {
                throw new GuestSignupRemoteException(
                        extractErrorMessage(null, "Failed to create account Status: " + response.statusCode() + " | " + response.statusMessage()));
            }
            return identityMapper.toGetOrCreateIdentity(response);
        } catch (IOException exception) {
            Log.e("IdentifyRepositoryImpl", "error:" + Arrays.toString(exception.getStackTrace()));
            throw new GuestSignupRemoteException("Failed to create account", exception);
        }
    }

    @Override
    public UserSession login() {
        try {
            Response response = phpServerDataSource.post(Request.defaultRequest(Path.LOGIN));

            if (!response.isSuccess()) {
                throw new LoginRemoteException("Failed to login Status: " + response.statusCode() + " | " + response.statusMessage() + " |" + response.body());
            }
            return mapSessionResponse(response);
        } catch (IOException exception) {
            Log.e("IdentifyRepositoryImpl", "error:" + Arrays.toString(exception.getStackTrace()));
            throw new LoginRemoteException("Failed to login", exception);
        }
    }

    @Override
    public UserSession linkGoogleAccount(String providerIdToken) {
        if (providerIdToken == null || providerIdToken.trim().isEmpty()) {
            throw new IllegalArgumentException("providerIdToken is empty");
        }

        try {
            Request request = Request.defaultRequest(Path.LINK_GOOGLE);
            Map<String, Object> body = new HashMap<>();
            body.put("provider_id_token", providerIdToken);
            request.setBody(body);

            Response response = phpServerDataSource.post(request);
            if (!response.isSuccess()) {
                throw new LinkGoogleRemoteException(
                        extractErrorMessage(null, "Failed to link Google account Status: " + response.statusCode() + " | " + response.statusMessage()));
            }
            return mapSessionResponse(response);
        } catch (IOException exception) {
            Log.e("IdentifyRepositoryImpl", "error:" + Arrays.toString(exception.getStackTrace()));
            throw new LinkGoogleRemoteException("Failed to link Google account", exception);
        }
    }

    @Override
    public void logout() {
        try {
            Response response = phpServerDataSource.post(Request.defaultRequest(Path.LOGOUT));

            if (!response.isSuccess()) {
                throw new LogoutRemoteException("Failed to logout Status: " + response.statusCode() + " | " + response.statusMessage());
            }
        } catch (IOException exception) {
            throw new LogoutRemoteException("Failed to logout", exception);
        }
    }

    @Override
    public void deactivateAccount() {
        try {
            Response response = phpServerDataSource.post(Request.defaultRequest(Path.DEACTIVATE_ACCOUNT));

            if (!response.isSuccess()) {
                throw new LogoutRemoteException("Failed to logout Status: " + response.statusCode() + " | " + response.statusMessage());
            }
        } catch (IOException exception) {
            throw new LogoutRemoteException("Failed to logout", exception);
        }
    }

    private String resolveProvider(AuthProvider provider) {
        return provider != null ? provider.name() : DEFAULT_PROVIDER;
    }

    private UserSession mapSessionResponse(Response response) {
        Map<String, Object> body = response.body();
        User user = userResponseMapper.mapUserProfile(body);
        AuthProvider provider = resolveProviderValue(body);
        return UserSession.of(user, provider);
    }

    private AuthProvider resolveProviderValue(Map<String, Object> body) {
        Object value = body != null ? body.get("provider") : null;
        if (value == null) {
            return AuthProvider.GUEST;
        }

        if (value instanceof Map<?, ?> providerPayload) {
            Object providerValue = providerPayload.get("provider");
            if (providerValue != null) {
                return resolveProviderToken(providerValue.toString());
            }
            Log.w("IdentifyRepositoryImpl", "Provider payload missing provider key: " + providerPayload);
            return AuthProvider.GUEST;
        }

        return resolveProviderToken(value.toString());
    }

    private AuthProvider resolveProviderToken(String token) {
        if (token == null) {
            return AuthProvider.GUEST;
        }
        try {
            return AuthProvider.valueOf(token.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            Log.w("IdentifyRepositoryImpl", "Unknown provider token: " + token);
            return AuthProvider.GUEST;
        }
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
