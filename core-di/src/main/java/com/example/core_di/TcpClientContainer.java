package com.example.core_di;

import com.example.application.session.GameInfoStore;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.TcpClientConfig;
import com.example.core.network.tcp.dispatcher.ClientDispatcher;
import com.example.core.network.tcp.protocol.FrameType;
import com.example.core_di.tcp.JoinInGameSessionHandler;
import com.example.infra.tcp.provider.FramedTcpClientProvider;

/**
 * Provides access to the singleton TCP client instance.
 */
public final class TcpClientContainer {

    private static volatile TcpClientContainer instance;

    public static void init(TcpClientConfig config, GameInfoStore gameInfoStore) {
        if (instance != null) {
            return;
        }
        synchronized (TcpClientContainer.class) {
            if (instance == null) {
                instance = new TcpClientContainer(config, gameInfoStore);
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

    private TcpClientContainer(TcpClientConfig config, GameInfoStore gameInfoStore) {
        this.tcpClient = FramedTcpClientProvider.init(
                config.host(),
                config.port()
        );
        registerFrameHandlers(gameInfoStore);
    }

    private void registerFrameHandlers(GameInfoStore gameInfoStore) {
        ClientDispatcher dispatcher = tcpClient.dispatcher();
        dispatcher.register(FrameType.JOIN_IN_GAME_SESSION,
                () -> new JoinInGameSessionHandler(gameInfoStore));
    }

    public TcpClient getClient() {
        return tcpClient;
    }

    public ClientDispatcher getDispatcher() {
        return tcpClient.dispatcher();
    }
}
