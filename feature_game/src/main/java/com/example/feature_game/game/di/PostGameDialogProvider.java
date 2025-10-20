package com.example.feature_game.game.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogProvider;
import com.example.core.dialog.MainDialogType;
import com.example.feature_game.game.presentation.dialog.PostGameDialogController;

/**
 * Registers the post-game dialog with the shared dialog registry.
 */
public final class PostGameDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.POST_GAME;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new PostGameDialogController();
    }
}
