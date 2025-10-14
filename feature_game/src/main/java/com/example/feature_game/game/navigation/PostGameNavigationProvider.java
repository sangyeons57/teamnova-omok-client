package com.example.feature_game.game.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationProvider;
import com.example.feature_game.game.presentation.ui.PostGameFragment;

import java.util.function.Supplier;

/**
 * Supplies the post-game fragment to the shared navigation registry.
 */
public final class PostGameNavigationProvider implements FragmentNavigationProvider<AppNavigationKey> {

    @NonNull
    @Override
    public Class<AppNavigationKey> getNavigationKeyType() {
        return AppNavigationKey.class;
    }

    @NonNull
    @Override
    public AppNavigationKey getNavigationKey() {
        return AppNavigationKey.POST_GAME;
    }

    @NonNull
    @Override
    public Supplier<? extends Fragment> getFragmentFactory() {
        return PostGameFragment::new;
    }
}
