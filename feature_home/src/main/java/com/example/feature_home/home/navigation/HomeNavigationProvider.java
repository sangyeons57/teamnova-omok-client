package com.example.feature_home.home.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationProvider;
import com.example.feature_home.home.presentation.ui.HomeFragment;

import java.util.function.Supplier;

public final class HomeNavigationProvider implements FragmentNavigationProvider<AppNavigationKey> {

    @NonNull
    @Override
    public Class<AppNavigationKey> getNavigationKeyType() {
        return AppNavigationKey.class;
    }

    @NonNull
    @Override
    public AppNavigationKey getNavigationKey() {
        return AppNavigationKey.HOME;
    }

    @NonNull
    @Override
    public Supplier<? extends Fragment> getFragmentFactory() {
        return HomeFragment::new;
    }
}
