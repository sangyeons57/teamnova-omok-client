package com.example.feature_home.home.di;

import androidx.annotation.NonNull;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogProvider;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_home.home.presentation.dialog.RankingDialogController;

/**
 * Provides the ranking dialog to the shared registry.
 */
public final class RankingDialogProvider implements DialogProvider<MainDialogType> {

    @NonNull
    @Override
    public Class<MainDialogType> getDialogKeyType() {
        return MainDialogType.class;
    }

    @NonNull
    @Override
    public MainDialogType getDialogKey() {
        return MainDialogType.RANKING;
    }

    @NonNull
    @Override
    public DialogController<MainDialogType> createController() {
        return new RankingDialogController();
    }
}
