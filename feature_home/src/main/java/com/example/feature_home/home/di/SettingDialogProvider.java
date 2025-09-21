package com.example.feature_home.home.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogProvider;
import com.example.core.dialog.MainDialogType;
import com.example.feature_home.home.presentation.dialog.SettingDialogController;

/**
 * Provides the settings dialog binding.
 */
public final class SettingDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.SETTING;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new SettingDialogController();
    }
}
