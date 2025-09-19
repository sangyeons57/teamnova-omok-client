package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogConfig;
import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogProvider;
import com.example.core.dialog.MainDialogType;
import com.example.feature_auth.login.presentation.TermsAgreementDialogController;

/**
 * Supplies the terms agreement dialog binding for the main host.
 */
public final class LoginDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.TERMS_AGREEMENT;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new TermsAgreementDialogController();
    }

    @NonNull
    @Override
    public DialogConfig getDialogConfig() {
        return DialogConfig.DEFAULT;
    }
}
