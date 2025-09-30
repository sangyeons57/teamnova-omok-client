package com.example.core.network.tcp;

import java.util.Objects;

import com.example.core.network.tcp.handler.ClientHandlerRegistry;

/**
 * Bundles the configuration required to bootstrap a {@link TcpClient} instance.
 */
public record TcpClientConfig(String host,
                              int port,
                              ClientHandlerRegistry handlerRegistry) {

    public TcpClientConfig {
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(handlerRegistry, "handlerRegistry");
    }
}
