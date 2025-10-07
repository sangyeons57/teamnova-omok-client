package com.example.feature_game.game.presentation.util;

import androidx.annotation.DrawableRes;

import com.example.feature_game.R;

/**
 * Maps profile icon codes provided by the backend to drawable resources.
 */
public final class ProfileIconResolver {

    private ProfileIconResolver() {
        // No instances.
    }

    @DrawableRes
    public static int resolve(int profileIconCode) {
        switch (profileIconCode) {
            case 1:
                return R.drawable.ic_profile_avatar_primary;
            case 2:
                return R.drawable.ic_profile_avatar_secondary;
            case 3:
                return R.drawable.ic_profile_avatar_tertiary;
            case 4:
                return R.drawable.ic_profile_avatar_quaternary;
            default:
                return R.drawable.ic_person_outline;
        }
    }
}

