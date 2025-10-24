package com.example.core_api.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * Executes navigation requests using the registered fragment factories.
 */
public final class FragmentNavigationHost<T extends Enum<T>> {

    private final FragmentNavigator navigator;
    private final FragmentNavigationRegistry<T> registry;

    public FragmentNavigationHost(@NonNull FragmentNavigator navigator,
                                  @NonNull FragmentNavigationRegistry<T> registry) {
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public void navigateTo(@NonNull T key) {
        Objects.requireNonNull(key, "key");
        navigateTo(key, true);
    }

    public void navigateTo(@NonNull T key, boolean addToBackStack) {
        Objects.requireNonNull(key, "key");
        Fragment fragment = registry.create(key);
        navigator.navigateTo(fragment, addToBackStack);
    }

    public boolean popBackStack() {
        return navigator.popBackStack();
    }

    public void clearBackStack() {
        navigator.clearBackStack();
    }
}
