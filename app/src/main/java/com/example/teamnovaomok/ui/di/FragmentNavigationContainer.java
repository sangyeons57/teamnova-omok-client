package com.example.teamnovaomok.ui.di;

import androidx.annotation.NonNull;

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationProvider;
import com.example.core.navigation.FragmentNavigationRegistry;
import com.example.core.navigation.FragmentNavigator;
import com.example.core.navigation.FragmentNavigatorHost;
import com.example.feature_auth.login.navigation.LoginNavigationProvider;
import com.example.feature_home.home.navigation.HomeNavigationProvider;
import com.example.feature_home.home.navigation.MatchingNavigationProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Composes the navigation infrastructure by wiring providers into a host instance.
 */
public final class FragmentNavigationContainer {

    private final FragmentNavigatorHost<AppNavigationKey> host;
    private final FragmentNavigationRegistry<AppNavigationKey> registry;

    private FragmentNavigationContainer(@NonNull FragmentNavigator navigator) {
        registry = new FragmentNavigationRegistry<>(AppNavigationKey.class);
        registry.registerProvider(new FragmentNavigationProvider<AppNavigationKey>() {
        });
        for (FragmentNavigationProvider<AppNavigationKey> provider : providers) {
        }
        host = new FragmentNavigatorHost<>(navigator, registry);
    }

    @NonNull
    public FragmentNavigatorHost<AppNavigationKey> getHost() {
        return host;
    }
}
