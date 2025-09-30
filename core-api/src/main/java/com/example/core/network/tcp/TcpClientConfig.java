package com.example.core.network.tcp;

import java.util.Objects;

/**
 * Bundles the configuration required to bootstrap a {@link TcpClient} instance.
 */
public record TcpClientConfig(String host,
                              int port) {

    public TcpClientConfig {
        Objects.requireNonNull(host, "host");
    }
}
