package com.example.teamnovaomok.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogRegistry;
import com.example.core.dialog.MainDialogType;
import com.example.feature_auth.login.di.GeneralInfoDialogProvider;
import com.example.feature_auth.login.di.TermsAgreementDialogProvider;
import com.example.feature_home.home.di.DeleteAccountDialogProvider;
import com.example.feature_home.home.di.GameModeDialogProvider;
import com.example.feature_home.home.di.LogoutDialogProvider;
import com.example.feature_home.home.di.RankingDialogProvider;
import com.example.feature_home.home.di.ScoreDialogProvider;
import com.example.feature_home.home.di.SettingDialogProvider;
import com.example.feature_game.game.di.GameInfoDialogProvider;
import com.example.feature_game.game.di.GameResultDialogProvider;
import com.example.feature_home.home.di.SettingProfileDialogProvider;

/**
 * Composes dialog hosts by wiring feature-level providers into core infrastructure.
 */
public final class DialogContainer {

    private final DialogHost<MainDialogType> mainDialogHost;

    public DialogContainer() {
        DialogRegistry<MainDialogType> registry = new DialogRegistry<>(MainDialogType.class);
        registry.registerProvider(new TermsAgreementDialogProvider());
        registry.registerProvider(new GeneralInfoDialogProvider());
        registry.registerProvider(new GameModeDialogProvider());
        registry.registerProvider(new ScoreDialogProvider());
        registry.registerProvider(new RankingDialogProvider());
        registry.registerProvider(new SettingDialogProvider());
        registry.registerProvider(new SettingProfileDialogProvider());
        registry.registerProvider(new GameInfoDialogProvider());
        registry.registerProvider(new GameResultDialogProvider());
        registry.registerProvider(new LogoutDialogProvider());
        registry.registerProvider(new DeleteAccountDialogProvider());

        mainDialogHost = new DialogHost<>(registry);
    }

    @NonNull
    public DialogHost<MainDialogType> getMainDialogHost() {
        return mainDialogHost;
    }
}
