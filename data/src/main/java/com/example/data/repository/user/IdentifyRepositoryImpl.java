package com.example.data.repository.user;

import android.util.Log;

import com.example.application.port.out.user.IdentifyRepository;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.exception.GuestSignupRemoteException;
import com.example.data.exception.LoginRemoteException;
import com.example.data.exception.LogoutRemoteException;
import com.example.data.mapper.IdentityMapper;
import com.example.data.mapper.UserResponseMapper;
import com.example.data.model.http.request.Path;
import com.example.data.model.http.request.Request;
import com.example.data.model.http.response.Response;
import com.example.domain.common.value.LoginAction;
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
    public User createAccount(LoginAction provider, String providerUserId) {
        try {
            Request request = Request.defaultRequest(Path.CREATE_ACCOUNT);
            Map<String, Object> body = new HashMap<>();
            body.put("provider", resolveProvider(provider));
            if (providerUserId != null && !providerUserId.trim().isEmpty()) {
                body.put("provider_user_id", providerUserId);
            }
            request.setBody(body);

            Response response = phpServerDataSource.post(request);
            if (!response.isSuccess()) {
                throw new GuestSignupRemoteException(
                        extractErrorMessage(null, "Failed to create account Status: " + response.statusCode() + " | " + response.statusMessage()));
            }
            return identityMapper.toIdentity(response);
        } catch (IOException exception) {
            Log.e("IdentifyRepositoryImpl", "error:" + Arrays.toString(exception.getStackTrace()));
            throw new GuestSignupRemoteException("Failed to create account", exception);
        }
    }

    @Override
    public User login() {
        try {
            Response response = phpServerDataSource.post( Request.defaultRequest(Path.LOGIN) );

            if (!response.isSuccess()) {
                throw new LoginRemoteException("Failed to login Status: " + response.statusCode() + " | " + response.statusMessage() + " |" + response.body() );
            }
            return mapLoginResponse(response);
        } catch (IOException exception) {
            Log.e("IdentifyRepositoryImpl", "error:" + Arrays.toString(exception.getStackTrace()));
            throw new LoginRemoteException("Failed to login", exception);
        }
    }

    @Override
    public void logout() {
        try {
            Response response = phpServerDataSource.post( Request.defaultRequest(Path.LOGOUT) );

            if(!response.isSuccess()) {
                throw new LogoutRemoteException("Failed to logout Status: " + response.statusCode() + " | " + response.statusMessage());
            }
        } catch (IOException exception) {
            throw new LogoutRemoteException("Failed to logout", exception);
        }
    }

    @Override
    public void deactivateAccount() {
        try {
            Response response = phpServerDataSource.post( Request.defaultRequest(Path.DEACTIVATE_ACCOUNT) );

            if(!response.isSuccess()) {
                throw new LogoutRemoteException("Failed to logout Status: " + response.statusCode() + " | " + response.statusMessage());
            }
        } catch (IOException exception) {
            throw new LogoutRemoteException("Failed to logout", exception);
        }
    }

    private String resolveProvider(LoginAction provider) {
        return provider != null ? provider.name() : DEFAULT_PROVIDER;
    }

    private User mapLoginResponse(Response response) {
        Map<String, Object> body = response.body();
        return userResponseMapper.mapUserProfile(body);
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
