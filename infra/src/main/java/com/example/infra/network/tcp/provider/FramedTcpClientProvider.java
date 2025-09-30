package com.example.infra.network.tcp.provider;

import android.util.Log;

import com.example.core.network.tcp.TcpClient;
import com.example.infra.network.tcp.FramedTcpClient;

import java.io.IOException;
import java.util.Objects;

/**
 * Lazily creates and exposes a singleton {@link FramedTcpClient} instance.
 */
public final class FramedTcpClientProvider {

    private static volatile TcpClient instance;

    private FramedTcpClientProvider() {
    }

    public static TcpClient init(String host,
                                 int port) {
        Objects.requireNonNull(host, "host");
        if (instance != null) {
            return instance;
        }

        if (instance == null) {
            instance = new FramedTcpClient(host, port);
            try {
                instance.connect();
            } catch (IOException e) {
                Log.e("FramedTcpClientProvider", "Failed to connect to " + host + ":" + port, e);
            }
        }
        return instance;
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
