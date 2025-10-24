package com.example.core_api.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Registry that maps enum navigation keys to fragment factories via providers.
 */
public final class FragmentNavigationRegistry<T extends Enum<T>> {

    private final Class<T> keyType;
    private final Map<T, Supplier<? extends Fragment>> factories;

    public FragmentNavigationRegistry(@NonNull Class<T> keyType) {
        this.keyType = Objects.requireNonNull(keyType, "keyType");
        this.factories = new EnumMap<>(keyType);
    }

    @NonNull
    public Class<T> getKeyType() {
        return keyType;
    }

    public void registerProvider(@NonNull FragmentNavigationProvider<T> provider) {
        Objects.requireNonNull(provider, "provider");
        Class<T> providerKeyType = provider.getNavigationKeyType();
        if (!keyType.equals(providerKeyType)) {
            throw new IllegalArgumentException(
                    "Provider key type does not match registry key type. Expected " +
                            keyType.getName() + " but was " + providerKeyType.getName()
            );
        }

        T key = provider.getNavigationKey();
        if (factories.containsKey(key)) {
            throw new IllegalStateException("Navigation key already registered: " + key.name());
        }

        factories.put(key, provider.getFragmentFactory());
    }

    public boolean contains(@NonNull T key) {
        Objects.requireNonNull(key, "key");
        return factories.containsKey(key);
    }

    @NonNull
    public Fragment create(@NonNull T key) {
        Objects.requireNonNull(key, "key");
        Supplier<? extends Fragment> factory = factories.get(key);
        if (factory == null) {
            throw new IllegalStateException("No fragment factory registered for key: " + key.name());
        }
        Fragment fragment = factory.get();
        if (fragment == null) {
            throw new IllegalStateException("Factory returned null for key: " + key.name());
        }
        return fragment;
    }
}
