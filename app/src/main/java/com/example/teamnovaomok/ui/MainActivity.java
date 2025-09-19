package com.example.teamnovaomok.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.core.dialog.DialogHost;
import com.example.feature_auth.login.di.AuthDialogHostOwner;
import com.example.feature_auth.login.presentation.AuthDialogType;
import com.example.feature_auth.login.presentation.LoginFragment;
import com.example.teamnovaomok.R;
import com.example.teamnovaomok.ui.di.DialogAssembler;
import com.example.core.navigation.NavigationHelper;
import com.example.core.token.TokenManager;

public class MainActivity extends AppCompatActivity implements AuthDialogHostOwner {

    private NavigationHelper navigationHelper;
    private DialogAssembler dialogAssembler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeManager(getApplicationContext());

        dialogAssembler = new DialogAssembler();
        dialogAssembler.getAuthDialogHost().attach(this);

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
    public DialogHost<AuthDialogType> getDialogHost() {
        return dialogAssembler.getAuthDialogHost();
    }

    @Override
    protected void onDestroy() {
        if (dialogAssembler != null) {
            DialogHost<AuthDialogType> host = dialogAssembler.getAuthDialogHost();
            if (host.isAttached()) {
                host.detach(this);
            }
        }
        super.onDestroy();
    }

    public NavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    private void initializeManager(Context context) {
        TokenManager.getInstance(context);
    }
}
