package com.example.teamnovaomok.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.application.port.in.UseCaseRegistry;
import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;
import com.example.core.navigation.NavigationHelper;
import com.example.core.token.TokenManager;
import com.example.feature_auth.login.di.LoginDependencyProvider;
import com.example.feature_home.home.di.HomeDependencyProvider;
import com.example.feature_auth.login.presentation.ui.LoginFragment;
import com.example.teamnovaomok.R;
import com.example.teamnovaomok.ui.di.DialogContainer;
import com.example.teamnovaomok.ui.di.UseCaseContainer;

public class MainActivity extends AppCompatActivity implements DialogHostOwner<MainDialogType>, LoginDependencyProvider, HomeDependencyProvider {

    private NavigationHelper navigationHelper;
    private DialogContainer dialogContainer;
    private UseCaseContainer useCaseContainer;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeManager(getApplicationContext());

        dialogContainer = new DialogContainer();
        dialogContainer.getMainDialogHost().attach(this);

        useCaseContainer = new UseCaseContainer();

        navigationHelper = new NavigationHelper(getSupportFragmentManager(), R.id.main_fragment_container, "MainNavigation");

        if (savedInstanceState == null) {
            navigationHelper.navigateTo(new LoginFragment(), NavigationHelper.NavigationOptions.builder()
                    .addToBackStack(false)
                    .tag("Login")
                    .build());
        }
    }

    @NonNull
    @Override
    public DialogHost<MainDialogType> getDialogHost() {
        return dialogContainer.getMainDialogHost();
    }

    @NonNull
    @Override
    public UseCaseRegistry getUseCaseRegistry() {
        return useCaseContainer.registry;
    }

    @NonNull
    @Override
    public TokenManager getTokenManager() {
        return tokenManager;
    }

    @Override
    protected void onDestroy() {
        if (dialogContainer != null) {
            DialogHost<MainDialogType> host = dialogContainer.getMainDialogHost();
            if (host.isAttached()) {
                host.detach(this);
            }
        }
        super.onDestroy();
    }

    @NonNull
    @Override
    public NavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    private void initializeManager(Context context) {
        tokenManager = TokenManager.getInstance(context);
    }
}


