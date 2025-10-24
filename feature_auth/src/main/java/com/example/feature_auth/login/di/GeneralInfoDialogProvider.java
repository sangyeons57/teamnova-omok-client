package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogProvider;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_auth.login.presentation.dialog.GeneralInfoDialogController;

/**
 * Supplies the general information dialog for the shared dialog host.
 */
public final class GeneralInfoDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.GENERAL_INFO;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new GeneralInfoDialogController();
    }
}
