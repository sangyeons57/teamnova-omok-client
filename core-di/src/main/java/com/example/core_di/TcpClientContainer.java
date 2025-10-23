package com.example.core_di;

import com.example.application.session.GameInfoStore;
import com.example.application.session.postgame.PostGameSessionStore;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.TcpClientConfig;
import com.example.core.network.tcp.dispatcher.ClientDispatcher;
import com.example.core.network.tcp.protocol.FrameType;
import com.example.core.sound.SoundManager;
import com.example.core_di.tcp.BoardUpdatedHandler;
import com.example.core_di.tcp.GamePostDecisionPromptHandler;
import com.example.core_di.tcp.GamePostDecisionUpdateHandler;
import com.example.core_di.tcp.GameSessionStartedHandler;
import com.example.core_di.tcp.GameSessionCompletedHandler;
import com.example.core_di.tcp.GameSessionRematchStartedHandler;
import com.example.core_di.tcp.GameSessionPlayerDisconnectedHandler;
import com.example.core_di.tcp.GameSessionTerminatedHandler;
import com.example.core_di.tcp.JoinInGameSessionHandler;
import com.example.core_di.tcp.ReadyInGameSessionHandler;
import com.example.core_di.tcp.TurnEndedHandler;
import com.example.core_di.tcp.TurnStartedHandler;
import com.example.infra.tcp.provider.FramedTcpClientProvider;

/**
 * Provides access to the singleton TCP client instance.
 */
public final class TcpClientContainer {

    private static volatile TcpClientContainer instance;

    public static void init(TcpClientConfig config,
                            GameInfoStore gameInfoStore,
                            PostGameSessionStore postGameSessionStore) {
        if (instance != null) {
            return;
        }
        synchronized (TcpClientContainer.class) {
            if (instance == null) {
                instance = new TcpClientContainer(config, gameInfoStore, postGameSessionStore);
            }
        }
    }

    public static TcpClientContainer getInstance() {
        TcpClientContainer container = instance;
        if (container == null) {
            throw new IllegalStateException("TcpClientContainer is not initialized");
        }
        return container;
    }

    private final TcpClient tcpClient;

    private TcpClientContainer(TcpClientConfig config,
                               GameInfoStore gameInfoStore,
                               PostGameSessionStore postGameSessionStore) {
        this.tcpClient = FramedTcpClientProvider.init(
                config.host(),
                config.port()
        );
        registerFrameHandlers(gameInfoStore, postGameSessionStore);
    }

    private void registerFrameHandlers(GameInfoStore gameInfoStore,
                                       PostGameSessionStore postGameSessionStore) {
        SoundManager soundManager = SoundManagerContainer.getInstance().getSoundManager();
        ClientDispatcher dispatcher = tcpClient.dispatcher();
        dispatcher.register(FrameType.JOIN_IN_GAME_SESSION,
                () -> new JoinInGameSessionHandler(gameInfoStore));
        dispatcher.register(FrameType.READY_IN_GAME_SESSION,
                () -> new ReadyInGameSessionHandler(gameInfoStore));
        dispatcher.register(FrameType.GAME_SESSION_STARTED,
                () -> new GameSessionStartedHandler(gameInfoStore));
        dispatcher.register(FrameType.TURN_STARTED,
                () -> new TurnStartedHandler(gameInfoStore));
        dispatcher.register(FrameType.TURN_ENDED,
                () -> new TurnEndedHandler(gameInfoStore));
        dispatcher.register(FrameType.BOARD_UPDATED,
                () -> new BoardUpdatedHandler(gameInfoStore, soundManager));
        dispatcher.register(FrameType.GAME_SESSION_COMPLETED,
                () -> new GameSessionCompletedHandler(postGameSessionStore));
        dispatcher.register(FrameType.GAME_POST_DECISION_PROMPT,
                () -> new GamePostDecisionPromptHandler(postGameSessionStore));
        dispatcher.register(FrameType.GAME_POST_DECISION_UPDATE,
                () -> new GamePostDecisionUpdateHandler(postGameSessionStore));
        dispatcher.register(FrameType.GAME_SESSION_REMATCH_STARTED,
                () -> new GameSessionRematchStartedHandler(postGameSessionStore, gameInfoStore));
        dispatcher.register(FrameType.GAME_SESSION_TERMINATED,
                () -> new GameSessionTerminatedHandler(postGameSessionStore, gameInfoStore));
        dispatcher.register(FrameType.GAME_SESSION_PLAYER_DISCONNECTED,
                () -> new GameSessionPlayerDisconnectedHandler(postGameSessionStore));
    }

    public TcpClient getClient() {
        return tcpClient;
    }

    public ClientDispatcher getDispatcher() {
        return tcpClient.dispatcher();
    }
}
