package com.example.teamnovaomok;

import android.app.Application;

import com.example.core.network.tcp.TcpClientConfig;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.PostGameSessionContainer;
import com.example.core_di.SoundManagerContainer;
import com.example.core_di.TcpClientContainer;
import com.example.core_di.TokenContainer;
import com.example.core_di.UserSessionContainer;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenContainer.init(this);
        GameInfoContainer.init();
        PostGameSessionContainer.init();
        UserSessionContainer.init();
        SoundManagerContainer.init(this);
        TcpClientContainer.init(
                new TcpClientConfig(BuildConfig.TCP_HOST, BuildConfig.TCP_PORT),
                GameInfoContainer.getInstance().getStore(),
                PostGameSessionContainer.getInstance().getStore()
        );
    }
}
