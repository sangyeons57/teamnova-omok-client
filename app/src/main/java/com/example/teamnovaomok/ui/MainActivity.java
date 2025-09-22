package com.example.teamnovaomok.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.application.port.in.UseCaseRegistry;
import com.example.application.port.in.UseCaseRegistryProvider;
import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core.navigation.FragmentNavigator;
import com.example.core.navigation.FragmentNavigatorHost;
import com.example.core.token.TokenManager;
import com.example.core.token.TokenManagerProvider;
import com.example.feature_auth.login.di.TermsAgreementHandler;
import com.example.teamnovaomok.R;
import com.example.teamnovaomok.ui.di.DialogContainer;
import com.example.teamnovaomok.ui.di.FragmentNavigationContainer;
import com.example.teamnovaomok.ui.di.UseCaseContainer;

public class MainActivity extends AppCompatActivity implements
        DialogHostOwner<MainDialogType>,
        FragmentNavigationHostOwner<AppNavigationKey>,
        UseCaseRegistryProvider,
        TokenManagerProvider,
        TermsAgreementHandler {

    private FragmentNavigator fragmentNavigator;
    private DialogContainer dialogContainer;
    private UseCaseContainer useCaseContainer;
    private FragmentNavigationContainer fragmentNavigationContainer;
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

        fragmentNavigator = new FragmentNavigator(getSupportFragmentManager(), R.id.main_fragment_container, "MainNavigation");
        fragmentNavigationContainer = new FragmentNavigationContainer();(fragmentNavigator);

        if (savedInstanceState == null) {
            getFragmentNavigatorHost().navigateTo(AppNavigationKey.LOGIN, FragmentNavigator.Options.builder()
                    .addToBackStack(false)
                    .tag(AppNavigationKey.LOGIN.name())
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

    @NonNull
    @Override
    public FragmentNavigatorHost<AppNavigationKey> getFragmentNavigatorHost() {
        return fragmentNavigationContainer.getHost();
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

    @Override
    public void onAllTermsAccepted() {
        FragmentNavigatorHost<AppNavigationKey> host = getFragmentNavigatorHost();
        host.clearBackStack();
        host.navigateTo(AppNavigationKey.HOME, FragmentNavigator.Options.builder()
                .addToBackStack(false)
                .tag(AppNavigationKey.HOME.name())
                .build());
    }

    private void initializeManager(Context context) {
        tokenManager = TokenManager.getInstance(context);
    }
}
