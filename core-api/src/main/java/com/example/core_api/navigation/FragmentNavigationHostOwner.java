package com.example.core_api.navigation;

import androidx.annotation.NonNull;

public interface FragmentNavigationHostOwner<T extends Enum<T>> {
    @NonNull
    FragmentNavigationHost<T> getFragmentNavigatorHost();
}
