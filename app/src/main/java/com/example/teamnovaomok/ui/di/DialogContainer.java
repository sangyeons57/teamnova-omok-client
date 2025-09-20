package com.example.teamnovaomok.ui.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogRegistry;
import com.example.core.dialog.MainDialogType;
import com.example.feature_auth.login.di.TermsAgreementDialogProvider;

/**
 * Composes dialog hosts by wiring feature-level providers into core infrastructure.
 */
public final class DialogContainer {

    private final DialogHost<MainDialogType> mainDialogHost;

    public DialogContainer() {
        DialogRegistry<MainDialogType> registry = new DialogRegistry<>(MainDialogType.class);
        registry.registerProvider(new TermsAgreementDialogProvider());

        mainDialogHost = new DialogHost<>(registry);
    }

    @NonNull
    public DialogHost<MainDialogType> getMainDialogHost() {
        return mainDialogHost;
    }
}
