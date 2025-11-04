package com.example.feature_home.di;

import androidx.annotation.NonNull;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogProvider;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_home.presentation.dialog.DeleteAccountDialogController;

/**
 * Provides the account deletion confirmation dialog binding.
 */
public final class DeleteAccountDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.ACCOUNT_DELETION_CONFIRMATION;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new DeleteAccountDialogController();
    }
}

