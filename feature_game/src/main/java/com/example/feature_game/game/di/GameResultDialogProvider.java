package com.example.feature_game.game.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogProvider;
import com.example.core.dialog.MainDialogType;
import com.example.feature_game.game.presentation.dialog.GameResultDialogController;

/**
 * Registers the game result dialog with the shared dialog registry.
 */
public final class GameResultDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.GAME_RESULT;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new GameResultDialogController();
    }
}
