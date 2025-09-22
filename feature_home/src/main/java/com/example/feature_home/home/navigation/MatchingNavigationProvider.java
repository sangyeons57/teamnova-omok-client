package com.example.feature_home.home.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationProvider;
import com.example.feature_home.home.presentation.ui.MatchingFragment;

import java.util.function.Supplier;

public final class MatchingNavigationProvider implements FragmentNavigationProvider<AppNavigationKey> {

    @NonNull
    @Override
    public Class<AppNavigationKey> getNavigationKeyType() {
        return AppNavigationKey.class;
    }

    @NonNull
    @Override
    public AppNavigationKey getNavigationKey() {
        return AppNavigationKey.MATCHING;
    }

    @NonNull
    @Override
    public Supplier<? extends Fragment> getFragmentFactory() {
        return MatchingFragment::new;
    }
}
