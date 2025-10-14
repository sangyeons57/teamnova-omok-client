package com.example.teamnovaomok.di;

import androidx.annotation.NonNull;

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationProvider;
import com.example.core.navigation.FragmentNavigationRegistry;
import com.example.core.navigation.FragmentNavigator;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.feature_auth.login.navigation.LoginNavigationProvider;
import com.example.feature_home.home.navigation.HomeNavigationProvider;
import com.example.feature_home.home.navigation.MatchingNavigationProvider;
import com.example.feature_home.home.navigation.ScoreNavigationProvider;
import com.example.feature_game.game.navigation.GameNavigationProvider;
import com.example.feature_game.game.navigation.PostGameNavigationProvider;

/**
 * Composes the navigation infrastructure by wiring providers into a host instance.
 */
public final class FragmentNavigationContainer {

    private final FragmentNavigationHost<AppNavigationKey> host;

    public FragmentNavigationContainer(@NonNull FragmentNavigator navigator) {
        FragmentNavigationRegistry<AppNavigationKey> registry = new FragmentNavigationRegistry<>(AppNavigationKey.class);
        registry.registerProvider(new HomeNavigationProvider());
        registry.registerProvider(new LoginNavigationProvider());
        registry.registerProvider(new MatchingNavigationProvider());
        registry.registerProvider(new ScoreNavigationProvider());
        registry.registerProvider(new GameNavigationProvider());
        registry.registerProvider(new PostGameNavigationProvider());
        host = new FragmentNavigationHost<>(navigator, registry);
    }

    @NonNull
    public FragmentNavigationHost<AppNavigationKey> getHost() {
        return host;
    }
}
