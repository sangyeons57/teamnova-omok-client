package com.example.infra.network.tcp.provider;

import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.handler.ClientHandlerRegistry;
import com.example.infra.network.tcp.FramedTcpClient;

import java.util.Objects;

/**
 * Lazily creates and exposes a singleton {@link FramedTcpClient} instance.
 */
public final class FramedTcpClientProvider {

    private static volatile TcpClient instance;

    private FramedTcpClientProvider() {
    }

    public static TcpClient init(String host,
                                 int port,
                                 ClientHandlerRegistry registry) {
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(registry, "registry");
        if (instance != null) {
            return instance;
        }
        synchronized (FramedTcpClientProvider.class) {
            if (instance == null) {
                instance = new FramedTcpClient(host, port, registry);
            }
            return instance;
        }
    }

    public static TcpClient getInstance() {
        TcpClient client = instance;
        if (client == null) {
            throw new IllegalStateException("FramedTcpClientProvider is not initialized");
        }
        return client;
    }

    public static void setForTest(TcpClient fake) {
        instance = fake;
    }
}
