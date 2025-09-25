package com.example.domain.user.factory;

import android.util.Log;

import com.example.domain.user.entity.Identity;
import com.example.domain.user.entity.User;
import com.example.domain.user.value.AccessToken;
import com.example.domain.user.value.RefreshToken;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserId;
import com.example.domain.user.value.UserProfileIcon;
import com.example.domain.user.value.UserRole;
import com.example.domain.user.value.UserScore;
import com.example.domain.user.value.UserStatus;

import java.sql.Ref;
import java.util.Objects;

public class UserFactory {
    private static User defaultCreate (UserId userId, UserDisplayName displayName, UserProfileIcon profileIcon, UserRole role, UserStatus status, UserScore score, Identity identity) {
        return User.of(
                userId == null ? UserId.EMPTY : userId,
                displayName == null ? UserDisplayName.EMPTY : displayName,
                profileIcon == null ? UserProfileIcon.EMPTY : profileIcon,
                role == null ? UserRole.EMPTY : role,
                status == null ? UserStatus.EMPTY : status,
                score == null ? UserScore.EMPTY : score,
                identity == null ? Identity.EMPTY : identity
        );

    }
    public static User createUser(String userId, String displayName, int profileIcon, String role, String status, int score, String accessToken, String refreshToken) {
        return User.of(
                UserId.of(userId),
                UserDisplayName.of(displayName),
                UserProfileIcon.of(profileIcon),
                UserRole.of(role),
                UserStatus.of(status),
                UserScore.of(score),
                Identity.of(accessToken, refreshToken));
    }

    public static User createProfile(String userId,
                                     String displayName,
                                     Integer profileIconCode,
                                     String role,
                                     String status,
                                     Integer score) {
        return defaultCreate(
                userId != null ? UserId.of(userId) : null,
                displayName != null ? UserDisplayName.of(displayName) : null,
                profileIconCode != null ? UserProfileIcon.of(profileIconCode) : null,
                role != null ? UserRole.of(role) : null,
                status != null ? UserStatus.of(status) : null,
                score != null ? UserScore.of(score) : null,
                Identity.EMPTY);
    }

    public static User createIdentity(String userId, String accessToken, String refreshToken) {
        Log.d("test", "identity:" );
        Identity identity = Identity.of(accessToken, refreshToken);
        User user = defaultCreate(
                UserId.of(userId),
                UserDisplayName.EMPTY,
                UserProfileIcon.EMPTY,
                UserRole.EMPTY,
                UserStatus.EMPTY,
                UserScore.EMPTY,
                identity
        );
        return  user;
    }

    public static User create(String userId) {
        return defaultCreate(
                UserId.of(userId),
                UserDisplayName.EMPTY,
                UserProfileIcon.EMPTY,
                UserRole.EMPTY,
                UserStatus.EMPTY,
                UserScore.EMPTY,
                Identity.EMPTY);
    }
}
