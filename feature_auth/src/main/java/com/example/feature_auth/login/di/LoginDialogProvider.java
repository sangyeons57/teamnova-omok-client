package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogProvider;
import com.example.core.dialog.DialogRegistry;
import com.example.feature_auth.login.presentation.AuthDialogType;
import com.example.feature_auth.login.presentation.TermsAgreementDialogController;

/**
 * Supplies dialog registrations for the auth feature.
 */
public final class LoginDialogProvider implements DialogProvider<AuthDialogType> {

    @NonNull
    @Override
    public Class<AuthDialogType> getDialogKeyType() {
        return AuthDialogType.class;
    }

    @Override
    public void register(@NonNull DialogRegistry<AuthDialogType> registry) {
        registry.register(AuthDialogType.TERMS_AGREEMENT, new TermsAgreementDialogController());
    }
}
