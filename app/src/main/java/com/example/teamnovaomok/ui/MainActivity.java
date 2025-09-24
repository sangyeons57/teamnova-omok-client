package com.example.teamnovaomok.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;
import com.example.core.event.AppEvent;
import com.example.core.event.SessionInvalidatedEvent;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core.navigation.FragmentNavigator;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.core_di.TokenContainer;
import com.example.core_di.EventBusContainer;
import com.example.teamnovaomok.R;
import com.example.teamnovaomok.ui.di.DialogContainer;
import com.example.teamnovaomok.ui.di.FragmentNavigationContainer;

public class MainActivity extends AppCompatActivity implements
        DialogHostOwner<MainDialogType>,
        FragmentNavigationHostOwner<AppNavigationKey> {

    private DialogContainer dialogContainer;
    private FragmentNavigationContainer fragmentNavigationContainer;
    private long sessionInvalidatedListenerId = -1L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dialogContainer = new DialogContainer();
        dialogContainer.getMainDialogHost().attach(this);
        TokenContainer.init(getApplication());
        sessionInvalidatedListenerId = EventBusContainer.getInstance().register(this::onAppEvent);

        fragmentNavigationContainer = new FragmentNavigationContainer(
                new FragmentNavigator(getSupportFragmentManager(), R.id.main_fragment_container)
        );

        // 로그인 정보가 있으면 (AccessToken 로그인 시도) 성공시 홈 화면으로 이동 실패시 LOGIN화면으로 이동
        if (savedInstanceState == null) {
            getFragmentNavigatorHost().navigateTo(AppNavigationKey.LOGIN, false);
        }
    }

    @NonNull
    @Override
    public DialogHost<MainDialogType> getDialogHost() {
        return dialogContainer.getMainDialogHost();
    }

    @NonNull
    @Override
    public FragmentNavigationHost<AppNavigationKey> getFragmentNavigatorHost() {
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
        if (sessionInvalidatedListenerId != -1L) {
            EventBusContainer.getInstance().unregister(sessionInvalidatedListenerId);
            sessionInvalidatedListenerId = -1L;
        }
    }

    private void onAppEvent(AppEvent event) {
        if (!(event instanceof SessionInvalidatedEvent)) {
            return;
        }
        runOnUiThread(() -> {
            FragmentNavigationHost<AppNavigationKey> host = getFragmentNavigatorHost();
            host.clearBackStack();
            host.navigateTo(AppNavigationKey.LOGIN, false);
        });
    }
}
