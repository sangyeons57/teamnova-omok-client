package com.example.teamnovaomok;

import android.app.Application;

import com.example.core.network.tcp.TcpClientConfig;
import com.example.core_di.GameInfoContainer;
import com.example.core_di.PostGameSessionContainer;
import com.example.core_di.SoundManagerContainer;
import com.example.core_di.TcpClientContainer;
import com.example.core_di.TokenContainer;
import com.example.core_di.UserSessionContainer;
import com.example.core.sound.SoundIds;
import com.example.core.sound.SoundManager;
import com.example.core_di.R;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenContainer.init(this);
        GameInfoContainer.init();
        PostGameSessionContainer.init();
        UserSessionContainer.init();
        SoundManagerContainer.init(this);
        registerSoundEffects();
        TcpClientContainer.init(
                new TcpClientConfig(BuildConfig.TCP_HOST, BuildConfig.TCP_PORT),
                GameInfoContainer.getInstance().getStore(),
                PostGameSessionContainer.getInstance().getStore()
        );
    }

    private void registerSoundEffects() {
        SoundManager soundManager = SoundManagerContainer.getInstance().getSoundManager();
        if (!soundManager.isRegistered(SoundIds.UI_BUTTON_CLICK)) {
            soundManager.register(new SoundManager.SoundRegistration(
                    SoundIds.UI_BUTTON_CLICK,
                    R.raw.button_click_sound_effect,
                    1f,
                    false
            ));
        }
    }
}
