package com.example.teamnovaomok;

import android.app.Application;

import com.example.core_api.network.tcp.TcpClientConfig;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.PostGameSessionContainer;
import com.example.core_di.SoundManagerContainer;
import com.example.core_di.TcpClientContainer;
import com.example.core_di.TokenContainer;
import com.example.core_di.UserSessionContainer;
import com.example.core_di.RulesDataSourceContainer;
import com.example.data.datasource.room.RulesDatabaseConfigFactory;
import com.example.infra.room.RoomAssetDatabaseProvider;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RoomAssetDatabaseProvider.configure(RulesDatabaseConfigFactory.provideConfig());
        RoomAssetDatabaseProvider.initialize(this);

        TokenContainer.init(this);
        GameInfoContainer.init();
        PostGameSessionContainer.init();
        SoundManagerContainer.init(this);
        RulesDataSourceContainer.init();
        TcpClientContainer.init(
                new TcpClientConfig(BuildConfig.TCP_HOST, BuildConfig.TCP_PORT),
                GameInfoContainer.getInstance().getStore(),
                PostGameSessionContainer.getInstance().getStore()
        );

        UserSessionContainer.init();
    }

}
