package com.example.feature_auth.login.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationProvider;
import com.example.feature_auth.login.presentation.ui.LoginFragment;

import java.util.function.Supplier;

public final class LoginNavigationProvider implements FragmentNavigationProvider<AppNavigationKey> {

    @NonNull
    @Override
    public Class<AppNavigationKey> getNavigationKeyType() {
        return AppNavigationKey.class;
    }

    @NonNull
    @Override
    public AppNavigationKey getNavigationKey() {
        return AppNavigationKey.LOGIN;
    }

    @NonNull
    @Override
    public Supplier<? extends Fragment> getFragmentFactory() {
        return LoginFragment::new;
    }
}
