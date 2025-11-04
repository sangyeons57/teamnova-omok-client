package com.example.feature_home.di;

import androidx.annotation.NonNull;

import com.example.core_api.dialog.DialogConfig;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogProvider;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_home.presentation.dialog.ExitConfirmationDialogController;

/**
 * Supplies the exit confirmation dialog controller for the home screen.
 */
public final class ExitConfirmationDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.EXIT_CONFIRMATION;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new ExitConfirmationDialogController();
    }

    @NonNull
    @Override
    public DialogConfig getDialogConfig() {
        return DialogConfig.builder()
                .setCancelable(true)
                .setCancelOnTouchOutside(true)
                .build();
    }
}
