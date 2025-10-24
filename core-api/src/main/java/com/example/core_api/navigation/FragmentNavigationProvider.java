package com.example.core_api.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.function.Supplier;

/**
 * Supplies a single navigation destination binding for an enum-backed key.
 */
public interface FragmentNavigationProvider<T extends Enum<T>> {

    @NonNull
    Class<T> getNavigationKeyType();

    @NonNull
    T getNavigationKey();

    @NonNull
    Supplier<? extends Fragment> getFragmentFactory();
}
