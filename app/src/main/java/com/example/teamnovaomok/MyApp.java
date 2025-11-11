package com.example.teamnovaomok;

import android.app.Application;

import com.example.application.usecase.TcpAuthUseCase;
import com.example.core_api.network.tcp.TcpClientConfig;
import com.example.core_di.EventBusContainer;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.PostGameSessionContainer;
import com.example.core_di.RoomClientContainer;
import com.example.core_di.SoundManagerContainer;
import com.example.core_di.TcpClientContainer;
import com.example.core_di.TokenContainer;
import com.example.core_di.UserSessionContainer;
import com.example.core_di.UseCaseContainer;
import com.example.core_di.realtime.RealtimeReconnectObserver;

public class MyApp extends Application {

    private RealtimeReconnectObserver realtimeReconnectObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        RoomClientContainer.init(this);

        TokenContainer.init(this);
        GameInfoContainer.init();
        PostGameSessionContainer.init();
        SoundManagerContainer.init(this);
        TcpClientContainer.init(
                new TcpClientConfig(BuildConfig.TCP_HOST, BuildConfig.TCP_PORT),
                GameInfoContainer.getInstance().getStore(),
                PostGameSessionContainer.getInstance().getStore()
        );

        UserSessionContainer.init();
        initializeRealtimeReconnectObserver();
    }

    private void initializeRealtimeReconnectObserver() {
        UseCaseContainer useCaseContainer = UseCaseContainer.getInstance();
        realtimeReconnectObserver = new RealtimeReconnectObserver(
                EventBusContainer.getInstance(),
                TokenContainer.getInstance(),
                useCaseContainer.get(TcpAuthUseCase.class)
        );
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (realtimeReconnectObserver != null) {
            realtimeReconnectObserver.dispose();
            realtimeReconnectObserver = null;
        }
    }

}
