package com.example.feature_home.home.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogProvider;
import com.example.core.dialog.MainDialogType;
import com.example.feature_home.home.presentation.dialog.SettingProfileDialogController;

/**
 * Provides the profile configuration dialog binding.
 */
public final class SettingProfileDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.SETTING_PROFILE;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new SettingProfileDialogController();
    }
}
