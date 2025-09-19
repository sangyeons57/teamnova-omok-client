package com.example.teamnovaomok.ui.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogRegistry;
import com.example.feature_auth.login.di.LoginDialogProvider;
import com.example.feature_auth.login.presentation.AuthDialogType;

/**
 * Composes dialog hosts by wiring feature-level providers into core infrastructure.
 */
public final class DialogAssembler {

    private final DialogHost<AuthDialogType> authDialogHost;

    public DialogAssembler() {
        DialogRegistry<AuthDialogType> authRegistry = new DialogRegistry<>(AuthDialogType.class);
        authRegistry.registerProvider( new LoginDialogProvider() );
        authDialogHost = new DialogHost<>(authRegistry);
    }

    @NonNull
    public DialogHost<AuthDialogType> getAuthDialogHost() {
        return authDialogHost;
    }
}
