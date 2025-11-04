package com.example.feature_home.di;

import androidx.annotation.NonNull;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogProvider;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_home.presentation.dialog.GameModeDialogController;

/**
 * Registers the game mode dialog with the dialog registry.
 */
public final class GameModeDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.GAME_MODE;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new GameModeDialogController();
    }
}
