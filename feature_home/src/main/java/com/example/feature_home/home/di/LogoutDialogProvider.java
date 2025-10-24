package com.example.feature_home.home.di;

import androidx.annotation.NonNull;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogProvider;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_home.home.presentation.dialog.LogoutDialogController;

/**
 * Provides the logout confirmation dialog controller binding.
 */
public final class LogoutDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.LOGOUT_CONFIRMATION;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new LogoutDialogController();
    }
}

