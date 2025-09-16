package com.example.teamnovaomok.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.core.navigation.NavigationHelper;
import com.example.feature_auth.login.presentation.LoginFragment;
import com.example.teamnovaomok.R;

public class MainActivity extends AppCompatActivity {

    private NavigationHelper navigationHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationHelper = new NavigationHelper(getSupportFragmentManager(), R.id.main_fragment_container, "MainNavigation");

        if (savedInstanceState == null) {
            navigationHelper.navigateTo(new LoginFragment(), NavigationHelper.NavigationOptions.builder()
                    .addToBackStack(false)
                    .tag("Login")
                    .build());
        }
    }

    public NavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    @Override
    public void onBackPressed() {
        if (!navigationHelper.popBackStack()) {
            super.onBackPressed();
        }
    }
}
