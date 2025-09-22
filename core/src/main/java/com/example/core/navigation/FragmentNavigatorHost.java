package com.example.core.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * Executes navigation requests using the registered fragment factories.
 */
public final class FragmentNavigatorHost<T extends Enum<T>> {

    private final FragmentNavigator navigator;
    private final FragmentNavigationRegistry<T> registry;

    public FragmentNavigatorHost(@NonNull FragmentNavigator navigator,
                                 @NonNull FragmentNavigationRegistry<T> registry) {
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public void navigateTo(@NonNull T key) {
        Objects.requireNonNull(key, "key");
        navigateTo(key, FragmentNavigator.Options.builder().build());
    }

    public void navigateTo(@NonNull T key, @NonNull FragmentNavigator.Options options) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(options, "options");
        Fragment fragment = registry.create(key);
        navigator.navigateTo(fragment, options);
    }

    public boolean popBackStack() {
        return navigator.popBackStack();
    }

    public void clearBackStack() {
        navigator.clearBackStack();
    }
}
