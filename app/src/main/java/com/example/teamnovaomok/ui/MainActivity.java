package com.example.teamnovaomok.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.MainDialogType;
import com.example.core.navigation.NavigationHelper;
import com.example.core.token.TokenManager;
import com.example.feature_auth.login.di.MainDialogHostOwner;
import com.example.feature_auth.login.presentation.LoginFragment;
import com.example.teamnovaomok.R;
import com.example.teamnovaomok.ui.di.DialogContainer;

public class MainActivity extends AppCompatActivity implements MainDialogHostOwner {

    private NavigationHelper navigationHelper;
    private DialogContainer dialogContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeManager(getApplicationContext());

        dialogContainer = new DialogContainer();
        dialogContainer.getMainDialogHost().attach(this);

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

    public NavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    private void initializeManager(Context context) {
        TokenManager.getInstance(context);
    }
}
