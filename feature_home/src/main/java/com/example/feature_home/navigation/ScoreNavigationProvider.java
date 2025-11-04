package com.example.feature_home.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.core_api.navigation.AppNavigationKey;
import com.example.core_api.navigation.FragmentNavigationProvider;
import com.example.feature_home.presentation.ui.ScoreFragment;

import java.util.function.Supplier;

public final class ScoreNavigationProvider implements FragmentNavigationProvider<AppNavigationKey> {

    @NonNull
    @Override
    public Class<AppNavigationKey> getNavigationKeyType() {
        return AppNavigationKey.class;
    }

    @NonNull
    @Override
    public AppNavigationKey getNavigationKey() {
        return AppNavigationKey.SCORE;
    }

    @NonNull
    @Override
    public Supplier<? extends Fragment> getFragmentFactory() {
        return ScoreFragment::new;
    }
}
