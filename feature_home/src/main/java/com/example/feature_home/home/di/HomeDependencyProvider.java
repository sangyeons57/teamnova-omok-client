package com.example.feature_home.home.di;

import androidx.annotation.NonNull;

import com.example.core.navigation.NavigationHelper;

/**
 * Supplies dependencies required by Home feature fragments.
 */
public interface HomeDependencyProvider {

    @NonNull
    NavigationHelper getNavigationHelper();
}
