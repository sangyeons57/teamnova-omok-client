package com.example.core_di;

import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.TcpClientConfig;
import com.example.infra.network.tcp.provider.FramedTcpClientProvider;

/**
 * Provides access to the singleton TCP client instance.
 */
public final class TcpClientContainer {
    private static volatile TcpClientContainer instance;

    public static void init(TcpClientConfig config) {
        if (instance != null) {
            return;
        }
        synchronized (TcpClientContainer.class) {
            if (instance == null) {
                instance = new TcpClientContainer(config);
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

    private TcpClientContainer(TcpClientConfig config) {
        this.tcpClient = FramedTcpClientProvider.init(
                config.host(),
                config.port(),
                config.handlerRegistry()
        );
    }

    public TcpClient getClient() {
        return tcpClient;
    }
}
