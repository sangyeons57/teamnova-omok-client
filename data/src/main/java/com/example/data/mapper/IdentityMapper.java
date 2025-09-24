package com.example.data.mapper;

import android.util.Log;

import com.example.data.model.http.response.Response;
import com.example.domain.user.entity.Identity;
import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;

import java.util.Objects;

/**
 * Maps guest sign-up transport models into domain representations.
 */
public class IdentityMapper {

    public User toIdentity(Response response) {
        String userId = Objects.requireNonNull(response.body().get("user_id")).toString();;
        String accessToken = Objects.requireNonNull(response.body().get("access_token")).toString();
        String refreshToken = Objects.requireNonNull(response.body().get("refresh_token")).toString();

        return UserFactory.createIdentity(userId, accessToken, refreshToken);
    }

    public User onlyUserId(Response response) {
        String userId = Objects.requireNonNull(response.body().get("user_id")).toString();
        return UserFactory.create(userId);
    }
}
